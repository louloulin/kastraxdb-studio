package ai.magicdb.script.runtime.limit

import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * 资源限制器
 *
 * @author magicdb
 */
class ResourceLimiter {
    private val logger = LoggerFactory.getLogger(ResourceLimiter::class.java)
    
    // 全局并发限制
    private val globalConcurrentLimit = Semaphore(100)
    
    // 服务并发限制
    private val serviceConcurrentLimits = ConcurrentHashMap<String, Semaphore>()
    
    // 服务调用计数
    private val serviceCallCounts = ConcurrentHashMap<String, AtomicInteger>()
    
    // 服务错误计数
    private val serviceErrorCounts = ConcurrentHashMap<String, AtomicInteger>()
    
    // 服务最后错误时间
    private val serviceLastErrorTimes = ConcurrentHashMap<String, AtomicLong>()
    
    // 服务断路器状态
    private val serviceCircuitBreakers = ConcurrentHashMap<String, CircuitBreakerStatus>()
    
    /**
     * 获取资源许可
     *
     * @param serviceId 服务ID
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    fun acquirePermit(serviceId: String, timeout: Long, unit: TimeUnit): Boolean {
        // 检查断路器状态
        val breakerStatus = serviceCircuitBreakers[serviceId]
        if (breakerStatus == CircuitBreakerStatus.OPEN) {
            // 断路器打开，检查是否可以尝试恢复
            val lastErrorTime = serviceLastErrorTimes[serviceId]?.get() ?: 0
            val now = System.currentTimeMillis()
            if (now - lastErrorTime > CIRCUIT_BREAKER_RESET_TIMEOUT) {
                // 尝试恢复，将断路器设置为半开状态
                serviceCircuitBreakers[serviceId] = CircuitBreakerStatus.HALF_OPEN
                logger.info("断路器半开: {}", serviceId)
            } else {
                // 断路器仍然打开，拒绝请求
                logger.warn("断路器打开，拒绝请求: {}", serviceId)
                return false
            }
        }
        
        // 尝试获取全局许可
        if (!globalConcurrentLimit.tryAcquire(timeout, unit)) {
            logger.warn("获取全局许可超时: {}", serviceId)
            return false
        }
        
        try {
            // 获取服务许可
            val serviceSemaphore = serviceConcurrentLimits.computeIfAbsent(serviceId) {
                Semaphore(10) // 默认每个服务最多10个并发
            }
            
            if (!serviceSemaphore.tryAcquire(timeout, unit)) {
                logger.warn("获取服务许可超时: {}", serviceId)
                return false
            }
            
            // 增加调用计数
            serviceCallCounts.computeIfAbsent(serviceId) { AtomicInteger(0) }.incrementAndGet()
            
            return true
        } catch (e: Exception) {
            // 释放全局许可
            globalConcurrentLimit.release()
            throw e
        }
    }
    
    /**
     * 释放资源许可
     *
     * @param serviceId 服务ID
     * @param success 是否成功
     */
    fun releasePermit(serviceId: String, success: Boolean) {
        try {
            // 释放服务许可
            serviceConcurrentLimits[serviceId]?.release()
            
            // 处理执行结果
            if (!success) {
                // 增加错误计数
                val errorCount = serviceErrorCounts.computeIfAbsent(serviceId) { AtomicInteger(0) }.incrementAndGet()
                
                // 更新最后错误时间
                serviceLastErrorTimes.computeIfAbsent(serviceId) { AtomicLong(0) }.set(System.currentTimeMillis())
                
                // 检查是否需要打开断路器
                val callCount = serviceCallCounts[serviceId]?.get() ?: 0
                if (callCount > 0 && errorCount > 0) {
                    val errorRate = errorCount.toDouble() / callCount.toDouble()
                    if (errorRate > ERROR_THRESHOLD && errorCount >= MIN_ERROR_COUNT) {
                        // 打开断路器
                        serviceCircuitBreakers[serviceId] = CircuitBreakerStatus.OPEN
                        logger.warn("断路器打开: {}, 错误率: {}", serviceId, errorRate)
                    }
                }
            } else if (serviceCircuitBreakers[serviceId] == CircuitBreakerStatus.HALF_OPEN) {
                // 半开状态下成功执行，关闭断路器
                serviceCircuitBreakers[serviceId] = CircuitBreakerStatus.CLOSED
                
                // 重置错误计数
                serviceErrorCounts[serviceId]?.set(0)
                
                logger.info("断路器关闭: {}", serviceId)
            }
        } finally {
            // 释放全局许可
            globalConcurrentLimit.release()
        }
    }
    
