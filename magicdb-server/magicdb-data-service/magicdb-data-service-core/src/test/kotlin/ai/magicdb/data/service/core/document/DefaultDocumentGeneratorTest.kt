package ai.magicdb.data.service.core.document

import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceParameter
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class DefaultDocumentGeneratorTest {
    
    private lateinit var documentGenerator: DefaultDocumentGenerator
    
    @BeforeEach
    fun setUp() {
        documentGenerator = DefaultDocumentGenerator(ObjectMapper())
    }
    
    @Test
    fun `generateDocument should create document from service`() {
        // Arrange
        val service = createSampleService()
        
        // Act
        val document = documentGenerator.generateDocument(service)
        
        // Assert
        assertEquals(service.name, document.title)
        assertEquals(service.description, document.description)
        assertEquals(service.id, document.serviceId)
        assertEquals(service.type, document.serviceType)
        assertEquals("/api/data-service/${service.id}/execute", document.path)
        assertEquals("POST", document.method)
        assertEquals(service.tags, document.tags)
        
        // Check parameters
        assertEquals(service.parameters.size, document.parameters.size)
        service.parameters.forEachIndexed { index, param ->
            val docParam = document.parameters[index]
            assertEquals(param.name, docParam.name)
            assertEquals(param.type, docParam.type)
            assertEquals(param.required, docParam.required)
            assertEquals(param.defaultValue, docParam.defaultValue)
            assertEquals(param.description, docParam.description)
        }
    }
    
    @Test
    fun `generateOpenApiSpec should create valid OpenAPI JSON`() {
        // Arrange
        val service = createSampleService()
        
        // Act
        val openApiSpec = documentGenerator.generateOpenApiSpec(service)
        
        // Assert
        assertNotNull(openApiSpec)
        assertTrue(openApiSpec.contains("\"openapi\""))
        assertTrue(openApiSpec.contains("\"paths\""))
        assertTrue(openApiSpec.contains("\"info\""))
        assertTrue(openApiSpec.contains(service.name))
        assertTrue(openApiSpec.contains(service.description ?: ""))
    }
    
    @Test
    fun `generateMarkdownDoc should create valid Markdown`() {
        // Arrange
        val service = createSampleService()
        
        // Act
        val markdown = documentGenerator.generateMarkdownDoc(service)
        
        // Assert
        assertNotNull(markdown)
        assertTrue(markdown.contains("# ${service.name}"))
        assertTrue(markdown.contains(service.description ?: ""))
        assertTrue(markdown.contains("## 基本信息"))
        assertTrue(markdown.contains("## 请求参数"))
        
        // Check parameters are included
        service.parameters.forEach { param ->
            assertTrue(markdown.contains(param.name))
        }
    }
    
    @Test
    fun `generateHtmlDoc should create valid HTML`() {
        // Arrange
        val service = createSampleService()
        
        // Act
        val html = documentGenerator.generateHtmlDoc(service)
        
        // Assert
        assertNotNull(html)
        assertTrue(html.contains("<!DOCTYPE html>"))
        assertTrue(html.contains("<html>"))
        assertTrue(html.contains("<title>${service.name}"))
        assertTrue(html.contains(service.description ?: ""))
        
        // Check parameters are included
        service.parameters.forEach { param ->
            assertTrue(html.contains(param.name))
        }
    }
    
    @Test
    fun `generatePdfDoc should create PDF bytes`() {
        // Arrange
        val service = createSampleService()
        
        // Act
        val pdfBytes = documentGenerator.generatePdfDoc(service)
        
        // Assert
        assertNotNull(pdfBytes)
        assertTrue(pdfBytes.isNotEmpty())
        
        // Check PDF header
        assertTrue(pdfBytes.size > 4)
        assertEquals('%'.code.toByte(), pdfBytes[0])
        assertEquals('P'.code.toByte(), pdfBytes[1])
        assertEquals('D'.code.toByte(), pdfBytes[2])
        assertEquals('F'.code.toByte(), pdfBytes[3])
    }
    
    private fun createSampleService(): DataService {
        val id = UUID.randomUUID().toString()
        return DataService(
            id = id,
            name = "Test Service",
            description = "This is a test service for documentation generation",
            type = "query",
            script = """
                /**
                 * This is a sample script that demonstrates the documentation generation.
                 * It returns a simple greeting message.
                 */
                function execute(params) {
                    // Get the name parameter or use a default
                    var name = params.name || "World";
                    
                    // Return a greeting object
                    return {
                        message: "Hello, " + name + "!",
                        timestamp: new Date().getTime(),
                        success: true
                    };
                }
            """.trimIndent(),
            language = "js",
            gmtCreate = LocalDateTime.now(),
            gmtModified = LocalDateTime.now(),
            enabled = true,
            tags = listOf("test", "documentation", "sample"),
            parameters = listOf(
                ServiceParameter(
                    serviceId = id,
                    name = "name",
                    type = "string",
                    description = "Name to greet",
                    defaultValue = "World",
                    required = false,
                    orderNum = 1
                ),
                ServiceParameter(
                    serviceId = id,
                    name = "format",
                    type = "string",
                    description = "Response format",
                    defaultValue = "json",
                    required = false,
                    orderNum = 2
                )
            )
        )
    }
}
