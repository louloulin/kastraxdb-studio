package ai.magicdb.script.engine.provider

import ai.magicdb.script.api.LanguageProvider
import org.slf4j.LoggerFactory
import javax.script.Compilable
import javax.script.ScriptEngineManager
import javax.script.SimpleBindings

/**
 * JSR223规范支持
 *
 * @author magicdb
 */
class JSR223LanguageProvider : LanguageProvider {
    private val logger = LoggerFactory.getLogger(JSR223LanguageProvider::class.java)
    private val scriptEngineManager = ScriptEngineManager()

    override fun support(languageName: String): Boolean {
        return scriptEngineManager.getEngineByName(languageName) != null
    }

    @Throws(Exception::class)
    override fun execute(languageName: String, script: String, context: Map<String, Any?>): Any? {
        val scriptEngine = scriptEngineManager.getEngineByName(languageName)
            ?: throw UnsupportedOperationException("Unsupported language: $languageName")
        
        return if (scriptEngine is Compilable) {
            try {
                scriptEngine.compile(script).eval(SimpleBindings(context))
            } catch (e: Exception) {
                logger.error("编译{}出错", languageName, e)
                throw RuntimeException("编译${languageName}出错", e)
            }
        } else {
            scriptEngine.eval(script, SimpleBindings(context))
        }
    }
}
