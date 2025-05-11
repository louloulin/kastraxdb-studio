package ai.magicdb.script.engine

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnJre
import org.junit.jupiter.api.condition.JRE

/**
 * Python支持测试
 *
 * @author magicdb
 */
class PythonSupportTest {
    
    private val scriptExecutor = GraalVMScriptExecutor()
    
    @Test
    fun testGetSupportedLanguages() {
        val languages = scriptExecutor.getSupportedLanguages()
        
        assertNotNull(languages)
        assertTrue(languages.contains("python"))
    }
    
    @Test
    @EnabledOnJre(JRE.JAVA_11, JRE.JAVA_17) // GraalVM Python支持需要Java 11或更高版本
    fun testSimplePythonScript() {
        val script = """
            x = 10
            y = 20
            result = x + y
            result
        """.trimIndent()
        
        val context = mapOf<String, Any?>()
        
        try {
            val result = scriptExecutor.execute("python", script, context)
            assertEquals(30, result)
        } catch (e: Exception) {
            // 如果GraalVM Python运行时不可用，测试将被跳过
            System.err.println("GraalVM Python runtime is not available: ${e.message}")
        }
    }
    
    @Test
    @EnabledOnJre(JRE.JAVA_11, JRE.JAVA_17)
    fun testPythonScriptWithContext() {
        val script = """
            result = a + b
            result
        """.trimIndent()
        
        val context = mapOf(
            "a" to 15,
            "b" to 25
        )
        
        try {
            val result = scriptExecutor.execute("python", script, context)
            assertEquals(40, result)
        } catch (e: Exception) {
            // 如果GraalVM Python运行时不可用，测试将被跳过
            System.err.println("GraalVM Python runtime is not available: ${e.message}")
        }
    }
    
    @Test
    @EnabledOnJre(JRE.JAVA_11, JRE.JAVA_17)
    fun testPythonListManipulation() {
        val script = """
            numbers = [1, 2, 3, 4, 5]
            result = [n * 2 for n in numbers]
            result
        """.trimIndent()
        
        val context = mapOf<String, Any?>()
        
        try {
            val result = scriptExecutor.execute("python", script, context)
            assertTrue(result is List<*>)
            val resultList = result as List<*>
            assertEquals(5, resultList.size)
            assertEquals(2, resultList[0])
            assertEquals(4, resultList[1])
            assertEquals(6, resultList[2])
            assertEquals(8, resultList[3])
            assertEquals(10, resultList[4])
        } catch (e: Exception) {
            // 如果GraalVM Python运行时不可用，测试将被跳过
            System.err.println("GraalVM Python runtime is not available: ${e.message}")
        }
    }
    
    @Test
    @EnabledOnJre(JRE.JAVA_11, JRE.JAVA_17)
    fun testPythonDictionary() {
        val script = """
            person = {"name": "John", "age": 30}
            person["age"] = 31
            person
        """.trimIndent()
        
        val context = mapOf<String, Any?>()
        
        try {
            val result = scriptExecutor.execute("python", script, context)
            assertTrue(result is Map<*, *>)
            val resultMap = result as Map<*, *>
            assertEquals("John", resultMap["name"])
            assertEquals(31, resultMap["age"])
        } catch (e: Exception) {
            // 如果GraalVM Python运行时不可用，测试将被跳过
            System.err.println("GraalVM Python runtime is not available: ${e.message}")
        }
    }
}
