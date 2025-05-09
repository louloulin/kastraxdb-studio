package ai.magicdb.script.runtime.web

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 用户管理控制器
 *
 * @author magicdb
 */
@RestController("scriptUserController")
@RequestMapping("/api/script/user")
class UserController {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    // 模拟用户存储
    private val users = ConcurrentHashMap<String, User>()

    init {
        // 添加默认管理员用户
        val adminUser = User(
            id = "1",
            username = "admin",
            password = "admin123", // 实际应用中应该加密存储
            name = "管理员",
            email = "admin@magicdb.ai",
            role = "admin",
            enabled = true,
            createTime = Date(),
            updateTime = Date()
        )
        users[adminUser.id] = adminUser
    }

    /**
     * 获取用户列表
     */
    @GetMapping
    fun getUserList(): ResponseEntity<List<UserResponse>> {
        logger.info("获取用户列表")

        val userList = users.values.map { it.toResponse() }
        return ResponseEntity.ok(userList)
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): ResponseEntity<UserResponse> {
        logger.info("获取用户详情: {}", id)

        val user = users[id] ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user.toResponse())
    }

    /**
     * 创建用户
     */
    @PostMapping
    fun createUser(@RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        logger.info("创建用户: {}", request)

        // 检查用户名是否已存在
        if (users.values.any { it.username == request.username }) {
            return ResponseEntity.badRequest().build()
        }

        val now = Date()
        val user = User(
            id = UUID.randomUUID().toString(),
            username = request.username,
            password = request.password ?: "", // 实际应用中应该加密存储
            name = request.name,
            email = request.email,
            role = request.role,
            enabled = request.enabled ?: true,
            createTime = now,
            updateTime = now
        )

        users[user.id] = user
        return ResponseEntity.ok(user.toResponse())
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: String, @RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        logger.info("更新用户: {}, {}", id, request)

        val existingUser = users[id] ?: return ResponseEntity.notFound().build()

        // 检查用户名是否已被其他用户使用
        if (request.username != existingUser.username &&
            users.values.any { it.id != id && it.username == request.username }) {
            return ResponseEntity.badRequest().build()
        }

        val updatedUser = existingUser.copy(
            username = request.username,
            password = request.password ?: existingUser.password,
            name = request.name,
            email = request.email,
            role = request.role,
            enabled = request.enabled ?: existingUser.enabled,
            updateTime = Date()
        )

        users[id] = updatedUser
        return ResponseEntity.ok(updatedUser.toResponse())
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: String): ResponseEntity<Boolean> {
        logger.info("删除用户: {}", id)

        if (!users.containsKey(id)) {
            return ResponseEntity.notFound().build()
        }

        users.remove(id)
        return ResponseEntity.ok(true)
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        logger.info("用户登录: {}", request.username)

        val user = users.values.find { it.username == request.username } ?: return ResponseEntity.badRequest().build()

        if (user.password != request.password) {
            return ResponseEntity.badRequest().build()
        }

        if (!user.enabled) {
            return ResponseEntity.badRequest().build()
        }

        return ResponseEntity.ok(
            LoginResponse(
                token = UUID.randomUUID().toString(),
                user = user.toResponse()
            )
        )
    }
}

/**
 * 用户实体
 */
data class User(
    val id: String,
    val username: String,
    val password: String,
    val name: String,
    val email: String,
    val role: String,
    val enabled: Boolean,
    val createTime: Date,
    val updateTime: Date
) {
    fun toResponse(): UserResponse {
        return UserResponse(
            id = id,
            username = username,
            name = name,
            email = email,
            role = role,
            enabled = enabled,
            createTime = createTime,
            updateTime = updateTime
        )
    }
}

/**
 * 用户请求
 */
data class UserRequest(
    val username: String,
    val password: String? = null,
    val name: String,
    val email: String,
    val role: String,
    val enabled: Boolean? = true
)

/**
 * 用户响应
 */
data class UserResponse(
    val id: String,
    val username: String,
    val name: String,
    val email: String,
    val role: String,
    val enabled: Boolean,
    val createTime: Date,
    val updateTime: Date
)

/**
 * 登录请求
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * 登录响应
 */
data class LoginResponse(
    val token: String,
    val user: UserResponse
)
