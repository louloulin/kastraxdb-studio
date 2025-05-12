package ai.magicdb.data.service.core.monitoring

import ai.magicdb.data.service.api.DataServiceRepository
import ai.magicdb.data.service.api.MonitoringRepository
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceCallRecord
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

/**
 * 默认监控服务测试
 *
 * @author magicdb
 */
class DefaultMonitoringServiceTest {
    
    private lateinit var monitoringService: DefaultMonitoringService
    private lateinit var monitoringRepository: MonitoringRepository
    private lateinit var dataServiceRepository: DataServiceRepository
    
    @BeforeEach
    fun setUp() {
        monitoringRepository = mock(MonitoringRepository::class.java)
        dataServiceRepository = mock(DataServiceRepository::class.java)
        monitoringService = DefaultMonitoringService(monitoringRepository, dataServiceRepository)
    }
    
    @Test
    fun testRecordServiceCall() {
        // 准备测试数据
        val serviceId = "test-service"
        val executionTime = 100L
        val success = true
        val userId = 123L
        val clientIp = "127.0.0.1"
        
        // 设置模拟对象的行为
        val service = DataService(id = serviceId, name = "Test Service")
        `when`(dataServiceRepository.getService(serviceId)).thenReturn(service)
        
        // 执行方法
        monitoringService.recordServiceCall(serviceId, executionTime, success, null, userId, clientIp)
        
        // 验证方法调用
        verify(monitoringRepository).saveCallRecord(any())
    }
    
    @Test
    fun testRecordServiceCallWithError() {
        // 准备测试数据
        val serviceId = "test-service"
        val executionTime = 100L
        val success = false
        val errorMessage = "Test error"
        
        // 设置模拟对象的行为
        val service = DataService(id = serviceId, name = "Test Service")
        `when`(dataServiceRepository.getService(serviceId)).thenReturn(service)
        
        // 执行方法
        monitoringService.recordServiceCall(serviceId, executionTime, success, errorMessage)
        
        // 验证方法调用
        verify(monitoringRepository).saveCallRecord(any())
    }
    
    @Test
    fun testGetServiceCallStatistics() {
        // 准备测试数据
        val serviceId = "test-service"
        val startTime = LocalDateTime.now().minusDays(1)
        val endTime = LocalDateTime.now()
        
        // 设置模拟对象的行为
        val records = listOf(
            ServiceCallRecord(
                id = "1",
                serviceId = serviceId,
                serviceName = "Test Service",
                callTime = LocalDateTime.now(),
                executionTime = 100,
                success = true
            )
        )
        `when`(monitoringRepository.getCallRecords(eq(serviceId), eq(startTime), eq(endTime), isNull(), anyInt(), anyInt()))
            .thenReturn(records)
        `when`(monitoringRepository.getCallRecordCount(eq(serviceId), eq(startTime), eq(endTime), isNull()))
            .thenReturn(1)
        `when`(monitoringRepository.getSuccessCallCount(eq(serviceId), eq(startTime), eq(endTime)))
            .thenReturn(1)
        `when`(monitoringRepository.getFailedCallCount(eq(serviceId), eq(startTime), eq(endTime)))
            .thenReturn(0)
        `when`(monitoringRepository.getAvgExecutionTime(eq(serviceId), eq(startTime), eq(endTime), isNull()))
            .thenReturn(100.0)
        `when`(monitoringRepository.getMaxExecutionTime(eq(serviceId), eq(startTime), eq(endTime), isNull()))
            .thenReturn(100)
        `when`(monitoringRepository.getMinExecutionTime(eq(serviceId), eq(startTime), eq(endTime), isNull()))
            .thenReturn(100)
        `when`(monitoringRepository.getLastCallTime(eq(serviceId)))
            .thenReturn(LocalDateTime.now())
        `when`(monitoringRepository.getUniqueUserCount(eq(serviceId), eq(startTime), eq(endTime)))
            .thenReturn(1)
        `when`(monitoringRepository.getUniqueIpCount(eq(serviceId), eq(startTime), eq(endTime)))
            .thenReturn(1)
        `when`(monitoringRepository.getCallsByHour(eq(serviceId), eq(startTime), eq(endTime)))
            .thenReturn(mapOf(10 to 1L))
        `when`(monitoringRepository.getCallsByDay(eq(serviceId), eq(startTime), eq(endTime)))
            .thenReturn(mapOf("2023-01-01" to 1L))
        
        // 执行方法
        val statistics = monitoringService.getServiceCallStatistics(serviceId, startTime, endTime)
        
        // 验证结果
        assertNotNull(statistics)
        assertEquals(1, statistics.size)
        assertEquals(serviceId, statistics[0].serviceId)
        assertEquals(1, statistics[0].totalCalls)
        assertEquals(1, statistics[0].successCalls)
        assertEquals(0, statistics[0].failedCalls)
        assertEquals(1.0, statistics[0].successRate)
        assertEquals(100.0, statistics[0].avgExecutionTime)
        assertEquals(100, statistics[0].maxExecutionTime)
        assertEquals(100, statistics[0].minExecutionTime)
        assertEquals(1, statistics[0].uniqueUsers)
        assertEquals(1, statistics[0].uniqueIps)
        assertEquals(1, statistics[0].callsByHour.size)
        assertEquals(1, statistics[0].callsByDay.size)
    }
    
