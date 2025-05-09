package ai.magicdb.script.api

import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptVersion

/**
 * 脚本版本控制接口
 *
 * @author magicdb
 */
interface ScriptVersionControl {
    /**
     * 创建新版本
     *
     * @param script 脚本
     * @param description 版本描述
     * @param creator 创建者
     * @return 新版本
     */
    fun createVersion(script: Script, description: String, creator: String?): ScriptVersion

    /**
     * 获取版本
     *
     * @param scriptId 脚本ID
     * @param version 版本号
     * @return 脚本版本
     */
    fun getVersion(scriptId: String, version: Int): ScriptVersion?

    /**
     * 获取所有版本
     *
     * @param scriptId 脚本ID
     * @return 版本列表
     */
    fun getVersions(scriptId: String): List<ScriptVersion>

    /**
     * 获取当前版本
     *
     * @param scriptId 脚本ID
     * @return 当前版本
     */
    fun getCurrentVersion(scriptId: String): ScriptVersion?

    /**
     * 切换版本
     *
     * @param scriptId 脚本ID
     * @param version 版本号
     * @return 切换后的脚本
     */
    fun switchVersion(scriptId: String, version: Int): Script?

    /**
     * 比较版本
     *
     * @param scriptId 脚本ID
     * @param fromVersion 起始版本
     * @param toVersion 目标版本
     * @return 差异信息
     */
    fun compareVersions(scriptId: String, fromVersion: Int, toVersion: Int): VersionDiff?

    /**
     * 删除版本
     *
     * @param scriptId 脚本ID
     * @param version 版本号
     * @return 是否删除成功
     */
    fun deleteVersion(scriptId: String, version: Int): Boolean
}
