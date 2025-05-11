package ai.magicdb.dataservice.core.transform

import ai.magicdb.dataservice.api.model.TransformationRequest
import ai.magicdb.dataservice.api.model.TransformationResult
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.LoggerFactory
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.yaml.snakeyaml.Yaml

/**
 * JSON转换器
 *
 * @author magicdb
 */
class JsonTransformer(
    private val objectMapper: ObjectMapper
) : FormatTransformer {
    private val logger = LoggerFactory.getLogger(JsonTransformer::class.java)

    override fun transform(request: TransformationRequest): TransformationResult {
        try {
            // 解析源数据为JSON
            val jsonNode = parseSourceData(request.sourceData)

            // 根据目标格式转换
            return when (request.targetFormat.lowercase()) {
                "json" -> transformToJson(jsonNode, request)
                "xml" -> transformToXml(jsonNode, request)
                "csv" -> transformToCsv(jsonNode, request)
                "yaml" -> transformToYaml(jsonNode, request)
                else -> TransformationResult.failure("不支持的目标格式: ${request.targetFormat}")
            }
        } catch (e: Exception) {
            logger.error("JSON转换失败: {}", e.message, e)
            return TransformationResult.failure("JSON转换失败: ${e.message}")
        }
    }

    override fun getSupportedSourceFormats(): List<String> {
        return listOf("json")
    }

    override fun getSupportedTargetFormats(): List<String> {
        return listOf("json", "xml", "csv", "yaml")
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
                    "type" to "script",
                    "language" to "js",
                    "script" to "value.toUpperCase()"
                )
            ),
            "includeFields" to listOf("field1", "field2"),
            "excludeFields" to listOf("field3", "field4"),
            "rootElement" to "root", // 用于XML
            "delimiter" to ",", // 用于CSV
            "quoteChar" to "\"", // 用于CSV
            "header" to true, // 用于CSV
            "pretty" to true // 格式化输出
        )
    }

    /**
     * 解析源数据
     */
    private fun parseSourceData(sourceData: Any): JsonNode {
        return when (sourceData) {
            is String -> objectMapper.readTree(sourceData)
            is Map<*, *> -> objectMapper.valueToTree(sourceData)
            is Collection<*> -> objectMapper.valueToTree(sourceData)
            is JsonNode -> sourceData
            else -> objectMapper.valueToTree(sourceData)
        }
    }

    /**
     * 转换为JSON
     */
    private fun transformToJson(jsonNode: JsonNode, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedNode = applyRules(jsonNode, request.rules)

            // 转换为JSON字符串
            val pretty = request.options["pretty"] as? Boolean ?: false
            val jsonString = if (pretty) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(transformedNode)
            } else {
                objectMapper.writeValueAsString(transformedNode)
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
    private fun transformToXml(jsonNode: JsonNode, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedNode = applyRules(jsonNode, request.rules)

            // 创建XML文档
            val docFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = docFactory.newDocumentBuilder()
            val doc = docBuilder.newDocument()

            // 设置根元素
            val rootElementName = request.rules["rootElement"] as? String ?: "root"
            val rootElement = doc.createElement(rootElementName)
            doc.appendChild(rootElement)

            // 转换JSON为XML
            jsonToXml(transformedNode, rootElement, doc)

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
    private fun transformToCsv(jsonNode: JsonNode, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedNode = applyRules(jsonNode, request.rules)

            // 检查是否为数组
            if (!transformedNode.isArray()) {
                return TransformationResult.failure("CSV转换需要JSON数组")
            }

            // 获取CSV选项
            val delimiter = request.options["delimiter"] as? String ?: ","
            val quoteChar = request.options["quoteChar"] as? String ?: "\""
            val header = request.options["header"] as? Boolean ?: true

            // 创建CSV格式
            val csvFormat = CSVFormat.DEFAULT
                .withDelimiter(delimiter[0])
                .withQuote(quoteChar[0])
                .withHeader(*getHeaders(transformedNode as ArrayNode).toTypedArray())
                .withSkipHeaderRecord(!header)

            // 创建CSV打印器
            val writer = StringWriter()
            val csvPrinter = CSVPrinter(writer, csvFormat)

            // 写入数据
            for (i in 0 until transformedNode.size()) {
                val row = transformedNode[i]
                if (row.isObject()) {
                    val values = mutableListOf<Any?>()
                    for (header in getHeaders(transformedNode)) {
                        values.add(row[header]?.asText() ?: "")
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
     * 转换为YAML
     */
    private fun transformToYaml(jsonNode: JsonNode, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedNode = applyRules(jsonNode, request.rules)

            // 转换为Java对象
            val javaObject = objectMapper.treeToValue(transformedNode, Any::class.java)

            // 转换为YAML
            val yaml = Yaml()
            val yamlString = yaml.dump(javaObject)

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
    private fun applyRules(jsonNode: JsonNode, rules: Map<String, Any?>): JsonNode {
        // 创建副本
        val result: JsonNode = jsonNode.deepCopy()

        // 应用字段映射
        @Suppress("UNCHECKED_CAST")
        val fieldMappings = rules["fieldMappings"] as? Map<String, String>
        if (fieldMappings != null && result.isObject()) {
            val objectNode = result as ObjectNode
            val fieldsToAdd = mutableMapOf<String, JsonNode>()
            val fieldsToRemove = mutableListOf<String>()

            for ((sourceField, targetField) in fieldMappings) {
                if (objectNode.has(sourceField) && sourceField != targetField) {
                    fieldsToAdd[targetField] = objectNode.get(sourceField)
                    fieldsToRemove.add(sourceField)
                }
            }

            // 添加新字段
            for ((field, value) in fieldsToAdd) {
                objectNode.replace(field, value)
            }

            // 移除旧字段
            for (field in fieldsToRemove) {
                objectNode.remove(field)
            }
        }

        // 应用值转换
        val valueConverters = rules["valueConverters"] as? Map<*, *>
        if (valueConverters != null) {
            applyValueConverters(result, valueConverters)
        }

        // 应用包含字段
        val includeFields = rules["includeFields"] as? List<*>
        if (includeFields != null && result.isObject()) {
            val objectNode = result as ObjectNode
            val fieldsToRemove = mutableListOf<String>()

            objectNode.fieldNames().forEach { field ->
                if (!includeFields.contains(field)) {
                    fieldsToRemove.add(field)
                }
            }

            for (field in fieldsToRemove) {
                objectNode.remove(field)
            }
        }

        // 应用排除字段
        val excludeFields = rules["excludeFields"] as? List<*>
        if (excludeFields != null && result.isObject()) {
            val objectNode = result as ObjectNode

            for (field in excludeFields) {
                objectNode.remove(field.toString())
            }
        }

        return result
    }

    /**
     * 应用值转换器
     */
    private fun applyValueConverters(jsonNode: JsonNode, valueConverters: Map<*, *>) {
        when {
            jsonNode.isObject() -> {
                val objectNode = jsonNode as ObjectNode

                // 遍历字段
                objectNode.fieldNames().forEach { field ->
                    val converter = valueConverters[field]
                    if (converter != null) {
                        // 应用转换器
                        val value = objectNode.get(field)
                        val convertedValue = applyValueConverter(value, converter as Map<*, *>)
                        objectNode.set<JsonNode>(field, convertedValue)
                    }

                    // 递归处理嵌套对象
                    val value = objectNode.get(field)
                    if (value.isObject() || value.isArray()) {
                        applyValueConverters(value, valueConverters)
                    }
                }
            }
            jsonNode.isArray() -> {
                val arrayNode = jsonNode as ArrayNode

                // 遍历数组元素
                for (i in 0 until arrayNode.size()) {
                    val element = arrayNode.get(i)
                    if (element.isObject() || element.isArray()) {
                        applyValueConverters(element, valueConverters)
                    }
                }
            }
        }
    }

    /**
     * 应用值转换器
     */
    private fun applyValueConverter(value: JsonNode, converter: Map<*, *>): JsonNode {
        val type = converter["type"] as? String ?: return value

        return when (type) {
            "replace" -> {
                val search = converter["search"] as? String ?: return value
                val replace = converter["replace"] as? String ?: ""
                val text = value.asText().replace(search, replace)
                objectMapper.valueToTree(text)
            }
            "uppercase" -> {
                val text = value.asText().uppercase()
                objectMapper.valueToTree(text)
            }
            "lowercase" -> {
                val text = value.asText().lowercase()
                objectMapper.valueToTree(text)
            }
            "trim" -> {
                val text = value.asText().trim()
                objectMapper.valueToTree(text)
            }
            "number" -> {
                try {
                    val number = value.asText().toDouble()
                    objectMapper.valueToTree(number)
                } catch (e: Exception) {
                    value
                }
            }
            "boolean" -> {
                val text = value.asText().lowercase()
                val boolean = text == "true" || text == "yes" || text == "1"
                objectMapper.valueToTree(boolean)
            }
            "date" -> {
                // 简单日期转换，实际应用中可能需要更复杂的逻辑
                try {
                    val format = converter["format"] as? String ?: "yyyy-MM-dd"
                    val date = java.text.SimpleDateFormat(format).parse(value.asText())
                    objectMapper.valueToTree(date)
                } catch (e: Exception) {
                    value
                }
            }
            else -> value
        }
    }

    /**
     * JSON转XML
     */
    private fun jsonToXml(jsonNode: JsonNode, parentElement: Element, doc: Document) {
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
                            jsonToXml(value, element, doc)
                        }
                        value.isArray() -> {
                            // 处理数组节点
                            val arrayNode = value as ArrayNode
                            for (i in 0 until arrayNode.size()) {
                                val element = doc.createElement(name)
                                parentElement.appendChild(element)
                                jsonToXml(arrayNode.get(i), element, doc)
                            }
                        }
                    }
                }
            }
            jsonNode.isArray() -> {
                // 处理数组
                val arrayNode = jsonNode as ArrayNode
                for (i in 0 until arrayNode.size()) {
                    val element = doc.createElement("item")
                    parentElement.appendChild(element)
                    jsonToXml(arrayNode.get(i), element, doc)
                }
            }
            jsonNode.isValueNode() -> {
                // 处理值
                parentElement.textContent = jsonNode.asText()
            }
        }
    }

    /**
     * 获取CSV表头
     */
    private fun getHeaders(arrayNode: ArrayNode): List<String> {
        val headers = mutableSetOf<String>()

        // 遍历数组中的所有对象，收集所有字段名
        for (i in 0 until arrayNode.size()) {
            val node = arrayNode.get(i)
            if (node.isObject()) {
                node.fieldNames().forEach { headers.add(it) }
            }
        }

        return headers.toList()
    }
}
