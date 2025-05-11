package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.MonitoringService
import ai.magicdb.dataservice.api.model.ServiceCallStatistics
import ai.magicdb.dataservice.api.model.ServiceErrorStatistics
import ai.magicdb.dataservice.api.model.ServicePerformanceMetrics
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 监控控制器测试
 *
 * @author magicdb
 */
@WebMvcTest(MonitoringController::class)
class MonitoringControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockBean
    private lateinit var monitoringService: MonitoringService
    
    private lateinit var testStatistics: ServiceCallStatistics
    private lateinit var testErrorStatistics: ServiceErrorStatistics
    private lateinit var testPerformanceMetrics: ServicePerformanceMetrics
    
    @BeforeEach
    fun setUp() {
        // 创建测试数据
        val now = LocalDateTime.now()
        val yesterday = now.minusDays(1)
        
        testStatistics = ServiceCallStatistics(
            serviceId = "test-service",
            serviceName = "Test Service",
            totalCalls = 100,
            successCalls = 90,
            failedCalls = 10,
            successRate = 0.9,
            avgExecutionTime = 50.0,
            maxExecutionTime = 100,
            minExecutionTime = 10,
            lastCalledTime = now,
            startTime = yesterday,
            endTime = now,
            uniqueUsers = 10,
            uniqueIps = 5,
            callsByHour = mapOf(10 to 20L, 11 to 30L),
            callsByDay = mapOf("2023-01-01" to 50L, "2023-01-02" to 50L)
        )
        
        testErrorStatistics = ServiceErrorStatistics(
            serviceId = "test-service",
            serviceName = "Test Service",
            totalErrors = 10,
            errorRate = 0.1,
            errorTypes = mapOf("NullPointerException" to 5L, "IllegalArgumentException" to 5L),
            mostCommonErrors = listOf(
                ServiceErrorStatistics.ErrorInfo(
                    message = "Test error",
                    count = 5,
                    lastOccurrence = now
                )
            ),
            lastErrorTime = now,
            startTime = yesterday,
            endTime = now,
            errorsByHour = mapOf(10 to 5L, 11 to 5L),
            errorsByDay = mapOf("2023-01-01" to 5L, "2023-01-02" to 5L)
        )
        
        testPerformanceMetrics = ServicePerformanceMetrics(
            serviceId = "test-service",
            serviceName = "Test Service",
            avgResponseTime = 50.0,
            maxResponseTime = 100,
            minResponseTime = 10,
            percentiles = mapOf(
                "50th" to 40,
                "90th" to 80,
                "95th" to 90,
                "99th" to 95
            ),
            requestsPerSecond = 2.0,
            concurrentUsers = 5,
            memoryUsage = 100.0,
            cpuUsage = 50.0,
            startTime = yesterday,
            endTime = now,
            responseTimeByTimeSlot = mapOf(
                "2023-01-01 10:00" to 40.0,
                "2023-01-01 11:00" to 60.0
            ),
            requestsByTimeSlot = mapOf(
                "2023-01-01 10:00" to 40L,
                "2023-01-01 11:00" to 60L
            )
        )
    }
    
    @Test
    fun testGetServiceCallStatistics() {
        // 设置模拟对象的行为
        `when`(monitoringService.getServiceCallStatistics(anyString(), any(), any()))
            .thenReturn(listOf(testStatistics))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/monitoring/statistics")
            .param("serviceId", "test-service")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].serviceId").value("test-service"))
            .andExpect(jsonPath("$.data[0].totalCalls").value(100))
            .andExpect(jsonPath("$.data[0].successCalls").value(90))
            .andExpect(jsonPath("$.data[0].failedCalls").value(10))
            .andExpect(jsonPath("$.data[0].successRate").value(0.9))
        
        // 验证方法调用
        verify(monitoringService).getServiceCallStatistics(eq("test-service"), any(), any())
    }
    
    @Test
    fun testGetServiceErrorStatistics() {
        // 设置模拟对象的行为
        `when`(monitoringService.getServiceErrorStatistics(anyString(), any(), any()))
            .thenReturn(listOf(testErrorStatistics))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/monitoring/errors")
            .param("serviceId", "test-service")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].serviceId").value("test-service"))
            .andExpect(jsonPath("$.data[0].totalErrors").value(10))
            .andExpect(jsonPath("$.data[0].errorRate").value(0.1))
            .andExpect(jsonPath("$.data[0].errorTypes.NullPointerException").value(5))
        
        // 验证方法调用
        verify(monitoringService).getServiceErrorStatistics(eq("test-service"), any(), any())
    }
    
    @Test
    fun testGetServicePerformanceMetrics() {
        // 设置模拟对象的行为
        `when`(monitoringService.getServicePerformanceMetrics(anyString(), any(), any()))
            .thenReturn(listOf(testPerformanceMetrics))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/monitoring/performance")
            .param("serviceId", "test-service")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].serviceId").value("test-service"))
            .andExpect(jsonPath("$.data[0].avgResponseTime").value(50.0))
            .andExpect(jsonPath("$.data[0].maxResponseTime").value(100))
            .andExpect(jsonPath("$.data[0].minResponseTime").value(10))
            .andExpect(jsonPath("$.data[0].requestsPerSecond").value(2.0))
        
        // 验证方法调用
        verify(monitoringService).getServicePerformanceMetrics(eq("test-service"), any(), any())
    }
    
    @Test
    fun testGetRecentlyCalledServices() {
        // 设置模拟对象的行为
        `when`(monitoringService.getRecentlyCalledServices(anyInt()))
            .thenReturn(listOf("service1", "service2", "service3"))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/monitoring/recent-services")
            .param("limit", "5")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0]").value("service1"))
            .andExpect(jsonPath("$.data[1]").value("service2"))
            .andExpect(jsonPath("$.data[2]").value("service3"))
        
        // 验证方法调用
        verify(monitoringService).getRecentlyCalledServices(eq(5))
    }
    
    @Test
    fun testGetMostCalledServices() {
        // 设置模拟对象的行为
        `when`(monitoringService.getMostCalledServices(anyInt(), any(), any()))
            .thenReturn(listOf("service1", "service2", "service3"))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/monitoring/most-called-services")
            .param("limit", "5")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0]").value("service1"))
            .andExpect(jsonPath("$.data[1]").value("service2"))
            .andExpect(jsonPath("$.data[2]").value("service3"))
        
        // 验证方法调用
        verify(monitoringService).getMostCalledServices(eq(5), any(), any())
    }
    
    @Test
    fun testGetMostErrorServices() {
        // 设置模拟对象的行为
        `when`(monitoringService.getMostErrorServices(anyInt(), any(), any()))
            .thenReturn(listOf("service1", "service2", "service3"))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/monitoring/most-error-services")
            .param("limit", "5")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0]").value("service1"))
            .andExpect(jsonPath("$.data[1]").value("service2"))
            .andExpect(jsonPath("$.data[2]").value("service3"))
        
        // 验证方法调用
        verify(monitoringService).getMostErrorServices(eq(5), any(), any())
    }
    
    @Test
    fun testGetWorstPerformanceServices() {
        // 设置模拟对象的行为
        `when`(monitoringService.getWorstPerformanceServices(anyInt(), any(), any()))
            .thenReturn(listOf("service1", "service2", "service3"))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/monitoring/worst-performance-services")
            .param("limit", "5")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0]").value("service1"))
            .andExpect(jsonPath("$.data[1]").value("service2"))
            .andExpect(jsonPath("$.data[2]").value("service3"))
        
        // 验证方法调用
        verify(monitoringService).getWorstPerformanceServices(eq(5), any(), any())
    }
    
    @Test
    fun testClearMonitoringData() {
        // 设置模拟对象的行为
        `when`(monitoringService.clearMonitoringData(anyString(), any()))
            .thenReturn(10)
        
        // 执行请求
        mockMvc.perform(delete("/api/data-service/monitoring/data")
            .param("serviceId", "test-service")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(10))
        
        // 验证方法调用
        verify(monitoringService).clearMonitoringData(eq("test-service"), any())
    }
    
    @Test
    fun testRecordServiceCall() {
        // 执行请求
        mockMvc.perform(post("/api/data-service/monitoring/record")
            .param("serviceId", "test-service")
            .param("executionTime", "100")
            .param("success", "true")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
        
        // 验证方法调用
        verify(monitoringService).recordServiceCall(
            eq("test-service"),
            eq(100L),
            eq(true),
            isNull(),
            isNull(),
            isNull()
        )
    }
    
    @Test
    fun testRecordServiceCallWithError() {
        // 执行请求
        mockMvc.perform(post("/api/data-service/monitoring/record")
            .param("serviceId", "test-service")
            .param("executionTime", "100")
            .param("success", "false")
            .param("errorMessage", "Test error")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
        
        // 验证方法调用
        verify(monitoringService).recordServiceCall(
            eq("test-service"),
            eq(100L),
            eq(false),
            eq("Test error"),
            isNull(),
            isNull()
        )
    }
}
