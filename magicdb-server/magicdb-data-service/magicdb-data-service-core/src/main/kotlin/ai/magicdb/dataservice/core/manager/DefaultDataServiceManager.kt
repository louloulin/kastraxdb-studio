package ai.magicdb.dataservice.core.manager

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceManager
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceResult
import ai.magicdb.dataservice.core.security.DataServicePermissionUtils
import ai.magicdb.server.tools.common.exception.PermissionDeniedBusinessException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import java.util.*

/**
 * 默认数据服务管理实现
 *
 * @author magicdb
 */
class DefaultDataServiceManager(
    private val repository: DataServiceRepository,
    private val executor: DataServiceExecutor,
    private val objectMapper: ObjectMapper
) : DataServiceManager {
    private val logger = LoggerFactory.getLogger(DefaultDataServiceManager::class.java)

    override fun saveService(service: DataService): DataService {
        // 如果是新服务，设置当前用户为创建者
        if (service.id.isBlank()) {
            DataServicePermissionUtils.setCurrentUserAsCreator(service)
        } else {
            // 如果是更新服务，检查操作权限
            val existingService = repository.getService(service.id)
            if (existingService != null) {
                DataServicePermissionUtils.checkServiceOperationPermission(existingService)
            }
        }

        return repository.saveService(service)
    }

    override fun deleteService(id: String): Boolean {
        // 检查操作权限
        val service = repository.getService(id)
        if (service != null) {
            DataServicePermissionUtils.checkServiceOperationPermission(service)
        }

        return repository.deleteService(id)
    }

    override fun getService(id: String): DataService? {
        val service = repository.getService(id) ?: return null

        // 检查访问权限
        if (!DataServicePermissionUtils.checkServiceAccessPermission(service)) {
            throw PermissionDeniedBusinessException()
        }

        return service
    }

    override fun getAllServices(): List<DataService> {
        // 获取所有服务
        val allServices = repository.getAllServices()

        // 过滤没有访问权限的服务
        return allServices.filter { service ->
            try {
                DataServicePermissionUtils.checkServiceAccessPermission(service)
            } catch (e: Exception) {
                false
            }
        }
    }

    override fun getServicesByGroup(groupId: String): List<DataService> {
        // 获取分组下的所有服务
        val services = repository.getServicesByGroup(groupId)

        // 过滤没有访问权限的服务
        return services.filter { service ->
            try {
                DataServicePermissionUtils.checkServiceAccessPermission(service)
            } catch (e: Exception) {
                false
            }
        }
    }

    override fun getServicesByTag(tag: String): List<DataService> {
        // 获取标签下的所有服务
        val services = repository.getServicesByTag(tag)

        // 过滤没有访问权限的服务
        return services.filter { service ->
            try {
                DataServicePermissionUtils.checkServiceAccessPermission(service)
            } catch (e: Exception) {
                false
            }
        }
    }

    override fun saveGroup(group: ServiceGroup): ServiceGroup {
        // 如果是新分组，设置当前用户为创建者
        if (group.id.isBlank()) {
            DataServicePermissionUtils.setCurrentUserAsCreator(group)
        } else {
            // 如果是更新分组，检查操作权限
            val existingGroup = repository.getGroup(group.id)
            if (existingGroup != null) {
                DataServicePermissionUtils.checkGroupOperationPermission(existingGroup)
            }
        }

        return repository.saveGroup(group)
    }

    override fun deleteGroup(id: String): Boolean {
        // 检查操作权限
        val group = repository.getGroup(id)
        if (group != null) {
            DataServicePermissionUtils.checkGroupOperationPermission(group)
        }

        return repository.deleteGroup(id)
    }

    override fun getGroup(id: String): ServiceGroup? {
        return repository.getGroup(id)
    }

    override fun getAllGroups(): List<ServiceGroup> {
        return repository.getAllGroups()
    }

    override fun getChildGroups(parentId: String?): List<ServiceGroup> {
        return repository.getChildGroups(parentId)
    }

    override fun executeService(serviceId: String, parameters: Map<String, Any?>): ServiceResult {
        // 获取服务
        val service = repository.getService(serviceId)
            ?: throw IllegalArgumentException("服务不存在: $serviceId")

        // 检查访问权限
        if (!DataServicePermissionUtils.checkServiceAccessPermission(service)) {
            throw PermissionDeniedBusinessException()
        }

        // 检查脚本执行权限
        if (!DataServicePermissionUtils.checkScriptExecutionPermission(service.script, service.language)) {
            logger.warn("没有执行脚本的权限")
            throw PermissionDeniedBusinessException()
        }

        return executor.execute(serviceId, parameters)
    }

    override fun executeScript(script: String, language: String, parameters: Map<String, Any?>): ServiceResult {
        // 检查脚本执行权限
        if (!DataServicePermissionUtils.checkScriptExecutionPermission(script, language)) {
            logger.warn("没有执行脚本的权限")
            throw PermissionDeniedBusinessException()
        }

        return executor.executeScript(script, language, parameters)
    }

    override fun validateScript(script: String, language: String): ServiceResult {
        return executor.validateScript(script, language)
    }

    override fun exportService(serviceId: String): Map<String, Any?> {
        val service = repository.getService(serviceId)
            ?: throw IllegalArgumentException("服务不存在: $serviceId")

        // 转换为Map
        val serviceMap = objectMapper.convertValue(service, Map::class.java)

        // 添加导出信息
        val exportInfo = mutableMapOf<String, Any?>()
        exportInfo["exportTime"] = Date()
        exportInfo["version"] = "1.0"

        // 组合结果
        val result = mutableMapOf<String, Any?>()
        result["service"] = serviceMap
        result["exportInfo"] = exportInfo

        return result
    }

    override fun importService(data: Map<String, Any?>): DataService {
        try {
            @Suppress("UNCHECKED_CAST")
            val serviceMap = data["service"] as? Map<String, Any?>
                ?: throw IllegalArgumentException("导入数据格式错误，缺少service字段")

            // 转换为DataService对象
            val service = objectMapper.convertValue(serviceMap, DataService::class.java)

            // 生成新的ID
            service.id = UUID.randomUUID().toString()
            service.createTime = Date()
            service.updateTime = Date()

            // 保存服务
            return repository.saveService(service)
        } catch (e: Exception) {
            logger.error("导入服务失败: {}", e.message, e)
            throw IllegalArgumentException("导入服务失败: ${e.message}", e)
        }
    }
}
