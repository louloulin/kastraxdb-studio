package ai.magicdb.data.service.core.debug

import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.model.BreakpointInfo
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.DebugSession
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.Date

class DefaultScriptDebugServiceTest {
    
    private lateinit var dataServiceManager: DataServiceManager
    private lateinit var scriptDebugService: DefaultScriptDebugService
    
    @BeforeEach
    fun setUp() {
        dataServiceManager = mock(DataServiceManager::class.java)
        scriptDebugService = DefaultScriptDebugService(dataServiceManager)
    }
    
    @Test
    fun `createDebugSession should create a debug session for JavaScript`() {
        // Arrange
        val serviceId = "test-service"
        val script = """
            function execute(params) {
                return { message: "Hello, " + params.name };
            }
        """.trimIndent()
        
        val service = DataService(
            id = serviceId,
            name = "Test Service",
            script = script,
            language = "javascript",
            createTime = Date(),
            updateTime = Date()
        )
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        
        // Act
        val session = scriptDebugService.createDebugSession(serviceId, mapOf("name" to "World"))
        
        // Assert
        assertNotNull(session)
        assertEquals(serviceId, session.serviceId)
        assertEquals(DebugSession.Status.CREATED, session.status)
    }
    
    @Test
    fun `addBreakpoint should add a breakpoint to the session`() {
        // Arrange
        val serviceId = "test-service"
        val script = """
            function execute(params) {
                return { message: "Hello, " + params.name };
            }
        """.trimIndent()
        
        val service = DataService(
            id = serviceId,
            name = "Test Service",
            script = script,
            language = "javascript",
            createTime = Date(),
            updateTime = Date()
        )
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        
        val session = scriptDebugService.createDebugSession(serviceId, mapOf("name" to "World"))
        
        // Act
        val breakpoint = BreakpointInfo(line = 2, column = 5)
        val result = scriptDebugService.addBreakpoint(session.id, breakpoint)
        
        // Assert
        assertTrue(result)
        
        // Verify breakpoint was added
        val breakpoints = scriptDebugService.getBreakpoints(session.id)
        assertEquals(1, breakpoints.size)
        assertEquals(2, breakpoints[0].line)
        assertEquals(5, breakpoints[0].column)
    }
    
    @Test
    fun `evaluateExpression should evaluate JavaScript expression`() {
        // Arrange
        val serviceId = "test-service"
        val script = """
            function execute(params) {
                return { message: "Hello, " + params.name };
            }
        """.trimIndent()
        
        val service = DataService(
            id = serviceId,
            name = "Test Service",
            script = script,
            language = "javascript",
            createTime = Date(),
            updateTime = Date()
        )
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        
        val session = scriptDebugService.createDebugSession(serviceId, mapOf("name" to "World"))
        
        // Act
        val result = scriptDebugService.evaluateExpression(session.id, "1 + 2")
        
        // Assert
        assertEquals(3, result)
    }
    
    @Test
    fun `getVariables should return session variables`() {
        // Arrange
        val serviceId = "test-service"
        val script = """
            function execute(params) {
                return { message: "Hello, " + params.name };
            }
        """.trimIndent()
        
        val service = DataService(
            id = serviceId,
            name = "Test Service",
            script = script,
            language = "javascript",
            createTime = Date(),
            updateTime = Date()
        )
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        
        val session = scriptDebugService.createDebugSession(serviceId, mapOf("name" to "World"))
        
        // Act
        val variables = scriptDebugService.getVariables(session.id)
        
        // Assert
        assertTrue(variables.isNotEmpty())
        assertTrue(variables.any { it.name == "params" })
    }
}
