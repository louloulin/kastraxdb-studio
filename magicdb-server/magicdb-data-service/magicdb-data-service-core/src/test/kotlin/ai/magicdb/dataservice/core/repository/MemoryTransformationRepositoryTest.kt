package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.model.TransformationRule
import ai.magicdb.dataservice.api.model.TransformationRuleType
import ai.magicdb.dataservice.api.model.TransformationTemplate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * 内存转换存储库测试
 *
 * @author magicdb
 */
class MemoryTransformationRepositoryTest {
    
    private lateinit var repository: MemoryTransformationRepository
    private lateinit var testRule: TransformationRule
    private lateinit var testTemplate: TransformationTemplate
    
    @BeforeEach
    fun setUp() {
        repository = MemoryTransformationRepository()
        
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
    fun testSaveRule() {
        // 执行方法
        val ruleId = repository.saveRule(testRule)
        
        // 验证结果
        assertEquals("test-rule", ruleId)
        
        // 验证规则已保存
        val savedRule = repository.getRule(ruleId)
        assertNotNull(savedRule)
        assertEquals("测试规则", savedRule!!.name)
    }
    
    @Test
    fun testSaveRuleWithoutId() {
        // 准备测试数据
        val rule = testRule.copy(id = "")
        
        // 执行方法
        val ruleId = repository.saveRule(rule)
        
        // 验证结果
        assertNotNull(ruleId)
        assertNotEquals("", ruleId)
        
        // 验证规则已保存
        val savedRule = repository.getRule(ruleId)
        assertNotNull(savedRule)
        assertEquals("测试规则", savedRule!!.name)
    }
    
    @Test
    fun testUpdateRule() {
        // 保存规则
        repository.saveRule(testRule)
        
        // 准备更新数据
        val updatedRule = testRule.copy(name = "更新后的规则")
        
        // 执行方法
        val result = repository.updateRule(updatedRule)
        
        // 验证结果
        assertTrue(result)
        
        // 验证规则已更新
        val savedRule = repository.getRule(testRule.id)
        assertNotNull(savedRule)
        assertEquals("更新后的规则", savedRule!!.name)
    }
    
    @Test
    fun testUpdateRuleNotFound() {
        // 准备测试数据
        val rule = testRule.copy(id = "not-exist")
        
        // 执行方法
        val result = repository.updateRule(rule)
        
        // 验证结果
        assertFalse(result)
    }
    
    @Test
    fun testDeleteRule() {
        // 保存规则
        repository.saveRule(testRule)
        
        // 执行方法
        val result = repository.deleteRule(testRule.id)
        
        // 验证结果
        assertTrue(result)
        
        // 验证规则已删除
        val savedRule = repository.getRule(testRule.id)
        assertNull(savedRule)
    }
    
    @Test
    fun testDeleteRuleNotFound() {
        // 执行方法
        val result = repository.deleteRule("not-exist")
        
        // 验证结果
        assertFalse(result)
    }
    
    @Test
    fun testGetRule() {
        // 保存规则
        repository.saveRule(testRule)
        
        // 执行方法
        val rule = repository.getRule(testRule.id)
        
        // 验证结果
        assertNotNull(rule)
        assertEquals("测试规则", rule!!.name)
    }
    
    @Test
    fun testGetRuleNotFound() {
        // 执行方法
        val rule = repository.getRule("not-exist")
        
        // 验证结果
        assertNull(rule)
    }
    
    @Test
    fun testGetAllRules() {
        // 保存规则
        repository.saveRule(testRule)
        
        // 执行方法
        val rules = repository.getAllRules()
        
        // 验证结果
        assertEquals(1, rules.size)
        assertEquals("测试规则", rules[0].name)
    }
    
    @Test
    fun testGetRulesByFormat() {
        // 保存规则
        repository.saveRule(testRule)
        
        // 执行方法
        val rules = repository.getRulesByFormat("json", "xml")
        
        // 验证结果
        assertEquals(1, rules.size)
        assertEquals("测试规则", rules[0].name)
    }
    
    @Test
    fun testGetRulesByFormatNotFound() {
        // 保存规则
        repository.saveRule(testRule)
        
        // 执行方法
        val rules = repository.getRulesByFormat("xml", "json")
        
        // 验证结果
        assertEquals(0, rules.size)
    }
    
    @Test
    fun testGetRulesByTag() {
        // 保存规则
        repository.saveRule(testRule)
        
        // 执行方法
        val rules = repository.getRulesByTag("test")
        
        // 验证结果
        assertEquals(1, rules.size)
        assertEquals("测试规则", rules[0].name)
    }
    
    @Test
    fun testGetRulesByTagNotFound() {
        // 保存规则
        repository.saveRule(testRule)
        
        // 执行方法
        val rules = repository.getRulesByTag("not-exist")
        
        // 验证结果
        assertEquals(0, rules.size)
    }
    
    @Test
    fun testSaveTemplate() {
        // 执行方法
        val templateId = repository.saveTemplate(testTemplate)
        
        // 验证结果
        assertEquals("test-template", templateId)
        
        // 验证模板已保存
        val savedTemplate = repository.getTemplate(templateId)
        assertNotNull(savedTemplate)
        assertEquals("测试模板", savedTemplate!!.name)
    }
    
    @Test
    fun testSaveTemplateWithoutId() {
        // 准备测试数据
        val template = testTemplate.copy(id = "")
        
        // 执行方法
        val templateId = repository.saveTemplate(template)
        
        // 验证结果
        assertNotNull(templateId)
        assertNotEquals("", templateId)
        
        // 验证模板已保存
        val savedTemplate = repository.getTemplate(templateId)
        assertNotNull(savedTemplate)
        assertEquals("测试模板", savedTemplate!!.name)
    }
    
    @Test
    fun testUpdateTemplate() {
        // 保存模板
        repository.saveTemplate(testTemplate)
        
        // 准备更新数据
        val updatedTemplate = testTemplate.copy(name = "更新后的模板")
        
        // 执行方法
        val result = repository.updateTemplate(updatedTemplate)
        
        // 验证结果
        assertTrue(result)
        
        // 验证模板已更新
        val savedTemplate = repository.getTemplate(testTemplate.id)
        assertNotNull(savedTemplate)
        assertEquals("更新后的模板", savedTemplate!!.name)
    }
    
    @Test
    fun testUpdateTemplateNotFound() {
        // 准备测试数据
        val template = testTemplate.copy(id = "not-exist")
        
        // 执行方法
        val result = repository.updateTemplate(template)
        
        // 验证结果
        assertFalse(result)
    }
    
    @Test
    fun testDeleteTemplate() {
        // 保存模板
        repository.saveTemplate(testTemplate)
        
        // 执行方法
        val result = repository.deleteTemplate(testTemplate.id)
        
        // 验证结果
        assertTrue(result)
        
        // 验证模板已删除
        val savedTemplate = repository.getTemplate(testTemplate.id)
        assertNull(savedTemplate)
    }
    
    @Test
    fun testDeleteTemplateNotFound() {
        // 执行方法
        val result = repository.deleteTemplate("not-exist")
        
        // 验证结果
        assertFalse(result)
    }
    
    @Test
    fun testGetTemplate() {
        // 保存模板
        repository.saveTemplate(testTemplate)
        
        // 执行方法
        val template = repository.getTemplate(testTemplate.id)
        
        // 验证结果
        assertNotNull(template)
        assertEquals("测试模板", template!!.name)
    }
    
    @Test
    fun testGetTemplateNotFound() {
        // 执行方法
        val template = repository.getTemplate("not-exist")
        
        // 验证结果
        assertNull(template)
    }
    
    @Test
    fun testGetAllTemplates() {
        // 保存模板
        repository.saveTemplate(testTemplate)
        
        // 执行方法
        val templates = repository.getAllTemplates()
        
        // 验证结果
        assertEquals(1, templates.size)
        assertEquals("测试模板", templates[0].name)
    }
    
    @Test
    fun testGetTemplatesByFormat() {
        // 保存模板
        repository.saveTemplate(testTemplate)
        
        // 执行方法
        val templates = repository.getTemplatesByFormat("json", "xml")
        
        // 验证结果
        assertEquals(1, templates.size)
        assertEquals("测试模板", templates[0].name)
    }
    
    @Test
    fun testGetTemplatesByFormatNotFound() {
        // 保存模板
        repository.saveTemplate(testTemplate)
        
        // 执行方法
        val templates = repository.getTemplatesByFormat("xml", "json")
        
        // 验证结果
        assertEquals(0, templates.size)
    }
    
    @Test
    fun testGetTemplatesByTag() {
        // 保存模板
        repository.saveTemplate(testTemplate)
        
        // 执行方法
        val templates = repository.getTemplatesByTag("test")
        
        // 验证结果
        assertEquals(1, templates.size)
        assertEquals("测试模板", templates[0].name)
    }
    
    @Test
    fun testGetTemplatesByTagNotFound() {
        // 保存模板
        repository.saveTemplate(testTemplate)
        
        // 执行方法
        val templates = repository.getTemplatesByTag("not-exist")
        
        // 验证结果
        assertEquals(0, templates.size)
    }
    
    @Test
    fun testIncrementTemplateUseCount() {
        // 保存模板
        repository.saveTemplate(testTemplate)
        
        // 执行方法
        val result = repository.incrementTemplateUseCount(testTemplate.id)
        
        // 验证结果
        assertTrue(result)
        
        // 验证使用次数已增加
        val savedTemplate = repository.getTemplate(testTemplate.id)
        assertNotNull(savedTemplate)
        assertEquals(11, savedTemplate!!.useCount)
    }
    
    @Test
    fun testIncrementTemplateUseCountNotFound() {
        // 执行方法
        val result = repository.incrementTemplateUseCount("not-exist")
        
        // 验证结果
        assertFalse(result)
    }
}
