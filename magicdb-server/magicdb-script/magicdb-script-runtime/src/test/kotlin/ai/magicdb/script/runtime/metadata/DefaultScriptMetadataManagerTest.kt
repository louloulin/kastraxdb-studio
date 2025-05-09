package ai.magicdb.script.runtime.metadata

import ai.magicdb.script.api.model.Script
import ai.magicdb.script.runtime.repository.MemoryScriptRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * 默认脚本元数据管理测试
 *
 * @author magicdb
 */
class DefaultScriptMetadataManagerTest {
    
    private lateinit var metadataManager: DefaultScriptMetadataManager
    private lateinit var repository: MemoryScriptRepository
    
    @BeforeEach
    fun setUp() {
        repository = MemoryScriptRepository()
        metadataManager = DefaultScriptMetadataManager(repository)
        
        // 创建测试脚本
        val script1 = Script(
            id = "script1",
            name = "脚本1",
            content = "// js\nvar x = 1 + 2;\nreturn x;",
            language = "js",
            description = "测试脚本1",
            tags = listOf("test")
        )
        repository.saveScript(script1)
        
        val script2 = Script(
            id = "script2",
            name = "脚本2",
            content = "// js\nvar y = 3 + 4;\nreturn y;",
            language = "js",
            description = "测试脚本2",
            tags = listOf("demo")
        )
        repository.saveScript(script2)
    }
    
    @Test
    fun testGetMetadata() {
        // 初始状态下应该没有元数据
        val metadata = metadataManager.getMetadata("script1")
        assertNull(metadata)
    }
    
    @Test
    fun testUpdateMetadata() {
        // 创建元数据
        val metadata = metadataManager.updateMetadata("script1", ai.magicdb.script.api.model.ScriptMetadata(
            scriptId = "script1",
            properties = mapOf("key1" to "value1", "key2" to "value2")
        ))
        
        // 验证元数据
        assertNotNull(metadata)
        assertEquals("script1", metadata.scriptId)
        assertEquals(2, metadata.properties.size)
        assertEquals("value1", metadata.properties["key1"])
        assertEquals("value2", metadata.properties["key2"])
        
        // 获取元数据
        val retrievedMetadata = metadataManager.getMetadata("script1")
        assertNotNull(retrievedMetadata)
        assertEquals("script1", retrievedMetadata?.scriptId)
        assertEquals(2, retrievedMetadata?.properties?.size)
        assertEquals("value1", retrievedMetadata?.properties?.get("key1"))
        assertEquals("value2", retrievedMetadata?.properties?.get("key2"))
    }
    
    @Test
    fun testAddTag() {
        // 添加标签
        val script = metadataManager.addTag("script1", "new-tag")
        
        // 验证标签
        assertNotNull(script)
        assertEquals(2, script?.tags?.size)
        assertTrue(script?.tags?.contains("test") ?: false)
        assertTrue(script?.tags?.contains("new-tag") ?: false)
        
        // 添加已存在的标签
        val script2 = metadataManager.addTag("script1", "test")
        
        // 验证标签没有重复
        assertNotNull(script2)
        assertEquals(2, script2?.tags?.size)
    }
    
    @Test
    fun testRemoveTag() {
        // 添加标签
        metadataManager.addTag("script1", "new-tag")
        
        // 删除标签
        val script = metadataManager.removeTag("script1", "test")
        
        // 验证标签
        assertNotNull(script)
        assertEquals(1, script?.tags?.size)
        assertFalse(script?.tags?.contains("test") ?: true)
        assertTrue(script?.tags?.contains("new-tag") ?: false)
        
        // 删除不存在的标签
        val script2 = metadataManager.removeTag("script1", "non-existent")
        
        // 验证标签没有变化
        assertNotNull(script2)
        assertEquals(1, script2?.tags?.size)
    }
    
    @Test
    fun testAddAndGetDependency() {
        // 添加依赖
        val result = metadataManager.addDependency("script1", "script2")
        
        // 验证依赖
        assertTrue(result)
        
        // 获取依赖
        val dependencies = metadataManager.getDependencies("script1")
        
        // 验证依赖
        assertEquals(1, dependencies.size)
        assertEquals("script2", dependencies[0])
    }
    
    @Test
    fun testRemoveDependency() {
        // 添加依赖
        metadataManager.addDependency("script1", "script2")
        
        // 删除依赖
        val result = metadataManager.removeDependency("script1", "script2")
        
        // 验证依赖
        assertTrue(result)
        
        // 获取依赖
        val dependencies = metadataManager.getDependencies("script1")
        
        // 验证依赖已删除
        assertEquals(0, dependencies.size)
    }
    
    @Test
    fun testRecordExecution() {
        // 记录执行
        metadataManager.recordExecution("script1", true, 100)
        metadataManager.recordExecution("script1", true, 200)
        metadataManager.recordExecution("script1", false, 300)
        
        // 获取使用情况
        val usage = metadataManager.getUsage("script1")
        
        // 验证使用情况
        assertEquals("script1", usage.scriptId)
        assertEquals(3, usage.executeCount)
        assertEquals(2, usage.successCount)
        assertEquals(1, usage.failCount)
        assertEquals(200, usage.avgExecuteTime) // (100 + 200 + 300) / 3 = 200
        assertEquals(300, usage.maxExecuteTime)
        assertEquals(100, usage.minExecuteTime)
        assertNotNull(usage.lastExecuteTime)
        
        // 验证执行记录
        assertEquals(3, usage.recentExecutions.size)
        assertEquals(300, usage.recentExecutions[0].duration)
        assertEquals(false, usage.recentExecutions[0].success)
        assertEquals(200, usage.recentExecutions[1].duration)
        assertEquals(true, usage.recentExecutions[1].success)
        assertEquals(100, usage.recentExecutions[2].duration)
        assertEquals(true, usage.recentExecutions[2].success)
    }
}
