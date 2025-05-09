package ai.magicdb.script.runtime.service

import ai.magicdb.script.api.model.ApiGroupInfo
import ai.magicdb.script.api.model.ApiInfo
import ai.magicdb.script.engine.GraalVMScriptExecutor
import ai.magicdb.script.runtime.repository.MemoryApiRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * 默认API服务测试
 *
 * @author magicdb
 */
class DefaultApiServiceTest {
    
    private lateinit var apiService: DefaultApiService
    private lateinit var apiRepository: MemoryApiRepository
    
    @BeforeEach
    fun setUp() {
        apiRepository = MemoryApiRepository()
        apiService = DefaultApiService(apiRepository, GraalVMScriptExecutor())
        
        // 创建测试分组
        val group = ApiGroupInfo(
            id = "test-group",
            name = "测试分组",
            path = "/test",
            description = "测试分组描述"
        )
        apiRepository.saveGroup(group)
        
        // 创建测试API
        val api = ApiInfo(
            id = "test-api",
            name = "测试API",
            path = "/hello",
            method = "GET",
            groupId = "test-group",
            script = "// js\nvar message = 'Hello, ' + name;\nreturn message;",
            language = "js"
        )
        apiRepository.saveApi(api)
    }
    
    @Test
    fun testSaveApi() {
        val api = ApiInfo(
            name = "新API",
            path = "/new",
            method = "POST",
            groupId = "test-group",
            script = "// js\nreturn 'New API';",
            language = "js"
        )
        
        val savedApi = apiService.saveApi(api)
        
        assertNotNull(savedApi.id)
        assertEquals("新API", savedApi.name)
        assertEquals("/new", savedApi.path)
        assertEquals("POST", savedApi.method)
        assertEquals("test-group", savedApi.groupId)
    }
    
    @Test
    fun testGetApi() {
        val api = apiService.getApi("test-api")
        
        assertNotNull(api)
        assertEquals("test-api", api?.id)
        assertEquals("测试API", api?.name)
        assertEquals("/hello", api?.path)
        assertEquals("GET", api?.method)
        assertEquals("test-group", api?.groupId)
    }
    
    @Test
    fun testDeleteApi() {
        val result = apiService.deleteApi("test-api")
        
        assertTrue(result)
        assertNull(apiService.getApi("test-api"))
    }
    
    @Test
    fun testGetAllApis() {
        val apis = apiService.getAllApis()
        
        assertEquals(1, apis.size)
        assertEquals("test-api", apis[0].id)
    }
    
    @Test
    fun testGetApisByGroupId() {
        val apis = apiService.getApisByGroupId("test-group")
        
        assertEquals(1, apis.size)
        assertEquals("test-api", apis[0].id)
    }
    
    @Test
    fun testSaveGroup() {
        val group = ApiGroupInfo(
            name = "新分组",
            path = "/new-group",
            description = "新分组描述"
        )
        
        val savedGroup = apiService.saveGroup(group)
        
        assertNotNull(savedGroup.id)
        assertEquals("新分组", savedGroup.name)
        assertEquals("/new-group", savedGroup.path)
        assertEquals("新分组描述", savedGroup.description)
    }
    
    @Test
    fun testGetGroup() {
        val group = apiService.getGroup("test-group")
        
        assertNotNull(group)
        assertEquals("test-group", group?.id)
        assertEquals("测试分组", group?.name)
        assertEquals("/test", group?.path)
        assertEquals("测试分组描述", group?.description)
    }
    
    @Test
    fun testDeleteGroup() {
        val result = apiService.deleteGroup("test-group")
        
        assertTrue(result)
        assertNull(apiService.getGroup("test-group"))
    }
    
    @Test
    fun testGetAllGroups() {
        val groups = apiService.getAllGroups()
        
        assertEquals(1, groups.size)
        assertEquals("test-group", groups[0].id)
    }
    
    @Test
    fun testExecuteApi() {
        val result = apiService.executeApi("test-api", mapOf("name" to "World"))
        
        assertEquals("Hello, World", result)
    }
    
    @Test
    fun testExecuteApiByPath() {
        val result = apiService.executeApi("/hello", "GET", mapOf("name" to "World"))
        
        assertEquals("Hello, World", result)
    }
    
    @Test
    fun testExecuteApiNotFound() {
        assertThrows<IllegalArgumentException> {
            apiService.executeApi("non-existent", mapOf())
        }
        
        assertThrows<IllegalArgumentException> {
            apiService.executeApi("/non-existent", "GET", mapOf())
        }
    }
}
