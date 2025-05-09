package ai.magicdb.script.engine.compiler

import ai.magicdb.script.engine.cache.CompiledScript
import org.slf4j.LoggerFactory
import javax.script.Compilable
import javax.script.CompiledScript as JSR223CompiledScript
import javax.script.ScriptEngineManager
import javax.script.SimpleBindings

/**
 * JSR223脚本编译器
 *
 * @author magicdb
 */
class JSR223ScriptCompiler : ScriptCompiler() {
    private val logger = LoggerFactory.getLogger(JSR223ScriptCompiler::class.java)
    private val scriptEngineManager = ScriptEngineManager()
    
    override fun doCompile(script: String, language: String): Any {
        try {
            // 获取脚本引擎
            val scriptEngine = scriptEngineManager.getEngineByName(language)
                ?: throw IllegalArgumentException("不支持的脚本语言: $language")
            
            // 检查是否支持编译
            if (scriptEngine !is Compilable) {
                throw UnsupportedOperationException("脚本引擎不支持编译: $language")
            }
            
            // 编译脚本
            return scriptEngine.compile(script)
        } catch (e: Exception) {
            logger.error("编译脚本出错: {}", e.message, e)
            throw RuntimeException("编译脚本出错: ${e.message}", e)
        }
    }
    
    override fun doExecute(compiledScript: CompiledScript, context: Map<String, Any?>): Any? {
        try {
            // 执行已编译的脚本
            val jsr223CompiledScript = compiledScript.compiledObject as JSR223CompiledScript
            return jsr223CompiledScript.eval(SimpleBindings(context))
        } catch (e: Exception) {
            logger.error("执行脚本出错: {}", e.message, e)
            throw RuntimeException("执行脚本出错: ${e.message}", e)
        }
    }
}
