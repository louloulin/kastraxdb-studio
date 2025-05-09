package ai.magicdb.script.runtime.repository

import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptGroup
import ai.magicdb.script.api.model.ScriptVersion
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

/**
 * 内存脚本存储测试
 *
 * @author magicdb
 */
class MemoryScriptRepositoryTest {
    
    private lateinit var repository: MemoryScriptRepository
    
    @BeforeEach
    fun setUp() {
        repository = MemoryScriptRepository()
        
        // 创建测试分组
        val group = ScriptGroup(
            id = "test-group",
            name = "测试分组",
            description = "测试分组描述"
        )
        repository.saveGroup(group)
        
        // 创建测试脚本
        val script = Script(
            id = "test-script",
            name = "测试脚本",
            content = "// js\nvar x = 1 + 2;\nreturn x;",
            language = "js",
            description = "测试脚本描述",
            groupId = "test-group",
            tags = listOf("test", "demo")
        )
        repository.saveScript(script)
        
        // 创建测试版本
        val version = ScriptVersion(
            id = "test-version",
            scriptId = "test-script",
            version = 1,
            content = "// js\nvar x = 1 + 2;\nreturn x;",
            language = "js",
            description = "初始版本",
            current = true
        )
        repository.saveScriptVersion(version)
    }
    
    @Test
    fun testSaveScript() {
        val script = Script(
            name = "新脚本",
            content = "// js\nvar y = 3 + 4;\nreturn y;",
            language = "js",
            description = "新脚本描述"
        )
        
        val savedScript = repository.saveScript(script)
        
        assertNotNull(savedScript.id)
        assertEquals("新脚本", savedScript.name)
        assertEquals("// js\nvar y = 3 + 4;\nreturn y;", savedScript.content)
        assertEquals("js", savedScript.language)
        assertEquals("新脚本描述", savedScript.description)
    }
    
    @Test
    fun testDeleteScript() {
        val result = repository.deleteScript("test-script")
        
        assertTrue(result)
        assertNull(repository.getScript("test-script"))
    }
    
    @Test
    fun testGetScript() {
        val script = repository.getScript("test-script")
        
        assertNotNull(script)
        assertEquals("test-script", script?.id)
        assertEquals("测试脚本", script?.name)
        assertEquals("// js\nvar x = 1 + 2;\nreturn x;", script?.content)
        assertEquals("js", script?.language)
        assertEquals("测试脚本描述", script?.description)
        assertEquals("test-group", script?.groupId)
    }
    
    @Test
    fun testGetAllScripts() {
        val scripts = repository.getAllScripts()
        
        assertEquals(1, scripts.size)
        assertEquals("test-script", scripts[0].id)
    }
    
    @Test
    fun testGetScriptsByGroup() {
        val scripts = repository.getScriptsByGroup("test-group")
        
        assertEquals(1, scripts.size)
        assertEquals("test-script", scripts[0].id)
    }
    
    @Test
    fun testGetScriptsByTag() {
        val scripts = repository.getScriptsByTag("test")
        
        assertEquals(1, scripts.size)
        assertEquals("test-script", scripts[0].id)
    }
    
    @Test
    fun testSaveGroup() {
        val group = ScriptGroup(
            name = "新分组",
            description = "新分组描述"
        )
        
        val savedGroup = repository.saveGroup(group)
        
        assertNotNull(savedGroup.id)
        assertEquals("新分组", savedGroup.name)
        assertEquals("新分组描述", savedGroup.description)
    }
    
    @Test
    fun testDeleteGroup() {
        val result = repository.deleteGroup("test-group")
        
        assertTrue(result)
        assertNull(repository.getGroup("test-group"))
    }
    
    @Test
    fun testGetGroup() {
        val group = repository.getGroup("test-group")
        
        assertNotNull(group)
        assertEquals("test-group", group?.id)
        assertEquals("测试分组", group?.name)
        assertEquals("测试分组描述", group?.description)
    }
    
    @Test
    fun testGetAllGroups() {
        val groups = repository.getAllGroups()
        
        assertEquals(1, groups.size)
        assertEquals("test-group", groups[0].id)
    }
    
    @Test
    fun testGetChildGroups() {
        // 创建父分组
        val parentGroup = ScriptGroup(
            id = "parent-group",
            name = "父分组",
            description = "父分组描述"
        )
        repository.saveGroup(parentGroup)
        
        // 创建子分组
        val childGroup = ScriptGroup(
            id = "child-group",
            name = "子分组",
            description = "子分组描述",
            parentId = "parent-group"
        )
        repository.saveGroup(childGroup)
        
        // 测试获取子分组
        val childGroups = repository.getChildGroups("parent-group")
        
        assertEquals(1, childGroups.size)
        assertEquals("child-group", childGroups[0].id)
        
        // 测试获取根分组
        val rootGroups = repository.getChildGroups(null)
        
        assertEquals(2, rootGroups.size) // test-group 和 parent-group
    }
    
    @Test
    fun testSaveScriptVersion() {
        val version = ScriptVersion(
            scriptId = "test-script",
            version = 2,
            content = "// js\nvar x = 1 + 2 + 3;\nreturn x;",
            language = "js",
            description = "更新版本",
            current = false
        )
        
        val savedVersion = repository.saveScriptVersion(version)
        
        assertNotNull(savedVersion.id)
        assertEquals("test-script", savedVersion.scriptId)
        assertEquals(2, savedVersion.version)
        assertEquals("// js\nvar x = 1 + 2 + 3;\nreturn x;", savedVersion.content)
        assertEquals("js", savedVersion.language)
        assertEquals("更新版本", savedVersion.description)
        assertFalse(savedVersion.current)
    }
    
    @Test
    fun testGetScriptVersion() {
        val version = repository.getScriptVersion("test-script", 1)
        
        assertNotNull(version)
        assertEquals("test-version", version?.id)
        assertEquals("test-script", version?.scriptId)
        assertEquals(1, version?.version)
        assertEquals("// js\nvar x = 1 + 2;\nreturn x;", version?.content)
        assertEquals("js", version?.language)
        assertEquals("初始版本", version?.description)
        assertTrue(version?.current ?: false)
    }
    
    @Test
    fun testGetScriptVersions() {
        // 添加第二个版本
        val version2 = ScriptVersion(
            scriptId = "test-script",
            version = 2,
            content = "// js\nvar x = 1 + 2 + 3;\nreturn x;",
            language = "js",
            description = "更新版本",
            current = false
        )
        repository.saveScriptVersion(version2)
        
        val versions = repository.getScriptVersions("test-script")
        
        assertEquals(2, versions.size)
        assertEquals(1, versions[0].version)
        assertEquals(2, versions[1].version)
    }
    
    @Test
    fun testDeleteScriptVersion() {
        val result = repository.deleteScriptVersion("test-script", 1)
        
        assertTrue(result)
        assertNull(repository.getScriptVersion("test-script", 1))
    }
}
