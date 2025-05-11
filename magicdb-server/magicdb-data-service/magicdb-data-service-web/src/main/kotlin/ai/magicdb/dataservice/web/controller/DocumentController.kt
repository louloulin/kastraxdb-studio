package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.DocumentGenerator
import ai.magicdb.dataservice.api.model.DocumentTemplate
import ai.magicdb.dataservice.api.model.ServiceDocument
import ai.magicdb.dataservice.core.document.DocumentExporter
import ai.magicdb.dataservice.core.document.ExampleGenerator
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 文档控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/document")
class DocumentController(
    private val documentGenerator: DocumentGenerator,
    private val documentExporter: DocumentExporter,
    private val exampleGenerator: ExampleGenerator
) {

    /**
     * 生成服务文档
     *
     * @param serviceId 服务ID
     * @param templateId 模板ID
     * @return 文档
     */
    @PostMapping("/service/{serviceId}")
    fun generateServiceDocument(
        @PathVariable serviceId: String,
        @RequestParam(required = false) templateId: String?
    ): DataResult<ServiceDocument> {
        val document = documentGenerator.generateServiceDocument(serviceId, templateId)
        return DataResult.of(document)
    }

    /**
     * 生成分组文档
     *
     * @param groupId 分组ID
     * @param templateId 模板ID
     * @return 文档
     */
    @PostMapping("/group/{groupId}")
    fun generateGroupDocument(
        @PathVariable groupId: String,
        @RequestParam(required = false) templateId: String?
    ): DataResult<ServiceDocument> {
        val document = documentGenerator.generateGroupDocument(groupId, templateId)
        return DataResult.of(document)
    }

    /**
     * 生成API文档
     *
     * @param templateId 模板ID
     * @return 文档
     */
    @PostMapping("/api")
    fun generateApiDocument(
        @RequestParam(required = false) templateId: String?
    ): DataResult<ServiceDocument> {
        val document = documentGenerator.generateApiDocument(templateId)
        return DataResult.of(document)
    }

    /**
     * 获取文档
     *
     * @param documentId 文档ID
     * @return 文档
     */
    @GetMapping("/{documentId}")
    fun getDocument(@PathVariable documentId: String): DataResult<ServiceDocument> {
        val document = documentGenerator.getDocument(documentId)
        return if (document != null) {
            DataResult.of(document)
        } else {
            DataResult.empty()
        }
    }

    /**
     * 获取服务文档
     *
     * @param serviceId 服务ID
     * @return 文档
     */
    @GetMapping("/service/{serviceId}")
    fun getServiceDocument(@PathVariable serviceId: String): DataResult<ServiceDocument> {
        val document = documentGenerator.getServiceDocument(serviceId)
        return if (document != null) {
            DataResult.of(document)
        } else {
            DataResult.empty()
        }
    }

    /**
     * 获取分组文档
     *
     * @param groupId 分组ID
     * @return 文档
     */
    @GetMapping("/group/{groupId}")
    fun getGroupDocument(@PathVariable groupId: String): DataResult<ServiceDocument> {
        val document = documentGenerator.getGroupDocument(groupId)
        return if (document != null) {
            DataResult.of(document)
        } else {
            DataResult.empty()
        }
    }

    /**
     * 获取API文档
     *
     * @return 文档
     */
    @GetMapping("/api")
    fun getApiDocument(): DataResult<ServiceDocument> {
        val document = documentGenerator.getApiDocument()
        return if (document != null) {
            DataResult.of(document)
        } else {
            DataResult.empty()
        }
    }

    /**
     * 获取所有文档
     *
     * @return 文档列表
     */
    @GetMapping("/list")
    fun getAllDocuments(): ListResult<ServiceDocument> {
        val documents = documentGenerator.getAllDocuments()
        return ListResult.of(documents)
    }

    /**
     * 保存文档
     *
     * @param document 文档
     * @return 文档ID
     */
    @PostMapping
    fun saveDocument(@RequestBody document: ServiceDocument): DataResult<String> {
        val documentId = documentGenerator.saveDocument(document)
        return DataResult.of(documentId)
    }

    /**
     * 删除文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    @DeleteMapping("/{documentId}")
    fun deleteDocument(@PathVariable documentId: String): ActionResult {
        val success = documentGenerator.deleteDocument(documentId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "删除文档失败", "")
        }
    }

    /**
     * 获取模板
     *
     * @param templateId 模板ID
     * @return 模板
     */
    @GetMapping("/template/{templateId}")
    fun getTemplate(@PathVariable templateId: String): DataResult<DocumentTemplate> {
        val template = documentGenerator.getTemplate(templateId)
        return if (template != null) {
            DataResult.of(template)
        } else {
            DataResult.empty()
        }
    }

    /**
     * 获取所有模板
     *
     * @param type 模板类型
     * @return 模板列表
     */
    @GetMapping("/template/list")
    fun getAllTemplates(@RequestParam(required = false) type: String?): ListResult<DocumentTemplate> {
        val templates = documentGenerator.getAllTemplates(type)
        return ListResult.of(templates)
    }

    /**
     * 获取默认模板
     *
     * @param type 模板类型
     * @return 默认模板
     */
    @GetMapping("/template/default/{type}")
    fun getDefaultTemplate(@PathVariable type: String): DataResult<DocumentTemplate> {
        val template = documentGenerator.getDefaultTemplate(type)
        return DataResult.of(template)
    }

    /**
     * 保存模板
     *
     * @param template 模板
     * @return 模板ID
     */
    @PostMapping("/template")
    fun saveTemplate(@RequestBody template: DocumentTemplate): DataResult<String> {
        val templateId = documentGenerator.saveTemplate(template)
        return DataResult.of(templateId)
    }

    /**
     * 删除模板
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    @DeleteMapping("/template/{templateId}")
    fun deleteTemplate(@PathVariable templateId: String): ActionResult {
        val success = documentGenerator.deleteTemplate(templateId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "删除模板失败", "")
        }
    }

    /**
     * 导出文档为PDF
     *
     * @param documentId 文档ID
     * @return PDF文件
     */
    @GetMapping("/{documentId}/export/pdf", produces = [MediaType.APPLICATION_PDF_VALUE])
    fun exportDocumentToPdf(@PathVariable documentId: String): ResponseEntity<ByteArray> {
        val document = documentGenerator.getDocument(documentId)
            ?: throw IllegalArgumentException("文档不存在: $documentId")

        val outputStream = java.io.ByteArrayOutputStream()
        documentExporter.export(document, DocumentExporter.ExportFormat.PDF, outputStream)

        val fileName = URLEncoder.encode("${document.title}.pdf", StandardCharsets.UTF_8.toString())

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
            .body(outputStream.toByteArray())
    }

    /**
     * 导出文档为HTML
     *
     * @param documentId 文档ID
     * @return HTML文件
     */
    @GetMapping("/{documentId}/export/html", produces = [MediaType.TEXT_HTML_VALUE])
    fun exportDocumentToHtml(@PathVariable documentId: String): ResponseEntity<ByteArray> {
        val document = documentGenerator.getDocument(documentId)
            ?: throw IllegalArgumentException("文档不存在: $documentId")

        val outputStream = java.io.ByteArrayOutputStream()
        documentExporter.export(document, DocumentExporter.ExportFormat.HTML, outputStream)

        val fileName = URLEncoder.encode("${document.title}.html", StandardCharsets.UTF_8.toString())

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
            .body(outputStream.toByteArray())
    }

    /**
     * 导出文档为Word
     *
     * @param documentId 文档ID
     * @return Word文件
     */
    @GetMapping("/{documentId}/export/word", produces = ["application/vnd.openxmlformats-officedocument.wordprocessingml.document"])
    fun exportDocumentToWord(@PathVariable documentId: String): ResponseEntity<ByteArray> {
        val document = documentGenerator.getDocument(documentId)
            ?: throw IllegalArgumentException("文档不存在: $documentId")

        val outputStream = java.io.ByteArrayOutputStream()
        documentExporter.export(document, DocumentExporter.ExportFormat.WORD, outputStream)

        val fileName = URLEncoder.encode("${document.title}.docx", StandardCharsets.UTF_8.toString())

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
            .body(outputStream.toByteArray())
    }

    /**
     * 获取示例文档
     *
     * @return 示例文档
     */
    @GetMapping("/example")
    fun getExampleDocument(): DataResult<ServiceDocument> {
        val document = exampleGenerator.generateExampleDocument()
        return DataResult.of(document)
    }

    /**
     * 获取示例服务
     *
     * @return 示例服务
     */
    @GetMapping("/example/service")
    fun getExampleService(): DataResult<ai.magicdb.dataservice.api.model.DataService> {
        val service = exampleGenerator.generateExampleService()
        return DataResult.of(service)
    }

    /**
     * 获取示例分组
     *
     * @return 示例分组
     */
    @GetMapping("/example/group")
    fun getExampleGroup(): DataResult<ai.magicdb.dataservice.api.model.ServiceGroup> {
        val group = exampleGenerator.generateExampleGroup()
        return DataResult.of(group)
    }
}
