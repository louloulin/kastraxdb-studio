package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.model.ServiceCallRecord
import ai.magicdb.dataservice.core.converter.MonitoringConverter
import ai.magicdb.dataservice.core.entity.ServiceCallRecordDO
import ai.magicdb.dataservice.core.mapper.ServiceCallRecordMapper
import ai.magicdb.dataservice.core.mapper.ServiceCallStatsMapper
import ai.magicdb.dataservice.core.mapper.ServicePerformanceMapper
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.util.ArrayList

/**
 * MyBatis监控存储库测试
 *
 * @author magicdb
 */
@ExtendWith(MockitoExtension::class)
class MybatisMonitoringRepositoryTest {
    
    @Mock
    private lateinit var callRecordMapper: ServiceCallRecordMapper
    
    @Mock
    private lateinit var callStatsMapper: ServiceCallStatsMapper
    
    @Mock
    private lateinit var performanceMapper: ServicePerformanceMapper
    
    @Mock
    private lateinit var monitoringConverter: MonitoringConverter
    
    @InjectMocks
    private lateinit var repository: MybatisMonitoringRepository
    
    private lateinit var testRecord: ServiceCallRecord
    private lateinit var testRecordDO: ServiceCallRecordDO
    private lateinit var now: LocalDateTime
    
