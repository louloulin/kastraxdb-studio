package ai.magicdb.script.engine.compiler

import ai.magicdb.script.engine.cache.CompiledScript
import org.slf4j.LoggerFactory

/**
 * Kotlin脚本编译器
 *
 * @author magicdb
 */
class KotlinScriptCompiler : ScriptCompiler() {
    private val logger = LoggerFactory.getLogger(KotlinScriptCompiler::class.java)

    override fun doCompile(script: String, language: String): Any {
        // 简化实现，直接返回脚本字符串
        logger.info("编译Kotlin脚本: {}", script)
        return script
    }

    override fun doExecute(compiledScript: CompiledScript, context: Map<String, Any?>): Any? {
        // 简化实现，直接返回上下文
        logger.info("执行Kotlin脚本: {}", compiledScript.compiledObject)
        return context
    }
}
