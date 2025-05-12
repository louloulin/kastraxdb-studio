package ai.magicdb.data.service.core.snippet

import ai.magicdb.data.service.api.model.CodeSnippet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date
import java.util.UUID

class DefaultCodeSnippetServiceTest {
    
    private lateinit var snippetService: DefaultCodeSnippetService
    
    @BeforeEach
    fun setUp() {
        snippetService = DefaultCodeSnippetService()
    }
    
    @Test
    fun `getAllSnippets should return all snippets`() {
        // Act
        val snippets = snippetService.getAllSnippets()
        
        // Assert
        assertFalse(snippets.isEmpty())
        
        // Verify predefined snippets are included
        assertTrue(snippets.any { it.name == "SQL 查询" && it.language == "javascript" })
        assertTrue(snippets.any { it.name == "HTTP 请求" && it.language == "javascript" })
    }
    
    @Test
    fun `getAllSnippets with language filter should return snippets for that language`() {
        // Act
        val jsSnippets = snippetService.getAllSnippets("javascript")
        val kotlinSnippets = snippetService.getAllSnippets("kotlin")
        
        // Assert
        assertFalse(jsSnippets.isEmpty())
        assertFalse(kotlinSnippets.isEmpty())
        
        // Verify all returned snippets are for the specified language
        assertTrue(jsSnippets.all { it.language == "javascript" })
        assertTrue(kotlinSnippets.all { it.language == "kotlin" })
    }
    
    @Test
    fun `getSnippet should return the snippet with the given id`() {
        // Arrange
        val snippets = snippetService.getAllSnippets()
        val snippet = snippets.first()
        
        // Act
        val retrievedSnippet = snippetService.getSnippet(snippet.id)
        
        // Assert
        assertNotNull(retrievedSnippet)
        assertEquals(snippet.id, retrievedSnippet?.id)
        assertEquals(snippet.name, retrievedSnippet?.name)
        assertEquals(snippet.content, retrievedSnippet?.content)
    }
    
    @Test
    fun `createSnippet should create a new snippet`() {
        // Arrange
        val snippet = CodeSnippet(
            id = "",
            name = "Test Snippet",
            description = "This is a test snippet",
            content = "function test() { return 'Hello'; }",
            language = "javascript",
            tags = listOf("test", "example"),
            system = false,
            creator = "test",
            createTime = Date(),
            updateTime = Date()
        )
        
        // Act
        val createdSnippet = snippetService.createSnippet(snippet)
        
        // Assert
        assertNotNull(createdSnippet)
        assertNotNull(createdSnippet.id)
        assertEquals(snippet.name, createdSnippet.name)
        assertEquals(snippet.description, createdSnippet.description)
        assertEquals(snippet.content, createdSnippet.content)
        assertEquals(snippet.language, createdSnippet.language)
        assertEquals(snippet.tags, createdSnippet.tags)
        assertEquals(snippet.system, createdSnippet.system)
        assertEquals(snippet.creator, createdSnippet.creator)
        
        // Verify snippet is added to the service
        val retrievedSnippet = snippetService.getSnippet(createdSnippet.id)
        assertNotNull(retrievedSnippet)
    }
    
    @Test
    fun `updateSnippet should update an existing snippet`() {
        // Arrange
        val snippet = CodeSnippet(
            id = "",
            name = "Test Snippet",
            description = "This is a test snippet",
            content = "function test() { return 'Hello'; }",
            language = "javascript",
            tags = listOf("test", "example"),
            system = false,
            creator = "test",
            createTime = Date(),
            updateTime = Date()
        )
        
        val createdSnippet = snippetService.createSnippet(snippet)
        
        val updatedSnippet = createdSnippet.copy(
            name = "Updated Snippet",
            description = "This is an updated snippet",
            content = "function test() { return 'Hello World'; }",
            tags = listOf("test", "example", "updated")
        )
        
        // Act
        val result = snippetService.updateSnippet(createdSnippet.id, updatedSnippet)
        
        // Assert
        assertNotNull(result)
        assertEquals(createdSnippet.id, result?.id)
        assertEquals(updatedSnippet.name, result?.name)
        assertEquals(updatedSnippet.description, result?.description)
        assertEquals(updatedSnippet.content, result?.content)
        assertEquals(updatedSnippet.tags, result?.tags)
        
        // Verify snippet is updated in the service
        val retrievedSnippet = snippetService.getSnippet(createdSnippet.id)
        assertEquals(updatedSnippet.name, retrievedSnippet?.name)
    }
    
    @Test
    fun `updateSnippet should not update a system snippet`() {
        // Arrange
        val snippets = snippetService.getAllSnippets()
        val systemSnippet = snippets.first { it.system }
        
        val updatedSnippet = systemSnippet.copy(
            name = "Updated System Snippet",
            description = "This is an updated system snippet"
        )
        
        // Act
        val result = snippetService.updateSnippet(systemSnippet.id, updatedSnippet)
        
        // Assert
        assertNull(result)
        
        // Verify snippet is not updated in the service
        val retrievedSnippet = snippetService.getSnippet(systemSnippet.id)
        assertEquals(systemSnippet.name, retrievedSnippet?.name)
    }
    
    @Test
    fun `deleteSnippet should delete a snippet`() {
        // Arrange
        val snippet = CodeSnippet(
            id = "",
            name = "Test Snippet",
            description = "This is a test snippet",
            content = "function test() { return 'Hello'; }",
            language = "javascript",
            tags = listOf("test", "example"),
            system = false,
            creator = "test",
            createTime = Date(),
            updateTime = Date()
        )
        
        val createdSnippet = snippetService.createSnippet(snippet)
        
        // Act
        val result = snippetService.deleteSnippet(createdSnippet.id)
        
        // Assert
        assertTrue(result)
        
        // Verify snippet is deleted from the service
        val retrievedSnippet = snippetService.getSnippet(createdSnippet.id)
        assertNull(retrievedSnippet)
    }
    
    @Test
    fun `deleteSnippet should not delete a system snippet`() {
        // Arrange
        val snippets = snippetService.getAllSnippets()
        val systemSnippet = snippets.first { it.system }
        
        // Act
        val result = snippetService.deleteSnippet(systemSnippet.id)
        
        // Assert
        assertFalse(result)
        
        // Verify snippet is not deleted from the service
        val retrievedSnippet = snippetService.getSnippet(systemSnippet.id)
        assertNotNull(retrievedSnippet)
    }
    
    @Test
    fun `searchSnippets should return snippets matching the keyword`() {
        // Arrange
        val keyword = "SQL"
        
        // Act
        val results = snippetService.searchSnippets(keyword)
        
        // Assert
        assertFalse(results.isEmpty())
        
        // Verify all returned snippets match the keyword
        assertTrue(results.all { 
            it.name.contains(keyword, ignoreCase = true) || 
            it.description?.contains(keyword, ignoreCase = true) == true ||
            it.tags.any { tag -> tag.contains(keyword, ignoreCase = true) }
        })
    }
    
    @Test
    fun `searchSnippets with language filter should return matching snippets for that language`() {
        // Arrange
        val keyword = "SQL"
        val language = "javascript"
        
        // Act
        val results = snippetService.searchSnippets(keyword, language)
        
        // Assert
        assertFalse(results.isEmpty())
        
        // Verify all returned snippets match the keyword and language
        assertTrue(results.all { 
            it.language == language &&
            (it.name.contains(keyword, ignoreCase = true) || 
            it.description?.contains(keyword, ignoreCase = true) == true ||
            it.tags.any { tag -> tag.contains(keyword, ignoreCase = true) })
        })
    }
}
