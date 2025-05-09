package ai.magicdb.script.engine

import ai.magicdb.script.api.LanguageProvider
import ai.magicdb.script.api.ScriptExecutor
import ai.magicdb.script.engine.compiler.GraalVMScriptCompiler
import ai.magicdb.script.engine.compiler.JSR223ScriptCompiler
import ai.magicdb.script.engine.compiler.KotlinScriptCompiler
import ai.magicdb.script.engine.compiler.ScriptCompiler
import ai.magicdb.script.engine.provider.GraalVMLanguageProvider
import ai.magicdb.script.engine.provider.JSR223LanguageProvider
import ai.magicdb.script.engine.provider.KotlinLanguageProvider
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * 优化的GraalVM脚本执行器
 *
 * @author magicdb
 */
class OptimizedGraalVMScriptExecutor : ScriptExecutor {
    private val logger = LoggerFactory.getLogger(OptimizedGraalVMScriptExecutor::class.java)
    private val languageProviders = ConcurrentHashMap<String, LanguageProvider>()
    private val compilers = ConcurrentHashMap<String, ScriptCompiler>()
    
    init {
        // 注册内置的语言提供者
        registerLanguageProvider(GraalVMLanguageProvider())
        registerLanguageProvider(JSR223LanguageProvider())
        registerLanguageProvider(KotlinLanguageProvider())
        
        // 注册脚本编译器
        registerCompiler("js", GraalVMScriptCompiler())
        registerCompiler("python", GraalVMScriptCompiler())
        registerCompiler("wasm", GraalVMScriptCompiler())
        registerCompiler("kotlin", KotlinScriptCompiler())
        registerCompiler("default", JSR223ScriptCompiler())
        
        // 通过SPI加载其他语言提供者
        ServiceLoader.load(LanguageProvider::class.java).forEach { provider ->
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
    
    /**
     * 注册脚本编译器
     *
     * @param language 语言
     * @param compiler 编译器
     */
    fun registerCompiler(language: String, compiler: ScriptCompiler) {
        compilers[language] = compiler
    }
    
    @Throws(Exception::class)
    override fun execute(languageName: String, script: String, context: Map<String, Any?>): Any? {
        // 尝试使用编译器执行
        val compiler = compilers[languageName] ?: compilers["default"]
        if (compiler != null) {
            try {
                return compiler.execute(script, languageName, context)
            } catch (e: Exception) {
                logger.warn("使用编译器执行脚本失败，尝试使用语言提供者: {}", e.message)
                // 如果编译器执行失败，尝试使用语言提供者
            }
        }
        
        // 使用语言提供者执行
        for (provider in languageProviders.values) {
            if (provider.support(languageName)) {
                return provider.execute(languageName, script, context)
            }
        }
        
        throw UnsupportedOperationException("不支持的语言: $languageName")
    }
    
    /**
     * 带超时的脚本执行
     *
     * @param languageName 语言名称
     * @param script 脚本内容
     * @param context 上下文
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 执行结果
     */
    @Throws(Exception::class)
    fun executeWithTimeout(
        languageName: String,
        script: String,
        context: Map<String, Any?>,
        timeout: Long,
        unit: TimeUnit
    ): Any? {
        // 创建执行任务
        val task = java.util.concurrent.Callable {
            execute(languageName, script, context)
        }
        
        // 创建执行器
        val executor = java.util.concurrent.Executors.newSingleThreadExecutor()
        val future = executor.submit(task)
        
        try {
            // 等待执行完成或超时
            return future.get(timeout, unit)
        } catch (e: java.util.concurrent.TimeoutException) {
            future.cancel(true)
            throw TimeoutException("脚本执行超时")
        } finally {
            executor.shutdownNow()
        }
    }
    
    override fun getSupportedLanguages(): Array<String> {
        val languages = mutableSetOf<String>()
        
        // 添加编译器支持的语言
        languages.addAll(compilers.keys)
        
        // 添加GraalVM支持的语言
        languages.add("js")
        languages.add("python")
        languages.add("wasm")
        
        // 添加其他支持的语言
        languages.add("kotlin")
        
        // 移除default
        languages.remove("default")
        
        return languages.toTypedArray()
    }
    
    /**
     * 清除脚本缓存
     */
    fun clearCache() {
        compilers.values.forEach { it.clearCache() }
    }
    
    /**
     * 获取缓存大小
     *
     * @return 缓存大小
     */
    fun getCacheSize(): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        compilers.forEach { (language, compiler) ->
            result[language] = compiler.getCacheSize()
        }
        return result
    }
}
