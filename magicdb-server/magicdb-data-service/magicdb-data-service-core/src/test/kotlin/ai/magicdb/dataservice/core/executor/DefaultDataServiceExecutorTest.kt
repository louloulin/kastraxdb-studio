package ai.magicdb.dataservice.core.executor

import ai.magicdb.dataservice.api.DataSourceService
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceResult
import ai.magicdb.dataservice.core.cache.DataServiceCacheManager
import ai.magicdb.dataservice.core.repository.MemoryDataServiceRepository
import ai.magicdb.script.engine.GraalVMScriptExecutor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

/**
 * 默认数据服务执行器测试
 *
 * @author magicdb
 */
class DefaultDataServiceExecutorTest {

    private lateinit var executor: DefaultDataServiceExecutor
    private lateinit var repository: MemoryDataServiceRepository
    private lateinit var scriptExecutor: GraalVMScriptExecutor
    private lateinit var cacheManager: DataServiceCacheManager

    @BeforeEach
    fun setUp() {
        repository = MemoryDataServiceRepository()
        scriptExecutor = GraalVMScriptExecutor()
        cacheManager = mock(DataServiceCacheManager::class.java)
        val dataSourceService = mock(DataSourceService::class.java)
        executor = DefaultDataServiceExecutor(repository, scriptExecutor, cacheManager, dataSourceService)

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
        // 模拟缓存行为
        val cacheKey = "service:cache-service:d41d8cd98f00b204e9800998ecf8427e"
        val cachedResult = ServiceResult(
            success = true,
            data = mapOf("message" to "Hello, World", "timestamp" to 1234567890),
            fromCache = true,
            cacheKey = cacheKey,
            cacheExpireTime = System.currentTimeMillis() + 60000
        )

        // 第一次执行，没有缓存
        `when`(cacheManager.get(anyString())).thenReturn(null)
        val result1 = executor.execute("cache-service", mapOf("name" to "World"))

        assertTrue(result1.success)
        assertFalse(result1.fromCache)

        // 第二次执行，模拟从缓存获取
        `when`(cacheManager.get(anyString())).thenReturn(cachedResult)
        val result2 = executor.execute("cache-service", mapOf("name" to "World"))

        assertTrue(result2.success)
        assertTrue(result2.fromCache)

        // 验证缓存管理器方法调用
        verify(cacheManager, atLeastOnce()).get(anyString())
        verify(cacheManager, atLeastOnce()).put(anyString(), any(), anyLong())
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
