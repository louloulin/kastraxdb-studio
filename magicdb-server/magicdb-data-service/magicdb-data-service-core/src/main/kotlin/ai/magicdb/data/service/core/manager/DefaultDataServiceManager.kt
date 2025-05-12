package ai.magicdb.data.service.core.manager

import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.DataServiceRepository
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceGroup
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

/**
 * Default implementation of DataServiceManager
 */
@Service
class DefaultDataServiceManager(
    private val repository: DataServiceRepository
) : DataServiceManager {
    
    override fun createService(dataService: DataService): DataService {
        val now = LocalDateTime.now()
        val id = dataService.id.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        
        val serviceToSave = dataService.copy(
            id = id,
            gmtCreate = now,
            gmtModified = now
        )
        
        return repository.saveService(serviceToSave)
    }
    
    override fun updateService(dataService: DataService): DataService {
        val existingService = repository.getService(dataService.id)
            ?: throw IllegalArgumentException("Service with ID ${dataService.id} not found")
        
        val serviceToSave = dataService.copy(
            gmtCreate = existingService.gmtCreate,
            gmtModified = LocalDateTime.now()
        )
        
        return repository.saveService(serviceToSave)
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
    
    override fun createGroup(serviceGroup: ServiceGroup): ServiceGroup {
        val now = LocalDateTime.now()
        val id = serviceGroup.id.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        
        val groupToSave = serviceGroup.copy(
            id = id,
            gmtCreate = now,
            gmtModified = now
        )
        
        return repository.saveGroup(groupToSave)
    }
    
    override fun updateGroup(serviceGroup: ServiceGroup): ServiceGroup {
        val existingGroup = repository.getGroup(serviceGroup.id)
            ?: throw IllegalArgumentException("Group with ID ${serviceGroup.id} not found")
        
        val groupToSave = serviceGroup.copy(
            gmtCreate = existingGroup.gmtCreate,
            gmtModified = LocalDateTime.now()
        )
        
        return repository.saveGroup(groupToSave)
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
    
    override fun getGroupsByParent(parentId: String?): List<ServiceGroup> {
        return repository.getGroupsByParent(parentId)
    }
}
