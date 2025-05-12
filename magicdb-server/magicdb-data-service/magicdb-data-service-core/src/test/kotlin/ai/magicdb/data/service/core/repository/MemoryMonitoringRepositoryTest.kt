package ai.magicdb.data.service.core.repository

import ai.magicdb.data.service.api.model.ServiceCallRecord
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

/**
 * 内存监控存储库测试
 *
 * @author magicdb
 */
class MemoryMonitoringRepositoryTest {
    
    private lateinit var repository: MemoryMonitoringRepository
    private lateinit var testRecord1: ServiceCallRecord
    private lateinit var testRecord2: ServiceCallRecord
    private lateinit var testRecord3: ServiceCallRecord
    private lateinit var startTime: LocalDateTime
    private lateinit var endTime: LocalDateTime
    
    @BeforeEach
    fun setUp() {
        repository = MemoryMonitoringRepository()
        
        // 创建测试时间
        startTime = LocalDateTime.now().minusDays(1)
        endTime = LocalDateTime.now()
        
        // 创建测试记录
        testRecord1 = ServiceCallRecord(
            id = "1",
            serviceId = "service1",
            serviceName = "Service 1",
            callTime = LocalDateTime.now().minusHours(2),
            executionTime = 100,
            success = true,
            userId = 1,
            clientIp = "127.0.0.1"
        )
        
        testRecord2 = ServiceCallRecord(
            id = "2",
            serviceId = "service1",
            serviceName = "Service 1",
            callTime = LocalDateTime.now().minusHours(1),
            executionTime = 200,
            success = false,
            errorMessage = "Test error",
            errorType = "TestError",
            userId = 2,
            clientIp = "127.0.0.2"
        )
        
        testRecord3 = ServiceCallRecord(
            id = "3",
            serviceId = "service2",
            serviceName = "Service 2",
            callTime = LocalDateTime.now().minusMinutes(30),
            executionTime = 50,
            success = true,
            userId = 1,
            clientIp = "127.0.0.1"
        )
        
        // 保存测试记录
        repository.saveCallRecord(testRecord1)
        repository.saveCallRecord(testRecord2)
        repository.saveCallRecord(testRecord3)
    }
    
    @Test
    fun testSaveCallRecord() {
        // 创建新记录
        val record = ServiceCallRecord(
            id = "4",
            serviceId = "service3",
            serviceName = "Service 3",
            callTime = LocalDateTime.now(),
            executionTime = 150,
            success = true
        )
        
        // 保存记录
        val id = repository.saveCallRecord(record)
        
        // 验证结果
        assertEquals("4", id)
        assertNotNull(repository.getCallRecord(id))
    }
    
    @Test
    fun testSaveCallRecordWithoutId() {
        // 创建新记录（无ID）
        val record = ServiceCallRecord(
            id = "",
            serviceId = "service3",
            serviceName = "Service 3",
            callTime = LocalDateTime.now(),
            executionTime = 150,
            success = true
        )
        
        // 保存记录
        val id = repository.saveCallRecord(record)
        
        // 验证结果
        assertNotEquals("", id)
        assertNotNull(repository.getCallRecord(id))
    }
    
    @Test
    fun testGetCallRecord() {
        // 获取记录
        val record = repository.getCallRecord("1")
        
        // 验证结果
        assertNotNull(record)
        assertEquals("service1", record!!.serviceId)
        assertEquals(100L, record.executionTime)
        assertTrue(record.success)
    }
    
    @Test
    fun testGetCallRecordNotFound() {
        // 获取不存在的记录
        val record = repository.getCallRecord("not-exist")
        
        // 验证结果
        assertNull(record)
    }
    
    @Test
    fun testGetCallRecords() {
        // 获取所有记录
        val records = repository.getCallRecords(limit = 10)
        
        // 验证结果
        assertEquals(3, records.size)
    }
    
    @Test
    fun testGetCallRecordsByServiceId() {
        // 获取指定服务的记录
        val records = repository.getCallRecords(serviceId = "service1", limit = 10)
        
        // 验证结果
        assertEquals(2, records.size)
        assertTrue(records.all { it.serviceId == "service1" })
    }
    
