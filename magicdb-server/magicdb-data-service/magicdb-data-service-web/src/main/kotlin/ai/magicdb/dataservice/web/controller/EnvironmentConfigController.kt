package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.core.config.EnvironmentConfigManager
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.*

/**
 * 环境配置控制器
 * 用于管理环境配置
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/config")
class EnvironmentConfigController(
    private val configManager: EnvironmentConfigManager
) {
    
    /**
     * 获取当前环境
     */
    @GetMapping("/environment")
    fun getCurrentEnvironment(): DataResult<String> {
        return DataResult.of(configManager.getCurrentEnvironment())
    }
    
    /**
     * 获取所有配置属性
     */
    @GetMapping("/properties")
    fun getAllProperties(): DataResult<Map<String, String>> {
        return DataResult.of(configManager.getAllProperties())
    }
    
    /**
     * 获取配置属性
     *
     * @param key 属性键
     */
    @GetMapping("/property/{key}")
    fun getProperty(@PathVariable key: String): DataResult<String> {
        return DataResult.of(configManager.getProperty(key, ""))
    }
    
    /**
     * 设置配置属性
     *
     * @param key 属性键
     * @param value 属性值
     */
    @PostMapping("/property/{key}")
    fun setProperty(@PathVariable key: String, @RequestBody value: String): ActionResult {
        configManager.setProperty(key, value)
        return ActionResult.isSuccess()
    }
    
    /**
     * 设置多个配置属性
     *
     * @param properties 属性映射
     */
    @PostMapping("/properties")
    fun setProperties(@RequestBody properties: Map<String, String>): ActionResult {
        properties.forEach { (key, value) ->
            configManager.setProperty(key, value)
        }
        return ActionResult.isSuccess()
    }
}
