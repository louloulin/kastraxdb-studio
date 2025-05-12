package ai.magicdb.dataservice.core.document

import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.DocumentGenerator
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceDocument
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * 文档导出器
 * 用于导出服务文档为不同格式
 *
 * @author magicdb
 */
@Component
class DocumentExporter(
    private val dataServiceRepository: DataServiceRepository,
    private val documentGenerator: DocumentGenerator
) {
    private val logger = LoggerFactory.getLogger(DocumentExporter::class.java)

    /**
     * 导出为Markdown
     *
     * @param serviceId 服务ID
     * @param document 服务文档
     * @return Markdown内容
     */
    fun exportToMarkdown(serviceId: String, document: ServiceDocument): String {
        val service = dataServiceRepository.getService(serviceId)
        if (service == null) {
            logger.warn("服务不存在: {}", serviceId)
            return "# 服务不存在\n\n服务ID: $serviceId"
        }

        val markdown = StringBuilder()

        // 标题
        markdown.append("# ${document.title}\n\n")

        // 描述
        if (!document.description.isNullOrBlank()) {
            markdown.append("${document.description}\n\n")
        } else if (!service.description.isNullOrBlank()) {
            markdown.append("${service.description}\n\n")
        }

        // 基本信息
        markdown.append("## 基本信息\n\n")
        markdown.append("- **服务ID**: ${service.id}\n")
        markdown.append("- **服务名称**: ${service.name}\n")
        markdown.append("- **创建时间**: ${service.createTime}\n")
        markdown.append("- **更新时间**: ${service.updateTime}\n")
        if (!service.tags.isNullOrEmpty()) {
            markdown.append("- **标签**: ${service.tags.joinToString(", ")}\n")
        }
        markdown.append("\n")

        // 参数
        if (document.parameters.isNotEmpty()) {
            markdown.append("## 参数\n\n")
            markdown.append("| 参数名 | 类型 | 必填 | 默认值 | 描述 |\n")
            markdown.append("|-------|------|------|--------|------|\n")

            document.parameters.forEach { param ->
                val required = if (param.required) "是" else "否"
                val defaultValue = param.defaultValue ?: "-"
                markdown.append("| ${param.name} | ${param.type} | $required | $defaultValue | ${param.description ?: "-"} |\n")
            }

            markdown.append("\n")
        }

        // 返回值
        if (document.returnFields.isNotEmpty()) {
            markdown.append("## 返回值\n\n")
            markdown.append("| 字段名 | 类型 | 描述 |\n")
            markdown.append("|-------|------|------|\n")

            document.returnFields.forEach { field ->
                markdown.append("| ${field.name} | ${field.type} | ${field.description ?: "-"} |\n")
            }

            markdown.append("\n")
        }

        // 示例
        if (document.examples.isNotEmpty()) {
            markdown.append("## 示例\n\n")

            document.examples.forEachIndexed { index, example ->
                markdown.append("### 示例 ${index + 1}\n\n")

                if (!example.description.isNullOrBlank()) {
                    markdown.append("${example.description}\n\n")
                }

                markdown.append("**请求参数**:\n\n")
                markdown.append("```json\n")
                markdown.append(example.requestParams ?: "{}")
                markdown.append("\n```\n\n")

                markdown.append("**返回结果**:\n\n")
                markdown.append("```json\n")
                markdown.append(example.responseData ?: "{}")
                markdown.append("\n```\n\n")
            }
        }

        // 注意事项
        if (!document.notes.isNullOrBlank()) {
            markdown.append("## 注意事项\n\n")
            markdown.append("${document.notes}\n\n")
        }

        return markdown.toString()
    }

    /**
     * 导出为HTML
     *
     * @param serviceId 服务ID
     * @param document 服务文档
     * @return HTML内容
     */
    fun exportToHtml(serviceId: String, document: ServiceDocument): String {
        val markdown = exportToMarkdown(serviceId, document)

        // 简单的Markdown转HTML
        val html = StringBuilder()
        html.append("<!DOCTYPE html>\n")
        html.append("<html>\n")
        html.append("<head>\n")
        html.append("  <meta charset=\"UTF-8\">\n")
        html.append("  <title>${document.title}</title>\n")
        html.append("  <style>\n")
        html.append("    body { font-family: Arial, sans-serif; line-height: 1.6; max-width: 800px; margin: 0 auto; padding: 20px; }\n")
        html.append("    h1, h2, h3 { color: #333; }\n")
        html.append("    table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }\n")
        html.append("    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n")
        html.append("    th { background-color: #f2f2f2; }\n")
        html.append("    pre { background-color: #f5f5f5; padding: 10px; border-radius: 5px; overflow-x: auto; }\n")
        html.append("    code { font-family: Consolas, monospace; }\n")
        html.append("  </style>\n")
        html.append("</head>\n")
        html.append("<body>\n")

        // 转换Markdown为HTML
        val lines = markdown.split("\n")
        var inCodeBlock = false
        var inTable = false

        for (line in lines) {
            when {
                line.startsWith("# ") -> {
                    html.append("<h1>${line.substring(2)}</h1>\n")
                }
                line.startsWith("## ") -> {
                    html.append("<h2>${line.substring(3)}</h2>\n")
                }
                line.startsWith("### ") -> {
                    html.append("<h3>${line.substring(4)}</h3>\n")
                }
                line.startsWith("- ") -> {
                    html.append("<ul><li>${line.substring(2)}</li></ul>\n")
                }
                line.startsWith("```") -> {
                    if (inCodeBlock) {
                        html.append("</code></pre>\n")
                        inCodeBlock = false
                    } else {
                        html.append("<pre><code>")
                        inCodeBlock = true
                    }
                }
                line.startsWith("|") -> {
                    if (!inTable) {
                        html.append("<table>\n")
                        inTable = true
                    }

                    val cells = line.split("|").filter { it.isNotBlank() }
                    val isHeader = line.contains("---")

                    if (!isHeader) {
                        html.append("  <tr>\n")
                        cells.forEach { cell ->
                            if (inTable && line == cells[0]) {
                                html.append("    <th>${cell.trim()}</th>\n")
                            } else {
                                html.append("    <td>${cell.trim()}</td>\n")
                            }
                        }
                        html.append("  </tr>\n")
                    }

                    if (inTable && line.isEmpty()) {
                        html.append("</table>\n")
                        inTable = false
                    }
                }
                line.isBlank() -> {
                    if (!inCodeBlock && !inTable) {
                        html.append("<br>\n")
                    }
                }
                else -> {
                    if (inCodeBlock) {
                        html.append("${line}\n")
                    } else {
                        html.append("<p>${line}</p>\n")
                    }
                }
            }
        }

        if (inTable) {
            html.append("</table>\n")
        }

        if (inCodeBlock) {
            html.append("</code></pre>\n")
        }

        html.append("</body>\n")
        html.append("</html>")

        return html.toString()
    }

    /**
     * 导出为PDF
     *
     * @param serviceId 服务ID
     * @param document 服务文档
     * @return PDF内容
     */
    fun exportToPdf(serviceId: String, document: ServiceDocument): ByteArray {
        // 这里应该使用PDF生成库，如iText或PDFBox
        // 由于依赖问题，这里简单返回HTML内容的字节数组
        val html = exportToHtml(serviceId, document)
        return html.toByteArray(StandardCharsets.UTF_8)
    }

    /**
     * 导出为Word
     *
     * @param serviceId 服务ID
     * @param document 服务文档
     * @return Word内容
     */
    fun exportToWord(serviceId: String, document: ServiceDocument): ByteArray {
        // 这里应该使用Word生成库，如Apache POI
        // 由于依赖问题，这里简单返回Markdown内容的字节数组
        val markdown = exportToMarkdown(serviceId, document)
        return markdown.toByteArray(StandardCharsets.UTF_8)
    }

    /**
     * 导出文档
     *
     * @param serviceId 服务ID
     * @param format 导出格式
     * @return 导出内容
     */
    fun export(serviceId: String, format: ExportFormat): ByteArray {
        // 获取文档
        val document = documentGenerator.getServiceDocument(serviceId)
            ?: throw IllegalArgumentException("服务不存在: $serviceId")

        // 根据格式导出
        return when (format) {
            ExportFormat.MARKDOWN -> exportToMarkdown(serviceId, document).toByteArray(StandardCharsets.UTF_8)
            ExportFormat.HTML -> exportToHtml(serviceId, document).toByteArray(StandardCharsets.UTF_8)
            ExportFormat.PDF -> exportToPdf(serviceId, document)
            ExportFormat.WORD -> exportToWord(serviceId, document)
        }
    }

    /**
     * 导出分组文档
     *
     * @param groupId 分组ID
     * @param format 导出格式
     * @return 导出内容
     */
    fun exportGroup(groupId: String, format: ExportFormat): ByteArray {
        // 获取文档
        val document = documentGenerator.getGroupDocument(groupId)
            ?: throw IllegalArgumentException("分组不存在: $groupId")

        // 根据格式导出
        return when (format) {
            ExportFormat.MARKDOWN -> exportToMarkdown("group-$groupId", document).toByteArray(StandardCharsets.UTF_8)
            ExportFormat.HTML -> exportToHtml("group-$groupId", document).toByteArray(StandardCharsets.UTF_8)
            ExportFormat.PDF -> exportToPdf("group-$groupId", document)
            ExportFormat.WORD -> exportToWord("group-$groupId", document)
        }
    }

    /**
     * 导出API文档
     *
     * @param apiId API ID
     * @param format 导出格式
     * @return 导出内容
     */
    fun exportApi(apiId: String, format: ExportFormat): ByteArray {
        // 获取文档
        val document = documentGenerator.getApiDocument(apiId)
            ?: throw IllegalArgumentException("API不存在: $apiId")

        // 根据格式导出
        return when (format) {
            ExportFormat.MARKDOWN -> exportToMarkdown("api-$apiId", document).toByteArray(StandardCharsets.UTF_8)
            ExportFormat.HTML -> exportToHtml("api-$apiId", document).toByteArray(StandardCharsets.UTF_8)
            ExportFormat.PDF -> exportToPdf("api-$apiId", document)
            ExportFormat.WORD -> exportToWord("api-$apiId", document)
        }
    }

    /**
     * 批量导出为ZIP
     *
     * @param serviceIds 服务ID列表
     * @param format 导出格式
     * @return ZIP内容
     */
    fun batchExportToZip(serviceIds: List<String>, format: String): ByteArray {
        val baos = ByteArrayOutputStream()
        val zos = ZipOutputStream(baos)

        serviceIds.forEach { serviceId ->
            val service = dataServiceRepository.getService(serviceId)
            if (service != null) {
                val document = generateDocument(service)
                val content = when (format.lowercase()) {
                    "markdown", "md" -> exportToMarkdown(serviceId, document).toByteArray(StandardCharsets.UTF_8)
                    "html" -> exportToHtml(serviceId, document).toByteArray(StandardCharsets.UTF_8)
                    "pdf" -> exportToPdf(serviceId, document)
                    "word", "docx" -> exportToWord(serviceId, document)
                    else -> exportToMarkdown(serviceId, document).toByteArray(StandardCharsets.UTF_8)
                }

                val extension = when (format.lowercase()) {
                    "markdown" -> "md"
                    "html" -> "html"
                    "pdf" -> "pdf"
                    "word" -> "docx"
                    else -> "md"
                }

                val fileName = "${service.name.replace(" ", "_")}_${serviceId}.$extension"
                val entry = ZipEntry(fileName)
                zos.putNextEntry(entry)
                zos.write(content)
                zos.closeEntry()
            }
        }

        zos.close()
        return baos.toByteArray()
    }

    /**
     * 生成文档
     *
     * @param service 服务
     * @return 服务文档
     */
    private fun generateDocument(service: DataService): ServiceDocument {
        // 这里应该根据服务信息生成文档
        // 简单实现，实际应该更复杂
        return ServiceDocument(
            title = service.name,
            description = service.description,
            parameters = emptyList(),
            returnFields = emptyList(),
            examples = emptyList()
        )
    }
}
