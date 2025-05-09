package ai.magicdb.script.runtime.limit

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * 资源限制器测试
 *
 * @author magicdb
 */
class ResourceLimiterTest {
    
    private lateinit var resourceLimiter: ResourceLimiter
    
    @BeforeEach
    fun setUp() {
        resourceLimiter = ResourceLimiter()
    }
    
    @Test
    fun testAcquireAndReleasePermit() {
        // 获取许可
        val acquired = resourceLimiter.acquirePermit("test-service", 1, TimeUnit.SECONDS)
        
        // 验证获取成功
        assertTrue(acquired)
        
        // 释放许可
        resourceLimiter.releasePermit("test-service", true)
        
        // 验证服务统计
        val stats = resourceLimiter.getServiceStats("test-service")
        assertEquals(1, stats.callCount)
        assertEquals(0, stats.errorCount)
        assertEquals(0.0, stats.errorRate)
        assertEquals(CircuitBreakerStatus.CLOSED, stats.circuitBreakerStatus)
    }
    
    @Test
    fun testServiceConcurrentLimit() {
        // 设置服务并发限制为2
        resourceLimiter.setServiceConcurrentLimit("test-service", 2)
        
        // 创建3个线程同时获取许可
        val latch = CountDownLatch(1)
        val successCount = AtomicInteger(0)
        val threads = mutableListOf<Thread>()
        
        for (i in 1..3) {
            val thread = Thread {
                try {
                    latch.await()
                    val acquired = resourceLimiter.acquirePermit("test-service", 1, TimeUnit.SECONDS)
                    if (acquired) {
                        successCount.incrementAndGet()
                        Thread.sleep(500) // 持有许可一段时间
                        resourceLimiter.releasePermit("test-service", true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            threads.add(thread)
            thread.start()
        }
        
        // 启动所有线程
        latch.countDown()
        
        // 等待所有线程完成
        for (thread in threads) {
            thread.join()
        }
        
        // 验证只有2个线程获取到许可
        assertEquals(2, successCount.get())
        
        // 验证服务统计
        val stats = resourceLimiter.getServiceStats("test-service")
        assertEquals(2, stats.callCount)
        assertEquals(0, stats.errorCount)
        assertEquals(0.0, stats.errorRate)
        assertEquals(CircuitBreakerStatus.CLOSED, stats.circuitBreakerStatus)
    }
    
    @Test
    fun testCircuitBreaker() {
        // 设置服务并发限制为10
        resourceLimiter.setServiceConcurrentLimit("test-service", 10)
        
        // 模拟6次调用，其中3次失败
        for (i in 1..6) {
            val acquired = resourceLimiter.acquirePermit("test-service", 1, TimeUnit.SECONDS)
            assertTrue(acquired)
            resourceLimiter.releasePermit("test-service", i > 3) // 前3次失败
        }
        
        // 验证服务统计
        val stats = resourceLimiter.getServiceStats("test-service")
        assertEquals(6, stats.callCount)
        assertEquals(3, stats.errorCount)
        assertEquals(0.5, stats.errorRate)
        assertEquals(CircuitBreakerStatus.OPEN, stats.circuitBreakerStatus) // 断路器应该打开
        
        // 尝试获取许可，应该失败
        val acquired = resourceLimiter.acquirePermit("test-service", 1, TimeUnit.SECONDS)
        assertFalse(acquired)
    }
    
    @Test
    fun testCircuitBreakerHalfOpen() {
        // 设置服务并发限制为10
        resourceLimiter.setServiceConcurrentLimit("test-service", 10)
        
        // 模拟6次调用，其中3次失败
        for (i in 1..6) {
            val acquired = resourceLimiter.acquirePermit("test-service", 1, TimeUnit.SECONDS)
            assertTrue(acquired)
            resourceLimiter.releasePermit("test-service", i > 3) // 前3次失败
        }
        
        // 验证断路器打开
        var stats = resourceLimiter.getServiceStats("test-service")
        assertEquals(CircuitBreakerStatus.OPEN, stats.circuitBreakerStatus)
        
        // 修改最后错误时间为很久以前，使断路器进入半开状态
        val field = ResourceLimiter::class.java.getDeclaredField("serviceLastErrorTimes")
        field.isAccessible = true
        val serviceLastErrorTimes = field.get(resourceLimiter) as Map<*, *>
        val lastErrorTime = serviceLastErrorTimes["test-service"]
        val timeField = lastErrorTime!!.javaClass.getDeclaredField("value")
        timeField.isAccessible = true
        timeField.set(lastErrorTime, System.currentTimeMillis() - 70000) // 70秒前
        
        // 尝试获取许可，应该成功（断路器半开）
        val acquired = resourceLimiter.acquirePermit("test-service", 1, TimeUnit.SECONDS)
        assertTrue(acquired)
        
        // 验证断路器半开
        stats = resourceLimiter.getServiceStats("test-service")
        assertEquals(CircuitBreakerStatus.HALF_OPEN, stats.circuitBreakerStatus)
        
        // 释放许可，模拟成功执行
        resourceLimiter.releasePermit("test-service", true)
        
        // 验证断路器关闭
        stats = resourceLimiter.getServiceStats("test-service")
        assertEquals(CircuitBreakerStatus.CLOSED, stats.circuitBreakerStatus)
    }
    
    @Test
    fun testResetServiceStats() {
        // 模拟3次调用，其中1次失败
        for (i in 1..3) {
            val acquired = resourceLimiter.acquirePermit("test-service", 1, TimeUnit.SECONDS)
            assertTrue(acquired)
            resourceLimiter.releasePermit("test-service", i != 2) // 第2次失败
        }
        
        // 验证服务统计
        var stats = resourceLimiter.getServiceStats("test-service")
        assertEquals(3, stats.callCount)
        assertEquals(1, stats.errorCount)
        
        // 重置服务统计
        resourceLimiter.resetServiceStats("test-service")
        
        // 验证统计已重置
        stats = resourceLimiter.getServiceStats("test-service")
        assertEquals(0, stats.callCount)
        assertEquals(0, stats.errorCount)
        assertEquals(CircuitBreakerStatus.CLOSED, stats.circuitBreakerStatus)
    }
    
    @Test
    fun testGetAllServiceStats() {
        // 模拟多个服务的调用
        resourceLimiter.acquirePermit("service1", 1, TimeUnit.SECONDS)
        resourceLimiter.releasePermit("service1", true)
        
        resourceLimiter.acquirePermit("service2", 1, TimeUnit.SECONDS)
        resourceLimiter.releasePermit("service2", false)
        
        resourceLimiter.acquirePermit("service3", 1, TimeUnit.SECONDS)
        resourceLimiter.releasePermit("service3", true)
        
        // 获取所有服务统计
        val allStats = resourceLimiter.getAllServiceStats()
        
        // 验证结果
        assertEquals(3, allStats.size)
        assertTrue(allStats.containsKey("service1"))
        assertTrue(allStats.containsKey("service2"))
        assertTrue(allStats.containsKey("service3"))
        
        assertEquals(1, allStats["service1"]?.callCount)
        assertEquals(0, allStats["service1"]?.errorCount)
        
        assertEquals(1, allStats["service2"]?.callCount)
        assertEquals(1, allStats["service2"]?.errorCount)
        
        assertEquals(1, allStats["service3"]?.callCount)
        assertEquals(0, allStats["service3"]?.errorCount)
    }
}
