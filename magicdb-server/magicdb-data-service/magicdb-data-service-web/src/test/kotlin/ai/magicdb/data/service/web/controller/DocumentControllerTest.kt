package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.DocumentGenerator
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceDocument
import ai.magicdb.data.service.api.model.ServiceParameter
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.time.LocalDateTime
import java.util.UUID

class DocumentControllerTest {
    
    private lateinit var dataServiceManager: DataServiceManager
    private lateinit var documentGenerator: DocumentGenerator
    private lateinit var controller: DocumentController
    
    @BeforeEach
    fun setUp() {
        dataServiceManager = mock(DataServiceManager::class.java)
        documentGenerator = mock(DocumentGenerator::class.java)
        controller = DocumentController(dataServiceManager, documentGenerator)
    }
    
    @Test
    fun `getServiceDoc should return service document`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val document = createSampleDocument(serviceId = serviceId)
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        `when`(documentGenerator.generateDocument(service)).thenReturn(document)
        
        // Act
        val result = controller.getServiceDoc(serviceId)
        
        // Assert
        assertTrue(result.success)
        assertEquals(document, result.data)
        verify(dataServiceManager).getService(serviceId)
        verify(documentGenerator).generateDocument(service)
    }
    
    @Test
    fun `getServiceDoc should return error when service not found`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(null)
        
        // Act
        val result = controller.getServiceDoc(serviceId)
        
        // Assert
        assertTrue(!result.success)
        assertEquals(null, result.data)
        assertEquals("Service not found", result.errorMessage)
        verify(dataServiceManager).getService(serviceId)
    }
    
    @Test
    fun `getServiceSwagger should return OpenAPI spec`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val openApiSpec = "{\"openapi\":\"3.0.1\"}"
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        `when`(documentGenerator.generateOpenApiSpec(service)).thenReturn(openApiSpec)
        
        // Act
        val result = controller.getServiceSwagger(serviceId)
        
        // Assert
        assertTrue(result.success)
        assertEquals(openApiSpec, result.data)
        verify(dataServiceManager).getService(serviceId)
        verify(documentGenerator).generateOpenApiSpec(service)
    }
    
    @Test
    fun `exportServiceDoc should return markdown document`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val markdown = "# Test Service"
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        `when`(documentGenerator.generateMarkdownDoc(service)).thenReturn(markdown)
        
        // Act
        val result = controller.exportServiceDoc(serviceId, "markdown")
        
        // Assert
        assertEquals(HttpStatus.OK.value(), result.statusCodeValue)
        assertEquals(MediaType.parseMediaType("text/markdown"), result.headers.contentType)
        assertNotNull(result.body)
        assertEquals(markdown, String(result.body!!))
        verify(dataServiceManager).getService(serviceId)
        verify(documentGenerator).generateMarkdownDoc(service)
    }
    
    @Test
    fun `exportServiceDoc should return HTML document`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val html = "<!DOCTYPE html><html><body>Test</body></html>"
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        `when`(documentGenerator.generateHtmlDoc(service)).thenReturn(html)
        
        // Act
        val result = controller.exportServiceDoc(serviceId, "html")
        
        // Assert
        assertEquals(HttpStatus.OK.value(), result.statusCodeValue)
        assertEquals(MediaType.TEXT_HTML, result.headers.contentType)
        assertNotNull(result.body)
        assertEquals(html, String(result.body!!))
        verify(dataServiceManager).getService(serviceId)
        verify(documentGenerator).generateHtmlDoc(service)
    }
    
    @Test
    fun `exportServiceDoc should return PDF document`() {
        // Arrange
        val serviceId = UUID.randomUUID().toString()
        val service = createSampleService(id = serviceId)
        val pdfBytes = byteArrayOf(1, 2, 3, 4)
        
        `when`(dataServiceManager.getService(serviceId)).thenReturn(service)
        `when`(documentGenerator.generatePdfDoc(service)).thenReturn(pdfBytes)
        
        // Act
        val result = controller.exportServiceDoc(serviceId, "pdf")
        
        // Assert
        assertEquals(HttpStatus.OK.value(), result.statusCodeValue)
        assertEquals(MediaType.APPLICATION_PDF, result.headers.contentType)
        assertNotNull(result.body)
        assertEquals(pdfBytes.size, result.body!!.size)
        verify(dataServiceManager).getService(serviceId)
        verify(documentGenerator).generatePdfDoc(service)
    }
    
    private fun createSampleService(id: String = UUID.randomUUID().toString()): DataService {
        return DataService(
            id = id,
            name = "Test Service",
            description = "This is a test service",
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
    
    private fun createSampleDocument(serviceId: String): ServiceDocument {
        return ServiceDocument(
            title = "Test Service",
            description = "This is a test service",
            serviceId = serviceId,
            serviceType = "query",
            path = "/api/data-service/$serviceId/execute",
            method = "POST"
        )
    }
}
