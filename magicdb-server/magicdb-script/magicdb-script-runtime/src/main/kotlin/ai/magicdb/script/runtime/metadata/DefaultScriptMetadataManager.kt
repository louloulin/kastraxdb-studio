package ai.magicdb.script.runtime.metadata

import ai.magicdb.script.api.ExecutionRecord
import ai.magicdb.script.api.ScriptMetadataManager
import ai.magicdb.script.api.ScriptRepository
import ai.magicdb.script.api.ScriptUsage
import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptMetadata
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 默认脚本元数据管理实现
 *
 * @author magicdb
 */
class DefaultScriptMetadataManager(private val scriptRepository: ScriptRepository) : ScriptMetadataManager {
    private val logger = LoggerFactory.getLogger(DefaultScriptMetadataManager::class.java)
    
    // 脚本元数据
    private val metadataMap = ConcurrentHashMap<String, ScriptMetadata>()
    
    // 脚本执行记录
    private val executionRecords = ConcurrentHashMap<String, ConcurrentLinkedQueue<ExecutionRecord>>()
    
    // 最大执行记录数
    private val MAX_EXECUTION_RECORDS = 100

    override fun getMetadata(scriptId: String): ScriptMetadata? {
        return metadataMap[scriptId]
    }

    override fun updateMetadata(scriptId: String, metadata: ScriptMetadata): ScriptMetadata {
        metadataMap[scriptId] = metadata
        return metadata
    }

    override fun addTag(scriptId: String, tag: String): Script? {
        // 获取脚本
        val script = scriptRepository.getScript(scriptId) ?: return null
        
        // 添加标签
        val tags = script.tags.toMutableList()
        if (!tags.contains(tag)) {
            tags.add(tag)
            
            // 更新脚本
            val updatedScript = script.copy(
                tags = tags,
                updateTime = Date()
            )
            
            return scriptRepository.saveScript(updatedScript)
        }
        
        return script
    }

    override fun removeTag(scriptId: String, tag: String): Script? {
        // 获取脚本
        val script = scriptRepository.getScript(scriptId) ?: return null
        
        // 删除标签
        val tags = script.tags.toMutableList()
        if (tags.contains(tag)) {
            tags.remove(tag)
            
            // 更新脚本
            val updatedScript = script.copy(
                tags = tags,
                updateTime = Date()
            )
            
            return scriptRepository.saveScript(updatedScript)
        }
        
        return script
    }

    override fun getDependencies(scriptId: String): List<String> {
        val metadata = metadataMap[scriptId] ?: return emptyList()
        return metadata.dependencies
    }

    override fun addDependency(scriptId: String, dependencyId: String): Boolean {
        // 检查脚本是否存在
        val script = scriptRepository.getScript(scriptId) ?: return false
        val dependency = scriptRepository.getScript(dependencyId) ?: return false
        
        // 获取元数据
        val metadata = metadataMap.computeIfAbsent(scriptId) {
            ScriptMetadata(scriptId = it)
        }
        
        // 添加依赖
        val dependencies = metadata.dependencies.toMutableList()
        if (!dependencies.contains(dependencyId)) {
            dependencies.add(dependencyId)
            
            // 更新元数据
            val updatedMetadata = metadata.copy(
                dependencies = dependencies,
                updateTime = Date()
            )
            
            metadataMap[scriptId] = updatedMetadata
            
            // 更新被依赖脚本的元数据
            val dependencyMetadata = metadataMap.computeIfAbsent(dependencyId) {
                ScriptMetadata(scriptId = it)
            }
            
            val dependents = dependencyMetadata.dependents.toMutableList()
            if (!dependents.contains(scriptId)) {
                dependents.add(scriptId)
                
                val updatedDependencyMetadata = dependencyMetadata.copy(
                    dependents = dependents,
                    updateTime = Date()
                )
                
                metadataMap[dependencyId] = updatedDependencyMetadata
            }
            
            return true
        }
        
        return false
    }

