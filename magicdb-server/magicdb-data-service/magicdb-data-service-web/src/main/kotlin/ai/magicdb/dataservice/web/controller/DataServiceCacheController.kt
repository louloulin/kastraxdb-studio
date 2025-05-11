package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.core.cache.DataServiceCacheManager
import ai.magicdb.dataservice.core.cache.RedisDataServiceCacheManager
import ai.magicdb.dataservice.core.cluster.ServiceSynchronizer
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.*

/**
 * 数据服务缓存控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/cache")
class DataServiceCacheController(
    private val cacheManager: DataServiceCacheManager,
    private val redisCacheManager: RedisDataServiceCacheManager,
    private val serviceSynchronizer: ServiceSynchronizer
) {

    /**
     * 获取缓存统计信息
     */
    @GetMapping("/stats")
    fun getStats(): DataResult<Map<String, Any>> {
        // 合并本地缓存和Redis缓存的统计信息
        val localStats = cacheManager.getStats()
        val redisStats = redisCacheManager.getStats()
        
        val result = mutableMapOf<String, Any>()
        result["local"] = localStats
        result["redis"] = redisStats
        
        return DataResult.of(result)
    }

    /**
     * 清除指定服务的缓存
     *
     * @param serviceId 服务ID
     */
    @DeleteMapping("/service/{serviceId}")
    fun clearServiceCache(@PathVariable serviceId: String): ActionResult {
        // 清除本地缓存
        cacheManager.removeByServiceId(serviceId)
        
        // 清除Redis缓存
        redisCacheManager.removeByServiceId(serviceId)
        
        // 通知其他节点清除缓存
        serviceSynchronizer.publishServiceChange(
            ServiceSynchronizer.ChangeType.DELETE,
            ServiceSynchronizer.EntityType.CACHE,
            serviceId
        )
        
        return ActionResult.isSuccess()
    }

    /**
     * 清除指定缓存键的缓存
     *
     * @param cacheKey 缓存键
     */
    @DeleteMapping("/key/{cacheKey}")
    fun clearCache(@PathVariable cacheKey: String): ActionResult {
        // 清除本地缓存
        cacheManager.remove(cacheKey)
        
        // 清除Redis缓存
        redisCacheManager.remove(cacheKey)
        
        return ActionResult.isSuccess()
    }

    /**
     * 清除所有缓存
     */
    @DeleteMapping("/all")
    fun clearAllCache(): ActionResult {
        // 清除本地缓存
        cacheManager.clear()
        
        // 清除Redis缓存
        redisCacheManager.clear()
        
        // 通知其他节点清除缓存
        serviceSynchronizer.publishServiceChange(
            ServiceSynchronizer.ChangeType.DELETE,
            ServiceSynchronizer.EntityType.CACHE,
            "all"
        )
        
        return ActionResult.isSuccess()
    }
    
    /**
     * 获取集群缓存统计信息
     */
    @GetMapping("/cluster/stats")
    fun getClusterStats(): DataResult<Map<String, Any>> {
        // 获取Redis缓存统计信息
        val redisStats = redisCacheManager.getStats()
        
        return DataResult.of(mapOf("redis" to redisStats))
    }
}
