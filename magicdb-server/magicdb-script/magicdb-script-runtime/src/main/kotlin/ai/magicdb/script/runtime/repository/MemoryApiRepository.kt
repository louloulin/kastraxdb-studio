package ai.magicdb.script.runtime.repository

import ai.magicdb.script.api.ApiRepository
import ai.magicdb.script.api.model.ApiGroupInfo
import ai.magicdb.script.api.model.ApiInfo
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 内存API存储实现
 *
 * @author magicdb
 */
class MemoryApiRepository : ApiRepository {
    private val apis = ConcurrentHashMap<String, ApiInfo>()
    private val groups = ConcurrentHashMap<String, ApiGroupInfo>()

    override fun saveApi(apiInfo: ApiInfo): ApiInfo {
        apiInfo.updateTime = Date()
        if (apiInfo.id.isBlank()) {
            apiInfo.id = UUID.randomUUID().toString()
            apiInfo.createTime = Date()
        }
        apis[apiInfo.id] = apiInfo
        return apiInfo
    }

    override fun deleteApi(id: String): Boolean {
        return apis.remove(id) != null
    }

    override fun getApi(id: String): ApiInfo? {
        return apis[id]
    }

    override fun getAllApis(): List<ApiInfo> {
        return apis.values.toList()
    }

    override fun getApisByGroupId(groupId: String): List<ApiInfo> {
        return apis.values.filter { it.groupId == groupId }
    }

    override fun saveGroup(groupInfo: ApiGroupInfo): ApiGroupInfo {
        groupInfo.updateTime = Date()
        if (groupInfo.id.isBlank()) {
            groupInfo.id = UUID.randomUUID().toString()
            groupInfo.createTime = Date()
        }
        groups[groupInfo.id] = groupInfo
        return groupInfo
    }

    override fun deleteGroup(id: String): Boolean {
        return groups.remove(id) != null
    }

    override fun getGroup(id: String): ApiGroupInfo? {
        return groups[id]
    }

    override fun getAllGroups(): List<ApiGroupInfo> {
        return groups.values.toList()
    }

    override fun getChildGroups(parentId: String?): List<ApiGroupInfo> {
        return groups.values.filter { it.parentId == parentId }
    }
}
