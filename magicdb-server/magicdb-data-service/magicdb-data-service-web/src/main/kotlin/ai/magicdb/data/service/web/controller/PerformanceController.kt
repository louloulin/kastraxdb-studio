package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.core.performance.PerformanceAnalyzer
import ai.magicdb.data.service.core.performance.PerformanceCollector
import ai.magicdb.data.service.core.performance.PerformanceOptimizer
import ai.magicdb.data.service.core.performance.PerformanceReportGenerator
import ai.magicdb.data.service.core.performance.StressTestTool
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

/**
 * 性能控制器
 * 用于管理系统性能监控和优化
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/performance")
class PerformanceController(
    private val performanceCollector: PerformanceCollector,
    private val performanceAnalyzer: PerformanceAnalyzer,
    private val performanceOptimizer: PerformanceOptimizer,
    private val performanceReportGenerator: PerformanceReportGenerator,
    private val stressTestTool: StressTestTool
) {

    /**
     * 获取系统性能指标
     */
    @GetMapping("/metrics")
    fun getSystemMetrics(): DataResult<Any> {
        val metrics = performanceCollector.collectSystemMetrics()
        return DataResult.of(metrics)
    }

    /**
     * 获取性能警告
     *
     * @param metricName 指标名称
     * @param limit 限制数量
     */
    @GetMapping("/warnings")
    fun getWarnings(
        @RequestParam(required = false) metricName: String?,
        @RequestParam(defaultValue = "10") limit: Int
    ): ListResult<Any> {
        val warnings = performanceAnalyzer.getWarnings(metricName, limit)
        return ListResult.of(warnings)
    }

    /**
     * 清除性能警告
     *
     * @param metricName 指标名称
     */
    @DeleteMapping("/warnings")
    fun clearWarnings(@RequestParam(required = false) metricName: String?): DataResult<Any> {
        performanceAnalyzer.clearWarnings(metricName)
        return DataResult.of(true)
    }

    /**
     * 获取性能阈值
     *
     * @param name 阈值名称
     */
    @GetMapping("/thresholds/{name}")
    fun getThreshold(@PathVariable name: String): DataResult<Any> {
        val threshold = performanceAnalyzer.getThreshold(name)
        return DataResult.of(threshold)
    }

    /**
     * 获取所有性能阈值
     */
    @GetMapping("/thresholds")
    fun getAllThresholds(): DataResult<Any> {
        val thresholds = performanceAnalyzer.getAllThresholds()
        return DataResult.of(thresholds)
    }

    /**
     * 设置性能阈值
     *
     * @param name 阈值名称
     * @param value 阈值值
     * @param type 阈值类型
     */
    @PostMapping("/thresholds/{name}")
    fun setThreshold(
        @PathVariable name: String,
        @RequestParam value: Double,
        @RequestParam type: PerformanceAnalyzer.ThresholdType
    ): DataResult<Any> {
        performanceAnalyzer.setThreshold(name, value, type)
        return DataResult.of(true)
    }

    /**
     * 获取优化策略
     *
     * @param name 策略名称
     */
    @GetMapping("/strategies/{name}")
    fun getStrategy(@PathVariable name: String): DataResult<Any> {
        val strategy = performanceOptimizer.getStrategy(name)
        return DataResult.of(strategy)
    }

    /**
     * 获取所有优化策略
     */
    @GetMapping("/strategies")
    fun getAllStrategies(): ListResult<Any> {
        val strategies = performanceOptimizer.getAllStrategies()
        return ListResult.of(strategies)
    }

    /**
     * 获取优化历史
     *
     * @param limit 限制数量
     */
    @GetMapping("/optimization-history")
    fun getOptimizationHistory(@RequestParam(defaultValue = "10") limit: Int): ListResult<Any> {
        val history = performanceOptimizer.getOptimizationHistory(limit)
        return ListResult.of(history)
    }

    /**
     * 清除优化历史
     */
    @DeleteMapping("/optimization-history")
    fun clearOptimizationHistory(): DataResult<Any> {
        performanceOptimizer.clearOptimizationHistory()
        return DataResult.of(true)
    }

    /**
     * 生成系统性能报告
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @PostMapping("/reports/system")
    fun generateSystemReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime
    ): DataResult<Any> {
        val reportId = performanceReportGenerator.generateSystemReport(startTime, endTime)
        return DataResult.of(mapOf("reportId" to reportId))
    }

    /**
     * 生成服务性能报告
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @PostMapping("/reports/service/{serviceId}")
    fun generateServiceReport(
        @PathVariable serviceId: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime
    ): DataResult<Any> {
        val reportId = performanceReportGenerator.generateServiceReport(serviceId, startTime, endTime)
        return DataResult.of(mapOf("reportId" to reportId))
    }

    /**
     * 生成压力测试报告
     *
     * @param testId 测试ID
     */
    @PostMapping("/reports/stress-test/{testId}")
    fun generateStressTestReport(@PathVariable testId: String): DataResult<Any> {
        val reportId = performanceReportGenerator.generateStressTestReport(testId)
        return DataResult.of(mapOf("reportId" to reportId))
    }

    /**
     * 获取报告
     *
     * @param reportId 报告ID
     */
    @GetMapping("/reports/{reportId}")
    fun getReport(@PathVariable reportId: String): DataResult<Any> {
        val report = performanceReportGenerator.getReport(reportId)
        return DataResult.of(report)
    }

    /**
     * 获取所有报告
     */
    @GetMapping("/reports")
    fun getAllReports(): ListResult<Any> {
        val reports = performanceReportGenerator.getAllReports()
        return ListResult.of(reports)
    }

    /**
     * 删除报告
     *
     * @param reportId 报告ID
     */
    @DeleteMapping("/reports/{reportId}")
    fun deleteReport(@PathVariable reportId: String): DataResult<Any> {
        val success = performanceReportGenerator.deleteReport(reportId)
        return DataResult.of(success)
    }

    /**
     * 启动压力测试
     *
     * @param serviceId 服务ID
     * @param parameters 参数
     * @param concurrentUsers 并发用户数
     * @param duration 持续时间（秒）
     * @param rampUp 爬升时间（秒）
     */
    @PostMapping("/stress-tests")
    fun startStressTest(
        @RequestParam serviceId: String,
        @RequestParam parameters: Map<String, Any?>,
        @RequestParam concurrentUsers: Int,
        @RequestParam duration: Int,
        @RequestParam rampUp: Int
    ): DataResult<Any> {
        val testId = stressTestTool.startTest(serviceId, parameters, concurrentUsers, duration, rampUp)
        return DataResult.of(mapOf("testId" to testId))
    }

    /**
     * 停止压力测试
     *
     * @param testId 测试ID
     */
    @PostMapping("/stress-tests/{testId}/stop")
    fun stopStressTest(@PathVariable testId: String): DataResult<Any> {
        val success = stressTestTool.stopTest(testId)
        return DataResult.of(success)
    }

    /**
     * 获取压力测试状态
     *
     * @param testId 测试ID
     */
    @GetMapping("/stress-tests/{testId}")
    fun getStressTestStatus(@PathVariable testId: String): DataResult<Any> {
        val status = stressTestTool.getTestStatus(testId)
        return DataResult.of(status)
    }

    /**
     * 获取所有压力测试状态
     */
    @GetMapping("/stress-tests")
    fun getAllStressTestStatus(): ListResult<Any> {
        val statuses = stressTestTool.getAllTestStatus()
        return ListResult.of(statuses)
    }

    /**
     * 清除压力测试历史
     *
     * @param testId 测试ID
     */
    @DeleteMapping("/stress-tests/{testId}")
    fun clearStressTestHistory(@PathVariable testId: String): DataResult<Any> {
        val success = stressTestTool.clearTestHistory(testId)
        return DataResult.of(success)
    }

    /**
     * 清除所有压力测试历史
     */
    @DeleteMapping("/stress-tests")
    fun clearAllStressTestHistory(): DataResult<Any> {
        stressTestTool.clearAllTestHistory()
        return DataResult.of(true)
    }
}
