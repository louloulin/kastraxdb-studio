package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.DocumentGenerator
import ai.magicdb.data.service.api.model.ServiceDocument
import ai.magicdb.data.service.web.util.DataResultExtensions
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 文档控制器
 */
@RestController
@RequestMapping("/api/data-service")
class DocumentController(
    private val dataServiceManager: DataServiceManager,
    private val documentGenerator: DocumentGenerator
) {
    
    /**
     * 获取服务文档
     */
    @GetMapping("/{id}/doc")
    fun getServiceDoc(@PathVariable id: String): DataResult<ServiceDocument> {
        val service = dataServiceManager.getService(id)
            ?: return DataResultExtensions.failed("Service not found")
        
        val document = documentGenerator.generateDocument(service)
        return DataResult.of(document)
    }
    
    /**
     * 获取服务 OpenAPI 规范
     */
    @GetMapping("/{id}/swagger")
    fun getServiceSwagger(@PathVariable id: String): DataResult<String> {
        val service = dataServiceManager.getService(id)
            ?: return DataResultExtensions.failed("Service not found")
        
        val openApiSpec = documentGenerator.generateOpenApiSpec(service)
        return DataResult.of(openApiSpec)
    }
    
    /**
     * 导出服务文档
     */
    @GetMapping("/{id}/doc/export")
    fun exportServiceDoc(
        @PathVariable id: String,
        @RequestParam format: String
    ): ResponseEntity<ByteArray> {
        val service = dataServiceManager.getService(id)
            ?: throw IllegalArgumentException("Service not found")
        
        val fileName = URLEncoder.encode("${service.name}_doc", StandardCharsets.UTF_8)
        
        return when (format.lowercase()) {
            "markdown", "md" -> {
                val markdown = documentGenerator.generateMarkdownDoc(service)
                ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName.md")
                    .contentType(MediaType.parseMediaType("text/markdown"))
                    .body(markdown.toByteArray(StandardCharsets.UTF_8))
            }
            "html" -> {
                val html = documentGenerator.generateHtmlDoc(service)
                ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName.html")
                    .contentType(MediaType.TEXT_HTML)
                    .body(html.toByteArray(StandardCharsets.UTF_8))
            }
            "pdf" -> {
                val pdf = documentGenerator.generatePdfDoc(service)
                ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf)
            }
            else -> throw IllegalArgumentException("Unsupported format: $format")
        }
    }
    
    /**
     * 获取所有服务的 OpenAPI 规范
     */
    @GetMapping("/swagger")
    fun getAllServicesSwagger(): ResponseEntity<String> {
        val services = dataServiceManager.getAllServices()
        
        // 合并所有服务的 OpenAPI 规范
        // 这里简化处理，实际应用中需要更复杂的合并逻辑
        val firstService = services.firstOrNull()
            ?: return ResponseEntity.ok("{}")
        
        val openApiSpec = documentGenerator.generateOpenApiSpec(firstService)
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(openApiSpec)
    }
}
