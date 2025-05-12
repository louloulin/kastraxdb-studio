package ai.magicdb.data.service.core.debug

import ai.magicdb.data.service.api.DataSourceService
import ai.magicdb.data.service.api.ScriptDebugger
import ai.magicdb.data.service.api.model.ScriptDebugRequest
import ai.magicdb.data.service.api.model.ScriptDebugResult
import ai.magicdb.data.service.core.script.LoggingScriptExecutor
import ai.magicdb.data.service.core.script.ScriptExecutor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/**
 * 默认脚本调试器实现
 *
 * @author magicdb
 */
@Service
class DefaultScriptDebugger(
    private val scriptExecutor: ScriptExecutor,
    private val dataSourceService: DataSourceService
) : ScriptDebugger {

    private val logger = LoggerFactory.getLogger(DefaultScriptDebugger::class.java)
    
    // 调试会话缓存
    private val sessions = ConcurrentHashMap<String, DebugSession>()

    override fun debug(request: ScriptDebugRequest): ScriptDebugResult {
        try {
            // 创建日志收集器
            val logs = CopyOnWriteArrayList<String>()
            val console = CopyOnWriteArrayList<String>()

            // 创建带日志的脚本执行器
            val loggingExecutor = LoggingScriptExecutor(scriptExecutor, logs, console)

            // 创建执行上下文
            val context = createExecutionContext(request)

            // 执行脚本
            val startTime = System.currentTimeMillis()
            val result = loggingExecutor.execute(request.script, request.language, context, request.timeout, TimeUnit.MILLISECONDS)
            val duration = System.currentTimeMillis() - startTime

            // 创建调试结果
            return ScriptDebugResult(
                success = true,
                data = result,
                duration = duration,
                logs = logs,
                console = console
            )
        } catch (e: Exception) {
            logger.error("调试脚本失败", e)

            // 创建错误结果
            return ScriptDebugResult(
                success = false,
                message = e.message,
                stackTrace = e.stackTraceToString()
            )
        }
    }

    override fun getLanguages(): List<String> {
        return scriptExecutor.getSupportedLanguages()
    }

    override fun getTemplate(language: String): String {
        return when (language) {
            "js" -> JS_TEMPLATE
            "kotlin" -> KOTLIN_TEMPLATE
            "python" -> PYTHON_TEMPLATE
            else -> JS_TEMPLATE
        }
    }
    
    override fun createSession(request: ScriptDebugRequest): String {
        // 创建会话ID
        val sessionId = java.util.UUID.randomUUID().toString()
        
        // 创建会话
        val session = DebugSession(
            id = sessionId,
            request = request,
            context = createExecutionContext(request),
            logs = CopyOnWriteArrayList(),
            console = CopyOnWriteArrayList(),
            startTime = System.currentTimeMillis()
        )
        
        // 保存会话
        sessions[sessionId] = session
        
        return sessionId
    }
    
    override fun closeSession(sessionId: String): Boolean {
        // 移除会话
        return sessions.remove(sessionId) != null
    }
    
    override fun getSession(sessionId: String): Map<String, Any?> {
        // 获取会话
        val session = sessions[sessionId] ?: return emptyMap()
        
        // 返回会话信息
        return mapOf(
            "id" to session.id,
            "request" to session.request,
            "logs" to session.logs,
            "console" to session.console,
            "startTime" to session.startTime,
            "duration" to (System.currentTimeMillis() - session.startTime)
        )
    }
    
    override fun executeCommand(sessionId: String, command: String): ScriptDebugResult {
        // 获取会话
        val session = sessions[sessionId] ?: return ScriptDebugResult(
            success = false,
            message = "会话不存在: $sessionId"
        )
        
        try {
            // 创建带日志的脚本执行器
            val loggingExecutor = LoggingScriptExecutor(scriptExecutor, session.logs, session.console)
            
            // 执行命令
            val startTime = System.currentTimeMillis()
            val result = loggingExecutor.execute(command, session.request.language, session.context, session.request.timeout, TimeUnit.MILLISECONDS)
            val duration = System.currentTimeMillis() - startTime
            
            // 创建调试结果
            return ScriptDebugResult(
                success = true,
                data = result,
                duration = duration,
                logs = session.logs,
                console = session.console
            )
        } catch (e: Exception) {
            logger.error("执行命令失败", e)
            
            // 创建错误结果
            return ScriptDebugResult(
                success = false,
                message = e.message,
                stackTrace = e.stackTraceToString(),
                logs = session.logs,
                console = session.console
            )
        }
    }

    /**
     * 创建执行上下文
     */
    private fun createExecutionContext(request: ScriptDebugRequest): Map<String, Any?> {
        val context = mutableMapOf<String, Any?>()

        // 添加参数
        context.putAll(request.parameters)

        // 添加数据源信息
        if (request.dataSourceId != null) {
            val dataSourceId = request.dataSourceId!!
            context["dataSourceId"] = dataSourceId

            // 添加数据源信息
            val dataSource = dataSourceService.getDataSource(dataSourceId)
            if (dataSource != null) {
                context["dataSource"] = dataSource
            }

            // 添加数据库查询函数
            val databaseName = request.databaseName ?: ""
            context["executeQuery"] = { sql: String ->
                dataSourceService.executeQuery(dataSourceId, databaseName, sql)
            }

            // 添加获取表信息函数
            context["getTables"] = {
                dataSourceService.getTables(dataSourceId, databaseName)
            }

            // 添加获取列信息函数
            context["getColumns"] = { tableName: String ->
                dataSourceService.getColumns(dataSourceId, databaseName, tableName)
            }

            // 添加测试连接函数
            context["testConnection"] = {
                dataSourceService.testConnection(dataSourceId)
            }

            // 添加获取数据库列表函数
            context["getDatabases"] = {
                dataSourceService.getDatabases(dataSourceId)
            }
        }

        if (request.databaseName != null) {
            context["databaseName"] = request.databaseName
        }
        if (request.schemaName != null) {
            context["schemaName"] = request.schemaName
        }
        if (request.tableName != null) {
            context["tableName"] = request.tableName
        }

        return context
    }
    
    /**
     * 调试会话
     */
    data class DebugSession(
        val id: String,
        val request: ScriptDebugRequest,
        val context: Map<String, Any?>,
        val logs: MutableList<String>,
        val console: MutableList<String>,
        val startTime: Long
    )

    companion object {
        /**
         * JavaScript模板
         */
        private val JS_TEMPLATE = """
            /**
             * 数据服务脚本
             *
             * @param {Object} params - 请求参数
             * @returns {Object} 返回结果
             */
            function execute(params) {
                // 在这里编写你的代码
                console.log('Hello, World!');
                console.log('Params:', params);

                // 返回结果
                return {
                    message: 'Hello, ' + (params.name || 'World')
                };
            }

            // 执行函数
            execute(params);
        """.trimIndent()

        /**
         * Kotlin模板
         */
        private val KOTLIN_TEMPLATE = """
            /**
             * 数据服务脚本
             *
             * @param params 请求参数
             * @return 返回结果
             */
            fun execute(params: Map<String, Any?>) {
                // 在这里编写你的代码
                println("Hello, World!")
                println("Params: ${'$'}params")

                // 返回结果
                return mapOf(
                    "message" to "Hello, ${'$'}{params["name"] ?: "World"}"
                )
            }

            // 执行函数
            execute(params as Map<String, Any?>)
        """.trimIndent()

        /**
         * Python模板
         */
        private val PYTHON_TEMPLATE = """
            # 数据服务脚本
            #
            # Args:
            #     params: 请求参数
            #
            # Returns:
            #     返回结果
            def execute(params):
                # 在这里编写你的代码
                print("Hello, World!")
                print("Params:", params)

                # 返回结果
                return {
                    "message": "Hello, " + params.get("name", "World")
                }

            # 执行函数
            execute(params)
        """.trimIndent()
    }
}
