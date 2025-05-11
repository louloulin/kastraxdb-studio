package ai.magicdb.dataservice.core.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

/**
 * 环境配置管理器
 * 负责管理不同环境的配置信息
 *
 * @author magicdb
 */
@Component
class EnvironmentConfigManager(
    private val environment: Environment,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(EnvironmentConfigManager::class.java)
    
    @Value("\${spring.profiles.active:dev}")
    private lateinit var activeProfile: String
    
    // 配置文件目录
    private val configDir = System.getProperty("user.home") + File.separator + ".magicdb" + 
            File.separator + "config" + File.separator + "data-service"
    
    // 配置文件路径
    private lateinit var configFilePath: String
    
    // 配置历史文件路径
    private lateinit var configHistoryFilePath: String
    
    // 当前配置
    private lateinit var currentConfig: EnvironmentConfig
    
    /**
     * 初始化
     */
    @PostConstruct
    fun init() {
        try {
            // 创建配置目录
            val configDirFile = File(configDir)
            if (!configDirFile.exists()) {
                configDirFile.mkdirs()
            }
            
            // 设置配置文件路径
            configFilePath = "$configDir${File.separator}config_$activeProfile.json"
            configHistoryFilePath = "$configDir${File.separator}config_history_$activeProfile.txt"
            
            // 加载配置
            loadConfig()
            
            logger.info("环境配置管理器初始化完成，当前环境: {}", activeProfile)
        } catch (e: Exception) {
            logger.error("初始化环境配置管理器失败", e)
        }
    }
    
    /**
     * 加载配置
     */
    private fun loadConfig() {
        val configFile = File(configFilePath)
        if (!configFile.exists()) {
            // 创建默认配置
            currentConfig = EnvironmentConfig(
                environment = activeProfile,
                updateTime = LocalDateTime.now(),
                properties = mutableMapOf(
                    "data-service.cluster.enabled" to "false",
                    "data-service.cache.enabled" to "true",
                    "data-service.debug.enabled" to activeProfile != "prod"
                )
            )
            
            // 保存配置
            saveConfig()
        } else {
            try {
                // 读取配置
                currentConfig = objectMapper.readValue(configFile, EnvironmentConfig::class.java)
            } catch (e: Exception) {
                logger.error("读取配置文件失败", e)
                
                // 创建默认配置
                currentConfig = EnvironmentConfig(
                    environment = activeProfile,
                    updateTime = LocalDateTime.now(),
                    properties = mutableMapOf(
                        "data-service.cluster.enabled" to "false",
                        "data-service.cache.enabled" to "true",
                        "data-service.debug.enabled" to activeProfile != "prod"
                    )
                )
                
                // 保存配置
                saveConfig()
            }
        }
    }
    
    /**
     * 保存配置
     */
    private fun saveConfig() {
        try {
            // 更新时间
            currentConfig.updateTime = LocalDateTime.now()
            
            // 保存配置
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(File(configFilePath), currentConfig)
            
            // 添加配置历史
            addConfigHistory("配置更新")
        } catch (e: Exception) {
            logger.error("保存配置文件失败", e)
        }
    }
    
    /**
     * 添加配置历史
     */
    private fun addConfigHistory(description: String) {
        try {
            val historyLine = "${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)},$description\n"
            
            val historyFile = File(configHistoryFilePath)
            if (!historyFile.exists()) {
                historyFile.createNewFile()
            }
            
            Files.write(
                Paths.get(configHistoryFilePath),
                historyLine.toByteArray(),
                StandardOpenOption.APPEND
            )
        } catch (e: Exception) {
            logger.error("添加配置历史失败", e)
        }
    }
    
    /**
     * 获取配置属性
     *
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    fun getProperty(key: String, defaultValue: String): String {
        return currentConfig.properties[key] ?: defaultValue
    }
    
    /**
     * 获取配置属性
     *
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    fun getProperty(key: String, defaultValue: Boolean): Boolean {
        val value = currentConfig.properties[key] ?: return defaultValue
        return value.toBoolean()
    }
    
    /**
     * 获取配置属性
     *
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    fun getProperty(key: String, defaultValue: Int): Int {
        val value = currentConfig.properties[key] ?: return defaultValue
        return value.toIntOrNull() ?: defaultValue
    }
    
    /**
     * 获取配置属性
     *
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    fun getProperty(key: String, defaultValue: Long): Long {
        val value = currentConfig.properties[key] ?: return defaultValue
        return value.toLongOrNull() ?: defaultValue
    }
    
    /**
     * 设置配置属性
     *
     * @param key 属性键
     * @param value 属性值
     */
    fun setProperty(key: String, value: String) {
        currentConfig.properties[key] = value
        saveConfig()
    }
    
    /**
     * 设置配置属性
     *
     * @param key 属性键
     * @param value 属性值
     */
    fun setProperty(key: String, value: Boolean) {
        currentConfig.properties[key] = value.toString()
        saveConfig()
    }
    
    /**
     * 设置配置属性
     *
     * @param key 属性键
     * @param value 属性值
     */
    fun setProperty(key: String, value: Int) {
        currentConfig.properties[key] = value.toString()
        saveConfig()
    }
    
    /**
     * 设置配置属性
     *
     * @param key 属性键
     * @param value 属性值
     */
    fun setProperty(key: String, value: Long) {
        currentConfig.properties[key] = value.toString()
        saveConfig()
    }
    
    /**
     * 获取所有配置属性
     *
     * @return 所有配置属性
     */
    fun getAllProperties(): Map<String, String> {
        return currentConfig.properties.toMap()
    }
    
    /**
     * 获取当前环境
     *
     * @return 当前环境
     */
    fun getCurrentEnvironment(): String {
        return activeProfile
    }
    
    /**
     * 环境配置
     */
    data class EnvironmentConfig(
        val environment: String,
        var updateTime: LocalDateTime,
        val properties: MutableMap<String, String>
    )
}