    @Test
    fun testGetCallRecordsByTimeRange() {
        // 获取指定时间范围的记录
        val records = repository.getCallRecords(
            startTime = LocalDateTime.now().minusHours(1).minusMinutes(1),
            endTime = LocalDateTime.now(),
            limit = 10
        )
        
        // 验证结果
        assertEquals(2, records.size) // testRecord2 和 testRecord3
    }
    
    @Test
    fun testGetCallRecordsBySuccess() {
        // 获取成功的记录
        val successRecords = repository.getCallRecords(success = true, limit = 10)
        
        // 验证结果
        assertEquals(2, successRecords.size)
        assertTrue(successRecords.all { it.success })
        
        // 获取失败的记录
        val failedRecords = repository.getCallRecords(success = false, limit = 10)
        
        // 验证结果
        assertEquals(1, failedRecords.size)
        assertTrue(failedRecords.all { !it.success })
    }
    
    @Test
    fun testGetCallRecordCount() {
        // 获取记录数量
        val count = repository.getCallRecordCount()
        
        // 验证结果
        assertEquals(3, count)
    }
    
    @Test
    fun testGetCallRecordCountByServiceId() {
        // 获取指定服务的记录数量
        val count = repository.getCallRecordCount(serviceId = "service1")
        
        // 验证结果
        assertEquals(2, count)
    }
    
    @Test
    fun testGetSuccessCallCount() {
        // 获取成功记录数量
        val count = repository.getSuccessCallCount()
        
        // 验证结果
        assertEquals(2, count)
    }
    
    @Test
    fun testGetFailedCallCount() {
        // 获取失败记录数量
        val count = repository.getFailedCallCount()
        
        // 验证结果
        assertEquals(1, count)
    }
    
    @Test
    fun testGetAvgExecutionTime() {
        // 获取平均执行时间
        val avgTime = repository.getAvgExecutionTime()
        
        // 验证结果
        assertEquals((100 + 200 + 50) / 3.0, avgTime)
    }
    
    @Test
    fun testGetMaxExecutionTime() {
        // 获取最大执行时间
        val maxTime = repository.getMaxExecutionTime()
        
        // 验证结果
        assertEquals(200, maxTime)
    }
    
    @Test
    fun testGetMinExecutionTime() {
        // 获取最小执行时间
        val minTime = repository.getMinExecutionTime()
        
        // 验证结果
        assertEquals(50, minTime)
    }
    
    @Test
    fun testGetLastCallTime() {
        // 获取最后调用时间
        val lastTime = repository.getLastCallTime()
        
        // 验证结果
        assertNotNull(lastTime)
        // 最后调用时间应该是 testRecord3 的时间
        assertTrue(lastTime!!.isAfter(testRecord2.callTime))
    }
    
    @Test
    fun testGetUniqueUserCount() {
        // 获取唯一用户数
        val userCount = repository.getUniqueUserCount()
        
        // 验证结果
        assertEquals(2, userCount)
    }
    
    @Test
    fun testGetUniqueIpCount() {
        // 获取唯一IP数
        val ipCount = repository.getUniqueIpCount()
        
        // 验证结果
        assertEquals(2, ipCount)
    }
    
    @Test
    fun testGetErrorTypeStats() {
        // 获取错误类型统计
        val errorStats = repository.getErrorTypeStats()
        
        // 验证结果
        assertEquals(1, errorStats.size)
        assertEquals(1, errorStats["TestError"])
    }
    
    @Test
    fun testGetMostCommonErrors() {
        // 获取最常见错误
        val errors = repository.getMostCommonErrors()
        
        // 验证结果
        assertEquals(1, errors.size)
        assertEquals("Test error", errors[0].first)
        assertEquals(1, errors[0].second)
    }
    
    @Test
    fun testGetRecentlyCalledServices() {
        // 获取最近调用的服务
        val services = repository.getRecentlyCalledServices()
        
        // 验证结果
        assertEquals(2, services.size)
        // 最近调用的应该是 service2，然后是 service1
        assertEquals("service2", services[0])
        assertEquals("service1", services[1])
    }
    
