package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.CodeSnippetService
import ai.magicdb.data.service.api.model.CodeSnippet
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 代码片段控制器
 */
@RestController
@RequestMapping("/api/code-snippet")
class CodeSnippetController(
    private val codeSnippetService: CodeSnippetService
) {
    
    /**
     * 获取所有代码片段
     */
    @GetMapping
    fun getAllSnippets(@RequestParam(required = false) language: String?): DataResult<List<CodeSnippet>> {
        val snippets = codeSnippetService.getAllSnippets(language)
        return DataResult.of(snippets)
    }
    
    /**
     * 获取代码片段
     */
    @GetMapping("/{id}")
    fun getSnippet(@PathVariable("id") id: String): DataResult<CodeSnippet?> {
        val snippet = codeSnippetService.getSnippet(id)
        return DataResult.of(snippet)
    }
    
    /**
     * 创建代码片段
     */
    @PostMapping
    fun createSnippet(@RequestBody snippet: CodeSnippet): DataResult<CodeSnippet> {
        val createdSnippet = codeSnippetService.createSnippet(snippet)
        return DataResult.of(createdSnippet)
    }
    
    /**
     * 更新代码片段
     */
    @PutMapping("/{id}")
    fun updateSnippet(
        @PathVariable("id") id: String,
        @RequestBody snippet: CodeSnippet
    ): DataResult<CodeSnippet?> {
        val updatedSnippet = codeSnippetService.updateSnippet(id, snippet)
        return DataResult.of(updatedSnippet)
    }
    
    /**
     * 删除代码片段
     */
    @DeleteMapping("/{id}")
    fun deleteSnippet(@PathVariable("id") id: String): DataResult<Boolean> {
        val result = codeSnippetService.deleteSnippet(id)
        return DataResult.of(result)
    }
    
    /**
     * 搜索代码片段
     */
    @GetMapping("/search")
    fun searchSnippets(
        @RequestParam keyword: String,
        @RequestParam(required = false) language: String?
    ): DataResult<List<CodeSnippet>> {
        val snippets = codeSnippetService.searchSnippets(keyword, language)
        return DataResult.of(snippets)
    }
}
