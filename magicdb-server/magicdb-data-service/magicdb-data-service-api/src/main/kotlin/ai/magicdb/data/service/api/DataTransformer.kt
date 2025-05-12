package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.TransformationRequest
import ai.magicdb.data.service.api.model.TransformationResult

/**
 * 数据转换器接口
 *
 * @author magicdb
 */
interface DataTransformer {
    /**
     * 转换数据
     *
     * @param request 转换请求
     * @return 转换结果
     */
    fun transform(request: TransformationRequest): TransformationResult
    
    /**
     * 获取支持的源格式
     *
     * @return 源格式列表
     */
    fun getSupportedSourceFormats(): List<String>
    
    /**
     * 获取支持的目标格式
     *
     * @return 目标格式列表
     */
    fun getSupportedTargetFormats(): List<String>
    
    /**
     * 获取支持的转换类型
     *
     * @return 转换类型列表
     */
    fun getSupportedTransformationTypes(): List<String>
    
    /**
     * 验证转换规则
     *
     * @param rules 转换规则
     * @param sourceFormat 源格式
     * @param targetFormat 目标格式
     * @return 验证结果，如果为空则表示验证通过
     */
    fun validateRules(rules: Map<String, Any?>, sourceFormat: String, targetFormat: String): List<String>
    
    /**
     * 获取转换规则模板
     *
     * @param sourceFormat 源格式
     * @param targetFormat 目标格式
     * @return 转换规则模板
     */
    fun getRuleTemplate(sourceFormat: String, targetFormat: String): Map<String, Any?>
}