    @Test
    fun testGetMostCalledServices() {
        // 获取调用最多的服务
        val services = repository.getMostCalledServices()
        
        // 验证结果
        assertEquals(2, services.size)
        // service1 有 2 次调用，service2 有 1 次调用
        assertEquals("service1", services[0].first)
        assertEquals(2L, services[0].second)
        assertEquals("service2", services[1].first)
        assertEquals(1L, services[1].second)
    }
    
    @Test
    fun testGetMostErrorServices() {
        // 获取错误最多的服务
        val services = repository.getMostErrorServices()
        
        // 验证结果
        assertEquals(1, services.size)
        assertEquals("service1", services[0].first)
        assertEquals(1L, services[0].second)
    }
    
    @Test
    fun testGetWorstPerformanceServices() {
        // 获取性能最差的服务
        val services = repository.getWorstPerformanceServices()
        
        // 验证结果
        assertEquals(2, services.size)
        // service1 的平均执行时间是 (100 + 200) / 2 = 150，service2 的平均执行时间是 50
        assertEquals("service1", services[0].first)
        assertEquals(150.0, services[0].second)
        assertEquals("service2", services[1].first)
        assertEquals(50.0, services[1].second)
    }
    
    @Test
    fun testGetCallsByHour() {
        // 获取按小时统计的调用次数
        val callsByHour = repository.getCallsByHour()
        
        // 验证结果
        assertTrue(callsByHour.isNotEmpty())
        // 具体小时数取决于测试运行时间，所以只验证总和
        assertEquals(3, callsByHour.values.sum())
    }
    
    @Test
    fun testGetCallsByDay() {
        // 获取按天统计的调用次数
        val callsByDay = repository.getCallsByDay()
        
        // 验证结果
        assertTrue(callsByDay.isNotEmpty())
        // 具体日期取决于测试运行时间，所以只验证总和
        assertEquals(3, callsByDay.values.sum())
    }
    
    @Test
    fun testGetErrorsByHour() {
        // 获取按小时统计的错误次数
        val errorsByHour = repository.getErrorsByHour()
        
        // 验证结果
        assertTrue(errorsByHour.isNotEmpty())
        // 具体小时数取决于测试运行时间，所以只验证总和
        assertEquals(1, errorsByHour.values.sum())
    }
    
    @Test
    fun testGetErrorsByDay() {
        // 获取按天统计的错误次数
        val errorsByDay = repository.getErrorsByDay()
        
        // 验证结果
        assertTrue(errorsByDay.isNotEmpty())
        // 具体日期取决于测试运行时间，所以只验证总和
        assertEquals(1, errorsByDay.values.sum())
    }
    
    @Test
    fun testClearMonitoringData() {
        // 清除监控数据
        val count = repository.clearMonitoringData()
        
        // 验证结果
        assertEquals(3, count)
        assertEquals(0, repository.getCallRecordCount())
    }
    
    @Test
    fun testClearMonitoringDataByServiceId() {
        // 清除指定服务的监控数据
        val count = repository.clearMonitoringData(serviceId = "service1")
        
        // 验证结果
        assertEquals(2, count)
        assertEquals(1, repository.getCallRecordCount())
        assertEquals(0, repository.getCallRecordCount(serviceId = "service1"))
        assertEquals(1, repository.getCallRecordCount(serviceId = "service2"))
    }
    
    @Test
    fun testClearMonitoringDataByTime() {
        // 清除指定时间之前的监控数据
        val count = repository.clearMonitoringData(
            before = LocalDateTime.now().minusMinutes(45)
        )
        
        // 验证结果
        assertEquals(2, count) // testRecord1 和 testRecord2
        assertEquals(1, repository.getCallRecordCount())
        assertEquals(0, repository.getCallRecordCount(serviceId = "service1"))
        assertEquals(1, repository.getCallRecordCount(serviceId = "service2"))
    }
}
