package ai.magicdb.dataservice.core.performance

import ai.magicdb.dataservice.api.MonitoringService
import ai.magicdb.dataservice.api.model.ServicePerformanceMetrics
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.lang.management.ManagementFactory
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct

/**
 * 性能分析器
 * 用于分析系统性能瓶颈
 *
 * @author magicdb
 */
@Component
class PerformanceAnalyzer(
    private val monitoringService: MonitoringService,
    private val performanceCollector: PerformanceCollector
) {
    private val logger = LoggerFactory.getLogger(PerformanceAnalyzer::class.java)
    
    // 性能阈值
    private val performanceThresholds = ConcurrentHashMap<String, PerformanceThreshold>()
    
    // 性能警告记录
    private val performanceWarnings = ConcurrentHashMap<String, MutableList<PerformanceWarning>>()
    
    /**
     * 初始化
     */
    @PostConstruct
    fun init() {
        // 设置默认性能阈值
        setDefaultThresholds()
        
        logger.info("性能分析器初始化完成")
    }
    
    /**
     * 设置默认性能阈值
     */
    private fun setDefaultThresholds() {
        // 响应时间阈值（毫秒）
        performanceThresholds["response_time_warning"] = PerformanceThreshold(500.0, ThresholdType.GREATER_THAN)
        performanceThresholds["response_time_critical"] = PerformanceThreshold(1000.0, ThresholdType.GREATER_THAN)
        
        // CPU使用率阈值（百分比）
        performanceThresholds["cpu_usage_warning"] = PerformanceThreshold(70.0, ThresholdType.GREATER_THAN)
        performanceThresholds["cpu_usage_critical"] = PerformanceThreshold(90.0, ThresholdType.GREATER_THAN)
        
        // 内存使用率阈值（百分比）
        performanceThresholds["memory_usage_warning"] = PerformanceThreshold(70.0, ThresholdType.GREATER_THAN)
        performanceThresholds["memory_usage_critical"] = PerformanceThreshold(90.0, ThresholdType.GREATER_THAN)
        
        // 线程数阈值
        performanceThresholds["thread_count_warning"] = PerformanceThreshold(100.0, ThresholdType.GREATER_THAN)
        performanceThresholds["thread_count_critical"] = PerformanceThreshold(200.0, ThresholdType.GREATER_THAN)
        
        // 请求率阈值（每秒请求数）
        performanceThresholds["request_rate_warning"] = PerformanceThreshold(50.0, ThresholdType.GREATER_THAN)
        performanceThresholds["request_rate_critical"] = PerformanceThreshold(100.0, ThresholdType.GREATER_THAN)
        
        // 错误率阈值（百分比）
        performanceThresholds["error_rate_warning"] = PerformanceThreshold(5.0, ThresholdType.GREATER_THAN)
        performanceThresholds["error_rate_critical"] = PerformanceThreshold(10.0, ThresholdType.GREATER_THAN)
    }
    
    /**
     * 定时分析系统性能
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    fun analyzeSystemPerformance() {
        try {
            // 收集系统性能指标
            val systemMetrics = performanceCollector.collectSystemMetrics()
            
            // 分析系统性能
            val warnings = analyzeSystemMetrics(systemMetrics)
            
            // 记录性能警告
            if (warnings.isNotEmpty()) {
                logger.warn("系统性能警告: {}", warnings)
                
                // 添加到警告记录
                warnings.forEach { warning ->
                    performanceWarnings.computeIfAbsent(warning.metricName) { mutableListOf() }.add(warning)
                }
            }
        } catch (e: Exception) {
            logger.error("分析系统性能失败", e)
        }
    }
    
    /**
     * 定时分析服务性能
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    fun analyzeServicePerformance() {
        try {
            // 获取服务性能指标
            val endTime = LocalDateTime.now()
            val startTime = endTime.minusMinutes(5)
            val serviceMetrics = monitoringService.getServicePerformanceMetrics(null, startTime, endTime)
            
            // 分析服务性能
            val warnings = mutableListOf<PerformanceWarning>()
            
            serviceMetrics.forEach { metrics ->
                warnings.addAll(analyzeServiceMetrics(metrics))
            }
            
            // 记录性能警告
            if (warnings.isNotEmpty()) {
                logger.warn("服务性能警告: {}", warnings)
                
                // 添加到警告记录
                warnings.forEach { warning ->
                    performanceWarnings.computeIfAbsent(warning.metricName) { mutableListOf() }.add(warning)
                }
            }
        } catch (e: Exception) {
            logger.error("分析服务性能失败", e)
        }
    }
    
    /**
     * 分析系统性能指标
     *
     * @param metrics 系统性能指标
     * @return 性能警告列表
     */
    fun analyzeSystemMetrics(metrics: SystemMetrics): List<PerformanceWarning> {
        val warnings = mutableListOf<PerformanceWarning>()
        
        // 分析CPU使用率
        val cpuUsage = metrics.cpuUsage
        if (checkThreshold("cpu_usage_critical", cpuUsage)) {
            warnings.add(
                PerformanceWarning(
                    metricName = "cpu_usage",
                    metricValue = cpuUsage,
                    threshold = performanceThresholds["cpu_usage_critical"]!!.value,
                    severity = WarningLevel.CRITICAL,
                    message = "CPU使用率过高: $cpuUsage%",
                    timestamp = LocalDateTime.now()
                )
            )
        } else if (checkThreshold("cpu_usage_warning", cpuUsage)) {
            warnings.add(
                PerformanceWarning(
                    metricName = "cpu_usage",
                    metricValue = cpuUsage,
                    threshold = performanceThresholds["cpu_usage_warning"]!!.value,
                    severity = WarningLevel.WARNING,
                    message = "CPU使用率较高: $cpuUsage%",
                    timestamp = LocalDateTime.now()
                )
            )
        }
        
        // 分析内存使用率
        val memoryUsage = metrics.memoryUsage
        if (checkThreshold("memory_usage_critical", memoryUsage)) {
            warnings.add(
                PerformanceWarning(
                    metricName = "memory_usage",
                    metricValue = memoryUsage,
                    threshold = performanceThresholds["memory_usage_critical"]!!.value,
                    severity = WarningLevel.CRITICAL,
                    message = "内存使用率过高: $memoryUsage%",
                    timestamp = LocalDateTime.now()
                )
            )
        } else if (checkThreshold("memory_usage_warning", memoryUsage)) {
            warnings.add(
                PerformanceWarning(
                    metricName = "memory_usage",
                    metricValue = memoryUsage,
                    threshold = performanceThresholds["memory_usage_warning"]!!.value,
                    severity = WarningLevel.WARNING,
                    message = "内存使用率较高: $memoryUsage%",
                    timestamp = LocalDateTime.now()
                )
            )
        }
        
        // 分析线程数
        val threadCount = metrics.threadCount.toDouble()
        if (checkThreshold("thread_count_critical", threadCount)) {
            warnings.add(
                PerformanceWarning(
                    metricName = "thread_count",
                    metricValue = threadCount,
                    threshold = performanceThresholds["thread_count_critical"]!!.value,
                    severity = WarningLevel.CRITICAL,
                    message = "线程数过多: $threadCount",
                    timestamp = LocalDateTime.now()
                )
            )
        } else if (checkThreshold("thread_count_warning", threadCount)) {
            warnings.add(
                PerformanceWarning(
                    metricName = "thread_count",
                    metricValue = threadCount,
                    threshold = performanceThresholds["thread_count_warning"]!!.value,
                    severity = WarningLevel.WARNING,
                    message = "线程数较多: $threadCount",
                    timestamp = LocalDateTime.now()
                )
            )
        }
        
        return warnings
    }
    
    /**
     * 分析服务性能指标
     *
     * @param metrics 服务性能指标
     * @return 性能警告列表
     */
    fun analyzeServiceMetrics(metrics: ServicePerformanceMetrics): List<PerformanceWarning> {
        val warnings = mutableListOf<PerformanceWarning>()
        
        // 分析平均响应时间
        val avgResponseTime = metrics.avgResponseTime
        if (checkThreshold("response_time_critical", avgResponseTime)) {
            warnings.add(
                PerformanceWarning(
                    metricName = "response_time",
                    metricValue = avgResponseTime,
                    threshold = performanceThresholds["response_time_critical"]!!.value,
                    severity = WarningLevel.CRITICAL,
                    message = "服务[${metrics.serviceId}]响应时间过长: ${avgResponseTime}ms",
                    timestamp = LocalDateTime.now(),
                    serviceId = metrics.serviceId
                )
            )
        } else if (checkThreshold("response_time_warning", avgResponseTime)) {
            warnings.add(
                PerformanceWarning(
                    metricName = "response_time",
                    metricValue = avgResponseTime,
                    threshold = performanceThresholds["response_time_warning"]!!.value,
                    severity = WarningLevel.WARNING,
                    message = "服务[${metrics.serviceId}]响应时间较长: ${avgResponseTime}ms",
                    timestamp = LocalDateTime.now(),
                    serviceId = metrics.serviceId
                )
            )
        }
        
        // 分析请求率
        val requestRate = metrics.requestsPerSecond
        if (checkThreshold("request_rate_critical", requestRate)) {
            warnings.add(
                PerformanceWarning(
                    metricName = "request_rate",
                    metricValue = requestRate,
                    threshold = performanceThresholds["request_rate_critical"]!!.value,
                    severity = WarningLevel.CRITICAL,
                    message = "服务[${metrics.serviceId}]请求率过高: ${requestRate}请求/秒",
                    timestamp = LocalDateTime.now(),
                    serviceId = metrics.serviceId
                )
            )
        } else if (checkThreshold("request_rate_warning", requestRate)) {
            warnings.add(
                PerformanceWarning(
                    metricName = "request_rate",
                    metricValue = requestRate,
                    threshold = performanceThresholds["request_rate_warning"]!!.value,
                    severity = WarningLevel.WARNING,
                    message = "服务[${metrics.serviceId}]请求率较高: ${requestRate}请求/秒",
                    timestamp = LocalDateTime.now(),
                    serviceId = metrics.serviceId
                )
            )
        }
        
        // 分析错误率
        val errorRate = metrics.errorRate * 100 // 转换为百分比
        if (checkThreshold("error_rate_critical", errorRate)) {
            warnings.add(
                PerformanceWarning(
                    metricName = "error_rate",
                    metricValue = errorRate,
                    threshold = performanceThresholds["error_rate_critical"]!!.value,
                    severity = WarningLevel.CRITICAL,
                    message = "服务[${metrics.serviceId}]错误率过高: ${errorRate}%",
                    timestamp = LocalDateTime.now(),
                    serviceId = metrics.serviceId
                )
            )
        } else if (checkThreshold("error_rate_warning", errorRate)) {
            warnings.add(
                PerformanceWarning(
                    metricName = "error_rate",
                    metricValue = errorRate,
                    threshold = performanceThresholds["error_rate_warning"]!!.value,
                    severity = WarningLevel.WARNING,
                    message = "服务[${metrics.serviceId}]错误率较高: ${errorRate}%",
                    timestamp = LocalDateTime.now(),
                    serviceId = metrics.serviceId
                )
            )
        }
        
        return warnings
    }
    
    /**
     * 检查阈值
     *
     * @param thresholdName 阈值名称
     * @param value 值
     * @return 是否超过阈值
     */
    private fun checkThreshold(thresholdName: String, value: Double): Boolean {
        val threshold = performanceThresholds[thresholdName] ?: return false
        
        return when (threshold.type) {
            ThresholdType.GREATER_THAN -> value > threshold.value
            ThresholdType.LESS_THAN -> value < threshold.value
            ThresholdType.EQUAL_TO -> value == threshold.value
        }
    }
    
    /**
     * 设置性能阈值
     *
     * @param name 阈值名称
     * @param value 阈值值
     * @param type 阈值类型
     */
    fun setThreshold(name: String, value: Double, type: ThresholdType) {
        performanceThresholds[name] = PerformanceThreshold(value, type)
    }
    
    /**
     * 获取性能阈值
     *
     * @param name 阈值名称
     * @return 性能阈值
     */
    fun getThreshold(name: String): PerformanceThreshold? {
        return performanceThresholds[name]
    }
    
    /**
     * 获取所有性能阈值
     *
     * @return 所有性能阈值
     */
    fun getAllThresholds(): Map<String, PerformanceThreshold> {
        return performanceThresholds.toMap()
    }
    
    /**
     * 获取性能警告
     *
     * @param metricName 指标名称
     * @param limit 限制数量
     * @return 性能警告列表
     */
    fun getWarnings(metricName: String? = null, limit: Int = 100): List<PerformanceWarning> {
        return if (metricName != null) {
            performanceWarnings[metricName]?.take(limit) ?: emptyList()
        } else {
            performanceWarnings.values.flatten().sortedByDescending { it.timestamp }.take(limit)
        }
    }
    
    /**
     * 清除性能警告
     *
     * @param metricName 指标名称
     */
    fun clearWarnings(metricName: String? = null) {
        if (metricName != null) {
            performanceWarnings.remove(metricName)
        } else {
            performanceWarnings.clear()
        }
    }
    
    /**
     * 性能阈值
     */
    data class PerformanceThreshold(
        val value: Double,
        val type: ThresholdType
    )
    
    /**
     * 阈值类型
     */
    enum class ThresholdType {
        GREATER_THAN,
        LESS_THAN,
        EQUAL_TO
    }
    
    /**
     * 性能警告
     */
    data class PerformanceWarning(
        val metricName: String,
        val metricValue: Double,
        val threshold: Double,
        val severity: WarningLevel,
        val message: String,
        val timestamp: LocalDateTime,
        val serviceId: String? = null
    )
    
    /**
     * 警告级别
     */
    enum class WarningLevel {
        INFO,
        WARNING,
        CRITICAL
    }
}
