package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.ScriptDebugRequest
import ai.magicdb.data.service.api.model.ScriptDebugResult

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
    
    /**
     * 创建调试会话
     *
     * @param request 调试请求
     * @return 会话ID
     */
    fun createSession(request: ScriptDebugRequest): String
    
    /**
     * 关闭会话
     *
     * @param sessionId 会话ID
     * @return 是否成功
     */
    fun closeSession(sessionId: String): Boolean
    
    /**
     * 获取会话信息
     *
     * @param sessionId 会话ID
     * @return 会话信息
     */
    fun getSession(sessionId: String): Map<String, Any?>
    
    /**
     * 执行命令
     *
     * @param sessionId 会话ID
     * @param command 命令
     * @return 执行结果
     */
    fun executeCommand(sessionId: String, command: String): ScriptDebugResult
}
