package ai.magicdb.dataservice.core.script

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import javax.script.ScriptEngineManager
import javax.script.SimpleBindings

/**
 * 默认脚本执行器实现
 *
 * @author magicdb
 */
@Service
class DefaultScriptExecutor : ScriptExecutor {
    
    private val logger = LoggerFactory.getLogger(DefaultScriptExecutor::class.java)
    private val scriptEngineManager = ScriptEngineManager()
    
    override fun execute(script: String, language: String, context: Map<String, Any?>, timeout: Long, timeUnit: TimeUnit): Any? {
        try {
            // 获取脚本引擎
            val engineName = getEngineNameByLanguage(language)
            val engine = scriptEngineManager.getEngineByName(engineName)
                ?: throw IllegalArgumentException("不支持的脚本语言: $language")
            
            // 创建绑定
            val bindings = SimpleBindings(context)
            
            // 执行脚本
            return engine.eval(script, bindings)
        } catch (e: Exception) {
            logger.error("执行脚本失败: {}", language, e)
            throw e
        }
    }
    
    override fun getSupportedLanguages(): List<String> {
        return listOf("js", "kotlin", "python")
    }
    
    /**
     * 根据语言获取引擎名称
     */
    private fun getEngineNameByLanguage(language: String): String {
        return when (language.lowercase()) {
            "js", "javascript" -> "nashorn"
            "kotlin" -> "kotlin"
            "python" -> "python"
            else -> language
        }
    }
}
