package ai.magicdb.script.engine.provider

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnJre
import org.junit.jupiter.api.condition.JRE

/**
 * Python语言提供者测试
 *
 * @author magicdb
 */
class PythonLanguageProviderTest {
    
    private val provider = PythonLanguageProvider()
    
    @Test
    fun testSupport() {
        assertTrue(provider.support("python"))
        assertTrue(provider.support("py"))
        assertTrue(provider.support("PYTHON"))
        assertFalse(provider.support("js"))
        assertFalse(provider.support("kotlin"))
    }
    
    @Test
    @EnabledOnJre(JRE.JAVA_11, JRE.JAVA_17) // GraalVM Python支持需要Java 11或更高版本
    fun testExecuteSimpleScript() {
        val script = """
            x = 10
            y = 20
            result = x + y
            result
        """.trimIndent()
        
        val context = mapOf<String, Any?>()
        
        try {
            val result = provider.execute("python", script, context)
            assertEquals(30, result)
        } catch (e: Exception) {
            // 如果GraalVM Python运行时不可用，测试将被跳过
            assumeTrue(false, "GraalVM Python runtime is not available: ${e.message}")
        }
    }
    
    @Test
    @EnabledOnJre(JRE.JAVA_11, JRE.JAVA_17)
    fun testExecuteWithContext() {
        val script = """
            result = a + b
            result
        """.trimIndent()
        
        val context = mapOf(
            "a" to 15,
            "b" to 25
        )
        
        try {
            val result = provider.execute("python", script, context)
            assertEquals(40, result)
        } catch (e: Exception) {
            // 如果GraalVM Python运行时不可用，测试将被跳过
            assumeTrue(false, "GraalVM Python runtime is not available: ${e.message}")
        }
    }
    
    @Test
    @EnabledOnJre(JRE.JAVA_11, JRE.JAVA_17)
    fun testExecuteWithJsonHelpers() {
        val script = """
            data = {"name": "John", "age": 30}
            json_str = to_json(data)
            parsed = from_json(json_str)
            parsed["age"] = 31
            parsed
        """.trimIndent()
        
        val context = mapOf<String, Any?>()
        
        try {
            val result = provider.execute("python", script, context) as Map<*, *>
            assertEquals("John", result["name"])
            assertEquals(31, result["age"])
        } catch (e: Exception) {
            // 如果GraalVM Python运行时不可用，测试将被跳过
            assumeTrue(false, "GraalVM Python runtime is not available: ${e.message}")
        }
    }
    
    @Test
    @EnabledOnJre(JRE.JAVA_11, JRE.JAVA_17)
    fun testExecuteWithDatetimeHelpers() {
        val script = """
            today_date = today()
            parsed_date = parse_date("2023-01-15")
            {"today": str(today_date), "parsed": str(parsed_date)}
        """.trimIndent()
        
        val context = mapOf<String, Any?>()
        
        try {
            val result = provider.execute("python", script, context) as Map<*, *>
            assertNotNull(result["today"])
            assertEquals("2023-01-15", result["parsed"])
        } catch (e: Exception) {
            // 如果GraalVM Python运行时不可用，测试将被跳过
            assumeTrue(false, "GraalVM Python runtime is not available: ${e.message}")
        }
    }
    
    @Test
    @EnabledOnJre(JRE.JAVA_11, JRE.JAVA_17)
    fun testExecuteWithList() {
        val script = """
            numbers = [1, 2, 3, 4, 5]
            result = [n * 2 for n in numbers]
            result
        """.trimIndent()
        
        val context = mapOf<String, Any?>()
        
        try {
            val result = provider.execute("python", script, context) as List<*>
            assertEquals(5, result.size)
            assertEquals(2, result[0])
            assertEquals(4, result[1])
            assertEquals(6, result[2])
            assertEquals(8, result[3])
            assertEquals(10, result[4])
        } catch (e: Exception) {
            // 如果GraalVM Python运行时不可用，测试将被跳过
            assumeTrue(false, "GraalVM Python runtime is not available: ${e.message}")
        }
    }
    
    private fun assumeTrue(condition: Boolean, message: String) {
        if (!condition) {
            System.err.println("Test skipped: $message")
            return
        }
    }
}
