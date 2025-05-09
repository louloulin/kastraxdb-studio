package ai.magicdb.script.runtime.integration

import ai.magicdb.script.api.ScriptManager
import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptGroup
import ai.magicdb.script.engine.GraalVMScriptExecutor
import ai.magicdb.script.runtime.metadata.DefaultScriptMetadataManager
import ai.magicdb.script.runtime.repository.MemoryScriptRepository
import ai.magicdb.script.runtime.service.DefaultScriptManager
import ai.magicdb.script.runtime.version.DefaultScriptVersionControl
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * 脚本集成测试
 *
 * @author magicdb
 */
class ScriptIntegrationTest {
    
    private lateinit var scriptManager: ScriptManager
    private lateinit var repository: MemoryScriptRepository
    private lateinit var versionControl: DefaultScriptVersionControl
    private lateinit var metadataManager: DefaultScriptMetadataManager
    private lateinit var scriptExecutor: GraalVMScriptExecutor
    private lateinit var objectMapper: ObjectMapper
    
    @BeforeEach
    fun setUp() {
        repository = MemoryScriptRepository()
        versionControl = DefaultScriptVersionControl(repository)
        metadataManager = DefaultScriptMetadataManager(repository)
        scriptExecutor = GraalVMScriptExecutor()
        objectMapper = ObjectMapper()
        
        scriptManager = DefaultScriptManager(
            repository,
            versionControl,
            metadataManager,
            scriptExecutor,
            objectMapper
        )
        
        // 创建测试分组
        val group = ScriptGroup(
            id = "test-group",
            name = "测试分组",
            description = "测试分组描述"
        )
        scriptManager.saveGroup(group)
    }
    
    @Test
    fun testCompleteWorkflow() {
        // 1. 创建脚本
        val script = Script(
            name = "测试脚本",
            content = "// js\nfunction add(a, b) { return a + b; }\nreturn add(x, y);",
            language = "js",
            description = "测试脚本描述",
            groupId = "test-group",
            tags = listOf("test", "demo")
        )
        
        val savedScript = scriptManager.saveScript(script)
        assertNotNull(savedScript.id)
        
        // 2. 执行脚本
        val result1 = scriptManager.executeScript(savedScript.id, mapOf("x" to 10, "y" to 20))
        assertEquals(30, result1)
        
        // 3. 更新脚本
        val updatedScript = savedScript.copy(
            content = "// js\nfunction multiply(a, b) { return a * b; }\nreturn multiply(x, y);",
            description = "更新后的描述"
        )
        
        val savedUpdatedScript = scriptManager.saveScript(updatedScript)
        assertEquals(2, savedUpdatedScript.version)
        
        // 4. 执行更新后的脚本
        val result2 = scriptManager.executeScript(savedUpdatedScript.id, mapOf("x" to 10, "y" to 20))
        assertEquals(200, result2)
        
        // 5. 获取版本历史
        val versions = scriptManager.getVersions(savedUpdatedScript.id)
        assertEquals(2, versions.size)
        
        // 6. 切换到旧版本
        val switchedScript = scriptManager.switchVersion(savedUpdatedScript.id, 1)
        assertEquals(1, switchedScript?.version)
        
        // 7. 执行旧版本脚本
        val result3 = scriptManager.executeScript(switchedScript!!.id, mapOf("x" to 10, "y" to 20))
        assertEquals(30, result3)
        
        // 8. 获取使用情况
        val usage = scriptManager.getUsage(switchedScript.id)
        assertEquals(3, usage.executeCount)
        assertEquals(3, usage.successCount)
        assertEquals(0, usage.failCount)
        
        // 9. 添加标签
        val taggedScript = scriptManager.addTag(switchedScript.id, "integration-test")
        assertEquals(3, taggedScript?.tags?.size)
        assertTrue(taggedScript?.tags?.contains("integration-test") ?: false)
        
        // 10. 导出脚本
        val exportData = scriptManager.exportScript(switchedScript.id)
        assertNotNull(exportData)
        
        // 11. 导入脚本
        val importedScript = scriptManager.importScript(exportData)
        assertNotNull(importedScript)
        assertNotEquals(switchedScript.id, importedScript.id)
        
        // 12. 执行导入的脚本
        val result4 = scriptManager.executeScript(importedScript.id, mapOf("x" to 10, "y" to 20))
        assertEquals(30, result4)
        
        // 13. 删除脚本
        val deleteResult = scriptManager.deleteScript(switchedScript.id)
        assertTrue(deleteResult)
        assertNull(scriptManager.getScript(switchedScript.id))
    }
    
    @Test
    fun testScriptDependencies() {
        // 1. 创建依赖脚本
        val utilScript = Script(
            name = "工具脚本",
            content = "// js\nfunction add(a, b) { return a + b; }\nfunction multiply(a, b) { return a * b; }\nreturn { add, multiply };",
            language = "js",
            description = "工具函数脚本",
            tags = listOf("util")
        )
        
        val savedUtilScript = scriptManager.saveScript(utilScript)
        
        // 2. 创建主脚本
        val mainScript = Script(
            name = "主脚本",
            content = "// js\nvar utils = context.getScript('${savedUtilScript.id}');\nreturn utils.add(x, y) + utils.multiply(x, y);",
            language = "js",
            description = "使用工具脚本的主脚本"
        )
        
        val savedMainScript = scriptManager.saveScript(mainScript)
        
        // 3. 添加依赖关系
        val addDependencyResult = scriptManager.addDependency(savedMainScript.id, savedUtilScript.id)
        assertTrue(addDependencyResult)
        
        // 4. 获取依赖
        val dependencies = scriptManager.getDependencies(savedMainScript.id)
        assertEquals(1, dependencies.size)
        assertEquals(savedUtilScript.id, dependencies[0])
        
        // 5. 模拟执行（实际执行需要上下文支持getScript方法）
        // 这里我们只是验证依赖关系的管理
        
        // 6. 删除依赖
        val removeDependencyResult = scriptManager.removeDependency(savedMainScript.id, savedUtilScript.id)
        assertTrue(removeDependencyResult)
        
        // 7. 验证依赖已删除
        val dependenciesAfterRemove = scriptManager.getDependencies(savedMainScript.id)
        assertEquals(0, dependenciesAfterRemove.size)
    }
}
