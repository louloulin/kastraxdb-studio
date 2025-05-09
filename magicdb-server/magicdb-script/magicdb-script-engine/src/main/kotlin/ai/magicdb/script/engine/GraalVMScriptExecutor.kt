package ai.magicdb.script.engine

import ai.magicdb.script.api.LanguageProvider
import ai.magicdb.script.api.ScriptExecutor
import ai.magicdb.script.engine.provider.GraalVMLanguageProvider
import ai.magicdb.script.engine.provider.JSR223LanguageProvider
import ai.magicdb.script.engine.provider.KotlinLanguageProvider
import org.slf4j.LoggerFactory
import java.util.*

/**
 * GraalVM脚本执行器
 *
 * @author magicdb
 */
class GraalVMScriptExecutor : ScriptExecutor {
    private val logger = LoggerFactory.getLogger(GraalVMScriptExecutor::class.java)
    private val languageProviders = mutableMapOf<String, LanguageProvider>()

    init {
        // 注册内置的语言提供者
        registerLanguageProvider(GraalVMLanguageProvider())
        registerLanguageProvider(JSR223LanguageProvider())
        registerLanguageProvider(KotlinLanguageProvider())

        // 通过SPI加载其他语言提供者
        val serviceLoader = ServiceLoader.load(LanguageProvider::class.java)
        for (provider in serviceLoader) {
            registerLanguageProvider(provider)
        }
    }

    /**
     * 注册语言提供者
     *
     * @param provider 语言提供者
     */
    fun registerLanguageProvider(provider: LanguageProvider) {
        languageProviders[provider.javaClass.name] = provider
    }

    @Throws(Exception::class)
    override fun execute(languageName: String, script: String, context: Map<String, Any?>): Any? {
        for (provider in languageProviders.values) {
            if (provider.support(languageName)) {
                return provider.execute(languageName, script, context)
            }
        }
        throw UnsupportedOperationException("Unsupported language: $languageName")
    }

    override fun getSupportedLanguages(): Array<String> {
        val languages = mutableListOf<String>()

        // 添加GraalVM支持的语言
        languages.add("js")
        // Python和WebAssembly需要GraalVM企业版
        // languages.add("python")
        // languages.add("wasm")

        // 添加其他支持的语言
        languages.add("kotlin")

        return languages.toTypedArray()
    }
}
