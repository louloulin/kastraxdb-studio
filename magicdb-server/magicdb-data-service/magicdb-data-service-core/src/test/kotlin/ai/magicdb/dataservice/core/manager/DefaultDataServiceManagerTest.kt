package ai.magicdb.dataservice.core.manager

import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceResult
import ai.magicdb.dataservice.core.cache.DataServiceCacheManager
import ai.magicdb.dataservice.core.executor.DefaultDataServiceExecutor
import ai.magicdb.dataservice.core.repository.MemoryDataServiceRepository
import ai.magicdb.script.engine.GraalVMScriptExecutor
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

/**
 * 默认数据服务管理测试
 *
 * @author magicdb
 */
class DefaultDataServiceManagerTest {

    private lateinit var manager: DefaultDataServiceManager
    private lateinit var repository: MemoryDataServiceRepository
    private lateinit var executor: DefaultDataServiceExecutor
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        repository = MemoryDataServiceRepository()
        val scriptExecutor = GraalVMScriptExecutor()
        val cacheManager = mock(DataServiceCacheManager::class.java)
        executor = DefaultDataServiceExecutor(repository, scriptExecutor, cacheManager)
        objectMapper = ObjectMapper()
        manager = DefaultDataServiceManager(repository, executor, objectMapper)

        // 创建测试分组
        val group = ServiceGroup(
            id = "test-group",
            name = "测试分组",
            description = "测试分组描述"
        )
        repository.saveGroup(group)

        // 创建测试服务
        val service = DataService(
            id = "test-service",
            name = "测试服务",
            description = "测试服务描述",
            type = "query",
            script = "// js\nvar message = 'Hello, ' + name;\nreturn { message: message };",
            language = "js",
            groupId = "test-group",
            tags = listOf("test", "demo"),
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
    }

    @Test
    fun testSaveService() {
        val service = DataService(
            name = "新服务",
            description = "新服务描述",
            type = "transform",
            script = "// js\nreturn { message: 'New Service' };",
            language = "js"
        )

        val savedService = manager.saveService(service)

        assertNotNull(savedService.id)
        assertEquals("新服务", savedService.name)
        assertEquals("新服务描述", savedService.description)
        assertEquals("transform", savedService.type)
    }

    @Test
    fun testDeleteService() {
        val result = manager.deleteService("test-service")

        assertTrue(result)
        assertNull(manager.getService("test-service"))
    }

    @Test
    fun testGetService() {
        val service = manager.getService("test-service")

        assertNotNull(service)
        assertEquals("test-service", service?.id)
        assertEquals("测试服务", service?.name)
        assertEquals("测试服务描述", service?.description)
        assertEquals("query", service?.type)
        assertEquals("test-group", service?.groupId)
    }

    @Test
    fun testGetAllServices() {
        val services = manager.getAllServices()

        assertEquals(1, services.size)
        assertEquals("test-service", services[0].id)
    }

    @Test
    fun testGetServicesByGroup() {
        val services = manager.getServicesByGroup("test-group")

        assertEquals(1, services.size)
        assertEquals("test-service", services[0].id)
    }

    @Test
    fun testGetServicesByTag() {
        val services = manager.getServicesByTag("test")

        assertEquals(1, services.size)
        assertEquals("test-service", services[0].id)
    }

    @Test
    fun testSaveGroup() {
        val group = ServiceGroup(
            name = "新分组",
            description = "新分组描述"
        )

        val savedGroup = manager.saveGroup(group)

        assertNotNull(savedGroup.id)
        assertEquals("新分组", savedGroup.name)
        assertEquals("新分组描述", savedGroup.description)
    }

    @Test
    fun testDeleteGroup() {
        val result = manager.deleteGroup("test-group")

        assertTrue(result)
        assertNull(manager.getGroup("test-group"))
    }

    @Test
    fun testGetGroup() {
        val group = manager.getGroup("test-group")

        assertNotNull(group)
        assertEquals("test-group", group?.id)
        assertEquals("测试分组", group?.name)
        assertEquals("测试分组描述", group?.description)
    }

    @Test
    fun testGetAllGroups() {
        val groups = manager.getAllGroups()

        assertEquals(1, groups.size)
        assertEquals("test-group", groups[0].id)
    }

    @Test
    fun testExecuteService() {
        val result = manager.executeService("test-service", mapOf("name" to "World"))

        assertTrue(result.success)
        assertNotNull(result.data)

        @Suppress("UNCHECKED_CAST")
        val data = result.data as Map<String, Any?>
        assertEquals("Hello, World", data["message"])
    }

    @Test
    fun testExecuteScript() {
        val script = "// js\nvar message = 'Hello, ' + name;\nreturn { message: message };"
        val result = manager.executeScript(script, "js", mapOf("name" to "World"))

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
        val validResult = manager.validateScript(validScript, "js")

        assertTrue(validResult.success)

        // 无效脚本
        val invalidScript = "// js\nvar message = 'Hello, World';\nreturn message"
        val invalidResult = manager.validateScript(invalidScript, "js")

        assertFalse(invalidResult.success)
        assertNotNull(invalidResult.message)
    }

    @Test
    fun testExportService() {
        val exportData = manager.exportService("test-service")

        assertNotNull(exportData)
        assertTrue(exportData.containsKey("service"))
        assertTrue(exportData.containsKey("exportInfo"))

        @Suppress("UNCHECKED_CAST")
        val serviceData = exportData["service"] as Map<String, Any?>
        assertEquals("test-service", serviceData["id"])
        assertEquals("测试服务", serviceData["name"])
    }

    @Test
    fun testImportService() {
        // 导出服务
        val exportData = manager.exportService("test-service")

        // 导入服务
        val importedService = manager.importService(exportData)

        assertNotNull(importedService)
        assertNotEquals("test-service", importedService.id) // ID应该是新生成的
        assertEquals("测试服务", importedService.name)
        assertEquals("测试服务描述", importedService.description)
        assertEquals("query", importedService.type)
        assertEquals("test-group", importedService.groupId)
    }
}
