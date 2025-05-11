package ai.magicdb.script.engine.debug

import ai.magicdb.script.api.Breakpoint
import ai.magicdb.script.api.DebugState
import ai.magicdb.script.api.DebugStatus
import ai.magicdb.script.api.ScriptDebugger
import ai.magicdb.script.api.StackFrame
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 默认脚本调试器实现
 *
 * @author magicdb
 */
class DefaultScriptDebugger : ScriptDebugger {
    private val logger = LoggerFactory.getLogger(DefaultScriptDebugger::class.java)
    
    // 调试会话管理
    private val sessions = ConcurrentHashMap<String, DebugSession>()
    
    override fun createSession(script: String, language: String, context: Map<String, Any?>): String {
        val sessionId = UUID.randomUUID().toString()
        val session = DebugSession(sessionId, script, language, context)
        sessions[sessionId] = session
        logger.info("创建调试会话: {}", sessionId)
        return sessionId
    }
    
    override fun setBreakpoint(sessionId: String, lineNumber: Int, condition: String?): String {
        val session = getSession(sessionId)
        val breakpointId = session.setBreakpoint(lineNumber, condition)
        logger.info("设置断点: 会话={}, 行号={}, 条件={}", sessionId, lineNumber, condition)
        return breakpointId
    }
    
    override fun removeBreakpoint(sessionId: String, breakpointId: String): Boolean {
        val session = getSession(sessionId)
        val result = session.removeBreakpoint(breakpointId)
        logger.info("删除断点: 会话={}, 断点={}, 结果={}", sessionId, breakpointId, result)
        return result
    }
    
    override fun getBreakpoints(sessionId: String): List<Breakpoint> {
        val session = getSession(sessionId)
        return session.getBreakpoints()
    }
    
    override fun start(sessionId: String): DebugStatus {
        val session = getSession(sessionId)
        val status = session.start()
        logger.info("开始执行: 会话={}, 状态={}", sessionId, status.state)
        return status
    }
    
    override fun continue_(sessionId: String): DebugStatus {
        val session = getSession(sessionId)
        val status = session.continue_()
        logger.info("继续执行: 会话={}, 状态={}", sessionId, status.state)
        return status
    }
    
    override fun stepOver(sessionId: String): DebugStatus {
        val session = getSession(sessionId)
        val status = session.stepOver()
        logger.info("单步执行: 会话={}, 状态={}", sessionId, status.state)
        return status
    }
    
    override fun stepInto(sessionId: String): DebugStatus {
        val session = getSession(sessionId)
        val status = session.stepInto()
        logger.info("步入函数: 会话={}, 状态={}", sessionId, status.state)
        return status
    }
    
    override fun stepOut(sessionId: String): DebugStatus {
        val session = getSession(sessionId)
        val status = session.stepOut()
        logger.info("步出函数: 会话={}, 状态={}", sessionId, status.state)
        return status
    }
    
    override fun pause(sessionId: String): DebugStatus {
        val session = getSession(sessionId)
        val status = session.pause()
        logger.info("暂停执行: 会话={}, 状态={}", sessionId, status.state)
        return status
    }
    
    override fun stop(sessionId: String): Boolean {
        val session = getSession(sessionId)
        val result = session.stop()
        logger.info("停止执行: 会话={}, 结果={}", sessionId, result)
        return result
    }
    
    override fun getVariable(sessionId: String, variableName: String): Any? {
        val session = getSession(sessionId)
        return session.getVariable(variableName)
    }
    
    override fun getVariables(sessionId: String): Map<String, Any?> {
        val session = getSession(sessionId)
        return session.getVariables()
    }
    
    override fun evaluate(sessionId: String, expression: String): Any? {
        val session = getSession(sessionId)
        return session.evaluate(expression)
    }
    
    override fun getCallStack(sessionId: String): List<StackFrame> {
        val session = getSession(sessionId)
        return session.getCallStack()
    }
    
    override fun getStatus(sessionId: String): DebugStatus {
        val session = getSession(sessionId)
        return session.getStatus()
    }
    
    override fun closeSession(sessionId: String): Boolean {
        val session = sessions.remove(sessionId)
        if (session != null) {
            session.stop()
            logger.info("关闭调试会话: {}", sessionId)
            return true
        }
        return false
    }
    
    /**
     * 获取调试会话
     */
    private fun getSession(sessionId: String): DebugSession {
        return sessions[sessionId] ?: throw IllegalArgumentException("调试会话不存在: $sessionId")
    }
}
