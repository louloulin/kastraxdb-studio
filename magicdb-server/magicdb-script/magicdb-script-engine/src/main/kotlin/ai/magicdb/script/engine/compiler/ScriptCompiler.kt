package ai.magicdb.script.engine.compiler

import ai.magicdb.script.engine.cache.CompiledScript
import ai.magicdb.script.engine.cache.ScriptCache
import org.slf4j.LoggerFactory
import java.security.MessageDigest

/**
 * 脚本编译器
 *
 * @author magicdb
 */
abstract class ScriptCompiler {
    private val logger = LoggerFactory.getLogger(ScriptCompiler::class.java)
    private val scriptCache = ScriptCache()
    
    /**
     * 编译脚本
     *
     * @param script 脚本内容
     * @param language 脚本语言
     * @return 已编译的脚本
     */
    fun compile(script: String, language: String): CompiledScript {
        // 生成缓存键
        val cacheKey = generateCacheKey(script, language)
        
        // 尝试从缓存获取
        val cachedScript = scriptCache.getCompiledScript(cacheKey)
        if (cachedScript != null) {
            logger.debug("从缓存获取脚本: {}", cacheKey)
            cachedScript.updateAccess()
            return cachedScript
        }
        
        // 编译脚本
        logger.debug("编译脚本: {}", cacheKey)
        val compiledObject = doCompile(script, language)
        
        // 缓存编译结果
        val compiledScript = CompiledScript(language, script, compiledObject)
        scriptCache.putCompiledScript(cacheKey, compiledScript)
        
        return compiledScript
    }
    
    /**
     * 执行已编译的脚本
     *
     * @param compiledScript 已编译的脚本
     * @param context 执行上下文
     * @return 执行结果
     */
    fun execute(compiledScript: CompiledScript, context: Map<String, Any?>): Any? {
        compiledScript.updateAccess()
        return doExecute(compiledScript, context)
    }
    
    /**
     * 执行脚本
     *
     * @param script 脚本内容
     * @param language 脚本语言
     * @param context 执行上下文
     * @return 执行结果
     */
    fun execute(script: String, language: String, context: Map<String, Any?>): Any? {
        val compiledScript = compile(script, language)
        return execute(compiledScript, context)
    }
    
    /**
     * 清除脚本缓存
     */
    fun clearCache() {
        scriptCache.clear()
    }
    
    /**
     * 获取缓存大小
     *
     * @return 缓存大小
     */
    fun getCacheSize(): Int {
        return scriptCache.size()
    }
    
    /**
     * 生成缓存键
     *
     * @param script 脚本内容
     * @param language 脚本语言
     * @return 缓存键
     */
    private fun generateCacheKey(script: String, language: String): String {
        val md = MessageDigest.getInstance("MD5")
        val input = "$language:$script"
        val bytes = md.digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * 执行编译
     *
     * @param script 脚本内容
     * @param language 脚本语言
     * @return 编译后的对象
     */
    protected abstract fun doCompile(script: String, language: String): Any
    
    /**
     * 执行已编译的脚本
     *
     * @param compiledScript 已编译的脚本
     * @param context 执行上下文
     * @return 执行结果
     */
    protected abstract fun doExecute(compiledScript: CompiledScript, context: Map<String, Any?>): Any?
}
