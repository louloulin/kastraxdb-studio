package ai.magicdb.data.service.web.dto

import ai.magicdb.data.service.api.model.ServiceParameter
import java.time.LocalDateTime

/**
 * DTO for DataService
 */
data class DataServiceDTO(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val type: String,
    val dataSourceId: Long? = null,
    val databaseName: String? = null,
    val schemaName: String? = null,
    val tableName: String? = null,
    val script: String? = null,
    val language: String,
    val groupId: String? = null,
    val gmtCreate: LocalDateTime? = null,
    val gmtModified: LocalDateTime? = null,
    val enabled: Boolean? = null,
    val timeout: Long? = null,
    val cacheTime: Long? = null,
    val createUserId: Long? = null,
    val modifiedUserId: Long? = null,
    val tags: List<String>? = null,
    val metadata: Map<String, Any>? = null,
    val parameters: List<ServiceParameterDTO>? = null
)

/**
 * DTO for ServiceParameter
 */
data class ServiceParameterDTO(
    val id: Long? = null,
    val name: String,
    val type: String,
    val description: String? = null,
    val defaultValue: String? = null,
    val required: Boolean = false,
    val orderNum: Int = 0
) {
    /**
     * Convert DTO to model
     */
    fun toModel(serviceId: String): ServiceParameter {
        return ServiceParameter(
            id = id,
            serviceId = serviceId,
            name = name,
            type = type,
            description = description,
            defaultValue = defaultValue,
            required = required,
            orderNum = orderNum
        )
    }
}

/**
 * Extension function to convert ServiceParameter to DTO
 */
fun ServiceParameter.toDTO(): ServiceParameterDTO {
    return ServiceParameterDTO(
        id = id,
        name = name,
        type = type,
        description = description,
        defaultValue = defaultValue,
        required = required,
        orderNum = orderNum
    )
}
