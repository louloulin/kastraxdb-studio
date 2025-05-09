package ai.magicdb.script.runtime.limit

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * 资源限制器管理器
 *
 * @author magicdb
 */
@Component
class ResourceLimiterManager {
    private val logger = LoggerFactory.getLogger(ResourceLimiterManager::class.java)
    private val resourceLimiter = ResourceLimiter()
    
    /**
     * 执行带资源限制的操作
     *
     * @param serviceId 服务ID
     * @param timeout 超时时间
     * @param unit 时间单位
     * @param action 执行操作
     * @return 执行结果
     */
    fun <T> executeWithLimit(
        serviceId: String,
        timeout: Long,
        unit: TimeUnit,
        action: () -> T
    ): T {
        // 获取资源许可
        if (!resourceLimiter.acquirePermit(serviceId, timeout, unit)) {
            throw ResourceLimitException("资源限制，无法执行服务: $serviceId")
        }
        
        var success = false
        try {
            // 执行操作
            val result = action()
            success = true
            return result
        } finally {
            // 释放资源许可
            resourceLimiter.releasePermit(serviceId, success)
        }
    }
    
    /**
     * 设置服务并发限制
     *
     * @param serviceId 服务ID
     * @param limit 并发限制
     */
    fun setServiceConcurrentLimit(serviceId: String, limit: Int) {
        resourceLimiter.setServiceConcurrentLimit(serviceId, limit)
    }
    
    /**
     * 设置全局并发限制
     *
     * @param limit 并发限制
     */
    fun setGlobalConcurrentLimit(limit: Int) {
        resourceLimiter.setGlobalConcurrentLimit(limit)
    }
    
    /**
     * 重置服务统计
     *
     * @param serviceId 服务ID
     */
    fun resetServiceStats(serviceId: String) {
        resourceLimiter.resetServiceStats(serviceId)
    }
    
    /**
     * 获取服务统计
     *
     * @param serviceId 服务ID
     * @return 服务统计
     */
    fun getServiceStats(serviceId: String): ServiceStats {
        return resourceLimiter.getServiceStats(serviceId)
    }
    
    /**
     * 获取所有服务统计
     *
     * @return 所有服务统计
     */
    fun getAllServiceStats(): Map<String, ServiceStats> {
        return resourceLimiter.getAllServiceStats()
    }
}
