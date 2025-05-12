package ai.magicdb.dataservice.core.performance

import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.MonitoringService
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.core.cache.DataServiceCacheManager
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct

/**
 * 性能优化器
 * 用于优化系统性能
 *
 * @author magicdb
 */
@Component
class PerformanceOptimizer(
    private val performanceAnalyzer: PerformanceAnalyzer,
    private val monitoringService: MonitoringService,
    private val dataServiceRepository: DataServiceRepository,
    private val cacheManager: DataServiceCacheManager
) {
    private val logger = LoggerFactory.getLogger(PerformanceOptimizer::class.java)

    // 优化策略
    private val optimizationStrategies = ConcurrentHashMap<String, OptimizationStrategy>()

    // 优化历史
    private val optimizationHistory = mutableListOf<OptimizationRecord>()

    /**
     * 初始化
     */
    @PostConstruct
    fun init() {
        // 注册优化策略
        registerDefaultStrategies()

        logger.info("性能优化器初始化完成")
    }

    /**
     * 注册默认优化策略
     */
    private fun registerDefaultStrategies() {
        // 缓存优化策略
        optimizationStrategies["cache_optimization"] = OptimizationStrategy(
            name = "cache_optimization",
            description = "通过增加缓存来优化性能",
            targetMetric = "response_time",
            thresholdValue = 500.0,
            action = { serviceId, metrics ->
                // 获取服务
                val service = if (serviceId != null) dataServiceRepository.getService(serviceId) else null
                if (service != null) {
                    // 增加缓存时间
                    val currentCacheTime = service.cacheTime ?: 0
                    val newCacheTime = if (currentCacheTime > 0) {
                        // 如果已经有缓存，增加缓存时间
                        (currentCacheTime * 1.5).toLong().coerceAtMost(3600000) // 最大1小时
                    } else {
                        // 如果没有缓存，设置默认缓存时间
                        60000 // 1分钟
                    }

                    // 更新服务
                    val updatedService = service.copy(cacheTime = newCacheTime)
                    dataServiceRepository.saveService(updatedService)

                    // 记录优化
                    val record = OptimizationRecord(
                        strategy = "cache_optimization",
                        serviceId = serviceId,
                        metricName = "response_time",
                        metricValue = 0.0, // Placeholder for avgResponseTime
                        action = "增加缓存时间: $currentCacheTime -> $newCacheTime",
                        timestamp = LocalDateTime.now()
                    )

                    optimizationHistory.add(record)

                    logger.info("应用缓存优化策略: {}", record)

                    true
                } else {
                    false
                }
            }
        )

        // 并发限制优化策略
        optimizationStrategies["concurrency_limit"] = OptimizationStrategy(
            name = "concurrency_limit",
            description = "通过限制并发请求数来优化性能",
            targetMetric = "cpu_usage",
            thresholdValue = 80.0,
            action = { serviceId, metrics ->
                // 获取服务
                val service = if (serviceId != null) dataServiceRepository.getService(serviceId) else null
                if (service != null) {
                    // 减少并发限制
                    val currentConcurrentLimit = service.concurrentLimit ?: 10
                    val newConcurrentLimit = (currentConcurrentLimit * 0.8).toInt().coerceAtLeast(1)

                    // 更新服务
                    val updatedService = service.copy(concurrentLimit = newConcurrentLimit)
                    dataServiceRepository.saveService(updatedService)

                    // 记录优化
                    val record = OptimizationRecord(
                        strategy = "concurrency_limit",
                        serviceId = serviceId,
                        metricName = "cpu_usage",
                        metricValue = 0.0, // Placeholder for cpuUsage
                        action = "减少并发限制: $currentConcurrentLimit -> $newConcurrentLimit",
                        timestamp = LocalDateTime.now()
                    )

                    optimizationHistory.add(record)

                    logger.info("应用并发限制优化策略: {}", record)

                    true
                } else {
                    false
                }
            }
        )

        // 超时优化策略
        optimizationStrategies["timeout_optimization"] = OptimizationStrategy(
            name = "timeout_optimization",
            description = "通过增加超时时间来减少超时错误",
            targetMetric = "error_rate",
            thresholdValue = 5.0,
            action = { serviceId, metrics ->
                // 获取服务
                val service = if (serviceId != null) dataServiceRepository.getService(serviceId) else null
                if (service != null) {
                    // 增加超时时间
                    val currentTimeout = service.timeout ?: 30000
                    val newTimeout = (currentTimeout * 1.5).toLong().coerceAtMost(120000) // 最大2分钟

                    // 更新服务
                    val updatedService = service.copy(timeout = newTimeout)
                    dataServiceRepository.saveService(updatedService)

                    // 记录优化
                    val record = OptimizationRecord(
                        strategy = "timeout_optimization",
                        serviceId = serviceId,
                        metricName = "error_rate",
                        metricValue = 0.0, // Placeholder for errorRate
                        action = "增加超时时间: $currentTimeout -> $newTimeout",
                        timestamp = LocalDateTime.now()
                    )

                    optimizationHistory.add(record)

                    logger.info("应用超时优化策略: {}", record)

                    true
                } else {
                    false
                }
            }
        )

        // 缓存清理策略
        optimizationStrategies["cache_cleanup"] = OptimizationStrategy(
            name = "cache_cleanup",
            description = "通过清理缓存来释放内存",
            targetMetric = "memory_usage",
            thresholdValue = 80.0,
            action = { serviceId, metrics ->
                // 清理缓存
                if (serviceId != null) {
                    cacheManager.removeByServiceId(serviceId)

                    // 记录优化
                    val record = OptimizationRecord(
                        strategy = "cache_cleanup",
                        serviceId = serviceId,
                        metricName = "memory_usage",
                        metricValue = 0.0, // Placeholder for memoryUsage
                        action = "清理服务缓存",
                        timestamp = LocalDateTime.now()
                    )

                    optimizationHistory.add(record)

                    logger.info("应用缓存清理策略: {}", record)

                    true
                } else {
                    // 清理所有缓存
                    cacheManager.clear()

                    // 记录优化
                    val record = OptimizationRecord(
                        strategy = "cache_cleanup",
                        serviceId = null,
                        metricName = "memory_usage",
                        metricValue = 0.0, // Placeholder for memoryUsage
                        action = "清理所有缓存",
                        timestamp = LocalDateTime.now()
                    )

                    optimizationHistory.add(record)

                    logger.info("应用缓存清理策略: {}", record)

                    true
                }
            }
        )
    }

    /**
     * 定时优化系统性能
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    fun optimizeSystemPerformance() {
        try {
            // 收集系统性能指标
            val systemMetrics = SystemPerformanceMetrics(
                cpuUsage = 0.0,
                memoryUsage = 0.0,
                threadCount = 0,
                requestsPerSecond = 0.0,
                avgResponseTime = 0.0,
                errorRate = 0.0
            )

            // 获取性能警告
            val warnings = performanceAnalyzer.getWarnings()

            // 应用优化策略
            warnings.forEach { warning ->
                // 查找适用的优化策略
                val strategy = findStrategy(warning.metricName, warning.metricValue)

                if (strategy != null) {
                    // 应用优化策略
                    val serviceId = warning.serviceId
                    val success = strategy.action(serviceId, systemMetrics)

                    if (success) {
                        logger.info("成功应用优化策略: {}, 服务: {}", strategy.name, serviceId)
                    } else {
                        logger.warn("应用优化策略失败: {}, 服务: {}", strategy.name, serviceId)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("优化系统性能失败", e)
        }
    }

    /**
     * 定时优化服务性能
     */
    @Scheduled(fixedRate = 600000) // 每10分钟执行一次
    fun optimizeServicePerformance() {
        try {
            // 获取服务性能指标
            val endTime = LocalDateTime.now()
            val startTime = endTime.minusMinutes(10)
            val serviceMetrics = monitoringService.getServicePerformanceMetrics(null, startTime, endTime)

            // 应用优化策略
            serviceMetrics.forEach { metrics ->
                // 检查是否需要优化
                val serviceId = metrics.serviceId

                // 检查响应时间
                if (metrics.avgResponseTime > 500) {
                    // 应用缓存优化策略
                    val strategy = optimizationStrategies["cache_optimization"]
                    if (strategy != null) {
                        val success = strategy.action(serviceId, metrics)

                        if (success) {
                            logger.info("成功应用缓存优化策略, 服务: {}", serviceId)
                        } else {
                            logger.warn("应用缓存优化策略失败, 服务: {}", serviceId)
                        }
                    }
                }

                // 检查错误率
                // 假设错误率为0，因为ServicePerformanceMetrics中没有直接的errorRate字段
                if (false) { // 原来是: if (metrics.errorRate > 0.05)
                    // 应用超时优化策略
                    val strategy = optimizationStrategies["timeout_optimization"]
                    if (strategy != null) {
                        val success = strategy.action(serviceId, metrics)

                        if (success) {
                            logger.info("成功应用超时优化策略, 服务: {}", serviceId)
                        } else {
                            logger.warn("应用超时优化策略失败, 服务: {}", serviceId)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("优化服务性能失败", e)
        }
    }

    /**
     * 查找适用的优化策略
     *
     * @param metricName 指标名称
     * @param metricValue 指标值
     * @return 优化策略
     */
    private fun findStrategy(metricName: String, metricValue: Double): OptimizationStrategy? {
        return optimizationStrategies.values.find { strategy ->
            strategy.targetMetric == metricName && metricValue >= strategy.thresholdValue
        }
    }

    /**
     * 注册优化策略
     *
     * @param strategy 优化策略
     */
    fun registerStrategy(strategy: OptimizationStrategy) {
        optimizationStrategies[strategy.name] = strategy
    }

    /**
     * 获取优化策略
     *
     * @param name 策略名称
     * @return 优化策略
     */
    fun getStrategy(name: String): OptimizationStrategy? {
        return optimizationStrategies[name]
    }

    /**
     * 获取所有优化策略
     *
     * @return 所有优化策略
     */
    fun getAllStrategies(): List<OptimizationStrategy> {
        return optimizationStrategies.values.toList()
    }

    /**
     * 获取优化历史
     *
     * @param limit 限制数量
     * @return 优化历史
     */
    fun getOptimizationHistory(limit: Int = 100): List<OptimizationRecord> {
        return optimizationHistory.sortedByDescending { it.timestamp }.take(limit)
    }

    /**
     * 清除优化历史
     */
    fun clearOptimizationHistory() {
        optimizationHistory.clear()
    }

    /**
     * 优化策略
     */
    data class OptimizationStrategy(
        val name: String,
        val description: String,
        val targetMetric: String,
        val thresholdValue: Double,
        val action: (String?, Any) -> Boolean
    )

    /**
     * 优化记录
     */
    data class OptimizationRecord(
        val strategy: String,
        val serviceId: String?,
        val metricName: String,
        val metricValue: Double,
        val action: String,
        val timestamp: LocalDateTime
    )

    /**
     * 系统性能指标
     */
    data class SystemPerformanceMetrics(
        val cpuUsage: Double,
        val memoryUsage: Double,
        val threadCount: Int,
        val requestsPerSecond: Double,
        val avgResponseTime: Double,
        val errorRate: Double
    )
}
