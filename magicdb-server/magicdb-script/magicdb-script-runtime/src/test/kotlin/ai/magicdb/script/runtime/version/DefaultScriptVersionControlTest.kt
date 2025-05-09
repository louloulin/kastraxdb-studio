package ai.magicdb.script.runtime.version

import ai.magicdb.script.api.DiffType
import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptVersion
import ai.magicdb.script.runtime.repository.MemoryScriptRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

/**
 * 默认脚本版本控制测试
 *
 * @author magicdb
 */
class DefaultScriptVersionControlTest {
    
    private lateinit var versionControl: DefaultScriptVersionControl
    private lateinit var repository: MemoryScriptRepository
    
    @BeforeEach
    fun setUp() {
        repository = MemoryScriptRepository()
        versionControl = DefaultScriptVersionControl(repository)
        
        // 创建测试脚本
        val script = Script(
            id = "test-script",
            name = "测试脚本",
            content = "// js\nvar x = 1 + 2;\nreturn x;",
            language = "js",
            description = "测试脚本描述",
            version = 1
        )
        repository.saveScript(script)
    }
    
    @Test
    fun testCreateVersion() {
        val script = repository.getScript("test-script")!!
        
        val version = versionControl.createVersion(script, "初始版本", "tester")
        
        assertNotNull(version)
        assertEquals("test-script", version.scriptId)
        assertEquals(1, version.version)
        assertEquals("// js\nvar x = 1 + 2;\nreturn x;", version.content)
        assertEquals("js", version.language)
        assertEquals("初始版本", version.description)
        assertEquals("tester", version.creator)
        assertTrue(version.current)
    }
    
    @Test
    fun testGetVersion() {
        // 创建版本
        val script = repository.getScript("test-script")!!
        val createdVersion = versionControl.createVersion(script, "初始版本", "tester")
        
        // 获取版本
        val version = versionControl.getVersion("test-script", 1)
        
        assertNotNull(version)
        assertEquals(createdVersion.id, version?.id)
        assertEquals("test-script", version?.scriptId)
        assertEquals(1, version?.version)
        assertEquals("// js\nvar x = 1 + 2;\nreturn x;", version?.content)
        assertEquals("js", version?.language)
        assertEquals("初始版本", version?.description)
        assertEquals("tester", version?.creator)
        assertTrue(version?.current ?: false)
    }
    
    @Test
    fun testGetVersions() {
        // 创建初始版本
        val script = repository.getScript("test-script")!!
        versionControl.createVersion(script, "初始版本", "tester")
        
        // 更新脚本
        val updatedScript = script.copy(
            content = "// js\nvar x = 1 + 2 + 3;\nreturn x;",
            version = 2
        )
        repository.saveScript(updatedScript)
        
        // 创建第二个版本
        versionControl.createVersion(updatedScript, "更新版本", "tester")
        
        // 获取所有版本
        val versions = versionControl.getVersions("test-script")
        
        assertEquals(2, versions.size)
        assertEquals(1, versions[0].version)
        assertEquals(2, versions[1].version)
        assertFalse(versions[0].current)
        assertTrue(versions[1].current)
    }
    
    @Test
    fun testGetCurrentVersion() {
        // 创建初始版本
        val script = repository.getScript("test-script")!!
        versionControl.createVersion(script, "初始版本", "tester")
        
        // 更新脚本
        val updatedScript = script.copy(
            content = "// js\nvar x = 1 + 2 + 3;\nreturn x;",
            version = 2
        )
        repository.saveScript(updatedScript)
        
        // 创建第二个版本
        versionControl.createVersion(updatedScript, "更新版本", "tester")
        
        // 获取当前版本
        val currentVersion = versionControl.getCurrentVersion("test-script")
        
        assertNotNull(currentVersion)
        assertEquals(2, currentVersion?.version)
        assertEquals("更新版本", currentVersion?.description)
        assertTrue(currentVersion?.current ?: false)
    }
    
