package ai.magicdb.script.engine.provider

import ai.magicdb.script.api.LanguageProvider
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import org.slf4j.LoggerFactory

/**
 * Kotlin语言提供者
 *
 * @author magicdb
 */
class KotlinLanguageProvider : LanguageProvider {
    private val logger = LoggerFactory.getLogger(KotlinLanguageProvider::class.java)
    private val scriptingHost = BasicJvmScriptingHost()

    override fun support(languageName: String): Boolean {
        return "kotlin".equals(languageName, ignoreCase = true)
    }

    @Throws(Exception::class)
    override fun execute(languageName: String, script: String, context: Map<String, Any?>): Any? {
        try {
            // 创建编译配置
            val compilationConfiguration = ScriptCompilationConfiguration {
                jvm {
                    baseClassLoader(Thread.currentThread().contextClassLoader)
                }
            }

            // 创建执行配置
            val evaluationConfiguration = ScriptEvaluationConfiguration {
                providedProperties(context)
            }

            // 编译并执行脚本
            val result = scriptingHost.eval(
                SourceCode(script),
                compilationConfiguration,
                evaluationConfiguration
            )

            // 处理结果
            return when (val resultValue = result.valueOrNull()) {
                null -> {
                    // 检查是否有错误
                    result.reports.forEach { diagnostic ->
                        if (diagnostic.severity == ScriptDiagnostic.Severity.ERROR) {
                            throw RuntimeException("Kotlin script error: ${diagnostic.message}")
                        }
                    }
                    null
                }
                else -> resultValue.returnValue
            }
        } catch (e: Exception) {
            logger.error("执行Kotlin脚本出错: {}", e.message, e)
            throw e
        }
    }
}
