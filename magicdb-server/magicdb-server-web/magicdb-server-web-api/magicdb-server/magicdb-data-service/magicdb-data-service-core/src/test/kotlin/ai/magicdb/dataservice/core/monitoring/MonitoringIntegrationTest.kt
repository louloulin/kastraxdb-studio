package ai.magicdb.dataservice.core.monitoring

import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.MonitoringRepository
import ai.magicdb.dataservice.api.MonitoringService
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceCallRecord
import ai.magicdb.dataservice.core.converter.MonitoringConverter
import ai.magicdb.dataservice.core.entity.ServiceCallRecordDO
import ai.magicdb.dataservice.core.mapper.ServiceCallRecordMapper
import ai.magicdb.dataservice.core.mapper.ServiceCallStatsMapper
import ai.magicdb.dataservice.core.mapper.ServicePerformanceMapper
import ai.magicdb.dataservice.core.repository.MybatisMonitoringRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.util.Date

/**
 * 监控集成测试
 *
 * @author magicdb
 */
@ExtendWith(MockitoExtension::class)
class MonitoringIntegrationTest {
    
    @Mock
    private lateinit var callRecordMapper: ServiceCallRecordMapper
    
    @Mock
    private lateinit var callStatsMapper: ServiceCallStatsMapper
    
    @Mock
    private lateinit var performanceMapper: ServicePerformanceMapper
    
    @Mock
    private lateinit var dataServiceRepository: DataServiceRepository
    
    private lateinit var monitoringConverter: MonitoringConverter
    private lateinit var monitoringRepository: MonitoringRepository
    private lateinit var monitoringService: MonitoringService
    
    private lateinit var testService: DataService
    private lateinit var testRecord: ServiceCallRecord
    private lateinit var testRecordDO: ServiceCallRecordDO
    private lateinit var now: LocalDateTime
    
    @BeforeEach
    fun setUp() {
        now = LocalDateTime.now()
        
        // 创建ObjectMapper
        val objectMapper = ObjectMapper()
        
        // 创建转换器
        monitoringConverter = MonitoringConverter(objectMapper)
        
        // 创建存储库
        monitoringRepository = MybatisMonitoringRepository(
            callRecordMapper,
            callStatsMapper,
            performanceMapper,
            monitoringConverter
        )
        
        // 创建服务
        monitoringService = DefaultMonitoringService(monitoringRepository, dataServiceRepository)
        
        // 创建测试服务
        testService = DataService(
            id = "test-service",
            name = "Test Service",
            description = "Test service for monitoring",
            createTime = Date(),
            updateTime = Date()
        )
        
        // 创建测试记录
        testRecord = ServiceCallRecord(
            id = "test-id",
            serviceId = "test-service",
            serviceName = "Test Service",
            callTime = now,
            executionTime = 100,
            success = true,
            userId = 1,
            clientIp = "127.0.0.1"
        )
        
        // 创建测试记录DO
        testRecordDO = ServiceCallRecordDO(
            id = "test-id",
            serviceId = "test-service",
            serviceName = "Test Service",
            callTime = now,
            executionTime = 100,
            success = true,
            errorMessage = null,
            errorType = null,
            userId = 1,
            clientIp = "127.0.0.1",
            requestParams = null,
            responseData = null,
            method = null,
            path = null,
            source = null
        )
    }
    
    @Test
    fun testRecordServiceCall() {
        // 设置模拟行为
        `when`(dataServiceRepository.getService(anyString())).thenReturn(testService)
        `when`(callRecordMapper.insert(any())).thenReturn(1)
        
        // 执行测试
        monitoringService.recordServiceCall(
            serviceId = "test-service",
            executionTime = 100,
            success = true,
            userId = 1,
            clientIp = "127.0.0.1"
        )
        
        // 验证调用
        verify(dataServiceRepository).getService("test-service")
        verify(callRecordMapper).insert(any())
    }
    
