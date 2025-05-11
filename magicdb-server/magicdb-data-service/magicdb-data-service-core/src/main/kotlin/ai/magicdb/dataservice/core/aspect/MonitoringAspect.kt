package ai.magicdb.dataservice.core.aspect

import ai.magicdb.dataservice.api.MonitoringService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import jakarta.servlet.http.HttpServletRequest

/**
 * 监控切面
 *
 * @author magicdb
 */
@Aspect
@Component
class MonitoringAspect(
    private val monitoringService: MonitoringService
) {
    private val logger = LoggerFactory.getLogger(MonitoringAspect::class.java)

    /**
     * 监控数据服务执行
     */
    @Around("execution(* ai.magicdb.dataservice.api.DataServiceExecutor.execute(..))")
    fun monitorDataServiceExecution(joinPoint: ProceedingJoinPoint): Any? {
        // 获取服务ID
        val serviceId = joinPoint.args[0] as String

        // 获取请求信息
        val request = getCurrentRequest()
        val clientIp = request?.remoteAddr
        val userId = getUserIdFromRequest(request)

        // 记录开始时间
        val startTime = System.currentTimeMillis()

        try {
            // 执行方法
            val result = joinPoint.proceed()

            // 计算执行时间
            val executionTime = System.currentTimeMillis() - startTime

            // 记录成功调用
            monitoringService.recordServiceCall(
                serviceId = serviceId,
                executionTime = executionTime,
                success = true,
                userId = userId,
                clientIp = clientIp
            )

            return result
        } catch (e: Exception) {
            // 计算执行时间
            val executionTime = System.currentTimeMillis() - startTime

            // 记录失败调用
            monitoringService.recordServiceCall(
                serviceId = serviceId,
                executionTime = executionTime,
                success = false,
                errorMessage = e.message,
                userId = userId,
                clientIp = clientIp
            )

            // 重新抛出异常
            throw e
        }
    }

    /**
     * 获取当前请求
     */
    private fun getCurrentRequest(): HttpServletRequest? {
        return try {
            val requestAttributes = RequestContextHolder.getRequestAttributes()
            if (requestAttributes is ServletRequestAttributes) {
                requestAttributes.request
            } else {
                null
            }
        } catch (e: Exception) {
            logger.debug("获取当前请求失败: {}", e.message)
            null
        }
    }

    /**
     * 从请求中获取用户ID
     */
    private fun getUserIdFromRequest(request: HttpServletRequest?): Long? {
        if (request == null) {
            return null
        }

        // 从请求头或会话中获取用户ID
        val userIdStr = request.getHeader("X-User-Id") ?: request.getSession(false)?.getAttribute("userId")?.toString()

        return userIdStr?.toLongOrNull()
    }
}