    @Test
    fun testSwitchVersion() {
        // 创建初始版本
        val script = repository.getScript("test-script")!!
        versionControl.createVersion(script, "初始版本", "tester")
        
        // 更新脚本
        val updatedScript = script.copy(
            content = "// js\nvar x = 1 + 2 + 3;\nreturn x;",
            version = 2
        )
        repository.saveScript(updatedScript)
        
        // 创建第二个版本
        versionControl.createVersion(updatedScript, "更新版本", "tester")
        
        // 切换到第一个版本
        val switchedScript = versionControl.switchVersion("test-script", 1)
        
        assertNotNull(switchedScript)
        assertEquals("test-script", switchedScript?.id)
        assertEquals("// js\nvar x = 1 + 2;\nreturn x;", switchedScript?.content)
        assertEquals(1, switchedScript?.version)
        
        // 验证版本状态
        val versions = versionControl.getVersions("test-script")
        assertTrue(versions[0].current)
        assertFalse(versions[1].current)
    }
    
    @Test
    fun testCompareVersions() {
        // 创建初始版本
        val script = repository.getScript("test-script")!!
        versionControl.createVersion(script, "初始版本", "tester")
        
        // 更新脚本
        val updatedScript = script.copy(
            content = "// js\nvar x = 1 + 2 + 3;\nreturn x;",
            version = 2
        )
        repository.saveScript(updatedScript)
        
        // 创建第二个版本
        versionControl.createVersion(updatedScript, "更新版本", "tester")
        
        // 比较版本
        val diff = versionControl.compareVersions("test-script", 1, 2)
        
        assertNotNull(diff)
        assertEquals("test-script", diff?.scriptId)
        assertEquals(1, diff?.fromVersion)
        assertEquals(2, diff?.toVersion)
        
        // 验证差异
        val diffLines = diff?.diffLines
        assertEquals(3, diffLines?.size)
        
        // 第一行不变
        assertEquals(1, diffLines?.get(0)?.lineNumber)
        assertEquals(DiffType.UNCHANGED, diffLines?.get(0)?.type)
        assertEquals("// js", diffLines?.get(0)?.oldContent)
        assertEquals("// js", diffLines?.get(0)?.newContent)
        
        // 第二行修改
        assertEquals(2, diffLines?.get(1)?.lineNumber)
        assertEquals(DiffType.MODIFY, diffLines?.get(1)?.type)
        assertEquals("var x = 1 + 2;", diffLines?.get(1)?.oldContent)
        assertEquals("var x = 1 + 2 + 3;", diffLines?.get(1)?.newContent)
        
        // 第三行不变
        assertEquals(3, diffLines?.get(2)?.lineNumber)
        assertEquals(DiffType.UNCHANGED, diffLines?.get(2)?.type)
        assertEquals("return x;", diffLines?.get(2)?.oldContent)
        assertEquals("return x;", diffLines?.get(2)?.newContent)
    }
    
    @Test
    fun testDeleteVersion() {
        // 创建初始版本
        val script = repository.getScript("test-script")!!
        versionControl.createVersion(script, "初始版本", "tester")
        
        // 更新脚本
        val updatedScript = script.copy(
            content = "// js\nvar x = 1 + 2 + 3;\nreturn x;",
            version = 2
        )
        repository.saveScript(updatedScript)
        
        // 创建第二个版本
        versionControl.createVersion(updatedScript, "更新版本", "tester")
        
        // 尝试删除当前版本（应该失败）
        val result1 = versionControl.deleteVersion("test-script", 2)
        assertFalse(result1)
        
        // 切换到第一个版本
        versionControl.switchVersion("test-script", 1)
        
        // 删除第二个版本（应该成功）
        val result2 = versionControl.deleteVersion("test-script", 2)
        assertTrue(result2)
        
        // 验证版本已删除
        val versions = versionControl.getVersions("test-script")
        assertEquals(1, versions.size)
        assertEquals(1, versions[0].version)
    }
}
