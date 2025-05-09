package ai.magicdb.script.engine.compiler

import ai.magicdb.script.engine.cache.CompiledScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import org.slf4j.LoggerFactory

/**
 * Kotlin脚本编译器
 *
 * @author magicdb
 */
class KotlinScriptCompiler : ScriptCompiler() {
    private val logger = LoggerFactory.getLogger(KotlinScriptCompiler::class.java)
    private val scriptingHost = BasicJvmScriptingHost()
    
    override fun doCompile(script: String, language: String): Any {
        try {
            // 创建编译配置
            val compilationConfiguration = ScriptCompilationConfiguration {
                jvm {
                    baseClassLoader(Thread.currentThread().contextClassLoader)
                }
            }
            
            // 编译脚本
            val compilationResult = scriptingHost.compiler.compile(
                SourceCode(script),
                compilationConfiguration
            )
            
            // 检查编译结果
            when (compilationResult) {
                is ResultWithDiagnostics.Success -> {
                    return compilationResult.value
                }
                is ResultWithDiagnostics.Failure -> {
                    val errors = compilationResult.reports
                        .filter { it.severity == ScriptDiagnostic.Severity.ERROR }
                        .joinToString("\n") { it.message }
                    throw RuntimeException("编译Kotlin脚本出错: $errors")
                }
            }
        } catch (e: Exception) {
            logger.error("编译Kotlin脚本出错: {}", e.message, e)
            throw RuntimeException("编译Kotlin脚本出错: ${e.message}", e)
        }
    }
    
    override fun doExecute(compiledScript: CompiledScript, context: Map<String, Any?>): Any? {
        try {
            // 创建执行配置
            val evaluationConfiguration = ScriptEvaluationConfiguration {
                providedProperties(context)
            }
            
            // 执行已编译的脚本
            val compiledKotlinScript = compiledScript.compiledObject as CompiledScript
            val evaluationResult = scriptingHost.evaluator.eval(
                compiledKotlinScript,
                evaluationConfiguration
            )
            
            // 检查执行结果
            when (evaluationResult) {
                is ResultWithDiagnostics.Success -> {
                    return evaluationResult.value.returnValue
                }
                is ResultWithDiagnostics.Failure -> {
                    val errors = evaluationResult.reports
                        .filter { it.severity == ScriptDiagnostic.Severity.ERROR }
                        .joinToString("\n") { it.message }
                    throw RuntimeException("执行Kotlin脚本出错: $errors")
                }
            }
        } catch (e: Exception) {
            logger.error("执行Kotlin脚本出错: {}", e.message, e)
            throw RuntimeException("执行Kotlin脚本出错: ${e.message}", e)
        }
    }
}
