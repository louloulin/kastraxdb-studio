package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceGroup
import ai.magicdb.data.service.web.dto.DataServiceDTO
import ai.magicdb.data.service.web.dto.ServiceGroupDTO
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import ai.magicdb.server.tools.common.util.ContextUtils
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID

/**
 * Controller for managing data services
 */
@RestController
@RequestMapping("/api/data-service")
class DataServiceController(
    private val dataServiceManager: DataServiceManager
) {
    
    /**
     * Get all data services
     */
    @GetMapping
    fun getAllServices(): ListResult<DataServiceDTO> {
        val services = dataServiceManager.getAllServices()
        return ListResult.of(services.map { it.toDTO() })
    }
    
    /**
     * Get a data service by ID
     */
    @GetMapping("/{id}")
    fun getService(@PathVariable id: String): DataResult<DataServiceDTO> {
        val service = dataServiceManager.getService(id)
            ?: return DataResult.failed("Service not found")
        
        return DataResult.of(service.toDTO())
    }
    
    /**
     * Create a new data service
     */
    @PostMapping
    fun createService(@RequestBody dto: DataServiceDTO): DataResult<DataServiceDTO> {
        val userId = ContextUtils.getUserId()
        val now = LocalDateTime.now()
        
        val service = DataService(
            id = dto.id ?: UUID.randomUUID().toString(),
            name = dto.name,
            description = dto.description,
            type = dto.type,
            dataSourceId = dto.dataSourceId,
            databaseName = dto.databaseName,
            schemaName = dto.schemaName,
            tableName = dto.tableName,
            script = dto.script,
            language = dto.language,
            groupId = dto.groupId,
            gmtCreate = now,
            gmtModified = now,
            enabled = dto.enabled ?: true,
            timeout = dto.timeout ?: 30000,
            cacheTime = dto.cacheTime ?: 0,
            createUserId = userId,
            modifiedUserId = userId,
            tags = dto.tags ?: emptyList(),
            metadata = dto.metadata ?: emptyMap(),
            parameters = dto.parameters?.map { it.toModel(dto.id ?: UUID.randomUUID().toString()) } ?: emptyList()
        )
        
        val createdService = dataServiceManager.createService(service)
        return DataResult.of(createdService.toDTO())
    }
    
    /**
     * Update an existing data service
     */
    @PutMapping("/{id}")
    fun updateService(@PathVariable id: String, @RequestBody dto: DataServiceDTO): DataResult<DataServiceDTO> {
        val existingService = dataServiceManager.getService(id)
            ?: return DataResult.failed("Service not found")
        
        val userId = ContextUtils.getUserId()
        
        val service = DataService(
            id = id,
            name = dto.name,
            description = dto.description,
            type = dto.type,
            dataSourceId = dto.dataSourceId,
            databaseName = dto.databaseName,
            schemaName = dto.schemaName,
            tableName = dto.tableName,
            script = dto.script,
            language = dto.language,
            groupId = dto.groupId,
            gmtCreate = existingService.gmtCreate,
            gmtModified = LocalDateTime.now(),
            enabled = dto.enabled ?: existingService.enabled,
            timeout = dto.timeout ?: existingService.timeout,
            cacheTime = dto.cacheTime ?: existingService.cacheTime,
            createUserId = existingService.createUserId,
            modifiedUserId = userId,
            tags = dto.tags ?: existingService.tags,
            metadata = dto.metadata ?: existingService.metadata,
            parameters = dto.parameters?.map { it.toModel(id) } ?: existingService.parameters
        )
        
        val updatedService = dataServiceManager.updateService(service)
        return DataResult.of(updatedService.toDTO())
    }
    
    /**
     * Delete a data service
     */
    @DeleteMapping("/{id}")
    fun deleteService(@PathVariable id: String): ActionResult {
        val deleted = dataServiceManager.deleteService(id)
        return if (deleted) ActionResult.isSuccess() else ActionResult.isFailed("Service not found")
    }
    
    /**
     * Get all service groups
     */
    @GetMapping("/group")
    fun getAllGroups(): ListResult<ServiceGroupDTO> {
        val groups = dataServiceManager.getAllGroups()
        return ListResult.of(groups.map { it.toDTO() })
    }
    
    /**
     * Get a service group by ID
     */
    @GetMapping("/group/{id}")
    fun getGroup(@PathVariable id: String): DataResult<ServiceGroupDTO> {
        val group = dataServiceManager.getGroup(id)
            ?: return DataResult.failed("Group not found")
        
        return DataResult.of(group.toDTO())
    }
    
    /**
     * Create a new service group
     */
    @PostMapping("/group")
    fun createGroup(@RequestBody dto: ServiceGroupDTO): DataResult<ServiceGroupDTO> {
        val userId = ContextUtils.getUserId()
        val now = LocalDateTime.now()
        
        val group = ServiceGroup(
            id = dto.id ?: UUID.randomUUID().toString(),
            name = dto.name,
            description = dto.description,
            parentId = dto.parentId,
            orderNum = dto.orderNum ?: 0,
            gmtCreate = now,
            gmtModified = now,
            createUserId = userId,
            modifiedUserId = userId,
            metadata = dto.metadata ?: emptyMap()
        )
        
        val createdGroup = dataServiceManager.createGroup(group)
        return DataResult.of(createdGroup.toDTO())
    }
    
    /**
     * Update an existing service group
     */
    @PutMapping("/group/{id}")
    fun updateGroup(@PathVariable id: String, @RequestBody dto: ServiceGroupDTO): DataResult<ServiceGroupDTO> {
        val existingGroup = dataServiceManager.getGroup(id)
            ?: return DataResult.failed("Group not found")
        
        val userId = ContextUtils.getUserId()
        
        val group = ServiceGroup(
            id = id,
            name = dto.name,
            description = dto.description,
            parentId = dto.parentId,
            orderNum = dto.orderNum ?: existingGroup.orderNum,
            gmtCreate = existingGroup.gmtCreate,
            gmtModified = LocalDateTime.now(),
            createUserId = existingGroup.createUserId,
            modifiedUserId = userId,
            metadata = dto.metadata ?: existingGroup.metadata
        )
        
        val updatedGroup = dataServiceManager.updateGroup(group)
        return DataResult.of(updatedGroup.toDTO())
    }
    
    /**
     * Delete a service group
     */
    @DeleteMapping("/group/{id}")
    fun deleteGroup(@PathVariable id: String): ActionResult {
        val deleted = dataServiceManager.deleteGroup(id)
        return if (deleted) ActionResult.isSuccess() else ActionResult.isFailed("Group not found")
    }
    
    /**
     * Get services by group ID
     */
    @GetMapping("/group/{id}/services")
    fun getServicesByGroup(@PathVariable id: String): ListResult<DataServiceDTO> {
        val services = dataServiceManager.getServicesByGroup(id)
        return ListResult.of(services.map { it.toDTO() })
    }
    
    /**
     * Get groups by parent ID
     */
    @GetMapping("/group/parent/{parentId}")
    fun getGroupsByParent(@PathVariable parentId: String): ListResult<ServiceGroupDTO> {
        val groups = dataServiceManager.getGroupsByParent(parentId)
        return ListResult.of(groups.map { it.toDTO() })
    }
    
    /**
     * Get root groups
     */
    @GetMapping("/group/root")
    fun getRootGroups(): ListResult<ServiceGroupDTO> {
        val groups = dataServiceManager.getGroupsByParent(null)
        return ListResult.of(groups.map { it.toDTO() })
    }
    
    /**
     * Convert DataService to DTO
     */
    private fun DataService.toDTO(): DataServiceDTO {
        return DataServiceDTO(
            id = id,
            name = name,
            description = description,
            type = type,
            dataSourceId = dataSourceId,
            databaseName = databaseName,
            schemaName = schemaName,
            tableName = tableName,
            script = script,
            language = language,
            groupId = groupId,
            gmtCreate = gmtCreate,
            gmtModified = gmtModified,
            enabled = enabled,
            timeout = timeout,
            cacheTime = cacheTime,
            createUserId = createUserId,
            modifiedUserId = modifiedUserId,
            tags = tags,
            metadata = metadata,
            parameters = parameters.map { it.toDTO() }
        )
    }
    
    /**
     * Convert ServiceGroup to DTO
     */
    private fun ServiceGroup.toDTO(): ServiceGroupDTO {
        return ServiceGroupDTO(
            id = id,
            name = name,
            description = description,
            parentId = parentId,
            orderNum = orderNum,
            gmtCreate = gmtCreate,
            gmtModified = gmtModified,
            createUserId = createUserId,
            modifiedUserId = modifiedUserId,
            metadata = metadata
        )
    }
}
