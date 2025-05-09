package ai.magicdb.script.engine.provider

import ai.magicdb.script.api.LanguageProvider
import org.graalvm.polyglot.Context
import org.slf4j.LoggerFactory

/**
 * GraalVM语言提供者
 *
 * @author magicdb
 */
class GraalVMLanguageProvider : LanguageProvider {
    private val logger = LoggerFactory.getLogger(GraalVMLanguageProvider::class.java)
    
    companion object {
        private val SUPPORTED_LANGUAGES = arrayOf("js", "python", "wasm")
    }

    override fun support(languageName: String): Boolean {
        return SUPPORTED_LANGUAGES.any { it.equals(languageName, ignoreCase = true) }
    }

    @Throws(Exception::class)
    override fun execute(languageName: String, script: String, context: Map<String, Any?>): Any? {
        try {
            Context.newBuilder()
                .allowAllAccess(true)
                .build().use { polyglotContext ->
                
                // 绑定上下文变量
                val bindings = polyglotContext.getBindings(languageName)
                context.forEach { (key, value) ->
                    bindings.putMember(key, value)
                }
                
                // 执行脚本
                val result = polyglotContext.eval(languageName, script)
                
                // 转换结果为Java对象
                return result.`as`(Any::class.java)
            }
        } catch (e: Exception) {
            logger.error("执行{}脚本出错: {}", languageName, e.message, e)
            throw e
        }
    }
}
