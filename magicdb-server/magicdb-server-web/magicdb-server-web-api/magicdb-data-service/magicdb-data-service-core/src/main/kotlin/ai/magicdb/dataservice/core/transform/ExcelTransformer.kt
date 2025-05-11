package ai.magicdb.dataservice.core.transform

import ai.magicdb.dataservice.api.model.TransformationRequest
import ai.magicdb.dataservice.api.model.TransformationResult
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.StringWriter
import java.util.Base64
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Excel 格式转换器
 *
 * @author magicdb
 */
class ExcelTransformer(private val objectMapper: ObjectMapper) : FormatTransformer {
    private val logger = LoggerFactory.getLogger(ExcelTransformer::class.java)
    
    /**
     * 转换数据
     */
    override fun transform(request: TransformationRequest): TransformationResult {
        try {
            // 解析源数据为 Excel 数据
            val excelData = parseSourceData(request.sourceData)
            
            // 根据目标格式转换
            return when (request.targetFormat.lowercase()) {
                "excel" -> transformToExcel(excelData, request)
                "json" -> transformToJson(excelData, request)
                "xml" -> transformToXml(excelData, request)
                "csv" -> transformToCsv(excelData, request)
                "yaml" -> transformToYaml(excelData, request)
                else -> TransformationResult.failure("不支持的目标格式: ${request.targetFormat}")
            }
        } catch (e: Exception) {
            logger.error("Excel 转换失败: {}", e.message, e)
            return TransformationResult.failure("Excel 转换失败: ${e.message}")
        }
    }
    
    /**
     * 获取支持的源格式
     */
    override fun getSupportedSourceFormats(): List<String> {
        return listOf("excel", "xlsx", "xls")
    }
    
    /**
     * 获取支持的目标格式
     */
    override fun getSupportedTargetFormats(): List<String> {
        return listOf("excel", "xlsx", "xls", "json", "xml", "csv", "yaml")
    }
    
    /**
     * 验证转换规则
     */
    override fun validateRules(rules: Map<String, Any?>): List<String> {
        val errors = mutableListOf<String>()
        
        // 验证字段映射
        val fieldMappings = rules["fieldMappings"] as? Map<*, *>
        if (fieldMappings != null) {
            for ((key, value) in fieldMappings) {
                if (key == null || value == null) {
                    errors.add("字段映射中的键和值不能为空")
                }
            }
        }
        
        // 验证值转换器
        val valueConverters = rules["valueConverters"] as? Map<*, *>
        if (valueConverters != null) {
            for ((key, value) in valueConverters) {
                if (key == null) {
                    errors.add("值转换器中的键不能为空")
                }
                
                val converter = value as? Map<*, *>
                if (converter == null) {
                    errors.add("值转换器必须是一个对象")
                } else {
                    val type = converter["type"] as? String
                    if (type == null) {
                        errors.add("值转换器必须指定类型")
                    } else if (!listOf("replace", "uppercase", "lowercase", "trim").contains(type)) {
                        errors.add("不支持的值转换器类型: $type")
                    }
                }
            }
        }
        
        return errors
    }
    
    /**
     * 获取转换规则模板
     */
    override fun getRuleTemplate(): Map<String, Any?> {
        return mapOf(
            "fieldMappings" to mapOf(
                "sourceField1" to "targetField1",
                "sourceField2" to "targetField2"
            ),
            "valueConverters" to mapOf(
                "field1" to mapOf(
                    "type" to "replace",
                    "search" to "searchText",
                    "replace" to "replaceText"
                ),
                "field2" to mapOf(
                    "type" to "uppercase"
                )
            ),
            "includeFields" to listOf("field1", "field2"),
            "excludeFields" to listOf("field3"),
            "sheetName" to "Sheet1",
            "headerRow" to 0
        )
    }
    
    /**
     * 解析源数据
     */
    private fun parseSourceData(sourceData: Any): List<Map<String, String>> {
        when (sourceData) {
            is String -> {
                // 尝试解析为 Base64 编码的 Excel 文件
                try {
                    val bytes = Base64.getDecoder().decode(sourceData)
                    return parseExcelBytes(bytes)
                } catch (e: Exception) {
                    logger.error("解析 Excel 数据失败: {}", e.message, e)
                    throw IllegalArgumentException("无效的 Excel 数据格式")
                }
            }
            is ByteArray -> {
                return parseExcelBytes(sourceData)
            }
            else -> {
                throw IllegalArgumentException("不支持的源数据类型: ${sourceData.javaClass.name}")
            }
        }
    }
    
