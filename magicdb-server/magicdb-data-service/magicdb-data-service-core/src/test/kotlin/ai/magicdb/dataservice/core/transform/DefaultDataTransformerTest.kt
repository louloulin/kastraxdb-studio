package ai.magicdb.dataservice.core.transform

import ai.magicdb.dataservice.api.model.TransformationRequest
import ai.magicdb.dataservice.core.script.ScriptExecutor
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.junit.jupiter.api.Assertions.*

/**
 * 默认数据转换器测试
 *
 * @author magicdb
 */
class DefaultDataTransformerTest {
    
    private lateinit var dataTransformer: DefaultDataTransformer
    private lateinit var scriptExecutor: ScriptExecutor
    private lateinit var objectMapper: ObjectMapper
    
    @BeforeEach
    fun setUp() {
        scriptExecutor = mock(ScriptExecutor::class.java)
        objectMapper = ObjectMapper()
        dataTransformer = DefaultDataTransformer(scriptExecutor, objectMapper)
    }
    
    @Test
    fun testTransformJsonToJson() {
        // 准备测试数据
        val sourceData = """{"name":"测试","age":30}"""
        val request = TransformationRequest(
            sourceData = sourceData,
            sourceFormat = "json",
            targetFormat = "json"
        )
        
        // 执行转换
        val result = dataTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("json", result.targetFormat)
    }
    
    @Test
    fun testTransformJsonToXml() {
        // 准备测试数据
        val sourceData = """{"name":"测试","age":30}"""
        val request = TransformationRequest(
            sourceData = sourceData,
            sourceFormat = "json",
            targetFormat = "xml",
            options = mapOf("pretty" to true)
        )
        
        // 执行转换
        val result = dataTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("xml", result.targetFormat)
        assertTrue((result.targetData as String).contains("<name>测试</name>"))
        assertTrue((result.targetData as String).contains("<age>30</age>"))
    }
    
    @Test
    fun testTransformWithFieldMapping() {
        // 准备测试数据
        val sourceData = """{"name":"测试","age":30}"""
        val request = TransformationRequest(
            sourceData = sourceData,
            sourceFormat = "json",
            targetFormat = "json",
            rules = mapOf(
                "fieldMappings" to mapOf(
                    "name" to "username",
                    "age" to "userAge"
                )
            )
        )
        
        // 执行转换
        val result = dataTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("json", result.targetFormat)
        
        // 解析结果
        val resultJson = objectMapper.readTree(result.targetData as String)
        assertTrue(resultJson.has("username"))
        assertTrue(resultJson.has("userAge"))
        assertEquals("测试", resultJson.get("username").asText())
        assertEquals(30, resultJson.get("userAge").asInt())
    }
    
    @Test
    fun testTransformWithValueConverter() {
        // 准备测试数据
        val sourceData = """{"name":"test","age":30}"""
        val request = TransformationRequest(
            sourceData = sourceData,
            sourceFormat = "json",
            targetFormat = "json",
            rules = mapOf(
                "valueConverters" to mapOf(
                    "name" to mapOf(
                        "type" to "uppercase"
                    )
                )
            )
        )
        
        // 执行转换
        val result = dataTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("json", result.targetFormat)
        
        // 解析结果
        val resultJson = objectMapper.readTree(result.targetData as String)
        assertEquals("TEST", resultJson.get("name").asText())
        assertEquals(30, resultJson.get("age").asInt())
    }
    
    @Test
    fun testTransformWithIncludeFields() {
        // 准备测试数据
        val sourceData = """{"name":"测试","age":30,"email":"test@example.com"}"""
        val request = TransformationRequest(
            sourceData = sourceData,
            sourceFormat = "json",
            targetFormat = "json",
            rules = mapOf(
                "includeFields" to listOf("name", "age")
            )
        )
        
        // 执行转换
        val result = dataTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("json", result.targetFormat)
        
        // 解析结果
        val resultJson = objectMapper.readTree(result.targetData as String)
        assertTrue(resultJson.has("name"))
        assertTrue(resultJson.has("age"))
        assertFalse(resultJson.has("email"))
    }
    
    @Test
    fun testTransformWithExcludeFields() {
        // 准备测试数据
        val sourceData = """{"name":"测试","age":30,"email":"test@example.com"}"""
        val request = TransformationRequest(
            sourceData = sourceData,
            sourceFormat = "json",
            targetFormat = "json",
            rules = mapOf(
                "excludeFields" to listOf("email")
            )
        )
        
        // 执行转换
        val result = dataTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("json", result.targetFormat)
        
        // 解析结果
        val resultJson = objectMapper.readTree(result.targetData as String)
        assertTrue(resultJson.has("name"))
        assertTrue(resultJson.has("age"))
        assertFalse(resultJson.has("email"))
    }
    
    @Test
    fun testGetSupportedSourceFormats() {
        // 执行方法
        val formats = dataTransformer.getSupportedSourceFormats()
        
        // 验证结果
        assertTrue(formats.contains("json"))
        assertTrue(formats.contains("xml"))
        assertTrue(formats.contains("csv"))
        assertTrue(formats.contains("yaml"))
    }
    
    @Test
    fun testGetSupportedTargetFormats() {
        // 执行方法
        val formats = dataTransformer.getSupportedTargetFormats()
        
        // 验证结果
        assertTrue(formats.contains("json"))
        assertTrue(formats.contains("xml"))
        assertTrue(formats.contains("csv"))
        assertTrue(formats.contains("yaml"))
    }
    
    @Test
    fun testGetSupportedTransformationTypes() {
        // 执行方法
        val types = dataTransformer.getSupportedTransformationTypes()
        
        // 验证结果
        assertTrue(types.contains("default"))
        assertTrue(types.contains("script"))
        assertTrue(types.contains("template"))
        assertTrue(types.contains("mapping"))
    }
    
    @Test
    fun testValidateRules() {
        // 准备测试数据
        val rules = mapOf(
            "fieldMappings" to mapOf(
                "sourceField" to "targetField"
            )
        )
        
        // 执行方法
        val errors = dataTransformer.validateRules(rules, "json", "xml")
        
        // 验证结果
        assertTrue(errors.isEmpty())
    }
    
    @Test
    fun testValidateRulesWithErrors() {
        // 准备测试数据
        val rules = mapOf(
            "fieldMappings" to mapOf(
                "sourceField" to ""
            )
        )
        
        // 执行方法
        val errors = dataTransformer.validateRules(rules, "json", "xml")
        
        // 验证结果
        assertFalse(errors.isEmpty())
    }
    
    @Test
    fun testGetRuleTemplate() {
        // 执行方法
        val template = dataTransformer.getRuleTemplate("json", "xml")
        
        // 验证结果
        assertNotNull(template)
        assertTrue(template.containsKey("fieldMappings"))
        assertTrue(template.containsKey("valueConverters"))
    }
    
    @Test
    fun testUnsupportedFormat() {
        // 准备测试数据
        val sourceData = """{"name":"测试","age":30}"""
        val request = TransformationRequest(
            sourceData = sourceData,
            sourceFormat = "json",
            targetFormat = "unsupported"
        )
        
        // 执行转换
        val result = dataTransformer.transform(request)
        
        // 验证结果
        assertFalse(result.success)
        assertNotNull(result.errorMessage)
        assertTrue(result.errorMessage!!.contains("不支持的转换格式"))
    }
}
