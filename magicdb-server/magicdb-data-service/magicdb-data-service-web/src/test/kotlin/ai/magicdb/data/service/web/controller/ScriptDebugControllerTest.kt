package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.ScriptDebugger
import ai.magicdb.data.service.api.model.ScriptDebugRequest
import ai.magicdb.data.service.api.model.ScriptDebugResult
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
 * 脚本调试控制器测试
 *
 * @author magicdb
 */
@WebMvcTest(ScriptDebugController::class)
class ScriptDebugControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockBean
    private lateinit var scriptDebugger: ScriptDebugger
    
    @BeforeEach
    fun setUp() {
        // 设置模拟对象的行为
    }
    
    @Test
    fun testDebug() {
        // 准备测试数据
        val request = ScriptDebugRequest(
            script = "function execute(params) { return { message: 'Hello, ' + params.name }; }",
            language = "js",
            parameters = mapOf("name" to "World")
        )
        val result = ScriptDebugResult(
            success = true,
            data = mapOf("message" to "Hello, World"),
            duration = 100
        )
        
        // 设置模拟对象的行为
        `when`(scriptDebugger.debug(any())).thenReturn(result)
        
        // 执行请求
        val response = mockMvc.perform(post("/api/data-service/script/debug")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn()
            .response.contentAsString
        
        // 验证结果
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"Hello, World\""))
        
        // 验证方法调用
        verify(scriptDebugger).debug(any())
    }
    
    @Test
    fun testGetLanguages() {
        // 准备测试数据
        val languages = listOf("js", "kotlin", "python")
        
        // 设置模拟对象的行为
        `when`(scriptDebugger.getLanguages()).thenReturn(languages)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/script/languages")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"js\""))
        assert(response.contains("\"kotlin\""))
        assert(response.contains("\"python\""))
        
        // 验证方法调用
        verify(scriptDebugger).getLanguages()
    }
    
    @Test
    fun testGetTemplate() {
        // 准备测试数据
        val language = "js"
        val template = "function execute(params) { return { message: 'Hello, ' + params.name }; }"
        
        // 设置模拟对象的行为
        `when`(scriptDebugger.getTemplate(language)).thenReturn(template)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/script/template")
            .param("language", language)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains(template.replace("\"", "\\\"")))
        
        // 验证方法调用
        verify(scriptDebugger).getTemplate(language)
    }
}