    /**
     * 解析 Excel 字节数据
     */
    private fun parseExcelBytes(bytes: ByteArray): List<Map<String, String>> {
        val result = mutableListOf<Map<String, String>>()
        
        ByteArrayInputStream(bytes).use { inputStream ->
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)
            
            // 获取表头行
            val headerRow = sheet.getRow(0)
            val headers = mutableListOf<String>()
            
            // 解析表头
            for (cell in headerRow) {
                headers.add(cell.stringCellValue)
            }
            
            // 解析数据行
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue
                val rowData = mutableMapOf<String, String>()
                
                for (cellIndex in headers.indices) {
                    val cell = row.getCell(cellIndex)
                    val header = headers[cellIndex]
                    
                    // 根据单元格类型获取值
                    val value = when (cell?.cellType) {
                        CellType.STRING -> cell.stringCellValue
                        CellType.NUMERIC -> {
                            if (DateUtil.isCellDateFormatted(cell)) {
                                cell.dateCellValue.toString()
                            } else {
                                cell.numericCellValue.toString()
                            }
                        }
                        CellType.BOOLEAN -> cell.booleanCellValue.toString()
                        CellType.FORMULA -> {
                            try {
                                cell.stringCellValue
                            } catch (e: Exception) {
                                try {
                                    cell.numericCellValue.toString()
                                } catch (e2: Exception) {
                                    ""
                                }
                            }
                        }
                        else -> ""
                    }
                    
                    rowData[header] = value
                }
                
                result.add(rowData)
            }
        }
        
        return result
    }
    
    /**
     * 转换为 Excel
     */
    private fun transformToExcel(data: List<Map<String, String>>, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedData = applyRules(data, request.rules)
            
            // 创建工作簿
            val workbook = WorkbookFactory.create(true)
            val sheet = workbook.createSheet(request.rules["sheetName"] as? String ?: "Sheet1")
            
            // 创建表头行
            val headerRow = sheet.createRow(0)
            val headers = if (transformedData.isNotEmpty()) transformedData[0].keys.toList() else emptyList()
            
            // 设置表头
            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
            }
            
            // 设置数据行
            transformedData.forEachIndexed { rowIndex, rowData ->
                val row = sheet.createRow(rowIndex + 1)
                
                headers.forEachIndexed { colIndex, header ->
                    val cell = row.createCell(colIndex)
                    cell.setCellValue(rowData[header] ?: "")
                }
            }
            
            // 转换为字节数组
            val outputStream = ByteArrayOutputStream()
            workbook.write(outputStream)
            val bytes = outputStream.toByteArray()
            
            // 转换为 Base64 字符串
            val base64 = Base64.getEncoder().encodeToString(bytes)
            
            return TransformationResult.success(
                targetData = base64,
                targetFormat = "excel"
            )
        } catch (e: Exception) {
            logger.error("转换为 Excel 失败: {}", e.message, e)
            return TransformationResult.failure("转换为 Excel 失败: ${e.message}")
        }
    }
    
    /**
     * 转换为 JSON
     */
    private fun transformToJson(data: List<Map<String, String>>, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedData = applyRules(data, request.rules)
            
            // 创建 JSON 数组
            val arrayNode = objectMapper.createArrayNode()
            
            // 转换记录为 JSON 对象
            for (record in transformedData) {
                val objectNode = objectMapper.createObjectNode()
                for ((key, value) in record) {
                    objectNode.put(key, value)
                }
                arrayNode.add(objectNode)
            }
            
            // 转换为 JSON 字符串
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
            logger.error("转换为 JSON 失败: {}", e.message, e)
            return TransformationResult.failure("转换为 JSON 失败: ${e.message}")
        }
    }
    
    /**
     * 转换为 XML
     */
    private fun transformToXml(data: List<Map<String, String>>, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedData = applyRules(data, request.rules)
            
            // 创建 XML 文档
            val docFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = docFactory.newDocumentBuilder()
            val doc = docBuilder.newDocument()
            
            // 设置根元素
            val rootElementName = request.rules["rootElement"] as? String ?: "root"
            val rootElement = doc.createElement(rootElementName)
            doc.appendChild(rootElement)
            
            // 设置记录元素名称
            val recordElementName = request.rules["recordElement"] as? String ?: "record"
            
            // 转换记录为 XML 元素
            for (record in transformedData) {
                val recordElement = doc.createElement(recordElementName)
                rootElement.appendChild(recordElement)
                
                for ((key, value) in record) {
                    val fieldElement = doc.createElement(key)
                    fieldElement.textContent = value
                    recordElement.appendChild(fieldElement)
                }
            }
            
            // 转换为 XML 字符串
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
            logger.error("转换为 XML 失败: {}", e.message, e)
            return TransformationResult.failure("转换为 XML 失败: ${e.message}")
        }
    }
    
    /**
     * 转换为 CSV
     */
    private fun transformToCsv(data: List<Map<String, String>>, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedData = applyRules(data, request.rules)
            
            // 获取所有字段名
            val headers = if (transformedData.isNotEmpty()) {
                transformedData[0].keys.toList()
            } else {
                emptyList()
            }
            
            // 构建 CSV 字符串
            val sb = StringBuilder()
            
            // 添加表头
            sb.append(headers.joinToString(",") { escapeCSV(it) })
            sb.append("\n")
            
            // 添加数据行
            for (record in transformedData) {
                val values = headers.map { escapeCSV(record[it] ?: "") }
                sb.append(values.joinToString(","))
                sb.append("\n")
            }
            
            return TransformationResult.success(
                targetData = sb.toString(),
                targetFormat = "csv"
            )
        } catch (e: Exception) {
            logger.error("转换为 CSV 失败: {}", e.message, e)
            return TransformationResult.failure("转换为 CSV 失败: ${e.message}")
        }
    }
    
    /**
     * 转换为 YAML
     */
    private fun transformToYaml(data: List<Map<String, String>>, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedData = applyRules(data, request.rules)
            
            // 转换为 YAML
            val yaml = Yaml()
            val yamlString = yaml.dump(transformedData)
            
            return TransformationResult.success(
                targetData = yamlString,
                targetFormat = "yaml"
            )
        } catch (e: Exception) {
            logger.error("转换为 YAML 失败: {}", e.message, e)
            return TransformationResult.failure("转换为 YAML 失败: ${e.message}")
        }
    }
    
    /**
     * 应用转换规则
     */
    private fun applyRules(data: List<Map<String, String>>, rules: Map<String, Any?>): List<Map<String, String>> {
        // 创建结果列表
        val result = mutableListOf<Map<String, String>>()
        
        // 获取字段映射
        val fieldMappings = rules["fieldMappings"] as? Map<String, String> ?: emptyMap()
        
        // 获取值转换器
        val valueConverters = rules["valueConverters"] as? Map<String, Map<String, Any?>> ?: emptyMap()
        
        // 获取包含列
        val includeFields = rules["includeFields"] as? List<String>
        
        // 获取排除列
        val excludeFields = rules["excludeFields"] as? List<String>
        
        // 处理每条记录
        for (record in data) {
            // 创建新的记录映射
            val recordMap = mutableMapOf<String, String>()
            
            // 处理字段映射和过滤
            for ((key, value) in record) {
                // 检查是否包含该列
                if (includeFields != null && !includeFields.contains(key)) {
                    continue
                }
                
                // 检查是否排除该列
                if (excludeFields != null && excludeFields.contains(key)) {
                    continue
                }
                
                // 应用字段映射
                val targetKey = fieldMappings[key] ?: key
                
                // 应用值转换器
                val targetValue = applyValueConverter(key, value, valueConverters)
                
                // 添加到新记录中
                recordMap[targetKey] = targetValue
            }
            
            // 添加到结果中
            result.add(recordMap)
        }
        
        return result
    }
    
    /**
     * 应用值转换器
     */
    private fun applyValueConverter(key: String, value: String, valueConverters: Map<String, Map<String, Any?>>): String {
        // 获取该字段的值转换器
        val converter = valueConverters[key] ?: return value
        
        // 应用转换规则
        return when (val type = converter["type"] as? String) {
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
            else -> {
                logger.warn("不支持的值转换器类型: {}", type)
                value
            }
        }
    }
    
    /**
     * 转义 CSV 字段值
     */
    private fun escapeCSV(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}
