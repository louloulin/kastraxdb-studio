package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.ServiceTestManager
import ai.magicdb.data.service.api.model.ServiceTest
import ai.magicdb.data.service.api.model.ServiceTestResult
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * 服务测试控制器测试
 *
 * @author magicdb
 */
@WebMvcTest(ServiceTestController::class)
class ServiceTestControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockBean
    private lateinit var testManager: ServiceTestManager
    
    @BeforeEach
    fun setUp() {
        // 设置模拟对象的行为
    }
    
    @Test
    fun testCreateTest() {
        // 准备测试数据
        val test = ServiceTest(
            name = "测试用例1",
            serviceId = "service-123",
            parameters = mapOf("name" to "World"),
            expectedResult = mapOf("success" to true)
        )
        
        // 设置模拟对象的行为
        `when`(testManager.createTest(any())).thenReturn("test-123")
        
        // 执行请求
        val result = mockMvc.perform(post("/api/data-service/test")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(test)))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"test-123\""))
        
        // 验证方法调用
        verify(testManager).createTest(any())
    }
    
    @Test
    fun testUpdateTest() {
        // 准备测试数据
        val test = ServiceTest(
            id = "test-123",
            name = "测试用例1（更新）",
            serviceId = "service-123",
            parameters = mapOf("name" to "World"),
            expectedResult = mapOf("success" to true)
        )
        
        // 设置模拟对象的行为
        `when`(testManager.updateTest(any())).thenReturn(true)
        
        // 执行请求
        val result = mockMvc.perform(put("/api/data-service/test")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(test)))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(testManager).updateTest(any())
    }
    
    @Test
    fun testDeleteTest() {
        // 准备测试数据
        val testId = "test-123"
        
        // 设置模拟对象的行为
        `when`(testManager.deleteTest(testId)).thenReturn(true)
        
        // 执行请求
        val result = mockMvc.perform(delete("/api/data-service/test/$testId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(testManager).deleteTest(testId)
    }
    
    @Test
    fun testGetTest() {
        // 准备测试数据
        val testId = "test-123"
        val test = ServiceTest(
            id = testId,
            name = "测试用例1",
            serviceId = "service-123",
            parameters = mapOf("name" to "World"),
            expectedResult = mapOf("success" to true)
        )
        
        // 设置模拟对象的行为
        `when`(testManager.getTest(testId)).thenReturn(test)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/test/$testId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试用例1\""))
        
        // 验证方法调用
        verify(testManager).getTest(testId)
    }
    
    @Test
    fun testGetTestsByService() {
        // 准备测试数据
        val serviceId = "service-123"
        val test1 = ServiceTest(
            id = "test-123",
            name = "测试用例1",
            serviceId = serviceId,
            parameters = mapOf("name" to "World"),
            expectedResult = mapOf("success" to true)
        )
        val test2 = ServiceTest(
            id = "test-456",
            name = "测试用例2",
            serviceId = serviceId,
            parameters = mapOf("name" to "MagicDB"),
            expectedResult = mapOf("success" to true)
        )
        val tests = listOf(test1, test2)
        
        // 设置模拟对象的行为
        `when`(testManager.getTestsByService(serviceId)).thenReturn(tests)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/test/service/$serviceId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试用例1\""))
        assert(response.contains("\"测试用例2\""))
        
        // 验证方法调用
        verify(testManager).getTestsByService(serviceId)
    }
    
    @Test
    fun testRunTest() {
        // 准备测试数据
        val testId = "test-123"
        val result = ServiceTestResult(
            testId = testId,
            testName = "测试用例1",
            serviceId = "service-123",
            passed = true
        )
        
        // 设置模拟对象的行为
        `when`(testManager.runTest(testId)).thenReturn(result)
        
        // 执行请求
        val response = mockMvc.perform(post("/api/data-service/test/$testId/run")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .response.contentAsString
        
        // 验证结果
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试用例1\""))
        assert(response.contains("\"passed\":true"))
        
        // 验证方法调用
        verify(testManager).runTest(testId)
    }
    
    @Test
    fun testRunTestsByService() {
        // 准备测试数据
        val serviceId = "service-123"
        val result1 = ServiceTestResult(
            testId = "test-123",
            testName = "测试用例1",
            serviceId = serviceId,
            passed = true
        )
        val result2 = ServiceTestResult(
            testId = "test-456",
            testName = "测试用例2",
            serviceId = serviceId,
            passed = false,
            errorMessage = "测试失败"
        )
        val results = listOf(result1, result2)
        
        // 设置模拟对象的行为
        `when`(testManager.runTestsByService(serviceId)).thenReturn(results)
        
        // 执行请求
        val response = mockMvc.perform(post("/api/data-service/test/service/$serviceId/run")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .response.contentAsString
        
        // 验证结果
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试用例1\""))
        assert(response.contains("\"测试用例2\""))
        assert(response.contains("\"passed\":true"))
        assert(response.contains("\"passed\":false"))
        assert(response.contains("\"测试失败\""))
        
        // 验证方法调用
        verify(testManager).runTestsByService(serviceId)
    }
    
    @Test
    fun testGetTestResult() {
        // 准备测试数据
        val testId = "test-123"
        val result = ServiceTestResult(
            testId = testId,
            testName = "测试用例1",
            serviceId = "service-123",
            passed = true
        )
        
        // 设置模拟对象的行为
        `when`(testManager.getTestResult(testId)).thenReturn(result)
        
        // 执行请求
        val response = mockMvc.perform(get("/api/data-service/test/$testId/result")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .response.contentAsString
        
        // 验证结果
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试用例1\""))
        assert(response.contains("\"passed\":true"))
        
        // 验证方法调用
        verify(testManager).getTestResult(testId)
    }
    
    @Test
    fun testGetTestResultsByService() {
        // 准备测试数据
        val serviceId = "service-123"
        val result1 = ServiceTestResult(
            testId = "test-123",
            testName = "测试用例1",
            serviceId = serviceId,
            passed = true
        )
        val result2 = ServiceTestResult(
            testId = "test-456",
            testName = "测试用例2",
            serviceId = serviceId,
            passed = false,
            errorMessage = "测试失败"
        )
        val results = listOf(result1, result2)
        
        // 设置模拟对象的行为
        `when`(testManager.getTestResultsByService(serviceId)).thenReturn(results)
        
        // 执行请求
        val response = mockMvc.perform(get("/api/data-service/test/service/$serviceId/results")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .response.contentAsString
        
        // 验证结果
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试用例1\""))
        assert(response.contains("\"测试用例2\""))
        assert(response.contains("\"passed\":true"))
        assert(response.contains("\"passed\":false"))
        assert(response.contains("\"测试失败\""))
        
        // 验证方法调用
        verify(testManager).getTestResultsByService(serviceId)
    }
}
