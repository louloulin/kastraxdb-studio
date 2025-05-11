package ai.magicdb.dataservice.core.script

import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/**
 * 带日志收集的脚本执行器
 *
 * @author magicdb
 */
class LoggingScriptExecutor(
    private val delegate: ScriptExecutor,
    private val logs: CopyOnWriteArrayList<String>,
    private val console: CopyOnWriteArrayList<String>
) : ScriptExecutor {
    
    private val logger = LoggerFactory.getLogger(LoggingScriptExecutor::class.java)
    
    override fun execute(script: String, language: String, context: Map<String, Any?>, timeout: Long, timeUnit: TimeUnit): Any? {
        try {
            // 添加日志收集器
            val contextWithLogging = context.toMutableMap()
            
            // 添加日志函数
            contextWithLogging["log"] = { message: Any? ->
                val logMessage = message?.toString() ?: "null"
                logs.add(logMessage)
                logger.info("Script log: {}", logMessage)
            }
            
            // 添加控制台输出拦截
            val originalOut = System.out
            val originalErr = System.err
            
            try {
                // 替换标准输出和错误输出
                System.setOut(LoggingPrintStream(console, originalOut))
                System.setErr(LoggingPrintStream(console, originalErr))
                
                // 执行脚本
                return delegate.execute(script, language, contextWithLogging, timeout, timeUnit)
            } finally {
                // 恢复标准输出和错误输出
                System.setOut(originalOut)
                System.setErr(originalErr)
            }
        } catch (e: Exception) {
            // 记录异常
            logs.add("Error: ${e.message}")
            console.add("Error: ${e.message}")
            logger.error("Script execution error", e)
            throw e
        }
    }
    
    override fun getSupportedLanguages(): List<String> {
        return delegate.getSupportedLanguages()
    }
}
