package ai.magicdb.data.service.core.transform

import ai.magicdb.data.service.api.model.TransformationRequest
import ai.magicdb.data.service.api.model.TransformationResult
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import org.yaml.snakeyaml.Yaml
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * YAML转换器
 *
 * @author magicdb
 */
class YamlTransformer(
    private val objectMapper: ObjectMapper
) : FormatTransformer {
    private val logger = LoggerFactory.getLogger(YamlTransformer::class.java)
    
    override fun transform(request: TransformationRequest): TransformationResult {
        try {
            // 解析源数据为YAML
            val yamlData = parseSourceData(request.sourceData)
            
            // 根据目标格式转换
            return when (request.targetFormat.lowercase()) {
                "yaml" -> transformToYaml(yamlData, request)
                "json" -> transformToJson(yamlData, request)
                "xml" -> transformToXml(yamlData, request)
                "csv" -> transformToCsv(yamlData, request)
                else -> TransformationResult.failure("不支持的目标格式: ${request.targetFormat}")
            }
        } catch (e: Exception) {
            logger.error("YAML转换失败: {}", e.message, e)
            return TransformationResult.failure("YAML转换失败: ${e.message}")
        }
    }
    
    override fun getSupportedSourceFormats(): List<String> {
        return listOf("yaml")
    }
    
    override fun getSupportedTargetFormats(): List<String> {
        return listOf("yaml", "json", "xml", "csv")
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
                "sourceField1" to "targetField1",
                "sourceField2" to "targetField2"
            ),
            "valueConverters" to mapOf(
                "field1" to mapOf(
                    "type" to "replace",
                    "search" to "oldValue",
                    "replace" to "newValue"
                ),
                "field2" to mapOf(
                    "type" to "uppercase"
                )
            ),
            "includeFields" to listOf("field1", "field2"),
            "excludeFields" to listOf("field3", "field4"),
            "rootElement" to "root", // 用于XML
            "recordElement" to "record", // 用于XML
            "delimiter" to ",", // 用于CSV
            "quoteChar" to "\"", // 用于CSV
            "header" to true, // 用于CSV
            "pretty" to true // 格式化输出
        )
    }
    
    /**
     * 解析源数据
     */
    private fun parseSourceData(sourceData: Any): Any {
        return when (sourceData) {
            is String -> {
                val yaml = Yaml()
                yaml.load<Any>(sourceData)
            }
            is Map<*, *>, is List<*> -> sourceData
            else -> throw IllegalArgumentException("不支持的YAML源数据类型: ${sourceData.javaClass.name}")
        }
    }
    
    /**
     * 转换为YAML
     */
    private fun transformToYaml(yamlData: Any, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedData = applyRules(yamlData, request.rules)
            
            // 转换为YAML
            val yaml = Yaml()
            val yamlString = yaml.dump(transformedData)
            
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
     * 转换为JSON
     */
    private fun transformToJson(yamlData: Any, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedData = applyRules(yamlData, request.rules)
            
            // 转换为JSON
            val pretty = request.options["pretty"] as? Boolean ?: false
            val jsonString = if (pretty) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(transformedData)
            } else {
                objectMapper.writeValueAsString(transformedData)
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
    private fun transformToXml(yamlData: Any, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedData = applyRules(yamlData, request.rules)
            
            // 转换为JSON节点
            val jsonNode = objectMapper.valueToTree<JsonNode>(transformedData)
            
            // 创建XML文档
            val docFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = docFactory.newDocumentBuilder()
            val doc = docBuilder.newDocument()
            
            // 设置根元素
            val rootElementName = request.rules["rootElement"] as? String ?: "root"
            val rootElement = doc.createElement(rootElementName)
            doc.appendChild(rootElement)
            
            // 转换JSON为XML
            jsonToXml(jsonNode, rootElement, doc, request.rules)
            
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
     * 转换为CSV
     */
    private fun transformToCsv(yamlData: Any, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedData = applyRules(yamlData, request.rules)
            
            // 检查是否为列表
            if (transformedData !is List<*>) {
                return TransformationResult.failure("CSV转换需要YAML列表")
            }
            
            // 检查列表是否为空
            if (transformedData.isEmpty()) {
                return TransformationResult.success(
                    targetData = "",
                    targetFormat = "csv"
                )
            }
            
            // 检查列表元素是否为映射
            if (transformedData[0] !is Map<*, *>) {
                return TransformationResult.failure("CSV转换需要YAML映射列表")
            }
            
            // 获取CSV选项
            val delimiter = request.options["delimiter"] as? String ?: ","
            val quoteChar = request.options["quoteChar"] as? String ?: "\""
            val header = request.options["header"] as? Boolean ?: true
            
            // 获取表头
            val headers = (transformedData[0] as Map<*, *>).keys.map { it.toString() }
            
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
            for (item in transformedData) {
                if (item is Map<*, *>) {
                    val values = headers.map { header ->
                        item[header]?.toString() ?: ""
                    }
                    csvPrinter.printRecord(values)
                }
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
     * 应用转换规则
     */
    @Suppress("UNCHECKED_CAST")
    private fun applyRules(data: Any, rules: Map<String, Any?>): Any {
        when (data) {
            is Map<*, *> -> {
                val result = mutableMapOf<String, Any?>()
                
                // 获取字段映射
                val fieldMappings = rules["fieldMappings"] as? Map<*, *> ?: emptyMap<String, String>()
                
                // 获取值转换器
                val valueConverters = rules["valueConverters"] as? Map<*, *> ?: emptyMap<String, Map<String, Any?>>()
                
                // 获取包含字段
                val includeFields = rules["includeFields"] as? List<*>
                
                // 获取排除字段
                val excludeFields = rules["excludeFields"] as? List<*>
                
                // 处理每个字段
                for ((key, value) in data) {
                    val keyStr = key.toString()
                    
                    // 获取目标字段名
                    val targetKey = fieldMappings[keyStr]?.toString() ?: keyStr
                    
                    // 递归应用规则
                    val targetValue = when (value) {
                        is Map<*, *>, is List<*> -> applyRules(value, rules)
                        else -> {
                            // 应用值转换
                            val converter = valueConverters[keyStr] as? Map<*, *>
                            if (converter != null) {
                                applyValueConverter(value?.toString() ?: "", converter)
                            } else {
                                value
                            }
                        }
                    }
                    
                    // 检查是否包含该字段
                    if (includeFields != null && !includeFields.contains(keyStr)) {
                        continue
                    }
                    
                    // 检查是否排除该字段
                    if (excludeFields != null && excludeFields.contains(keyStr)) {
                        continue
                    }
                    
                    // 添加到结果
                    result[targetKey] = targetValue
                }
                
                return result
            }
            is List<*> -> {
                return data.map { item ->
                    if (item != null) {
                        applyRules(item, rules)
                    } else {
                        null
                    }
                }
            }
            else -> return data
        }
    }
    
    /**
     * 应用值转换器
     */
    private fun applyValueConverter(value: String, converter: Map<*, *>): Any {
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
            "number" -> {
                try {
                    value.toDouble()
                } catch (e: Exception) {
                    value
                }
            }
            "boolean" -> {
                val text = value.lowercase()
                text == "true" || text == "yes" || text == "1"
            }
            else -> value
        }
    }
    
    /**
     * JSON转XML
     */
    private fun jsonToXml(jsonNode: JsonNode, parentElement: org.w3c.dom.Element, doc: Document, rules: Map<String, Any?>) {
        val recordElementName = rules["recordElement"] as? String ?: "record"
        
        when {
            jsonNode.isObject() -> {
                // 处理对象
                val objectNode = jsonNode as ObjectNode
                objectNode.fields().forEach { (name, value) ->
                    when {
                        value.isValueNode() -> {
                            // 处理值节点
                            val element = doc.createElement(name)
                            element.textContent = value.asText()
                            parentElement.appendChild(element)
                        }
                        value.isObject() -> {
                            // 处理对象节点
                            val element = doc.createElement(name)
                            parentElement.appendChild(element)
                            jsonToXml(value, element, doc, rules)
                        }
                        value.isArray() -> {
                            // 处理数组节点
                            val arrayNode = value as ArrayNode
                            for (i in 0 until arrayNode.size()) {
                                val element = doc.createElement(name)
                                parentElement.appendChild(element)
                                jsonToXml(arrayNode.get(i), element, doc, rules)
                            }
                        }
                    }
                }
            }
            jsonNode.isArray() -> {
                // 处理数组
                val arrayNode = jsonNode as ArrayNode
                for (i in 0 until arrayNode.size()) {
                    val element = doc.createElement(recordElementName)
                    parentElement.appendChild(element)
                    jsonToXml(arrayNode.get(i), element, doc, rules)
                }
            }
            jsonNode.isValueNode() -> {
                // 处理值
                parentElement.textContent = jsonNode.asText()
            }
        }
    }
}
