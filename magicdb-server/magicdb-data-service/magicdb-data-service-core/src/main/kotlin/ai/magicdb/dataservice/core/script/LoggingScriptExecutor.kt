package ai.magicdb.dataservice.core.script

import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/**
 * 带日志的脚本执行器
 *
 * @author magicdb
 */
class LoggingScriptExecutor(
    private val delegate: ScriptExecutor,
    private val logs: MutableList<String> = CopyOnWriteArrayList(),
    private val console: MutableList<String> = CopyOnWriteArrayList()
) : ScriptExecutor {
    private val logger = LoggerFactory.getLogger(LoggingScriptExecutor::class.java)

    override fun execute(script: String, language: String, context: Map<String, Any?>, timeout: Long, timeUnit: TimeUnit): Any? {
        try {
            // 创建带日志的上下文
            val loggingContext = createLoggingContext(context)

            // 执行脚本
            val result = delegate.execute(script, language, loggingContext, timeout, timeUnit)

            // 记录执行结果
            logs.add("Script execution completed with result: $result")

            return result
        } catch (e: Exception) {
            // 记录错误
            logs.add("Script execution failed: ${e.message}")
            logger.error("执行脚本失败: {}", e.message, e)
            throw e
        }
    }

    override fun getSupportedLanguages(): List<String> {
        return delegate.getSupportedLanguages()
    }

    /**
     * 创建带日志的上下文
     */
    private fun createLoggingContext(context: Map<String, Any?>): Map<String, Any?> {
        val loggingContext = context.toMutableMap()

        // 添加日志函数
        loggingContext["log"] = { message: Any? ->
            val logMessage = message.toString()
            logs.add(logMessage)
            logger.info(logMessage)
        }

        // 添加控制台输出函数
        loggingContext["console"] = object {
            fun log(message: Any?) {
                val logMessage = message.toString()
                console.add(logMessage)
                logs.add("CONSOLE: $logMessage")
            }

            fun info(message: Any?) {
                val logMessage = message.toString()
                console.add("INFO: $logMessage")
                logs.add("CONSOLE INFO: $logMessage")
            }

            fun warn(message: Any?) {
                val logMessage = message.toString()
                console.add("WARN: $logMessage")
                logs.add("CONSOLE WARN: $logMessage")
            }

            fun error(message: Any?) {
                val logMessage = message.toString()
                console.add("ERROR: $logMessage")
                logs.add("CONSOLE ERROR: $logMessage")
            }

            fun debug(message: Any?) {
                val logMessage = message.toString()
                console.add("DEBUG: $logMessage")
                logs.add("CONSOLE DEBUG: $logMessage")
            }
        }

        // 添加打印函数
        loggingContext["print"] = { message: Any? ->
            val logMessage = message.toString()
            console.add(logMessage)
            logs.add("PRINT: $logMessage")
        }

        // 添加println函数
        loggingContext["println"] = { message: Any? ->
            val logMessage = message.toString()
            console.add(logMessage)
            logs.add("PRINTLN: $logMessage")
        }

        return loggingContext
    }
}
