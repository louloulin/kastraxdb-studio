package ai.magicdb.data.service.core.repository

import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceGroup
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * 内存数据服务存储测试
 *
 * @author magicdb
 */
class MemoryDataServiceRepositoryTest {
    
    private lateinit var repository: MemoryDataServiceRepository
    
    @BeforeEach
    fun setUp() {
        repository = MemoryDataServiceRepository()
        
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
            script = "// js\nreturn { message: 'Hello, World!' };",
            language = "js",
            groupId = "test-group",
            tags = listOf("test", "demo")
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
        
        val savedService = repository.saveService(service)
        
        assertNotNull(savedService.id)
        assertEquals("新服务", savedService.name)
        assertEquals("新服务描述", savedService.description)
        assertEquals("transform", savedService.type)
    }
    
    @Test
    fun testDeleteService() {
        val result = repository.deleteService("test-service")
        
        assertTrue(result)
        assertNull(repository.getService("test-service"))
    }
    
    @Test
    fun testGetService() {
        val service = repository.getService("test-service")
        
        assertNotNull(service)
        assertEquals("test-service", service?.id)
        assertEquals("测试服务", service?.name)
        assertEquals("测试服务描述", service?.description)
        assertEquals("query", service?.type)
        assertEquals("test-group", service?.groupId)
    }
    
    @Test
    fun testGetAllServices() {
        val services = repository.getAllServices()
        
        assertEquals(1, services.size)
        assertEquals("test-service", services[0].id)
    }
    
    @Test
    fun testGetServicesByGroup() {
        val services = repository.getServicesByGroup("test-group")
        
        assertEquals(1, services.size)
        assertEquals("test-service", services[0].id)
    }
    
    @Test
    fun testGetServicesByTag() {
        val services = repository.getServicesByTag("test")
        
        assertEquals(1, services.size)
        assertEquals("test-service", services[0].id)
    }
    
    @Test
    fun testSaveGroup() {
        val group = ServiceGroup(
            name = "新分组",
            description = "新分组描述"
        )
        
        val savedGroup = repository.saveGroup(group)
        
        assertNotNull(savedGroup.id)
        assertEquals("新分组", savedGroup.name)
        assertEquals("新分组描述", savedGroup.description)
    }
    
    @Test
    fun testDeleteGroup() {
        val result = repository.deleteGroup("test-group")
        
        assertTrue(result)
        assertNull(repository.getGroup("test-group"))
    }
    
    @Test
    fun testGetGroup() {
        val group = repository.getGroup("test-group")
        
        assertNotNull(group)
        assertEquals("test-group", group?.id)
        assertEquals("测试分组", group?.name)
        assertEquals("测试分组描述", group?.description)
    }
    
    @Test
    fun testGetAllGroups() {
        val groups = repository.getAllGroups()
        
        assertEquals(1, groups.size)
        assertEquals("test-group", groups[0].id)
    }
    
    @Test
    fun testGetChildGroups() {
        // 创建父分组
        val parentGroup = ServiceGroup(
            id = "parent-group",
            name = "父分组",
            description = "父分组描述"
        )
        repository.saveGroup(parentGroup)
        
        // 创建子分组
        val childGroup = ServiceGroup(
            id = "child-group",
            name = "子分组",
            description = "子分组描述",
            parentId = "parent-group"
        )
        repository.saveGroup(childGroup)
        
        // 测试获取子分组
        val childGroups = repository.getChildGroups("parent-group")
        
        assertEquals(1, childGroups.size)
        assertEquals("child-group", childGroups[0].id)
        
        // 测试获取根分组
        val rootGroups = repository.getChildGroups(null)
        
        assertEquals(2, rootGroups.size) // test-group 和 parent-group
    }
}
