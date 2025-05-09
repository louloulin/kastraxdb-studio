package ai.magicdb.script.runtime.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * 默认安全管理器测试
 *
 * @author magicdb
 */
class DefaultSecurityManagerTest {
    
    private val securityManager = DefaultSecurityManager()
    
    @Test
    fun testCheckApiAccess() {
        // 简单实现，应该始终返回true
        assertTrue(securityManager.checkApiAccess("test-api", "user"))
        assertTrue(securityManager.checkApiAccess("test-api", null))
    }
    
    @Test
    fun testCheckScriptExecution() {
        // 安全脚本
        assertTrue(securityManager.checkScriptExecution("var x = 1 + 2; return x;", "js", "user"))
        
        // 危险脚本
        assertFalse(securityManager.checkScriptExecution("var process = require('process'); process.exit(1);", "js", "user"))
        assertFalse(securityManager.checkScriptExecution("var fs = require('fs'); fs.readFileSync('/etc/passwd');", "js", "user"))
        assertFalse(securityManager.checkScriptExecution("Runtime.getRuntime().exec('rm -rf /');", "js", "user"))
        
        // SQL注入
        assertFalse(securityManager.checkScriptExecution("db.query('SELECT * FROM users WHERE id = 1 OR 1=1');", "js", "user"))
        assertFalse(securityManager.checkScriptExecution("db.query('DROP TABLE users');", "js", "user"))
    }
    
    @Test
    fun testValidateInput() {
        // SQL类型
        assertTrue(securityManager.validateInput("SELECT * FROM users WHERE id = 1", "sql"))
        assertFalse(securityManager.validateInput("SELECT * FROM users WHERE id = 1 OR 1=1", "sql"))
        assertFalse(securityManager.validateInput("DROP TABLE users", "sql"))
        
        // 脚本类型
        assertTrue(securityManager.validateInput("var x = 1 + 2; return x;", "script"))
        assertFalse(securityManager.validateInput("var process = require('process'); process.exit(1);", "script"))
        
        // 邮箱类型
        assertTrue(securityManager.validateInput("user@example.com", "email"))
        assertFalse(securityManager.validateInput("invalid-email", "email"))
        
        // URL类型
        assertTrue(securityManager.validateInput("https://example.com", "url"))
        assertFalse(securityManager.validateInput("example.com", "url"))
    }
    
    @Test
    fun testGetSecurityContext() {
        val context = securityManager.getSecurityContext("user")
        
        assertEquals("user", context["user"])
        assertTrue(context["roles"] is List<*>)
        assertTrue(context["permissions"] is List<*>)
        
        val roles = context["roles"] as List<*>
        val permissions = context["permissions"] as List<*>
        
        assertTrue(roles.contains("user"))
        assertTrue(permissions.contains("api:read"))
        assertTrue(permissions.contains("api:execute"))
    }
}
