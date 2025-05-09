package ai.magicdb.script.runtime.version

import ai.magicdb.script.api.DiffLine
import ai.magicdb.script.api.DiffType
import ai.magicdb.script.api.ScriptRepository
import ai.magicdb.script.api.ScriptVersionControl
import ai.magicdb.script.api.VersionDiff
import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptVersion
import org.slf4j.LoggerFactory
import java.util.*

/**
 * 默认脚本版本控制实现
 *
 * @author magicdb
 */
class DefaultScriptVersionControl(private val scriptRepository: ScriptRepository) : ScriptVersionControl {
    private val logger = LoggerFactory.getLogger(DefaultScriptVersionControl::class.java)

    override fun createVersion(script: Script, description: String, creator: String?): ScriptVersion {
        // 获取当前最大版本号
        val versions = scriptRepository.getScriptVersions(script.id)
        val maxVersion = versions.maxByOrNull { it.version }?.version ?: 0
        
        // 创建新版本
        val newVersion = ScriptVersion(
            scriptId = script.id,
            version = maxVersion + 1,
            content = script.content,
            language = script.language,
            description = description,
            createTime = Date(),
            creator = creator,
            current = true
        )
        
        // 更新之前的当前版本
        versions.filter { it.current }.forEach {
            val updatedVersion = it.copy(current = false)
            scriptRepository.saveScriptVersion(updatedVersion)
        }
        
        // 保存新版本
        return scriptRepository.saveScriptVersion(newVersion)
    }

    override fun getVersion(scriptId: String, version: Int): ScriptVersion? {
        return scriptRepository.getScriptVersion(scriptId, version)
    }

    override fun getVersions(scriptId: String): List<ScriptVersion> {
        return scriptRepository.getScriptVersions(scriptId)
    }

    override fun getCurrentVersion(scriptId: String): ScriptVersion? {
        val versions = scriptRepository.getScriptVersions(scriptId)
        return versions.find { it.current }
    }

    override fun switchVersion(scriptId: String, version: Int): Script? {
        // 获取脚本
        val script = scriptRepository.getScript(scriptId) ?: return null
        
        // 获取指定版本
        val targetVersion = scriptRepository.getScriptVersion(scriptId, version) ?: return null
        
        // 更新脚本内容
        val updatedScript = script.copy(
            content = targetVersion.content,
            language = targetVersion.language,
            version = targetVersion.version,
            updateTime = Date()
        )
        
        // 更新版本状态
        val versions = scriptRepository.getScriptVersions(scriptId)
        versions.forEach {
            val updatedVersion = it.copy(current = it.version == version)
            scriptRepository.saveScriptVersion(updatedVersion)
        }
        
        // 保存脚本
        return scriptRepository.saveScript(updatedScript)
    }

    override fun compareVersions(scriptId: String, fromVersion: Int, toVersion: Int): VersionDiff? {
        // 获取版本
        val fromVersionObj = scriptRepository.getScriptVersion(scriptId, fromVersion) ?: return null
        val toVersionObj = scriptRepository.getScriptVersion(scriptId, toVersion) ?: return null
        
        // 比较内容
        val fromLines = fromVersionObj.content.split("\n")
        val toLines = toVersionObj.content.split("\n")
        
        // 使用最长公共子序列算法计算差异
        val diffLines = calculateDiff(fromLines, toLines)
        
        return VersionDiff(
            scriptId = scriptId,
            fromVersion = fromVersion,
            toVersion = toVersion,
            diffLines = diffLines
        )
    }

    override fun deleteVersion(scriptId: String, version: Int): Boolean {
        // 获取版本
        val versionObj = scriptRepository.getScriptVersion(scriptId, version) ?: return false
        
        // 不允许删除当前版本
        if (versionObj.current) {
            logger.warn("不能删除当前版本: {}, 版本: {}", scriptId, version)
            return false
        }
        
        // 删除版本
        return scriptRepository.deleteScriptVersion(scriptId, version)
    }

    /**
     * 计算差异
     *
     * @param fromLines 原始行
     * @param toLines 目标行
     * @return 差异行
     */
    private fun calculateDiff(fromLines: List<String>, toLines: List<String>): List<DiffLine> {
        val diffLines = mutableListOf<DiffLine>()
        
        // 使用简单的行比较算法
        val maxLines = maxOf(fromLines.size, toLines.size)
        
        for (i in 0 until maxLines) {
            when {
                i >= fromLines.size -> {
                    // 添加行
                    diffLines.add(DiffLine(
                        lineNumber = i + 1,
                        type = DiffType.ADD,
                        oldContent = null,
                        newContent = toLines[i]
                    ))
                }
                i >= toLines.size -> {
                    // 删除行
                    diffLines.add(DiffLine(
                        lineNumber = i + 1,
                        type = DiffType.DELETE,
                        oldContent = fromLines[i],
                        newContent = null
                    ))
                }
                fromLines[i] != toLines[i] -> {
                    // 修改行
                    diffLines.add(DiffLine(
                        lineNumber = i + 1,
                        type = DiffType.MODIFY,
                        oldContent = fromLines[i],
                        newContent = toLines[i]
                    ))
                }
                else -> {
                    // 不变行
                    diffLines.add(DiffLine(
                        lineNumber = i + 1,
                        type = DiffType.UNCHANGED,
                        oldContent = fromLines[i],
                        newContent = toLines[i]
                    ))
                }
            }
        }
        
        return diffLines
    }
}
