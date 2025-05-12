package ai.magicdb.data.service.core.repository

import ai.magicdb.data.service.api.TransformationRepository
import ai.magicdb.data.service.api.model.TransformationRule
import ai.magicdb.data.service.api.model.TransformationTemplate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 内存转换存储库实现
 *
 * @author magicdb
 */
@Repository
class MemoryTransformationRepository : TransformationRepository {
    private val logger = LoggerFactory.getLogger(MemoryTransformationRepository::class.java)
    
    // 规则存储
    private val rules = ConcurrentHashMap<String, TransformationRule>()
    
    // 模板存储
    private val templates = ConcurrentHashMap<String, TransformationTemplate>()
    
    override fun saveRule(rule: TransformationRule): String {
        try {
            // 生成ID
            if (rule.id.isBlank()) {
                rule.id = UUID.randomUUID().toString()
            }
            
            // 设置时间
            val now = System.currentTimeMillis()
            rule.createTime = now
            rule.updateTime = now
            
            // 保存规则
            rules[rule.id] = rule
            
            return rule.id
        } catch (e: Exception) {
            logger.error("保存转换规则失败: {}", rule.name, e)
            throw e
        }
    }
    
    override fun updateRule(rule: TransformationRule): Boolean {
        try {
            // 检查规则是否存在
            if (!rules.containsKey(rule.id)) {
                return false
            }
            
            // 设置更新时间
            rule.updateTime = System.currentTimeMillis()
            
            // 更新规则
            rules[rule.id] = rule
            
            return true
        } catch (e: Exception) {
            logger.error("更新转换规则失败: {}", rule.id, e)
            return false
        }
    }
    
    override fun deleteRule(ruleId: String): Boolean {
        try {
            // 删除规则
            return rules.remove(ruleId) != null
        } catch (e: Exception) {
            logger.error("删除转换规则失败: {}", ruleId, e)
            return false
        }
    }
    
    override fun getRule(ruleId: String): TransformationRule? {
        try {
            // 获取规则
            return rules[ruleId]
        } catch (e: Exception) {
            logger.error("获取转换规则失败: {}", ruleId, e)
            return null
        }
    }
    
    override fun getAllRules(): List<TransformationRule> {
        try {
            // 获取所有规则
            return rules.values.toList()
        } catch (e: Exception) {
            logger.error("获取所有转换规则失败", e)
            return emptyList()
        }
    }
    
    override fun getRulesByFormat(sourceFormat: String, targetFormat: String): List<TransformationRule> {
        try {
            // 获取指定格式的规则
            return rules.values.filter { 
                it.sourceFormat.equals(sourceFormat, ignoreCase = true) && 
                it.targetFormat.equals(targetFormat, ignoreCase = true)
            }
        } catch (e: Exception) {
            logger.error("获取格式转换规则失败: {} -> {}", sourceFormat, targetFormat, e)
            return emptyList()
        }
    }
    
    override fun getRulesByTag(tag: String): List<TransformationRule> {
        try {
            // 获取指定标签的规则
            return rules.values.filter { it.tags.contains(tag) }
        } catch (e: Exception) {
            logger.error("获取标签转换规则失败: {}", tag, e)
            return emptyList()
        }
    }
    
    override fun saveTemplate(template: TransformationTemplate): String {
        try {
            // 生成ID
            if (template.id.isBlank()) {
                template.id = UUID.randomUUID().toString()
            }
            
            // 设置时间
            val now = System.currentTimeMillis()
            template.createTime = now
            template.updateTime = now
            
            // 保存模板
            templates[template.id] = template
            
            return template.id
        } catch (e: Exception) {
            logger.error("保存转换模板失败: {}", template.name, e)
            throw e
        }
    }
    
    override fun updateTemplate(template: TransformationTemplate): Boolean {
        try {
            // 检查模板是否存在
            if (!templates.containsKey(template.id)) {
                return false
            }
            
            // 设置更新时间
            template.updateTime = System.currentTimeMillis()
            
            // 更新模板
            templates[template.id] = template
            
            return true
        } catch (e: Exception) {
            logger.error("更新转换模板失败: {}", template.id, e)
            return false
        }
    }
    
    override fun deleteTemplate(templateId: String): Boolean {
        try {
            // 删除模板
            return templates.remove(templateId) != null
        } catch (e: Exception) {
            logger.error("删除转换模板失败: {}", templateId, e)
            return false
        }
    }
    
    override fun getTemplate(templateId: String): TransformationTemplate? {
        try {
            // 获取模板
            return templates[templateId]
        } catch (e: Exception) {
            logger.error("获取转换模板失败: {}", templateId, e)
            return null
        }
    }
    
    override fun getAllTemplates(): List<TransformationTemplate> {
        try {
            // 获取所有模板
            return templates.values.toList()
        } catch (e: Exception) {
            logger.error("获取所有转换模板失败", e)
            return emptyList()
        }
    }
    
    override fun getTemplatesByFormat(sourceFormat: String, targetFormat: String): List<TransformationTemplate> {
        try {
            // 获取指定格式的模板
            return templates.values.filter { 
                it.sourceFormat.equals(sourceFormat, ignoreCase = true) && 
                it.targetFormat.equals(targetFormat, ignoreCase = true)
            }
        } catch (e: Exception) {
            logger.error("获取格式转换模板失败: {} -> {}", sourceFormat, targetFormat, e)
            return emptyList()
        }
    }
    
    override fun getTemplatesByTag(tag: String): List<TransformationTemplate> {
        try {
            // 获取指定标签的模板
            return templates.values.filter { it.tags.contains(tag) }
        } catch (e: Exception) {
            logger.error("获取标签转换模板失败: {}", tag, e)
            return emptyList()
        }
    }
    
    override fun incrementTemplateUseCount(templateId: String): Boolean {
        try {
            // 获取模板
            val template = templates[templateId] ?: return false
            
            // 增加使用次数
            template.useCount++
            
            // 更新模板
            templates[templateId] = template
            
            return true
        } catch (e: Exception) {
            logger.error("增加模板使用次数失败: {}", templateId, e)
            return false
        }
    }
}
