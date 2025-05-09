package ai.magicdb.script.api

import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptGroup
import ai.magicdb.script.api.model.ScriptMetadata
import ai.magicdb.script.api.model.ScriptVersion

/**
 * 脚本管理接口
 *
 * @author magicdb
 */
interface ScriptManager {
    /**
     * 保存脚本
     *
     * @param script 脚本
     * @return 保存后的脚本
     */
    fun saveScript(script: Script): Script

    /**
     * 删除脚本
     *
     * @param id 脚本ID
     * @return 是否删除成功
     */
    fun deleteScript(id: String): Boolean

    /**
     * 获取脚本
     *
     * @param id 脚本ID
     * @return 脚本
     */
    fun getScript(id: String): Script?

    /**
     * 获取所有脚本
     *
     * @return 脚本列表
     */
    fun getAllScripts(): List<Script>

    /**
     * 根据分组获取脚本
     *
     * @param groupId 分组ID
     * @return 脚本列表
     */
    fun getScriptsByGroup(groupId: String): List<Script>

    /**
     * 根据标签获取脚本
     *
     * @param tag 标签
     * @return 脚本列表
     */
    fun getScriptsByTag(tag: String): List<Script>

    /**
     * 保存脚本分组
     *
     * @param group 脚本分组
     * @return 保存后的脚本分组
     */
    fun saveGroup(group: ScriptGroup): ScriptGroup

    /**
     * 删除脚本分组
     *
     * @param id 分组ID
     * @return 是否删除成功
     */
    fun deleteGroup(id: String): Boolean

    /**
     * 获取脚本分组
     *
     * @param id 分组ID
     * @return 脚本分组
     */
    fun getGroup(id: String): ScriptGroup?

    /**
     * 获取所有脚本分组
     *
     * @return 脚本分组列表
     */
    fun getAllGroups(): List<ScriptGroup>

    /**
     * 获取子分组
     *
     * @param parentId 父分组ID
     * @return 脚本分组列表
     */
    fun getChildGroups(parentId: String?): List<ScriptGroup>

    /**
     * 创建脚本版本
     *
     * @param scriptId 脚本ID
     * @param description 版本描述
     * @param creator 创建者
     * @return 新版本
     */
    fun createVersion(scriptId: String, description: String, creator: String?): ScriptVersion?

    /**
     * 获取脚本版本
     *
     * @param scriptId 脚本ID
     * @param version 版本号
     * @return 脚本版本
     */
    fun getVersion(scriptId: String, version: Int): ScriptVersion?

    /**
     * 获取脚本所有版本
     *
     * @param scriptId 脚本ID
     * @return 脚本版本列表
     */
    fun getVersions(scriptId: String): List<ScriptVersion>

    /**
     * 切换脚本版本
     *
     * @param scriptId 脚本ID
     * @param version 版本号
     * @return 切换后的脚本
     */
    fun switchVersion(scriptId: String, version: Int): Script?

    /**
     * 比较脚本版本
     *
     * @param scriptId 脚本ID
     * @param fromVersion 起始版本
     * @param toVersion 目标版本
     * @return 差异信息
     */
    fun compareVersions(scriptId: String, fromVersion: Int, toVersion: Int): VersionDiff?

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
     * 执行脚本
     *
     * @param scriptId 脚本ID
     * @param parameters 参数
     * @return 执行结果
     */
    fun executeScript(scriptId: String, parameters: Map<String, Any?>): Any?

    /**
     * 验证脚本
     *
     * @param script 脚本内容
     * @param language 脚本语言
     * @return 验证结果
     */
    fun validateScript(script: String, language: String): Boolean

    /**
     * 导出脚本
     *
     * @param scriptId 脚本ID
     * @return 导出的数据
     */
    fun exportScript(scriptId: String): Map<String, Any?>

    /**
     * 导入脚本
     *
     * @param data 导入的数据
     * @return 导入的脚本
     */
    fun importScript(data: Map<String, Any?>): Script
}