    override fun removeDependency(scriptId: String, dependencyId: String): Boolean {
        // 获取元数据
        val metadata = metadataMap[scriptId] ?: return false
        
        // 删除依赖
        val dependencies = metadata.dependencies.toMutableList()
        if (dependencies.contains(dependencyId)) {
            dependencies.remove(dependencyId)
            
            // 更新元数据
            val updatedMetadata = metadata.copy(
                dependencies = dependencies,
                updateTime = Date()
            )
            
            metadataMap[scriptId] = updatedMetadata
            
            // 更新被依赖脚本的元数据
            val dependencyMetadata = metadataMap[dependencyId]
            if (dependencyMetadata != null) {
                val dependents = dependencyMetadata.dependents.toMutableList()
                if (dependents.contains(scriptId)) {
                    dependents.remove(scriptId)
                    
                    val updatedDependencyMetadata = dependencyMetadata.copy(
                        dependents = dependents,
                        updateTime = Date()
                    )
                    
                    metadataMap[dependencyId] = updatedDependencyMetadata
                }
            }
            
            return true
        }
        
        return false
    }

    override fun getUsage(scriptId: String): ScriptUsage {
        // 获取元数据
        val metadata = metadataMap[scriptId] ?: return ScriptUsage(
            scriptId = scriptId,
            executeCount = 0,
            successCount = 0,
            failCount = 0,
            avgExecuteTime = 0,
            maxExecuteTime = 0,
            minExecuteTime = 0,
            lastExecuteTime = null,
            recentExecutions = emptyList()
        )
        
        // 获取执行记录
        val records = executionRecords[scriptId]?.toList() ?: emptyList()
        
        return ScriptUsage(
            scriptId = scriptId,
            executeCount = metadata.executeCount,
            successCount = metadata.successCount,
            failCount = metadata.failCount,
            avgExecuteTime = metadata.avgExecuteTime,
            maxExecuteTime = metadata.maxExecuteTime,
            minExecuteTime = metadata.minExecuteTime,
            lastExecuteTime = metadata.lastExecuteTime,
            recentExecutions = records
        )
    }

    override fun recordExecution(scriptId: String, success: Boolean, duration: Long) {
        // 获取元数据
        val metadata = metadataMap.computeIfAbsent(scriptId) {
            ScriptMetadata(scriptId = it)
        }
        
        // 更新执行统计
        val executeCount = metadata.executeCount + 1
        val successCount = if (success) metadata.successCount + 1 else metadata.successCount
        val failCount = if (!success) metadata.failCount + 1 else metadata.failCount
        
        // 计算平均执行时间
        val totalTime = metadata.avgExecuteTime * metadata.executeCount + duration
        val avgExecuteTime = totalTime / executeCount
        
        // 更新最大最小执行时间
        val maxExecuteTime = if (metadata.executeCount == 0 || duration > metadata.maxExecuteTime) duration else metadata.maxExecuteTime
        val minExecuteTime = if (metadata.executeCount == 0 || duration < metadata.minExecuteTime) duration else metadata.minExecuteTime
        
        // 更新元数据
        val updatedMetadata = metadata.copy(
            executeCount = executeCount,
            successCount = successCount,
            failCount = failCount,
            avgExecuteTime = avgExecuteTime,
            maxExecuteTime = maxExecuteTime,
            minExecuteTime = minExecuteTime,
            lastExecuteTime = Date(),
            updateTime = Date()
        )
        
        metadataMap[scriptId] = updatedMetadata
        
        // 添加执行记录
        val record = ExecutionRecord(
            executeTime = Date(),
            success = success,
            duration = duration,
            errorMessage = if (!success) "执行失败" else null
        )
        
        val records = executionRecords.computeIfAbsent(scriptId) {
            ConcurrentLinkedQueue()
        }
        
        records.add(record)
        
        // 限制记录数量
        while (records.size > MAX_EXECUTION_RECORDS) {
            records.poll()
        }
    }
}
