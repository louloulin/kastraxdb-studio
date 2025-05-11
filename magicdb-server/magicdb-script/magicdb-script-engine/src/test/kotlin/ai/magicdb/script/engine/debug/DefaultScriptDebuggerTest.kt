package ai.magicdb.script.engine.debug

import ai.magicdb.script.api.DebugState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * 默认脚本调试器测试
 *
 * @author magicdb
 */
class DefaultScriptDebuggerTest {
    
    private lateinit var debugger: DefaultScriptDebugger
    
    @BeforeEach
    fun setUp() {
        debugger = DefaultScriptDebugger()
    }
    
    @Test
    fun testCreateSession() {
        val script = """
            x = 10
            y = 20
            z = x + y
        """.trimIndent()
        
        val sessionId = debugger.createSession(script, "js", emptyMap())
        
        assertNotNull(sessionId)
        assertEquals(DebugState.NOT_STARTED, debugger.getStatus(sessionId).state)
    }
    
    @Test
    fun testSetBreakpoint() {
        val script = """
            x = 10
            y = 20
            z = x + y
        """.trimIndent()
        
        val sessionId = debugger.createSession(script, "js", emptyMap())
        val breakpointId = debugger.setBreakpoint(sessionId, 2, null)
        
        assertNotNull(breakpointId)
        
        val breakpoints = debugger.getBreakpoints(sessionId)
        assertEquals(1, breakpoints.size)
        assertEquals(2, breakpoints[0].lineNumber)
        assertNull(breakpoints[0].condition)
        assertTrue(breakpoints[0].enabled)
    }
    
    @Test
    fun testRemoveBreakpoint() {
        val script = """
            x = 10
            y = 20
            z = x + y
        """.trimIndent()
        
        val sessionId = debugger.createSession(script, "js", emptyMap())
        val breakpointId = debugger.setBreakpoint(sessionId, 2, null)
        
        assertTrue(debugger.removeBreakpoint(sessionId, breakpointId))
        assertEquals(0, debugger.getBreakpoints(sessionId).size)
    }
    
    @Test
    fun testStartAndPause() {
        val script = """
            x = 10
            y = 20
            z = x + y
        """.trimIndent()
        
        val sessionId = debugger.createSession(script, "js", emptyMap())
        
        // 启动调试
        val status = debugger.start(sessionId)
        assertEquals(DebugState.RUNNING, status.state)
        
        // 等待一段时间
        Thread.sleep(200)
        
        // 暂停调试
        val pauseStatus = debugger.pause(sessionId)
        assertEquals(DebugState.PAUSED, pauseStatus.state)
        
        // 继续执行
        val continueStatus = debugger.continue_(sessionId)
        assertEquals(DebugState.RUNNING, continueStatus.state)
        
        // 等待执行完成
        waitForCompletion(sessionId)
        
        // 检查最终状态
        val finalStatus = debugger.getStatus(sessionId)
        assertEquals(DebugState.COMPLETED, finalStatus.state)
    }
    
    @Test
    fun testBreakpointHit() {
        val script = """
            x = 10
            y = 20
            z = x + y
        """.trimIndent()
        
        val sessionId = debugger.createSession(script, "js", emptyMap())
        
        // 设置断点
        debugger.setBreakpoint(sessionId, 2, null)
        
        // 启动调试
        debugger.start(sessionId)
        
        // 等待断点命中
        waitForState(sessionId, DebugState.PAUSED)
        
        // 检查状态
        val status = debugger.getStatus(sessionId)
        assertEquals(DebugState.PAUSED, status.state)
        assertEquals(2, status.currentLine)
        
        // 继续执行
        debugger.continue_(sessionId)
        
        // 等待执行完成
        waitForCompletion(sessionId)
    }
    
    @Test
    fun testStepOver() {
        val script = """
            x = 10
            y = 20
            z = x + y
        """.trimIndent()
        
        val sessionId = debugger.createSession(script, "js", emptyMap())
        
        // 设置断点
        debugger.setBreakpoint(sessionId, 1, null)
        
        // 启动调试
        debugger.start(sessionId)
        
        // 等待断点命中
        waitForState(sessionId, DebugState.PAUSED)
        
        // 检查状态
        var status = debugger.getStatus(sessionId)
        assertEquals(DebugState.PAUSED, status.state)
        assertEquals(1, status.currentLine)
        
        // 单步执行
        debugger.stepOver(sessionId)
        
        // 等待下一个暂停点
        waitForState(sessionId, DebugState.PAUSED)
        
        // 检查状态
        status = debugger.getStatus(sessionId)
        assertEquals(DebugState.PAUSED, status.state)
        assertEquals(2, status.currentLine)
        
        // 继续执行
        debugger.continue_(sessionId)
        
        // 等待执行完成
        waitForCompletion(sessionId)
    }
    
