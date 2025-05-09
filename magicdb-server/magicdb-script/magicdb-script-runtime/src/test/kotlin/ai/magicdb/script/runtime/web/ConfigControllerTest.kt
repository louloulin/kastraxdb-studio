package ai.magicdb.script.runtime.web

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConfigControllerTest {

    private val configController = ConfigController()

    @Test
    fun testGetConfigList() {
        // 执行测试
        val result = configController.getConfigList()
        
        // 验证结果
        assertEquals(HttpStatus.OK, result.statusCode)
        
        val configs = result.body
        assertNotNull(configs)
        assertTrue(configs.isNotEmpty())
        
        // 验证默认配置
        val systemName = configs.find { it.key == "system.name" }
        assertNotNull(systemName)
        assertEquals("MagicDB", systemName.value)
    }

    @Test
    fun testGetConfig() {
        // 获取配置列表
        val listResult = configController.getConfigList()
        val configs = listResult.body
        assertNotNull(configs)
        
        // 获取第一个配置的ID
        val firstConfig = configs[0]
        
        // 执行测试
        val result = configController.getConfig(firstConfig.id)
        
        // 验证结果
        assertEquals(HttpStatus.OK, result.statusCode)
        
        val config = result.body
        assertNotNull(config)
        assertEquals(firstConfig.id, config.id)
        assertEquals(firstConfig.key, config.key)
        assertEquals(firstConfig.value, config.value)
    }

    @Test
    fun testGetConfigByKey() {
        // 执行测试
        val result = configController.getConfigByKey("system.name")
        
        // 验证结果
        assertEquals(HttpStatus.OK, result.statusCode)
        
        val config = result.body
        assertNotNull(config)
        assertEquals("system.name", config.key)
        assertEquals("MagicDB", config.value)
    }

    @Test
    fun testCreateConfig() {
        // 准备测试数据
        val request = ConfigRequest(
            key = "test.config",
            value = "test value",
            description = "测试配置"
        )
        
        // 执行测试
        val result = configController.createOrUpdateConfig(request)
        
        // 验证结果
        assertEquals(HttpStatus.OK, result.statusCode)
        
        val config = result.body
        assertNotNull(config)
        assertEquals("test.config", config.key)
        assertEquals("test value", config.value)
        assertEquals("测试配置", config.description)
        
        // 验证配置已创建
        val getResult = configController.getConfigByKey("test.config")
        assertEquals(HttpStatus.OK, getResult.statusCode)
        assertNotNull(getResult.body)
    }

    @Test
    fun testUpdateConfig() {
        // 准备测试数据
        val createRequest = ConfigRequest(
            key = "update.config",
            value = "initial value",
            description = "更新配置"
        )
        
        // 创建配置
        val createResult = configController.createOrUpdateConfig(createRequest)
        val createdConfig = createResult.body
        assertNotNull(createdConfig)
        
        // 更新配置
        val updateRequest = ConfigRequest(
            key = "update.config",
            value = "updated value",
            description = "已更新配置"
        )
        
        val updateResult = configController.createOrUpdateConfig(updateRequest)
        assertEquals(HttpStatus.OK, updateResult.statusCode)
        
        val updatedConfig = updateResult.body
        assertNotNull(updatedConfig)
        assertEquals(createdConfig.id, updatedConfig.id)
        assertEquals("update.config", updatedConfig.key)
        assertEquals("updated value", updatedConfig.value)
        assertEquals("已更新配置", updatedConfig.description)
    }

    @Test
    fun testDeleteConfig() {
        // 准备测试数据
        val request = ConfigRequest(
            key = "delete.config",
            value = "delete value",
            description = "删除配置"
        )
        
        // 创建配置
        val createResult = configController.createOrUpdateConfig(request)
        val createdConfig = createResult.body
        assertNotNull(createdConfig)
        
        // 删除配置
        val deleteResult = configController.deleteConfig(createdConfig.id)
        assertEquals(HttpStatus.OK, deleteResult.statusCode)
        assertTrue(deleteResult.body == true)
        
        // 验证配置已删除
        val getResult = configController.getConfig(createdConfig.id)
        assertEquals(HttpStatus.NOT_FOUND, getResult.statusCode)
        assertNull(getResult.body)
    }
}
