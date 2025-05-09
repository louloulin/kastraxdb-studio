package ai.magicdb.dataservice.core.manager

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceManager
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceResult
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
        return repository.saveService(service)
    }

    override fun deleteService(id: String): Boolean {
        return repository.deleteService(id)
    }

    override fun getService(id: String): DataService? {
        return repository.getService(id)
    }

    override fun getAllServices(): List<DataService> {
        return repository.getAllServices()
    }

    override fun getServicesByGroup(groupId: String): List<DataService> {
        return repository.getServicesByGroup(groupId)
    }

    override fun getServicesByTag(tag: String): List<DataService> {
        return repository.getServicesByTag(tag)
    }

    override fun saveGroup(group: ServiceGroup): ServiceGroup {
        return repository.saveGroup(group)
    }

    override fun deleteGroup(id: String): Boolean {
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
        return executor.execute(serviceId, parameters)
    }

    override fun executeScript(script: String, language: String, parameters: Map<String, Any?>): ServiceResult {
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
