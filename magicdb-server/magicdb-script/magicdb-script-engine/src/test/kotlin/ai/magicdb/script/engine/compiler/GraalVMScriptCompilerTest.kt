package ai.magicdb.script.engine.compiler

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * GraalVM脚本编译器测试
 *
 * @author magicdb
 */
class GraalVMScriptCompilerTest {
    
    private lateinit var compiler: GraalVMScriptCompiler
    
    @BeforeEach
    fun setUp() {
        compiler = GraalVMScriptCompiler()
    }
    
    @Test
    fun testCompileAndExecute() {
        // 编译脚本
        val compiledScript = compiler.compile("var x = 1 + 2; x;", "js")
        
        // 执行脚本
        val result = compiler.execute(compiledScript, emptyMap())
        
        // 验证结果
        assertEquals(3, result)
    }
    
    @Test
    fun testExecuteWithContext() {
        // 执行脚本
        val result = compiler.execute("var sum = a + b; sum;", "js", mapOf("a" to 10, "b" to 20))
        
        // 验证结果
        assertEquals(30, result)
    }
    
    @Test
    fun testCompileError() {
        // 编译错误的脚本
        assertThrows<RuntimeException> {
            compiler.compile("var x = 1 + ; x;", "js")
        }
    }
    
    @Test
    fun testExecuteError() {
        // 执行错误的脚本
        assertThrows<RuntimeException> {
            compiler.execute("var x = a + b; x;", "js", mapOf("a" to 10))
        }
    }
    
    @Test
    fun testCacheReuse() {
        // 编译脚本
        val script = "var x = 1 + 2; x;"
        
        // 第一次编译
        val compiledScript1 = compiler.compile(script, "js")
        
        // 第二次编译，应该复用缓存
        val compiledScript2 = compiler.compile(script, "js")
        
        // 验证是同一个对象
        assertSame(compiledScript1.compiledObject, compiledScript2.compiledObject)
        
        // 执行脚本
        val result = compiler.execute(compiledScript2, emptyMap())
        
        // 验证结果
        assertEquals(3, result)
    }
    
    @Test
    fun testClearCache() {
        // 编译脚本
        val script = "var x = 1 + 2; x;"
        
        // 第一次编译
        val compiledScript1 = compiler.compile(script, "js")
        
        // 清除缓存
        compiler.clearCache()
        
        // 第二次编译，应该重新编译
        val compiledScript2 = compiler.compile(script, "js")
        
        // 验证是不同的对象
        assertNotSame(compiledScript1.compiledObject, compiledScript2.compiledObject)
        
        // 执行脚本
        val result = compiler.execute(compiledScript2, emptyMap())
        
        // 验证结果
        assertEquals(3, result)
    }
    
    @Test
    fun testGetCacheSize() {
        // 验证初始缓存大小为0
        assertEquals(0, compiler.getCacheSize())
        
        // 编译脚本
        compiler.compile("var x = 1 + 2; x;", "js")
        
        // 验证缓存大小为1
        assertEquals(1, compiler.getCacheSize())
        
        // 编译另一个脚本
        compiler.compile("var y = 3 + 4; y;", "js")
        
        // 验证缓存大小为2
        assertEquals(2, compiler.getCacheSize())
        
        // 清除缓存
        compiler.clearCache()
        
        // 验证缓存大小为0
        assertEquals(0, compiler.getCacheSize())
    }
}
