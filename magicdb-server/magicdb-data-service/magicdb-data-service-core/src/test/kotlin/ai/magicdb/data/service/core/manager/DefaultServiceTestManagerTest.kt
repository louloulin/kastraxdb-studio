package ai.magicdb.data.service.core.manager

import ai.magicdb.data.service.api.DataServiceExecutor
import ai.magicdb.data.service.api.DataServiceRepository
import ai.magicdb.data.service.api.ServiceTestRepository
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceResult
import ai.magicdb.data.service.api.model.ServiceTest
import ai.magicdb.data.service.api.model.ServiceTestResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.*

/**
 * 默认服务测试管理器测试
 *
 * @author magicdb
 */
class DefaultServiceTestManagerTest {
    
    private lateinit var testManager: DefaultServiceTestManager
    private lateinit var testRepository: ServiceTestRepository
    private lateinit var serviceRepository: DataServiceRepository
    private lateinit var serviceExecutor: DataServiceExecutor
    
    @BeforeEach
    fun setUp() {
        testRepository = mock(ServiceTestRepository::class.java)
        serviceRepository = mock(DataServiceRepository::class.java)
        serviceExecutor = mock(DataServiceExecutor::class.java)
        testManager = DefaultServiceTestManager(testRepository, serviceRepository, serviceExecutor)
    }
    
    @Test
    fun testCreateTest() {
        // 准备测试数据
        val serviceId = "service-123"
        val service = DataService(id = serviceId, name = "测试服务")
        val test = ServiceTest(
            name = "测试用例1",
            serviceId = serviceId,
            parameters = mapOf("name" to "World"),
            expectedResult = mapOf("success" to true)
        )
        
        // 设置模拟对象的行为
        `when`(serviceRepository.getService(serviceId)).thenReturn(service)
        `when`(testRepository.saveTest(test)).thenReturn("test-123")
        
        // 执行测试
        val testId = testManager.createTest(test)
        
        // 验证结果
        assertEquals("test-123", testId)
        
        // 验证方法调用
        verify(serviceRepository).getService(serviceId)
        verify(testRepository).saveTest(test)
    }
    
    @Test
    fun testUpdateTest() {
        // 准备测试数据
        val testId = "test-123"
        val serviceId = "service-123"
        val service = DataService(id = serviceId, name = "测试服务")
        val existingTest = ServiceTest(
            id = testId,
            name = "测试用例1",
            serviceId = serviceId,
            parameters = mapOf("name" to "World"),
            expectedResult = mapOf("success" to true),
            createTime = 1000,
            createUserId = 1
        )
        val updatedTest = ServiceTest(
            id = testId,
            name = "测试用例1（更新）",
            serviceId = serviceId,
            parameters = mapOf("name" to "World"),
            expectedResult = mapOf("success" to true)
        )
        
        // 设置模拟对象的行为
        `when`(testRepository.getTest(testId)).thenReturn(existingTest)
        `when`(serviceRepository.getService(serviceId)).thenReturn(service)
        `when`(testRepository.saveTest(any())).thenReturn(testId)
        
        // 执行测试
        val success = testManager.updateTest(updatedTest)
        
        // 验证结果
        assertTrue(success)
        assertEquals(1000, updatedTest.createTime)
        assertEquals(1, updatedTest.createUserId)
        
        // 验证方法调用
        verify(testRepository).getTest(testId)
        verify(serviceRepository).getService(serviceId)
        verify(testRepository).saveTest(updatedTest)
    }
    
    @Test
    fun testDeleteTest() {
        // 准备测试数据
        val testId = "test-123"
        
        // 设置模拟对象的行为
        `when`(testRepository.deleteTest(testId)).thenReturn(true)
        
        // 执行测试
        val success = testManager.deleteTest(testId)
        
        // 验证结果
        assertTrue(success)
        
        // 验证方法调用
        verify(testRepository).deleteTest(testId)
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
        `when`(testRepository.getTest(testId)).thenReturn(test)
        
        // 执行测试
        val result = testManager.getTest(testId)
        
        // 验证结果
        assertNotNull(result)
        assertEquals(testId, result?.id)
        assertEquals("测试用例1", result?.name)
        
        // 验证方法调用
        verify(testRepository).getTest(testId)
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
        `when`(testRepository.getTestsByService(serviceId)).thenReturn(tests)
        
        // 执行测试
        val result = testManager.getTestsByService(serviceId)
        
        // 验证结果
        assertEquals(2, result.size)
        assertEquals("测试用例1", result[0].name)
        assertEquals("测试用例2", result[1].name)
        
        // 验证方法调用
        verify(testRepository).getTestsByService(serviceId)
    }
    
