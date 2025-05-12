package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceResult

/**
 * Interface for executing data services
 */
interface DataServiceExecutor {
    /**
     * Execute a data service with the given parameters
     *
     * @param service The data service to execute
     * @param parameters The parameters to pass to the service
     * @return The result of the service execution
     */
    fun execute(service: DataService, parameters: Map<String, Any?>): ServiceResult
    
    /**
     * Execute a data service by ID with the given parameters
     *
     * @param serviceId The ID of the data service to execute
     * @param parameters The parameters to pass to the service
     * @return The result of the service execution
     */
    fun executeById(serviceId: String, parameters: Map<String, Any?>): ServiceResult
    
    /**
     * Validate parameters against a data service's parameter definitions
     *
     * @param service The data service to validate parameters against
     * @param parameters The parameters to validate
     * @return A map of parameter name to error message, empty if all parameters are valid
     */
    fun validateParameters(service: DataService, parameters: Map<String, Any?>): Map<String, String>
    
    /**
     * Execute a script directly with the given parameters
     *
     * @param script The script to execute
     * @param language The language of the script
     * @param parameters The parameters to pass to the script
     * @return The result of the script execution
     */
    fun executeScript(script: String, language: String, parameters: Map<String, Any?>): ServiceResult
}
