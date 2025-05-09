package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 内存数据服务存储实现
 *
 * @author magicdb
 */
class MemoryDataServiceRepository : DataServiceRepository {
    private val logger = LoggerFactory.getLogger(MemoryDataServiceRepository::class.java)
    
    private val services = ConcurrentHashMap<String, DataService>()
    private val groups = ConcurrentHashMap<String, ServiceGroup>()

    override fun saveService(service: DataService): DataService {
        service.updateTime = Date()
        if (service.id.isBlank()) {
            service.id = UUID.randomUUID().toString()
            service.createTime = Date()
        }
        services[service.id] = service
        logger.info("保存数据服务: {}", service.id)
        return service
    }

    override fun deleteService(id: String): Boolean {
        val removed = services.remove(id) != null
        if (removed) {
            logger.info("删除数据服务: {}", id)
        } else {
            logger.warn("删除数据服务失败，未找到服务: {}", id)
        }
        return removed
    }

    override fun getService(id: String): DataService? {
        return services[id]
    }

    override fun getAllServices(): List<DataService> {
        return services.values.toList()
    }

    override fun getServicesByGroup(groupId: String): List<DataService> {
        return services.values.filter { it.groupId == groupId }
    }

    override fun getServicesByTag(tag: String): List<DataService> {
        return services.values.filter { it.tags.contains(tag) }
    }

    override fun saveGroup(group: ServiceGroup): ServiceGroup {
        group.updateTime = Date()
        if (group.id.isBlank()) {
            group.id = UUID.randomUUID().toString()
            group.createTime = Date()
        }
        groups[group.id] = group
        logger.info("保存服务分组: {}", group.id)
        return group
    }

    override fun deleteGroup(id: String): Boolean {
        val removed = groups.remove(id) != null
        if (removed) {
            logger.info("删除服务分组: {}", id)
        } else {
            logger.warn("删除服务分组失败，未找到分组: {}", id)
        }
        return removed
    }

    override fun getGroup(id: String): ServiceGroup? {
        return groups[id]
    }

    override fun getAllGroups(): List<ServiceGroup> {
        return groups.values.toList()
    }

    override fun getChildGroups(parentId: String?): List<ServiceGroup> {
        return groups.values.filter { it.parentId == parentId }
    }
}
