package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.core.cache.DataServiceCacheManager
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
 * 数据服务缓存控制器测试
 *
 * @author magicdb
 */
@WebMvcTest(DataServiceCacheController::class)
class DataServiceCacheControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @MockBean
    private lateinit var cacheManager: DataServiceCacheManager
    
    @BeforeEach
    fun setUp() {
        // 设置模拟对象的行为
    }
    
    @Test
    fun testGetStats() {
        // 准备测试数据
        val stats = mapOf(
            "test-service:123" to DataServiceCacheManager.CacheStats(
                hits = 10,
                misses = 2,
                puts = 1
            )
        )
        
        // 设置模拟对象的行为
        `when`(cacheManager.getStats()).thenReturn(stats)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/cache/stats")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assertTrue(response.contains("\"success\":true"))
        assertTrue(response.contains("\"test-service:123\""))
        assertTrue(response.contains("\"hits\":10"))
        
        // 验证方法调用
        verify(cacheManager).getStats()
    }
    
    @Test
    fun testClearServiceCache() {
        // 执行请求
        val result = mockMvc.perform(delete("/api/data-service/cache/service/test-service")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assertTrue(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(cacheManager).removeByServiceId("test-service")
    }
    
    @Test
    fun testClearCache() {
        // 执行请求
        val result = mockMvc.perform(delete("/api/data-service/cache/key/test-service:123")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assertTrue(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(cacheManager).remove("test-service:123")
    }
    
    @Test
    fun testClearAllCache() {
        // 执行请求
        val result = mockMvc.perform(delete("/api/data-service/cache/all")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assertTrue(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(cacheManager).clear()
    }
}
