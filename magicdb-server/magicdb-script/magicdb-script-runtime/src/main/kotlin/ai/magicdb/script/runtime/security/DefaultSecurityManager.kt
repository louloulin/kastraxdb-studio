package ai.magicdb.script.runtime.security

import ai.magicdb.script.api.security.SecurityManager
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

/**
 * 默认安全管理器实现
 *
 * @author magicdb
 */
class DefaultSecurityManager : SecurityManager {
    private val logger = LoggerFactory.getLogger(DefaultSecurityManager::class.java)
    
    // SQL注入检测模式
    private val sqlInjectionPattern = Pattern.compile(
        "(?i)\\b(select|insert|update|delete|drop|alter|create|exec|union|where)\\b.*?(?i)\\b(from|into|table|database|values|set)\\b",
        Pattern.CASE_INSENSITIVE
    )
    
    // 危险脚本检测模式
    private val dangerousScriptPattern = Pattern.compile(
        "(?i)\\b(system|exec|eval|runtime|process|file|io|network|socket|url|http|ftp)\\b",
        Pattern.CASE_INSENSITIVE
    )

    override fun checkApiAccess(apiId: String, user: String?): Boolean {
        // 简单实现，允许所有访问
        // 实际应用中应该根据用户角色和权限进行检查
        return true
    }

    override fun checkScriptExecution(script: String, language: String, user: String?): Boolean {
        // 检查脚本是否包含危险操作
        if (dangerousScriptPattern.matcher(script).find()) {
            logger.warn("脚本包含危险操作: {}", script)
            return false
        }
        
        // 检查SQL注入
        if (sqlInjectionPattern.matcher(script).find()) {
            logger.warn("脚本可能包含SQL注入: {}", script)
            return false
        }
        
        return true
    }

    override fun validateInput(input: String, type: String): Boolean {
        when (type.toLowerCase()) {
            "sql" -> {
                // 检查SQL注入
                if (sqlInjectionPattern.matcher(input).find()) {
                    logger.warn("输入可能包含SQL注入: {}", input)
                    return false
                }
            }
            "script" -> {
                // 检查脚本是否包含危险操作
                if (dangerousScriptPattern.matcher(input).find()) {
                    logger.warn("输入包含危险操作: {}", input)
                    return false
                }
            }
            "email" -> {
                // 检查邮箱格式
                val emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$")
                if (!emailPattern.matcher(input).matches()) {
                    logger.warn("输入不是有效的邮箱: {}", input)
                    return false
                }
            }
            "url" -> {
                // 检查URL格式
                val urlPattern = Pattern.compile("^(http|https)://.*$")
                if (!urlPattern.matcher(input).matches()) {
                    logger.warn("输入不是有效的URL: {}", input)
                    return false
                }
            }
        }
        
        return true
    }

    override fun getSecurityContext(user: String?): Map<String, Any?> {
        // 创建安全上下文
        val context = mutableMapOf<String, Any?>()
        
        // 添加用户信息
        context["user"] = user
        
        // 添加角色信息
        context["roles"] = listOf("user")
        
        // 添加权限信息
        context["permissions"] = listOf("api:read", "api:execute")
        
        return context
    }
}
