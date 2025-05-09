package ai.magicdb.dataservice.core.integration

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceManager
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceParameter
import ai.magicdb.dataservice.core.executor.DefaultDataServiceExecutor
import ai.magicdb.dataservice.core.manager.DefaultDataServiceManager
import ai.magicdb.dataservice.core.repository.MemoryDataServiceRepository
import ai.magicdb.script.engine.GraalVMScriptExecutor
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.cache.concurrent.ConcurrentMapCacheManager

/**
 * 数据服务集成测试
 *
 * @author magicdb
 */
class DataServiceIntegrationTest {
    
    private lateinit var dataServiceManager: DataServiceManager
    private lateinit var repository: DataServiceRepository
    private lateinit var executor: DataServiceExecutor
    private lateinit var scriptExecutor: GraalVMScriptExecutor
    private lateinit var cacheManager: ConcurrentMapCacheManager
    private lateinit var objectMapper: ObjectMapper
    
    @BeforeEach
    fun setUp() {
        repository = MemoryDataServiceRepository()
        scriptExecutor = GraalVMScriptExecutor()
        cacheManager = ConcurrentMapCacheManager("dataService")
        executor = DefaultDataServiceExecutor(repository, scriptExecutor, cacheManager)
        objectMapper = ObjectMapper()
        
        dataServiceManager = DefaultDataServiceManager(repository, executor, objectMapper)
        
        // 创建测试分组
        val group = ServiceGroup(
            id = "test-group",
            name = "测试分组",
            description = "测试分组描述"
        )
        dataServiceManager.saveGroup(group)
    }
    
    @Test
    fun testCompleteWorkflow() {
        // 1. 创建数据服务
        val service = DataService(
            name = "测试数据服务",
            description = "测试数据服务描述",
            type = "query",
            script = "// js\nfunction processData(input) { return { result: input.value * 2 }; }\nreturn processData(input);",
            language = "js",
            groupId = "test-group",
            parameters = listOf(
                ServiceParameter(
                    name = "input",
                    type = "object",
                    description = "输入参数",
                    required = true
                )
            ),
            tags = listOf("test", "demo")
        )
        
        val savedService = dataServiceManager.saveService(service)
        assertNotNull(savedService.id)
        
        // 2. 执行数据服务
        val result1 = dataServiceManager.executeService(savedService.id, mapOf("input" to mapOf("value" to 10)))
        assertTrue(result1.success)
        
        @Suppress("UNCHECKED_CAST")
        val data1 = result1.data as Map<String, Any?>
        assertEquals(20, data1["result"])
        
        // 3. 更新数据服务
        val updatedService = savedService.copy(
            script = "// js\nfunction processData(input) { return { result: input.value * 3 }; }\nreturn processData(input);",
            description = "更新后的描述"
        )
        
        val savedUpdatedService = dataServiceManager.saveService(updatedService)
        
        // 4. 执行更新后的数据服务
        val result2 = dataServiceManager.executeService(savedUpdatedService.id, mapOf("input" to mapOf("value" to 10)))
        assertTrue(result2.success)
        
        @Suppress("UNCHECKED_CAST")
        val data2 = result2.data as Map<String, Any?>
        assertEquals(30, data2["result"])
        
        // 5. 测试缓存
        // 启用缓存
        val cachedService = savedUpdatedService.copy(
            cacheTime = 60000 // 1分钟缓存
        )
        
        val savedCachedService = dataServiceManager.saveService(cachedService)
        
        // 第一次执行（不使用缓存）
        val result3 = dataServiceManager.executeService(savedCachedService.id, mapOf("input" to mapOf("value" to 10)))
        assertFalse(result3.fromCache)
        
        // 第二次执行（应该使用缓存）
        val result4 = dataServiceManager.executeService(savedCachedService.id, mapOf("input" to mapOf("value" to 10)))
        assertTrue(result4.fromCache)
        
        // 验证结果相同
        @Suppress("UNCHECKED_CAST")
        val data3 = result3.data as Map<String, Any?>
        @Suppress("UNCHECKED_CAST")
        val data4 = result4.data as Map<String, Any?>
        assertEquals(data3["result"], data4["result"])
        
        // 6. 导出数据服务
        val exportData = dataServiceManager.exportService(savedCachedService.id)
        assertNotNull(exportData)
        
        // 7. 导入数据服务
        val importedService = dataServiceManager.importService(exportData)
        assertNotNull(importedService)
        assertNotEquals(savedCachedService.id, importedService.id)
        
        // 8. 执行导入的数据服务
        val result5 = dataServiceManager.executeService(importedService.id, mapOf("input" to mapOf("value" to 10)))
        assertTrue(result5.success)
        
        @Suppress("UNCHECKED_CAST")
        val data5 = result5.data as Map<String, Any?>
        assertEquals(30, data5["result"])
        
        // 9. 删除数据服务
        val deleteResult = dataServiceManager.deleteService(savedCachedService.id)
        assertTrue(deleteResult)
        assertNull(dataServiceManager.getService(savedCachedService.id))
    }
    
    @Test
    fun testServiceWithDifferentLanguages() {
        // 1. JavaScript服务
        val jsService = DataService(
            name = "JavaScript服务",
            description = "JavaScript服务描述",
            type = "query",
            script = "// js\nfunction calculate(a, b) { return a + b; }\nreturn calculate(params.a, params.b);",
            language = "js"
        )
        
        val savedJsService = dataServiceManager.saveService(jsService)
        
        // 执行JavaScript服务
        val jsResult = dataServiceManager.executeService(savedJsService.id, mapOf("params" to mapOf("a" to 10, "b" to 20)))
        assertTrue(jsResult.success)
        assertEquals(30, jsResult.data)
        
        // 2. Python服务（如果支持）
        try {
            val pythonService = DataService(
                name = "Python服务",
                description = "Python服务描述",
                type = "query",
                script = "# python\ndef calculate(a, b):\n    return a + b\n\nresult = calculate(params['a'], params['b'])\nresult",
                language = "python"
            )
            
            val savedPythonService = dataServiceManager.saveService(pythonService)
            
            // 执行Python服务
            val pythonResult = dataServiceManager.executeService(savedPythonService.id, mapOf("params" to mapOf("a" to 10, "b" to 20)))
            assertTrue(pythonResult.success)
            assertEquals(30, pythonResult.data)
        } catch (e: Exception) {
            // Python可能不支持，忽略异常
            println("Python不支持: ${e.message}")
        }
    }
    
    @Test
    fun testErrorHandling() {
        // 创建会产生错误的服务
        val errorService = DataService(
            name = "错误服务",
            description = "会产生错误的服务",
            type = "query",
            script = "// js\nfunction divide(a, b) { return a / b; }\nreturn divide(params.a, params.b);",
            language = "js"
        )
        
        val savedErrorService = dataServiceManager.saveService(errorService)
        
        // 正常执行
        val result1 = dataServiceManager.executeService(savedErrorService.id, mapOf("params" to mapOf("a" to 10, "b" to 2)))
        assertTrue(result1.success)
        assertEquals(5.0, result1.data)
        
        // 除以零错误
        val result2 = dataServiceManager.executeService(savedErrorService.id, mapOf("params" to mapOf("a" to 10, "b" to 0)))
        assertFalse(result2.success)
        assertNotNull(result2.message)
        
        // 缺少参数错误
        val result3 = dataServiceManager.executeService(savedErrorService.id, mapOf("params" to mapOf("a" to 10)))
        assertFalse(result3.success)
        assertNotNull(result3.message)
    }
}
