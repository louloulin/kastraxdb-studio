package ai.magicdb.dataservice.core.transform

import ai.magicdb.dataservice.api.model.TransformationRequest
import ai.magicdb.dataservice.api.model.TransformationResult
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.yaml.snakeyaml.Yaml

/**
 * XML转换器
 *
 * @author magicdb
 */
class XmlTransformer(
    private val objectMapper: ObjectMapper
) : FormatTransformer {
    private val logger = LoggerFactory.getLogger(XmlTransformer::class.java)
    
    override fun transform(request: TransformationRequest): TransformationResult {
        try {
            // 解析源数据为XML
            val document = parseSourceData(request.sourceData)
            
            // 根据目标格式转换
            return when (request.targetFormat.lowercase()) {
                "xml" -> transformToXml(document, request)
                "json" -> transformToJson(document, request)
                "csv" -> transformToCsv(document, request)
                "yaml" -> transformToYaml(document, request)
                else -> TransformationResult.failure("不支持的目标格式: ${request.targetFormat}")
            }
        } catch (e: Exception) {
            logger.error("XML转换失败: {}", e.message, e)
            return TransformationResult.failure("XML转换失败: ${e.message}")
        }
    }
    
    override fun getSupportedSourceFormats(): List<String> {
        return listOf("xml")
    }
    
    override fun getSupportedTargetFormats(): List<String> {
        return listOf("xml", "json", "csv", "yaml")
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
                "sourceElement1" to "targetElement1",
                "sourceElement2" to "targetElement2"
            ),
            "valueConverters" to mapOf(
                "element1" to mapOf(
                    "type" to "replace",
                    "search" to "oldValue",
                    "replace" to "newValue"
                ),
                "element2" to mapOf(
                    "type" to "script",
                    "language" to "js",
                    "script" to "value.toUpperCase()"
                )
            ),
            "includeElements" to listOf("element1", "element2"),
            "excludeElements" to listOf("element3", "element4"),
            "rootElement" to "root", // 用于XML
            "attributesAsElements" to false, // 是否将属性转换为元素
            "pretty" to true // 格式化输出
        )
    }
    
    /**
     * 解析源数据
     */
    private fun parseSourceData(sourceData: Any): Document {
        val xmlString = when (sourceData) {
            is String -> sourceData
            is Document -> documentToString(sourceData)
            else -> throw IllegalArgumentException("不支持的XML源数据类型: ${sourceData.javaClass.name}")
        }
        
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val inputSource = InputSource(StringReader(xmlString))
        return builder.parse(inputSource)
    }
    
    /**
     * 转换为XML
     */
    private fun transformToXml(document: Document, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedDoc = applyRules(document, request.rules)
            
            // 转换为XML字符串
            val pretty = request.options["pretty"] as? Boolean ?: false
            val xmlString = documentToString(transformedDoc, pretty)
            
            return TransformationResult.success(
                targetData = xmlString,
                targetFormat = "xml"
            )
        } catch (e: Exception) {
            logger.error("转换为XML失败: {}", e.message, e)
            return TransformationResult.failure("转换为XML失败: ${e.message}")
        }
    }
    
    /**
     * 转换为JSON
     */
    private fun transformToJson(document: Document, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedDoc = applyRules(document, request.rules)
            
            // 转换为JSON
            val jsonNode = xmlToJson(transformedDoc)
            
            // 转换为JSON字符串
            val pretty = request.options["pretty"] as? Boolean ?: false
            val jsonString = if (pretty) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode)
            } else {
                objectMapper.writeValueAsString(jsonNode)
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
     * 转换为CSV
     */
    private fun transformToCsv(document: Document, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedDoc = applyRules(document, request.rules)
            
            // 转换为JSON
            val jsonNode = xmlToJson(transformedDoc)
            
            // 检查是否为数组
            if (!jsonNode.isArray() && jsonNode.isObject()) {
                // 尝试获取根元素下的数组
                val objectNode = jsonNode as ObjectNode
                val arrayField = objectNode.fields().asSequence().find { it.value.isArray() }
                
                if (arrayField != null) {
                    val arrayNode = arrayField.value as ArrayNode
                    return jsonToCsv(arrayNode, request)
                } else {
                    // 创建单行CSV
                    return jsonToCsv(objectMapper.createArrayNode().add(jsonNode), request)
                }
            } else if (jsonNode.isArray()) {
                return jsonToCsv(jsonNode as ArrayNode, request)
            } else {
                return TransformationResult.failure("CSV转换需要XML元素集合")
            }
        } catch (e: Exception) {
            logger.error("转换为CSV失败: {}", e.message, e)
            return TransformationResult.failure("转换为CSV失败: ${e.message}")
        }
    }
    
    /**
     * 转换为YAML
     */
    private fun transformToYaml(document: Document, request: TransformationRequest): TransformationResult {
        try {
            // 应用转换规则
            val transformedDoc = applyRules(document, request.rules)
            
            // 转换为JSON
            val jsonNode = xmlToJson(transformedDoc)
            
            // 转换为Java对象
            val javaObject = objectMapper.treeToValue(jsonNode, Any::class.java)
            
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
    private fun applyRules(document: Document, rules: Map<String, Any?>): Document {
        // 创建副本
        val result = document.cloneNode(true) as Document
        
        // 应用字段映射
        val fieldMappings = rules["fieldMappings"] as? Map<*, *>
        if (fieldMappings != null) {
            for ((source, target) in fieldMappings) {
                val sourceElement = source.toString()
                val targetElement = target.toString()
                
                // 查找源元素
                val sourceNodes = result.getElementsByTagName(sourceElement)
                if (sourceNodes.length > 0 && sourceElement != targetElement) {
                    // 创建新元素
                    for (i in 0 until sourceNodes.length) {
                        val sourceNode = sourceNodes.item(i)
                        val parentNode = sourceNode.parentNode
                        
                        // 创建新元素
                        val newElement = result.createElement(targetElement)
                        
                        // 复制子节点
                        while (sourceNode.hasChildNodes()) {
                            newElement.appendChild(sourceNode.firstChild)
                        }
                        
                        // 复制属性
                        if (sourceNode is Element) {
                            val attributes = sourceNode.attributes
                            for (j in 0 until attributes.length) {
                                val attribute = attributes.item(j)
                                newElement.setAttribute(attribute.nodeName, attribute.nodeValue)
                            }
                        }
                        
                        // 替换元素
                        parentNode.replaceChild(newElement, sourceNode)
                    }
                }
            }
        }
        
        // 应用值转换
        val valueConverters = rules["valueConverters"] as? Map<*, *>
        if (valueConverters != null) {
            for ((field, converter) in valueConverters) {
                val elementName = field.toString()
                
                // 查找元素
                val elements = result.getElementsByTagName(elementName)
                for (i in 0 until elements.length) {
                    val element = elements.item(i)
                    
                    // 应用转换器
                    val value = element.textContent
                    val convertedValue = applyValueConverter(value, converter as Map<*, *>)
                    element.textContent = convertedValue
                }
            }
        }
        
        // 应用包含元素
        val includeElements = rules["includeElements"] as? List<*>
        if (includeElements != null) {
            // 获取所有元素
            val allElements = getAllElements(result.documentElement)
            
            // 过滤不包含的元素
            for (element in allElements) {
                if (!includeElements.contains(element.nodeName) && element != result.documentElement) {
                    element.parentNode.removeChild(element)
                }
            }
        }
        
        // 应用排除元素
        val excludeElements = rules["excludeElements"] as? List<*>
        if (excludeElements != null) {
            // 获取所有元素
            val allElements = getAllElements(result.documentElement)
            
            // 移除排除的元素
            for (element in allElements) {
                if (excludeElements.contains(element.nodeName)) {
                    element.parentNode.removeChild(element)
                }
            }
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
     * 获取所有元素
     */
    private fun getAllElements(element: Element): List<Element> {
        val elements = mutableListOf<Element>()
        
        // 添加当前元素
        elements.add(element)
        
        // 递归处理子元素
        val childNodes = element.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node is Element) {
                elements.addAll(getAllElements(node))
            }
        }
        
        return elements
    }
    
    /**
     * XML转JSON
     */
    private fun xmlToJson(document: Document): JsonNode {
        val root = document.documentElement
        return elementToJson(root)
    }
    
    /**
     * 元素转JSON
     */
    private fun elementToJson(element: Element): JsonNode {
        val objectNode = objectMapper.createObjectNode()
        
        // 处理属性
        val attributes = element.attributes
        if (attributes.length > 0) {
            val attributesNode = objectMapper.createObjectNode()
            for (i in 0 until attributes.length) {
                val attribute = attributes.item(i)
                attributesNode.put(attribute.nodeName, attribute.nodeValue)
            }
            objectNode.set<JsonNode>("@attributes", attributesNode)
        }
        
        // 处理子元素
        val childNodes = element.childNodes
        val childElements = mutableMapOf<String, MutableList<Element>>()
        
        // 分组子元素
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node is Element) {
                val name = node.nodeName
                if (!childElements.containsKey(name)) {
                    childElements[name] = mutableListOf()
                }
                childElements[name]!!.add(node)
            } else if (node.nodeType == Node.TEXT_NODE) {
                val text = node.nodeValue.trim()
                if (text.isNotEmpty()) {
                    objectNode.put("#text", text)
                }
            }
        }
        
        // 处理子元素
        for ((name, elements) in childElements) {
            if (elements.size == 1) {
                // 单个元素
                objectNode.set<JsonNode>(name, elementToJson(elements[0]))
            } else {
                // 多个元素
                val arrayNode = objectMapper.createArrayNode()
                for (element in elements) {
                    arrayNode.add(elementToJson(element))
                }
                objectNode.set<JsonNode>(name, arrayNode)
            }
        }
        
        // 如果只有文本内容，则简化为文本
        if (objectNode.size() == 1 && objectNode.has("#text")) {
            return objectMapper.valueToTree(objectNode.get("#text").asText())
        }
        
        return objectNode
    }
    
    /**
     * JSON转CSV
     */
    private fun jsonToCsv(arrayNode: ArrayNode, request: TransformationRequest): TransformationResult {
        try {
            // 获取CSV选项
            val delimiter = request.options["delimiter"] as? String ?: ","
            val quoteChar = request.options["quoteChar"] as? String ?: "\""
            val header = request.options["header"] as? Boolean ?: true
            
            // 创建CSV格式
            val csvFormat = CSVFormat.DEFAULT
                .withDelimiter(delimiter[0])
                .withQuote(quoteChar[0])
                .withHeader(*getHeaders(arrayNode).toTypedArray())
                .withSkipHeaderRecord(!header)
            
            // 创建CSV打印器
            val writer = StringWriter()
            val csvPrinter = CSVPrinter(writer, csvFormat)
            
            // 写入数据
            for (i in 0 until arrayNode.size()) {
                val row = arrayNode[i]
                if (row.isObject()) {
                    val values = mutableListOf<Any?>()
                    for (header in getHeaders(arrayNode)) {
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
    
    /**
     * 文档转字符串
     */
    private fun documentToString(document: Document, pretty: Boolean = false): String {
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        
        // 设置输出属性
        if (pretty) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        }
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        
        val source = DOMSource(document)
        val writer = StringWriter()
        val result = StreamResult(writer)
        transformer.transform(source, result)
        
        return writer.toString()
    }
}