    @BeforeEach
    fun setUp() {
        now = LocalDateTime.now()
        
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
    fun testSaveCallRecord() {
        // 设置模拟行为
        `when`(monitoringConverter.toDO(testRecord)).thenReturn(testRecordDO)
        `when`(callRecordMapper.insert(testRecordDO)).thenReturn(1)
        
        // 执行测试
        val result = repository.saveCallRecord(testRecord)
        
        // 验证结果
        assertEquals("test-id", result)
        verify(monitoringConverter).toDO(testRecord)
        verify(callRecordMapper).insert(testRecordDO)
    }
    
    @Test
    fun testGetCallRecord() {
        // 设置模拟行为
        `when`(callRecordMapper.selectById("test-id")).thenReturn(testRecordDO)
        `when`(monitoringConverter.toModel(testRecordDO)).thenReturn(testRecord)
        
        // 执行测试
        val result = repository.getCallRecord("test-id")
        
        // 验证结果
        assertNotNull(result)
        assertEquals("test-id", result?.id)
        verify(callRecordMapper).selectById("test-id")
        verify(monitoringConverter).toModel(testRecordDO)
    }
    
    @Test
    fun testGetCallRecords() {
        // 设置模拟行为
        val page = Page<ServiceCallRecordDO>()
        page.records = listOf(testRecordDO)
        
        `when`(callRecordMapper.selectPage(any(), any())).thenReturn(page)
        `when`(monitoringConverter.toModel(testRecordDO)).thenReturn(testRecord)
        
        // 执行测试
        val result = repository.getCallRecords(
            serviceId = "test-service",
            startTime = now.minusDays(1),
            endTime = now,
            success = true,
            limit = 10,
            offset = 0
        )
        
        // 验证结果
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("test-id", result[0].id)
        verify(callRecordMapper).selectPage(any(), any())
        verify(monitoringConverter).toModel(testRecordDO)
    }
    
    @Test
    fun testGetCallRecordCount() {
        // 设置模拟行为
        `when`(callRecordMapper.countRecords(anyString(), any(), any(), any())).thenReturn(10)
        
        // 执行测试
        val result = repository.getCallRecordCount("test-service", now.minusDays(1), now)
        
        // 验证结果
        assertEquals(10, result)
        verify(callRecordMapper).countRecords("test-service", now.minusDays(1), now, null)
    }
    
    @Test
    fun testGetSuccessCallCount() {
        // 设置模拟行为
        `when`(callRecordMapper.countRecords(anyString(), any(), any(), anyBoolean())).thenReturn(8)
        
        // 执行测试
        val result = repository.getSuccessCallCount("test-service", now.minusDays(1), now)
        
        // 验证结果
        assertEquals(8, result)
        verify(callRecordMapper).countRecords("test-service", now.minusDays(1), now, true)
    }
    
    @Test
    fun testGetFailedCallCount() {
        // 设置模拟行为
        `when`(callRecordMapper.countRecords(anyString(), any(), any(), anyBoolean())).thenReturn(2)
        
        // 执行测试
        val result = repository.getFailedCallCount("test-service", now.minusDays(1), now)
        
        // 验证结果
        assertEquals(2, result)
        verify(callRecordMapper).countRecords("test-service", now.minusDays(1), now, false)
    }
    
    @Test
    fun testGetAvgExecutionTime() {
        // 设置模拟行为
        `when`(callRecordMapper.avgExecutionTime(anyString(), any(), any(), anyBoolean())).thenReturn(50.0)
        
        // 执行测试
        val result = repository.getAvgExecutionTime("test-service", now.minusDays(1), now, true)
        
        // 验证结果
        assertEquals(50.0, result)
        verify(callRecordMapper).avgExecutionTime("test-service", now.minusDays(1), now, true)
    }
    
    @Test
    fun testGetMaxExecutionTime() {
        // 设置模拟行为
        `when`(callRecordMapper.maxExecutionTime(anyString(), any(), any(), anyBoolean())).thenReturn(100L)
        
        // 执行测试
        val result = repository.getMaxExecutionTime("test-service", now.minusDays(1), now, true)
        
        // 验证结果
        assertEquals(100L, result)
        verify(callRecordMapper).maxExecutionTime("test-service", now.minusDays(1), now, true)
    }
    
    @Test
    fun testGetMinExecutionTime() {
        // 设置模拟行为
        `when`(callRecordMapper.minExecutionTime(anyString(), any(), any(), anyBoolean())).thenReturn(10L)
        
        // 执行测试
        val result = repository.getMinExecutionTime("test-service", now.minusDays(1), now, true)
        
        // 验证结果
        assertEquals(10L, result)
        verify(callRecordMapper).minExecutionTime("test-service", now.minusDays(1), now, true)
    }
    
    @Test
    fun testGetLastCallTime() {
        // 设置模拟行为
        `when`(callRecordMapper.lastCallTime("test-service")).thenReturn(now)
        
        // 执行测试
        val result = repository.getLastCallTime("test-service")
        
        // 验证结果
        assertEquals(now, result)
        verify(callRecordMapper).lastCallTime("test-service")
    }
    
    @Test
    fun testGetLastErrorTime() {
        // 设置模拟行为
        `when`(callRecordMapper.lastErrorTime("test-service")).thenReturn(now)
        
        // 执行测试
        val result = repository.getLastErrorTime("test-service")
        
        // 验证结果
        assertEquals(now, result)
        verify(callRecordMapper).lastErrorTime("test-service")
    }
    
    @Test
    fun testGetUniqueUserCount() {
        // 设置模拟行为
        `when`(callRecordMapper.uniqueUserCount(anyString(), any(), any())).thenReturn(5)
        
        // 执行测试
        val result = repository.getUniqueUserCount("test-service", now.minusDays(1), now)
        
        // 验证结果
        assertEquals(5, result)
        verify(callRecordMapper).uniqueUserCount("test-service", now.minusDays(1), now)
    }
    
    @Test
    fun testGetUniqueIpCount() {
        // 设置模拟行为
        `when`(callRecordMapper.uniqueIpCount(anyString(), any(), any())).thenReturn(3)
        
        // 执行测试
        val result = repository.getUniqueIpCount("test-service", now.minusDays(1), now)
        
        // 验证结果
        assertEquals(3, result)
        verify(callRecordMapper).uniqueIpCount("test-service", now.minusDays(1), now)
    }
    
    @Test
    fun testGetErrorTypeStats() {
        // 设置模拟行为
        val errorTypeStats = listOf(
            mapOf("error_type" to "NullPointerException", "error_count" to 3),
            mapOf("error_type" to "IllegalArgumentException", "error_count" to 2)
        )
        `when`(callRecordMapper.errorTypeStats(anyString(), any(), any())).thenReturn(errorTypeStats)
        
        // 执行测试
        val result = repository.getErrorTypeStats("test-service", now.minusDays(1), now)
        
        // 验证结果
        assertEquals(2, result.size)
        assertEquals(3L, result["NullPointerException"])
        assertEquals(2L, result["IllegalArgumentException"])
        verify(callRecordMapper).errorTypeStats("test-service", now.minusDays(1), now)
    }
    
    @Test
    fun testGetMostCommonErrors() {
        // 设置模拟行为
        val mostCommonErrors = listOf(
            mapOf("error_message" to "Null pointer", "error_count" to 3),
            mapOf("error_message" to "Invalid argument", "error_count" to 2)
        )
        `when`(callRecordMapper.mostCommonErrors(anyString(), any(), any(), anyInt())).thenReturn(mostCommonErrors)
        
        // 执行测试
        val result = repository.getMostCommonErrors("test-service", now.minusDays(1), now, 10)
        
        // 验证结果
        assertEquals(2, result.size)
        assertEquals("Null pointer", result[0].first)
        assertEquals(3L, result[0].second)
        assertEquals("Invalid argument", result[1].first)
        assertEquals(2L, result[1].second)
        verify(callRecordMapper).mostCommonErrors("test-service", now.minusDays(1), now, 10)
    }
    
    @Test
    fun testClearMonitoringData() {
        // 设置模拟行为
        `when`(callRecordMapper.clearMonitoringData(anyString(), any())).thenReturn(5)
        
        // 执行测试
        val result = repository.clearMonitoringData("test-service", now.minusDays(7))
        
        // 验证结果
        assertEquals(5, result)
        verify(callRecordMapper).clearMonitoringData("test-service", now.minusDays(7))
    }
}