    @Test
    fun testGetServiceErrorStatistics() {
        // 准备测试数据
        val serviceId = "test-service"
        val startTime = LocalDateTime.now().minusDays(1)
        val endTime = LocalDateTime.now()
        
        // 设置模拟对象的行为
        val errorRecords = listOf(
            ServiceCallRecord(
                id = "1",
                serviceId = serviceId,
                serviceName = "Test Service",
                callTime = LocalDateTime.now(),
                executionTime = 100,
                success = false,
                errorMessage = "Test error",
                errorType = "TestError"
            )
        )
        `when`(monitoringRepository.getCallRecords(eq(serviceId), eq(startTime), eq(endTime), eq(false), anyInt(), anyInt()))
            .thenReturn(errorRecords)
        `when`(monitoringRepository.getCallRecordCount(eq(serviceId), eq(startTime), eq(endTime), isNull()))
            .thenReturn(1)
        `when`(monitoringRepository.getFailedCallCount(eq(serviceId), eq(startTime), eq(endTime)))
            .thenReturn(1)
        `when`(monitoringRepository.getErrorTypeStats(eq(serviceId), eq(startTime), eq(endTime)))
            .thenReturn(mapOf("TestError" to 1L))
        `when`(monitoringRepository.getMostCommonErrors(eq(serviceId), eq(startTime), eq(endTime), anyInt()))
            .thenReturn(listOf("Test error" to 1L))
        `when`(monitoringRepository.getErrorsByHour(eq(serviceId), eq(startTime), eq(endTime)))
            .thenReturn(mapOf(10 to 1L))
        `when`(monitoringRepository.getErrorsByDay(eq(serviceId), eq(startTime), eq(endTime)))
            .thenReturn(mapOf("2023-01-01" to 1L))
        
        // 执行方法
        val statistics = monitoringService.getServiceErrorStatistics(serviceId, startTime, endTime)
        
        // 验证结果
        assertNotNull(statistics)
        assertEquals(1, statistics.size)
        assertEquals(serviceId, statistics[0].serviceId)
        assertEquals(1, statistics[0].totalErrors)
        assertEquals(1.0, statistics[0].errorRate)
        assertEquals(1, statistics[0].errorTypes.size)
        assertEquals(1, statistics[0].mostCommonErrors.size)
        assertEquals(1, statistics[0].errorsByHour.size)
        assertEquals(1, statistics[0].errorsByDay.size)
    }
    
