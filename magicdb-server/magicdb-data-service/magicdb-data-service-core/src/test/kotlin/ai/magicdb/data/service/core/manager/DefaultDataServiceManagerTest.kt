package ai.magicdb.data.service.core.manager

import ai.magicdb.data.service.api.DataServiceRepository
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceGroup
import ai.magicdb.data.service.api.model.ServiceParameter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import java.util.UUID

class DefaultDataServiceManagerTest {
    
    private lateinit var repository: DataServiceRepository
    private lateinit var manager: DefaultDataServiceManager
    
    @BeforeEach
    fun setUp() {
        repository = mock(DataServiceRepository::class.java)
        manager = DefaultDataServiceManager(repository)
    }
    
    @Test
    fun `createService should generate ID and timestamps`() {
        // Arrange
        val service = createSampleService(id = "")
        val savedService = service.copy(id = UUID.randomUUID().toString())
        
        `when`(repository.saveService(service)).thenReturn(savedService)
        
        // Act
        val result = manager.createService(service)
        
        // Assert
        assertNotNull(result.id)
        assertNotNull(result.gmtCreate)
        assertNotNull(result.gmtModified)
        assertEquals(result.gmtCreate, result.gmtModified)
        verify(repository).saveService(service)
    }
    
    @Test
    fun `updateService should update timestamp`() {
        // Arrange
        val id = UUID.randomUUID().toString()
        val existingService = createSampleService(id = id)
        val updatedService = existingService.copy(name = "Updated Service")
        
        `when`(repository.getService(id)).thenReturn(existingService)
        `when`(repository.saveService(updatedService)).thenReturn(updatedService)
        
        // Act
        val result = manager.updateService(updatedService)
        
        // Assert
        assertEquals("Updated Service", result.name)
        assertEquals(existingService.gmtCreate, result.gmtCreate)
        verify(repository).saveService(updatedService)
    }
    
    @Test
    fun `createGroup should generate ID and timestamps`() {
        // Arrange
        val group = createSampleGroup(id = "")
        val savedGroup = group.copy(id = UUID.randomUUID().toString())
        
        `when`(repository.saveGroup(group)).thenReturn(savedGroup)
        
        // Act
        val result = manager.createGroup(group)
        
        // Assert
        assertNotNull(result.id)
        assertNotNull(result.gmtCreate)
        assertNotNull(result.gmtModified)
        assertEquals(result.gmtCreate, result.gmtModified)
        verify(repository).saveGroup(group)
    }
    
    private fun createSampleService(id: String = UUID.randomUUID().toString()): DataService {
        return DataService(
            id = id,
            name = "Test Service",
            description = "Test Description",
            type = "query",
            script = "function execute(params) { return { message: 'Hello' }; }",
            language = "js",
            gmtCreate = LocalDateTime.now(),
            gmtModified = LocalDateTime.now(),
            parameters = listOf(
                ServiceParameter(
                    serviceId = id,
                    name = "param1",
                    type = "string",
                    description = "Test Parameter",
                    required = false
                )
            )
        )
    }
    
    private fun createSampleGroup(id: String = UUID.randomUUID().toString()): ServiceGroup {
        return ServiceGroup(
            id = id,
            name = "Test Group",
            description = "Test Description",
            gmtCreate = LocalDateTime.now(),
            gmtModified = LocalDateTime.now()
        )
    }
}
