package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.ScriptLanguageService
import ai.magicdb.data.service.api.model.CompletionItem
import ai.magicdb.data.service.api.model.CompletionItemKind
import ai.magicdb.data.service.api.model.DiagnosticItem
import ai.magicdb.data.service.api.model.DiagnosticSeverity
import ai.magicdb.data.service.api.model.HoverInfo
import ai.magicdb.data.service.api.model.InsertTextFormat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ScriptLanguageControllerTest {
    
    private lateinit var scriptLanguageService: ScriptLanguageService
    private lateinit var controller: ScriptLanguageController
    
    @BeforeEach
    fun setUp() {
        scriptLanguageService = mock(ScriptLanguageService::class.java)
        controller = ScriptLanguageController(scriptLanguageService)
    }
    
    @Test
    fun `getCompletionItems should return completion items`() {
        // Arrange
        val language = "javascript"
        val script = "function test() { con }"
        val position = 20
        val request = ScriptLanguageController.CompletionRequest(script, position)
        
        val completionItems = listOf(
            CompletionItem(
                label = "console",
                insertText = "console",
                kind = CompletionItemKind.CLASS,
                detail = "JavaScript built-in object"
            )
        )
        
        `when`(scriptLanguageService.getCompletionItems(language, script, position)).thenReturn(completionItems)
        
        // Act
        val result = controller.getCompletionItems(language, request)
        
        // Assert
        assertTrue(result.success)
        assertEquals(completionItems, result.data)
        verify(scriptLanguageService).getCompletionItems(language, script, position)
    }
    
    @Test
    fun `getDiagnostics should return diagnostic items`() {
        // Arrange
        val language = "javascript"
        val script = "function test() { console.log('Hello' }" // Missing closing parenthesis
        val request = ScriptLanguageController.DiagnosticsRequest(script)
        
        val diagnostics = listOf(
            DiagnosticItem(
                message = "Missing closing parenthesis",
                startPosition = 30,
                endPosition = 31,
                severity = DiagnosticSeverity.ERROR
            )
        )
        
        `when`(scriptLanguageService.getDiagnostics(language, script)).thenReturn(diagnostics)
        
        // Act
        val result = controller.getDiagnostics(language, request)
        
        // Assert
        assertTrue(result.success)
        assertEquals(diagnostics, result.data)
        verify(scriptLanguageService).getDiagnostics(language, script)
    }
    
    @Test
    fun `getHoverInfo should return hover information`() {
        // Arrange
        val language = "javascript"
        val script = "function test() { console.log('Hello'); }"
        val position = 20
        val request = ScriptLanguageController.HoverRequest(script, position)
        
        val hoverInfo = HoverInfo(
            contents = listOf("JavaScript built-in object: console"),
            rangeStart = 15,
            rangeEnd = 22
        )
        
        `when`(scriptLanguageService.getHoverInfo(language, script, position)).thenReturn(hoverInfo)
        
        // Act
        val result = controller.getHoverInfo(language, request)
        
        // Assert
        assertTrue(result.success)
        assertEquals(hoverInfo, result.data)
        verify(scriptLanguageService).getHoverInfo(language, script, position)
    }
    
    @Test
    fun `formatCode should return formatted code`() {
        // Arrange
        val language = "javascript"
        val script = "function test() { console.log('Hello'); }"
        val request = ScriptLanguageController.FormatRequest(script)
        
        val formattedCode = "function test() {\n  console.log('Hello');\n}"
        
        `when`(scriptLanguageService.formatCode(language, script)).thenReturn(formattedCode)
        
        // Act
        val result = controller.formatCode(language, request)
        
        // Assert
        assertTrue(result.success)
        assertEquals(formattedCode, result.data)
        verify(scriptLanguageService).formatCode(language, script)
    }
    
    @Test
    fun `getLanguageCapabilities should return language capabilities`() {
        // Arrange
        val language = "javascript"
        val capabilities = mapOf(
            "completion" to true,
            "diagnostics" to true,
            "hover" to true,
            "formatting" to true
        )
        
        `when`(scriptLanguageService.getLanguageCapabilities(language)).thenReturn(capabilities)
        
        // Act
        val result = controller.getLanguageCapabilities(language)
        
        // Assert
        assertTrue(result.success)
        assertEquals(capabilities, result.data)
        verify(scriptLanguageService).getLanguageCapabilities(language)
    }
}
