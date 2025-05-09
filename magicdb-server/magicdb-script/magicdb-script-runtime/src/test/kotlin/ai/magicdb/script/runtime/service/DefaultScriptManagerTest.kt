package ai.magicdb.script.runtime.service

import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptGroup
import ai.magicdb.script.engine.GraalVMScriptExecutor
import ai.magicdb.script.runtime.metadata.DefaultScriptMetadataManager
import ai.magicdb.script.runtime.repository.MemoryScriptRepository
import ai.magicdb.script.runtime.version.DefaultScriptVersionControl
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * 默认脚本管理测试
 *
 * @author magicdb
 */
class DefaultScriptManagerTest {
    
    private lateinit var scriptManager: DefaultScriptManager
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
    fun testSaveScript() {
        // 创建新脚本
        val script = Script(
            name = "测试脚本",
            content = "// js\nvar x = 1 + 2;\nreturn x;",
            language = "js",
            description = "测试脚本描述",
            groupId = "test-group",
            tags = listOf("test", "demo")
        )
        
        // 保存脚本
        val savedScript = scriptManager.saveScript(script)
        
        // 验证脚本
        assertNotNull(savedScript.id)
        assertEquals("测试脚本", savedScript.name)
        assertEquals("// js\nvar x = 1 + 2;\nreturn x;", savedScript.content)
        assertEquals("js", savedScript.language)
        assertEquals("测试脚本描述", savedScript.description)
        assertEquals("test-group", savedScript.groupId)
        assertEquals(2, savedScript.tags.size)
        assertEquals(1, savedScript.version)
        
        // 验证版本
        val versions = scriptManager.getVersions(savedScript.id)
        assertEquals(1, versions.size)
        assertEquals(1, versions[0].version)
        assertEquals("初始版本", versions[0].description)
        assertTrue(versions[0].current)
    }
    
    @Test
    fun testUpdateScript() {
        // 创建新脚本
        val script = Script(
            name = "测试脚本",
            content = "// js\nvar x = 1 + 2;\nreturn x;",
            language = "js",
            description = "测试脚本描述"
        )
        
        // 保存脚本
        val savedScript = scriptManager.saveScript(script)
        
        // 更新脚本
        val updatedScript = savedScript.copy(
            content = "// js\nvar x = 1 + 2 + 3;\nreturn x;",
            description = "更新后的描述"
        )
        
        // 保存更新后的脚本
        val savedUpdatedScript = scriptManager.saveScript(updatedScript)
        
        // 验证脚本
        assertEquals(savedScript.id, savedUpdatedScript.id)
        assertEquals("测试脚本", savedUpdatedScript.name)
        assertEquals("// js\nvar x = 1 + 2 + 3;\nreturn x;", savedUpdatedScript.content)
        assertEquals("js", savedUpdatedScript.language)
        assertEquals("更新后的描述", savedUpdatedScript.description)
        assertEquals(2, savedUpdatedScript.version)
        
        // 验证版本
        val versions = scriptManager.getVersions(savedUpdatedScript.id)
        assertEquals(2, versions.size)
        assertEquals(1, versions[0].version)
        assertEquals(2, versions[1].version)
        assertEquals("初始版本", versions[0].description)
        assertEquals("更新脚本", versions[1].description)
        assertFalse(versions[0].current)
        assertTrue(versions[1].current)
    }
    
    @Test
    fun testDeleteScript() {
        // 创建新脚本
        val script = Script(
            name = "测试脚本",
            content = "// js\nvar x = 1 + 2;\nreturn x;",
            language = "js",
            description = "测试脚本描述"
        )
        
        // 保存脚本
        val savedScript = scriptManager.saveScript(script)
        
        // 删除脚本
        val result = scriptManager.deleteScript(savedScript.id)
        
        // 验证删除结果
        assertTrue(result)
        
        // 验证脚本已删除
        assertNull(scriptManager.getScript(savedScript.id))
    }
    
    @Test
    fun testExecuteScript() {
        // 创建新脚本
        val script = Script(
            name = "测试脚本",
            content = "// js\nvar sum = a + b;\nreturn sum;",
            language = "js",
            description = "测试脚本描述"
        )
        
        // 保存脚本
        val savedScript = scriptManager.saveScript(script)
        
        // 执行脚本
        val result = scriptManager.executeScript(savedScript.id, mapOf("a" to 10, "b" to 20))
        
        // 验证执行结果
        assertEquals(30, result)
        
        // 验证执行记录
        val usage = scriptManager.getUsage(savedScript.id)
        assertEquals(1, usage.executeCount)
        assertEquals(1, usage.successCount)
        assertEquals(0, usage.failCount)
    }
    
    @Test
    fun testValidateScript() {
        // 验证有效脚本
        val validResult = scriptManager.validateScript("// js\nvar x = 1 + 2;\nreturn x;", "js")
        assertTrue(validResult)
        
        // 验证无效脚本
        val invalidResult = scriptManager.validateScript("// js\nvar x = 1 + ;\nreturn x;", "js")
        assertFalse(invalidResult)
    }
    
    @Test
    fun testExportAndImportScript() {
        // 创建新脚本
        val script = Script(
            name = "测试脚本",
            content = "// js\nvar x = 1 + 2;\nreturn x;",
            language = "js",
            description = "测试脚本描述",
            tags = listOf("test", "demo")
        )
        
        // 保存脚本
        val savedScript = scriptManager.saveScript(script)
        
        // 添加标签
        scriptManager.addTag(savedScript.id, "export")
        
        // 导出脚本
        val exportData = scriptManager.exportScript(savedScript.id)
        
        // 验证导出数据
        assertNotNull(exportData)
        assertTrue(exportData.containsKey("script"))
        assertTrue(exportData.containsKey("versions"))
        assertTrue(exportData.containsKey("exportTime"))
        
        // 导入脚本
        val importedScript = scriptManager.importScript(exportData)
        
        // 验证导入脚本
        assertNotNull(importedScript)
        assertNotEquals(savedScript.id, importedScript.id) // ID应该是新生成的
        assertEquals("测试脚本", importedScript.name)
        assertEquals("// js\nvar x = 1 + 2;\nreturn x;", importedScript.content)
        assertEquals("js", importedScript.language)
        assertEquals("测试脚本描述", importedScript.description)
        assertEquals(3, importedScript.tags.size)
        assertTrue(importedScript.tags.contains("test"))
        assertTrue(importedScript.tags.contains("demo"))
        assertTrue(importedScript.tags.contains("export"))
    }
}
