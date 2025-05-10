package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.core.cache.DataServiceCacheManager
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
class DataServiceCacheController(private val cacheManager: DataServiceCacheManager) {

    /**
     * 获取缓存统计信息
     */
    @GetMapping("/stats")
    fun getStats(): DataResult<Map<String, DataServiceCacheManager.CacheStats>> {
        return DataResult.of(cacheManager.getStats())
    }

    /**
     * 清除指定服务的缓存
     *
     * @param serviceId 服务ID
     */
    @DeleteMapping("/service/{serviceId}")
    fun clearServiceCache(@PathVariable serviceId: String): ActionResult {
        cacheManager.removeByServiceId(serviceId)
        return ActionResult.isSuccess()
    }

    /**
     * 清除指定缓存键的缓存
     *
     * @param cacheKey 缓存键
     */
    @DeleteMapping("/key/{cacheKey}")
    fun clearCache(@PathVariable cacheKey: String): ActionResult {
        cacheManager.remove(cacheKey)
        return ActionResult.isSuccess()
    }

    /**
     * 清除所有缓存
     */
    @DeleteMapping("/all")
    fun clearAllCache(): ActionResult {
        cacheManager.clear()
        return ActionResult.isSuccess()
    }
}
