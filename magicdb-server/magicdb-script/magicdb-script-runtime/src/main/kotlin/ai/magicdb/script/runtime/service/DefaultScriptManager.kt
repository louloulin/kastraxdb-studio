package ai.magicdb.script.runtime.service

import ai.magicdb.script.api.*
import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptGroup
import ai.magicdb.script.api.model.ScriptMetadata
import ai.magicdb.script.api.model.ScriptVersion
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import java.util.*

/**
 * 默认脚本管理实现
 *
 * @author magicdb
 */
class DefaultScriptManager(
    private val scriptRepository: ScriptRepository,
    private val scriptVersionControl: ScriptVersionControl,
    private val scriptMetadataManager: ScriptMetadataManager,
    private val scriptExecutor: ScriptExecutor,
    private val objectMapper: ObjectMapper
) : ScriptManager {
    private val logger = LoggerFactory.getLogger(DefaultScriptManager::class.java)

    override fun saveScript(script: Script): Script {
        val now = Date()
        script.updateTime = now
        
        // 检查是否是新脚本
        val isNew = script.id.isBlank()
        if (isNew) {
            script.id = UUID.randomUUID().toString()
            script.createTime = now
            script.version = 1
        } else {
            // 获取现有脚本
            val existingScript = scriptRepository.getScript(script.id)
            if (existingScript != null) {
                // 检查内容是否变更
                if (existingScript.content != script.content || existingScript.language != script.language) {
                    // 内容变更，创建新版本
                    script.version = existingScript.version + 1
                    scriptVersionControl.createVersion(script, "更新脚本", script.updater)
                } else {
                    // 内容未变更，保持版本号
                    script.version = existingScript.version
                }
            }
        }
        
        // 保存脚本
        val savedScript = scriptRepository.saveScript(script)
        
        // 如果是新脚本，创建初始版本
        if (isNew) {
            scriptVersionControl.createVersion(savedScript, "初始版本", savedScript.creator)
        }
        
        return savedScript
    }

    override fun deleteScript(id: String): Boolean {
        return scriptRepository.deleteScript(id)
    }

    override fun getScript(id: String): Script? {
        return scriptRepository.getScript(id)
    }

    override fun getAllScripts(): List<Script> {
        return scriptRepository.getAllScripts()
    }

    override fun getScriptsByGroup(groupId: String): List<Script> {
        return scriptRepository.getScriptsByGroup(groupId)
    }

    override fun getScriptsByTag(tag: String): List<Script> {
        return scriptRepository.getScriptsByTag(tag)
    }

    override fun saveGroup(group: ScriptGroup): ScriptGroup {
        return scriptRepository.saveGroup(group)
    }

    override fun deleteGroup(id: String): Boolean {
        return scriptRepository.deleteGroup(id)
    }

    override fun getGroup(id: String): ScriptGroup? {
        return scriptRepository.getGroup(id)
    }

    override fun getAllGroups(): List<ScriptGroup> {
        return scriptRepository.getAllGroups()
    }

    override fun getChildGroups(parentId: String?): List<ScriptGroup> {
        return scriptRepository.getChildGroups(parentId)
    }

    override fun createVersion(scriptId: String, description: String, creator: String?): ScriptVersion? {
        val script = scriptRepository.getScript(scriptId) ?: return null
        return scriptVersionControl.createVersion(script, description, creator)
    }

    override fun getVersion(scriptId: String, version: Int): ScriptVersion? {
        return scriptVersionControl.getVersion(scriptId, version)
    }

    override fun getVersions(scriptId: String): List<ScriptVersion> {
        return scriptVersionControl.getVersions(scriptId)
    }

    override fun switchVersion(scriptId: String, version: Int): Script? {
        return scriptVersionControl.switchVersion(scriptId, version)
    }

    override fun compareVersions(scriptId: String, fromVersion: Int, toVersion: Int): VersionDiff? {
        return scriptVersionControl.compareVersions(scriptId, fromVersion, toVersion)
    }

    override fun getMetadata(scriptId: String): ScriptMetadata? {
        return scriptMetadataManager.getMetadata(scriptId)
    }

    override fun updateMetadata(scriptId: String, metadata: ScriptMetadata): ScriptMetadata {
        return scriptMetadataManager.updateMetadata(scriptId, metadata)
    }

    override fun addTag(scriptId: String, tag: String): Script? {
        return scriptMetadataManager.addTag(scriptId, tag)
    }

    override fun removeTag(scriptId: String, tag: String): Script? {
        return scriptMetadataManager.removeTag(scriptId, tag)
    }

    override fun getDependencies(scriptId: String): List<String> {
        return scriptMetadataManager.getDependencies(scriptId)
    }

    override fun addDependency(scriptId: String, dependencyId: String): Boolean {
        return scriptMetadataManager.addDependency(scriptId, dependencyId)
    }

    override fun removeDependency(scriptId: String, dependencyId: String): Boolean {
        return scriptMetadataManager.removeDependency(scriptId, dependencyId)
    }

    override fun getUsage(scriptId: String): ScriptUsage {
        return scriptMetadataManager.getUsage(scriptId)
    }

    override fun executeScript(scriptId: String, parameters: Map<String, Any?>): Any? {
        // 获取脚本
        val script = scriptRepository.getScript(scriptId) ?: throw IllegalArgumentException("脚本不存在: $scriptId")
        
        // 记录开始时间
        val startTime = System.currentTimeMillis()
        
        try {
            // 执行脚本
            val result = scriptExecutor.execute(script.language, script.content, parameters)
            
            // 计算执行时间
            val duration = System.currentTimeMillis() - startTime
            
            // 记录执行情况
            scriptMetadataManager.recordExecution(scriptId, true, duration)
            
            return result
        } catch (e: Exception) {
            // 计算执行时间
            val duration = System.currentTimeMillis() - startTime
            
            // 记录执行情况
            scriptMetadataManager.recordExecution(scriptId, false, duration)
            
            logger.error("执行脚本出错: {}", e.message, e)
            throw e
        }
    }

    override fun validateScript(script: String, language: String): Boolean {
        try {
            // 尝试执行脚本
            scriptExecutor.execute(language, script, emptyMap())
            return true
        } catch (e: Exception) {
            logger.error("验证脚本出错: {}", e.message, e)
            return false
        }
    }

    override fun exportScript(scriptId: String): Map<String, Any?> {
        // 获取脚本
        val script = scriptRepository.getScript(scriptId) ?: throw IllegalArgumentException("脚本不存在: $scriptId")
        
        // 获取版本
        val versions = scriptVersionControl.getVersions(scriptId)
        
        // 获取元数据
        val metadata = scriptMetadataManager.getMetadata(scriptId)
        
        // 构建导出数据
        val exportData = mutableMapOf<String, Any?>()
        exportData["script"] = script
        exportData["versions"] = versions
        exportData["metadata"] = metadata
        exportData["exportTime"] = Date()
        exportData["exportVersion"] = "1.0"
        
        return exportData
    }

    override fun importScript(data: Map<String, Any?>): Script {
        try {
            @Suppress("UNCHECKED_CAST")
            val scriptMap = data["script"] as? Map<String, Any?> ?: throw IllegalArgumentException("导入数据格式错误，缺少script字段")
            
            // 转换为Script对象
            val script = objectMapper.convertValue(scriptMap, Script::class.java)
            
            // 生成新的ID
            val originalId = script.id
            script.id = UUID.randomUUID().toString()
            script.createTime = Date()
            script.updateTime = Date()
            script.version = 1
            
            // 保存脚本
            val savedScript = scriptRepository.saveScript(script)
            
            // 导入版本
            @Suppress("UNCHECKED_CAST")
            val versionsMap = data["versions"] as? List<Map<String, Any?>> ?: emptyList()
            
            for (versionMap in versionsMap) {
                val version = objectMapper.convertValue(versionMap, ScriptVersion::class.java)
                
                // 更新脚本ID
                val newVersion = version.copy(
                    id = UUID.randomUUID().toString(),
                    scriptId = savedScript.id,
                    createTime = Date(),
                    current = version.version == script.version
                )
                
                scriptRepository.saveScriptVersion(newVersion)
            }
            
            // 导入元数据
            @Suppress("UNCHECKED_CAST")
            val metadataMap = data["metadata"] as? Map<String, Any?>
            if (metadataMap != null) {
                val metadata = objectMapper.convertValue(metadataMap, ScriptMetadata::class.java)
                
                // 更新脚本ID
                val newMetadata = metadata.copy(
                    scriptId = savedScript.id,
                    createTime = Date(),
                    updateTime = Date(),
                    lastExecuteTime = null,
                    executeCount = 0,
                    successCount = 0,
                    failCount = 0,
                    avgExecuteTime = 0,
                    maxExecuteTime = 0,
                    minExecuteTime = 0,
                    dependencies = emptyList(),
                    dependents = emptyList()
                )
                
                scriptMetadataManager.updateMetadata(savedScript.id, newMetadata)
            }
            
            return savedScript
        } catch (e: Exception) {
            logger.error("导入脚本失败: {}", e.message, e)
            throw IllegalArgumentException("导入脚本失败: ${e.message}", e)
        }
    }
}
