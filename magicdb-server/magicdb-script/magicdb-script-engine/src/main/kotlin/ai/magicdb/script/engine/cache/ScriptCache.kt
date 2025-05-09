package ai.magicdb.script.engine.cache

import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * 脚本缓存
 *
 * @author magicdb
 */
class ScriptCache {
    private val logger = LoggerFactory.getLogger(ScriptCache::class.java)
    
    // 缓存已编译的脚本
    private val compiledScripts = ConcurrentHashMap<String, CompiledScript>()
    
    // 脚本依赖关系
    private val scriptDependencies = ConcurrentHashMap<String, Set<String>>()
    
    /**
     * 获取已编译的脚本
     *
     * @param cacheKey 缓存键
     * @return 已编译的脚本，如果不存在则返回null
     */
    fun getCompiledScript(cacheKey: String): CompiledScript? {
        return compiledScripts[cacheKey]
    }
    
    /**
     * 缓存已编译的脚本
     *
     * @param cacheKey 缓存键
     * @param compiledScript 已编译的脚本
     */
    fun putCompiledScript(cacheKey: String, compiledScript: CompiledScript) {
        compiledScripts[cacheKey] = compiledScript
        logger.debug("缓存脚本: {}", cacheKey)
    }
    
    /**
     * 移除已编译的脚本
     *
     * @param cacheKey 缓存键
     */
    fun removeCompiledScript(cacheKey: String) {
        compiledScripts.remove(cacheKey)
        logger.debug("移除脚本缓存: {}", cacheKey)
        
        // 移除依赖该脚本的其他脚本
        val dependentScripts = scriptDependencies.entries
            .filter { it.value.contains(cacheKey) }
            .map { it.key }
        
        dependentScripts.forEach { removeCompiledScript(it) }
    }
    
    /**
     * 添加脚本依赖关系
     *
     * @param scriptKey 脚本键
     * @param dependencyKey 依赖脚本键
     */
    fun addDependency(scriptKey: String, dependencyKey: String) {
        val dependencies = scriptDependencies.getOrDefault(scriptKey, emptySet()).toMutableSet()
        dependencies.add(dependencyKey)
        scriptDependencies[scriptKey] = dependencies
        logger.debug("添加脚本依赖: {} -> {}", scriptKey, dependencyKey)
    }
    
    /**
     * 获取脚本依赖
     *
     * @param scriptKey 脚本键
     * @return 依赖脚本键集合
     */
    fun getDependencies(scriptKey: String): Set<String> {
        return scriptDependencies.getOrDefault(scriptKey, emptySet())
    }
    
    /**
     * 清空缓存
     */
    fun clear() {
        compiledScripts.clear()
        scriptDependencies.clear()
        logger.debug("清空脚本缓存")
    }
    
    /**
     * 获取缓存大小
     *
     * @return 缓存大小
     */
    fun size(): Int {
        return compiledScripts.size
    }
}
