package ai.magicdb.data.service.core.document

import ai.magicdb.data.service.api.DocumentGenerator
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceDocumentExample
import ai.magicdb.data.service.api.model.FieldDocument
import ai.magicdb.data.service.api.model.ParameterDocument
import ai.magicdb.data.service.api.model.ServiceDocument
import ai.magicdb.data.service.api.model.ServiceParameter
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import org.springframework.stereotype.Component
import org.xhtmlrenderer.pdf.ITextRenderer
import java.io.ByteArrayOutputStream
import java.util.UUID

/**
 * 默认文档生成器实现
 */
@Component
class DefaultDocumentGenerator(
    private val objectMapper: ObjectMapper
) : DocumentGenerator {

    override fun generateDocument(service: DataService): ServiceDocument {
        // 生成参数文档
        val parameters = service.parameters.map { parameter ->
            ParameterDocument(
                name = parameter.name,
                type = parameter.type,
                required = parameter.required,
                defaultValue = parameter.defaultValue,
                description = parameter.description,
                example = generateExampleValue(parameter)
            )
        }

        // 生成返回字段文档（通过分析脚本推断）
        val returnFields = inferReturnFields(service)

        // 生成示例
        val examples = generateExamples(service)

        // 构建服务文档
        return ServiceDocument(
            title = service.name,
            description = service.description,
            serviceId = service.id,
            serviceType = service.type,
            path = "/api/data-service/${service.id}/execute",
            method = "POST",
            parameters = parameters,
            returnFields = returnFields,
            examples = examples,
            notes = extractNotes(service.script),
            tags = service.tags
        )
    }

    override fun generateOpenApiSpec(service: DataService): String {
        // 创建 OpenAPI 对象
        val openAPI = OpenAPI()

        // 设置基本信息
        val info = Info()
            .title(service.name)
            .description(service.description)
            .version("1.0")
        openAPI.info(info)

        // 创建路径
        val paths = Paths()
        val pathItem = PathItem()

        // 创建操作
        val operation = Operation()
            .summary(service.name)
            .description(service.description)
            .addTagsItem(service.type)

        // 添加请求体
        val requestBody = RequestBody()
            .description("请求参数")
            .required(true)

        val content = Content()
        val mediaType = MediaType()
        val schema = Schema<Any>().type("object")

        // 添加参数属性
        for (parameter in service.parameters) {
            val paramSchema = Schema<Any>()
                .type(mapParameterType(parameter.type))
                .description(parameter.description)
                .example(generateExampleValue(parameter))

            if (parameter.required) {
                schema.addRequiredItem(parameter.name)
            }

            schema.addProperty(parameter.name, paramSchema)
        }

        mediaType.schema(schema)
        content.addMediaType("application/json", mediaType)
        requestBody.content(content)
        operation.requestBody(requestBody)

        // 添加响应
        val responses = ApiResponses()
        val response = ApiResponse()
            .description("成功响应")

        val responseContent = Content()
        val responseMediaType = MediaType()
        val responseSchema = Schema<Any>()
            .type("object")
            .addProperty("success", Schema<Boolean>().type("boolean").example(true))
            .addProperty("data", Schema<Any>().type("object").description("返回数据"))
            .addProperty("message", Schema<String>().type("string").description("消息"))

        responseMediaType.schema(responseSchema)
        responseContent.addMediaType("application/json", responseMediaType)
        response.content(responseContent)
        responses.addApiResponse("200", response)
        operation.responses(responses)

        // 设置路径
        pathItem.post(operation)
        paths.addPathItem("/api/data-service/${service.id}/execute", pathItem)
        openAPI.paths(paths)

        // 转换为 JSON
        return objectMapper.writeValueAsString(openAPI)
    }

    override fun generateMarkdownDoc(service: DataService): String {
        val document = generateDocument(service)
        val sb = StringBuilder()

        // 标题
        sb.append("# ${document.title}\n\n")

        // 描述
        if (document.description != null) {
            sb.append("${document.description}\n\n")
        }

        // 基本信息
        sb.append("## 基本信息\n\n")
        sb.append("- **服务ID**: ${document.serviceId}\n")
        sb.append("- **服务类型**: ${document.serviceType}\n")
        sb.append("- **请求路径**: ${document.path}\n")
        sb.append("- **请求方法**: ${document.method}\n")
        if (document.tags.isNotEmpty()) {
            sb.append("- **标签**: ${document.tags.joinToString(", ")}\n")
        }
        sb.append("\n")

        // 请求参数
        sb.append("## 请求参数\n\n")
        if (document.parameters.isEmpty()) {
            sb.append("无参数\n\n")
        } else {
            sb.append("| 参数名 | 类型 | 必填 | 默认值 | 描述 |\n")
            sb.append("|-------|------|------|--------|------|\n")
            document.parameters.forEach { param ->
                sb.append("| ${param.name} | ${param.type} | ${if (param.required) "是" else "否"} | ${param.defaultValue ?: ""} | ${param.description ?: ""} |\n")
            }
            sb.append("\n")
        }

        // 返回字段
        sb.append("## 返回字段\n\n")
        if (document.returnFields.isEmpty()) {
            sb.append("返回字段结构未知\n\n")
        } else {
            sb.append("| 字段名 | 类型 | 描述 |\n")
            sb.append("|-------|------|------|\n")
            document.returnFields.forEach { field ->
                sb.append("| ${field.name} | ${field.type} | ${field.description ?: ""} |\n")
            }
            sb.append("\n")
        }

        // 示例
        sb.append("## 示例\n\n")
        document.examples.forEach { example ->
            sb.append("### ${example.name}\n\n")
            if (example.description != null) {
                sb.append("${example.description}\n\n")
            }

            sb.append("**请求**\n\n")
            sb.append("```json\n${example.request}\n```\n\n")

            sb.append("**响应**\n\n")
            sb.append("```json\n${example.response}\n```\n\n")
        }

        // 注意事项
        if (document.notes != null) {
            sb.append("## 注意事项\n\n")
            sb.append("${document.notes}\n\n")
        }

        return sb.toString()
    }

    override fun generateHtmlDoc(service: DataService): String {
        val markdown = generateMarkdownDoc(service)

        // 使用 flexmark-java 将 Markdown 转换为 HTML
        val parser = org.commonmark.parser.Parser.builder().build()
        val document = parser.parse(markdown)
        val renderer = org.commonmark.renderer.html.HtmlRenderer.builder().build()
        val html = renderer.render(document)

        // 添加 HTML 头和样式
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>${service.name} - API 文档</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 800px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    h1, h2, h3 {
                        margin-top: 24px;
                        margin-bottom: 16px;
                        font-weight: 600;
                        line-height: 1.25;
                    }
                    h1 {
                        padding-bottom: 0.3em;
                        font-size: 2em;
                        border-bottom: 1px solid #eaecef;
                    }
                    h2 {
                        padding-bottom: 0.3em;
                        font-size: 1.5em;
                        border-bottom: 1px solid #eaecef;
                    }
                    h3 {
                        font-size: 1.25em;
                    }
                    table {
                        border-collapse: collapse;
                        width: 100%;
                        margin-bottom: 16px;
                    }
                    table, th, td {
                        border: 1px solid #dfe2e5;
                    }
                    th, td {
                        padding: 6px 13px;
                    }
                    th {
                        background-color: #f6f8fa;
                    }
                    pre {
                        background-color: #f6f8fa;
                        border-radius: 3px;
                        padding: 16px;
                        overflow: auto;
                    }
                    code {
                        font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, monospace;
                        background-color: rgba(27, 31, 35, 0.05);
                        border-radius: 3px;
                        padding: 0.2em 0.4em;
                    }
                    pre code {
                        background-color: transparent;
                        padding: 0;
                    }
                </style>
            </head>
            <body>
                $html
            </body>
            </html>
        """.trimIndent()
    }

    override fun generatePdfDoc(service: DataService): ByteArray {
        val html = generateHtmlDoc(service)

        // 使用 Flying Saucer 将 HTML 转换为 PDF
        val renderer = ITextRenderer()
        renderer.setDocumentFromString(html)
        renderer.layout()

        val outputStream = ByteArrayOutputStream()
        renderer.createPDF(outputStream)

        return outputStream.toByteArray()
    }

    /**
     * 推断返回字段
     */
    private fun inferReturnFields(service: DataService): List<FieldDocument> {
        // 这里需要分析脚本来推断返回字段
        // 简单实现，实际应用中可能需要更复杂的分析

        // 默认返回字段
        val defaultFields = listOf(
            FieldDocument(
                name = "success",
                type = "boolean",
                description = "是否成功",
                example = "true"
            ),
            FieldDocument(
                name = "data",
                type = "object",
                description = "返回数据"
            ),
            FieldDocument(
                name = "message",
                type = "string",
                description = "消息",
                example = "操作成功"
            )
        )

        // 尝试从脚本中推断返回字段
        val script = service.script ?: return defaultFields

        // 简单的返回值推断逻辑
        // 实际应用中可能需要更复杂的分析，如 AST 分析
        val returnFields = mutableListOf<FieldDocument>()

        // 查找 return 语句
        val returnPattern = "return\\s+\\{([^}]+)\\}".toRegex()
        val returnMatch = returnPattern.find(script)

        if (returnMatch != null) {
            val returnContent = returnMatch.groupValues[1]

            // 解析返回对象的字段
            val fieldPattern = "(\\w+)\\s*:\\s*([^,]+)".toRegex()
            val fieldMatches = fieldPattern.findAll(returnContent)

            fieldMatches.forEach { match ->
                val fieldName = match.groupValues[1].trim()
                val fieldValue = match.groupValues[2].trim()

                // 推断字段类型
                val fieldType = inferFieldType(fieldValue)

                returnFields.add(
                    FieldDocument(
                        name = fieldName,
                        type = fieldType,
                        description = "返回字段 $fieldName",
                        example = generateExampleForType(fieldType).toString()
                    )
                )
            }
        }

        return if (returnFields.isEmpty()) defaultFields else returnFields
    }

    /**
     * 推断字段类型
     */
    private fun inferFieldType(fieldValue: String): String {
        return when {
            fieldValue.startsWith("\"") || fieldValue.startsWith("'") -> "string"
            fieldValue == "true" || fieldValue == "false" -> "boolean"
            fieldValue.matches("\\d+".toRegex()) -> "integer"
            fieldValue.matches("\\d+\\.\\d+".toRegex()) -> "number"
            fieldValue.startsWith("[") -> "array"
            fieldValue.startsWith("{") -> "object"
            fieldValue.contains("new Date") -> "date"
            else -> "any"
        }
    }

    /**
     * 生成示例
     */
    private fun generateExamples(service: DataService): List<ServiceDocumentExample> {
        val examples = mutableListOf<ServiceDocumentExample>()

        // 生成默认示例
        val requestParams = mutableMapOf<String, Any?>()
        service.parameters.forEach { param ->
            requestParams[param.name] = generateExampleValue(param)
        }

        val requestJson = objectMapper.writeValueAsString(requestParams)

        // 生成默认响应
        val responseData = mutableMapOf<String, Any?>()
        val returnFields = inferReturnFields(service)
        returnFields.forEach { field ->
            if (field.name == "data") {
                // 为 data 字段生成示例数据
                val dataFields = mutableMapOf<String, Any?>()
                field.fields.forEach { subField ->
                    dataFields[subField.name] = generateExampleForType(subField.type)
                }
                responseData["data"] = if (dataFields.isEmpty()) {
                    mapOf("result" to "示例结果")
                } else {
                    dataFields
                }
            } else {
                responseData[field.name] = generateExampleForType(field.type)
            }
        }

        val responseJson = objectMapper.writeValueAsString(responseData)

        examples.add(
            ServiceDocumentExample(
                name = "基本示例",
                request = requestJson,
                response = responseJson,
                description = "基本调用示例"
            )
        )

        return examples
    }

    /**
     * 为参数生成示例值
     */
    private fun generateExampleValue(parameter: ServiceParameter): String {
        // 如果有默认值，使用默认值
        val defaultValue = parameter.defaultValue
        if (defaultValue != null) {
            return defaultValue
        }

        // 根据类型生成示例值
        return when (parameter.type.lowercase()) {
            "string" -> "示例字符串"
            "number", "integer" -> "123"
            "boolean" -> "true"
            "array" -> "[]"
            "object" -> "{}"
            "date" -> "2023-01-01"
            else -> "示例值"
        }
    }

    /**
     * 为类型生成示例值
     */
    private fun generateExampleForType(type: String): Any {
        return when (type.lowercase()) {
            "string" -> "示例字符串"
            "number" -> 123.45
            "integer" -> 123
            "boolean" -> true
            "array" -> listOf("示例项1", "示例项2")
            "object" -> mapOf("key" to "value")
            "date" -> "2023-01-01"
            else -> "示例值"
        }
    }

    /**
     * 从脚本中提取注释作为注意事项
     */
    private fun extractNotes(script: String?): String? {
        if (script == null) return null

        // 提取多行注释
        val multiLineCommentPattern = "/\\*\\*(.*?)\\*/".toRegex(RegexOption.DOT_MATCHES_ALL)
        val multiLineComments = multiLineCommentPattern.findAll(script)
            .map { it.groupValues[1].trim() }
            .filter { it.isNotEmpty() }
            .toList()

        // 提取单行注释
        val singleLineCommentPattern = "// (.*)".toRegex()
        val singleLineComments = singleLineCommentPattern.findAll(script)
            .map { it.groupValues[1].trim() }
            .filter { it.isNotEmpty() }
            .toList()

        val allComments = multiLineComments + singleLineComments

        return if (allComments.isEmpty()) null else allComments.joinToString("\n\n")
    }

    /**
     * 映射参数类型到 OpenAPI 类型
     */
    private fun mapParameterType(type: String): String {
        return when (type.lowercase()) {
            "string" -> "string"
            "number" -> "number"
            "integer" -> "integer"
            "boolean" -> "boolean"
            "array" -> "array"
            "object" -> "object"
            else -> "string"
        }
    }
}
