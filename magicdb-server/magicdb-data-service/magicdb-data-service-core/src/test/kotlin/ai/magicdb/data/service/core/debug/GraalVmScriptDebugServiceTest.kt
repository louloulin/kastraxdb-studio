package ai.magicdb.data.service.core.debug

import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.model.BreakpointInfo
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.DebugSessionStatus
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

class GraalVmScriptDebugServiceTest {
    
    private lateinit var dataServiceManager: DataServiceManager
    private lateinit var debugService: GraalVmScriptDebugService
    
    @BeforeEach
    fun setUp() {
        dataServiceManager = mock(DataServiceManager::class.java)
        debugService = GraalVmScriptDebugService(dataServiceManager)
    }
    
    @Test
    fun `createDebugSession should create a debug session`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val parameters = mapOf("name" to "test")
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        
        // Act
        val session = debugService.createDebugSession(serviceId, parameters)
        
        // Assert
        assertNotNull(session)
        assertEquals(serviceId, session.serviceId)
        assertEquals(service.script, session.script)
        assertEquals(service.language, session.language)
        assertEquals(parameters, session.parameters)
        assertEquals(DebugSessionStatus.CREATED, session.status)
    }
    
    @Test
    fun `getDebugSession should return the debug session`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val parameters = mapOf("name" to "test")
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        
        // Create a session
        val session = debugService.createDebugSession(serviceId, parameters)
        
        // Act
        val retrievedSession = debugService.getDebugSession(session.id)
        
        // Assert
        assertNotNull(retrievedSession)
        assertEquals(session.id, retrievedSession?.id)
        assertEquals(serviceId, retrievedSession?.serviceId)
    }
    
    @Test
    fun `terminateDebugSession should terminate the debug session`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val parameters = mapOf("name" to "test")
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        
        // Create a session
        val session = debugService.createDebugSession(serviceId, parameters)
        
        // Act
        val result = debugService.terminateDebugSession(session.id)
        
        // Assert
        assertTrue(result)
        
        // Verify session is terminated
        val retrievedSession = debugService.getDebugSession(session.id)
        assertEquals(null, retrievedSession)
    }
    
    @Test
    fun `addBreakpoint should add a breakpoint to the session`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val parameters = mapOf("name" to "test")
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        
        // Create a session
        val session = debugService.createDebugSession(serviceId, parameters)
        
        // Create a breakpoint
        val breakpoint = BreakpointInfo(
            id = "bp1",
            line = 5,
            enabled = true
        )
        
        // Act
        val result = debugService.addBreakpoint(session.id, breakpoint)
        
        // Assert
        assertTrue(result)
        
        // Verify breakpoint is added
        val breakpoints = debugService.getBreakpoints(session.id)
        assertEquals(1, breakpoints.size)
        assertEquals(breakpoint.id, breakpoints[0].id)
        assertEquals(breakpoint.line, breakpoints[0].line)
    }
    
    @Test
    fun `removeBreakpoint should remove a breakpoint from the session`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val parameters = mapOf("name" to "test")
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        
        // Create a session
        val session = debugService.createDebugSession(serviceId, parameters)
        
        // Create a breakpoint
        val breakpoint = BreakpointInfo(
            id = "bp1",
            line = 5,
            enabled = true
        )
        
        // Add breakpoint
        debugService.addBreakpoint(session.id, breakpoint)
        
        // Act
        val result = debugService.removeBreakpoint(session.id, breakpoint.id)
        
        // Assert
        assertTrue(result)
        
        // Verify breakpoint is removed
        val breakpoints = debugService.getBreakpoints(session.id)
        assertTrue(breakpoints.isEmpty())
    }
    
    @Test
    fun `getBreakpoints should return all breakpoints in the session`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val parameters = mapOf("name" to "test")
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        
        // Create a session
        val session = debugService.createDebugSession(serviceId, parameters)
        
        // Create breakpoints
        val breakpoint1 = BreakpointInfo(
            id = "bp1",
            line = 5,
            enabled = true
        )
        
        val breakpoint2 = BreakpointInfo(
            id = "bp2",
            line = 10,
            enabled = true
        )
        
        // Add breakpoints
        debugService.addBreakpoint(session.id, breakpoint1)
        debugService.addBreakpoint(session.id, breakpoint2)
        
        // Act
        val breakpoints = debugService.getBreakpoints(session.id)
        
        // Assert
        assertEquals(2, breakpoints.size)
        assertTrue(breakpoints.any { it.id == breakpoint1.id })
        assertTrue(breakpoints.any { it.id == breakpoint2.id })
    }
    
    @Test
    fun `evaluateExpression should evaluate an expression in the session`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val parameters = mapOf("name" to "test")
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        
        // Create a session
        val session = debugService.createDebugSession(serviceId, parameters)
        
        // Act
        val result = debugService.evaluateExpression(session.id, "1 + 1")
        
        // Assert
        assertEquals(2, result)
    }
    
    private fun createSampleService(id: String = UUID.randomUUID().toString()): DataService {
        return DataService(
            id = id,
            name = "Test Service",
            description = "This is a test service",
            type = "query",
            script = """
                function execute(params) {
                    var name = params.name || "World";
                    return {
                        message: "Hello, " + name + "!",
                        timestamp: new Date().getTime()
                    };
                }
            """.trimIndent(),
            language = "js",
            gmtCreate = LocalDateTime.now(),
            gmtModified = LocalDateTime.now()
        )
    }
}
