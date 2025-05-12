package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceGroup
import ai.magicdb.data.service.web.dto.DataServiceDTO
import ai.magicdb.data.service.web.dto.ServiceGroupDTO
import ai.magicdb.data.service.web.dto.ServiceParameterDTO
import ai.magicdb.server.tools.common.util.ContextUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import java.time.LocalDateTime
import java.util.UUID

@PrepareForTest(ContextUtils::class)
class DataServiceControllerTest {
    
    private lateinit var dataServiceManager: DataServiceManager
    private lateinit var controller: DataServiceController
    
    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dataServiceManager = mock(DataServiceManager::class.java)
        controller = DataServiceController(dataServiceManager)
        
        // Mock static ContextUtils
        PowerMockito.mockStatic(ContextUtils::class.java)
        PowerMockito.`when`(ContextUtils.getUserId()).thenReturn(1L)
    }
    
    @Test
    fun `getAllServices should return all services`() {
        // Arrange
        val services = listOf(
            createSampleService(),
            createSampleService()
        )
        
        `when`(dataServiceManager.getAllServices()).thenReturn(services)
        
        // Act
        val result = controller.getAllServices()
        
        // Assert
        assert(result.success)
        assert(result.data.size == 2)
        verify(dataServiceManager).getAllServices()
    }
    
    @Test
    fun `getService should return service by ID`() {
        // Arrange
        val id = UUID.randomUUID().toString()
        val service = createSampleService(id = id)
        
        `when`(dataServiceManager.getService(id)).thenReturn(service)
        
        // Act
        val result = controller.getService(id)
        
        // Assert
        assert(result.success)
        assert(result.data.id == id)
        verify(dataServiceManager).getService(id)
    }
    
    @Test
    fun `createService should create a new service`() {
        // Arrange
        val dto = createSampleServiceDTO()
        val service = any(DataService::class.java)
        val createdService = createSampleService()
        
        `when`(dataServiceManager.createService(service)).thenReturn(createdService)
        
        // Act
        val result = controller.createService(dto)
        
        // Assert
        assert(result.success)
        verify(dataServiceManager).createService(any())
    }
    
    @Test
    fun `updateService should update an existing service`() {
        // Arrange
        val id = UUID.randomUUID().toString()
        val dto = createSampleServiceDTO()
        val existingService = createSampleService(id = id)
        val updatedService = existingService.copy(name = "Updated Service")
        
        `when`(dataServiceManager.getService(id)).thenReturn(existingService)
        `when`(dataServiceManager.updateService(any())).thenReturn(updatedService)
        
        // Act
        val result = controller.updateService(id, dto)
        
        // Assert
        assert(result.success)
        verify(dataServiceManager).updateService(any())
    }
    
    @Test
    fun `getAllGroups should return all groups`() {
        // Arrange
        val groups = listOf(
            createSampleGroup(),
            createSampleGroup()
        )
        
        `when`(dataServiceManager.getAllGroups()).thenReturn(groups)
        
        // Act
        val result = controller.getAllGroups()
        
        // Assert
        assert(result.success)
        assert(result.data.size == 2)
        verify(dataServiceManager).getAllGroups()
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
            parameters = emptyList()
        )
    }
    
    private fun createSampleServiceDTO(): DataServiceDTO {
        return DataServiceDTO(
            name = "Test Service",
            description = "Test Description",
            type = "query",
            script = "function execute(params) { return { message: 'Hello' }; }",
            language = "js",
            parameters = listOf(
                ServiceParameterDTO(
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
    
    private fun createSampleGroupDTO(): ServiceGroupDTO {
        return ServiceGroupDTO(
            name = "Test Group",
            description = "Test Description"
        )
    }
}
