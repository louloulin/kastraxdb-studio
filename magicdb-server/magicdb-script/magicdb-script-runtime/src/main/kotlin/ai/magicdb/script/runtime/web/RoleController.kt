package ai.magicdb.script.runtime.web

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 角色管理控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/role")
class RoleController {
    private val logger = LoggerFactory.getLogger(RoleController::class.java)
    
    // 模拟角色存储
    private val roles = ConcurrentHashMap<String, Role>()
    
    // 模拟权限存储
    private val permissions = listOf(
        Permission("1", "系统管理", null, listOf(
            Permission("1-1", "用户管理", "1", listOf(
                Permission("1-1-1", "查看用户", "1-1", null),
                Permission("1-1-2", "创建用户", "1-1", null),
                Permission("1-1-3", "编辑用户", "1-1", null),
                Permission("1-1-4", "删除用户", "1-1", null)
            )),
            Permission("1-2", "角色管理", "1", listOf(
                Permission("1-2-1", "查看角色", "1-2", null),
                Permission("1-2-2", "创建角色", "1-2", null),
                Permission("1-2-3", "编辑角色", "1-2", null),
                Permission("1-2-4", "删除角色", "1-2", null)
            )),
            Permission("1-3", "系统配置", "1", null)
        )),
        Permission("2", "脚本管理", null, listOf(
            Permission("2-1", "查看脚本", "2", null),
            Permission("2-2", "创建脚本", "2", null),
            Permission("2-3", "编辑脚本", "2", null),
            Permission("2-4", "删除脚本", "2", null),
            Permission("2-5", "执行脚本", "2", null)
        )),
        Permission("3", "数据服务", null, listOf(
            Permission("3-1", "查看服务", "3", null),
            Permission("3-2", "创建服务", "3", null),
            Permission("3-3", "编辑服务", "3", null),
            Permission("3-4", "删除服务", "3", null),
            Permission("3-5", "执行服务", "3", null)
        ))
    )
    
    init {
        // 添加默认角色
        val adminRole = Role(
            id = "1",
            name = "管理员",
            code = "admin",
            description = "系统管理员，拥有所有权限",
            permissions = permissions.flatMap { it.getAllPermissionIds() },
            createTime = Date(),
            updateTime = Date()
        )
        roles[adminRole.id] = adminRole
        
        val userRole = Role(
            id = "2",
            name = "普通用户",
            code = "user",
            description = "普通用户，拥有基本权限",
            permissions = listOf("2-1", "2-5", "3-1", "3-5"),
            createTime = Date(),
            updateTime = Date()
        )
        roles[userRole.id] = userRole
        
        val guestRole = Role(
            id = "3",
            name = "访客",
            code = "guest",
            description = "访客，只有查看权限",
            permissions = listOf("2-1", "3-1"),
            createTime = Date(),
            updateTime = Date()
        )
        roles[guestRole.id] = guestRole
    }

    /**
     * 获取角色列表
     */
    @GetMapping
    fun getRoleList(): ResponseEntity<List<Role>> {
        logger.info("获取角色列表")
        
        val roleList = roles.values.toList()
        return ResponseEntity.ok(roleList)
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    fun getRole(@PathVariable id: String): ResponseEntity<Role> {
        logger.info("获取角色详情: {}", id)
        
        val role = roles[id] ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(role)
    }

    /**
     * 创建角色
     */
    @PostMapping
    fun createRole(@RequestBody request: RoleRequest): ResponseEntity<Role> {
        logger.info("创建角色: {}", request)
        
        // 检查角色编码是否已存在
        if (roles.values.any { it.code == request.code }) {
            return ResponseEntity.badRequest().build()
        }
        
        val now = Date()
        val role = Role(
            id = UUID.randomUUID().toString(),
            name = request.name,
            code = request.code,
            description = request.description,
            permissions = request.permissions,
            createTime = now,
            updateTime = now
        )
        
        roles[role.id] = role
        return ResponseEntity.ok(role)
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    fun updateRole(@PathVariable id: String, @RequestBody request: RoleRequest): ResponseEntity<Role> {
        logger.info("更新角色: {}, {}", id, request)
        
        val existingRole = roles[id] ?: return ResponseEntity.notFound().build()
        
        // 检查角色编码是否已被其他角色使用
        if (request.code != existingRole.code && 
            roles.values.any { it.id != id && it.code == request.code }) {
            return ResponseEntity.badRequest().build()
        }
        
        val updatedRole = existingRole.copy(
            name = request.name,
            code = request.code,
            description = request.description,
            permissions = request.permissions,
            updateTime = Date()
        )
        
        roles[id] = updatedRole
        return ResponseEntity.ok(updatedRole)
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    fun deleteRole(@PathVariable id: String): ResponseEntity<Boolean> {
        logger.info("删除角色: {}", id)
        
        if (!roles.containsKey(id)) {
            return ResponseEntity.notFound().build()
        }
        
        roles.remove(id)
        return ResponseEntity.ok(true)
    }

    /**
     * 获取权限列表
     */
    @GetMapping("/permission")
    fun getPermissionList(): ResponseEntity<List<Permission>> {
        logger.info("获取权限列表")
        
        return ResponseEntity.ok(permissions)
    }
}

/**
 * 角色实体
 */
data class Role(
    val id: String,
    val name: String,
    val code: String,
    val description: String,
    val permissions: List<String>,
    val createTime: Date,
    val updateTime: Date
)

/**
 * 角色请求
 */
data class RoleRequest(
    val name: String,
    val code: String,
    val description: String,
    val permissions: List<String>
)

/**
 * 权限实体
 */
data class Permission(
    val id: String,
    val name: String,
    val parentId: String?,
    val children: List<Permission>?
) {
    fun getAllPermissionIds(): List<String> {
        val ids = mutableListOf(id)
        children?.forEach { child ->
            ids.addAll(child.getAllPermissionIds())
        }
        return ids
    }
}
