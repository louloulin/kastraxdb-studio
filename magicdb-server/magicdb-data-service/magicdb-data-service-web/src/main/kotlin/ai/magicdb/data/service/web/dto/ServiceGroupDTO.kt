package ai.magicdb.data.service.web.dto

import java.time.LocalDateTime

/**
 * DTO for ServiceGroup
 */
data class ServiceGroupDTO(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val parentId: String? = null,
    val orderNum: Int? = null,
    val gmtCreate: LocalDateTime? = null,
    val gmtModified: LocalDateTime? = null,
    val createUserId: Long? = null,
    val modifiedUserId: Long? = null,
    val metadata: Map<String, Any>? = null
)
