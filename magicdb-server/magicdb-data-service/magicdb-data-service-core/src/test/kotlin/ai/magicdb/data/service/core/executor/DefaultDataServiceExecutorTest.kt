package ai.magicdb.data.service.core.executor

import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceParameter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.time.LocalDateTime
import java.util.UUID

class DefaultDataServiceExecutorTest {
    
    private lateinit var dataServiceManager: DataServiceManager
    private lateinit var executor: DefaultDataServiceExecutor
    
    @BeforeEach
    fun setUp() {
        dataServiceManager = mock(DataServiceManager::class.java)
        executor = DefaultDataServiceExecutor(dataServiceManager)
    }
    
    @Test
    fun `executeScript should execute JavaScript successfully`() {
        // Arrange
        val script = """
            function execute(params) {
                return {
                    message: "Hello, " + (params.name || "World") + "!"
                };
            }
        """.trimIndent()
        
        val parameters = mapOf("name" to "Test")
        
        // Act
        val result = executor.executeScript(script, "js", parameters)
        
        // Assert
        assertTrue(result.success)
        assertNotNull(result.data)
        val data = result.data as Map<*, *>
        assertEquals("Hello, Test!", data["message"])
    }
    
    @Test
    fun `executeScript should handle errors`() {
        // Arrange
        val script = """
            function execute(params) {
                throw new Error("Test error");
            }
        """.trimIndent()
        
        val parameters = mapOf<String, Any>()
        
        // Act
        val result = executor.executeScript(script, "js", parameters)
        
        // Assert
        assertFalse(result.success)
        assertNotNull(result.errorMessage)
        assertTrue(result.errorMessage!!.contains("Test error"))
    }
    
    @Test
    fun `validateParameters should validate required parameters`() {
        // Arrange
        val service = createSampleService()
        val parameters = mapOf<String, Any>()
        
        // Act
        val errors = executor.validateParameters(service, parameters)
        
        // Assert
        assertEquals(1, errors.size)
        assertTrue(errors.containsKey("requiredParam"))
        assertEquals("Required parameter is missing", errors["requiredParam"])
    }
    
    @Test
    fun `validateParameters should validate parameter types`() {
        // Arrange
        val service = createSampleService()
        val parameters = mapOf(
            "requiredParam" to 123,
            "stringParam" to 456
        )
        
        // Act
        val errors = executor.validateParameters(service, parameters)
        
        // Assert
        assertEquals(1, errors.size)
        assertTrue(errors.containsKey("stringParam"))
        assertEquals("Expected string, got Integer", errors["stringParam"])
    }
    
    @Test
    fun `executeById should execute service by ID`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val parameters = mapOf(
            "requiredParam" to "value",
            "stringParam" to "string value"
        )
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        
        // Act
        val result = executor.executeById(serviceId, parameters)
        
        // Assert
        assertTrue(result.success)
        assertNotNull(result.data)
        val data = result.data as Map<*, *>
        assertEquals("Hello, value!", data["message"])
    }
    
    private fun createSampleService(id: String = UUID.randomUUID().toString()): DataService {
        return DataService(
            id = id,
            name = "Test Service",
            description = "Test Description",
            type = "query",
            script = """
                function execute(params) {
                    return {
                        message: "Hello, " + params.requiredParam + "!"
                    };
                }
            """.trimIndent(),
            language = "js",
            gmtCreate = LocalDateTime.now(),
            gmtModified = LocalDateTime.now(),
            parameters = listOf(
                ServiceParameter(
                    serviceId = id,
                    name = "requiredParam",
                    type = "string",
                    description = "Required Parameter",
                    required = true
                ),
                ServiceParameter(
                    serviceId = id,
                    name = "stringParam",
                    type = "string",
                    description = "String Parameter",
                    required = false
                )
            )
        )
    }
}
