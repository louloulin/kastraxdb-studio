package ai.magicdb.dataservice.core.debug

import ai.magicdb.dataservice.api.model.ScriptDebugRequest
import ai.magicdb.script.api.DebugState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * 默认脚本调试器适配器测试
 *
 * @author magicdb
 */
class DefaultScriptDebuggerAdapterTest {
    
    private lateinit var debugger: DefaultScriptDebuggerAdapter
    
    @BeforeEach
    fun setUp() {
        debugger = DefaultScriptDebuggerAdapter()
    }
    
    @Test
    fun testDebug() {
        val request = ScriptDebugRequest(
            script = """
                x = 10
                y = 20
                result = x + y
            """.trimIndent(),
            language = "js",
            parameters = emptyMap()
        )
        
        val result = debugger.debug(request)
        
        assertTrue(result.success)
        assertEquals(30, result.result)
        assertTrue(result.variables.containsKey("x"))
        assertTrue(result.variables.containsKey("y"))
        assertTrue(result.variables.containsKey("result"))
        assertEquals(10, result.variables["x"])
        assertEquals(20, result.variables["y"])
        assertEquals(30, result.variables["result"])
    }
    
    @Test
    fun testGetLanguages() {
        val languages = debugger.getLanguages()
        
        assertTrue(languages.contains("js"))
        assertTrue(languages.contains("python"))
        assertTrue(languages.contains("kotlin"))
    }
    
    @Test
    fun testGetTemplate() {
        val jsTemplate = debugger.getTemplate("js")
        val pythonTemplate = debugger.getTemplate("python")
        val kotlinTemplate = debugger.getTemplate("kotlin")
        
        assertTrue(jsTemplate.contains("JavaScript"))
        assertTrue(pythonTemplate.contains("Python"))
        assertTrue(kotlinTemplate.contains("Kotlin"))
    }
    
    @Test
    fun testCreateSessionAndBreakpoints() {
        val script = """
            x = 10
            y = 20
            z = x + y
        """.trimIndent()
        
        // 创建会话
        val sessionId = debugger.createSession(script, "js", emptyMap())
        assertNotNull(sessionId)
        
        // 设置断点
        val breakpointId = debugger.setBreakpoint(sessionId, 2, null)
        assertNotNull(breakpointId)
        
        // 获取断点
        val breakpoints = debugger.getBreakpoints(sessionId)
        assertEquals(1, breakpoints.size)
        assertEquals(2, breakpoints[0].lineNumber)
        
        // 删除断点
        assertTrue(debugger.removeBreakpoint(sessionId, breakpointId))
        
        // 验证断点已删除
        val updatedBreakpoints = debugger.getBreakpoints(sessionId)
        assertEquals(0, updatedBreakpoints.size)
        
        // 关闭会话
        assertTrue(debugger.closeSession(sessionId))
    }
    
    @Test
    fun testDebugExecution() {
        val script = """
            x = 10
            y = 20
            z = x + y
        """.trimIndent()
        
        // 创建会话
        val sessionId = debugger.createSession(script, "js", emptyMap())
        
        // 设置断点
        debugger.setBreakpoint(sessionId, 2, null)
        
        // 开始执行
        val startStatus = debugger.start(sessionId)
        assertEquals(DebugState.RUNNING, startStatus.state)
        
        // 等待断点命中
        var status = startStatus
        for (i in 0 until 50) {
            status = debugger.getStatus(sessionId)
            if (status.state == DebugState.PAUSED) {
                break
            }
            Thread.sleep(100)
        }
        
        // 验证断点命中
        assertEquals(DebugState.PAUSED, status.state)
        assertEquals(2, status.currentLine)
        
        // 获取变量
        val variables = debugger.getVariables(sessionId)
        assertTrue(variables.containsKey("x"))
        assertEquals(10, variables["x"])
        
        // 计算表达式
        val expressionResult = debugger.evaluate(sessionId, "x + 5")
        assertEquals(15, expressionResult)
        
        // 单步执行
        val stepStatus = debugger.stepOver(sessionId)
        assertEquals(DebugState.PAUSED, stepStatus.state)
        
        // 继续执行
        val continueStatus = debugger.continue_(sessionId)
        assertEquals(DebugState.RUNNING, continueStatus.state)
        
        // 等待执行完成
        for (i in 0 until 50) {
            status = debugger.getStatus(sessionId)
            if (status.state == DebugState.COMPLETED) {
                break
            }
            Thread.sleep(100)
        }
        
        // 验证执行完成
        assertEquals(DebugState.COMPLETED, status.state)
        
        // 获取最终变量
        val finalVariables = debugger.getVariables(sessionId)
        assertEquals(10, finalVariables["x"])
        assertEquals(20, finalVariables["y"])
        assertEquals(30, finalVariables["z"])
        
        // 关闭会话
        assertTrue(debugger.closeSession(sessionId))
    }
}
