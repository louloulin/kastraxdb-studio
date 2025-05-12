package ai.magicdb.data.service.core.executor

import ai.magicdb.data.service.api.DataServiceExecutor
import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceResult
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.Value
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

/**
 * Default implementation of DataServiceExecutor using GraalVM
 */
@Service
class DefaultDataServiceExecutor(
    private val dataServiceManager: DataServiceManager
) : DataServiceExecutor {
    
    private val logger = LoggerFactory.getLogger(DefaultDataServiceExecutor::class.java)
    
    // Cache for compiled scripts
    private val scriptCache = ConcurrentHashMap<String, Source>()
    
    override fun execute(service: DataService, parameters: Map<String, Any?>): ServiceResult {
        val startTime = System.currentTimeMillis()
        
        try {
            // Validate parameters
            val validationErrors = validateParameters(service, parameters)
            if (validationErrors.isNotEmpty()) {
                return ServiceResult(
                    success = false,
                    errorMessage = "Parameter validation failed: ${validationErrors.entries.joinToString { "${it.key}: ${it.value}" }}",
                    executionTime = System.currentTimeMillis() - startTime
                )
            }
            
            // Execute script
            val script = service.script ?: return ServiceResult(
                success = false,
                errorMessage = "Service script is empty",
                executionTime = System.currentTimeMillis() - startTime
            )
            
            val result = executeScript(script, service.language, parameters)
            
            return result.copy(
                executionTime = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            logger.error("Error executing service ${service.id}", e)
            return ServiceResult(
                success = false,
                errorMessage = e.message ?: "Unknown error",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }
    
    @Cacheable("dataServiceExecution", key = "#serviceId")
    override fun executeById(serviceId: String, parameters: Map<String, Any?>): ServiceResult {
        val service = dataServiceManager.getService(serviceId) ?: return ServiceResult(
            success = false,
            errorMessage = "Service not found: $serviceId"
        )
        
        return execute(service, parameters)
    }
    
    override fun validateParameters(service: DataService, parameters: Map<String, Any?>): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        
        // Check required parameters
        service.parameters.filter { it.required }.forEach { param ->
            if (!parameters.containsKey(param.name) || parameters[param.name] == null) {
                errors[param.name] = "Required parameter is missing"
            }
        }
        
        // Check parameter types
        service.parameters.forEach { param ->
            if (parameters.containsKey(param.name) && parameters[param.name] != null) {
                val value = parameters[param.name]
                when (param.type.lowercase()) {
                    "string" -> if (value !is String) errors[param.name] = "Expected string, got ${value?.javaClass?.simpleName}"
                    "number" -> if (value !is Number) errors[param.name] = "Expected number, got ${value?.javaClass?.simpleName}"
                    "boolean" -> if (value !is Boolean) errors[param.name] = "Expected boolean, got ${value?.javaClass?.simpleName}"
                    "object" -> if (value !is Map<*, *>) errors[param.name] = "Expected object, got ${value?.javaClass?.simpleName}"
                    "array" -> if (value !is List<*>) errors[param.name] = "Expected array, got ${value?.javaClass?.simpleName}"
                }
            }
        }
        
        return errors
    }
    
    override fun executeScript(script: String, language: String, parameters: Map<String, Any?>): ServiceResult {
        val languageId = mapLanguageId(language)
        
        try {
            // Create GraalVM context
            val context = Context.newBuilder()
                .allowAllAccess(true)
                .build()
            
            // Prepare parameters
            val bindings = context.getBindings(languageId)
            bindings.putMember("params", parameters)
            
            // Get or compile script
            val source = scriptCache.computeIfAbsent("$language:$script") {
                Source.create(languageId, script)
            }
            
            // Execute script
            val result = context.eval(source)
            
            // Convert result to Java object
            val data = convertValue(result)
            
            context.close()
            
            return ServiceResult(
                success = true,
                data = data
            )
        } catch (e: Exception) {
            logger.error("Error executing script", e)
            return ServiceResult(
                success = false,
                errorMessage = e.message ?: "Unknown error"
            )
        }
    }
    
    private fun mapLanguageId(language: String): String {
        return when (language.lowercase()) {
            "js", "javascript" -> "js"
            "py", "python" -> "python"
            "kt", "kotlin" -> "kotlin"
            else -> language.lowercase()
        }
    }
    
    private fun convertValue(value: Value): Any? {
        if (value.isNull) {
            return null
        }
        
        if (value.isString) {
            return value.asString()
        }
        
        if (value.isNumber) {
            return if (value.fitsInInt()) {
                value.asInt()
            } else if (value.fitsInLong()) {
                value.asLong()
            } else {
                value.asDouble()
            }
        }
        
        if (value.isBoolean) {
            return value.asBoolean()
        }
        
        if (value.hasArrayElements()) {
            val list = mutableListOf<Any?>()
            val size = value.arraySize
            for (i in 0 until size) {
                list.add(convertValue(value.getArrayElement(i)))
            }
            return list
        }
        
        if (value.hasMembers()) {
            val map = mutableMapOf<String, Any?>()
            for (key in value.memberKeys) {
                map[key] = convertValue(value.getMember(key))
            }
            return map
        }
        
        // Fallback: convert to string
        return value.toString()
    }
}
