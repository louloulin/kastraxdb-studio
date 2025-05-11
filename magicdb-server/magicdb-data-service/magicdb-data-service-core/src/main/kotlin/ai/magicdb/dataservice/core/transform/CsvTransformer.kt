package ai.magicdb.dataservice.core.transform

import ai.magicdb.dataservice.api.model.TransformationRequest
import ai.magicdb.dataservice.api.model.TransformationResult
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import org.yaml.snakeyaml.Yaml
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * CSV转换器
 *
 * @author magicdb
 */
class CsvTransformer(
    private val objectMapper: ObjectMapper
) : FormatTransformer {
    private val logger = LoggerFactory.getLogger(CsvTransformer::class.java)
    
    override fun transform(request: TransformationRequest): TransformationResult {
        try {
            // 解析源数据为CSV
            val csvRecords = parseSourceData(request.sourceData)
            
            // 根据目标格式转换
            return when (request.targetFormat.lowercase()) {
                "csv" -> transformToCsv(csvRecords, request)
                "json" -> transformToJson(csvRecords, request)
                "xml" -> transformToXml(csvRecords, request)
                "yaml" -> transformToYaml(csvRecords, request)
                else -> TransformationResult.failure("不支持的目标格式: ${request.targetFormat}")
            }
        } catch (e: Exception) {
            logger.error("CSV转换失败: {}", e.message, e)
            return TransformationResult.failure("CSV转换失败: ${e.message}")
        }
    }
    
    override fun getSupportedSourceFormats(): List<String> {
        return listOf("csv")
    }
    
    override fun getSupportedTargetFormats(): List<String> {
        return listOf("csv", "json", "xml", "yaml")
    }
    
    override fun validateRules(rules: Map<String, Any?>): List<String> {
        val errors = mutableListOf<String>()
        
        // 验证字段映射
        val fieldMappings = rules["fieldMappings"] as? Map<*, *>
        if (fieldMappings != null) {
            for ((source, target) in fieldMappings) {
                if (source == null || source.toString().isBlank()) {
                    errors.add("源字段不能为空")
                }
                if (target == null || target.toString().isBlank()) {
                    errors.add("目标字段不能为空")
                }
            }
        }
        
        // 验证值转换
        val valueConverters = rules["valueConverters"] as? Map<*, *>
        if (valueConverters != null) {
            for ((field, converter) in valueConverters) {
                if (field == null || field.toString().isBlank()) {
                    errors.add("转换字段不能为空")
                }
                if (converter == null) {
                    errors.add("值转换器不能为空")
                }
            }
        }
        
        return errors
    }
    
    override fun getRuleTemplate(): Map<String, Any?> {
        return mapOf(
            "fieldMappings" to mapOf(
                "sourceColumn1" to "targetColumn1",
                "sourceColumn2" to "targetColumn2"
            ),
            "valueConverters" to mapOf(
                "column1" to mapOf(
                    "type" to "replace",
                    "search" to "oldValue",
                    "replace" to "newValue"
                ),
                "column2" to mapOf(
                    "type" to "uppercase"
                )
            ),
            "includeColumns" to listOf("column1", "column2"),
            "excludeColumns" to listOf("column3", "column4"),
            "delimiter" to ",", // 用于CSV
            "quoteChar" to "\"", // 用于CSV
            "header" to true, // 用于CSV
            "rootElement" to "root", // 用于XML
            "recordElement" to "record", // 用于XML
            "pretty" to true // 格式化输出
        )
    }
    
    /**
     * 解析源数据
     */
    private fun parseSourceData(sourceData: Any): List<CSVRecord> {
        val csvString = when (sourceData) {
            is String -> sourceData
            else -> throw IllegalArgumentException("不支持的CSV源数据类型: ${sourceData.javaClass.name}")
        }
        
        // 解析CSV
        val parser = CSVParser.parse(
            csvString,
            CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim()
        )
        
        return parser.records
    }
    
    /**
     * 转换为CSV
     */
    private fun transformToCsv(records: List<CSVRecord>, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedRecords = applyRules(records, request.rules)
            
            // 获取CSV选项
            val delimiter = request.options["delimiter"] as? String ?: ","
            val quoteChar = request.options["quoteChar"] as? String ?: "\""
            val header = request.options["header"] as? Boolean ?: true
            
            // 获取表头
            val headers = if (transformedRecords.isNotEmpty()) {
                transformedRecords[0].map.keys.toList()
            } else {
                emptyList()
            }
            
            // 创建CSV格式
            val csvFormat = CSVFormat.DEFAULT
                .withDelimiter(delimiter[0])
                .withQuote(quoteChar[0])
                .withHeader(*headers.toTypedArray())
                .withSkipHeaderRecord(!header)
            
            // 创建CSV打印器
            val writer = StringWriter()
            val csvPrinter = CSVPrinter(writer, csvFormat)
            
            // 写入数据
            for (record in transformedRecords) {
                csvPrinter.printRecord(record.toList())
            }
            
            // 关闭打印器
            csvPrinter.close()
            
            return TransformationResult.success(
                targetData = writer.toString(),
                targetFormat = "csv"
            )
        } catch (e: Exception) {
            logger.error("转换为CSV失败: {}", e.message, e)
            return TransformationResult.failure("转换为CSV失败: ${e.message}")
        }
    }
    
    /**
     * 转换为JSON
     */
    private fun transformToJson(records: List<CSVRecord>, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedRecords = applyRules(records, request.rules)
            
            // 创建JSON数组
            val arrayNode = objectMapper.createArrayNode()
            
            // 转换记录为JSON对象
            for (record in transformedRecords) {
                val objectNode = objectMapper.createObjectNode()
                for ((key, value) in record.toMap()) {
                    objectNode.put(key, value)
                }
                arrayNode.add(objectNode)
            }
            
            // 转换为JSON字符串
            val pretty = request.options["pretty"] as? Boolean ?: false
            val jsonString = if (pretty) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode)
            } else {
                objectMapper.writeValueAsString(arrayNode)
            }
            
            return TransformationResult.success(
                targetData = jsonString,
                targetFormat = "json"
            )
        } catch (e: Exception) {
            logger.error("转换为JSON失败: {}", e.message, e)
            return TransformationResult.failure("转换为JSON失败: ${e.message}")
        }
    }
    
    /**
     * 转换为XML
     */
    private fun transformToXml(records: List<CSVRecord>, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedRecords = applyRules(records, request.rules)
            
            // 创建XML文档
            val docFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = docFactory.newDocumentBuilder()
            val doc = docBuilder.newDocument()
            
            // 设置根元素
            val rootElementName = request.rules["rootElement"] as? String ?: "root"
            val rootElement = doc.createElement(rootElementName)
            doc.appendChild(rootElement)
            
            // 设置记录元素名称
            val recordElementName = request.rules["recordElement"] as? String ?: "record"
            
            // 转换记录为XML元素
            for (record in transformedRecords) {
                val recordElement = doc.createElement(recordElementName)
                rootElement.appendChild(recordElement)
                
                for ((key, value) in record.toMap()) {
                    val fieldElement = doc.createElement(key)
                    fieldElement.textContent = value
                    recordElement.appendChild(fieldElement)
                }
            }
            
            // 转换为XML字符串
            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()
            
            // 设置输出属性
            val pretty = request.options["pretty"] as? Boolean ?: false
            if (pretty) {
                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            }
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
            
            val source = DOMSource(doc)
            val writer = StringWriter()
            val result = StreamResult(writer)
            transformer.transform(source, result)
            
            return TransformationResult.success(
                targetData = writer.toString(),
                targetFormat = "xml"
            )
        } catch (e: Exception) {
            logger.error("转换为XML失败: {}", e.message, e)
            return TransformationResult.failure("转换为XML失败: ${e.message}")
        }
    }
    
    /**
     * 转换为YAML
     */
    private fun transformToYaml(records: List<CSVRecord>, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedRecords = applyRules(records, request.rules)
            
            // 创建Java对象列表
            val list = transformedRecords.map { record ->
                record.toMap()
            }
            
            // 转换为YAML
            val yaml = Yaml()
            val yamlString = yaml.dump(list)
            
            return TransformationResult.success(
                targetData = yamlString,
                targetFormat = "yaml"
            )
        } catch (e: Exception) {
            logger.error("转换为YAML失败: {}", e.message, e)
            return TransformationResult.failure("转换为YAML失败: ${e.message}")
        }
    }
    
    /**
     * 应用转换规则
     */
    private fun applyRules(records: List<CSVRecord>, rules: Map<String, Any?>): List<CSVRecord> {
        // 创建结果列表
        val result = mutableListOf<CSVRecord>()
        
        // 获取字段映射
        val fieldMappings = rules["fieldMappings"] as? Map<*, *> ?: emptyMap<String, String>()
        
        // 获取值转换器
        val valueConverters = rules["valueConverters"] as? Map<*, *> ?: emptyMap<String, Map<String, Any?>>()
        
        // 获取包含列
        val includeColumns = rules["includeColumns"] as? List<*>
        
        // 获取排除列
        val excludeColumns = rules["excludeColumns"] as? List<*>
        
        // 处理每条记录
        for (record in records) {
            // 创建新记录
            val newRecord = mutableMapOf<String, String>()
            
            // 应用字段映射和值转换
            for ((key, value) in record.toMap()) {
                // 获取目标字段名
                val targetKey = fieldMappings[key]?.toString() ?: key
                
                // 应用值转换
                val converter = valueConverters[key] as? Map<*, *>
                val targetValue = if (converter != null) {
                    applyValueConverter(value, converter)
                } else {
                    value
                }
                
                // 检查是否包含该列
                if (includeColumns != null && !includeColumns.contains(key)) {
                    continue
                }
                
                // 检查是否排除该列
                if (excludeColumns != null && excludeColumns.contains(key)) {
                    continue
                }
                
                // 添加到新记录
                newRecord[targetKey] = targetValue
            }
            
            // 添加到结果列表
            result.add(createCSVRecord(newRecord))
        }
        
        return result
    }
    
    /**
     * 应用值转换器
     */
    private fun applyValueConverter(value: String, converter: Map<*, *>): String {
        val type = converter["type"] as? String ?: return value
        
        return when (type) {
            "replace" -> {
                val search = converter["search"] as? String ?: return value
                val replace = converter["replace"] as? String ?: ""
                value.replace(search, replace)
            }
            "uppercase" -> {
                value.uppercase()
            }
            "lowercase" -> {
                value.lowercase()
            }
            "trim" -> {
                value.trim()
            }
            else -> value
        }
    }
    
    /**
     * 创建CSV记录
     */
    private fun createCSVRecord(map: Map<String, String>): CSVRecord {
        // 创建CSV记录
        return object : CSVRecord {
            override fun get(i: Int): String {
                return map.values.elementAt(i)
            }
            
            override fun get(name: String): String {
                return map[name] ?: ""
            }
            
            override fun isSet(name: String): Boolean {
                return map.containsKey(name)
            }
            
            override fun iterator(): Iterator<String> {
                return map.values.iterator()
            }
            
            override fun toMap(): Map<String, String> {
                return map
            }
            
            override fun getRecordNumber(): Long {
                return 0
            }
            
            override fun size(): Int {
                return map.size
            }
            
            override fun toString(): String {
                return map.toString()
            }
            
            override fun getParser(): CSVParser? {
                return null
            }
            
            override fun getComment(): String? {
                return null
            }
        }
    }
}
