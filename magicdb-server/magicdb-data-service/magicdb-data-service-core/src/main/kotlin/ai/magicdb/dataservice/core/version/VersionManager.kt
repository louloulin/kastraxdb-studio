package ai.magicdb.dataservice.core.version

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties
import javax.annotation.PostConstruct

/**
 * 版本管理器
 * 负责管理应用程序的版本信息
 *
 * @author magicdb
 */
@Component
class VersionManager(
    @Value("\${magicdb.version:1.0.0}") private val appVersion: String,
    @Value("\${magicdb.data-service.version:1.0.0}") private val dataServiceVersion: String
) {
    private val logger = LoggerFactory.getLogger(VersionManager::class.java)
    
    // 版本文件路径
    private val versionFilePath = System.getProperty("user.home") + File.separator + ".magicdb" + 
            File.separator + "versions" + File.separator + "data-service-version"
    
    // 版本历史文件路径
    private val versionHistoryFilePath = System.getProperty("user.home") + File.separator + ".magicdb" + 
            File.separator + "versions" + File.separator + "data-service-version-history"
    
    /**
     * 初始化
     */
    @PostConstruct
    fun init() {
        try {
            // 创建版本目录
            val versionDir = File(versionFilePath).parentFile
            if (!versionDir.exists()) {
                versionDir.mkdirs()
            }
            
            // 读取当前版本
            val currentVersion = getCurrentVersion()
            
            // 如果版本不同，更新版本文件
            if (currentVersion != dataServiceVersion) {
                updateVersionFile()
                addVersionHistory()
                logger.info("版本已更新: {} -> {}", currentVersion, dataServiceVersion)
            }
        } catch (e: Exception) {
            logger.error("初始化版本管理器失败", e)
        }
    }
    
    /**
     * 获取当前版本
     *
     * @return 当前版本
     */
    fun getCurrentVersion(): String {
        val versionFile = File(versionFilePath)
        if (!versionFile.exists()) {
            return "0.0.0"
        }
        
        return try {
            val properties = Properties()
            properties.load(versionFile.inputStream())
            properties.getProperty("version", "0.0.0")
        } catch (e: Exception) {
            logger.error("读取版本文件失败", e)
            "0.0.0"
        }
    }
    
    /**
     * 获取应用程序版本
     *
     * @return 应用程序版本
     */
    fun getAppVersion(): String {
        return appVersion
    }
    
    /**
     * 获取数据服务版本
     *
     * @return 数据服务版本
     */
    fun getDataServiceVersion(): String {
        return dataServiceVersion
    }
    
    /**
     * 获取版本历史
     *
     * @return 版本历史
     */
    fun getVersionHistory(): List<VersionHistory> {
        val historyFile = File(versionHistoryFilePath)
        if (!historyFile.exists()) {
            return emptyList()
        }
        
        return try {
            Files.readAllLines(historyFile.toPath())
                .filter { it.isNotBlank() }
                .map { line ->
                    val parts = line.split(",")
                    if (parts.size >= 3) {
                        VersionHistory(
                            version = parts[0],
                            updateTime = LocalDateTime.parse(parts[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            description = parts.subList(2, parts.size).joinToString(",")
                        )
                    } else {
                        VersionHistory(
                            version = parts.getOrElse(0) { "unknown" },
                            updateTime = LocalDateTime.now(),
                            description = parts.getOrElse(1) { "" }
                        )
                    }
                }
                .sortedByDescending { it.updateTime }
        } catch (e: Exception) {
            logger.error("读取版本历史文件失败", e)
            emptyList()
        }
    }
    
    /**
     * 更新版本文件
     */
    private fun updateVersionFile() {
        try {
            val properties = Properties()
            properties.setProperty("version", dataServiceVersion)
            properties.setProperty("updateTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            
            val versionFile = File(versionFilePath)
            properties.store(versionFile.outputStream(), "Data Service Version")
        } catch (e: Exception) {
            logger.error("更新版本文件失败", e)
        }
    }
    
    /**
     * 添加版本历史
     */
    private fun addVersionHistory() {
        try {
            val historyLine = "$dataServiceVersion,${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)},自动更新\n"
            
            val historyFile = File(versionHistoryFilePath)
            if (!historyFile.exists()) {
                historyFile.createNewFile()
            }
            
            Files.write(
                Paths.get(versionHistoryFilePath),
                historyLine.toByteArray(),
                StandardOpenOption.APPEND
            )
        } catch (e: Exception) {
            logger.error("添加版本历史失败", e)
        }
    }
    
    /**
     * 版本历史
     */
    data class VersionHistory(
        val version: String,
        val updateTime: LocalDateTime,
        val description: String
    )
}
