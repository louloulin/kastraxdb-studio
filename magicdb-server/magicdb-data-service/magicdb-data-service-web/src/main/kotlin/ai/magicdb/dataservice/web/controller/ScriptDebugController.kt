package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.ScriptDebugger
import ai.magicdb.dataservice.api.model.ScriptDebugRequest
import ai.magicdb.dataservice.api.model.ScriptDebugResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import org.springframework.web.bind.annotation.*

/**
 * 脚本调试控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/script")
class ScriptDebugController(private val scriptDebugger: ScriptDebugger) {
    
    /**
     * 调试脚本
     *
     * @param request 调试请求
     * @return 调试结果
     */
    @PostMapping("/debug")
    fun debug(@RequestBody request: ScriptDebugRequest): DataResult<ScriptDebugResult> {
        val result = scriptDebugger.debug(request)
        return DataResult.of(result)
    }
    
    /**
     * 获取脚本语言列表
     *
     * @return 脚本语言列表
     */
    @GetMapping("/languages")
    fun getLanguages(): ListResult<String> {
        val languages = scriptDebugger.getLanguages()
        return ListResult.of(languages)
    }
    
    /**
     * 获取脚本模板
     *
     * @param language 脚本语言
     * @return 脚本模板
     */
    @GetMapping("/template")
    fun getTemplate(@RequestParam language: String): DataResult<String> {
        val template = scriptDebugger.getTemplate(language)
        return DataResult.of(template)
    }
}
