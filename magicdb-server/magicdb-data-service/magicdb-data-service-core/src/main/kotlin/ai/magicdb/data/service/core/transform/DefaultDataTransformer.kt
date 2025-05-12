package ai.magicdb.data.service.core.transform

import ai.magicdb.data.service.api.DataTransformer
import ai.magicdb.data.service.api.model.TransformationRequest
import ai.magicdb.data.service.api.model.TransformationResult
import ai.magicdb.data.service.core.script.ScriptExecutor
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * 默认数据转换器实现
 *
 * @author magicdb
 */
@Service
class DefaultDataTransformer(
    private val scriptExecutor: ScriptExecutor,
    private val objectMapper: ObjectMapper
) : DataTransformer {
    private val logger = LoggerFactory.getLogger(DefaultDataTransformer::class.java)

    // 转换器注册表
    private val transformers = ConcurrentHashMap<String, FormatTransformer>()

    // 初始化转换器
    init {
        // 注册JSON转换器
        registerTransformer(JsonTransformer(objectMapper))

        // 注册XML转换器
        registerTransformer(XmlTransformer(objectMapper))

        // 注册CSV转换器
        registerTransformer(CsvTransformer(objectMapper))

        // 注册YAML转换器
        registerTransformer(YamlTransformer(objectMapper))

        // TODO: 注册Excel转换器
        // registerTransformer(ExcelTransformer(objectMapper))
    }

    /**
     * 注册转换器
     */
    fun registerTransformer(transformer: FormatTransformer) {
        // 注册源格式到目标格式的转换器
        for (sourceFormat in transformer.getSupportedSourceFormats()) {
            for (targetFormat in transformer.getSupportedTargetFormats()) {
                val key = getTransformerKey(sourceFormat, targetFormat)
                transformers[key] = transformer
            }
        }
    }

    /**
     * 获取转换器键
     */
    private fun getTransformerKey(sourceFormat: String, targetFormat: String): String {
        return "$sourceFormat->$targetFormat"
    }

    /**
     * 转换数据
     */
    override fun transform(request: TransformationRequest): TransformationResult {
        try {
            // 获取转换器
            val transformer = getTransformer(request.sourceFormat, request.targetFormat)
                ?: return TransformationResult.failure("不支持的转换格式: ${request.sourceFormat} -> ${request.targetFormat}")

            // 记录开始时间
            val startTime = System.currentTimeMillis()

            // 执行转换
            val result = transformer.transform(request)

            // 计算耗时
            val duration = System.currentTimeMillis() - startTime

            // 返回结果
            return if (result.success) {
                TransformationResult.success(
                    targetData = result.targetData,
                    targetFormat = result.targetFormat,
                    duration = duration,
                    warnings = result.warnings,
                    statistics = result.statistics
                )
            } else {
                TransformationResult.failure(
                    errorMessage = result.errorMessage ?: "转换失败",
                    warnings = result.warnings
                )
            }
        } catch (e: Exception) {
            logger.error("转换数据失败: {}", e.message, e)
            return TransformationResult.failure("转换数据失败: ${e.message}")
        }
    }

    /**
     * 获取支持的源格式
     */
    override fun getSupportedSourceFormats(): List<String> {
        return transformers.values
            .flatMap { it.getSupportedSourceFormats() }
            .distinct()
    }

    /**
     * 获取支持的目标格式
     */
    override fun getSupportedTargetFormats(): List<String> {
        return transformers.values
            .flatMap { it.getSupportedTargetFormats() }
            .distinct()
    }

    /**
     * 获取支持的转换类型
     */
    override fun getSupportedTransformationTypes(): List<String> {
        return listOf("default", "script", "template", "mapping")
    }

    /**
     * 验证转换规则
     */
    override fun validateRules(rules: Map<String, Any?>, sourceFormat: String, targetFormat: String): List<String> {
        try {
            // 获取转换器
            val transformer = getTransformer(sourceFormat, targetFormat)
                ?: return listOf("不支持的转换格式: $sourceFormat -> $targetFormat")

            // 验证规则
            return transformer.validateRules(rules)
        } catch (e: Exception) {
            logger.error("验证转换规则失败: {}", e.message, e)
            return listOf("验证转换规则失败: ${e.message}")
        }
    }

    /**
     * 获取转换规则模板
     */
    override fun getRuleTemplate(sourceFormat: String, targetFormat: String): Map<String, Any?> {
        try {
            // 获取转换器
            val transformer = getTransformer(sourceFormat, targetFormat)
                ?: return mapOf("error" to "不支持的转换格式: $sourceFormat -> $targetFormat")

            // 获取规则模板
            return transformer.getRuleTemplate()
        } catch (e: Exception) {
            logger.error("获取转换规则模板失败: {}", e.message, e)
            return mapOf("error" to "获取转换规则模板失败: ${e.message}")
        }
    }

    /**
     * 获取转换器
     */
    private fun getTransformer(sourceFormat: String, targetFormat: String): FormatTransformer? {
        val key = getTransformerKey(sourceFormat, targetFormat)
        return transformers[key]
    }
}
