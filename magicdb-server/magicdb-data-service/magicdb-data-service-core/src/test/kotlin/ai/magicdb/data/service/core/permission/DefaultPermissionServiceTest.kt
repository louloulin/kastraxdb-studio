package ai.magicdb.data.service.core.permission

import ai.magicdb.data.service.api.model.PermissionType
import ai.magicdb.data.service.api.model.ResourceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DefaultPermissionServiceTest {
    
    private lateinit var permissionService: DefaultPermissionService
    
    @BeforeEach
    fun setUp() {
        permissionService = DefaultPermissionService()
    }
    
    @Test
    fun `createRole should create a role`() {
        // 创建角色
        val role = permissionService.createRole("测试角色", "测试描述")
        
        // 验证
        assertNotNull(role)
        assertEquals("测试角色", role.name)
        assertEquals("测试描述", role.description)
        assertTrue(role.permissions.isEmpty())
    }
    
    @Test
    fun `createRole should throw exception when role name already exists`() {
        // 创建角色
        permissionService.createRole("测试角色", "测试描述")
        
        // 验证重复创建会抛出异常
        assertThrows<IllegalArgumentException> {
            permissionService.createRole("测试角色", "测试描述2")
        }
    }
    
    @Test
    fun `updateRole should update a role`() {
        // 创建角色
        val role = permissionService.createRole("测试角色", "测试描述")
        
        // 更新角色
        val updatedRole = permissionService.updateRole(role.id, "测试角色2", "测试描述2")
        
        // 验证
        assertNotNull(updatedRole)
        assertEquals("测试角色2", updatedRole.name)
        assertEquals("测试描述2", updatedRole.description)
    }
    
    @Test
    fun `updateRole should throw exception when role not found`() {
        // 验证更新不存在的角色会抛出异常
        assertThrows<IllegalArgumentException> {
            permissionService.updateRole("not-exist", "测试角色", "测试描述")
        }
    }
    
    @Test
    fun `updateRole should throw exception when role name already exists`() {
        // 创建两个角色
        val role1 = permissionService.createRole("测试角色1", "测试描述1")
        permissionService.createRole("测试角色2", "测试描述2")
        
        // 验证更新为已存在的名称会抛出异常
        assertThrows<IllegalArgumentException> {
            permissionService.updateRole(role1.id, "测试角色2", "测试描述1")
        }
    }
    
    @Test
    fun `deleteRole should delete a role`() {
        // 创建角色
        val role = permissionService.createRole("测试角色", "测试描述")
        
        // 删除角色
        val result = permissionService.deleteRole(role.id)
        
        // 验证
        assertTrue(result)
        
        // 验证角色已被删除
        val roles = permissionService.getRoles()
        assertFalse(roles.any { it.id == role.id })
    }
    
    @Test
    fun `deleteRole should return false when role not found`() {
        // 验证删除不存在的角色会返回false
        val result = permissionService.deleteRole("not-exist")
        assertFalse(result)
    }
    
    @Test
    fun `getRoles should return all roles`() {
        // 创建角色
        permissionService.createRole("测试角色1", "测试描述1")
        permissionService.createRole("测试角色2", "测试描述2")
        
        // 获取所有角色
        val roles = permissionService.getRoles()
        
        // 验证（包括预定义的3个角色）
        assertEquals(5, roles.size)
        assertTrue(roles.any { it.name == "测试角色1" })
        assertTrue(roles.any { it.name == "测试角色2" })
    }
    
    @Test
    fun `getRole should return a role with permissions`() {
        // 创建角色
        val role = permissionService.createRole("测试角色", "测试描述")
        
        // 添加权限
        permissionService.addRolePermission(role.id, ResourceType.SERVICE, "service1", PermissionType.READ)
        
        // 获取角色
        val retrievedRole = permissionService.getRole(role.id)
        
        // 验证
        assertNotNull(retrievedRole)
        assertEquals("测试角色", retrievedRole!!.name)
        assertEquals(1, retrievedRole.permissions.size)
        assertEquals(ResourceType.SERVICE, retrievedRole.permissions[0].resourceType)
        assertEquals("service1", retrievedRole.permissions[0].resourceId)
        assertEquals(PermissionType.READ, retrievedRole.permissions[0].permissionType)
    }
    
    @Test
    fun `getRole should return null when role not found`() {
        // 验证获取不存在的角色会返回null
        val role = permissionService.getRole("not-exist")
        assertEquals(null, role)
    }
    
    @Test
    fun `addRolePermission should add permission to role`() {
        // 创建角色
        val role = permissionService.createRole("测试角色", "测试描述")
        
        // 添加权限
        val result = permissionService.addRolePermission(role.id, ResourceType.SERVICE, "service1", PermissionType.READ)
        
        // 验证
        assertTrue(result)
        
        // 验证权限已添加
        val retrievedRole = permissionService.getRole(role.id)
        assertEquals(1, retrievedRole!!.permissions.size)
        assertEquals(ResourceType.SERVICE, retrievedRole.permissions[0].resourceType)
        assertEquals("service1", retrievedRole.permissions[0].resourceId)
        assertEquals(PermissionType.READ, retrievedRole.permissions[0].permissionType)
    }
    
    @Test
    fun `addRolePermission should return false when role not found`() {
        // 验证添加权限到不存在的角色会返回false
        val result = permissionService.addRolePermission("not-exist", ResourceType.SERVICE, "service1", PermissionType.READ)
        assertFalse(result)
    }
    
    @Test
    fun `removeRolePermission should remove permission from role`() {
        // 创建角色
        val role = permissionService.createRole("测试角色", "测试描述")
        
        // 添加权限
        permissionService.addRolePermission(role.id, ResourceType.SERVICE, "service1", PermissionType.READ)
        
        // 移除权限
        val result = permissionService.removeRolePermission(role.id, ResourceType.SERVICE, "service1", PermissionType.READ)
        
        // 验证
        assertTrue(result)
        
        // 验证权限已移除
        val retrievedRole = permissionService.getRole(role.id)
        assertTrue(retrievedRole!!.permissions.isEmpty())
    }
    
    @Test
    fun `removeRolePermission should return false when role not found`() {
        // 验证从不存在的角色移除权限会返回false
        val result = permissionService.removeRolePermission("not-exist", ResourceType.SERVICE, "service1", PermissionType.READ)
        assertFalse(result)
    }
    
    @Test
    fun `removeRolePermission should return false when permission not found`() {
        // 创建角色
        val role = permissionService.createRole("测试角色", "测试描述")
        
        // 验证移除不存在的权限会返回false
        val result = permissionService.removeRolePermission(role.id, ResourceType.SERVICE, "service1", PermissionType.READ)
        assertFalse(result)
    }
    
    @Test
    fun `assignRoleToUser should assign role to user`() {
        // 创建角色
        val role = permissionService.createRole("测试角色", "测试描述")
        
        // 分配角色给用户
        val result = permissionService.assignRoleToUser("user1", role.id)
        
        // 验证
        assertTrue(result)
        
        // 验证角色已分配
        val userRoles = permissionService.getUserRoles("user1")
        assertEquals(1, userRoles.size)
        assertEquals(role.id, userRoles[0].id)
    }
    
    @Test
    fun `assignRoleToUser should return false when role not found`() {
        // 验证分配不存在的角色会返回false
        val result = permissionService.assignRoleToUser("user1", "not-exist")
        assertFalse(result)
    }
    
    @Test
    fun `removeRoleFromUser should remove role from user`() {
        // 创建角色
        val role = permissionService.createRole("测试角色", "测试描述")
        
        // 分配角色给用户
        permissionService.assignRoleToUser("user1", role.id)
        
        // 移除用户角色
        val result = permissionService.removeRoleFromUser("user1", role.id)
        
        // 验证
        assertTrue(result)
        
        // 验证角色已移除
        val userRoles = permissionService.getUserRoles("user1")
        assertTrue(userRoles.isEmpty())
    }
    
    @Test
    fun `removeRoleFromUser should return false when user has no roles`() {
        // 验证从没有角色的用户移除角色会返回false
        val result = permissionService.removeRoleFromUser("user1", "role1")
        assertFalse(result)
    }
    
    @Test
    fun `removeRoleFromUser should return false when role not assigned`() {
        // 创建角色
        val role1 = permissionService.createRole("测试角色1", "测试描述1")
        val role2 = permissionService.createRole("测试角色2", "测试描述2")
        
        // 分配角色1给用户
        permissionService.assignRoleToUser("user1", role1.id)
        
        // 验证移除未分配的角色2会返回false
        val result = permissionService.removeRoleFromUser("user1", role2.id)
        assertFalse(result)
    }
    
    @Test
    fun `grantPermission should grant permission to user`() {
        // 授予用户权限
        val permission = permissionService.grantPermission("user1", ResourceType.SERVICE, "service1", PermissionType.READ)
        
        // 验证
        assertNotNull(permission)
        assertEquals("user1", permission.userId)
        assertEquals(ResourceType.SERVICE, permission.resourceType)
        assertEquals("service1", permission.resourceId)
        assertEquals(PermissionType.READ, permission.permissionType)
        
        // 验证权限已授予
        val userPermissions = permissionService.getUserPermissions("user1")
        assertEquals(1, userPermissions.size)
        assertEquals(permission.id, userPermissions[0].id)
    }
    
    @Test
    fun `grantPermission should return existing permission when already granted`() {
        // 授予用户权限
        val permission1 = permissionService.grantPermission("user1", ResourceType.SERVICE, "service1", PermissionType.READ)
        
        // 再次授予相同权限
        val permission2 = permissionService.grantPermission("user1", ResourceType.SERVICE, "service1", PermissionType.READ)
        
        // 验证返回的是同一个权限
        assertEquals(permission1.id, permission2.id)
        
        // 验证只有一个权限
        val userPermissions = permissionService.getUserPermissions("user1")
        assertEquals(1, userPermissions.size)
    }
    
    @Test
    fun `revokePermission should revoke permission from user`() {
        // 授予用户权限
        permissionService.grantPermission("user1", ResourceType.SERVICE, "service1", PermissionType.READ)
        
        // 撤销权限
        val result = permissionService.revokePermission("user1", ResourceType.SERVICE, "service1", PermissionType.READ)
        
        // 验证
        assertTrue(result)
        
        // 验证权限已撤销
        val userPermissions = permissionService.getUserPermissions("user1")
        assertTrue(userPermissions.isEmpty())
    }
    
    @Test
    fun `revokePermission should return false when permission not found`() {
        // 验证撤销不存在的权限会返回false
        val result = permissionService.revokePermission("user1", ResourceType.SERVICE, "service1", PermissionType.READ)
        assertFalse(result)
    }
    
    @Test
    fun `hasPermission should return true when user has direct permission`() {
        // 授予用户权限
        permissionService.grantPermission("user1", ResourceType.SERVICE, "service1", PermissionType.READ)
        
        // 验证用户有权限
        val result = permissionService.hasPermission("user1", ResourceType.SERVICE, "service1", PermissionType.READ)
        assertTrue(result)
    }
    
    @Test
    fun `hasPermission should return true when user has role permission`() {
        // 创建角色
        val role = permissionService.createRole("测试角色", "测试描述")
        
        // 添加权限到角色
        permissionService.addRolePermission(role.id, ResourceType.SERVICE, "service1", PermissionType.READ)
        
        // 分配角色给用户
        permissionService.assignRoleToUser("user1", role.id)
        
        // 验证用户有权限
        val result = permissionService.hasPermission("user1", ResourceType.SERVICE, "service1", PermissionType.READ)
        assertTrue(result)
    }
    
    @Test
    fun `hasPermission should return true when user has global permission`() {
        // 授予用户全局权限
        permissionService.grantPermission("user1", ResourceType.GLOBAL, "*", PermissionType.READ)
        
        // 验证用户有权限
        val result = permissionService.hasPermission("user1", ResourceType.SERVICE, "service1", PermissionType.READ)
        assertTrue(result)
    }
    
    @Test
    fun `hasPermission should return false when user has no permission`() {
        // 验证用户没有权限
        val result = permissionService.hasPermission("user1", ResourceType.SERVICE, "service1", PermissionType.READ)
        assertFalse(result)
    }
    
    @Test
    fun `getUserRoleInfo should return user role info`() {
        // 创建角色
        val role = permissionService.createRole("测试角色", "测试描述")
        
        // 添加权限到角色
        permissionService.addRolePermission(role.id, ResourceType.SERVICE, "service1", PermissionType.READ)
        
        // 分配角色给用户
        permissionService.assignRoleToUser("user1", role.id)
        
        // 授予用户直接权限
        permissionService.grantPermission("user1", ResourceType.SERVICE, "service2", PermissionType.EXECUTE)
        
        // 获取用户角色信息
        val userRoleInfo = permissionService.getUserRoleInfo("user1")
        
        // 验证
        assertEquals("user1", userRoleInfo.userId)
        assertEquals(1, userRoleInfo.roles.size)
        assertEquals(role.id, userRoleInfo.roles[0].id)
        assertEquals(2, userRoleInfo.permissions.size)
        assertTrue(userRoleInfo.permissions.any { 
            it.resourceType == ResourceType.SERVICE && it.resourceId == "service1" && it.permissionType == PermissionType.READ 
        })
        assertTrue(userRoleInfo.permissions.any { 
            it.resourceType == ResourceType.SERVICE && it.resourceId == "service2" && it.permissionType == PermissionType.EXECUTE 
        })
    }
}
