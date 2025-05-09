package ai.magicdb.script.runtime.web

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RoleControllerTest {

    private val roleController = RoleController()

    @Test
    fun testGetRoleList() {
        // 执行测试
        val result = roleController.getRoleList()
        
        // 验证结果
        assertEquals(HttpStatus.OK, result.statusCode)
        
        val roles = result.body
        assertNotNull(roles)
        assertTrue(roles.isNotEmpty())
        assertEquals("管理员", roles[0].name)
    }

    @Test
    fun testCreateAndGetRole() {
        // 准备测试数据
        val request = RoleRequest(
            name = "测试角色",
            code = "test",
            description = "测试角色描述",
            permissions = listOf("2-1", "2-5", "3-1", "3-5")
        )
        
        // 创建角色
        val createResult = roleController.createRole(request)
        assertEquals(HttpStatus.OK, createResult.statusCode)
        
        val createdRole = createResult.body
        assertNotNull(createdRole)
        assertEquals("测试角色", createdRole.name)
        assertEquals("test", createdRole.code)
        
        // 获取角色
        val getResult = roleController.getRole(createdRole.id)
        assertEquals(HttpStatus.OK, getResult.statusCode)
        
        val retrievedRole = getResult.body
        assertNotNull(retrievedRole)
        assertEquals(createdRole.id, retrievedRole.id)
        assertEquals("测试角色", retrievedRole.name)
    }

    @Test
    fun testUpdateRole() {
        // 准备测试数据
        val createRequest = RoleRequest(
            name = "更新角色",
            code = "update",
            description = "更新角色描述",
            permissions = listOf("2-1", "2-5")
        )
        
        // 创建角色
        val createResult = roleController.createRole(createRequest)
        val createdRole = createResult.body
        assertNotNull(createdRole)
        
        // 更新角色
        val updateRequest = RoleRequest(
            name = "已更新角色",
            code = "updated",
            description = "已更新角色描述",
            permissions = listOf("2-1", "2-5", "3-1", "3-5")
        )
        
        val updateResult = roleController.updateRole(createdRole.id, updateRequest)
        assertEquals(HttpStatus.OK, updateResult.statusCode)
        
        val updatedRole = updateResult.body
        assertNotNull(updatedRole)
        assertEquals(createdRole.id, updatedRole.id)
        assertEquals("已更新角色", updatedRole.name)
        assertEquals("updated", updatedRole.code)
        assertEquals(4, updatedRole.permissions.size)
    }

    @Test
    fun testDeleteRole() {
        // 准备测试数据
        val request = RoleRequest(
            name = "删除角色",
            code = "delete",
            description = "删除角色描述",
            permissions = listOf("2-1")
        )
        
        // 创建角色
        val createResult = roleController.createRole(request)
        val createdRole = createResult.body
        assertNotNull(createdRole)
        
        // 删除角色
        val deleteResult = roleController.deleteRole(createdRole.id)
        assertEquals(HttpStatus.OK, deleteResult.statusCode)
        assertTrue(deleteResult.body == true)
        
        // 验证角色已删除
        val getResult = roleController.getRole(createdRole.id)
        assertEquals(HttpStatus.NOT_FOUND, getResult.statusCode)
        assertNull(getResult.body)
    }

    @Test
    fun testGetPermissionList() {
        // 执行测试
        val result = roleController.getPermissionList()
        
        // 验证结果
        assertEquals(HttpStatus.OK, result.statusCode)
        
        val permissions = result.body
        assertNotNull(permissions)
        assertTrue(permissions.isNotEmpty())
        assertEquals("系统管理", permissions[0].name)
        
        // 验证权限树结构
        val systemManagement = permissions[0]
        assertNotNull(systemManagement.children)
        assertTrue(systemManagement.children!!.isNotEmpty())
        
        val userManagement = systemManagement.children!![0]
        assertEquals("用户管理", userManagement.name)
        assertNotNull(userManagement.children)
        assertEquals(4, userManagement.children!!.size)
    }
}
