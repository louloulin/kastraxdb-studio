package ai.magicdb.data.service.core.permission

import ai.magicdb.data.service.api.PermissionService
import ai.magicdb.data.service.api.model.PermissionInfo
import ai.magicdb.data.service.api.model.PermissionType
import ai.magicdb.data.service.api.model.ResourceType
import ai.magicdb.data.service.api.model.RoleInfo
import ai.magicdb.data.service.api.model.UserRoleInfo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 默认权限服务实现
 */
@Service
class DefaultPermissionService : PermissionService {

    private val logger = LoggerFactory.getLogger(DefaultPermissionService::class.java)
    
    // 权限存储
    private val permissions = ConcurrentHashMap<String, PermissionInfo>()
    
    // 角色存储
    private val roles = ConcurrentHashMap<String, RoleInfo>()
    
    // 用户角色关系存储
    private val userRoles = ConcurrentHashMap<String, MutableSet<String>>()
    
    // 角色权限关系存储
    private val rolePermissions = ConcurrentHashMap<String, MutableSet<String>>()
    
    init {
        // 初始化预定义角色
        initPredefinedRoles()
    }
    
    override fun hasPermission(
        userId: String,
        resourceType: ResourceType,
        resourceId: String,
        permissionType: PermissionType
    ): Boolean {
        // 检查用户直接权限
        val userPermissions = getUserPermissions(userId)
        
        if (userPermissions.any { 
            (it.resourceType == resourceType && it.resourceId == resourceId && it.permissionType == permissionType) ||
            (it.resourceType == ResourceType.GLOBAL && it.permissionType == permissionType)
        }) {
            return true
        }
        
        // 检查用户角色权限
        val userRoleIds = userRoles[userId] ?: emptySet()
        
        for (roleId in userRoleIds) {
            val rolePermissionIds = rolePermissions[roleId] ?: emptySet()
            
            for (permissionId in rolePermissionIds) {
                val permission = permissions[permissionId] ?: continue
                
                if ((permission.resourceType == resourceType && permission.resourceId == resourceId && 
                     permission.permissionType == permissionType) ||
                    (permission.resourceType == ResourceType.GLOBAL && permission.permissionType == permissionType)) {
                    return true
                }
            }
        }
        
        return false
    }
    
    override fun getUserPermissions(userId: String): List<PermissionInfo> {
        return permissions.values.filter { it.userId == userId }
    }
    
    override fun getUserRoles(userId: String): List<RoleInfo> {
        val userRoleIds = userRoles[userId] ?: emptySet()
        return userRoleIds.mapNotNull { roles[it] }
    }
    
    override fun getUserRoleInfo(userId: String): UserRoleInfo {
        val userRolesList = getUserRoles(userId)
        val userPermissionsList = getUserPermissions(userId)
        
        // 获取角色权限
        val rolePermissionsList = mutableListOf<PermissionInfo>()
        
        for (role in userRolesList) {
            val rolePermissionIds = rolePermissions[role.id] ?: emptySet()
            
            for (permissionId in rolePermissionIds) {
                val permission = permissions[permissionId] ?: continue
                rolePermissionsList.add(permission)
            }
        }
        
        // 合并用户直接权限和角色权限
        val allPermissions = (userPermissionsList + rolePermissionsList).distinctBy { 
            "${it.resourceType}_${it.resourceId}_${it.permissionType}" 
        }
        
        return UserRoleInfo(
            userId = userId,
            username = getUsernameById(userId),
            roles = userRolesList,
            permissions = allPermissions
        )
    }
    
    override fun grantPermission(
        userId: String,
        resourceType: ResourceType,
        resourceId: String,
        permissionType: PermissionType
    ): PermissionInfo {
        // 检查是否已存在相同权限
        val existingPermission = permissions.values.find { 
            it.userId == userId && 
            it.resourceType == resourceType && 
            it.resourceId == resourceId && 
            it.permissionType == permissionType 
        }
        
        if (existingPermission != null) {
            return existingPermission
        }
        
        // 创建新权限
        val id = UUID.randomUUID().toString()
        val now = Date()
        
        val permission = PermissionInfo(
            id = id,
            userId = userId,
            username = getUsernameById(userId),
            resourceType = resourceType,
            resourceId = resourceId,
            permissionType = permissionType,
            createTime = now,
            updateTime = now
        )
        
        permissions[id] = permission
        
        logger.info("授予用户权限: {}:{}:{}:{}", userId, resourceType, resourceId, permissionType)
        
        return permission
    }
    
    override fun revokePermission(
        userId: String,
        resourceType: ResourceType,
        resourceId: String,
        permissionType: PermissionType
    ): Boolean {
        // 查找权限
        val permission = permissions.values.find { 
            it.userId == userId && 
            it.resourceType == resourceType && 
            it.resourceId == resourceId && 
            it.permissionType == permissionType 
        } ?: return false
        
        // 移除权限
        permissions.remove(permission.id)
        
        logger.info("撤销用户权限: {}:{}:{}:{}", userId, resourceType, resourceId, permissionType)
        
        return true
    }
    
    override fun createRole(name: String, description: String?): RoleInfo {
        // 检查是否已存在同名角色
        if (roles.values.any { it.name == name }) {
            throw IllegalArgumentException("角色名称已存在: $name")
        }
        
        // 创建新角色
        val id = UUID.randomUUID().toString()
        val now = Date()
        
        val role = RoleInfo(
            id = id,
            name = name,
            description = description,
            permissions = emptyList(),
            createTime = now,
            updateTime = now
        )
        
        roles[id] = role
        rolePermissions[id] = mutableSetOf()
        
        logger.info("创建角色: {}", name)
        
        return role
    }
    
