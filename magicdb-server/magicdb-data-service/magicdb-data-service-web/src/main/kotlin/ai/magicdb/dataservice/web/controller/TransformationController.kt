package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.DataTransformer
import ai.magicdb.dataservice.api.TransformationRepository
import ai.magicdb.dataservice.api.model.TransformationRequest
import ai.magicdb.dataservice.api.model.TransformationRule
import ai.magicdb.dataservice.api.model.TransformationTemplate
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

/**
 * 数据转换控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/transform")
class TransformationController(
    private val dataTransformer: DataTransformer,
    private val transformationRepository: TransformationRepository
) {
    private val logger = LoggerFactory.getLogger(TransformationController::class.java)

    /**
     * 转换数据
     *
     * @param request 转换请求
     * @return 转换结果
     */
    @PostMapping
    fun transform(@RequestBody request: TransformationRequest): DataResult<Any> {
        logger.info("转换数据: {} -> {}", request.sourceFormat, request.targetFormat)
        val result = dataTransformer.transform(request)

        return if (result.success) {
            DataResult.of(result.targetData ?: "")
        } else {
            DataResult.error("transform.error", result.errorMessage ?: "转换失败")
        }
    }

    /**
     * 获取支持的源格式
     *
     * @return 源格式列表
     */
    @GetMapping("/source-formats")
    fun getSourceFormats(): ListResult<String> {
        logger.info("获取支持的源格式")
        val formats = dataTransformer.getSupportedSourceFormats()
        return ListResult.of(formats)
    }

    /**
     * 获取支持的目标格式
     *
     * @return 目标格式列表
     */
    @GetMapping("/target-formats")
    fun getTargetFormats(): ListResult<String> {
        logger.info("获取支持的目标格式")
        val formats = dataTransformer.getSupportedTargetFormats()
        return ListResult.of(formats)
    }

    /**
     * 获取支持的转换类型
     *
     * @return 转换类型列表
     */
    @GetMapping("/transformation-types")
    fun getTransformationTypes(): ListResult<String> {
        logger.info("获取支持的转换类型")
        val types = dataTransformer.getSupportedTransformationTypes()
        return ListResult.of(types)
    }

    /**
     * 验证转换规则
     *
     * @param sourceFormat 源格式
     * @param targetFormat 目标格式
     * @param rules 转换规则
     * @return 验证结果
     */
    @PostMapping("/validate")
    fun validateRules(
        @RequestParam sourceFormat: String,
        @RequestParam targetFormat: String,
        @RequestBody rules: Map<String, Any?>
    ): ListResult<String> {
        logger.info("验证转换规则: {} -> {}", sourceFormat, targetFormat)
        val errors = dataTransformer.validateRules(rules, sourceFormat, targetFormat)
        return ListResult.of(errors)
    }

    /**
     * 获取转换规则模板
     *
     * @param sourceFormat 源格式
     * @param targetFormat 目标格式
     * @return 转换规则模板
     */
    @GetMapping("/rule-template")
    fun getRuleTemplate(
        @RequestParam sourceFormat: String,
        @RequestParam targetFormat: String
    ): DataResult<Map<String, Any?>> {
        logger.info("获取转换规则模板: {} -> {}", sourceFormat, targetFormat)
        val template = dataTransformer.getRuleTemplate(sourceFormat, targetFormat)
        return DataResult.of(template)
    }

    /**
     * 创建转换规则
     *
     * @param rule 转换规则
     * @return 规则ID
     */
    @PostMapping("/rules")
    fun createRule(@RequestBody rule: TransformationRule): DataResult<String> {
        logger.info("创建转换规则: {}", rule.name)
        val ruleId = transformationRepository.saveRule(rule)
        return DataResult.of(ruleId)
    }

    /**
     * 更新转换规则
     *
     * @param rule 转换规则
     * @return 是否成功
     */
    @PutMapping("/rules")
    fun updateRule(@RequestBody rule: TransformationRule): ActionResult {
        logger.info("更新转换规则: {}", rule.id)
        val success = transformationRepository.updateRule(rule)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("rule.not.found", "转换规则不存在", "")
        }
    }

    /**
     * 删除转换规则
     *
     * @param ruleId 规则ID
     * @return 是否成功
     */
    @DeleteMapping("/rules/{ruleId}")
    fun deleteRule(@PathVariable ruleId: String): ActionResult {
        logger.info("删除转换规则: {}", ruleId)
        val success = transformationRepository.deleteRule(ruleId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("rule.not.found", "转换规则不存在", "")
        }
    }

    /**
     * 获取转换规则
     *
     * @param ruleId 规则ID
     * @return 转换规则
     */
    @GetMapping("/rules/{ruleId}")
    fun getRule(@PathVariable ruleId: String): DataResult<TransformationRule> {
        logger.info("获取转换规则: {}", ruleId)
        val rule = transformationRepository.getRule(ruleId)
        return if (rule != null) {
            DataResult.of(rule)
        } else {
            DataResult.error("rule.not.found", "转换规则不存在")
        }
    }

    /**
     * 获取所有转换规则
     *
     * @return 转换规则列表
     */
    @GetMapping("/rules")
    fun getAllRules(): ListResult<TransformationRule> {
        logger.info("获取所有转换规则")
        val rules = transformationRepository.getAllRules()
        return ListResult.of(rules)
    }

    /**
     * 获取转换规则（按格式）
     *
     * @param sourceFormat 源格式
     * @param targetFormat 目标格式
     * @return 转换规则列表
     */
    @GetMapping("/rules/by-format")
    fun getRulesByFormat(
        @RequestParam sourceFormat: String,
        @RequestParam targetFormat: String
    ): ListResult<TransformationRule> {
        logger.info("获取转换规则（按格式）: {} -> {}", sourceFormat, targetFormat)
        val rules = transformationRepository.getRulesByFormat(sourceFormat, targetFormat)
        return ListResult.of(rules)
    }

    /**
     * 获取转换规则（按标签）
     *
     * @param tag 标签
     * @return 转换规则列表
     */
    @GetMapping("/rules/by-tag")
    fun getRulesByTag(@RequestParam tag: String): ListResult<TransformationRule> {
        logger.info("获取转换规则（按标签）: {}", tag)
        val rules = transformationRepository.getRulesByTag(tag)
        return ListResult.of(rules)
    }

    /**
     * 创建转换模板
     *
     * @param template 转换模板
     * @return 模板ID
     */
    @PostMapping("/templates")
    fun createTemplate(@RequestBody template: TransformationTemplate): DataResult<String> {
        logger.info("创建转换模板: {}", template.name)
        val templateId = transformationRepository.saveTemplate(template)
        return DataResult.of(templateId)
    }

    /**
     * 更新转换模板
     *
     * @param template 转换模板
     * @return 是否成功
     */
    @PutMapping("/templates")
    fun updateTemplate(@RequestBody template: TransformationTemplate): ActionResult {
        logger.info("更新转换模板: {}", template.id)
        val success = transformationRepository.updateTemplate(template)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("template.not.found", "转换模板不存在", "")
        }
    }

    /**
     * 删除转换模板
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    @DeleteMapping("/templates/{templateId}")
    fun deleteTemplate(@PathVariable templateId: String): ActionResult {
        logger.info("删除转换模板: {}", templateId)
        val success = transformationRepository.deleteTemplate(templateId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("template.not.found", "转换模板不存在", "")
        }
    }

    /**
     * 获取转换模板
     *
     * @param templateId 模板ID
     * @return 转换模板
     */
    @GetMapping("/templates/{templateId}")
    fun getTemplate(@PathVariable templateId: String): DataResult<TransformationTemplate> {
        logger.info("获取转换模板: {}", templateId)
        val template = transformationRepository.getTemplate(templateId)
        return if (template != null) {
            DataResult.of(template)
        } else {
            DataResult.error("template.not.found", "转换模板不存在")
        }
    }

    /**
     * 获取所有转换模板
     *
     * @return 转换模板列表
     */
    @GetMapping("/templates")
    fun getAllTemplates(): ListResult<TransformationTemplate> {
        logger.info("获取所有转换模板")
        val templates = transformationRepository.getAllTemplates()
        return ListResult.of(templates)
    }

    /**
     * 获取转换模板（按格式）
     *
     * @param sourceFormat 源格式
     * @param targetFormat 目标格式
     * @return 转换模板列表
     */
    @GetMapping("/templates/by-format")
    fun getTemplatesByFormat(
        @RequestParam sourceFormat: String,
        @RequestParam targetFormat: String
    ): ListResult<TransformationTemplate> {
        logger.info("获取转换模板（按格式）: {} -> {}", sourceFormat, targetFormat)
        val templates = transformationRepository.getTemplatesByFormat(sourceFormat, targetFormat)
        return ListResult.of(templates)
    }

    /**
     * 获取转换模板（按标签）
     *
     * @param tag 标签
     * @return 转换模板列表
     */
    @GetMapping("/templates/by-tag")
    fun getTemplatesByTag(@RequestParam tag: String): ListResult<TransformationTemplate> {
        logger.info("获取转换模板（按标签）: {}", tag)
        val templates = transformationRepository.getTemplatesByTag(tag)
        return ListResult.of(templates)
    }

    /**
     * 增加模板使用次数
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    @PostMapping("/templates/{templateId}/increment-use-count")
    fun incrementTemplateUseCount(@PathVariable templateId: String): ActionResult {
        logger.info("增加模板使用次数: {}", templateId)
        val success = transformationRepository.incrementTemplateUseCount(templateId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("template.not.found", "转换模板不存在", "")
        }
    }
}
