package ai.magicdb.script.api

import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptGroup
import ai.magicdb.script.api.model.ScriptVersion

/**
 * 脚本存储接口
 *
 * @author magicdb
 */
interface ScriptRepository {
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
     * 保存脚本版本
     *
     * @param scriptVersion 脚本版本
     * @return 保存后的脚本版本
     */
    fun saveScriptVersion(scriptVersion: ScriptVersion): ScriptVersion

    /**
     * 获取脚本版本
     *
     * @param scriptId 脚本ID
     * @param version 版本号
     * @return 脚本版本
     */
    fun getScriptVersion(scriptId: String, version: Int): ScriptVersion?

    /**
     * 获取脚本所有版本
     *
     * @param scriptId 脚本ID
     * @return 脚本版本列表
     */
    fun getScriptVersions(scriptId: String): List<ScriptVersion>

    /**
     * 删除脚本版本
     *
     * @param scriptId 脚本ID
     * @param version 版本号
     * @return 是否删除成功
     */
    fun deleteScriptVersion(scriptId: String, version: Int): Boolean
}
