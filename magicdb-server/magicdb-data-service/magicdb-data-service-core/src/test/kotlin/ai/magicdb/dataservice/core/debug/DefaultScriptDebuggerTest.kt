package ai.magicdb.dataservice.core.debug

import ai.magicdb.dataservice.api.DataSourceService
import ai.magicdb.dataservice.api.model.ScriptDebugRequest
import ai.magicdb.dataservice.core.script.ScriptExecutor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.concurrent.TimeUnit

/**
 * 默认脚本调试器测试
 *
 * @author magicdb
 */
class DefaultScriptDebuggerTest {
    
    private lateinit var scriptDebugger: DefaultScriptDebugger
    private lateinit var scriptExecutor: ScriptExecutor
    private lateinit var dataSourceService: DataSourceService
    
    @BeforeEach
    fun setUp() {
        scriptExecutor = mock(ScriptExecutor::class.java)
        dataSourceService = mock(DataSourceService::class.java)
        scriptDebugger = DefaultScriptDebugger(scriptExecutor, dataSourceService)
    }
    
    @Test
    fun testDebug() {
        // 准备测试数据
        val script = "function execute(params) { return { message: 'Hello, ' + params.name }; }"
        val language = "js"
        val parameters = mapOf("name" to "World")
        val request = ScriptDebugRequest(
            script = script,
            language = language,
            parameters = parameters
        )
        val expectedResult = mapOf("message" to "Hello, World")
        
        // 设置模拟对象的行为
        `when`(scriptExecutor.execute(eq(script), eq(language), any(), eq(30000L), eq(TimeUnit.MILLISECONDS)))
            .thenReturn(expectedResult)
        
        // 执行测试
        val result = scriptDebugger.debug(request)
        
        // 验证结果
        assertTrue(result.success)
        assertEquals(expectedResult, result.data)
        assertNull(result.message)
        assertNotNull(result.logs)
        assertNotNull(result.console)
        
        // 验证方法调用
        verify(scriptExecutor).execute(eq(script), eq(language), any(), eq(30000L), eq(TimeUnit.MILLISECONDS))
    }
    
    @Test
    fun testDebugWithDataSource() {
        // 准备测试数据
        val script = "function execute(params) { return executeQuery('SELECT * FROM users'); }"
        val language = "js"
        val dataSourceId = 1L
        val databaseName = "test"
        val request = ScriptDebugRequest(
            script = script,
            language = language,
            dataSourceId = dataSourceId,
            databaseName = databaseName
        )
        val queryResult = mapOf(
            "success" to true,
            "data" to listOf(mapOf("id" to 1, "name" to "John"))
        )
        
        // 设置模拟对象的行为
        `when`(scriptExecutor.execute(eq(script), eq(language), any(), eq(30000L), eq(TimeUnit.MILLISECONDS)))
            .thenReturn(queryResult)
        
        // 执行测试
        val result = scriptDebugger.debug(request)
        
        // 验证结果
        assertTrue(result.success)
        assertEquals(queryResult, result.data)
        
        // 验证方法调用
        verify(scriptExecutor).execute(eq(script), eq(language), any(), eq(30000L), eq(TimeUnit.MILLISECONDS))
    }
    
    @Test
    fun testDebugWithError() {
        // 准备测试数据
        val script = "function execute(params) { throw new Error('Test error'); }"
        val language = "js"
        val request = ScriptDebugRequest(
            script = script,
            language = language
        )
        
        // 设置模拟对象的行为
        `when`(scriptExecutor.execute(eq(script), eq(language), any(), eq(30000L), eq(TimeUnit.MILLISECONDS)))
            .thenThrow(RuntimeException("Test error"))
        
        // 执行测试
        val result = scriptDebugger.debug(request)
        
        // 验证结果
        assertFalse(result.success)
        assertEquals("Test error", result.message)
        assertNotNull(result.stackTrace)
        
        // 验证方法调用
        verify(scriptExecutor).execute(eq(script), eq(language), any(), eq(30000L), eq(TimeUnit.MILLISECONDS))
    }
    
    @Test
    fun testGetLanguages() {
        // 准备测试数据
        val languages = listOf("js", "kotlin", "python")
        
        // 设置模拟对象的行为
        `when`(scriptExecutor.getSupportedLanguages()).thenReturn(languages)
        
        // 执行测试
        val result = scriptDebugger.getLanguages()
        
        // 验证结果
        assertEquals(languages, result)
        
        // 验证方法调用
        verify(scriptExecutor).getSupportedLanguages()
    }
    
    @Test
    fun testGetTemplate() {
        // 执行测试
        val jsTemplate = scriptDebugger.getTemplate("js")
        val kotlinTemplate = scriptDebugger.getTemplate("kotlin")
        val pythonTemplate = scriptDebugger.getTemplate("python")
        val unknownTemplate = scriptDebugger.getTemplate("unknown")
        
        // 验证结果
        assertTrue(jsTemplate.contains("function execute(params)"))
        assertTrue(kotlinTemplate.contains("fun execute(params: Map<String, Any?>)"))
        assertTrue(pythonTemplate.contains("def execute(params):"))
        assertTrue(unknownTemplate.contains("function execute(params)"))
    }
}
