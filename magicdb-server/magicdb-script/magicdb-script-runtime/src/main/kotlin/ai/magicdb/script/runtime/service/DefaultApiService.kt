package ai.magicdb.script.runtime.service

import ai.magicdb.script.api.ApiRepository
import ai.magicdb.script.api.ApiService
import ai.magicdb.script.api.ScriptExecutor
import ai.magicdb.script.api.model.ApiGroupInfo
import ai.magicdb.script.api.model.ApiInfo
import org.slf4j.LoggerFactory

/**
 * 默认API服务实现
 *
 * @author magicdb
 */
class DefaultApiService(
    private val apiRepository: ApiRepository,
    private val scriptExecutor: ScriptExecutor
) : ApiService {
    private val logger = LoggerFactory.getLogger(DefaultApiService::class.java)

    override fun saveApi(apiInfo: ApiInfo): ApiInfo {
        return apiRepository.saveApi(apiInfo)
    }

    override fun deleteApi(id: String): Boolean {
        return apiRepository.deleteApi(id)
    }

    override fun getApi(id: String): ApiInfo? {
        return apiRepository.getApi(id)
    }

    override fun getAllApis(): List<ApiInfo> {
        return apiRepository.getAllApis()
    }

    override fun getApisByGroupId(groupId: String): List<ApiInfo> {
        return apiRepository.getApisByGroupId(groupId)
    }

    override fun saveGroup(groupInfo: ApiGroupInfo): ApiGroupInfo {
        return apiRepository.saveGroup(groupInfo)
    }

    override fun deleteGroup(id: String): Boolean {
        return apiRepository.deleteGroup(id)
    }

    override fun getGroup(id: String): ApiGroupInfo? {
        return apiRepository.getGroup(id)
    }

    override fun getAllGroups(): List<ApiGroupInfo> {
        return apiRepository.getAllGroups()
    }

    override fun getChildGroups(parentId: String?): List<ApiGroupInfo> {
        return apiRepository.getChildGroups(parentId)
    }

    override fun executeApi(apiId: String, parameters: Map<String, Any?>): Any? {
        val apiInfo = apiRepository.getApi(apiId) ?: throw IllegalArgumentException("API not found: $apiId")
        return executeApiInternal(apiInfo, parameters)
    }

    override fun executeApi(path: String, method: String, parameters: Map<String, Any?>): Any? {
        val apiInfo = apiRepository.getAllApis()
            .find { it.path == path && it.method.equals(method, ignoreCase = true) }
            ?: throw IllegalArgumentException("API not found: $path [$method]")
        return executeApiInternal(apiInfo, parameters)
    }

    private fun executeApiInternal(apiInfo: ApiInfo, parameters: Map<String, Any?>): Any? {
        try {
            // 准备上下文
            val context = mutableMapOf<String, Any?>()
            context.putAll(parameters)
            context["api"] = apiInfo

            // 执行脚本
            return scriptExecutor.execute(apiInfo.language, apiInfo.script, context)
        } catch (e: Exception) {
            logger.error("执行API出错: {}", e.message, e)
            throw RuntimeException("执行API出错: ${e.message}", e)
        }
    }
}
