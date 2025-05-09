package ai.magicdb.script.runtime.web

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 系统配置控制器
 *
 * @author magicdb
 */
@RestController("scriptConfigController")
@RequestMapping("/api/script/config")
class ConfigController {
    private val logger = LoggerFactory.getLogger(ConfigController::class.java)

    // 模拟配置存储
    private val configs = ConcurrentHashMap<String, Config>()

    init {
        // 添加默认配置
        val defaultConfigs = listOf(
            Config(
                id = "1",
                key = "system.name",
                value = "MagicDB",
                description = "系统名称",
                createTime = Date(),
                updateTime = Date()
            ),
            Config(
                id = "2",
                key = "system.version",
                value = "1.0.0",
                description = "系统版本",
                createTime = Date(),
                updateTime = Date()
            ),
            Config(
                id = "3",
                key = "system.theme",
                value = "light",
                description = "系统主题",
                createTime = Date(),
                updateTime = Date()
            ),
            Config(
                id = "4",
                key = "system.language",
                value = "zh_CN",
                description = "系统语言",
                createTime = Date(),
                updateTime = Date()
            ),
            Config(
                id = "5",
                key = "script.defaultLanguage",
                value = "javascript",
                description = "脚本默认语言",
                createTime = Date(),
                updateTime = Date()
            ),
            Config(
                id = "6",
                key = "script.timeout",
                value = "30000",
                description = "脚本执行超时时间（毫秒）",
                createTime = Date(),
                updateTime = Date()
            ),
            Config(
                id = "7",
                key = "dataService.defaultCacheTime",
                value = "60000",
                description = "数据服务默认缓存时间（毫秒）",
                createTime = Date(),
                updateTime = Date()
            )
        )

        defaultConfigs.forEach { config ->
            configs[config.id] = config
        }
    }

    /**
     * 获取配置列表
     */
    @GetMapping
    fun getConfigList(): ResponseEntity<List<Config>> {
        logger.info("获取配置列表")

        val configList = configs.values.toList()
        return ResponseEntity.ok(configList)
    }

    /**
     * 获取配置详情
     */
    @GetMapping("/{id}")
    fun getConfig(@PathVariable id: String): ResponseEntity<Config> {
        logger.info("获取配置详情: {}", id)

        val config = configs[id] ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(config)
    }

    /**
     * 根据键获取配置
     */
    @GetMapping("/key/{key}")
    fun getConfigByKey(@PathVariable key: String): ResponseEntity<Config> {
        logger.info("根据键获取配置: {}", key)

        val config = configs.values.find { it.key == key } ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(config)
    }

    /**
     * 创建或更新配置
     */
    @PostMapping
    fun createOrUpdateConfig(@RequestBody request: ConfigRequest): ResponseEntity<Config> {
        logger.info("创建或更新配置: {}", request)

        // 检查键是否已存在
        val existingConfig = configs.values.find { it.key == request.key }

        val now = Date()
        val config = if (existingConfig != null) {
            // 更新配置
            val updatedConfig = existingConfig.copy(
                value = request.value,
                description = request.description ?: existingConfig.description,
                updateTime = now
            )

            configs[existingConfig.id] = updatedConfig
            updatedConfig
        } else {
            // 创建配置
            val newConfig = Config(
                id = UUID.randomUUID().toString(),
                key = request.key,
                value = request.value,
                description = request.description ?: "",
                createTime = now,
                updateTime = now
            )

            configs[newConfig.id] = newConfig
            newConfig
        }

        return ResponseEntity.ok(config)
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    fun deleteConfig(@PathVariable id: String): ResponseEntity<Boolean> {
        logger.info("删除配置: {}", id)

        if (!configs.containsKey(id)) {
            return ResponseEntity.notFound().build()
        }

        configs.remove(id)
        return ResponseEntity.ok(true)
    }
}

/**
 * 配置实体
 */
data class Config(
    val id: String,
    val key: String,
    val value: String,
    val description: String,
    val createTime: Date,
    val updateTime: Date
)

/**
 * 配置请求
 */
data class ConfigRequest(
    val key: String,
    val value: String,
    val description: String? = null
)
