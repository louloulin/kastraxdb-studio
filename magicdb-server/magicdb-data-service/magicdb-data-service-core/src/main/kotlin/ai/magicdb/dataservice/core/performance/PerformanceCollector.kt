package ai.magicdb.dataservice.core.performance

import ai.magicdb.dataservice.api.MonitoringRepository
import ai.magicdb.dataservice.api.model.ServicePerformanceRecord
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.lang.management.ManagementFactory
import java.time.LocalDateTime
import java.util.UUID
import javax.annotation.PostConstruct

/**
 * 性能收集器
 * 用于收集系统性能指标
 *
 * @author magicdb
 */
@Component
class PerformanceCollector(
    private val monitoringRepository: MonitoringRepository
) {
    private val logger = LoggerFactory.getLogger(PerformanceCollector::class.java)
    
    // JMX MBean
    private val operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean()
    private val runtimeMXBean = ManagementFactory.getRuntimeMXBean()
    private val memoryMXBean = ManagementFactory.getMemoryMXBean()
    private val threadMXBean = ManagementFactory.getThreadMXBean()
    
    // 上次收集时间
    private var lastCollectTime = System.currentTimeMillis()
    
    // 上次请求计数
    private var lastRequestCount = 0L
    
    // 总请求计数
    private var totalRequestCount = 0L
    
    /**
     * 初始化
     */
    @PostConstruct
    fun init() {
        logger.info("性能收集器初始化完成")
    }
    
    /**
     * 定时收集系统性能指标
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    fun collectAndSaveSystemMetrics() {
        try {
            // 收集系统性能指标
            val metrics = collectSystemMetrics()
            
            // 保存性能记录
            savePerformanceRecord(null, metrics)
            
            logger.debug("收集系统性能指标: {}", metrics)
        } catch (e: Exception) {
            logger.error("收集系统性能指标失败", e)
        }
    }
    
    /**
     * 收集系统性能指标
     *
     * @return 系统性能指标
     */
    fun collectSystemMetrics(): SystemMetrics {
        // 获取CPU使用率
        val cpuUsage = getCpuUsage()
        
        // 获取内存使用率
        val memoryUsage = getMemoryUsage()
        
        // 获取线程数
        val threadCount = threadMXBean.threadCount
        
        // 获取请求率
        val requestRate = getRequestRate()
        
        // 获取GC信息
        val gcInfo = getGcInfo()
        
        // 获取类加载信息
        val classLoadingInfo = getClassLoadingInfo()
        
        return SystemMetrics(
            cpuUsage = cpuUsage,
            memoryUsage = memoryUsage,
            threadCount = threadCount,
            requestRate = requestRate,
            gcInfo = gcInfo,
            classLoadingInfo = classLoadingInfo,
            uptime = runtimeMXBean.uptime
        )
    }
    
    /**
     * 获取CPU使用率
     *
     * @return CPU使用率（百分比）
     */
    private fun getCpuUsage(): Double {
        return try {
            val bean = operatingSystemMXBean
            
            if (bean is com.sun.management.OperatingSystemMXBean) {
                bean.processCpuLoad * 100
            } else {
                // 如果不是com.sun.management.OperatingSystemMXBean，则无法获取CPU使用率
                0.0
            }
        } catch (e: Exception) {
            logger.error("获取CPU使用率失败", e)
            0.0
        }
    }
    
    /**
     * 获取内存使用率
     *
     * @return 内存使用率（百分比）
     */
    private fun getMemoryUsage(): Double {
        return try {
            val heapMemoryUsage = memoryMXBean.heapMemoryUsage
            val used = heapMemoryUsage.used
            val max = heapMemoryUsage.max
            
            if (max > 0) {
                used.toDouble() / max * 100
            } else {
                0.0
            }
        } catch (e: Exception) {
            logger.error("获取内存使用率失败", e)
            0.0
        }
    }
    
    /**
     * 获取请求率
     *
     * @return 请求率（每秒请求数）
     */
    private fun getRequestRate(): Double {
        val now = System.currentTimeMillis()
        val elapsedTime = now - lastCollectTime
        
        if (elapsedTime <= 0) {
            return 0.0
        }
        
        val requestCount = totalRequestCount - lastRequestCount
        val requestRate = requestCount.toDouble() / (elapsedTime / 1000.0)
        
        lastCollectTime = now
        lastRequestCount = totalRequestCount
        
        return requestRate
    }
    
    /**
     * 获取GC信息
     *
     * @return GC信息
     */
    private fun getGcInfo(): Map<String, Any> {
        val gcInfo = mutableMapOf<String, Any>()
        
        try {
            val gcBeans = ManagementFactory.getGarbageCollectorMXBeans()
            
            var totalGcCount = 0L
            var totalGcTime = 0L
            
            gcBeans.forEach { gcBean ->
                val gcCount = gcBean.collectionCount
                val gcTime = gcBean.collectionTime
                
                if (gcCount > 0) {
                    totalGcCount += gcCount
                    totalGcTime += gcTime
                    
                    gcInfo["${gcBean.name}_count"] = gcCount
                    gcInfo["${gcBean.name}_time"] = gcTime
                }
            }
            
            gcInfo["total_count"] = totalGcCount
            gcInfo["total_time"] = totalGcTime
        } catch (e: Exception) {
            logger.error("获取GC信息失败", e)
        }
        
        return gcInfo
    }
    
    /**
     * 获取类加载信息
     *
     * @return 类加载信息
     */
    private fun getClassLoadingInfo(): Map<String, Any> {
        val classLoadingInfo = mutableMapOf<String, Any>()
        
        try {
            val classLoadingMXBean = ManagementFactory.getClassLoadingMXBean()
            
            classLoadingInfo["loaded_class_count"] = classLoadingMXBean.loadedClassCount
            classLoadingInfo["total_loaded_class_count"] = classLoadingMXBean.totalLoadedClassCount
            classLoadingInfo["unloaded_class_count"] = classLoadingMXBean.unloadedClassCount
        } catch (e: Exception) {
            logger.error("获取类加载信息失败", e)
        }
        
        return classLoadingInfo
    }
    
    /**
     * 记录请求
     */
    fun recordRequest() {
        totalRequestCount++
    }
    
    /**
     * 保存性能记录
     *
     * @param serviceId 服务ID
     * @param metrics 系统性能指标
     */
    private fun savePerformanceRecord(serviceId: String?, metrics: SystemMetrics) {
        try {
            // 创建性能记录
            val record = ServicePerformanceRecord(
                id = UUID.randomUUID().toString(),
                serviceId = serviceId,
                recordTime = LocalDateTime.now(),
                memoryUsage = metrics.memoryUsage,
                cpuUsage = metrics.cpuUsage,
                threadCount = metrics.threadCount,
                requestsPerSecond = metrics.requestRate,
                metadata = mapOf(
                    "gcInfo" to metrics.gcInfo,
                    "classLoadingInfo" to metrics.classLoadingInfo,
                    "uptime" to metrics.uptime
                )
            )
            
            // 保存性能记录
            monitoringRepository.savePerformanceRecord(record)
        } catch (e: Exception) {
            logger.error("保存性能记录失败", e)
        }
    }
}

/**
 * 系统性能指标
 */
data class SystemMetrics(
    val cpuUsage: Double,
    val memoryUsage: Double,
    val threadCount: Int,
    val requestRate: Double,
    val gcInfo: Map<String, Any>,
    val classLoadingInfo: Map<String, Any>,
    val uptime: Long
)
