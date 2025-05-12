package ai.magicdb.data.service.api.model

import java.time.LocalDateTime

/**
 * Service group model for organizing data services
 */
data class ServiceGroup(
    /**
     * Unique identifier for the group
     */
    val id: String,
    
    /**
     * Name of the group
     */
    val name: String,
    
    /**
     * Description of the group
     */
    val description: String? = null,
    
    /**
     * Parent group ID for hierarchical grouping
     */
    val parentId: String? = null,
    
    /**
     * Order number for sorting
     */
    val orderNum: Int = 0,
    
    /**
     * Creation timestamp
     */
    val gmtCreate: LocalDateTime,
    
    /**
     * Last modification timestamp
     */
    val gmtModified: LocalDateTime,
    
    /**
     * ID of the user who created this group
     */
    val createUserId: Long? = null,
    
    /**
     * ID of the user who last modified this group
     */
    val modifiedUserId: Long? = null,
    
    /**
     * Additional metadata for this group
     */
    val metadata: Map<String, Any> = emptyMap()
)
