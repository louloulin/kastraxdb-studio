package ai.magicdb.dataservice.core.security

import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.server.tools.common.exception.PermissionDeniedBusinessException
import ai.magicdb.server.tools.common.model.LoginUser
import ai.magicdb.server.tools.common.util.ContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.mockStatic
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

/**
 * 数据服务权限工具类测试
 *
 * @author magicdb
 */
@ExtendWith(MockitoExtension::class)
class DataServicePermissionUtilsTest {

    private lateinit var mockContextUtils: MockedStatic<ContextUtils>
    private lateinit var adminUser: LoginUser
    private lateinit var normalUser: LoginUser

    @BeforeEach
    fun setUp() {
        // 创建模拟用户
        adminUser = LoginUser.builder()
            .id(1L)
            .nickName("admin")
            .admin(true)
            .roleCode("ADMIN")
            .build()

        normalUser = LoginUser.builder()
            .id(2L)
            .nickName("user")
            .admin(false)
            .roleCode("USER")
            .build()

        // 模拟ContextUtils
        mockContextUtils = mockStatic(ContextUtils::class.java)
    }

    @Test
    fun testCheckServiceOperationPermission_AdminUser() {
        // 设置当前用户为管理员
        mockContextUtils.`when`<LoginUser> { ContextUtils.getLoginUser() }.thenReturn(adminUser)

        // 创建测试服务
        val service = DataService(
            id = "test-service",
            name = "测试服务",
            metadata = mapOf("createUserId" to 3L) // 创建者是其他用户
        )

        // 管理员应该有权限操作任何服务
        assertDoesNotThrow {
            DataServicePermissionUtils.checkServiceOperationPermission(service)
        }
    }

    @Test
    fun testCheckServiceOperationPermission_NormalUserOwner() {
        // 设置当前用户为普通用户
        mockContextUtils.`when`<LoginUser> { ContextUtils.getLoginUser() }.thenReturn(normalUser)

        // 创建测试服务，创建者是当前用户
        val service = DataService(
            id = "test-service",
            name = "测试服务",
            metadata = mapOf("createUserId" to 2L) // 创建者是当前用户
        )

        // 普通用户应该有权限操作自己的服务
        assertDoesNotThrow {
            DataServicePermissionUtils.checkServiceOperationPermission(service)
        }
    }

    @Test
    fun testCheckServiceOperationPermission_NormalUserNotOwner() {
        // 设置当前用户为普通用户
        mockContextUtils.`when`<LoginUser> { ContextUtils.getLoginUser() }.thenReturn(normalUser)

        // 创建测试服务，创建者是其他用户
        val service = DataService(
            id = "test-service",
            name = "测试服务",
            metadata = mapOf("createUserId" to 3L) // 创建者是其他用户
        )

        // 普通用户不应该有权限操作其他用户的服务
        assertThrows(PermissionDeniedBusinessException::class.java) {
            DataServicePermissionUtils.checkServiceOperationPermission(service)
        }
    }

    @Test
    fun testCheckServiceAccessPermission_AdminUser() {
        // 设置当前用户为管理员
        mockContextUtils.`when`<LoginUser> { ContextUtils.getLoginUser() }.thenReturn(adminUser)

        // 创建测试服务，创建者是其他用户，非公开
        val service = DataService(
            id = "test-service",
            name = "测试服务",
            metadata = mapOf(
                "createUserId" to 3L, // 创建者是其他用户
                "isPublic" to false // 非公开
            )
        )

        // 管理员应该有权限访问任何服务
        assertTrue(DataServicePermissionUtils.checkServiceAccessPermission(service))
    }

    @Test
    fun testCheckServiceAccessPermission_NormalUserPublic() {
        // 设置当前用户为普通用户
        mockContextUtils.`when`<LoginUser> { ContextUtils.getLoginUser() }.thenReturn(normalUser)

        // 创建测试服务，创建者是其他用户，公开
        val service = DataService(
            id = "test-service",
            name = "测试服务",
            metadata = mapOf(
                "createUserId" to 3L, // 创建者是其他用户
                "isPublic" to true // 公开
            )
        )

        // 普通用户应该有权限访问公开服务
        assertTrue(DataServicePermissionUtils.checkServiceAccessPermission(service))
    }

    @Test
    fun testCheckServiceAccessPermission_NormalUserNotPublic() {
        // 设置当前用户为普通用户
        mockContextUtils.`when`<LoginUser> { ContextUtils.getLoginUser() }.thenReturn(normalUser)

        // 创建测试服务，创建者是其他用户，非公开
        val service = DataService(
            id = "test-service",
            name = "测试服务",
            metadata = mapOf(
                "createUserId" to 3L, // 创建者是其他用户
                "isPublic" to false // 非公开
            )
        )

        // 普通用户不应该有权限访问非公开的其他用户的服务
        assertFalse(DataServicePermissionUtils.checkServiceAccessPermission(service))
    }

    @Test
    fun testCheckScriptExecutionPermission_AdminUser() {
        // 设置当前用户为管理员
        mockContextUtils.`when`<LoginUser> { ContextUtils.getLoginUser() }.thenReturn(adminUser)

        // 创建包含危险操作的脚本
        val script = """
            // 危险脚本
            System.exit(0);
        """.trimIndent()

        // 管理员应该有权限执行任何脚本
        assertTrue(DataServicePermissionUtils.checkScriptExecutionPermission(script, "js"))
    }

    @Test
    fun testCheckScriptExecutionPermission_NormalUserSafe() {
        // 设置当前用户为普通用户
        mockContextUtils.`when`<LoginUser> { ContextUtils.getLoginUser() }.thenReturn(normalUser)

        // 创建安全脚本
        val script = """
            // 安全脚本
            function add(a, b) {
                return a + b;
            }
            return add(1, 2);
        """.trimIndent()

        // 普通用户应该有权限执行安全脚本
        assertTrue(DataServicePermissionUtils.checkScriptExecutionPermission(script, "js"))
    }

    @Test
    fun testCheckScriptExecutionPermission_NormalUserDangerous() {
        // 设置当前用户为普通用户
        mockContextUtils.`when`<LoginUser> { ContextUtils.getLoginUser() }.thenReturn(normalUser)

        // 创建包含危险操作的脚本
        val script = """
            // 危险脚本
            System.exit(0);
        """.trimIndent()

        // 普通用户不应该有权限执行危险脚本
        assertFalse(DataServicePermissionUtils.checkScriptExecutionPermission(script, "js"))
    }

    @Test
    fun testSetCurrentUserAsCreator() {
        // 设置当前用户为普通用户
        mockContextUtils.`when`<LoginUser> { ContextUtils.getLoginUser() }.thenReturn(normalUser)

        // 创建测试服务
        val service = DataService(
            id = "test-service",
            name = "测试服务"
        )

        // 设置当前用户为创建者
        DataServicePermissionUtils.setCurrentUserAsCreator(service)

        // 验证创建者ID是否正确设置
        assertEquals(2L, service.metadata?.get("createUserId"))
    }
}