    @Test
    fun testGetServicePerformanceMetrics() {
        // 准备测试数据
        val serviceId = "test-service"
        val startTime = LocalDateTime.now().minusDays(1)
        val endTime = LocalDateTime.now()
        
        // 设置模拟对象的行为
        val records = listOf(
            ServiceCallRecord(
                id = "1",
                serviceId = serviceId,
                serviceName = "Test Service",
                callTime = LocalDateTime.now(),
                executionTime = 100,
                success = true
            )
        )
        `when`(monitoringRepository.getCallRecords(eq(serviceId), eq(startTime), eq(endTime), isNull(), anyInt(), anyInt()))
            .thenReturn(records)
        `when`(monitoringRepository.getCallRecords(eq(serviceId), eq(startTime), eq(endTime), eq(true), anyInt(), anyInt()))
            .thenReturn(records)
        `when`(monitoringRepository.getCallRecordCount(eq(serviceId), eq(startTime), eq(endTime), isNull()))
            .thenReturn(1)
        `when`(monitoringRepository.getAvgExecutionTime(eq(serviceId), eq(startTime), eq(endTime), eq(true)))
            .thenReturn(100.0)
        `when`(monitoringRepository.getMaxExecutionTime(eq(serviceId), eq(startTime), eq(endTime), eq(true)))
            .thenReturn(100)
        `when`(monitoringRepository.getMinExecutionTime(eq(serviceId), eq(startTime), eq(endTime), eq(true)))
            .thenReturn(100)
        `when`(monitoringRepository.getUniqueUserCount(eq(serviceId), eq(startTime), eq(endTime)))
            .thenReturn(1)
        
        // 执行方法
        val metrics = monitoringService.getServicePerformanceMetrics(serviceId, startTime, endTime)
        
        // 验证结果
        assertNotNull(metrics)
        assertEquals(1, metrics.size)
        assertEquals(serviceId, metrics[0].serviceId)
        assertEquals(100.0, metrics[0].avgResponseTime)
        assertEquals(100, metrics[0].maxResponseTime)
        assertEquals(100, metrics[0].minResponseTime)
        assertEquals(4, metrics[0].percentiles.size)
        assertTrue(metrics[0].requestsPerSecond >= 0)
        assertEquals(1, metrics[0].concurrentUsers)
    }
    
    @Test
    fun testGetRecentlyCalledServices() {
        // 准备测试数据
        val limit = 5
        val services = listOf("service1", "service2", "service3")
        
        // 设置模拟对象的行为
        `when`(monitoringRepository.getRecentlyCalledServices(limit)).thenReturn(services)
        
        // 执行方法
        val result = monitoringService.getRecentlyCalledServices(limit)
        
        // 验证结果
        assertEquals(services, result)
    }
    
    @Test
    fun testGetMostCalledServices() {
        // 准备测试数据
        val limit = 5
        val startTime = LocalDateTime.now().minusDays(1)
        val endTime = LocalDateTime.now()
        val services = listOf(Pair("service1", 10L), Pair("service2", 5L), Pair("service3", 2L))
        
        // 设置模拟对象的行为
        `when`(monitoringRepository.getMostCalledServices(limit, startTime, endTime)).thenReturn(services)
        
        // 执行方法
        val result = monitoringService.getMostCalledServices(limit, startTime, endTime)
        
        // 验证结果
        assertEquals(services.map { it.first }, result)
    }
    
    @Test
    fun testGetMostErrorServices() {
        // 准备测试数据
        val limit = 5
        val startTime = LocalDateTime.now().minusDays(1)
        val endTime = LocalDateTime.now()
        val services = listOf(Pair("service1", 10L), Pair("service2", 5L), Pair("service3", 2L))
        
        // 设置模拟对象的行为
        `when`(monitoringRepository.getMostErrorServices(limit, startTime, endTime)).thenReturn(services)
        
        // 执行方法
        val result = monitoringService.getMostErrorServices(limit, startTime, endTime)
        
        // 验证结果
        assertEquals(services.map { it.first }, result)
    }
    
    @Test
    fun testGetWorstPerformanceServices() {
        // 准备测试数据
        val limit = 5
        val startTime = LocalDateTime.now().minusDays(1)
        val endTime = LocalDateTime.now()
        val services = listOf(Pair("service1", 100.0), Pair("service2", 50.0), Pair("service3", 20.0))
        
        // 设置模拟对象的行为
        `when`(monitoringRepository.getWorstPerformanceServices(limit, startTime, endTime)).thenReturn(services)
        
        // 执行方法
        val result = monitoringService.getWorstPerformanceServices(limit, startTime, endTime)
        
        // 验证结果
        assertEquals(services.map { it.first }, result)
    }
    
    @Test
    fun testClearMonitoringData() {
        // 准备测试数据
        val serviceId = "test-service"
        val before = LocalDateTime.now().minusDays(7)
        
        // 设置模拟对象的行为
        `when`(monitoringRepository.clearMonitoringData(serviceId, before)).thenReturn(10)
        
        // 执行方法
        val result = monitoringService.clearMonitoringData(serviceId, before)
        
        // 验证结果
        assertEquals(10, result)
    }
}
