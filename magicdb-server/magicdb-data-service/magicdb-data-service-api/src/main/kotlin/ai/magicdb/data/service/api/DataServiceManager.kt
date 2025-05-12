package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceGroup

/**
 * Interface for managing data services
 */
interface DataServiceManager {
    /**
     * Create a new data service
     *
     * @param dataService The data service to create
     * @return The created data service
     */
    fun createService(dataService: DataService): DataService
    
    /**
     * Update an existing data service
     *
     * @param dataService The data service to update
     * @return The updated data service
     */
    fun updateService(dataService: DataService): DataService
    
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
     * Create a new service group
     *
     * @param serviceGroup The service group to create
     * @return The created service group
     */
    fun createGroup(serviceGroup: ServiceGroup): ServiceGroup
    
    /**
     * Update an existing service group
     *
     * @param serviceGroup The service group to update
     * @return The updated service group
     */
    fun updateGroup(serviceGroup: ServiceGroup): ServiceGroup
    
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
