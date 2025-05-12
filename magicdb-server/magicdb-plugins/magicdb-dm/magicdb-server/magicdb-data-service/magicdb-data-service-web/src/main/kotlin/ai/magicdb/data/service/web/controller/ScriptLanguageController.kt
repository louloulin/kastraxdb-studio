package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.ScriptLanguageService
import ai.magicdb.data.service.api.model.CompletionItem
import ai.magicdb.data.service.api.model.DiagnosticItem
import ai.magicdb.data.service.api.model.HoverInfo
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 脚本语言控制器
 */
@RestController
@RequestMapping("/api/script-language")
class ScriptLanguageController(
    private val scriptLanguageService: ScriptLanguageService
) {
    
    /**
     * 获取代码补全项
     */
    @PostMapping("/completion")
    fun getCompletionItems(
        @RequestParam language: String,
        @RequestBody request: CompletionRequest
    ): DataResult<List<CompletionItem>> {
        val completionItems = scriptLanguageService.getCompletionItems(
            language = language,
            script = request.script,
            position = request.position
        )
        return DataResult.of(completionItems)
    }
    
    /**
     * 获取诊断信息
     */
    @PostMapping("/diagnostics")
    fun getDiagnostics(
        @RequestParam language: String,
        @RequestBody request: DiagnosticsRequest
    ): DataResult<List<DiagnosticItem>> {
        val diagnostics = scriptLanguageService.getDiagnostics(
            language = language,
            script = request.script
        )
        return DataResult.of(diagnostics)
    }
    
    /**
     * 获取悬停信息
     */
    @PostMapping("/hover")
    fun getHoverInfo(
        @RequestParam language: String,
        @RequestBody request: HoverRequest
    ): DataResult<HoverInfo?> {
        val hoverInfo = scriptLanguageService.getHoverInfo(
            language = language,
            script = request.script,
            position = request.position
        )
        return DataResult.of(hoverInfo)
    }
    
    /**
     * 格式化代码
     */
    @PostMapping("/format")
    fun formatCode(
        @RequestParam language: String,
        @RequestBody request: FormatRequest
    ): DataResult<String> {
        val formattedCode = scriptLanguageService.formatCode(
            language = language,
            script = request.script
        )
        return DataResult.of(formattedCode)
    }
    
    /**
     * 获取语言支持的功能
     */
    @GetMapping("/capabilities")
    fun getLanguageCapabilities(
        @RequestParam language: String
    ): DataResult<Map<String, Boolean>> {
        val capabilities = scriptLanguageService.getLanguageCapabilities(language)
        return DataResult.of(capabilities)
    }
    
    /**
     * 补全请求
     */
    data class CompletionRequest(
        val script: String,
        val position: Int
    )
    
    /**
     * 诊断请求
     */
    data class DiagnosticsRequest(
        val script: String
    )
    
    /**
     * 悬停请求
     */
    data class HoverRequest(
        val script: String,
        val position: Int
    )
    
    /**
     * 格式化请求
     */
    data class FormatRequest(
        val script: String
    )
}
