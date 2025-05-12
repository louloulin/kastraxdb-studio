package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.DataServiceExecutor
import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.model.ServiceResult
import ai.magicdb.data.service.web.dto.ServiceResultDTO
import ai.magicdb.data.service.web.util.DataResultExtensions
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.*

/**
 * Controller for executing data services
 */
@RestController
@RequestMapping("/api/data-service")
class DataServiceRunController(
    private val dataServiceManager: DataServiceManager,
    private val dataServiceExecutor: DataServiceExecutor
) {

    /**
     * Execute a data service by ID
     */
    @PostMapping("/{id}/execute")
    fun executeService(
        @PathVariable id: String,
        @RequestBody parameters: Map<String, Any?>
    ): DataResult<ServiceResultDTO> {
        val service = dataServiceManager.getService(id)
            ?: return DataResultExtensions.failed("Service not found")

        val result = dataServiceExecutor.execute(service, parameters)
        return DataResult.of(result.toDTO())
    }

    /**
     * Execute a script directly
     */
    @PostMapping("/execute-script")
    fun executeScript(
        @RequestParam language: String,
        @RequestBody script: String,
        @RequestParam(required = false) parameters: Map<String, Any?>?
    ): DataResult<ServiceResultDTO> {
        val result = dataServiceExecutor.executeScript(script, language, parameters ?: emptyMap())
        return DataResult.of(result.toDTO())
    }

    /**
     * Validate parameters for a service
     */
    @PostMapping("/{id}/validate")
    fun validateParameters(
        @PathVariable id: String,
        @RequestBody parameters: Map<String, Any?>
    ): DataResult<Map<String, String>> {
        val service = dataServiceManager.getService(id)
            ?: return DataResult.of(null, "Service not found", false)

        val errors = dataServiceExecutor.validateParameters(service, parameters)
        return DataResult.of(errors)
    }

    /**
     * Convert ServiceResult to DTO
     */
    private fun ServiceResult.toDTO(): ServiceResultDTO {
        return ServiceResultDTO(
            success = success,
            data = data,
            errorMessage = errorMessage,
            executionTime = executionTime,
            metadata = metadata
        )
    }
}
