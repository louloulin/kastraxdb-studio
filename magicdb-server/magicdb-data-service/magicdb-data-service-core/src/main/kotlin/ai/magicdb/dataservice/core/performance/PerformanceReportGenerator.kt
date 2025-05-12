package ai.magicdb.dataservice.core.performance

import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.MonitoringRepository
import ai.magicdb.dataservice.api.MonitoringService
import ai.magicdb.dataservice.api.model.ServicePerformanceRecord
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.annotation.PostConstruct

/**
 * 性能报告生成器
 * 用于生成性能测试报告
 *
 * @author magicdb
 */
@Component
class PerformanceReportGenerator(
    private val monitoringService: MonitoringService,
    private val monitoringRepository: MonitoringRepository,
    private val dataServiceRepository: DataServiceRepository,
    private val performanceAnalyzer: PerformanceAnalyzer,
    private val performanceOptimizer: PerformanceOptimizer,
    private val stressTestTool: StressTestTool,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(PerformanceReportGenerator::class.java)

    // 报告目录
    private val reportDir = System.getProperty("user.home") + File.separator + ".magicdb" +
            File.separator + "reports" + File.separator + "performance"

    /**
     * 初始化
     */
    @PostConstruct
    fun init() {
        // 创建报告目录
        val reportDirFile = File(reportDir)
        if (!reportDirFile.exists()) {
            reportDirFile.mkdirs()
        }

        logger.info("性能报告生成器初始化完成")
    }

    /**
     * 生成系统性能报告
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 报告ID
     */
    fun generateSystemReport(startTime: LocalDateTime, endTime: LocalDateTime): String {
        try {
            // 创建报告ID
            val reportId = UUID.randomUUID().toString()

            // 创建报告对象
            val report = SystemPerformanceReport(
                id = reportId,
                startTime = startTime,
                endTime = endTime,
                generateTime = LocalDateTime.now()
            )

            // 收集性能指标
            val performanceRecords = monitoringRepository.getPerformanceRecords(
                null, startTime, endTime, 1000, 0
            )

            // 计算性能指标
            report.cpuUsage = calculateAverage(performanceRecords) { it.cpuUsage }
            report.memoryUsage = calculateAverage(performanceRecords) { it.memoryUsage }
            report.threadCount = calculateAverage(performanceRecords) { it.threadCount.toDouble() }.toInt()
            report.requestsPerSecond = calculateAverage(performanceRecords) { it.requestsPerSecond }

            // 获取性能警告
            report.warnings = performanceAnalyzer.getWarnings().map {
                PerformanceWarningInfo(
                    metricName = it.metricName,
                    metricValue = it.metricValue,
                    threshold = it.threshold,
                    severity = it.severity.name,
                    message = it.message,
                    timestamp = it.timestamp,
                    serviceId = it.serviceId
                )
            }

            // 获取优化历史
            report.optimizations = performanceOptimizer.getOptimizationHistory().map {
                OptimizationInfo(
                    strategy = it.strategy,
                    serviceId = it.serviceId,
                    metricName = it.metricName,
                    metricValue = it.metricValue,
                    action = it.action,
                    timestamp = it.timestamp
                )
            }

            // 保存报告
            saveReport(reportId, report)

            logger.info("生成系统性能报告: {}", reportId)

            return reportId
        } catch (e: Exception) {
            logger.error("生成系统性能报告失败", e)
            throw e
        }
    }

    /**
     * 生成服务性能报告
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 报告ID
     */
    fun generateServiceReport(serviceId: String, startTime: LocalDateTime, endTime: LocalDateTime): String {
        try {
            // 检查服务是否存在
            val service = dataServiceRepository.getService(serviceId)
            if (service == null) {
                throw IllegalArgumentException("服务不存在: $serviceId")
            }

            // 创建报告ID
            val reportId = UUID.randomUUID().toString()

            // 创建报告对象
            val report = ServicePerformanceReport(
                id = reportId,
                serviceId = serviceId,
                serviceName = service.name,
                startTime = startTime,
                endTime = endTime,
                generateTime = LocalDateTime.now()
            )

            // 获取服务性能指标
            val metrics = monitoringService.getServicePerformanceMetrics(serviceId, startTime, endTime)
            if (metrics.isNotEmpty()) {
                val metric = metrics[0]

                report.avgResponseTime = metric.avgResponseTime
                report.maxResponseTime = metric.maxResponseTime
                report.minResponseTime = metric.minResponseTime
                report.requestsPerSecond = metric.requestsPerSecond
                report.errorRate = 0.0 // Placeholder for errorRate
                report.percentiles = metric.percentiles
            }

            // 获取服务调用统计
            val statistics = monitoringService.getServiceCallStatistics(serviceId, startTime, endTime)
            if (statistics.isNotEmpty()) {
                val statistic = statistics[0]

                report.totalCalls = statistic.totalCalls
                report.successCalls = statistic.successCalls
                report.failedCalls = statistic.failedCalls
                report.successRate = statistic.successRate
                report.callsByHour = statistic.callsByHour
                report.callsByDay = statistic.callsByDay
            }

            // 获取服务错误统计
            val errorStatistics = monitoringService.getServiceErrorStatistics(serviceId, startTime, endTime)
            if (errorStatistics.isNotEmpty()) {
                val errorStatistic = errorStatistics[0]

                report.totalErrors = errorStatistic.totalErrors
                report.errorTypes = errorStatistic.errorTypes
                report.mostCommonErrors = errorStatistic.mostCommonErrors
                report.errorsByHour = errorStatistic.errorsByHour
                report.errorsByDay = errorStatistic.errorsByDay
            }

            // 获取性能警告
            report.warnings = performanceAnalyzer.getWarnings().filter {
                it.serviceId == serviceId
            }.map {
                PerformanceWarningInfo(
                    metricName = it.metricName,
                    metricValue = it.metricValue,
                    threshold = it.threshold,
                    severity = it.severity.name,
                    message = it.message,
                    timestamp = it.timestamp,
                    serviceId = it.serviceId
                )
            }

            // 获取优化历史
            report.optimizations = performanceOptimizer.getOptimizationHistory().filter {
                it.serviceId == serviceId
            }.map {
                OptimizationInfo(
                    strategy = it.strategy,
                    serviceId = it.serviceId,
                    metricName = it.metricName,
                    metricValue = it.metricValue,
                    action = it.action,
                    timestamp = it.timestamp
                )
            }

            // 保存报告
            saveReport(reportId, report)

            logger.info("生成服务性能报告: {}, 服务: {}", reportId, serviceId)

            return reportId
        } catch (e: Exception) {
            logger.error("生成服务性能报告失败: {}", serviceId, e)
            throw e
        }
    }

    /**
     * 生成压力测试报告
     *
     * @param testId 测试ID
     * @return 报告ID
     */
    fun generateStressTestReport(testId: String): String {
        try {
            // 获取测试状态
            val testStatus = stressTestTool.getTestStatus(testId)
            if (testStatus == null) {
                throw IllegalArgumentException("测试不存在: $testId")
            }

            // 检查测试是否完成
            if (testStatus.running) {
                throw IllegalStateException("测试尚未完成: $testId")
            }

            // 检查服务是否存在
            val service = dataServiceRepository.getService(testStatus.serviceId)
            if (service == null) {
                throw IllegalArgumentException("服务不存在: ${testStatus.serviceId}")
            }

            // 创建报告ID
            val reportId = UUID.randomUUID().toString()

            // 创建报告对象
            val report = StressTestReport(
                id = reportId,
                testId = testId,
                serviceId = testStatus.serviceId,
                serviceName = service.name,
                concurrentUsers = testStatus.concurrentUsers,
                duration = testStatus.duration,
                rampUp = testStatus.rampUp,
                startTime = testStatus.startTime,
                endTime = testStatus.endTime ?: LocalDateTime.now(),
                generateTime = LocalDateTime.now(),
                totalRequests = testStatus.totalRequests,
                successRequests = testStatus.successRequests,
                failedRequests = testStatus.failedRequests,
                totalTime = testStatus.totalTime,
                minTime = testStatus.minTime,
                maxTime = testStatus.maxTime,
                avgTime = testStatus.avgTime,
                requestsPerSecond = testStatus.requestsPerSecond,
                errorRate = testStatus.errorRate
            )

            // 保存报告
            saveReport(reportId, report)

            logger.info("生成压力测试报告: {}, 测试: {}", reportId, testId)

            return reportId
        } catch (e: Exception) {
            logger.error("生成压力测试报告失败: {}", testId, e)
            throw e
        }
    }

    /**
     * 获取报告
     *
     * @param reportId 报告ID
     * @return 报告内容
     */
    fun getReport(reportId: String): String? {
        val reportFile = File("$reportDir${File.separator}$reportId.json")
        if (!reportFile.exists()) {
            return null
        }

        return try {
            Files.readString(Paths.get(reportFile.toURI()))
        } catch (e: Exception) {
            logger.error("读取报告失败: {}", reportId, e)
            null
        }
    }

    /**
     * 获取所有报告
     *
     * @return 所有报告ID
     */
    fun getAllReports(): List<String> {
        val reportDirFile = File(reportDir)
        if (!reportDirFile.exists()) {
            return emptyList()
        }

        return reportDirFile.listFiles { file ->
            file.isFile && file.name.endsWith(".json")
        }?.map { file ->
            file.name.removeSuffix(".json")
        } ?: emptyList()
    }

    /**
     * 删除报告
     *
     * @param reportId 报告ID
     * @return 是否成功
     */
    fun deleteReport(reportId: String): Boolean {
        val reportFile = File("$reportDir${File.separator}$reportId.json")
        if (!reportFile.exists()) {
            return false
        }

        return try {
            reportFile.delete()
        } catch (e: Exception) {
            logger.error("删除报告失败: {}", reportId, e)
            false
        }
    }

    /**
     * 保存报告
     *
     * @param reportId 报告ID
     * @param report 报告对象
     */
    private fun saveReport(reportId: String, report: Any) {
        try {
            // 序列化报告
            val reportJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report)

            // 保存报告
            val reportFile = File("$reportDir${File.separator}$reportId.json")
            Files.writeString(Paths.get(reportFile.toURI()), reportJson)
        } catch (e: Exception) {
            logger.error("保存报告失败: {}", reportId, e)
            throw e
        }
    }

    /**
     * 计算平均值
     *
     * @param records 记录列表
     * @param valueExtractor 值提取器
     * @return 平均值
     */
    private fun calculateAverage(records: List<ServicePerformanceRecord>, valueExtractor: (ServicePerformanceRecord) -> Double): Double {
        if (records.isEmpty()) {
            return 0.0
        }

        val sum = records.sumOf(valueExtractor)
        return sum / records.size
    }

    /**
     * 系统性能报告
     */
    data class SystemPerformanceReport(
        val id: String,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime,
        val generateTime: LocalDateTime,
        var cpuUsage: Double = 0.0,
        var memoryUsage: Double = 0.0,
        var threadCount: Int = 0,
        var requestsPerSecond: Double = 0.0,
        var warnings: List<PerformanceWarningInfo> = emptyList(),
        var optimizations: List<OptimizationInfo> = emptyList()
    )

    /**
     * 服务性能报告
     */
    data class ServicePerformanceReport(
        val id: String,
        val serviceId: String,
        val serviceName: String,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime,
        val generateTime: LocalDateTime,
        var avgResponseTime: Double = 0.0,
        var maxResponseTime: Long = 0,
        var minResponseTime: Long = 0,
        var requestsPerSecond: Double = 0.0,
        var errorRate: Double = 0.0,
        var percentiles: Map<String, Double> = emptyMap(),
        var totalCalls: Long = 0,
        var successCalls: Long = 0,
        var failedCalls: Long = 0,
        var successRate: Double = 0.0,
        var callsByHour: Map<Int, Long> = emptyMap(),
        var callsByDay: Map<String, Long> = emptyMap(),
        var totalErrors: Long = 0,
        var errorTypes: Map<String, Long> = emptyMap(),
        var mostCommonErrors: List<Pair<String, Long>> = emptyList(),
        var errorsByHour: Map<Int, Long> = emptyMap(),
        var errorsByDay: Map<String, Long> = emptyMap(),
        var warnings: List<PerformanceWarningInfo> = emptyList(),
        var optimizations: List<OptimizationInfo> = emptyList()
    )

    /**
     * 压力测试报告
     */
    data class StressTestReport(
        val id: String,
        val testId: String,
        val serviceId: String,
        val serviceName: String,
        val concurrentUsers: Int,
        val duration: Int,
        val rampUp: Int,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime,
        val generateTime: LocalDateTime,
        val totalRequests: Long,
        val successRequests: Long,
        val failedRequests: Long,
        val totalTime: Long,
        val minTime: Long,
        val maxTime: Long,
        val avgTime: Long,
        val requestsPerSecond: Double,
        val errorRate: Double
    )

    /**
     * 性能警告信息
     */
    data class PerformanceWarningInfo(
        val metricName: String,
        val metricValue: Double,
        val threshold: Double,
        val severity: String,
        val message: String,
        val timestamp: LocalDateTime,
        val serviceId: String?
    )

    /**
     * 优化信息
     */
    data class OptimizationInfo(
        val strategy: String,
        val serviceId: String?,
        val metricName: String,
        val metricValue: Double,
        val action: String,
        val timestamp: LocalDateTime
    )
}
