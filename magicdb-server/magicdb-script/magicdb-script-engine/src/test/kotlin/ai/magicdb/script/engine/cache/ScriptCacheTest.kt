package ai.magicdb.script.engine.cache

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

/**
 * 脚本缓存测试
 *
 * @author magicdb
 */
class ScriptCacheTest {
    
    private lateinit var scriptCache: ScriptCache
    
    @BeforeEach
    fun setUp() {
        scriptCache = ScriptCache()
    }
    
    @Test
    fun testPutAndGetCompiledScript() {
        // 创建测试脚本
        val compiledScript = CompiledScript(
            language = "js",
            sourceScript = "var x = 1 + 2; return x;",
            compiledObject = "compiled-object"
        )
        
        // 缓存脚本
        scriptCache.putCompiledScript("test-key", compiledScript)
        
        // 获取脚本
        val cachedScript = scriptCache.getCompiledScript("test-key")
        
        // 验证结果
        assertNotNull(cachedScript)
        assertEquals("js", cachedScript?.language)
        assertEquals("var x = 1 + 2; return x;", cachedScript?.sourceScript)
        assertEquals("compiled-object", cachedScript?.compiledObject)
    }
    
    @Test
    fun testRemoveCompiledScript() {
        // 创建测试脚本
        val compiledScript = CompiledScript(
            language = "js",
            sourceScript = "var x = 1 + 2; return x;",
            compiledObject = "compiled-object"
        )
        
        // 缓存脚本
        scriptCache.putCompiledScript("test-key", compiledScript)
        
        // 验证脚本已缓存
        assertNotNull(scriptCache.getCompiledScript("test-key"))
        
        // 移除脚本
        scriptCache.removeCompiledScript("test-key")
        
        // 验证脚本已移除
        assertNull(scriptCache.getCompiledScript("test-key"))
    }
    
    @Test
    fun testAddAndGetDependencies() {
        // 添加依赖
        scriptCache.addDependency("script1", "dependency1")
        scriptCache.addDependency("script1", "dependency2")
        
        // 获取依赖
        val dependencies = scriptCache.getDependencies("script1")
        
        // 验证结果
        assertEquals(2, dependencies.size)
        assertTrue(dependencies.contains("dependency1"))
        assertTrue(dependencies.contains("dependency2"))
    }
    
    @Test
    fun testRemoveWithDependencies() {
        // 创建测试脚本
        val script1 = CompiledScript(
            language = "js",
            sourceScript = "var x = 1 + 2; return x;",
            compiledObject = "compiled-object-1"
        )
        
        val script2 = CompiledScript(
            language = "js",
            sourceScript = "var y = 3 + 4; return y;",
            compiledObject = "compiled-object-2"
        )
        
        // 缓存脚本
        scriptCache.putCompiledScript("script1", script1)
        scriptCache.putCompiledScript("script2", script2)
        
        // 添加依赖
        scriptCache.addDependency("script2", "script1")
        
        // 验证脚本已缓存
        assertNotNull(scriptCache.getCompiledScript("script1"))
        assertNotNull(scriptCache.getCompiledScript("script2"))
        
        // 移除依赖脚本
        scriptCache.removeCompiledScript("script1")
        
        // 验证依赖脚本已移除
        assertNull(scriptCache.getCompiledScript("script1"))
        
        // 验证依赖该脚本的脚本也已移除
        assertNull(scriptCache.getCompiledScript("script2"))
    }
    
    @Test
    fun testClear() {
        // 创建测试脚本
        val script1 = CompiledScript(
            language = "js",
            sourceScript = "var x = 1 + 2; return x;",
            compiledObject = "compiled-object-1"
        )
        
        val script2 = CompiledScript(
            language = "js",
            sourceScript = "var y = 3 + 4; return y;",
            compiledObject = "compiled-object-2"
        )
        
        // 缓存脚本
        scriptCache.putCompiledScript("script1", script1)
        scriptCache.putCompiledScript("script2", script2)
        
        // 添加依赖
        scriptCache.addDependency("script2", "script1")
        
        // 验证脚本已缓存
        assertNotNull(scriptCache.getCompiledScript("script1"))
        assertNotNull(scriptCache.getCompiledScript("script2"))
        
        // 清空缓存
        scriptCache.clear()
        
        // 验证缓存已清空
        assertNull(scriptCache.getCompiledScript("script1"))
        assertNull(scriptCache.getCompiledScript("script2"))
        assertTrue(scriptCache.getDependencies("script2").isEmpty())
    }
    
    @Test
    fun testSize() {
        // 验证初始大小为0
        assertEquals(0, scriptCache.size())
        
        // 创建测试脚本
        val script1 = CompiledScript(
            language = "js",
            sourceScript = "var x = 1 + 2; return x;",
            compiledObject = "compiled-object-1"
        )
        
        val script2 = CompiledScript(
            language = "js",
            sourceScript = "var y = 3 + 4; return y;",
            compiledObject = "compiled-object-2"
        )
        
        // 缓存脚本
        scriptCache.putCompiledScript("script1", script1)
        scriptCache.putCompiledScript("script2", script2)
        
        // 验证大小为2
        assertEquals(2, scriptCache.size())
        
        // 移除一个脚本
        scriptCache.removeCompiledScript("script1")
        
        // 验证大小为1
        assertEquals(1, scriptCache.size())
        
        // 清空缓存
        scriptCache.clear()
        
        // 验证大小为0
        assertEquals(0, scriptCache.size())
    }
}
