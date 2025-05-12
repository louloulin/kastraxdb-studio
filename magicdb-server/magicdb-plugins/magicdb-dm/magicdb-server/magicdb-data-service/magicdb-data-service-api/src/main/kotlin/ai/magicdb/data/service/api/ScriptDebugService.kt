package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.BreakpointInfo
import ai.magicdb.data.service.api.model.DebugSession
import ai.magicdb.data.service.api.model.DebugStepResult
import ai.magicdb.data.service.api.model.VariableInfo

/**
 * 脚本调试服务接口
 * 提供脚本调试相关功能
 */
interface ScriptDebugService {
    /**
     * 创建调试会话
     *
     * @param serviceId 服务 ID
     * @param parameters 参数
     * @return 调试会话
     */
    fun createDebugSession(serviceId: String, parameters: Map<String, Any?>): DebugSession
    
    /**
     * 获取调试会话
     *
     * @param sessionId 会话 ID
     * @return 调试会话
     */
    fun getDebugSession(sessionId: String): DebugSession?
    
    /**
     * 结束调试会话
     *
     * @param sessionId 会话 ID
     * @return 是否成功
     */
    fun terminateDebugSession(sessionId: String): Boolean
    
    /**
     * 添加断点
     *
     * @param sessionId 会话 ID
     * @param breakpoint 断点信息
     * @return 是否成功
     */
    fun addBreakpoint(sessionId: String, breakpoint: BreakpointInfo): Boolean
    
    /**
     * 删除断点
     *
     * @param sessionId 会话 ID
     * @param breakpointId 断点 ID
     * @return 是否成功
     */
    fun removeBreakpoint(sessionId: String, breakpointId: String): Boolean
    
    /**
     * 获取所有断点
     *
     * @param sessionId 会话 ID
     * @return 断点列表
     */
    fun getBreakpoints(sessionId: String): List<BreakpointInfo>
    
    /**
     * 单步执行
     *
     * @param sessionId 会话 ID
     * @return 执行结果
     */
    fun stepOver(sessionId: String): DebugStepResult
    
    /**
     * 单步进入
     *
     * @param sessionId 会话 ID
     * @return 执行结果
     */
    fun stepInto(sessionId: String): DebugStepResult
    
    /**
     * 单步跳出
     *
     * @param sessionId 会话 ID
     * @return 执行结果
     */
    fun stepOut(sessionId: String): DebugStepResult
    
    /**
     * 继续执行
     *
     * @param sessionId 会话 ID
     * @return 执行结果
     */
    fun continue(sessionId: String): DebugStepResult
    
    /**
     * 获取变量
     *
     * @param sessionId 会话 ID
     * @return 变量列表
     */
    fun getVariables(sessionId: String): List<VariableInfo>
    
    /**
     * 获取调用栈
     *
     * @param sessionId 会话 ID
     * @return 调用栈
     */
    fun getCallStack(sessionId: String): List<Map<String, Any?>>
    
    /**
     * 执行表达式
     *
     * @param sessionId 会话 ID
     * @param expression 表达式
     * @return 执行结果
     */
    fun evaluateExpression(sessionId: String, expression: String): Any?
}
