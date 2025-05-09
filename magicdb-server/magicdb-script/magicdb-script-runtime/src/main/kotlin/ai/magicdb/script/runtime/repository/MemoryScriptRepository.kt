package ai.magicdb.script.runtime.repository

import ai.magicdb.script.api.ScriptRepository
import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptGroup
import ai.magicdb.script.api.model.ScriptVersion
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 内存脚本存储实现
 *
 * @author magicdb
 */
class MemoryScriptRepository : ScriptRepository {
    private val logger = LoggerFactory.getLogger(MemoryScriptRepository::class.java)
    
    private val scripts = ConcurrentHashMap<String, Script>()
    private val groups = ConcurrentHashMap<String, ScriptGroup>()
    private val versions = ConcurrentHashMap<String, MutableList<ScriptVersion>>()

    override fun saveScript(script: Script): Script {
        script.updateTime = Date()
        if (script.id.isBlank()) {
            script.id = UUID.randomUUID().toString()
            script.createTime = Date()
        }
        scripts[script.id] = script
        logger.info("保存脚本: {}", script.id)
        return script
    }

    override fun deleteScript(id: String): Boolean {
        val removed = scripts.remove(id) != null
        if (removed) {
            // 删除相关版本
            versions.remove(id)
            logger.info("删除脚本: {}", id)
        } else {
            logger.warn("删除脚本失败，未找到脚本: {}", id)
        }
        return removed
    }

    override fun getScript(id: String): Script? {
        return scripts[id]
    }

    override fun getAllScripts(): List<Script> {
        return scripts.values.toList()
    }

    override fun getScriptsByGroup(groupId: String): List<Script> {
        return scripts.values.filter { it.groupId == groupId }
    }

    override fun getScriptsByTag(tag: String): List<Script> {
        return scripts.values.filter { it.tags.contains(tag) }
    }

    override fun saveGroup(group: ScriptGroup): ScriptGroup {
        group.updateTime = Date()
        if (group.id.isBlank()) {
            group.id = UUID.randomUUID().toString()
            group.createTime = Date()
        }
        groups[group.id] = group
        logger.info("保存脚本分组: {}", group.id)
        return group
    }

    override fun deleteGroup(id: String): Boolean {
        val removed = groups.remove(id) != null
        if (removed) {
            logger.info("删除脚本分组: {}", id)
        } else {
            logger.warn("删除脚本分组失败，未找到分组: {}", id)
        }
        return removed
    }

    override fun getGroup(id: String): ScriptGroup? {
        return groups[id]
    }

    override fun getAllGroups(): List<ScriptGroup> {
        return groups.values.toList()
    }

    override fun getChildGroups(parentId: String?): List<ScriptGroup> {
        return groups.values.filter { it.parentId == parentId }
    }

    override fun saveScriptVersion(scriptVersion: ScriptVersion): ScriptVersion {
        val scriptVersions = versions.computeIfAbsent(scriptVersion.scriptId) { mutableListOf() }
        
        // 检查版本号是否已存在
        val existingVersion = scriptVersions.find { it.version == scriptVersion.version }
        if (existingVersion != null) {
            // 更新现有版本
            val index = scriptVersions.indexOf(existingVersion)
            scriptVersions[index] = scriptVersion
        } else {
            // 添加新版本
            scriptVersions.add(scriptVersion)
            // 按版本号排序
            scriptVersions.sortBy { it.version }
        }
        
        logger.info("保存脚本版本: {}, 版本: {}", scriptVersion.scriptId, scriptVersion.version)
        return scriptVersion
    }

    override fun getScriptVersion(scriptId: String, version: Int): ScriptVersion? {
        val scriptVersions = versions[scriptId] ?: return null
        return scriptVersions.find { it.version == version }
    }

    override fun getScriptVersions(scriptId: String): List<ScriptVersion> {
        return versions[scriptId]?.toList() ?: emptyList()
    }

    override fun deleteScriptVersion(scriptId: String, version: Int): Boolean {
        val scriptVersions = versions[scriptId] ?: return false
        val removed = scriptVersions.removeIf { it.version == version }
        if (removed) {
            logger.info("删除脚本版本: {}, 版本: {}", scriptId, version)
        } else {
            logger.warn("删除脚本版本失败，未找到版本: {}, 版本: {}", scriptId, version)
        }
        return removed
    }
}
