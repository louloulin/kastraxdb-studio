package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.DocumentGenerator
import ai.magicdb.dataservice.api.model.DocumentTemplate
import ai.magicdb.dataservice.api.model.ServiceDocument
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * 文档控制器测试
 *
 * @author magicdb
 */
@WebMvcTest(DocumentController::class)
class DocumentControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockBean
    private lateinit var documentGenerator: DocumentGenerator
    
    @BeforeEach
    fun setUp() {
        // 设置模拟对象的行为
    }
    
    @Test
    fun testGenerateServiceDocument() {
        // 准备测试数据
        val serviceId = "service-123"
        val document = createTestServiceDocument(serviceId)
        
        // 设置模拟对象的行为
        `when`(documentGenerator.generateServiceDocument(serviceId, null)).thenReturn(document)
        
        // 执行请求
        val result = mockMvc.perform(post("/api/data-service/document/service/$serviceId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试服务 - 接口文档\""))
        
        // 验证方法调用
        verify(documentGenerator).generateServiceDocument(serviceId, null)
    }
    
    @Test
    fun testGenerateGroupDocument() {
        // 准备测试数据
        val groupId = "group-123"
        val document = createTestGroupDocument(groupId)
        
        // 设置模拟对象的行为
        `when`(documentGenerator.generateGroupDocument(groupId, null)).thenReturn(document)
        
        // 执行请求
        val result = mockMvc.perform(post("/api/data-service/document/group/$groupId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试分组 - 接口文档\""))
        
        // 验证方法调用
        verify(documentGenerator).generateGroupDocument(groupId, null)
    }
    
    @Test
    fun testGenerateApiDocument() {
        // 准备测试数据
        val document = createTestApiDocument()
        
        // 设置模拟对象的行为
        `when`(documentGenerator.generateApiDocument(null)).thenReturn(document)
        
        // 执行请求
        val result = mockMvc.perform(post("/api/data-service/document/api")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"API接口文档\""))
        
        // 验证方法调用
        verify(documentGenerator).generateApiDocument(null)
    }
    
    @Test
    fun testGetDocument() {
        // 准备测试数据
        val documentId = "doc-123"
        val document = createTestServiceDocument("service-123")
        document.id = documentId
        
        // 设置模拟对象的行为
        `when`(documentGenerator.getDocument(documentId)).thenReturn(document)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/document/$documentId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试服务 - 接口文档\""))
        
        // 验证方法调用
        verify(documentGenerator).getDocument(documentId)
    }
    
    @Test
    fun testGetServiceDocument() {
        // 准备测试数据
        val serviceId = "service-123"
        val document = createTestServiceDocument(serviceId)
        
        // 设置模拟对象的行为
        `when`(documentGenerator.getServiceDocument(serviceId)).thenReturn(document)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/document/service/$serviceId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试服务 - 接口文档\""))
        
        // 验证方法调用
        verify(documentGenerator).getServiceDocument(serviceId)
    }
    
    @Test
    fun testGetGroupDocument() {
        // 准备测试数据
        val groupId = "group-123"
        val document = createTestGroupDocument(groupId)
        
        // 设置模拟对象的行为
        `when`(documentGenerator.getGroupDocument(groupId)).thenReturn(document)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/document/group/$groupId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试分组 - 接口文档\""))
        
        // 验证方法调用
        verify(documentGenerator).getGroupDocument(groupId)
    }
    
    @Test
    fun testGetApiDocument() {
        // 准备测试数据
        val document = createTestApiDocument()
        
        // 设置模拟对象的行为
        `when`(documentGenerator.getApiDocument()).thenReturn(document)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/document/api")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"API接口文档\""))
        
        // 验证方法调用
        verify(documentGenerator).getApiDocument()
    }
    
    @Test
    fun testGetAllDocuments() {
        // 准备测试数据
        val document1 = createTestServiceDocument("service-123")
        val document2 = createTestGroupDocument("group-123")
        val documents = listOf(document1, document2)
        
        // 设置模拟对象的行为
        `when`(documentGenerator.getAllDocuments()).thenReturn(documents)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/document/list")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试服务 - 接口文档\""))
        assert(response.contains("\"测试分组 - 接口文档\""))
        
        // 验证方法调用
        verify(documentGenerator).getAllDocuments()
    }
    
    @Test
    fun testSaveDocument() {
        // 准备测试数据
        val document = createTestServiceDocument("service-123")
        
        // 设置模拟对象的行为
        `when`(documentGenerator.saveDocument(any())).thenReturn("doc-123")
        
        // 执行请求
        val result = mockMvc.perform(post("/api/data-service/document")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(document)))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"doc-123\""))
        
        // 验证方法调用
        verify(documentGenerator).saveDocument(any())
    }
    
    @Test
    fun testDeleteDocument() {
        // 准备测试数据
        val documentId = "doc-123"
        
        // 设置模拟对象的行为
        `when`(documentGenerator.deleteDocument(documentId)).thenReturn(true)
        
        // 执行请求
        val result = mockMvc.perform(delete("/api/data-service/document/$documentId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(documentGenerator).deleteDocument(documentId)
    }
    
    @Test
    fun testGetTemplate() {
        // 准备测试数据
        val templateId = "template-123"
        val template = createTestServiceTemplate()
        
        // 设置模拟对象的行为
        `when`(documentGenerator.getTemplate(templateId)).thenReturn(template)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/document/template/$templateId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"默认服务文档模板\""))
        
        // 验证方法调用
        verify(documentGenerator).getTemplate(templateId)
    }
    
    @Test
    fun testGetAllTemplates() {
        // 准备测试数据
        val template1 = createTestServiceTemplate()
        val template2 = createTestGroupTemplate()
        val templates = listOf(template1, template2)
        
        // 设置模拟对象的行为
        `when`(documentGenerator.getAllTemplates(null)).thenReturn(templates)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/document/template/list")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"默认服务文档模板\""))
        assert(response.contains("\"默认分组文档模板\""))
        
        // 验证方法调用
        verify(documentGenerator).getAllTemplates(null)
    }
    
    @Test
    fun testGetDefaultTemplate() {
        // 准备测试数据
        val type = "service"
        val template = createTestServiceTemplate()
        
        // 设置模拟对象的行为
        `when`(documentGenerator.getDefaultTemplate(type)).thenReturn(template)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/document/template/default/$type")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"默认服务文档模板\""))
        
        // 验证方法调用
        verify(documentGenerator).getDefaultTemplate(type)
    }
    
    @Test
    fun testSaveTemplate() {
        // 准备测试数据
        val template = createTestServiceTemplate()
        
        // 设置模拟对象的行为
        `when`(documentGenerator.saveTemplate(any())).thenReturn("template-123")
        
        // 执行请求
        val result = mockMvc.perform(post("/api/data-service/document/template")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(template)))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"template-123\""))
        
        // 验证方法调用
        verify(documentGenerator).saveTemplate(any())
    }
    
    @Test
    fun testDeleteTemplate() {
        // 准备测试数据
        val templateId = "template-123"
        
        // 设置模拟对象的行为
        `when`(documentGenerator.deleteTemplate(templateId)).thenReturn(true)
        
        // 执行请求
        val result = mockMvc.perform(delete("/api/data-service/document/template/$templateId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(documentGenerator).deleteTemplate(templateId)
    }
    
    /**
     * 创建测试服务文档
     */
    private fun createTestServiceDocument(serviceId: String): ServiceDocument {
        return ServiceDocument(
            id = "doc-123",
            title = "测试服务 - 接口文档",
            content = "# 测试服务\n\n**ID**: $serviceId\n\n**描述**: 测试服务描述",
            format = "markdown",
            serviceId = serviceId,
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1,
            isPublic = true
        )
    }
    
    /**
     * 创建测试分组文档
     */
    private fun createTestGroupDocument(groupId: String): ServiceDocument {
        return ServiceDocument(
            id = "doc-456",
            title = "测试分组 - 接口文档",
            content = "# 测试分组\n\n**ID**: $groupId\n\n**描述**: 测试分组描述",
            format = "markdown",
            groupId = groupId,
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1,
            isPublic = true
        )
    }
    
    /**
     * 创建测试API文档
     */
    private fun createTestApiDocument(): ServiceDocument {
        return ServiceDocument(
            id = "doc-789",
            title = "API接口文档",
            content = "# API接口文档\n\n## 目录\n\n## 接口详情",
            format = "markdown",
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1,
            isPublic = true
        )
    }
    
    /**
     * 创建测试服务模板
     */
    private fun createTestServiceTemplate(): DocumentTemplate {
        return DocumentTemplate(
            id = "template-123",
            name = "默认服务文档模板",
            content = "# {{service.name}}\n\n**ID**: {{service.id}}\n\n**描述**: {{service.description}}",
            format = "markdown",
            type = "service",
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1,
            isSystem = true,
            enabled = true,
            sort = 0,
            description = "默认服务文档模板"
        )
    }
    
    /**
     * 创建测试分组模板
     */
    private fun createTestGroupTemplate(): DocumentTemplate {
        return DocumentTemplate(
            id = "template-456",
            name = "默认分组文档模板",
            content = "# {{group.name}}\n\n**ID**: {{group.id}}\n\n**描述**: {{group.description}}",
            format = "markdown",
            type = "group",
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1,
            isSystem = true,
            enabled = true,
            sort = 0,
            description = "默认分组文档模板"
        )
    }
}