    @Test
    fun testGetVariables() {
        val script = """
            x = 10
            y = 20
            z = x + y
        """.trimIndent()
        
        val sessionId = debugger.createSession(script, "js", emptyMap())
        
        // 设置断点
        debugger.setBreakpoint(sessionId, 3, null)
        
        // 启动调试
        debugger.start(sessionId)
        
        // 等待断点命中
        waitForState(sessionId, DebugState.PAUSED)
        
        // 获取变量
        val variables = debugger.getVariables(sessionId)
        assertEquals(2, variables.size)
        assertEquals(10, variables["x"])
        assertEquals(20, variables["y"])
        
        // 继续执行
        debugger.continue_(sessionId)
        
        // 等待执行完成
        waitForCompletion(sessionId)
        
        // 获取最终变量
        val finalVariables = debugger.getVariables(sessionId)
        assertEquals(3, finalVariables.size)
        assertEquals(10, finalVariables["x"])
        assertEquals(20, finalVariables["y"])
        assertEquals(30, finalVariables["z"])
    }
    
    @Test
    fun testEvaluateExpression() {
        val script = """
            x = 10
            y = 20
            z = x + y
        """.trimIndent()
        
        val sessionId = debugger.createSession(script, "js", emptyMap())
        
        // 设置断点
        debugger.setBreakpoint(sessionId, 3, null)
        
        // 启动调试
        debugger.start(sessionId)
        
        // 等待断点命中
        waitForState(sessionId, DebugState.PAUSED)
        
        // 计算表达式
        assertEquals(10, debugger.evaluate(sessionId, "x"))
        assertEquals(20, debugger.evaluate(sessionId, "y"))
        assertEquals(30, debugger.evaluate(sessionId, "x + y"))
        
        // 继续执行
        debugger.continue_(sessionId)
        
        // 等待执行完成
        waitForCompletion(sessionId)
    }
    
    @Test
    fun testStopExecution() {
        val script = """
            x = 10
            y = 20
            z = x + y
        """.trimIndent()
        
        val sessionId = debugger.createSession(script, "js", emptyMap())
        
        // 启动调试
        debugger.start(sessionId)
        
        // 等待一段时间
        Thread.sleep(200)
        
        // 停止执行
        assertTrue(debugger.stop(sessionId))
        
        // 检查状态
        val status = debugger.getStatus(sessionId)
        assertEquals(DebugState.COMPLETED, status.state)
    }
    
    @Test
    fun testCloseSession() {
        val script = """
            x = 10
            y = 20
            z = x + y
        """.trimIndent()
        
        val sessionId = debugger.createSession(script, "js", emptyMap())
        
        // 启动调试
        debugger.start(sessionId)
        
        // 等待一段时间
        Thread.sleep(200)
        
        // 关闭会话
        assertTrue(debugger.closeSession(sessionId))
        
        // 检查会话是否存在
        assertThrows(IllegalArgumentException::class.java) {
            debugger.getStatus(sessionId)
        }
    }
    
    /**
     * 等待调试完成
     */
    private fun waitForCompletion(sessionId: String) {
        val latch = CountDownLatch(1)
        val thread = Thread {
            while (true) {
                val status = debugger.getStatus(sessionId)
                if (status.state == DebugState.COMPLETED || status.state == DebugState.ERROR) {
                    latch.countDown()
                    break
                }
                Thread.sleep(100)
            }
        }
        thread.start()
        assertTrue(latch.await(5, TimeUnit.SECONDS))
    }
    
    /**
     * 等待特定状态
     */
    private fun waitForState(sessionId: String, state: DebugState) {
        val latch = CountDownLatch(1)
        val thread = Thread {
            while (true) {
                val status = debugger.getStatus(sessionId)
                if (status.state == state) {
                    latch.countDown()
                    break
                }
                Thread.sleep(100)
            }
        }
        thread.start()
        assertTrue(latch.await(5, TimeUnit.SECONDS))
    }
}
