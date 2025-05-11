package ai.magicdb.script.engine.debug

import ai.magicdb.script.api.Breakpoint
import ai.magicdb.script.api.DebugState
import ai.magicdb.script.api.DebugStatus
import ai.magicdb.script.api.StackFrame
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

/**
 * 调试会话
 *
 * @author magicdb
 */
class DebugSession(
    val id: String,
    val script: String,
    val language: String,
    val initialContext: Map<String, Any?>
) {
    private val logger = LoggerFactory.getLogger(DebugSession::class.java)

    // 断点管理
    private val breakpoints = ConcurrentHashMap<String, Breakpoint>()

    // 当前状态
    private val currentStatus = AtomicReference<DebugStatus>(
        DebugStatus(
            sessionId = id,
            currentLine = 0,
            currentFile = "script.$language",
            state = DebugState.NOT_STARTED,
            error = null
        )
    )

    // 当前变量
    private val variables = ConcurrentHashMap<String, Any?>()

    // 调用栈
    private val callStack = mutableListOf<StackFrame>()

    // 调试线程
    private var debugThread: Thread? = null

    // 调试控制
    private val pauseLatch = CountDownLatch(1)
    private var stepMode = StepMode.CONTINUE
    private var stepDepth = 0

    /**
     * 设置断点
     */
    fun setBreakpoint(lineNumber: Int, condition: String?): String {
        val breakpointId = UUID.randomUUID().toString()
        val breakpoint = Breakpoint(
            id = breakpointId,
            lineNumber = lineNumber,
            condition = condition,
            enabled = true
        )
        breakpoints[breakpointId] = breakpoint
        return breakpointId
    }

    /**
     * 删除断点
     */
    fun removeBreakpoint(breakpointId: String): Boolean {
        return breakpoints.remove(breakpointId) != null
    }

    /**
     * 获取所有断点
     */
    fun getBreakpoints(): List<Breakpoint> {
        return breakpoints.values.toList()
    }

    /**
     * 开始执行
     */
    fun start(): DebugStatus {
        // 检查状态
        val status = currentStatus.get()
        if (status.state != DebugState.NOT_STARTED) {
            return status
        }

        // 初始化变量
        variables.clear()
        variables.putAll(initialContext)

        // 创建调试线程
        debugThread = Thread {
            try {
                // 更新状态
                updateStatus(DebugState.RUNNING, 1)

                // 解析脚本
                val lines = script.lines()

                // 模拟执行脚本
                simulateExecution(lines)

                // 更新状态
                updateStatus(DebugState.COMPLETED, lines.size)
            } catch (e: Exception) {
                logger.error("调试执行出错: {}", e.message, e)
                updateStatus(DebugState.ERROR, currentStatus.get().currentLine, e.message)
            }
        }

        // 启动线程
        debugThread?.start()

        return currentStatus.get()
    }

    /**
     * 继续执行
     */
    fun continue_(): DebugStatus {
        val status = currentStatus.get()
        if (status.state != DebugState.PAUSED) {
            return status
        }

        stepMode = StepMode.CONTINUE
        pauseLatch.countDown()

        return currentStatus.get()
    }

    /**
     * 单步执行
     */
    fun stepOver(): DebugStatus {
        val status = currentStatus.get()
        if (status.state != DebugState.PAUSED) {
            return status
        }

        stepMode = StepMode.STEP_OVER
        stepDepth = callStack.size
        pauseLatch.countDown()

        return currentStatus.get()
    }

    /**
     * 步入函数
     */
    fun stepInto(): DebugStatus {
        val status = currentStatus.get()
        if (status.state != DebugState.PAUSED) {
            return status
        }

        stepMode = StepMode.STEP_INTO
        pauseLatch.countDown()

        return currentStatus.get()
    }

    /**
     * 步出函数
     */
    fun stepOut(): DebugStatus {
        val status = currentStatus.get()
        if (status.state != DebugState.PAUSED) {
            return status
        }

        stepMode = StepMode.STEP_OUT
        stepDepth = callStack.size - 1
        if (stepDepth < 0) stepDepth = 0
        pauseLatch.countDown()

        return currentStatus.get()
    }

    /**
     * 暂停执行
     */
    fun pause(): DebugStatus {
        val status = currentStatus.get()
        if (status.state != DebugState.RUNNING) {
            return status
        }

        // 设置暂停标志
        updateStatus(DebugState.PAUSED, status.currentLine)

        return currentStatus.get()
    }

    /**
     * 停止执行
     */
    fun stop(): Boolean {
        val status = currentStatus.get()
        if (status.state == DebugState.COMPLETED || status.state == DebugState.ERROR) {
            return true
        }

        // 中断线程
        debugThread?.interrupt()

        // 更新状态
        updateStatus(DebugState.COMPLETED, status.currentLine)

        // 释放暂停锁
        pauseLatch.countDown()

        return true
    }

    /**
     * 获取变量值
     */
    fun getVariable(variableName: String): Any? {
        return variables[variableName]
    }

    /**
     * 获取所有变量
     */
    fun getVariables(): Map<String, Any?> {
        return variables.toMap()
    }

    /**
     * 计算表达式
     */
    fun evaluate(expression: String): Any? {
        // 简单实现，仅支持变量访问
        return when {
            variables.containsKey(expression) -> variables[expression]
            expression.contains("+") -> {
                val parts = expression.split("+").map { it.trim() }
                if (parts.size == 2) {
                    val left = evaluateSimple(parts[0])
                    val right = evaluateSimple(parts[1])
                    if (left is Number && right is Number) {
                        left.toDouble() + right.toDouble()
                    } else if (left is String && right is String) {
                        left + right
                    } else {
                        "$left$right"
                    }
                } else {
                    "表达式无法计算: $expression"
                }
            }
            else -> "表达式无法计算: $expression"
        }
    }

    /**
     * 计算简单表达式
     */
    private fun evaluateSimple(expr: String): Any? {
        return when {
            variables.containsKey(expr) -> variables[expr]
            expr.toIntOrNull() != null -> expr.toInt()
            expr.toDoubleOrNull() != null -> expr.toDouble()
            expr.startsWith("\"") && expr.endsWith("\"") -> expr.substring(1, expr.length - 1)
            expr == "true" -> true
            expr == "false" -> false
            expr == "null" -> null
            else -> expr
        }
    }

    /**
     * 获取调用栈
     */
    fun getCallStack(): List<StackFrame> {
        return callStack.toList()
    }

    /**
     * 获取状态
     */
    fun getStatus(): DebugStatus {
        return currentStatus.get()
    }

    /**
     * 更新状态
     */
    private fun updateStatus(state: DebugState, line: Int, error: String? = null) {
        currentStatus.set(
            DebugStatus(
                sessionId = id,
                currentLine = line,
                currentFile = "script.$language",
                state = state,
                error = error
            )
        )
    }

    /**
     * 模拟执行脚本
     */
    private fun simulateExecution(lines: List<String>) {
        var lineIndex = 0

        while (lineIndex < lines.size) {
            // 检查是否中断
            if (Thread.currentThread().isInterrupted) {
                break
            }

            // 当前行号（1-based）
            val lineNumber = lineIndex + 1

            // 更新当前行
            updateStatus(currentStatus.get().state, lineNumber)

            // 检查断点
            val hitBreakpoint = checkBreakpoint(lineNumber)

            // 检查是否需要暂停
            val shouldPause = hitBreakpoint ||
                              currentStatus.get().state == DebugState.PAUSED ||
                              (stepMode == StepMode.STEP_INTO) ||
                              (stepMode == StepMode.STEP_OVER && callStack.size <= stepDepth) ||
                              (stepMode == StepMode.STEP_OUT && callStack.size <= stepDepth)

            if (shouldPause) {
                // 更新状态为暂停
                updateStatus(DebugState.PAUSED, lineNumber)

                // 等待继续执行
                try {
                    pauseLatch.await()
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }

                // 重置暂停锁
                pauseLatch.countDown()

                // 更新状态为运行
                updateStatus(DebugState.RUNNING, lineNumber)
            }

            // 模拟执行当前行
            executeCurrentLine(lines[lineIndex], lineNumber)

            // 下一行
            lineIndex++
        }
    }

    /**
     * 检查断点
     */
    private fun checkBreakpoint(lineNumber: Int): Boolean {
        for (breakpoint in breakpoints.values) {
            if (breakpoint.enabled && breakpoint.lineNumber == lineNumber) {
                // 检查条件
                val condition = breakpoint.condition
                if (condition != null) {
                    val result = evaluate(condition)
                    if (result is Boolean && result) {
                        return true
                    }
                } else {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 模拟执行当前行
     */
    private fun executeCurrentLine(line: String, lineNumber: Int) {
        // 简单模拟变量赋值
        val trimmedLine = line.trim()

        if (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith("//")) {
            // 注释或空行，跳过
            return
        }

        if (trimmedLine.contains("=") && !trimmedLine.contains("==")) {
            // 变量赋值
            val parts = trimmedLine.split("=", limit = 2)
            if (parts.size == 2) {
                val varName = parts[0].trim()
                val varValue = evaluateSimple(parts[1].trim())
                variables[varName] = varValue
            }
        } else if (trimmedLine.startsWith("function") || trimmedLine.startsWith("def ")) {
            // 函数定义
            val functionName = trimmedLine.substringAfter("function ").substringAfter("def ")
                .substringBefore("(").trim()

            // 模拟函数调用栈
            callStack.add(
                StackFrame(
                    functionName = functionName,
                    fileName = "script.$language",
                    lineNumber = lineNumber,
                    localVariables = emptyMap()
                )
            )
        } else if (trimmedLine == "}" || trimmedLine == "end" || trimmedLine.startsWith("return")) {
            // 函数结束
            if (callStack.isNotEmpty()) {
                callStack.removeAt(callStack.size - 1)
            }
        }

        // 模拟执行延迟
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    /**
     * 步进模式
     */
    enum class StepMode {
        CONTINUE,
        STEP_INTO,
        STEP_OVER,
        STEP_OUT
    }
}
