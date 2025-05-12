package ai.magicdb.dataservice.core.performance

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.MonitoringRepository
import ai.magicdb.dataservice.api.MonitoringService
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServicePerformanceMetrics
import ai.magicdb.dataservice.api.model.ServicePerformanceRecord
import ai.magicdb.dataservice.api.model.ServiceResult
import ai.magicdb.dataservice.core.cache.DataServiceCacheManager
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*

/**
 * 性能测试
 *
 * @author magicdb
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [
    PerformanceCollector::class,
    PerformanceAnalyzer::class,
    PerformanceOptimizer::class,
    StressTestTool::class,
    PerformanceReportGenerator::class,
    ObjectMapper::class
])
class PerformanceTest {

    @MockBean
    private lateinit var monitoringRepository: MonitoringRepository

    @MockBean
    private lateinit var monitoringService: MonitoringService

    @MockBean
    private lateinit var dataServiceRepository: DataServiceRepository

    @MockBean
    private lateinit var dataServiceExecutor: DataServiceExecutor

    @MockBean
    private lateinit var cacheManager: DataServiceCacheManager

    @Autowired
    private lateinit var performanceCollector: PerformanceCollector

    @Autowired
    private lateinit var performanceAnalyzer: PerformanceAnalyzer

    @Autowired
    private lateinit var performanceOptimizer: PerformanceOptimizer

    @Autowired
    private lateinit var stressTestTool: StressTestTool

    @Autowired
    private lateinit var reportGenerator: PerformanceReportGenerator

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun testPerformanceCollector() {
        // 测试收集系统性能指标
        val metrics = performanceCollector.collectSystemMetrics()

        // 验证结果
        assertNotNull(metrics)
        assertTrue(metrics.cpuUsage >= 0)
        assertTrue(metrics.memoryUsage >= 0)
        assertTrue(metrics.threadCount > 0)
        assertTrue(metrics.gcInfo.isNotEmpty())
        assertTrue(metrics.classLoadingInfo.isNotEmpty())

        // 测试记录请求
        performanceCollector.recordRequest()
    }

    @Test
    fun testPerformanceAnalyzer() {
        // 测试分析系统性能指标
        val metrics = SystemMetrics(
            cpuUsage = 90.0,
            memoryUsage = 85.0,
            threadCount = 150,
            requestRate = 60.0,
            gcInfo = mapOf("total_count" to 100L),
            classLoadingInfo = mapOf("loaded_class_count" to 1000),
            uptime = 3600000
        )

        val warnings = performanceAnalyzer.analyzeSystemMetrics(metrics)

        // 验证结果
        assertNotNull(warnings)
        assertTrue(warnings.isNotEmpty())

        // 测试分析服务性能指标
        val serviceMetrics = ServicePerformanceMetrics(
            serviceId = "test-service",
            avgResponseTime = 800.0,
            maxResponseTime = 1500,
            minResponseTime = 100,
            requestsPerSecond = 60.0,
            // errorRate field doesn't exist in ServicePerformanceMetrics
            percentiles = mapOf("p95" to 1200.0, "p99" to 1400.0),
            cpuUsage = 90.0,
            memoryUsage = 85.0,
            // threadCount field doesn't exist in ServicePerformanceMetrics
            concurrentUsers = 10
        )

        val serviceWarnings = performanceAnalyzer.analyzeServiceMetrics(serviceMetrics)

        // 验证结果
        assertNotNull(serviceWarnings)
        assertTrue(serviceWarnings.isNotEmpty())

        // 测试设置和获取阈值
        performanceAnalyzer.setThreshold("test_threshold", 50.0, PerformanceAnalyzer.ThresholdType.GREATER_THAN)
        val threshold = performanceAnalyzer.getThreshold("test_threshold")

        // 验证结果
        assertNotNull(threshold)
        assertEquals(50.0, threshold?.value)
        assertEquals(PerformanceAnalyzer.ThresholdType.GREATER_THAN, threshold?.type)

        // 测试获取所有阈值
        val allThresholds = performanceAnalyzer.getAllThresholds()

        // 验证结果
        assertNotNull(allThresholds)
        assertTrue(allThresholds.isNotEmpty())

        // 测试获取警告
        val allWarnings = performanceAnalyzer.getWarnings()

        // 验证结果
        assertNotNull(allWarnings)
    }

    @Test
    fun testPerformanceOptimizer() {
        // 模拟服务
        val service = DataService(
            id = "test-service",
            name = "测试服务",
            script = "return 'Hello World';",
            language = "js",
            createTime = Date(),
            updateTime = Date(),
            cacheTime = 0,
            concurrentLimit = 10,
            timeout = 30000
        )

        `when`(dataServiceRepository.getService("test-service")).thenReturn(service)

        // 测试获取优化策略
        val strategies = performanceOptimizer.getAllStrategies()

        // 验证结果
        assertNotNull(strategies)
        assertTrue(strategies.isNotEmpty())

        // 测试获取优化历史
        val history = performanceOptimizer.getOptimizationHistory()

        // 验证结果
        assertNotNull(history)
    }

    @Test
    fun testStressTestTool() {
        // 模拟服务
        val service = DataService(
            id = "test-service",
            name = "测试服务",
            script = "return 'Hello World';",
            language = "js",
            createTime = Date(),
            updateTime = Date()
        )

        `when`(dataServiceRepository.getService("test-service")).thenReturn(service)

        // 模拟服务执行
        val result = ServiceResult(
            success = true,
            data = "Hello World"
        )

        `when`(dataServiceExecutor.execute(eq("test-service"), any())).thenReturn(result)

        // 测试启动压力测试
        val testId = stressTestTool.startTest(
            serviceId = "test-service",
            parameters = emptyMap(),
            concurrentUsers = 1,
            duration = 1,
            rampUp = 0
        )

        // 验证结果
        assertNotNull(testId)

        // 等待测试完成
        Thread.sleep(2000)

        // 测试获取测试状态
        val status = stressTestTool.getTestStatus(testId)

        // 验证结果
        assertNotNull(status)
        assertEquals(testId, status?.id)
        assertEquals("test-service", status?.serviceId)
        assertEquals(1, status?.concurrentUsers)
        assertEquals(1, status?.duration)
        assertEquals(0, status?.rampUp)
        assertFalse(status?.running ?: true)

        // 测试获取所有测试状态
        val allStatus = stressTestTool.getAllTestStatus()

        // 验证结果
        assertNotNull(allStatus)
        assertTrue(allStatus.isNotEmpty())

        // 测试清除测试历史
        val clearResult = stressTestTool.clearTestHistory(testId)

        // 验证结果
        assertTrue(clearResult)
    }

    @Test
    fun testPerformanceReportGenerator() {
        // 模拟服务
        val service = DataService(
            id = "test-service",
            name = "测试服务",
            script = "return 'Hello World';",
            language = "js",
            createTime = Date(),
            updateTime = Date()
        )

        `when`(dataServiceRepository.getService("test-service")).thenReturn(service)

        // 模拟性能记录
        val performanceRecords = listOf(
            ServicePerformanceRecord(
                id = "record-1",
                serviceId = "test-service",
                recordTime = LocalDateTime.now(),
                cpuUsage = 50.0,
                memoryUsage = 60.0,
                threadCount = 100,
                requestsPerSecond = 30.0
            )
        )

        `when`(monitoringRepository.getPerformanceRecords(isNull(), any(), any(), anyInt(), anyInt()))
            .thenReturn(performanceRecords)

        // 模拟服务性能指标
        val serviceMetrics = listOf(
            ServicePerformanceMetrics(
                serviceId = "test-service",
                avgResponseTime = 200.0,
                maxResponseTime = 500,
                minResponseTime = 50,
                requestsPerSecond = 30.0,
                // errorRate field doesn't exist in ServicePerformanceMetrics
                percentiles = mapOf("p95" to 400.0, "p99" to 450.0)
            )
        )

        `when`(monitoringService.getServicePerformanceMetrics(eq("test-service"), any(), any()))
            .thenReturn(serviceMetrics)

        // 测试生成系统性能报告
        val startTime = LocalDateTime.now().minusHours(1)
        val endTime = LocalDateTime.now()

        val systemReportId = reportGenerator.generateSystemReport(startTime, endTime)

        // 验证结果
        assertNotNull(systemReportId)

        // 测试生成服务性能报告
        val serviceReportId = reportGenerator.generateServiceReport("test-service", startTime, endTime)

        // 验证结果
        assertNotNull(serviceReportId)

        // 测试获取报告
        val systemReport = reportGenerator.getReport(systemReportId)

        // 验证结果
        assertNotNull(systemReport)

        // 测试获取所有报告
        val allReports = reportGenerator.getAllReports()

        // 验证结果
        assertNotNull(allReports)
        assertTrue(allReports.isNotEmpty())

        // 测试删除报告
        val deleteResult = reportGenerator.deleteReport(systemReportId)

        // 验证结果
        assertTrue(deleteResult)
    }
}
