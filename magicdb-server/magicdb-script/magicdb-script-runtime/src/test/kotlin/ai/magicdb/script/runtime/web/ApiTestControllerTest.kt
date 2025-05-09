package ai.magicdb.script.runtime.web

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class ApiTestControllerTest {

    @Mock
    private lateinit var restTemplate: RestTemplate

    @InjectMocks
    private lateinit var apiTestController: ApiTestController

    @Test
    fun testApiSuccess() {
        // 准备测试数据
        val request = ApiTestRequest(
            method = "GET",
            url = "https://api.example.com/test",
            params = mapOf("param1" to "value1"),
            headers = mapOf("Content-Type" to "application/json"),
            body = null
        )
        
        // 模拟RestTemplate的响应
        val responseBody = """{"message": "success"}"""
        val responseEntity = ResponseEntity.ok(responseBody)
        
        `when`(restTemplate.exchange(
            any(URI::class.java),
            eq(HttpMethod.GET),
            any(HttpEntity::class.java),
            eq(String::class.java)
        )).thenReturn(responseEntity)
        
        // 执行测试
        val result = apiTestController.testApi(request)
        
        // 验证结果
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        
        val response = result.body
        assertNotNull(response)
        assertEquals(200, response.status)
        assertEquals("OK", response.statusText)
        assertEquals(responseBody, response.data)
    }

    @Test
    fun testApiError() {
        // 准备测试数据
        val request = ApiTestRequest(
            method = "POST",
            url = "https://api.example.com/test",
            params = null,
            headers = null,
            body = mapOf("key" to "value")
        )
        
        // 模拟RestTemplate抛出异常
        `when`(restTemplate.exchange(
            any(URI::class.java),
            eq(HttpMethod.POST),
            any(HttpEntity::class.java),
            eq(String::class.java)
        )).thenThrow(RuntimeException("Connection refused"))
        
        // 执行测试
        val result = apiTestController.testApi(request)
        
        // 验证结果
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        
        val response = result.body
        assertNotNull(response)
        assertEquals(500, response.status)
        assertEquals("Internal Server Error", response.statusText)
        
        @Suppress("UNCHECKED_CAST")
        val errorData = response.data as Map<String, String>
        assertEquals("Connection refused", errorData["error"])
    }
}
