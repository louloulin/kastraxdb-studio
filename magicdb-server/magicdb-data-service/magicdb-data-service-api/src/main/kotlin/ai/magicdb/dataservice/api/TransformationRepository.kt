package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.TransformationRule
import ai.magicdb.dataservice.api.model.TransformationTemplate

/**
 * 转换存储库接口
 *
 * @author magicdb
 */
interface TransformationRepository {
    /**
     * 保存转换规则
     *
     * @param rule 转换规则
     * @return 规则ID
     */
    fun saveRule(rule: TransformationRule): String
    
    /**
     * 更新转换规则
     *
     * @param rule 转换规则
     * @return 是否成功
     */
    fun updateRule(rule: TransformationRule): Boolean
    
    /**
     * 删除转换规则
     *
     * @param ruleId 规则ID
     * @return 是否成功
     */
    fun deleteRule(ruleId: String): Boolean
    
    /**
     * 获取转换规则
     *
     * @param ruleId 规则ID
     * @return 转换规则
     */
    fun getRule(ruleId: String): TransformationRule?
    
    /**
     * 获取所有转换规则
     *
     * @return 转换规则列表
     */
    fun getAllRules(): List<TransformationRule>
    
    /**
     * 获取转换规则（按源格式和目标格式）
     *
     * @param sourceFormat 源格式
     * @param targetFormat 目标格式
     * @return 转换规则列表
     */
    fun getRulesByFormat(sourceFormat: String, targetFormat: String): List<TransformationRule>
    
    /**
     * 获取转换规则（按标签）
     *
     * @param tag 标签
     * @return 转换规则列表
     */
    fun getRulesByTag(tag: String): List<TransformationRule>
    
    /**
     * 保存转换模板
     *
     * @param template 转换模板
     * @return 模板ID
     */
    fun saveTemplate(template: TransformationTemplate): String
    
    /**
     * 更新转换模板
     *
     * @param template 转换模板
     * @return 是否成功
     */
    fun updateTemplate(template: TransformationTemplate): Boolean
    
    /**
     * 删除转换模板
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    fun deleteTemplate(templateId: String): Boolean
    
    /**
     * 获取转换模板
     *
     * @param templateId 模板ID
     * @return 转换模板
     */
    fun getTemplate(templateId: String): TransformationTemplate?
    
    /**
     * 获取所有转换模板
     *
     * @return 转换模板列表
     */
    fun getAllTemplates(): List<TransformationTemplate>
    
    /**
     * 获取转换模板（按源格式和目标格式）
     *
     * @param sourceFormat 源格式
     * @param targetFormat 目标格式
     * @return 转换模板列表
     */
    fun getTemplatesByFormat(sourceFormat: String, targetFormat: String): List<TransformationTemplate>
    
    /**
     * 获取转换模板（按标签）
     *
     * @param tag 标签
     * @return 转换模板列表
     */
    fun getTemplatesByTag(tag: String): List<TransformationTemplate>
    
    /**
     * 增加模板使用次数
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    fun incrementTemplateUseCount(templateId: String): Boolean
}
