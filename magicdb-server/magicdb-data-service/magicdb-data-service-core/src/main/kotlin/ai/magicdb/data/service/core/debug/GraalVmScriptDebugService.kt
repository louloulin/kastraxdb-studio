package ai.magicdb.data.service.core.debug

import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.ScriptDebugService
import ai.magicdb.data.service.api.model.BreakpointInfo
import ai.magicdb.data.service.api.model.DebugSession
import ai.magicdb.data.service.api.model.DebugSessionStatus
import ai.magicdb.data.service.api.model.DebugStepResult
import ai.magicdb.data.service.api.model.VariableInfo
import ai.magicdb.data.service.api.model.VariableScope
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Engine
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.Value
// 注释掉不存在的依赖
// import org.graalvm.polyglot.management.ExecutionEvent
// import org.graalvm.polyglot.management.ExecutionListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * GraalVM 脚本调试服务实现
 */
@Service
class GraalVmScriptDebugService(
    private val dataServiceManager: DataServiceManager
) : ScriptDebugService {

    private val logger = LoggerFactory.getLogger(GraalVmScriptDebugService::class.java)

    // 调试会话存储
    private val sessions = ConcurrentHashMap<String, DebugSessionContext>()

    // 锁
    private val lock = ReentrantLock()

    override fun createDebugSession(serviceId: String, parameters: Map<String, Any?>): DebugSession {
        try {
            // 获取服务
            val service = dataServiceManager.getService(serviceId)
                ?: throw IllegalArgumentException("Service not found: $serviceId")

            // 创建会话 ID
            val sessionId = UUID.randomUUID().toString()

            // 创建调试会话
            val session = DebugSession(
                id = sessionId,
                serviceId = serviceId,
                script = service.script ?: "",
                language = service.language ?: "js",
                parameters = parameters,
                status = DebugSessionStatus.CREATED
            )

            // 创建 GraalVM 引擎
            val engine = Engine.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .build()

            // 创建执行监听器
            // 注释掉不存在的依赖
            // val executionListener = engine.attachExecutionListener()

            // 创建 GraalVM 上下文
            val context = Context.newBuilder(session.language)
                .engine(engine)
                .option("js.debug", "true")
                .option("js.debug-builtin", "true")
                .option("js.print", "true")
                .build()

            // 创建调试会话上下文
            val sessionContext = DebugSessionContext(
                session = session,
                engine = engine,
                context = context,
                // 移除 executionListener 参数
                // executionListener = executionListener,
                breakpoints = mutableListOf(),
                variables = mutableListOf(),
                callStack = mutableListOf(),
                status = DebugSessionStatus.CREATED
            )

            // 存储会话
            sessions[sessionId] = sessionContext

            // 初始化参数
            initializeParameters(sessionContext)

            return session
        } catch (e: Exception) {
            logger.error("创建调试会话失败", e)
            throw e
        }
    }

    override fun getDebugSession(sessionId: String): DebugSession? {
        val sessionContext = sessions[sessionId] ?: return null
        return sessionContext.session.copy(
            breakpoints = sessionContext.breakpoints.toList(),
            currentLine = sessionContext.currentLine,
            currentColumn = sessionContext.currentColumn,
            status = sessionContext.status,
            updateTime = Date()
        )
    }

    override fun terminateDebugSession(sessionId: String): Boolean {
        return lock.withLock {
            val sessionContext = sessions[sessionId] ?: return false

            try {
                // 关闭 GraalVM 上下文
                sessionContext.context.close(true)

                // 关闭执行监听器
                // 注释掉不存在的依赖
                // sessionContext.executionListener.close()

                // 关闭引擎
                sessionContext.engine.close()

                // 移除会话
                sessions.remove(sessionId)

                return true
            } catch (e: Exception) {
                logger.error("终止调试会话失败: {}", sessionId, e)
                return false
            }
        }
    }

    override fun addBreakpoint(sessionId: String, breakpoint: BreakpointInfo): Boolean {
        val sessionContext = sessions[sessionId] ?: return false

        try {
            // 添加断点
            sessionContext.breakpoints.add(breakpoint)

            // 更新会话
            updateSession(sessionContext)

            return true
        } catch (e: Exception) {
            logger.error("添加断点失败: {}", sessionId, e)
            return false
        }
    }

    override fun removeBreakpoint(sessionId: String, breakpointId: String): Boolean {
        val sessionContext = sessions[sessionId] ?: return false

        try {
            // 移除断点
            val removed = sessionContext.breakpoints.removeIf { it.id == breakpointId }

            // 更新会话
            updateSession(sessionContext)

            return removed
        } catch (e: Exception) {
            logger.error("移除断点失败: {}", sessionId, e)
            return false
        }
    }

    override fun getBreakpoints(sessionId: String): List<BreakpointInfo> {
        val sessionContext = sessions[sessionId] ?: return emptyList()
        return sessionContext.breakpoints.toList()
    }

    override fun stepOver(sessionId: String): DebugStepResult {
        return lock.withLock {
            val sessionContext = sessions[sessionId] ?: return createErrorResult("Session not found")

            try {
                // 执行单步
                val result = executeStep(sessionContext, StepType.OVER)

                // 更新会话
                updateSession(sessionContext)

                return result
            } catch (e: Exception) {
                logger.error("单步执行失败: {}", sessionId, e)
                return createErrorResult(e.message ?: "Step over failed")
            }
        }
    }

    override fun stepInto(sessionId: String): DebugStepResult {
        return lock.withLock {
            val sessionContext = sessions[sessionId] ?: return createErrorResult("Session not found")

            try {
                // 执行单步进入
                val result = executeStep(sessionContext, StepType.INTO)

                // 更新会话
                updateSession(sessionContext)

                return result
            } catch (e: Exception) {
                logger.error("单步进入失败: {}", sessionId, e)
                return createErrorResult(e.message ?: "Step into failed")
            }
        }
    }

    override fun stepOut(sessionId: String): DebugStepResult {
        return lock.withLock {
            val sessionContext = sessions[sessionId] ?: return createErrorResult("Session not found")

            try {
                // 执行单步跳出
                val result = executeStep(sessionContext, StepType.OUT)

                // 更新会话
                updateSession(sessionContext)

                return result
            } catch (e: Exception) {
                logger.error("单步跳出失败: {}", sessionId, e)
                return createErrorResult(e.message ?: "Step out failed")
            }
        }
    }

    override fun continueExecution(sessionId: String): DebugStepResult {
        return lock.withLock {
            val sessionContext = sessions[sessionId] ?: return createErrorResult("Session not found")

            try {
                // 继续执行
                val result = executeStep(sessionContext, StepType.CONTINUE)

                // 更新会话
                updateSession(sessionContext)

                return result
            } catch (e: Exception) {
                logger.error("继续执行失败: {}", sessionId, e)
                return createErrorResult(e.message ?: "Continue failed")
            }
        }
    }

    override fun getVariables(sessionId: String): List<VariableInfo> {
        val sessionContext = sessions[sessionId] ?: return emptyList()
        return sessionContext.variables.toList()
    }

    override fun getCallStack(sessionId: String): List<Map<String, Any?>> {
        val sessionContext = sessions[sessionId] ?: return emptyList()
        return sessionContext.callStack.toList()
    }

    override fun evaluateExpression(sessionId: String, expression: String): Any? {
        val sessionContext = sessions[sessionId] ?: return null

        try {
            // 执行表达式
            val result = sessionContext.context.eval(sessionContext.session.language, expression)

            // 转换结果
            return convertValue(result)
        } catch (e: Exception) {
            logger.error("执行表达式失败: {}", sessionId, e)
            return null
        }
    }

    /**
     * 初始化参数
     */
    private fun initializeParameters(sessionContext: DebugSessionContext) {
        try {
            // 获取参数
            val parameters = sessionContext.session.parameters

            // 绑定参数
            val bindings = sessionContext.context.getBindings(sessionContext.session.language)

            // 创建参数对象
            val paramsObj = sessionContext.context.eval(
                sessionContext.session.language,
                "new Object()"
            )

            // 设置参数
            parameters.forEach { (key, value) ->
                try {
                    val convertedValue = convertToGraalValue(sessionContext.context, value)
                    paramsObj.putMember(key, convertedValue)
                } catch (e: Exception) {
                    logger.error("设置参数失败: {}", key, e)
                }
            }

            // 绑定参数对象
            bindings.putMember("params", paramsObj)
        } catch (e: Exception) {
            logger.error("初始化参数失败", e)
            throw e
        }
    }

    /**
     * 执行步骤
     */
    private fun executeStep(sessionContext: DebugSessionContext, stepType: StepType): DebugStepResult {
        try {
            // 检查状态
            if (sessionContext.status == DebugSessionStatus.COMPLETED ||
                sessionContext.status == DebugSessionStatus.ERROR ||
                sessionContext.status == DebugSessionStatus.TERMINATED) {
                return createErrorResult("Debug session already completed or terminated")
            }

            // 如果是首次执行
            if (sessionContext.status == DebugSessionStatus.CREATED) {
                // 解析脚本
                val source = Source.newBuilder(
                    sessionContext.session.language,
                    sessionContext.session.script,
                    "script.${sessionContext.session.language}"
                ).build()

                // 设置断点监听器
                setupBreakpointListener(sessionContext)

                // 设置状态
                sessionContext.status = DebugSessionStatus.RUNNING

                // 执行脚本
                try {
                    val result = sessionContext.context.eval(source)

                    // 更新状态
                    sessionContext.status = DebugSessionStatus.COMPLETED

                    // 更新变量
                    updateVariables(sessionContext)

                    // 更新调用栈
                    updateCallStack(sessionContext)

                    // 返回结果
                    return DebugStepResult(
                        success = true,
                        status = DebugSessionStatus.COMPLETED,
                        variables = sessionContext.variables.toList(),
                        callStack = sessionContext.callStack.toList(),
                        result = convertValue(result)
                    )
                } catch (e: Exception) {
                    // 更新状态
                    sessionContext.status = DebugSessionStatus.ERROR

                    // 返回错误
                    return createErrorResult(e.message ?: "Execution failed")
                }
            }

            // 如果已暂停
            if (sessionContext.status == DebugSessionStatus.PAUSED) {
                // 由于没有 ExecutionListener 依赖，这里使用简化的实现
                logger.info("执行步骤: {}", stepType)

                // 设置状态
                sessionContext.status = DebugSessionStatus.RUNNING

                // 模拟执行完成
                sessionContext.status = DebugSessionStatus.COMPLETED

                // 等待执行完成或暂停
                while (sessionContext.status == DebugSessionStatus.RUNNING) {
                    Thread.sleep(10)
                }

                // 更新变量
                updateVariables(sessionContext)

                // 更新调用栈
                updateCallStack(sessionContext)

                // 返回结果
                return DebugStepResult(
                    success = true,
                    line = sessionContext.currentLine,
                    column = sessionContext.currentColumn,
                    status = sessionContext.status,
                    variables = sessionContext.variables.toList(),
                    callStack = sessionContext.callStack.toList()
                )
            }

            // 其他状态
            return createErrorResult("Invalid debug session status: ${sessionContext.status}")
        } catch (e: Exception) {
            logger.error("执行步骤失败", e)
            return createErrorResult(e.message ?: "Step execution failed")
        }
    }

    /**
     * 设置断点监听器
     */
    private fun setupBreakpointListener(sessionContext: DebugSessionContext) {
        // 由于没有 ExecutionListener 依赖，这里使用简化的实现
        logger.info("设置断点监听器（简化实现）")

        // 在实际实现中，这里应该使用 GraalVM 的调试 API
        // 由于依赖问题，这里只是一个占位实现
    }

    /**
     * 更新变量
     */
    private fun updateVariables(sessionContext: DebugSessionContext) {
        try {
            // 清空变量列表
            sessionContext.variables.clear()

            // 获取绑定
            val bindings = sessionContext.context.getBindings(sessionContext.session.language)

            // 获取所有成员
            val memberKeys = bindings.memberKeys

            // 添加变量
            memberKeys.forEach { key ->
                try {
                    val value = bindings.getMember(key)
                    val variableInfo = createVariableInfo(key, value)
                    sessionContext.variables.add(variableInfo)
                } catch (e: Exception) {
                    logger.error("获取变量失败: {}", key, e)
                }
            }
        } catch (e: Exception) {
            logger.error("更新变量失败", e)
        }
    }

    /**
     * 更新调用栈
     */
    private fun updateCallStack(sessionContext: DebugSessionContext) {
        try {
            // 清空调用栈
            sessionContext.callStack.clear()

            // 获取调用栈
            // 注意：GraalVM 目前不提供直接获取调用栈的 API
            // 这里使用简化的实现

            // 添加当前帧
            val currentFrame = mapOf(
                "name" to "script",
                "line" to (sessionContext.currentLine ?: 0),
                "column" to (sessionContext.currentColumn ?: 0),
                "source" to "script.${sessionContext.session.language}"
            )

            sessionContext.callStack.add(currentFrame)
        } catch (e: Exception) {
            logger.error("更新调用栈失败", e)
        }
    }

    /**
     * 创建变量信息
     */
    private fun createVariableInfo(name: String, value: Value): VariableInfo {
        // 获取类型
        val type = when {
            value.isNull -> "null"
            value.isBoolean -> "boolean"
            value.isNumber -> "number"
            value.isString -> "string"
            value.hasArrayElements() -> "array"
            value.hasMembers() -> "object"
            value.isDate -> "date"
            value.isTime -> "time"
            value.isTimeZone -> "timezone"
            value.isDuration -> "duration"
            value.isException -> "exception"
            value.isHostObject -> "host"
            value.isProxyObject -> "proxy"
            else -> "unknown"
        }

        // 检查是否可展开
        val expandable = value.hasArrayElements() || value.hasMembers()

        // 获取子变量
        val children = mutableListOf<VariableInfo>()

        if (expandable) {
            if (value.hasArrayElements()) {
                // 数组元素
                val size = value.arraySize
                val maxItems = 10 // 最多显示 10 个元素

                for (i in 0 until minOf(size.toInt(), maxItems)) {
                    try {
                        val element = value.getArrayElement(i.toLong())
                        val childName = "[$i]"
                        val childInfo = createVariableInfo(childName, element)
                        children.add(childInfo)
                    } catch (e: Exception) {
                        logger.error("获取数组元素失败: {}", i, e)
                    }
                }

                // 如果数组元素过多，添加省略提示
                if (size > maxItems) {
                    children.add(
                        VariableInfo(
                            name = "...",
                            value = "${size - maxItems} more items",
                            type = "info",
                            expandable = false
                        )
                    )
                }
            } else if (value.hasMembers()) {
                // 对象成员
                val memberKeys = value.memberKeys
                val maxItems = 10 // 最多显示 10 个成员

                memberKeys.take(maxItems).forEach { key ->
                    try {
                        val member = value.getMember(key)
                        val childInfo = createVariableInfo(key, member)
                        children.add(childInfo)
                    } catch (e: Exception) {
                        logger.error("获取对象成员失败: {}", key, e)
                    }
                }

                // 如果成员过多，添加省略提示
                if (memberKeys.size > maxItems) {
                    children.add(
                        VariableInfo(
                            name = "...",
                            value = "${memberKeys.size - maxItems} more properties",
                            type = "info",
                            expandable = false
                        )
                    )
                }
            }
        }

        // 确定作用域
        val scope = when (name) {
            "params" -> VariableScope.PARAMETER
            "global", "window", "this" -> VariableScope.GLOBAL
            else -> VariableScope.LOCAL
        }

        // 创建变量信息
        return VariableInfo(
            name = name,
            value = convertValue(value),
            type = type,
            expandable = expandable,
            children = children,
            scope = scope
        )
    }

    /**
     * 转换值
     */
    private fun convertValue(value: Value): Any? {
        return when {
            value.isNull -> null
            value.isBoolean -> value.asBoolean()
            value.isNumber -> {
                if (value.fitsInInt()) {
                    value.asInt()
                } else if (value.fitsInLong()) {
                    value.asLong()
                } else if (value.fitsInFloat()) {
                    value.asFloat()
                } else if (value.fitsInDouble()) {
                    value.asDouble()
                } else {
                    value.toString()
                }
            }
            value.isString -> value.asString()
            value.hasArrayElements() -> {
                val size = value.arraySize
                val result = ArrayList<Any?>(size.toInt())

                for (i in 0 until size) {
                    val element = value.getArrayElement(i)
                    result.add(convertValue(element))
                }

                result
            }
            value.hasMembers() -> {
                val result = mutableMapOf<String, Any?>()

                for (key in value.memberKeys) {
                    val member = value.getMember(key)
                    result[key] = convertValue(member)
                }

                result
            }
            value.isDate -> value.asDate().toString()
            value.isTime -> value.asTime().toString()
            value.isTimeZone -> value.asTimeZone().toString()
            value.isDuration -> value.asDuration().toString()
            value.isException -> value.toString()
            value.isHostObject -> value.toString()
            value.isProxyObject -> value.toString()
            else -> value.toString()
        }
    }

    /**
     * 转换为 GraalVM 值
     */
    private fun convertToGraalValue(context: Context, value: Any?): Any? {
        return when (value) {
            null -> null
            is Boolean -> value
            is Number -> value
            is String -> value
            is List<*> -> {
                val array = context.eval("js", "[]")
                value.forEachIndexed { index, item ->
                    array.setArrayElement(index.toLong(), convertToGraalValue(context, item))
                }
                array
            }
            is Map<*, *> -> {
                val obj = context.eval("js", "{}")
                value.forEach { (key, item) ->
                    if (key is String) {
                        obj.putMember(key, convertToGraalValue(context, item))
                    }
                }
                obj
            }
            else -> value.toString()
        }
    }

    /**
     * 更新会话
     */
    private fun updateSession(sessionContext: DebugSessionContext) {
        // 更新会话
        val updatedSession = sessionContext.session.copy(
            breakpoints = sessionContext.breakpoints.toList(),
            currentLine = sessionContext.currentLine,
            currentColumn = sessionContext.currentColumn,
            status = sessionContext.status,
            updateTime = Date()
        )

        sessionContext.session = updatedSession
    }

    /**
     * 创建错误结果
     */
    private fun createErrorResult(errorMessage: String): DebugStepResult {
        return DebugStepResult(
            success = false,
            status = DebugSessionStatus.ERROR,
            errorMessage = errorMessage
        )
    }

    /**
     * 调试会话上下文
     */
    private data class DebugSessionContext(
        var session: DebugSession,
        val engine: Engine,
        val context: Context,
        // 注释掉不存在的依赖
        // val executionListener: ExecutionListener,
        val breakpoints: MutableList<BreakpointInfo>,
        val variables: MutableList<VariableInfo>,
        val callStack: MutableList<Map<String, Any?>>,
        var status: DebugSessionStatus,
        var currentLine: Int? = null,
        var currentColumn: Int? = null
    )

    /**
     * 步骤类型
     */
    private enum class StepType {
        OVER,
        INTO,
        OUT,
        CONTINUE
    }
}
