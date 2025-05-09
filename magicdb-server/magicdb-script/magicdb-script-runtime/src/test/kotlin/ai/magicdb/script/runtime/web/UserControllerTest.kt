package ai.magicdb.script.runtime.web

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserControllerTest {

    private val userController = UserController()

    @Test
    fun testGetUserList() {
        // 执行测试
        val result = userController.getUserList()
        
        // 验证结果
        assertEquals(HttpStatus.OK, result.statusCode)
        
        val users = result.body
        assertNotNull(users)
        assertTrue(users.isNotEmpty())
        assertEquals("管理员", users[0].name)
    }

    @Test
    fun testCreateAndGetUser() {
        // 准备测试数据
        val request = UserRequest(
            username = "testuser",
            password = "password123",
            name = "测试用户",
            email = "test@example.com",
            role = "user"
        )
        
        // 创建用户
        val createResult = userController.createUser(request)
        assertEquals(HttpStatus.OK, createResult.statusCode)
        
        val createdUser = createResult.body
        assertNotNull(createdUser)
        assertEquals("testuser", createdUser.username)
        assertEquals("测试用户", createdUser.name)
        
        // 获取用户
        val getResult = userController.getUser(createdUser.id)
        assertEquals(HttpStatus.OK, getResult.statusCode)
        
        val retrievedUser = getResult.body
        assertNotNull(retrievedUser)
        assertEquals(createdUser.id, retrievedUser.id)
        assertEquals("testuser", retrievedUser.username)
    }

    @Test
    fun testUpdateUser() {
        // 准备测试数据
        val createRequest = UserRequest(
            username = "updateuser",
            password = "password123",
            name = "更新用户",
            email = "update@example.com",
            role = "user"
        )
        
        // 创建用户
        val createResult = userController.createUser(createRequest)
        val createdUser = createResult.body
        assertNotNull(createdUser)
        
        // 更新用户
        val updateRequest = UserRequest(
            username = "updateuser",
            name = "已更新用户",
            email = "updated@example.com",
            role = "admin"
        )
        
        val updateResult = userController.updateUser(createdUser.id, updateRequest)
        assertEquals(HttpStatus.OK, updateResult.statusCode)
        
        val updatedUser = updateResult.body
        assertNotNull(updatedUser)
        assertEquals(createdUser.id, updatedUser.id)
        assertEquals("已更新用户", updatedUser.name)
        assertEquals("updated@example.com", updatedUser.email)
        assertEquals("admin", updatedUser.role)
    }

    @Test
    fun testDeleteUser() {
        // 准备测试数据
        val request = UserRequest(
            username = "deleteuser",
            password = "password123",
            name = "删除用户",
            email = "delete@example.com",
            role = "user"
        )
        
        // 创建用户
        val createResult = userController.createUser(request)
        val createdUser = createResult.body
        assertNotNull(createdUser)
        
        // 删除用户
        val deleteResult = userController.deleteUser(createdUser.id)
        assertEquals(HttpStatus.OK, deleteResult.statusCode)
        assertTrue(deleteResult.body == true)
        
        // 验证用户已删除
        val getResult = userController.getUser(createdUser.id)
        assertEquals(HttpStatus.NOT_FOUND, getResult.statusCode)
        assertNull(getResult.body)
    }

    @Test
    fun testLogin() {
        // 准备测试数据
        val createRequest = UserRequest(
            username = "loginuser",
            password = "password123",
            name = "登录用户",
            email = "login@example.com",
            role = "user"
        )
        
        // 创建用户
        val createResult = userController.createUser(createRequest)
        val createdUser = createResult.body
        assertNotNull(createdUser)
        
        // 登录
        val loginRequest = LoginRequest(
            username = "loginuser",
            password = "password123"
        )
        
        val loginResult = userController.login(loginRequest)
        assertEquals(HttpStatus.OK, loginResult.statusCode)
        
        val loginResponse = loginResult.body
        assertNotNull(loginResponse)
        assertNotNull(loginResponse.token)
        assertEquals(createdUser.id, loginResponse.user.id)
    }
}
