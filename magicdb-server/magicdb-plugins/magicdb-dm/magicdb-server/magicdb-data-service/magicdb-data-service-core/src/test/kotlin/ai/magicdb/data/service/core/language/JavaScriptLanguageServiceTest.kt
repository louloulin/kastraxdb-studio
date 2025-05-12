package ai.magicdb.data.service.core.language

import ai.magicdb.data.service.api.model.CompletionItemKind
import ai.magicdb.data.service.api.model.DiagnosticSeverity
import ai.magicdb.data.service.api.model.InsertTextFormat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JavaScriptLanguageServiceTest {
    
    private lateinit var languageService: JavaScriptLanguageService
    
    @BeforeEach
    fun setUp() {
        languageService = JavaScriptLanguageService()
    }
    
    @Test
    fun `getCompletionItems should return JavaScript keywords and built-ins`() {
        // Arrange
        val script = "function test() { con }"
        val position = 20 // Position after "con"
        
        // Act
        val completionItems = languageService.getCompletionItems("js", script, position)
        
        // Assert
        assertFalse(completionItems.isEmpty())
        
        // Should include keywords
        val keywordItems = completionItems.filter { it.kind == CompletionItemKind.KEYWORD }
        assertFalse(keywordItems.isEmpty())
        assertTrue(keywordItems.any { it.label == "function" })
        assertTrue(keywordItems.any { it.label == "return" })
        
        // Should include built-in objects
        val objectItems = completionItems.filter { it.kind == CompletionItemKind.CLASS }
        assertFalse(objectItems.isEmpty())
        assertTrue(objectItems.any { it.label == "console" })
        
        // Should include snippets
        val snippetItems = completionItems.filter { it.kind == CompletionItemKind.SNIPPET }
        assertFalse(snippetItems.isEmpty())
        assertTrue(snippetItems.any { it.label == "function" })
        assertTrue(snippetItems.any { it.insertTextFormat == InsertTextFormat.SNIPPET })
    }
    
    @Test
    fun `getDiagnostics should detect syntax errors`() {
        // Arrange
        val script = "function test() { console.log('Hello' }" // Missing closing parenthesis
        
        // Act
        val diagnostics = languageService.getDiagnostics("js", script)
        
        // Assert
        assertFalse(diagnostics.isEmpty())
        assertEquals(DiagnosticSeverity.ERROR, diagnostics[0].severity)
    }
    
    @Test
    fun `getDiagnostics should return empty list for valid script`() {
        // Arrange
        val script = "function test() { console.log('Hello'); }"
        
        // Act
        val diagnostics = languageService.getDiagnostics("js", script)
        
        // Assert
        assertTrue(diagnostics.isEmpty())
    }
    
    @Test
    fun `getHoverInfo should return information for known symbols`() {
        // Arrange
        val script = "function test() { console.log('Hello'); }"
        val position = 20 // Position on "console"
        
        // Act
        val hoverInfo = languageService.getHoverInfo("js", script, position)
        
        // Assert
        assertNotNull(hoverInfo)
        assertFalse(hoverInfo!!.contents.isEmpty())
        assertTrue(hoverInfo.contents[0].contains("console"))
    }
    
    @Test
    fun `formatCode should return the script unchanged`() {
        // Arrange
        val script = "function test() { console.log('Hello'); }"
        
        // Act
        val formattedCode = languageService.formatCode("js", script)
        
        // Assert
        assertEquals(script, formattedCode)
    }
    
    @Test
    fun `getLanguageCapabilities should return capabilities for JavaScript`() {
        // Act
        val capabilities = languageService.getLanguageCapabilities("js")
        
        // Assert
        assertFalse(capabilities.isEmpty())
        assertTrue(capabilities["completion"] == true)
        assertTrue(capabilities["diagnostics"] == true)
        assertTrue(capabilities["hover"] == true)
        assertTrue(capabilities["formatting"] == true)
    }
    
    @Test
    fun `getLanguageCapabilities should return empty map for unsupported language`() {
        // Act
        val capabilities = languageService.getLanguageCapabilities("unsupported")
        
        // Assert
        assertTrue(capabilities.isEmpty())
    }
}
