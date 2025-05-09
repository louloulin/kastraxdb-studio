package ai.magicdb.dataservice.core.executor

import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.core.repository.MemoryDataServiceRepository
import ai.magicdb.script.engine.GraalVMScriptExecutor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.cache.concurrent.ConcurrentMapCacheManager

/**
 * 默认数据服务执行器测试
 *
 * @author magicdb
 */
class DefaultDataServiceExecutorTest {
    
    private lateinit var executor: DefaultDataServiceExecutor
    private lateinit var repository: MemoryDataServiceRepository
    private lateinit var scriptExecutor: GraalVMScriptExecutor
    private lateinit var cacheManager: ConcurrentMapCacheManager
    
    @BeforeEach
    fun setUp() {
        repository = MemoryDataServiceRepository()
        scriptExecutor = GraalVMScriptExecutor()
        cacheManager = ConcurrentMapCacheManager("dataService")
        executor = DefaultDataServiceExecutor(repository, scriptExecutor, cacheManager)
        
        // 创建测试服务
        val service = DataService(
            id = "test-service",
            name = "测试服务",
            description = "测试服务描述",
            type = "query",
            script = "// js\nvar message = 'Hello, ' + name;\nreturn { message: message };",
            language = "js",
            parameters = listOf(
                ai.magicdb.dataservice.api.model.ServiceParameter(
                    name = "name",
                    type = "string",
                    description = "名称",
                    required = true
                )
            )
        )
        repository.saveService(service)
        
        // 创建缓存服务
        val cacheService = DataService(
            id = "cache-service",
            name = "缓存服务",
            description = "缓存服务描述",
            type = "query",
            script = "// js\nvar message = 'Hello, ' + name;\nreturn { message: message, timestamp: Date.now() };",
            language = "js",
            parameters = listOf(
                ai.magicdb.dataservice.api.model.ServiceParameter(
                    name = "name",
                    type = "string",
                    description = "名称",
                    required = true
                )
            ),
            cacheTime = 60000 // 1分钟缓存
        )
        repository.saveService(cacheService)
        
        // 创建禁用服务
        val disabledService = DataService(
            id = "disabled-service",
            name = "禁用服务",
            description = "禁用服务描述",
            type = "query",
            script = "// js\nreturn { message: 'Disabled' };",
            language = "js",
            enabled = false
        )
        repository.saveService(disabledService)
    }
    
    @Test
    fun testExecute() {
        val result = executor.execute("test-service", mapOf("name" to "World"))
        
        assertTrue(result.success)
        assertNotNull(result.data)
        
        @Suppress("UNCHECKED_CAST")
        val data = result.data as Map<String, Any?>
        assertEquals("Hello, World", data["message"])
    }
    
    @Test
    fun testExecuteWithMissingParameter() {
        val result = executor.execute("test-service", emptyMap())
        
        assertFalse(result.success)
        assertEquals("缺少必需参数: name", result.message)
    }
    
    @Test
    fun testExecuteNonExistentService() {
        val result = executor.execute("non-existent", emptyMap())
        
        assertFalse(result.success)
        assertEquals("服务不存在: non-existent", result.message)
    }
    
    @Test
    fun testExecuteDisabledService() {
        val result = executor.execute("disabled-service", emptyMap())
        
        assertFalse(result.success)
        assertEquals("服务已禁用: disabled-service", result.message)
    }
    
    @Test
    fun testExecuteWithCache() {
        // 第一次执行
        val result1 = executor.execute("cache-service", mapOf("name" to "World"))
        
        assertTrue(result1.success)
        assertFalse(result1.fromCache)
        
        // 第二次执行，应该从缓存获取
        val result2 = executor.execute("cache-service", mapOf("name" to "World"))
        
        assertTrue(result2.success)
        assertTrue(result2.fromCache)
        
        // 检查结果是否相同
        @Suppress("UNCHECKED_CAST")
        val data1 = result1.data as Map<String, Any?>
        @Suppress("UNCHECKED_CAST")
        val data2 = result2.data as Map<String, Any?>
        
        assertEquals(data1["message"], data2["message"])
        assertEquals(data1["timestamp"], data2["timestamp"]) // 时间戳应该相同，因为是从缓存获取的
    }
    
    @Test
    fun testExecuteScript() {
        val script = "// js\nvar message = 'Hello, ' + name;\nreturn { message: message };"
        val result = executor.executeScript(script, "js", mapOf("name" to "World"))
        
        assertTrue(result.success)
        assertNotNull(result.data)
        
        @Suppress("UNCHECKED_CAST")
        val data = result.data as Map<String, Any?>
        assertEquals("Hello, World", data["message"])
    }
    
    @Test
    fun testValidateScript() {
        // 有效脚本
        val validScript = "// js\nvar message = 'Hello, World';\nreturn message;"
        val validResult = executor.validateScript(validScript, "js")
        
        assertTrue(validResult.success)
        
        // 无效脚本
        val invalidScript = "// js\nvar message = 'Hello, World';\nreturn message"
        val invalidResult = executor.validateScript(invalidScript, "js")
        
        assertFalse(invalidResult.success)
        assertNotNull(invalidResult.message)
    }
}
