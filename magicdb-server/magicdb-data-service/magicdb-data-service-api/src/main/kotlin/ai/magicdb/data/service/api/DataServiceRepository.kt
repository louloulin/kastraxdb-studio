package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceGroup
import ai.magicdb.data.service.api.model.ServiceParameter

/**
 * Interface for storing and retrieving data services
 */
interface DataServiceRepository {
    /**
     * Save a data service
     *
     * @param dataService The data service to save
     * @return The saved data service
     */
    fun saveService(dataService: DataService): DataService
    
    /**
     * Delete a data service by ID
     *
     * @param id The ID of the data service to delete
     * @return True if the service was deleted, false otherwise
     */
    fun deleteService(id: String): Boolean
    
    /**
     * Get a data service by ID
     *
     * @param id The ID of the data service to get
     * @return The data service, or null if not found
     */
    fun getService(id: String): DataService?
    
    /**
     * Get all data services
     *
     * @return A list of all data services
     */
    fun getAllServices(): List<DataService>
    
    /**
     * Get data services by group ID
     *
     * @param groupId The ID of the group to get services for
     * @return A list of data services in the specified group
     */
    fun getServicesByGroup(groupId: String): List<DataService>
    
    /**
     * Save a service parameter
     *
     * @param parameter The service parameter to save
     * @return The saved service parameter
     */
    fun saveParameter(parameter: ServiceParameter): ServiceParameter
    
    /**
     * Delete a service parameter by ID
     *
     * @param id The ID of the service parameter to delete
     * @return True if the parameter was deleted, false otherwise
     */
    fun deleteParameter(id: Long): Boolean
    
    /**
     * Get service parameters by service ID
     *
     * @param serviceId The ID of the service to get parameters for
     * @return A list of service parameters for the specified service
     */
    fun getParametersByService(serviceId: String): List<ServiceParameter>
    
    /**
     * Save a service group
     *
     * @param serviceGroup The service group to save
     * @return The saved service group
     */
    fun saveGroup(serviceGroup: ServiceGroup): ServiceGroup
    
    /**
     * Delete a service group by ID
     *
     * @param id The ID of the service group to delete
     * @return True if the group was deleted, false otherwise
     */
    fun deleteGroup(id: String): Boolean
    
    /**
     * Get a service group by ID
     *
     * @param id The ID of the service group to get
     * @return The service group, or null if not found
     */
    fun getGroup(id: String): ServiceGroup?
    
    /**
     * Get all service groups
     *
     * @return A list of all service groups
     */
    fun getAllGroups(): List<ServiceGroup>
    
    /**
     * Get service groups by parent ID
     *
     * @param parentId The ID of the parent group to get child groups for
     * @return A list of service groups with the specified parent
     */
    fun getGroupsByParent(parentId: String?): List<ServiceGroup>
}
