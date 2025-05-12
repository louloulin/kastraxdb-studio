package ai.magicdb.data.service.api.model

import java.time.LocalDateTime

/**
 * Service parameter model for defining parameters of a data service
 */
data class ServiceParameter(
    /**
     * Unique identifier for the parameter
     */
    val id: Long? = null,
    
    /**
     * ID of the service this parameter belongs to
     */
    val serviceId: String,
    
    /**
     * Name of the parameter
     */
    val name: String,
    
    /**
     * Type of the parameter (e.g., string, number, boolean, object, array)
     */
    val type: String,
    
    /**
     * Description of the parameter
     */
    val description: String? = null,
    
    /**
     * Default value for the parameter
     */
    val defaultValue: String? = null,
    
    /**
     * Whether this parameter is required
     */
    val required: Boolean = false,
    
    /**
     * Order number for sorting
     */
    val orderNum: Int = 0,
    
    /**
     * Creation timestamp
     */
    val gmtCreate: LocalDateTime? = null,
    
    /**
     * Last modification timestamp
     */
    val gmtModified: LocalDateTime? = null
)
