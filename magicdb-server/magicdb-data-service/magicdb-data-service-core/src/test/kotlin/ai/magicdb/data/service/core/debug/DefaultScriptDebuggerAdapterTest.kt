package ai.magicdb.data.service.core.debug

import ai.magicdb.data.service.api.model.ScriptDebugRequest
import ai.magicdb.data.service.api.model.ScriptDebugResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

/**
 * 默认脚本调试器适配器测试
 *
 * @author magicdb
 */
class DefaultScriptDebuggerAdapterTest {
    
    private lateinit var debugger: DefaultScriptDebuggerAdapter
    private lateinit var delegateDebugger: DefaultScriptDebugger
    
    @BeforeEach
    fun setUp() {
        delegateDebugger = mock(DefaultScriptDebugger::class.java)
        debugger = DefaultScriptDebuggerAdapter()
        
        // 使用反射设置delegate字段
        val field = DefaultScriptDebuggerAdapter::class.java.getDeclaredField("delegate")
        field.isAccessible = true
        field.set(debugger, delegateDebugger)
    }
    
    @Test
    fun testDebug() {
        // 准备测试数据
        val request = ScriptDebugRequest(
            script = """
                x = 10
                y = 20
                result = x + y
            """.trimIndent(),
            language = "js",
            parameters = emptyMap()
        )
        
        // 模拟delegate的行为
        val mockResult = ScriptDebugResult(
            success = true,
            data = 30
        )
        `when`(delegateDebugger.debug(request)).thenReturn(mockResult)
        
        // 执行测试
        val result = debugger.debug(request)
        
        // 验证结果
        assertTrue(result.success)
        assertEquals(30, result.data)
        
        // 验证delegate方法被调用
        verify(delegateDebugger).debug(request)
    }
    
    @Test
    fun testGetLanguages() {
        // 模拟delegate的行为
        val languages = listOf("js", "python", "kotlin")
        `when`(delegateDebugger.getLanguages()).thenReturn(languages)
        
        // 执行测试
        val result = debugger.getLanguages()
        
        // 验证结果
        assertEquals(languages, result)
        assertTrue(result.contains("js"))
        assertTrue(result.contains("python"))
        assertTrue(result.contains("kotlin"))
        
        // 验证delegate方法被调用
        verify(delegateDebugger).getLanguages()
    }
    
    @Test
    fun testGetTemplate() {
        // 模拟delegate的行为
        val jsTemplate = "JavaScript template"
        val pythonTemplate = "Python template"
        val kotlinTemplate = "Kotlin template"
        
        `when`(delegateDebugger.getTemplate("js")).thenReturn(jsTemplate)
        `when`(delegateDebugger.getTemplate("python")).thenReturn(pythonTemplate)
        `when`(delegateDebugger.getTemplate("kotlin")).thenReturn(kotlinTemplate)
        
        // 执行测试
        val jsResult = debugger.getTemplate("js")
        val pythonResult = debugger.getTemplate("python")
        val kotlinResult = debugger.getTemplate("kotlin")
        
        // 验证结果
        assertEquals(jsTemplate, jsResult)
        assertEquals(pythonTemplate, pythonResult)
        assertEquals(kotlinTemplate, kotlinResult)
        
        // 验证delegate方法被调用
        verify(delegateDebugger).getTemplate("js")
        verify(delegateDebugger).getTemplate("python")
        verify(delegateDebugger).getTemplate("kotlin")
    }
    
    @Test
    fun testCreateSession() {
        // 准备测试数据
        val request = ScriptDebugRequest(
            script = "x = 10",
            language = "js"
        )
        
        // 模拟delegate的行为
        val sessionId = "test-session-id"
        `when`(delegateDebugger.createSession(request)).thenReturn(sessionId)
        
        // 执行测试
        val result = debugger.createSession(request)
        
        // 验证结果
        assertEquals(sessionId, result)
        
        // 验证delegate方法被调用
        verify(delegateDebugger).createSession(request)
    }
    
    @Test
    fun testCloseSession() {
        // 准备测试数据
        val sessionId = "test-session-id"
        
        // 模拟delegate的行为
        `when`(delegateDebugger.closeSession(sessionId)).thenReturn(true)
        
        // 执行测试
        val result = debugger.closeSession(sessionId)
        
        // 验证结果
        assertTrue(result)
        
        // 验证delegate方法被调用
        verify(delegateDebugger).closeSession(sessionId)
    }
    
    @Test
    fun testGetSession() {
        // 准备测试数据
        val sessionId = "test-session-id"
        
        // 模拟delegate的行为
        val sessionInfo = mapOf(
            "id" to sessionId,
            "script" to "x = 10",
            "language" to "js"
        )
        `when`(delegateDebugger.getSession(sessionId)).thenReturn(sessionInfo)
        
        // 执行测试
        val result = debugger.getSession(sessionId)
        
        // 验证结果
        assertEquals(sessionInfo, result)
        
        // 验证delegate方法被调用
        verify(delegateDebugger).getSession(sessionId)
    }
    
    @Test
    fun testExecuteCommand() {
        // 准备测试数据
        val sessionId = "test-session-id"
        val command = "x + 5"
        
        // 模拟delegate的行为
        val mockResult = ScriptDebugResult(
            success = true,
            data = 15
        )
        `when`(delegateDebugger.executeCommand(sessionId, command)).thenReturn(mockResult)
        
        // 执行测试
        val result = debugger.executeCommand(sessionId, command)
        
        // 验证结果
        assertTrue(result.success)
        assertEquals(15, result.data)
        
        // 验证delegate方法被调用
        verify(delegateDebugger).executeCommand(sessionId, command)
    }
}
