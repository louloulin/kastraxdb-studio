package ai.magicdb.script.api

import java.io.Serializable

/**
 * 脚本调试器接口
 *
 * @author magicdb
 */
interface ScriptDebugger {
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

/**
 * 断点信息
 */
data class Breakpoint(
    /**
     * 断点ID
     */
    val id: String,
    
    /**
     * 行号
     */
    val lineNumber: Int,
    
    /**
     * 条件表达式
     */
    val condition: String?,
    
    /**
     * 是否启用
     */
    val enabled: Boolean
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 调试状态
 */
data class DebugStatus(
    /**
     * 会话ID
     */
    val sessionId: String,
    
    /**
     * 当前行号
     */
    val currentLine: Int,
    
    /**
     * 当前文件
     */
    val currentFile: String,
    
    /**
     * 状态
     */
    val state: DebugState,
    
    /**
     * 错误信息
     */
    val error: String?
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 调试状态枚举
 */
enum class DebugState {
    /**
     * 未开始
     */
    NOT_STARTED,
    
    /**
     * 运行中
     */
    RUNNING,
    
    /**
     * 已暂停
     */
    PAUSED,
    
    /**
     * 已完成
     */
    COMPLETED,
    
    /**
     * 出错
     */
    ERROR
}

/**
 * 调用栈帧
 */
data class StackFrame(
    /**
     * 函数名
     */
    val functionName: String,
    
    /**
     * 文件名
     */
    val fileName: String,
    
    /**
     * 行号
     */
    val lineNumber: Int,
    
    /**
     * 局部变量
     */
    val localVariables: Map<String, Any?>
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
