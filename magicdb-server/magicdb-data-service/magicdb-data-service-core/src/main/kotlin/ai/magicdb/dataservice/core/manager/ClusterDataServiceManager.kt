package ai.magicdb.dataservice.core.manager

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceManager
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceResult
import ai.magicdb.dataservice.core.cluster.ClusterManager
import ai.magicdb.dataservice.core.cluster.LoadBalancer
import ai.magicdb.dataservice.core.cluster.ServiceSynchronizer
import ai.magicdb.dataservice.core.cluster.ServiceSynchronizer.ChangeType
import ai.magicdb.dataservice.core.cluster.ServiceSynchronizer.EntityType
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * 集群数据服务管理器
 * 支持集群环境下的服务管理和执行
 *
 * @author magicdb
 */
@Primary
@Component
class ClusterDataServiceManager(
    private val delegate: DefaultDataServiceManager,
    private val repository: DataServiceRepository,
    private val executor: DataServiceExecutor,
    private val objectMapper: ObjectMapper,
    private val serviceSynchronizer: ServiceSynchronizer,
    private val clusterManager: ClusterManager,
    private val loadBalancer: LoadBalancer
) : DataServiceManager, ServiceSynchronizer.ServiceChangeListener {
    
    private val logger = LoggerFactory.getLogger(ClusterDataServiceManager::class.java)
    
    /**
     * 初始化
     */
    @PostConstruct
    fun init() {
        // 注册服务变更监听器
        serviceSynchronizer.addListener("dataServiceManager", this)
        
        logger.info("集群数据服务管理器初始化完成")
    }
    
    override fun saveService(service: DataService): DataService {
        // 保存服务
        val savedService = delegate.saveService(service)
        
        // 发布服务变更消息
        val changeType = if (service.id.isBlank()) ChangeType.CREATE else ChangeType.UPDATE
        serviceSynchronizer.publishServiceChange(changeType, EntityType.SERVICE, savedService.id, savedService)
        
        return savedService
    }
    
    override fun deleteService(id: String): Boolean {
        // 删除服务
        val result = delegate.deleteService(id)
        
        // 发布服务变更消息
        if (result) {
            serviceSynchronizer.publishServiceChange(ChangeType.DELETE, EntityType.SERVICE, id)
        }
        
        return result
    }
    
    override fun getService(id: String): DataService? {
        return delegate.getService(id)
    }
    
    override fun getAllServices(): List<DataService> {
        return delegate.getAllServices()
    }
    
    override fun getServicesByGroup(groupId: String): List<DataService> {
        return delegate.getServicesByGroup(groupId)
    }
    
    override fun getServicesByTag(tag: String): List<DataService> {
        return delegate.getServicesByTag(tag)
    }
    
    override fun saveGroup(group: ServiceGroup): ServiceGroup {
        // 保存分组
        val savedGroup = delegate.saveGroup(group)
        
        // 发布分组变更消息
        val changeType = if (group.id.isBlank()) ChangeType.CREATE else ChangeType.UPDATE
        serviceSynchronizer.publishServiceChange(changeType, EntityType.GROUP, savedGroup.id, savedGroup)
        
        return savedGroup
    }
    
    override fun deleteGroup(id: String): Boolean {
        // 删除分组
        val result = delegate.deleteGroup(id)
        
        // 发布分组变更消息
        if (result) {
            serviceSynchronizer.publishServiceChange(ChangeType.DELETE, EntityType.GROUP, id)
        }
        
        return result
    }
    
    override fun getGroup(id: String): ServiceGroup? {
        return delegate.getGroup(id)
    }
    
    override fun getAllGroups(): List<ServiceGroup> {
        return delegate.getAllGroups()
    }
    
    override fun getChildGroups(parentId: String?): List<ServiceGroup> {
        return delegate.getChildGroups(parentId)
    }
    
    override fun executeService(serviceId: String, parameters: Map<String, Any?>): ServiceResult {
        // 选择节点执行服务
        val nodeId = loadBalancer.selectNode(serviceId)
        
        // 如果是当前节点，直接执行
        if (nodeId == clusterManager.getCurrentNodeId()) {
            logger.debug("在当前节点执行服务: {}", serviceId)
            return delegate.executeService(serviceId, parameters)
        }
        
        // 如果是其他节点，通过远程调用执行
        logger.debug("在远程节点执行服务: {}, 节点: {}", serviceId, nodeId)
        
        // 这里应该实现远程调用逻辑，但由于我们没有实现远程调用接口，
        // 所以暂时在本地执行
        return delegate.executeService(serviceId, parameters)
    }
    
    override fun executeScript(script: String, language: String, parameters: Map<String, Any?>): ServiceResult {
        // 脚本执行不需要负载均衡，直接在本地执行
        return delegate.executeScript(script, language, parameters)
    }
    
    override fun validateScript(script: String, language: String): ServiceResult {
        // 脚本验证不需要负载均衡，直接在本地执行
        return delegate.validateScript(script, language)
    }
    
    override fun exportService(serviceId: String): Map<String, Any?> {
        return delegate.exportService(serviceId)
    }
    
    override fun importService(data: Map<String, Any?>): DataService {
        // 导入服务
        val service = delegate.importService(data)
        
        // 发布服务变更消息
        serviceSynchronizer.publishServiceChange(ChangeType.CREATE, EntityType.SERVICE, service.id, service)
        
        return service
    }
    
    /**
     * 服务变更事件处理
     */
    override fun onServiceChanged(changeType: ChangeType, service: DataService) {
        try {
            logger.debug("接收到服务变更事件: {}, 服务: {}", changeType, service.id)
            
            // 更新本地缓存
            repository.saveService(service)
        } catch (e: Exception) {
            logger.error("处理服务变更事件失败", e)
        }
    }
    
    /**
     * 服务删除事件处理
     */
    override fun onServiceDeleted(serviceId: String) {
        try {
            logger.debug("接收到服务删除事件: {}", serviceId)
            
            // 从本地缓存中删除
            repository.deleteService(serviceId)
        } catch (e: Exception) {
            logger.error("处理服务删除事件失败", e)
        }
    }
    
    /**
     * 分组变更事件处理
     */
    override fun onGroupChanged(changeType: ChangeType, group: ServiceGroup) {
        try {
            logger.debug("接收到分组变更事件: {}, 分组: {}", changeType, group.id)
            
            // 更新本地缓存
            repository.saveGroup(group)
        } catch (e: Exception) {
            logger.error("处理分组变更事件失败", e)
        }
    }
    
    /**
     * 分组删除事件处理
     */
    override fun onGroupDeleted(groupId: String) {
        try {
            logger.debug("接收到分组删除事件: {}", groupId)
            
            // 从本地缓存中删除
            repository.deleteGroup(groupId)
        } catch (e: Exception) {
            logger.error("处理分组删除事件失败", e)
        }
    }
    
    /**
     * 服务缓存清除事件处理
     */
    override fun onServiceCacheCleared(serviceId: String) {
        try {
            logger.debug("接收到服务缓存清除事件: {}", serviceId)
            
            // 清除本地缓存
            // 这里需要访问缓存管理器，但由于我们没有直接的引用，
            // 所以暂时不实现
        } catch (e: Exception) {
            logger.error("处理服务缓存清除事件失败", e)
        }
    }
    
    /**
     * 所有缓存清除事件处理
     */
    override fun onCacheCleared() {
        try {
            logger.debug("接收到所有缓存清除事件")
            
            // 清除所有本地缓存
            // 这里需要访问缓存管理器，但由于我们没有直接的引用，
            // 所以暂时不实现
        } catch (e: Exception) {
            logger.error("处理所有缓存清除事件失败", e)
        }
    }
}