    override fun updateRole(id: String, name: String, description: String?): RoleInfo {
        // 检查角色是否存在
        val existingRole = roles[id] ?: throw IllegalArgumentException("角色不存在: $id")
        
        // 检查是否已存在同名角色（排除自身）
        if (roles.values.any { it.id != id && it.name == name }) {
            throw IllegalArgumentException("角色名称已存在: $name")
        }
        
        // 更新角色
        val updatedRole = existingRole.copy(
            name = name,
            description = description,
            updateTime = Date()
        )
        
        roles[id] = updatedRole
        
        logger.info("更新角色: {}", name)
        
        return updatedRole
    }
    
    override fun deleteRole(id: String): Boolean {
        // 检查角色是否存在
        val role = roles[id] ?: return false
        
        // 移除角色
        roles.remove(id)
        
        // 移除角色权限关系
        rolePermissions.remove(id)
        
        // 移除用户角色关系
        for (userRoleSet in userRoles.values) {
            userRoleSet.remove(id)
        }
        
        logger.info("删除角色: {}", role.name)
        
        return true
    }
    
    override fun getRoles(): List<RoleInfo> {
        return roles.values.toList()
    }
    
    override fun getRole(id: String): RoleInfo? {
        val role = roles[id] ?: return null
        
        // 获取角色权限
        val rolePermissionIds = rolePermissions[id] ?: emptySet()
        val rolePermissionsList = rolePermissionIds.mapNotNull { permissions[it] }
        
        return role.copy(permissions = rolePermissionsList)
    }
    
    override fun addRolePermission(
        roleId: String,
        resourceType: ResourceType,
        resourceId: String,
        permissionType: PermissionType
    ): Boolean {
        // 检查角色是否存在
        if (!roles.containsKey(roleId)) {
            return false
        }
        
        // 创建系统用户权限
        val systemUserId = "system"
        val permission = grantPermission(systemUserId, resourceType, resourceId, permissionType)
        
        // 添加到角色权限关系
        val rolePermissionSet = rolePermissions.computeIfAbsent(roleId) { mutableSetOf() }
        rolePermissionSet.add(permission.id)
        
        logger.info("为角色添加权限: {}:{}:{}:{}", roleId, resourceType, resourceId, permissionType)
        
        return true
    }
    
    override fun removeRolePermission(
        roleId: String,
        resourceType: ResourceType,
        resourceId: String,
        permissionType: PermissionType
    ): Boolean {
        // 检查角色是否存在
        if (!roles.containsKey(roleId)) {
            return false
        }
        
        // 查找权限
        val rolePermissionIds = rolePermissions[roleId] ?: return false
        
        val permissionId = rolePermissionIds.find { permissionId ->
            val permission = permissions[permissionId] ?: return@find false
            
            permission.resourceType == resourceType && 
            permission.resourceId == resourceId && 
            permission.permissionType == permissionType
        } ?: return false
        
        // 移除角色权限关系
        rolePermissionIds.remove(permissionId)
        
        logger.info("从角色移除权限: {}:{}:{}:{}", roleId, resourceType, resourceId, permissionType)
        
        return true
    }
    
    override fun assignRoleToUser(userId: String, roleId: String): Boolean {
        // 检查角色是否存在
        if (!roles.containsKey(roleId)) {
            return false
        }
        
        // 添加用户角色关系
        val userRoleSet = userRoles.computeIfAbsent(userId) { mutableSetOf() }
        userRoleSet.add(roleId)
        
        logger.info("为用户分配角色: {}:{}", userId, roleId)
        
        return true
    }
    
    override fun removeRoleFromUser(userId: String, roleId: String): Boolean {
        // 检查用户角色关系是否存在
        val userRoleSet = userRoles[userId] ?: return false
        
        // 移除用户角色关系
        val removed = userRoleSet.remove(roleId)
        
        if (removed) {
            logger.info("从用户移除角色: {}:{}", userId, roleId)
        }
        
        return removed
    }
    
    /**
     * 初始化预定义角色
     */
    private fun initPredefinedRoles() {
        // 创建管理员角色
        val adminRole = createRole("管理员", "系统管理员，拥有所有权限")
        
        // 添加全局权限
        addRolePermission(adminRole.id, ResourceType.GLOBAL, "*", PermissionType.READ)
        addRolePermission(adminRole.id, ResourceType.GLOBAL, "*", PermissionType.EXECUTE)
        addRolePermission(adminRole.id, ResourceType.GLOBAL, "*", PermissionType.EDIT)
        addRolePermission(adminRole.id, ResourceType.GLOBAL, "*", PermissionType.DELETE)
        addRolePermission(adminRole.id, ResourceType.GLOBAL, "*", PermissionType.MANAGE)
        
        // 创建开发者角色
        val developerRole = createRole("开发者", "服务开发者，可以创建和管理自己的服务")
        
        // 添加服务权限
        addRolePermission(developerRole.id, ResourceType.SERVICE, "*", PermissionType.READ)
        addRolePermission(developerRole.id, ResourceType.SERVICE, "*", PermissionType.EXECUTE)
        addRolePermission(developerRole.id, ResourceType.SERVICE, "*", PermissionType.EDIT)
        
        // 创建用户角色
        val userRole = createRole("用户", "普通用户，可以使用服务")
        
        // 添加服务权限
        addRolePermission(userRole.id, ResourceType.SERVICE, "*", PermissionType.READ)
        addRolePermission(userRole.id, ResourceType.SERVICE, "*", PermissionType.EXECUTE)
    }
    
    /**
     * 根据用户ID获取用户名
     * 实际应用中应该从用户服务获取
     */
    private fun getUsernameById(userId: String): String {
        return when (userId) {
            "system" -> "系统"
            else -> "用户_$userId"
        }
    }
}
