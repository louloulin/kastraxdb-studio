package ai.magicdb.script.api

import ai.magicdb.script.api.model.ApiGroupInfo
import ai.magicdb.script.api.model.ApiInfo

/**
 * API存储接口
 *
 * @author magicdb
 */
interface ApiRepository {
    /**
     * 保存API
     *
     * @param apiInfo API信息
     * @return 保存后的API信息
     */
    fun saveApi(apiInfo: ApiInfo): ApiInfo

    /**
     * 删除API
     *
     * @param id API ID
     * @return 是否删除成功
     */
    fun deleteApi(id: String): Boolean

    /**
     * 获取API
     *
     * @param id API ID
     * @return API信息
     */
    fun getApi(id: String): ApiInfo?

    /**
     * 获取所有API
     *
     * @return API列表
     */
    fun getAllApis(): List<ApiInfo>

    /**
     * 获取分组下的API
     *
     * @param groupId 分组ID
     * @return API列表
     */
    fun getApisByGroupId(groupId: String): List<ApiInfo>

    /**
     * 保存分组
     *
     * @param groupInfo 分组信息
     * @return 保存后的分组信息
     */
    fun saveGroup(groupInfo: ApiGroupInfo): ApiGroupInfo

    /**
     * 删除分组
     *
     * @param id 分组ID
     * @return 是否删除成功
     */
    fun deleteGroup(id: String): Boolean

    /**
     * 获取分组
     *
     * @param id 分组ID
     * @return 分组信息
     */
    fun getGroup(id: String): ApiGroupInfo?

    /**
     * 获取所有分组
     *
     * @return 分组列表
     */
    fun getAllGroups(): List<ApiGroupInfo>

    /**
     * 获取子分组
     *
     * @param parentId 父分组ID
     * @return 分组列表
     */
    fun getChildGroups(parentId: String?): List<ApiGroupInfo>
}
