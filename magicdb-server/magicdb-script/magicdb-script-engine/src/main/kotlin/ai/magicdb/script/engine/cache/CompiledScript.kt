package ai.magicdb.script.engine.cache

import java.time.Instant

/**
 * 已编译的脚本
 *
 * @author magicdb
 */
data class CompiledScript(
    /**
     * 脚本语言
     */
    val language: String,
    
    /**
     * 原始脚本
     */
    val sourceScript: String,
    
    /**
     * 编译后的脚本对象
     */
    val compiledObject: Any,
    
    /**
     * 编译时间
     */
    val compiledAt: Instant = Instant.now(),
    
    /**
     * 最后访问时间
     */
    var lastAccessedAt: Instant = Instant.now(),
    
    /**
     * 访问次数
     */
    var accessCount: Long = 0
) {
    /**
     * 更新访问信息
     */
    fun updateAccess() {
        lastAccessedAt = Instant.now()
        accessCount++
    }
}