    @Test
    fun testRunTest() {
        // 准备测试数据
        val testId = "test-123"
        val serviceId = "service-123"
        val test = ServiceTest(
            id = testId,
            name = "测试用例1",
            serviceId = serviceId,
            parameters = mapOf("name" to "World"),
            expectedResult = mapOf("success" to true, "data" to mapOf("message" to "Hello, World"))
        )
        val serviceResult = ServiceResult(
            success = true,
            data = mapOf("message" to "Hello, World"),
            duration = 100
        )
        
        // 设置模拟对象的行为
        `when`(testRepository.getTest(testId)).thenReturn(test)
        `when`(serviceExecutor.execute(serviceId, test.parameters)).thenReturn(serviceResult)
        `when`(testRepository.saveTestResult(any())).thenReturn(true)
        
        // 执行测试
        val result = testManager.runTest(testId)
        
        // 验证结果
        assertNotNull(result)
        assertEquals(testId, result.testId)
        assertEquals("测试用例1", result.testName)
        assertEquals(serviceId, result.serviceId)
        assertTrue(result.passed)
        
        // 验证方法调用
        verify(testRepository).getTest(testId)
        verify(serviceExecutor).execute(serviceId, test.parameters)
        verify(testRepository).saveTestResult(any())
    }
    
    @Test
    fun testRunTestWithFailure() {
        // 准备测试数据
        val testId = "test-123"
        val serviceId = "service-123"
        val test = ServiceTest(
            id = testId,
            name = "测试用例1",
            serviceId = serviceId,
            parameters = mapOf("name" to "World"),
            expectedResult = mapOf("success" to true, "data" to mapOf("message" to "Hello, World"))
        )
        val serviceResult = ServiceResult(
            success = false,
            message = "执行失败",
            duration = 100
        )
        
        // 设置模拟对象的行为
        `when`(testRepository.getTest(testId)).thenReturn(test)
        `when`(serviceExecutor.execute(serviceId, test.parameters)).thenReturn(serviceResult)
        `when`(testRepository.saveTestResult(any())).thenReturn(true)
        
        // 执行测试
        val result = testManager.runTest(testId)
        
        // 验证结果
        assertNotNull(result)
        assertEquals(testId, result.testId)
        assertEquals("测试用例1", result.testName)
        assertEquals(serviceId, result.serviceId)
        assertFalse(result.passed)
        assertEquals("测试结果与预期不符", result.errorMessage)
        
        // 验证方法调用
        verify(testRepository).getTest(testId)
        verify(serviceExecutor).execute(serviceId, test.parameters)
        verify(testRepository).saveTestResult(any())
    }
    
    @Test
    fun testRunTestWithException() {
        // 准备测试数据
        val testId = "test-123"
        val serviceId = "service-123"
        val test = ServiceTest(
            id = testId,
            name = "测试用例1",
            serviceId = serviceId,
            parameters = mapOf("name" to "World"),
            expectedResult = mapOf("success" to true, "data" to mapOf("message" to "Hello, World"))
        )
        
        // 设置模拟对象的行为
        `when`(testRepository.getTest(testId)).thenReturn(test)
        `when`(serviceExecutor.execute(serviceId, test.parameters)).thenThrow(RuntimeException("执行异常"))
        `when`(testRepository.saveTestResult(any())).thenReturn(true)
        
        // 执行测试
        val result = testManager.runTest(testId)
        
        // 验证结果
        assertNotNull(result)
        assertEquals(testId, result.testId)
        assertEquals("测试用例1", result.testName)
        assertEquals(serviceId, result.serviceId)
        assertFalse(result.passed)
        assertEquals("执行异常", result.errorMessage)
        
        // 验证方法调用
        verify(testRepository).getTest(testId)
        verify(serviceExecutor).execute(serviceId, test.parameters)
        verify(testRepository).saveTestResult(any())
    }
    
    @Test
    fun testRunTestsByService() {
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
            passed = true
        )
        
        // 设置模拟对象的行为
        `when`(testRepository.getTestsByService(serviceId)).thenReturn(tests)
        `when`(testManager.runTest("test-123")).thenReturn(result1)
        `when`(testManager.runTest("test-456")).thenReturn(result2)
        
        // 执行测试
        val results = testManager.runTestsByService(serviceId)
        
        // 验证结果
        assertEquals(2, results.size)
        assertEquals("test-123", results[0].testId)
        assertEquals("test-456", results[1].testId)
        
        // 验证方法调用
        verify(testRepository).getTestsByService(serviceId)
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
        `when`(testRepository.getTestResult(testId)).thenReturn(result)
        
        // 执行测试
        val testResult = testManager.getTestResult(testId)
        
        // 验证结果
        assertNotNull(testResult)
        assertEquals(testId, testResult?.testId)
        assertEquals("测试用例1", testResult?.testName)
        assertTrue(testResult?.passed ?: false)
        
        // 验证方法调用
        verify(testRepository).getTestResult(testId)
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
        `when`(testRepository.getTestResultsByService(serviceId)).thenReturn(results)
        
        // 执行测试
        val testResults = testManager.getTestResultsByService(serviceId)
        
        // 验证结果
        assertEquals(2, testResults.size)
        assertEquals("test-123", testResults[0].testId)
        assertEquals("test-456", testResults[1].testId)
        assertTrue(testResults[0].passed)
        assertFalse(testResults[1].passed)
        
        // 验证方法调用
        verify(testRepository).getTestResultsByService(serviceId)
    }
}
