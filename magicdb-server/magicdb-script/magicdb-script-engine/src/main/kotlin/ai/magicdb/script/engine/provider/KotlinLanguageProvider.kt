package ai.magicdb.script.engine.provider

import ai.magicdb.script.api.LanguageProvider
import org.slf4j.LoggerFactory

/**
 * Kotlin语言提供者
 *
 * @author magicdb
 */
class KotlinLanguageProvider : LanguageProvider {
    private val logger = LoggerFactory.getLogger(KotlinLanguageProvider::class.java)

    override fun support(languageName: String): Boolean {
        return "kotlin".equals(languageName, ignoreCase = true)
    }

    @Throws(Exception::class)
    override fun execute(languageName: String, script: String, context: Map<String, Any?>): Any? {
        // 简化实现，直接返回上下文中的变量
        logger.info("执行Kotlin脚本: {}", script)
        return context
    }
}