    /**
     * 设置服务并发限制
     *
     * @param serviceId 服务ID
     * @param limit 并发限制
     */
    fun setServiceConcurrentLimit(serviceId: String, limit: Int) {
        serviceConcurrentLimits[serviceId] = Semaphore(limit)
    }
    
    /**
     * 设置全局并发限制
     *
     * @param limit 并发限制
     */
    fun setGlobalConcurrentLimit(limit: Int) {
        val oldLimit = globalConcurrentLimit.availablePermits()
        val newSemaphore = Semaphore(limit)
        
        // 尝试释放相同数量的许可
        if (limit > oldLimit) {
            newSemaphore.acquire(oldLimit)
        }
        
        // 替换全局信号量
        val field = Semaphore::class.java.getDeclaredField("sync")
        field.isAccessible = true
        val oldSync = field.get(globalConcurrentLimit)
        field.set(globalConcurrentLimit, field.get(newSemaphore))
        
        logger.info("全局并发限制已更新: {} -> {}", oldLimit, limit)
    }
    
    /**
     * 重置服务统计
     *
     * @param serviceId 服务ID
     */
    fun resetServiceStats(serviceId: String) {
        serviceCallCounts[serviceId]?.set(0)
        serviceErrorCounts[serviceId]?.set(0)
        serviceCircuitBreakers[serviceId] = CircuitBreakerStatus.CLOSED
        logger.info("重置服务统计: {}", serviceId)
    }
    
    /**
     * 获取服务统计
     *
     * @param serviceId 服务ID
     * @return 服务统计
     */
    fun getServiceStats(serviceId: String): ServiceStats {
        val callCount = serviceCallCounts[serviceId]?.get() ?: 0
        val errorCount = serviceErrorCounts[serviceId]?.get() ?: 0
        val lastErrorTime = serviceLastErrorTimes[serviceId]?.get() ?: 0
        val breakerStatus = serviceCircuitBreakers[serviceId] ?: CircuitBreakerStatus.CLOSED
        val concurrentLimit = serviceConcurrentLimits[serviceId]?.availablePermits() ?: 10
        
        return ServiceStats(
            serviceId = serviceId,
            callCount = callCount,
            errorCount = errorCount,
            lastErrorTime = lastErrorTime,
            circuitBreakerStatus = breakerStatus,
            concurrentLimit = concurrentLimit
        )
    }
    
    /**
     * 获取所有服务统计
     *
     * @return 所有服务统计
     */
    fun getAllServiceStats(): Map<String, ServiceStats> {
        val result = mutableMapOf<String, ServiceStats>()
        
        // 合并所有服务ID
        val serviceIds = mutableSetOf<String>()
        serviceIds.addAll(serviceCallCounts.keys)
        serviceIds.addAll(serviceErrorCounts.keys)
        serviceIds.addAll(serviceLastErrorTimes.keys)
        serviceIds.addAll(serviceCircuitBreakers.keys)
        serviceIds.addAll(serviceConcurrentLimits.keys)
        
        // 获取每个服务的统计
        for (serviceId in serviceIds) {
            result[serviceId] = getServiceStats(serviceId)
        }
        
        return result
    }
    
    companion object {
        // 错误阈值，超过此阈值将打开断路器
        private const val ERROR_THRESHOLD = 0.5
        
        // 最小错误数，低于此数量不会打开断路器
        private const val MIN_ERROR_COUNT = 5
        
        // 断路器重置超时时间（毫秒）
        private const val CIRCUIT_BREAKER_RESET_TIMEOUT = 60000L
    }
}
