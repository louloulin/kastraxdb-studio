package ai.magicdb.data.service.api.model

import java.time.LocalDateTime

/**
 * Data service model representing a script-based service
 */
data class DataService(
    /**
     * Unique identifier for the service
     */
    val id: String,
    
    /**
     * Name of the service
     */
    val name: String,
    
    /**
     * Description of the service
     */
    val description: String? = null,
    
    /**
     * Type of the service (e.g., query, mutation)
     */
    val type: String,
    
    /**
     * ID of the data source used by this service
     */
    val dataSourceId: Long? = null,
    
    /**
     * Database name
     */
    val databaseName: String? = null,
    
    /**
     * Schema name
     */
    val schemaName: String? = null,
    
    /**
     * Table name
     */
    val tableName: String? = null,
    
    /**
     * Script content
     */
    val script: String? = null,
    
    /**
     * Script language (e.g., js, kotlin, python)
     */
    val language: String,
    
    /**
     * Group ID this service belongs to
     */
    val groupId: String? = null,
    
    /**
     * Creation timestamp
     */
    val gmtCreate: LocalDateTime,
    
    /**
     * Last modification timestamp
     */
    val gmtModified: LocalDateTime,
    
    /**
     * Whether this service is enabled
     */
    val enabled: Boolean = true,
    
    /**
     * Execution timeout in milliseconds
     */
    val timeout: Long = 30000,
    
    /**
     * Cache time in milliseconds (0 means no cache)
     */
    val cacheTime: Long = 0,
    
    /**
     * ID of the user who created this service
     */
    val createUserId: Long? = null,
    
    /**
     * ID of the user who last modified this service
     */
    val modifiedUserId: Long? = null,
    
    /**
     * Tags for this service
     */
    val tags: List<String> = emptyList(),
    
    /**
     * Additional metadata for this service
     */
    val metadata: Map<String, Any> = emptyMap(),
    
    /**
     * Parameters for this service
     */
    val parameters: List<ServiceParameter> = emptyList()
)
