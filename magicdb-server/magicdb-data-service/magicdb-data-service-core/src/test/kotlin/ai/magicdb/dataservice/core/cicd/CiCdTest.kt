package ai.magicdb.dataservice.core.cicd

import ai.magicdb.dataservice.core.config.EnvironmentConfigManager
import ai.magicdb.dataservice.core.version.VersionManager
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.env.Environment
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.File
import java.time.LocalDateTime

/**
 * CI/CD 测试
 *
 * @author magicdb
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [VersionManager::class, EnvironmentConfigManager::class, ObjectMapper::class])
class CiCdTest {
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockBean
    private lateinit var environment: Environment
    
    @Test
    fun testVersionManager() {
        // 创建版本管理器
        val versionManager = VersionManager("1.0.0", "1.0.0")
        
        // 测试获取版本
        assertEquals("1.0.0", versionManager.getAppVersion())
        assertEquals("1.0.0", versionManager.getDataServiceVersion())
        
        // 测试获取当前版本
        val currentVersion = versionManager.getCurrentVersion()
        assertNotNull(currentVersion)
        
        // 测试获取版本历史
        val versionHistory = versionManager.getVersionHistory()
        assertNotNull(versionHistory)
    }
    
    @Test
    fun testEnvironmentConfigManager() {
        // 模拟环境
        `when`(environment.getProperty("spring.profiles.active")).thenReturn("test")
        
        // 创建临时配置目录
        val tempDir = System.getProperty("java.io.tmpdir")
        val configDir = "$tempDir${File.separator}magicdb${File.separator}config${File.separator}data-service"
        val configDirFile = File(configDir)
        configDirFile.mkdirs()
        
        // 创建环境配置管理器
        val configManager = EnvironmentConfigManager(environment, objectMapper)
        
        // 测试获取当前环境
        assertEquals("test", configManager.getCurrentEnvironment())
        
        // 测试设置和获取属性
        configManager.setProperty("test.key", "test.value")
        assertEquals("test.value", configManager.getProperty("test.key", ""))
        
        configManager.setProperty("test.boolean", true)
        assertTrue(configManager.getProperty("test.boolean", false))
        
        configManager.setProperty("test.int", 123)
        assertEquals(123, configManager.getProperty("test.int", 0))
        
        configManager.setProperty("test.long", 123456789L)
        assertEquals(123456789L, configManager.getProperty("test.long", 0L))
        
        // 测试获取所有属性
        val allProperties = configManager.getAllProperties()
        assertNotNull(allProperties)
        assertTrue(allProperties.isNotEmpty())
        assertEquals("test.value", allProperties["test.key"])
    }
    
    @Test
    fun testEnvironmentConfig() {
        // 创建环境配置
        val config = EnvironmentConfigManager.EnvironmentConfig(
            environment = "test",
            updateTime = LocalDateTime.now(),
            properties = mutableMapOf(
                "test.key" to "test.value",
                "test.boolean" to "true",
                "test.int" to "123",
                "test.long" to "123456789"
            )
        )
        
        // 测试序列化和反序列化
        val json = objectMapper.writeValueAsString(config)
        assertNotNull(json)
        
        val deserializedConfig = objectMapper.readValue(json, EnvironmentConfigManager.EnvironmentConfig::class.java)
        assertNotNull(deserializedConfig)
        assertEquals(config.environment, deserializedConfig.environment)
        assertEquals(config.properties, deserializedConfig.properties)
    }
}