    @Test
    fun testGetServiceCallStatistics() {
        // 设置模拟行为
        `when`(callRecordMapper.countRecords(anyString(), any(), any(), any())).thenReturn(10)
        `when`(callRecordMapper.countRecords(anyString(), any(), any(), any())).thenReturn(8)
        `when`(callRecordMapper.avgExecutionTime(anyString(), any(), any(), any())).thenReturn(50.0)
        `when`(callRecordMapper.maxExecutionTime(anyString(), any(), any(), any())).thenReturn(100L)
        `when`(callRecordMapper.minExecutionTime(anyString(), any(), any(), any())).thenReturn(10L)
        `when`(callRecordMapper.lastCallTime(anyString())).thenReturn(now)
        `when`(callRecordMapper.uniqueUserCount(anyString(), any(), any())).thenReturn(5)
        `when`(callRecordMapper.uniqueIpCount(anyString(), any(), any())).thenReturn(3)
        `when`(dataServiceRepository.getService(anyString())).thenReturn(testService)
        
        // 执行测试
        val result = monitoringService.getServiceCallStatistics("test-service", now.minusDays(1), now)
        
        // 验证结果
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("test-service", result[0].serviceId)
        assertEquals("Test Service", result[0].serviceName)
        assertEquals(10, result[0].totalCalls)
        assertEquals(8, result[0].successCalls)
        assertEquals(2, result[0].failedCalls)
        assertEquals(0.8, result[0].successRate)
        assertEquals(50.0, result[0].avgExecutionTime)
        assertEquals(100L, result[0].maxExecutionTime)
        assertEquals(10L, result[0].minExecutionTime)
        assertEquals(now, result[0].lastCalledTime)
        assertEquals(5, result[0].uniqueUsers)
        assertEquals(3, result[0].uniqueIps)
    }
    
    @Test
    fun testGetServiceErrorStatistics() {
        // 设置模拟行为
        `when`(callRecordMapper.countRecords(anyString(), any(), any(), any())).thenReturn(10)
        `when`(callRecordMapper.countRecords(anyString(), any(), any(), any())).thenReturn(2)
        `when`(callRecordMapper.lastErrorTime(anyString())).thenReturn(now)
        `when`(dataServiceRepository.getService(anyString())).thenReturn(testService)
        
        // 执行测试
        val result = monitoringService.getServiceErrorStatistics("test-service", now.minusDays(1), now)
        
        // 验证结果
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("test-service", result[0].serviceId)
        assertEquals("Test Service", result[0].serviceName)
        assertEquals(2, result[0].totalErrors)
        assertEquals(0.2, result[0].errorRate)
        assertEquals(now, result[0].lastErrorTime)
    }
    
    @Test
    fun testGetServicePerformanceMetrics() {
        // 设置模拟行为
        `when`(callRecordMapper.countRecords(anyString(), any(), any(), any())).thenReturn(10)
        `when`(callRecordMapper.avgExecutionTime(anyString(), any(), any(), any())).thenReturn(50.0)
        `when`(callRecordMapper.uniqueUserCount(anyString(), any(), any())).thenReturn(5)
        `when`(dataServiceRepository.getService(anyString())).thenReturn(testService)
        
        // 执行测试
        val result = monitoringService.getServicePerformanceMetrics("test-service", now.minusDays(1), now)
        
        // 验证结果
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("test-service", result[0].serviceId)
        assertEquals("Test Service", result[0].serviceName)
        assertEquals(50.0, result[0].avgResponseTime)
        assertTrue(result[0].requestsPerSecond > 0)
        assertEquals(5, result[0].concurrentUsers)
    }
    
    @Test
    fun testClearMonitoringData() {
        // 设置模拟行为
        `when`(callRecordMapper.clearMonitoringData(anyString(), any())).thenReturn(5)
        
        // 执行测试
        val result = monitoringService.clearMonitoringData("test-service", now.minusDays(7))
        
        // 验证结果
        assertEquals(5, result)
        verify(callRecordMapper).clearMonitoringData("test-service", now.minusDays(7))
    }
}
