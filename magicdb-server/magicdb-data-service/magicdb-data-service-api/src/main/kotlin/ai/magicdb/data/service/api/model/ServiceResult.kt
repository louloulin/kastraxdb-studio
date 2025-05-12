package ai.magicdb.data.service.api.model

/**
 * Service result model for representing the result of a data service execution
 */
data class ServiceResult(
    /**
     * Whether the execution was successful
     */
    val success: Boolean,
    
    /**
     * Result data (can be any type)
     */
    val data: Any? = null,
    
    /**
     * Error message if execution failed
     */
    val errorMessage: String? = null,
    
    /**
     * Execution time in milliseconds
     */
    val executionTime: Long = 0,
    
    /**
     * Additional metadata for this result
     */
    val metadata: Map<String, Any> = emptyMap()
)
