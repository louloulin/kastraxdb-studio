package ai.magicdb.dataservice.core.monitoring

import ai.magicdb.dataservice.api.MonitoringRepository
import ai.magicdb.dataservice.api.model.ServicePerformanceMetrics
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.lang.management.ManagementFactory
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 系统资源监控器
 * 
 * 用于监控系统资源使用情况，包括CPU和内存使用情况
 *
 * @author magicdb
 */
@Component
class SystemResourceMonitor(
    private val monitoringRepository: MonitoringRepository
) {
    private val logger = LoggerFactory.getLogger(SystemResourceMonitor::class.java)
    
    // 服务性能指标缓存
    private val servicePerformanceCache = ConcurrentHashMap<String, ServicePerformanceMetrics>()
    
    // JMX相关对象
    private val runtime = Runtime.getRuntime()
    private val memoryMXBean = ManagementFactory.getMemoryMXBean()
    private val threadMXBean = ManagementFactory.getThreadMXBean()
    private val operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean()
    
    /**
     * 定时收集系统资源使用情况
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60000)
    fun collectSystemResourceUsage() {
        try {
            // 收集系统资源使用情况
            val memoryUsage = getMemoryUsage()
            val cpuUsage = getCpuUsage()
            val threadCount = getThreadCount()
            val activeConnections = getActiveConnections()
            
            // 创建系统性能指标
            val systemMetrics = ServicePerformanceMetrics(
                serviceId = "system",
                serviceName = "System",
                avgResponseTime = 0.0,
                maxResponseTime = 0,
                minResponseTime = 0,
                percentiles = emptyMap(),
                requestsPerSecond = 0.0,
                concurrentUsers = 0,
                memoryUsage = memoryUsage,
                cpuUsage = cpuUsage,
                startTime = LocalDateTime.now().minusMinutes(1),
                endTime = LocalDateTime.now(),
                responseTimeByTimeSlot = emptyMap(),
                requestsByTimeSlot = emptyMap()
            )
            
            // 更新缓存
            servicePerformanceCache["system"] = systemMetrics
            
            // 记录到数据库
            savePerformanceMetrics(systemMetrics)
            
            logger.debug("系统资源使用情况：内存 {}MB，CPU {}%，线程数 {}，活跃连接数 {}", 
                memoryUsage, cpuUsage, threadCount, activeConnections)
        } catch (e: Exception) {
            logger.error("收集系统资源使用情况失败", e)
        }
    }
    
    /**
     * 获取内存使用情况（MB）
     */
    fun getMemoryUsage(): Double {
        val totalMemory = runtime.totalMemory().toDouble() / (1024 * 1024)
        val freeMemory = runtime.freeMemory().toDouble() / (1024 * 1024)
        return totalMemory - freeMemory
    }
    
    /**
     * 获取CPU使用情况（%）
     */
    fun getCpuUsage(): Double {
        return try {
            val processCpuLoad = ManagementFactory.getPlatformMXBean(
                com.sun.management.OperatingSystemMXBean::class.java
            ).processCpuLoad * 100
            
            if (processCpuLoad < 0) 0.0 else processCpuLoad
        } catch (e: Exception) {
            logger.warn("获取CPU使用情况失败", e)
            0.0
        }
    }
    
    /**
     * 获取线程数
     */
    fun getThreadCount(): Int {
        return threadMXBean.threadCount
    }
    
    /**
     * 获取活跃连接数
     * 目前简化实现，返回0
     */
    fun getActiveConnections(): Int {
        return 0
    }
    
    /**
     * 保存性能指标
     */
    private fun savePerformanceMetrics(metrics: ServicePerformanceMetrics) {
        try {
            // 创建性能指标记录
            val record = ai.magicdb.dataservice.api.model.ServicePerformanceRecord(
                id = UUID.randomUUID().toString(),
                serviceId = metrics.serviceId,
                serviceName = metrics.serviceName,
                recordTime = LocalDateTime.now(),
                memoryUsage = metrics.memoryUsage,
                cpuUsage = metrics.cpuUsage,
                threadCount = getThreadCount(),
                activeConnections = getActiveConnections(),
                requestsPerSecond = metrics.requestsPerSecond,
                avgResponseTime = metrics.avgResponseTime,
                metadata = mapOf(
                    "percentiles" to metrics.percentiles,
                    "responseTimeByTimeSlot" to metrics.responseTimeByTimeSlot,
                    "requestsByTimeSlot" to metrics.requestsByTimeSlot
                )
            )
            
            // 保存记录
            monitoringRepository.savePerformanceRecord(record)
        } catch (e: Exception) {
            logger.error("保存性能指标失败", e)
        }
    }
    
    /**
     * 获取服务性能指标
     */
    fun getServicePerformanceMetrics(serviceId: String): ServicePerformanceMetrics? {
        return servicePerformanceCache[serviceId]
    }
    
    /**
     * 更新服务性能指标
     */
    fun updateServicePerformanceMetrics(metrics: ServicePerformanceMetrics) {
        servicePerformanceCache[metrics.serviceId] = metrics
        savePerformanceMetrics(metrics)
    }
}
