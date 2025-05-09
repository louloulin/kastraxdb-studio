package ai.magicdb.script.engine.provider

import ai.magicdb.script.api.LanguageProvider
import org.slf4j.LoggerFactory
import javax.script.ScriptEngineManager
import javax.script.SimpleBindings

/**
 * JavaScript语言提供者
 *
 * @author magicdb
 */
class GraalVMLanguageProvider : LanguageProvider {
    private val logger = LoggerFactory.getLogger(GraalVMLanguageProvider::class.java)
    private val engineManager = ScriptEngineManager()

    companion object {
        private val SUPPORTED_LANGUAGES = arrayOf("js", "javascript")
    }

    override fun support(languageName: String): Boolean {
        return SUPPORTED_LANGUAGES.any { it.equals(languageName, ignoreCase = true) }
    }

    @Throws(Exception::class)
    override fun execute(languageName: String, script: String, context: Map<String, Any?>): Any? {
        try {
            // 获取JavaScript引擎
            val engine = engineManager.getEngineByName("nashorn")
                ?: engineManager.getEngineByName("js")
                ?: throw IllegalStateException("No JavaScript engine found")

            // 绑定上下文变量
            val bindings = SimpleBindings()
            context.forEach { (key, value) ->
                bindings[key] = value
            }

            // 执行脚本
            return engine.eval(script, bindings)
        } catch (e: Exception) {
            logger.error("执行{}脚本出错: {}", languageName, e.message, e)
            throw e
        }
    }
}
