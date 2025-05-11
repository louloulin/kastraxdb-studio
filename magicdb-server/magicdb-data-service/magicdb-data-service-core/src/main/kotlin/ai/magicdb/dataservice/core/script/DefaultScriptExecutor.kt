package ai.magicdb.dataservice.core.script

import ai.magicdb.script.api.ScriptExecutor as GraalScriptExecutor
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

/**
 * 默认脚本执行器实现
 *
 * @author magicdb
 */
class DefaultScriptExecutor(
    private val scriptExecutor: GraalScriptExecutor
) : ScriptExecutor {
    private val logger = LoggerFactory.getLogger(DefaultScriptExecutor::class.java)

    override fun execute(script: String, language: String, context: Map<String, Any?>, timeout: Long, timeUnit: TimeUnit): Any? {
        try {
            // 执行脚本
            return scriptExecutor.execute(language, script, context)
        } catch (e: Exception) {
            logger.error("执行脚本失败: {}", e.message, e)
            throw e
        }
    }

    override fun getSupportedLanguages(): List<String> {
        return scriptExecutor.getSupportedLanguages().toList()
    }
}
