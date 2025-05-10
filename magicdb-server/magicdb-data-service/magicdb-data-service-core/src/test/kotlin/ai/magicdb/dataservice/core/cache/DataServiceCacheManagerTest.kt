package ai.magicdb.dataservice.core.cache

import ai.magicdb.dataservice.api.model.ServiceResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.Serializable

/**
 * 数据服务缓存管理器测试
 *
 * @author magicdb
 */
class DataServiceCacheManagerTest {
    
    private lateinit var cacheManager: DataServiceCacheManager
    
    @BeforeEach
    fun setUp() {
        cacheManager = DataServiceCacheManager()
    }
    
    @Test
    fun testPutAndGet() {
        // 创建测试数据
        val cacheKey = "test-service:123"
        val result = ServiceResult(
            success = true,
            data = mapOf("message" to "Hello, World"),
            duration = 100
        ) as Serializable
        
        // 缓存结果
        cacheManager.put(cacheKey, result as ServiceResult, 60000)
        
        // 获取缓存结果
        val cachedResult = cacheManager.get(cacheKey)
        
        // 验证结果
        assertNotNull(cachedResult)
        assertTrue(cachedResult!!.success)
        assertTrue(cachedResult.fromCache)
        assertNotNull(cachedResult.cacheExpireTime)
        assertEquals(cacheKey, cachedResult.cacheKey)
        
        @Suppress("UNCHECKED_CAST")
        val data = cachedResult.data as Map<String, String>
        assertEquals("Hello, World", data["message"])
    }
    
    @Test
    fun testRemove() {
        // 创建测试数据
        val cacheKey = "test-service:456"
        val result = ServiceResult(
            success = true,
            data = mapOf("message" to "Hello, World"),
            duration = 100
        ) as Serializable
        
        // 缓存结果
        cacheManager.put(cacheKey, result as ServiceResult, 60000)
        
        // 验证缓存存在
        assertNotNull(cacheManager.get(cacheKey))
        
        // 移除缓存
        cacheManager.remove(cacheKey)
        
        // 验证缓存已移除
        assertNull(cacheManager.get(cacheKey))
    }
    
    @Test
    fun testRemoveByServiceId() {
        // 创建测试数据
        val serviceId = "test-service"
        val cacheKey1 = "service:$serviceId:param1"
        val cacheKey2 = "service:$serviceId:param2"
        val cacheKey3 = "service:other-service:param1"
        
        val result = ServiceResult(
            success = true,
            data = mapOf("message" to "Hello, World"),
            duration = 100
        ) as Serializable
        
        // 缓存结果
        cacheManager.put(cacheKey1, result as ServiceResult, 60000)
        cacheManager.put(cacheKey2, result, 60000)
        cacheManager.put(cacheKey3, result, 60000)
        
        // 验证缓存存在
        assertNotNull(cacheManager.get(cacheKey1))
        assertNotNull(cacheManager.get(cacheKey2))
        assertNotNull(cacheManager.get(cacheKey3))
        
        // 移除服务相关缓存
        cacheManager.removeByServiceId(serviceId)
        
        // 验证缓存已移除
        assertNull(cacheManager.get(cacheKey1))
        assertNull(cacheManager.get(cacheKey2))
        assertNotNull(cacheManager.get(cacheKey3))
    }
    
    @Test
    fun testClear() {
        // 创建测试数据
        val cacheKey1 = "test-service:123"
        val cacheKey2 = "test-service:456"
        
        val result = ServiceResult(
            success = true,
            data = mapOf("message" to "Hello, World"),
            duration = 100
        ) as Serializable
        
        // 缓存结果
        cacheManager.put(cacheKey1, result as ServiceResult, 60000)
        cacheManager.put(cacheKey2, result, 60000)
        
        // 验证缓存存在
        assertNotNull(cacheManager.get(cacheKey1))
        assertNotNull(cacheManager.get(cacheKey2))
        
        // 清除所有缓存
        cacheManager.clear()
        
        // 验证缓存已清除
        assertNull(cacheManager.get(cacheKey1))
        assertNull(cacheManager.get(cacheKey2))
        assertTrue(cacheManager.getStats().isEmpty())
    }
    
    @Test
    fun testGetStats() {
        // 创建测试数据
        val cacheKey = "test-service:789"
        val result = ServiceResult(
            success = true,
            data = mapOf("message" to "Hello, World"),
            duration = 100
        ) as Serializable
        
        // 缓存结果
        cacheManager.put(cacheKey, result as ServiceResult, 60000)
        
        // 获取缓存
        cacheManager.get(cacheKey)
        cacheManager.get("non-existent-key")
        
        // 获取统计信息
        val stats = cacheManager.getStats()
        
        // 验证统计信息
        assertNotNull(stats[cacheKey])
        assertEquals(1, stats[cacheKey]?.puts)
        assertEquals(1, stats[cacheKey]?.hits)
        assertEquals(0, stats[cacheKey]?.misses)
        
        // 验证不存在的键的统计信息
        assertNotNull(stats["non-existent-key"])
        assertEquals(0, stats["non-existent-key"]?.puts)
        assertEquals(0, stats["non-existent-key"]?.hits)
        assertEquals(1, stats["non-existent-key"]?.misses)
    }
}
