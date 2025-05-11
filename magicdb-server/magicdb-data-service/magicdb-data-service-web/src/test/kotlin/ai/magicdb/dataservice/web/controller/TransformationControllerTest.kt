package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.DataTransformer
import ai.magicdb.dataservice.api.TransformationRepository
import ai.magicdb.dataservice.api.model.TransformationRequest
import ai.magicdb.dataservice.api.model.TransformationResult
import ai.magicdb.dataservice.api.model.TransformationRule
import ai.magicdb.dataservice.api.model.TransformationRuleType
import ai.magicdb.dataservice.api.model.TransformationTemplate
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * 数据转换控制器测试
 *
 * @author magicdb
 */
@WebMvcTest(TransformationController::class)
class TransformationControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockBean
    private lateinit var dataTransformer: DataTransformer
    
    @MockBean
    private lateinit var transformationRepository: TransformationRepository
    
    private lateinit var testRule: TransformationRule
    private lateinit var testTemplate: TransformationTemplate
    
    @BeforeEach
    fun setUp() {
        // 创建测试规则
        testRule = TransformationRule(
            id = "test-rule",
            name = "测试规则",
            description = "测试规则描述",
            type = TransformationRuleType.FIELD_MAPPING,
            sourceFormat = "json",
            targetFormat = "xml",
            config = mapOf(
                "fieldMappings" to mapOf(
                    "sourceField" to "targetField"
                )
            ),
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            enabled = true,
            tags = listOf("test", "demo")
        )
        
        // 创建测试模板
        testTemplate = TransformationTemplate(
            id = "test-template",
            name = "测试模板",
            description = "测试模板描述",
            sourceFormat = "json",
            targetFormat = "xml",
            transformationType = "default",
            rules = mapOf(
                "fieldMappings" to mapOf(
                    "sourceField" to "targetField"
                )
            ),
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            enabled = true,
            tags = listOf("test", "demo"),
            useCount = 10
        )
    }
    
    @Test
    fun testTransform() {
        // 准备测试数据
        val request = TransformationRequest(
            sourceData = mapOf("name" to "测试"),
            sourceFormat = "json",
            targetFormat = "xml"
        )
        
        val result = TransformationResult.success(
            targetData = "<root><name>测试</name></root>",
            targetFormat = "xml"
        )
        
        // 设置模拟对象的行为
        `when`(dataTransformer.transform(any())).thenReturn(result)
        
        // 执行请求
        mockMvc.perform(post("/api/data-service/transform")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("<root><name>测试</name></root>"))
        
        // 验证方法调用
        verify(dataTransformer).transform(any())
    }
    
    @Test
    fun testGetSourceFormats() {
        // 设置模拟对象的行为
        `when`(dataTransformer.getSupportedSourceFormats()).thenReturn(listOf("json", "xml", "csv", "yaml"))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/transform/source-formats")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0]").value("json"))
            .andExpect(jsonPath("$.data[1]").value("xml"))
            .andExpect(jsonPath("$.data[2]").value("csv"))
            .andExpect(jsonPath("$.data[3]").value("yaml"))
        
        // 验证方法调用
        verify(dataTransformer).getSupportedSourceFormats()
    }
    
    @Test
    fun testGetTargetFormats() {
        // 设置模拟对象的行为
        `when`(dataTransformer.getSupportedTargetFormats()).thenReturn(listOf("json", "xml", "csv", "yaml"))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/transform/target-formats")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0]").value("json"))
            .andExpect(jsonPath("$.data[1]").value("xml"))
            .andExpect(jsonPath("$.data[2]").value("csv"))
            .andExpect(jsonPath("$.data[3]").value("yaml"))
        
        // 验证方法调用
        verify(dataTransformer).getSupportedTargetFormats()
    }
    
    @Test
    fun testGetTransformationTypes() {
        // 设置模拟对象的行为
        `when`(dataTransformer.getSupportedTransformationTypes()).thenReturn(listOf("default", "script", "template", "mapping"))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/transform/transformation-types")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0]").value("default"))
            .andExpect(jsonPath("$.data[1]").value("script"))
            .andExpect(jsonPath("$.data[2]").value("template"))
            .andExpect(jsonPath("$.data[3]").value("mapping"))
        
        // 验证方法调用
        verify(dataTransformer).getSupportedTransformationTypes()
    }
    
    @Test
    fun testValidateRules() {
        // 准备测试数据
        val rules = mapOf(
            "fieldMappings" to mapOf(
                "sourceField" to "targetField"
            )
        )
        
        // 设置模拟对象的行为
        `when`(dataTransformer.validateRules(any(), eq("json"), eq("xml"))).thenReturn(emptyList())
        
        // 执行请求
        mockMvc.perform(post("/api/data-service/transform/validate")
            .param("sourceFormat", "json")
            .param("targetFormat", "xml")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rules)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data").isEmpty)
        
        // 验证方法调用
        verify(dataTransformer).validateRules(any(), eq("json"), eq("xml"))
    }
    
    @Test
    fun testGetRuleTemplate() {
        // 准备测试数据
        val template = mapOf(
            "fieldMappings" to mapOf(
                "sourceField" to "targetField"
            )
        )
        
        // 设置模拟对象的行为
        `when`(dataTransformer.getRuleTemplate("json", "xml")).thenReturn(template)
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/transform/rule-template")
            .param("sourceFormat", "json")
            .param("targetFormat", "xml")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.fieldMappings.sourceField").value("targetField"))
        
        // 验证方法调用
        verify(dataTransformer).getRuleTemplate("json", "xml")
    }
    
    @Test
    fun testCreateRule() {
        // 设置模拟对象的行为
        `when`(transformationRepository.saveRule(any())).thenReturn("test-rule")
        
        // 执行请求
        mockMvc.perform(post("/api/data-service/transform/rules")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRule)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("test-rule"))
        
        // 验证方法调用
        verify(transformationRepository).saveRule(any())
    }
    
    @Test
    fun testUpdateRule() {
        // 设置模拟对象的行为
        `when`(transformationRepository.updateRule(any())).thenReturn(true)
        
        // 执行请求
        mockMvc.perform(put("/api/data-service/transform/rules")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRule)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
        
        // 验证方法调用
        verify(transformationRepository).updateRule(any())
    }
    
    @Test
    fun testDeleteRule() {
        // 设置模拟对象的行为
        `when`(transformationRepository.deleteRule("test-rule")).thenReturn(true)
        
        // 执行请求
        mockMvc.perform(delete("/api/data-service/transform/rules/test-rule")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
        
        // 验证方法调用
        verify(transformationRepository).deleteRule("test-rule")
    }
    
    @Test
    fun testGetRule() {
        // 设置模拟对象的行为
        `when`(transformationRepository.getRule("test-rule")).thenReturn(testRule)
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/transform/rules/test-rule")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("test-rule"))
            .andExpect(jsonPath("$.data.name").value("测试规则"))
        
        // 验证方法调用
        verify(transformationRepository).getRule("test-rule")
    }
    
    @Test
    fun testGetAllRules() {
        // 设置模拟对象的行为
        `when`(transformationRepository.getAllRules()).thenReturn(listOf(testRule))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/transform/rules")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("test-rule"))
            .andExpect(jsonPath("$.data[0].name").value("测试规则"))
        
        // 验证方法调用
        verify(transformationRepository).getAllRules()
    }
    
    @Test
    fun testGetRulesByFormat() {
        // 设置模拟对象的行为
        `when`(transformationRepository.getRulesByFormat("json", "xml")).thenReturn(listOf(testRule))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/transform/rules/by-format")
            .param("sourceFormat", "json")
            .param("targetFormat", "xml")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("test-rule"))
            .andExpect(jsonPath("$.data[0].name").value("测试规则"))
        
        // 验证方法调用
        verify(transformationRepository).getRulesByFormat("json", "xml")
    }
    
    @Test
    fun testGetRulesByTag() {
        // 设置模拟对象的行为
        `when`(transformationRepository.getRulesByTag("test")).thenReturn(listOf(testRule))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/transform/rules/by-tag")
            .param("tag", "test")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("test-rule"))
            .andExpect(jsonPath("$.data[0].name").value("测试规则"))
        
        // 验证方法调用
        verify(transformationRepository).getRulesByTag("test")
    }
    
    @Test
    fun testCreateTemplate() {
        // 设置模拟对象的行为
        `when`(transformationRepository.saveTemplate(any())).thenReturn("test-template")
        
        // 执行请求
        mockMvc.perform(post("/api/data-service/transform/templates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTemplate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("test-template"))
        
        // 验证方法调用
        verify(transformationRepository).saveTemplate(any())
    }
    
    @Test
    fun testUpdateTemplate() {
        // 设置模拟对象的行为
        `when`(transformationRepository.updateTemplate(any())).thenReturn(true)
        
        // 执行请求
        mockMvc.perform(put("/api/data-service/transform/templates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTemplate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
        
        // 验证方法调用
        verify(transformationRepository).updateTemplate(any())
    }
    
    @Test
    fun testDeleteTemplate() {
        // 设置模拟对象的行为
        `when`(transformationRepository.deleteTemplate("test-template")).thenReturn(true)
        
        // 执行请求
        mockMvc.perform(delete("/api/data-service/transform/templates/test-template")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
        
        // 验证方法调用
        verify(transformationRepository).deleteTemplate("test-template")
    }
    
    @Test
    fun testGetTemplate() {
        // 设置模拟对象的行为
        `when`(transformationRepository.getTemplate("test-template")).thenReturn(testTemplate)
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/transform/templates/test-template")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("test-template"))
            .andExpect(jsonPath("$.data.name").value("测试模板"))
        
        // 验证方法调用
        verify(transformationRepository).getTemplate("test-template")
    }
    
    @Test
    fun testGetAllTemplates() {
        // 设置模拟对象的行为
        `when`(transformationRepository.getAllTemplates()).thenReturn(listOf(testTemplate))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/transform/templates")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("test-template"))
            .andExpect(jsonPath("$.data[0].name").value("测试模板"))
        
        // 验证方法调用
        verify(transformationRepository).getAllTemplates()
    }
    
    @Test
    fun testGetTemplatesByFormat() {
        // 设置模拟对象的行为
        `when`(transformationRepository.getTemplatesByFormat("json", "xml")).thenReturn(listOf(testTemplate))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/transform/templates/by-format")
            .param("sourceFormat", "json")
            .param("targetFormat", "xml")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("test-template"))
            .andExpect(jsonPath("$.data[0].name").value("测试模板"))
        
        // 验证方法调用
        verify(transformationRepository).getTemplatesByFormat("json", "xml")
    }
    
    @Test
    fun testGetTemplatesByTag() {
        // 设置模拟对象的行为
        `when`(transformationRepository.getTemplatesByTag("test")).thenReturn(listOf(testTemplate))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/transform/templates/by-tag")
            .param("tag", "test")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("test-template"))
            .andExpect(jsonPath("$.data[0].name").value("测试模板"))
        
        // 验证方法调用
        verify(transformationRepository).getTemplatesByTag("test")
    }
    
    @Test
    fun testIncrementTemplateUseCount() {
        // 设置模拟对象的行为
        `when`(transformationRepository.incrementTemplateUseCount("test-template")).thenReturn(true)
        
        // 执行请求
        mockMvc.perform(post("/api/data-service/transform/templates/test-template/increment-use-count")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
        
        // 验证方法调用
        verify(transformationRepository).incrementTemplateUseCount("test-template")
    }
}
