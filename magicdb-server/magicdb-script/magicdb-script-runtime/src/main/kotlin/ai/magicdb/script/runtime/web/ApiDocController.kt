package ai.magicdb.script.runtime.web

import ai.magicdb.script.api.ApiService
import ai.magicdb.script.runtime.doc.ApiDocGenerator
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * API文档控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/doc")
class ApiDocController(private val apiService: ApiService) {
    private val apiDocGenerator = ApiDocGenerator(apiService)

    /**
     * 获取Markdown文档
     */
    @GetMapping(produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getMarkdownDoc(): ResponseEntity<String> {
        return ResponseEntity.ok(apiDocGenerator.generateMarkdownDoc())
    }

    /**
     * 获取OpenAPI文档
     */
    @GetMapping(path = ["/openapi"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getOpenApiDoc(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(apiDocGenerator.generateOpenApiDoc())
    }
}
