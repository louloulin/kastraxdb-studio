package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.core.performance.PerformanceAnalyzer
import ai.magicdb.dataservice.core.performance.PerformanceCollector
import ai.magicdb.dataservice.core.performance.PerformanceOptimizer
import ai.magicdb.dataservice.core.performance.PerformanceReportGenerator
import ai.magicdb.dataservice.core.performance.StressTestTool
import ai.magicdb.dataservice.core.performance.SystemMetrics
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

/**
 * 性能控制器
 * 用于提供性能相关的API
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/performance")
class PerformanceController(
    private val performanceCollector: PerformanceCollector,
    private val performanceAnalyzer: PerformanceAnalyzer,
    private val performanceOptimizer: PerformanceOptimizer,
    private val stressTestTool: StressTestTool,
    private val reportGenerator: PerformanceReportGenerator
) {

    /**
     * 获取系统性能指标
     */
    @GetMapping("/metrics/system")
    fun getSystemMetrics(): DataResult<SystemMetrics> {
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
        @RequestParam(required = false, defaultValue = "100") limit: Int
    ): DataResult<List<PerformanceAnalyzer.PerformanceWarning>> {
        val warnings = performanceAnalyzer.getWarnings(metricName, limit)
        return DataResult.of(warnings)
    }

    /**
     * 清除性能警告
     *
     * @param metricName 指标名称
     */
    @DeleteMapping("/warnings")
    fun clearWarnings(@RequestParam(required = false) metricName: String?): ActionResult {
        performanceAnalyzer.clearWarnings(metricName)
        return ActionResult.isSuccess()
    }

    /**
     * 获取性能阈值
     *
     * @param name 阈值名称
     */
    @GetMapping("/thresholds/{name}")
    fun getThreshold(@PathVariable name: String): DataResult<PerformanceAnalyzer.PerformanceThreshold?> {
        val threshold = performanceAnalyzer.getThreshold(name)
        return DataResult.of(threshold)
    }

    /**
     * 获取所有性能阈值
     */
    @GetMapping("/thresholds")
    fun getAllThresholds(): DataResult<Map<String, PerformanceAnalyzer.PerformanceThreshold>> {
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
    ): ActionResult {
        performanceAnalyzer.setThreshold(name, value, type)
        return ActionResult.isSuccess()
    }

    /**
     * 获取优化策略
     *
     * @param name 策略名称
     */
    @GetMapping("/strategies/{name}")
    fun getStrategy(@PathVariable name: String): DataResult<PerformanceOptimizer.OptimizationStrategy?> {
        val strategy = performanceOptimizer.getStrategy(name)
        return DataResult.of(strategy)
    }

    /**
     * 获取所有优化策略
     */
    @GetMapping("/strategies")
    fun getAllStrategies(): DataResult<List<PerformanceOptimizer.OptimizationStrategy>> {
        val strategies = performanceOptimizer.getAllStrategies()
        return DataResult.of(strategies)
    }

    /**
     * 获取优化历史
     *
     * @param limit 限制数量
     */
    @GetMapping("/optimizations")
    fun getOptimizationHistory(@RequestParam(required = false, defaultValue = "100") limit: Int): DataResult<List<PerformanceOptimizer.OptimizationRecord>> {
        val history = performanceOptimizer.getOptimizationHistory(limit)
        return DataResult.of(history)
    }

    /**
     * 清除优化历史
     */
    @DeleteMapping("/optimizations")
    fun clearOptimizationHistory(): ActionResult {
        performanceOptimizer.clearOptimizationHistory()
        return ActionResult.isSuccess()
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
    @PostMapping("/stress-test")
    fun startStressTest(
        @RequestParam serviceId: String,
        @RequestBody parameters: Map<String, Any?>,
        @RequestParam concurrentUsers: Int,
        @RequestParam duration: Int,
        @RequestParam(required = false, defaultValue = "0") rampUp: Int
    ): DataResult<String> {
        val testId = stressTestTool.startTest(serviceId, parameters, concurrentUsers, duration, rampUp)
        return DataResult.of(testId)
    }

    /**
     * 停止压力测试
     *
     * @param testId 测试ID
     */
    @DeleteMapping("/stress-test/{testId}")
    fun stopStressTest(@PathVariable testId: String): ActionResult {
        val success = stressTestTool.stopTest(testId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("TEST_NOT_FOUND", "测试不存在或已停止", "")
        }
    }

    /**
     * 获取测试状态
     *
     * @param testId 测试ID
     */
    @GetMapping("/stress-test/{testId}")
    fun getTestStatus(@PathVariable testId: String): DataResult<StressTestTool.TestStatus?> {
        val status = stressTestTool.getTestStatus(testId)
        return DataResult.of(status)
    }

    /**
     * 获取所有测试状态
     */
    @GetMapping("/stress-test")
    fun getAllTestStatus(): DataResult<List<StressTestTool.TestStatus>> {
        val status = stressTestTool.getAllTestStatus()
        return DataResult.of(status)
    }

    /**
     * 清除测试历史
     *
     * @param testId 测试ID
     */
    @DeleteMapping("/stress-test/{testId}/history")
    fun clearTestHistory(@PathVariable testId: String): ActionResult {
        val success = stressTestTool.clearTestHistory(testId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("TEST_NOT_FOUND", "测试不存在或正在运行", "")
        }
    }

    /**
     * 清除所有测试历史
     */
    @DeleteMapping("/stress-test/history")
    fun clearAllTestHistory(): ActionResult {
        stressTestTool.clearAllTestHistory()
        return ActionResult.isSuccess()
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
    ): DataResult<String> {
        val reportId = reportGenerator.generateSystemReport(startTime, endTime)
        return DataResult.of(reportId)
    }

    /**
     * 生成服务性能报告
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @PostMapping("/reports/service")
    fun generateServiceReport(
        @RequestParam serviceId: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime
    ): DataResult<String> {
        val reportId = reportGenerator.generateServiceReport(serviceId, startTime, endTime)
        return DataResult.of(reportId)
    }

    /**
     * 生成压力测试报告
     *
     * @param testId 测试ID
     */
    @PostMapping("/reports/stress-test")
    fun generateStressTestReport(@RequestParam testId: String): DataResult<String> {
        val reportId = reportGenerator.generateStressTestReport(testId)
        return DataResult.of(reportId)
    }

    /**
     * 获取报告
     *
     * @param reportId 报告ID
     */
    @GetMapping("/reports/{reportId}")
    fun getReport(@PathVariable reportId: String): DataResult<String?> {
        val report = reportGenerator.getReport(reportId)
        return DataResult.of(report)
    }

    /**
     * 获取所有报告
     */
    @GetMapping("/reports")
    fun getAllReports(): DataResult<List<String>> {
        val reports = reportGenerator.getAllReports()
        return DataResult.of(reports)
    }

    /**
     * 删除报告
     *
     * @param reportId 报告ID
     */
    @DeleteMapping("/reports/{reportId}")
    fun deleteReport(@PathVariable reportId: String): ActionResult {
        val success = reportGenerator.deleteReport(reportId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("REPORT_NOT_FOUND", "报告不存在", "")
        }
    }
}
