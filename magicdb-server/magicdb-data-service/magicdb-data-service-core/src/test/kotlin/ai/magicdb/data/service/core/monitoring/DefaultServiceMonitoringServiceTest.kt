package ai.magicdb.data.service.core.monitoring

import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.model.DataService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.Calendar
import java.util.Date

class DefaultServiceMonitoringServiceTest {
    
    private lateinit var dataServiceManager: DataServiceManager
    private lateinit var monitoringService: DefaultServiceMonitoringService
    
    @BeforeEach
    fun setUp() {
        dataServiceManager = mock(DataServiceManager::class.java)
        monitoringService = DefaultServiceMonitoringService(dataServiceManager)
        
        // 模拟服务
        val service1 = DataService(
            id = "service1",
            name = "Test Service 1",
            script = "function execute() { return 'Hello'; }",
            language = "javascript",
            createTime = Date(),
            updateTime = Date()
        )
        
        val service2 = DataService(
            id = "service2",
            name = "Test Service 2",
            script = "function execute() { return 'World'; }",
            language = "javascript",
            createTime = Date(),
            updateTime = Date()
        )
        
        `when`(dataServiceManager.getService("service1")).thenReturn(service1)
        `when`(dataServiceManager.getService("service2")).thenReturn(service2)
    }
    
    @Test
    fun `recordExecution should record successful execution`() {
        // 记录成功执行
        monitoringService.recordExecution("service1", 100, true)
        
        // 获取统计
        val statistics = monitoringService.getServiceStatistics("service1")
        
        // 验证
        assertNotNull(statistics)
        assertEquals(1, statistics.size)
        
        val stats = statistics[0]
        assertEquals("service1", stats.serviceId)
        assertEquals("Test Service 1", stats.serviceName)
        assertEquals(1, stats.totalExecutions)
        assertEquals(1, stats.successfulExecutions)
        assertEquals(0, stats.failedExecutions)
        assertEquals(100.0, stats.averageExecutionTime)
        assertEquals(100, stats.maxExecutionTime)
        assertEquals(100, stats.minExecutionTime)
    }
    
    @Test
    fun `recordExecution should record failed execution`() {
        // 记录失败执行
        monitoringService.recordExecution("service1", 200, false, "Test error")
        
        // 获取统计
        val statistics = monitoringService.getServiceStatistics("service1")
        
        // 验证
        assertNotNull(statistics)
        assertEquals(1, statistics.size)
        
        val stats = statistics[0]
        assertEquals("service1", stats.serviceId)
        assertEquals("Test Service 1", stats.serviceName)
        assertEquals(1, stats.totalExecutions)
        assertEquals(0, stats.successfulExecutions)
        assertEquals(1, stats.failedExecutions)
        assertEquals(200.0, stats.averageExecutionTime)
        
        // 验证错误统计
        val errors = monitoringService.getErrorStatistics("service1")
        assertNotNull(errors)
        assertEquals(1, errors.size)
        
        val error = errors[0]
        assertEquals("service1", error.serviceId)
        assertEquals("Test Service 1", error.serviceName)
        assertEquals("Test error", error.errorMessage)
        assertEquals(1, error.occurrences)
    }
    
    @Test
    fun `recordExecution should update statistics for multiple executions`() {
        // 记录多次执行
        monitoringService.recordExecution("service1", 100, true)
        monitoringService.recordExecution("service1", 200, true)
        monitoringService.recordExecution("service1", 300, false, "Error 1")
        monitoringService.recordExecution("service1", 400, false, "Error 2")
        monitoringService.recordExecution("service1", 500, false, "Error 1")
        
        // 获取统计
        val statistics = monitoringService.getServiceStatistics("service1")
        
        // 验证
        assertNotNull(statistics)
        assertEquals(1, statistics.size)
        
        val stats = statistics[0]
        assertEquals("service1", stats.serviceId)
        assertEquals("Test Service 1", stats.serviceName)
        assertEquals(5, stats.totalExecutions)
        assertEquals(2, stats.successfulExecutions)
        assertEquals(3, stats.failedExecutions)
        assertEquals(300.0, stats.averageExecutionTime)
        assertEquals(500, stats.maxExecutionTime)
        assertEquals(100, stats.minExecutionTime)
        
        // 验证错误统计
        val errors = monitoringService.getErrorStatistics("service1")
        assertNotNull(errors)
        assertEquals(2, errors.size)
        
        // 验证 Error 1 出现了两次
        val error1 = errors.find { it.errorMessage == "Error 1" }
        assertNotNull(error1)
        assertEquals(2, error1!!.occurrences)
        
        // 验证 Error 2 出现了一次
        val error2 = errors.find { it.errorMessage == "Error 2" }
        assertNotNull(error2)
        assertEquals(1, error2!!.occurrences)
    }
    
    @Test
    fun `getExecutionHistory should return filtered history`() {
        // 记录多次执行
        monitoringService.recordExecution("service1", 100, true)
        monitoringService.recordExecution("service2", 200, true)
        monitoringService.recordExecution("service1", 300, false, "Error")
        
        // 获取所有历史
        val allHistory = monitoringService.getExecutionHistory()
        assertEquals(3, allHistory.size)
        
        // 获取 service1 的历史
        val service1History = monitoringService.getExecutionHistory(serviceId = "service1")
        assertEquals(2, service1History.size)
        assertTrue(service1History.all { it.serviceId == "service1" })
        
        // 获取失败的历史
        val failedHistory = monitoringService.getExecutionHistory().filter { !it.success }
        assertEquals(1, failedHistory.size)
        assertEquals("Error", failedHistory[0].errorMessage)
    }
    
    @Test
    fun `clearExecutionHistory should remove records`() {
        // 记录多次执行
        monitoringService.recordExecution("service1", 100, true)
        monitoringService.recordExecution("service2", 200, true)
        monitoringService.recordExecution("service1", 300, false, "Error")
        
        // 清除 service1 的历史
        val removedCount = monitoringService.clearExecutionHistory(serviceId = "service1")
        assertEquals(2, removedCount)
        
        // 验证只剩下 service2 的历史
        val remainingHistory = monitoringService.getExecutionHistory()
        assertEquals(1, remainingHistory.size)
        assertEquals("service2", remainingHistory[0].serviceId)
    }
    
    @Test
    fun `clearExecutionHistory should remove records before date`() {
        // 创建日期
        val calendar = Calendar.getInstance()
        val now = calendar.time
        
        // 记录第一次执行
        monitoringService.recordExecution("service1", 100, true)
        
        // 设置日期为一天后
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val oneDayLater = calendar.time
        
        // 记录第二次执行
        monitoringService.recordExecution("service1", 200, true)
        
        // 清除一天前的历史
        val removedCount = monitoringService.clearExecutionHistory(before = oneDayLater)
        assertEquals(1, removedCount)
        
        // 验证只剩下第二次执行的历史
        val remainingHistory = monitoringService.getExecutionHistory()
        assertEquals(1, remainingHistory.size)
        assertEquals(200L, remainingHistory[0].executionTime)
    }
}
