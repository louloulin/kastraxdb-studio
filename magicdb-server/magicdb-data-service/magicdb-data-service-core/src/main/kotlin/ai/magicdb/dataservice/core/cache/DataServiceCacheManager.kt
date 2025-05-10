package ai.magicdb.dataservice.core.cache

import ai.magicdb.dataservice.api.model.ServiceResult
import ai.magicdb.server.domain.core.cache.MemoryCacheManage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * 数据服务缓存管理器
 *
 * @author magicdb
 */
@Component
class DataServiceCacheManager {
    private val logger = LoggerFactory.getLogger(DataServiceCacheManager::class.java)
    
    // 缓存统计信息
    private val cacheStats = ConcurrentHashMap<String, CacheStats>()
    
    // 缓存清理调度器
    private val cleanupScheduler = Executors.newScheduledThreadPool(1)
    
    init {
        // 启动定期清理任务
        cleanupScheduler.scheduleAtFixedRate(
            { cleanupExpiredCache() },
            30, 30, TimeUnit.MINUTES
        )
    }
    
    /**
     * 获取缓存结果
     *
     * @param cacheKey 缓存键
     * @return 缓存结果，如果不存在则返回null
     */
    fun get(cacheKey: String): ServiceResult? {
        val result = MemoryCacheManage.get<ServiceResult>(cacheKey)
        if (result != null) {
            // 更新缓存统计
            updateCacheStats(cacheKey, CacheOperation.HIT)
            logger.debug("缓存命中: {}", cacheKey)
        } else {
            // 更新缓存统计
            updateCacheStats(cacheKey, CacheOperation.MISS)
            logger.debug("缓存未命中: {}", cacheKey)
        }
        return result
    }
    
    /**
     * 缓存结果
     *
     * @param cacheKey 缓存键
     * @param result 缓存结果
     * @param ttl 缓存时间（毫秒）
     */
    fun put(cacheKey: String, result: ServiceResult, ttl: Long) {
        if (ttl <= 0) {
            logger.debug("缓存时间为0，不缓存: {}", cacheKey)
            return
        }
        
        // 设置缓存过期时间
        val cacheResult = result.copy(
            cacheExpireTime = System.currentTimeMillis() + ttl,
            fromCache = true
        )
        
        // 缓存结果
        MemoryCacheManage.put(cacheKey, cacheResult as Serializable)
        
        // 更新缓存统计
        updateCacheStats(cacheKey, CacheOperation.PUT)
        
        logger.debug("缓存结果: {}, TTL: {}ms", cacheKey, ttl)
    }
    
    /**
     * 清除缓存
     *
     * @param cacheKey 缓存键
     */
    fun remove(cacheKey: String) {
        // 从缓存中移除
        MemoryCacheManage.put(cacheKey, null)
        
        // 更新缓存统计
        updateCacheStats(cacheKey, CacheOperation.REMOVE)
        
        logger.debug("移除缓存: {}", cacheKey)
    }
    
    /**
     * 清除服务相关的所有缓存
     *
     * @param serviceId 服务ID
     */
    fun removeByServiceId(serviceId: String) {
        // 查找服务相关的缓存键
        val keysToRemove = cacheStats.keys
            .filter { it.startsWith("service:$serviceId:") }
        
        // 移除缓存
        keysToRemove.forEach { remove(it) }
        
        logger.debug("移除服务相关缓存: {}, 共{}个", serviceId, keysToRemove.size)
    }
    
    /**
     * 清除所有缓存
     */
    fun clear() {
        // 清除缓存统计
        cacheStats.clear()
        
        logger.info("清除所有缓存")
    }
    
    /**
     * 获取缓存统计信息
     *
     * @return 缓存统计信息
     */
    fun getStats(): Map<String, CacheStats> {
        return cacheStats.toMap()
    }
    
    /**
     * 清理过期缓存
     */
    private fun cleanupExpiredCache() {
        val now = System.currentTimeMillis()
        var expiredCount = 0
        
        // 查找过期的缓存
        cacheStats.keys.forEach { cacheKey ->
            val result = MemoryCacheManage.get<ServiceResult>(cacheKey)
            if (result != null && result.cacheExpireTime != null && result.cacheExpireTime!! < now) {
                // 移除过期缓存
                remove(cacheKey)
                expiredCount++
            }
        }
        
        if (expiredCount > 0) {
            logger.info("清理过期缓存: {}个", expiredCount)
        }
    }
    
    /**
     * 更新缓存统计
     *
     * @param cacheKey 缓存键
     * @param operation 缓存操作
     */
    private fun updateCacheStats(cacheKey: String, operation: CacheOperation) {
        val stats = cacheStats.computeIfAbsent(cacheKey) { CacheStats() }
        
        when (operation) {
            CacheOperation.HIT -> {
                stats.hits++
                stats.lastHitTime = System.currentTimeMillis()
            }
            CacheOperation.MISS -> {
                stats.misses++
                stats.lastMissTime = System.currentTimeMillis()
            }
            CacheOperation.PUT -> {
                stats.puts++
                stats.lastPutTime = System.currentTimeMillis()
            }
            CacheOperation.REMOVE -> {
                stats.removes++
                stats.lastRemoveTime = System.currentTimeMillis()
            }
        }
    }
    
    /**
     * 缓存操作枚举
     */
    private enum class CacheOperation {
        HIT, MISS, PUT, REMOVE
    }
    
    /**
     * 缓存统计信息
     */
    data class CacheStats(
        var hits: Long = 0,
        var misses: Long = 0,
        var puts: Long = 0,
        var removes: Long = 0,
        var lastHitTime: Long? = null,
        var lastMissTime: Long? = null,
        var lastPutTime: Long? = null,
        var lastRemoveTime: Long? = null
    ) : Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }
}
