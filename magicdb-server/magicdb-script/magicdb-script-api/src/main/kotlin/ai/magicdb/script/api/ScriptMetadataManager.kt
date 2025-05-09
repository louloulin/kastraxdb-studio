package ai.magicdb.script.api

import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptMetadata

/**
 * 脚本元数据管理接口
 *
 * @author magicdb
 */
interface ScriptMetadataManager {
    /**
     * 获取脚本元数据
     *
     * @param scriptId 脚本ID
     * @return 脚本元数据
     */
    fun getMetadata(scriptId: String): ScriptMetadata?

    /**
     * 更新脚本元数据
     *
     * @param scriptId 脚本ID
     * @param metadata 元数据
     * @return 更新后的元数据
     */
    fun updateMetadata(scriptId: String, metadata: ScriptMetadata): ScriptMetadata

    /**
     * 添加标签
     *
     * @param scriptId 脚本ID
     * @param tag 标签
     * @return 更新后的脚本
     */
    fun addTag(scriptId: String, tag: String): Script?

    /**
     * 删除标签
     *
     * @param scriptId 脚本ID
     * @param tag 标签
     * @return 更新后的脚本
     */
    fun removeTag(scriptId: String, tag: String): Script?

    /**
     * 获取脚本依赖
     *
     * @param scriptId 脚本ID
     * @return 依赖脚本ID列表
     */
    fun getDependencies(scriptId: String): List<String>

    /**
     * 添加依赖
     *
     * @param scriptId 脚本ID
     * @param dependencyId 依赖脚本ID
     * @return 是否添加成功
     */
    fun addDependency(scriptId: String, dependencyId: String): Boolean

    /**
     * 删除依赖
     *
     * @param scriptId 脚本ID
     * @param dependencyId 依赖脚本ID
     * @return 是否删除成功
     */
    fun removeDependency(scriptId: String, dependencyId: String): Boolean

    /**
     * 获取脚本使用情况
     *
     * @param scriptId 脚本ID
     * @return 使用情况
     */
    fun getUsage(scriptId: String): ScriptUsage

    /**
     * 记录脚本执行
     *
     * @param scriptId 脚本ID
     * @param success 是否成功
     * @param duration 执行时间（毫秒）
     */
    fun recordExecution(scriptId: String, success: Boolean, duration: Long)
}
