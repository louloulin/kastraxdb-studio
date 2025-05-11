package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.ScriptDebugRequest
import ai.magicdb.dataservice.api.model.ScriptDebugResult
import ai.magicdb.script.api.Breakpoint
import ai.magicdb.script.api.DebugStatus
import ai.magicdb.script.api.StackFrame

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
     * @param script 脚本内容
     * @param language 脚本语言
     * @param context 上下文
     * @return 调试会话ID
     */
    fun createSession(script: String, language: String, context: Map<String, Any?> = emptyMap()): String

    /**
     * 设置断点
     *
     * @param sessionId 会话ID
     * @param lineNumber 行号
     * @param condition 条件表达式，可选
     * @return 断点ID
     */
    fun setBreakpoint(sessionId: String, lineNumber: Int, condition: String? = null): String

    /**
     * 删除断点
     *
     * @param sessionId 会话ID
     * @param breakpointId 断点ID
     * @return 是否成功
     */
    fun removeBreakpoint(sessionId: String, breakpointId: String): Boolean

    /**
     * 获取所有断点
     *
     * @param sessionId 会话ID
     * @return 断点列表
     */
    fun getBreakpoints(sessionId: String): List<Breakpoint>

    /**
     * 开始执行
     *
     * @param sessionId 会话ID
     * @return 执行状态
     */
    fun start(sessionId: String): DebugStatus

    /**
     * 继续执行
     *
     * @param sessionId 会话ID
     * @return 执行状态
     */
    fun continue_(sessionId: String): DebugStatus

    /**
     * 单步执行
     *
     * @param sessionId 会话ID
     * @return 执行状态
     */
    fun stepOver(sessionId: String): DebugStatus

    /**
     * 步入函数
     *
     * @param sessionId 会话ID
     * @return 执行状态
     */
    fun stepInto(sessionId: String): DebugStatus

    /**
     * 步出函数
     *
     * @param sessionId 会话ID
     * @return 执行状态
     */
    fun stepOut(sessionId: String): DebugStatus

    /**
     * 暂停执行
     *
     * @param sessionId 会话ID
     * @return 执行状态
     */
    fun pause(sessionId: String): DebugStatus

    /**
     * 停止执行
     *
     * @param sessionId 会话ID
     * @return 是否成功
     */
    fun stop(sessionId: String): Boolean

    /**
     * 获取变量值
     *
     * @param sessionId 会话ID
     * @param variableName 变量名
     * @return 变量值
     */
    fun getVariable(sessionId: String, variableName: String): Any?

    /**
     * 获取所有变量
     *
     * @param sessionId 会话ID
     * @return 变量映射
     */
    fun getVariables(sessionId: String): Map<String, Any?>

    /**
     * 计算表达式
     *
     * @param sessionId 会话ID
     * @param expression 表达式
     * @return 表达式结果
     */
    fun evaluate(sessionId: String, expression: String): Any?

    /**
     * 获取调用栈
     *
     * @param sessionId 会话ID
     * @return 调用栈
     */
    fun getCallStack(sessionId: String): List<StackFrame>

    /**
     * 获取会话状态
     *
     * @param sessionId 会话ID
     * @return 会话状态
     */
    fun getStatus(sessionId: String): DebugStatus

    /**
     * 关闭会话
     *
     * @param sessionId 会话ID
     * @return 是否成功
     */
    fun closeSession(sessionId: String): Boolean
}
