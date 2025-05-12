package ai.magicdb.data.service.core.repository

import ai.magicdb.data.service.api.DataServiceRepository
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceGroup
import ai.magicdb.data.service.api.model.ServiceParameter
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * In-memory implementation of DataServiceRepository
 */
@Repository
class MemoryDataServiceRepository : DataServiceRepository {
    
    private val services = ConcurrentHashMap<String, DataService>()
    private val groups = ConcurrentHashMap<String, ServiceGroup>()
    private val parameters = ConcurrentHashMap<Long, ServiceParameter>()
    private val parameterIdGenerator = AtomicLong(1)
    
    override fun saveService(dataService: DataService): DataService {
        services[dataService.id] = dataService
        
        // Save parameters
        val existingParameters = getParametersByService(dataService.id)
        existingParameters.forEach { deleteParameter(it.id!!) }
        
        dataService.parameters.forEach { saveParameter(it) }
        
        return dataService
    }
    
    override fun deleteService(id: String): Boolean {
        val removed = services.remove(id) != null
        
        if (removed) {
            // Remove associated parameters
            val parametersToRemove = parameters.values.filter { it.serviceId == id }
            parametersToRemove.forEach { deleteParameter(it.id!!) }
        }
        
        return removed
    }
    
    override fun getService(id: String): DataService? {
        val service = services[id] ?: return null
        val serviceParameters = getParametersByService(id)
        
        return service.copy(parameters = serviceParameters)
    }
    
    override fun getAllServices(): List<DataService> {
        return services.values.map { service ->
            val serviceParameters = getParametersByService(service.id)
            service.copy(parameters = serviceParameters)
        }.toList()
    }
    
    override fun getServicesByGroup(groupId: String): List<DataService> {
        return services.values
            .filter { it.groupId == groupId }
            .map { service ->
                val serviceParameters = getParametersByService(service.id)
                service.copy(parameters = serviceParameters)
            }
            .toList()
    }
    
    override fun saveParameter(parameter: ServiceParameter): ServiceParameter {
        val id = parameter.id ?: parameterIdGenerator.getAndIncrement()
        val parameterToSave = parameter.copy(id = id)
        parameters[id] = parameterToSave
        return parameterToSave
    }
    
    override fun deleteParameter(id: Long): Boolean {
        return parameters.remove(id) != null
    }
    
    override fun getParametersByService(serviceId: String): List<ServiceParameter> {
        return parameters.values
            .filter { it.serviceId == serviceId }
            .sortedBy { it.orderNum }
            .toList()
    }
    
    override fun saveGroup(serviceGroup: ServiceGroup): ServiceGroup {
        groups[serviceGroup.id] = serviceGroup
        return serviceGroup
    }
    
    override fun deleteGroup(id: String): Boolean {
        return groups.remove(id) != null
    }
    
    override fun getGroup(id: String): ServiceGroup? {
        return groups[id]
    }
    
    override fun getAllGroups(): List<ServiceGroup> {
        return groups.values.toList()
    }
    
    override fun getGroupsByParent(parentId: String?): List<ServiceGroup> {
        return groups.values
            .filter { it.parentId == parentId }
            .sortedBy { it.orderNum }
            .toList()
    }
}
