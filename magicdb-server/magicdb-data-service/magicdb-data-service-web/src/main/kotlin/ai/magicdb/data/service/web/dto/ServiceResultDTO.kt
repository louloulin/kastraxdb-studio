package ai.magicdb.data.service.web.dto

/**
 * DTO for ServiceResult
 */
data class ServiceResultDTO(
    val success: Boolean,
    val data: Any? = null,
    val errorMessage: String? = null,
    val executionTime: Long = 0,
    val metadata: Map<String, Any> = emptyMap()
)
