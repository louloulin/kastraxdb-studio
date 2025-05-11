package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.ScriptDebugRequest
import ai.magicdb.dataservice.api.model.ScriptDebugResult

/**
 * 脚本调试器接口
 *
 * @author magicdb
 */
interface ScriptDebugger {
    
    /**
     * 调试脚本
     *
     * @param request 调试请求
     * @return 调试结果
     */
    fun debug(request: ScriptDebugRequest): ScriptDebugResult
    
    /**
     * 获取脚本语言列表
     *
     * @return 脚本语言列表
     */
    fun getLanguages(): List<String>
    
    /**
     * 获取脚本模板
     *
     * @param language 脚本语言
     * @return 脚本模板
     */
    fun getTemplate(language: String): String
}
