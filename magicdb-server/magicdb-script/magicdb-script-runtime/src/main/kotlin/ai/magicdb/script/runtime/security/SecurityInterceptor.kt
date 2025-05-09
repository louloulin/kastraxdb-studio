package ai.magicdb.script.runtime.security

import ai.magicdb.script.api.ApiService
import ai.magicdb.script.api.security.SecurityManager
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.HandlerInterceptor
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

/**
 * 安全拦截器
 *
 * @author magicdb
 */
class SecurityInterceptor(
    private val apiService: ApiService,
    private val securityManager: SecurityManager
) : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(SecurityInterceptor::class.java)
    private val apiPrefix = "/script-api"

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // 获取请求路径
        val path = request.requestURI

        // 检查是否是API请求
        if (path.startsWith(apiPrefix)) {
            // 获取API路径
            val apiPath = path.substring(apiPrefix.length)

            // 获取请求方法
            val method = request.method

            // 获取用户信息
            val user = request.getHeader("X-User")

            try {
                // 查找API
                val apis = apiService.getAllApis()
                val api = apis.find { it.path == apiPath && it.method.equals(method, ignoreCase = true) }

                if (api != null) {
                    // 检查API访问权限
                    if (!securityManager.checkApiAccess(api.id, user)) {
                        logger.warn("用户 {} 没有访问API {} 的权限", user, api.id)
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有访问权限")
                        return false
                    }

                    // 检查脚本执行权限
                    if (!securityManager.checkScriptExecution(api.script, api.language, user)) {
                        logger.warn("用户 {} 没有执行脚本 {} 的权限", user, api.id)
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有执行权限")
                        return false
                    }

                    // 验证请求参数
                    val parameterMap = request.parameterMap
                    for (param in api.parameters) {
                        val value = parameterMap[param.name]?.get(0)
                        if (param.required && (value == null || value.isBlank())) {
                            logger.warn("缺少必须参数: {}", param.name)
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少必须参数: ${param.name}")
                            return false
                        }

                        if (value != null && !securityManager.validateInput(value, param.type)) {
                            logger.warn("参数 {} 验证失败: {}", param.name, value)
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数 ${param.name} 验证失败")
                            return false
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("安全检查出错: {}", e.message, e)
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "安全检查出错")
                return false
            }
        }

        return true
    }
}
