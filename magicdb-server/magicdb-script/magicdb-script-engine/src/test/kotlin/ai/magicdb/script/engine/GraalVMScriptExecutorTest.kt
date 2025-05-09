package ai.magicdb.script.engine

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * GraalVM脚本执行器测试
 *
 * @author magicdb
 */
class GraalVMScriptExecutorTest {
    
    private val scriptExecutor = GraalVMScriptExecutor()
    
    @Test
    fun testJavaScriptExecution() {
        val script = """
            var result = 1 + 2;
            result;
        """.trimIndent()
        
        val context = mapOf<String, Any?>("x" to 10, "y" to 20)
        val result = scriptExecutor.execute("js", script, context)
        
        assertEquals(3, result)
    }
    
    @Test
    fun testJavaScriptWithContext() {
        val script = """
            var sum = x + y;
            sum;
        """.trimIndent()
        
        val context = mapOf<String, Any?>("x" to 10, "y" to 20)
        val result = scriptExecutor.execute("js", script, context)
        
        assertEquals(30, result)
    }
    
    @Test
    fun testGetSupportedLanguages() {
        val languages = scriptExecutor.getSupportedLanguages()
        
        assertNotNull(languages)
        assert(languages.contains("js"))
        assert(languages.contains("kotlin"))
    }
    
    @Test
    fun testUnsupportedLanguage() {
        val script = "println('Hello')"
        val context = mapOf<String, Any?>()
        
        assertThrows<UnsupportedOperationException> {
            scriptExecutor.execute("unsupported", script, context)
        }
    }
}
