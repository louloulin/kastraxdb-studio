package ai.magicdb.dataservice.core.cache

import ai.magicdb.dataservice.api.model.ServiceResult
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Redis 数据服务缓存管理器
 * 基于 Redis 实现的分布式缓存管理器
 *
 * @author magicdb
 */
@Component
class RedisDataServiceCacheManager(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val logger = LoggerFactory.getLogger(RedisDataServiceCacheManager::class.java)
    
    // 缓存前缀
    private val CACHE_PREFIX = "data_service:cache:"
    
    // 缓存统计信息前缀
    private val STATS_PREFIX = "data_service:stats:"
    
    /**
     * 获取缓存结果
     *
     * @param cacheKey 缓存键
     * @return 缓存结果，如果不存在则返回null
     */
    fun get(cacheKey: String): ServiceResult? {
        val key = CACHE_PREFIX + cacheKey
        val result = redisTemplate.opsForValue().get(key) as? ServiceResult
        
        if (result != null) {
            // 更新缓存统计
            updateCacheStats(cacheKey, CacheOperation.HIT)
            logger.debug("Redis缓存命中: {}", cacheKey)
        } else {
            // 更新缓存统计
            updateCacheStats(cacheKey, CacheOperation.MISS)
            logger.debug("Redis缓存未命中: {}", cacheKey)
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
            fromCache = true,
            cacheKey = cacheKey
        )
        
        // 缓存结果
        val key = CACHE_PREFIX + cacheKey
        redisTemplate.opsForValue().set(key, cacheResult, ttl, TimeUnit.MILLISECONDS)
        
        // 更新缓存统计
        updateCacheStats(cacheKey, CacheOperation.PUT)
        
        logger.debug("Redis缓存结果: {}, TTL: {}ms", cacheKey, ttl)
    }
    
    /**
     * 清除缓存
     *
     * @param cacheKey 缓存键
     */
    fun remove(cacheKey: String) {
        // 从缓存中移除
        val key = CACHE_PREFIX + cacheKey
        redisTemplate.delete(key)
        
        // 更新缓存统计
        updateCacheStats(cacheKey, CacheOperation.REMOVE)
        
        logger.debug("移除Redis缓存: {}", cacheKey)
    }
    
    /**
     * 清除服务相关的所有缓存
     *
     * @param serviceId 服务ID
     */
    fun removeByServiceId(serviceId: String) {
        // 查找服务相关的缓存键
        val pattern = CACHE_PREFIX + "service:$serviceId:*"
        val keys = redisTemplate.keys(pattern)
        
        if (keys.isNotEmpty()) {
            // 移除缓存
            redisTemplate.delete(keys)
            
            logger.debug("移除服务相关Redis缓存: {}, 共{}个", serviceId, keys.size)
        }
    }
    
    /**
     * 清除所有缓存
     */
    fun clear() {
        // 清除所有缓存
        val keys = redisTemplate.keys("$CACHE_PREFIX*")
        if (keys.isNotEmpty()) {
            redisTemplate.delete(keys)
        }
        
        // 清除所有统计信息
        val statsKeys = redisTemplate.keys("$STATS_PREFIX*")
        if (statsKeys.isNotEmpty()) {
            redisTemplate.delete(statsKeys)
        }
        
        logger.info("清除所有Redis缓存")
    }
    
    /**
     * 获取缓存统计信息
     *
     * @return 缓存统计信息
     */
    fun getStats(): Map<String, CacheStats> {
        val statsKeys = redisTemplate.keys("$STATS_PREFIX*")
        val stats = mutableMapOf<String, CacheStats>()
        
        statsKeys.forEach { key ->
            val cacheKey = key.substring(STATS_PREFIX.length)
            val cacheStats = redisTemplate.opsForValue().get(key) as? CacheStats
            if (cacheStats != null) {
                stats[cacheKey] = cacheStats
            }
        }
        
        return stats
    }
    
    /**
     * 更新缓存统计
     *
     * @param cacheKey 缓存键
     * @param operation 缓存操作
     */
    private fun updateCacheStats(cacheKey: String, operation: CacheOperation) {
        val statsKey = STATS_PREFIX + cacheKey
        
        // 获取当前统计信息
        var stats = redisTemplate.opsForValue().get(statsKey) as? CacheStats
        if (stats == null) {
            stats = CacheStats()
        }
        
        // 更新统计信息
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
        
        // 保存统计信息
        redisTemplate.opsForValue().set(statsKey, stats)
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
