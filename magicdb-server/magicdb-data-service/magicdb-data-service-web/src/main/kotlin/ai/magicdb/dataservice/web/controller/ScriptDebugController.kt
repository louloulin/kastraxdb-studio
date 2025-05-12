package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.ScriptDebugger
import ai.magicdb.dataservice.api.model.ScriptDebugRequest
import ai.magicdb.dataservice.api.model.ScriptDebugResult
import ai.magicdb.dataservice.api.model.ServiceResult
import ai.magicdb.script.api.Breakpoint
import ai.magicdb.script.api.DebugStatus
import ai.magicdb.script.api.StackFrame
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

/**
 * 脚本调试控制器
 *
 * @author magicdb
 */
// @RestController
// @RequestMapping("/api/data-service/script")
class ScriptDebugController(private val scriptDebugger: ScriptDebugger) {
    private val logger = LoggerFactory.getLogger(ScriptDebugController::class.java)

    // 暂时禁用脚本调试控制器
    /*

    /**
     * 调试脚本
     *
     * @param request 调试请求
     * @return 调试结果
     */
    @PostMapping("/debug")
    fun debug(@RequestBody request: ScriptDebugRequest): DataResult<ScriptDebugResult> {
        val result = scriptDebugger.debug(request)
        return DataResult.of(result)
    }

    /**
     * 获取脚本语言列表
     *
     * @return 脚本语言列表
     */
    @GetMapping("/languages")
    fun getLanguages(): ListResult<String> {
        val languages = scriptDebugger.getLanguages()
        return ListResult.of(languages)
    }

    /**
     * 获取脚本模板
     *
     * @param language 脚本语言
     * @return 脚本模板
     */
    @GetMapping("/template")
    fun getTemplate(@RequestParam language: String): DataResult<String> {
        val template = scriptDebugger.getTemplate(language)
        return DataResult.of(template)
    }

    /**
     * 创建调试会话
     */
    @PostMapping("/debug/session")
    fun createSession(@RequestBody request: CreateSessionRequest): DataResult<Map<String, String>> {
        try {
            val sessionId = scriptDebugger.createSession(
                request.script,
                request.language,
                request.context ?: emptyMap()
            )

            return DataResult.of(mapOf("sessionId" to sessionId))
        } catch (e: Exception) {
            logger.error("创建调试会话失败: {}", e.message, e)
            return DataResult.error("500", "创建调试会话失败: ${e.message}")
        }
    }

    /**
     * 设置断点
     */
    @PostMapping("/debug/breakpoint")
    fun setBreakpoint(@RequestBody request: SetBreakpointRequest): DataResult<Map<String, String>> {
        try {
            val breakpointId = scriptDebugger.setBreakpoint(
                request.sessionId,
                request.lineNumber,
                request.condition
            )

            return DataResult.of(mapOf("breakpointId" to breakpointId))
        } catch (e: Exception) {
            logger.error("设置断点失败: {}", e.message, e)
            return DataResult.error("500", "设置断点失败: ${e.message}")
        }
    }

    /**
     * 删除断点
     */
    @DeleteMapping("/debug/breakpoint")
    fun removeBreakpoint(@RequestBody request: RemoveBreakpointRequest): DataResult<Boolean> {
        try {
            val result = scriptDebugger.removeBreakpoint(
                request.sessionId,
                request.breakpointId
            )

            return DataResult.of(result)
        } catch (e: Exception) {
            logger.error("删除断点失败: {}", e.message, e)
            return DataResult.error("500", "删除断点失败: ${e.message}")
        }
    }

    /**
     * 获取所有断点
     */
    @GetMapping("/debug/breakpoints/{sessionId}")
    fun getBreakpoints(@PathVariable sessionId: String): DataResult<List<Breakpoint>> {
        try {
            val breakpoints = scriptDebugger.getBreakpoints(sessionId)

            return DataResult.of(breakpoints)
        } catch (e: Exception) {
            logger.error("获取断点失败: {}", e.message, e)
            return DataResult.error("500", "获取断点失败: ${e.message}")
        }
    }

    /**
     * 开始执行
     */
    @PostMapping("/debug/start")
    fun start(@RequestBody request: SessionRequest): DataResult<DebugStatus> {
        try {
            val status = scriptDebugger.start(request.sessionId)

            return DataResult.of(status)
        } catch (e: Exception) {
            logger.error("开始执行失败: {}", e.message, e)
            return DataResult.error("500", "开始执行失败: ${e.message}")
        }
    }

    /**
     * 继续执行
     */
    @PostMapping("/debug/continue")
    fun continue_(@RequestBody request: SessionRequest): DataResult<DebugStatus> {
        try {
            val status = scriptDebugger.continue_(request.sessionId)

            return DataResult.of(status)
        } catch (e: Exception) {
            logger.error("继续执行失败: {}", e.message, e)
            return DataResult.error("500", "继续执行失败: ${e.message}")
        }
    }

    /**
     * 单步执行
     */
    @PostMapping("/debug/step-over")
    fun stepOver(@RequestBody request: SessionRequest): DataResult<DebugStatus> {
        try {
            val status = scriptDebugger.stepOver(request.sessionId)

            return DataResult.of(status)
        } catch (e: Exception) {
            logger.error("单步执行失败: {}", e.message, e)
            return DataResult.error("500", "单步执行失败: ${e.message}")
        }
    }

    /**
     * 步入函数
     */
    @PostMapping("/debug/step-into")
    fun stepInto(@RequestBody request: SessionRequest): DataResult<DebugStatus> {
        try {
            val status = scriptDebugger.stepInto(request.sessionId)

            return DataResult.of(status)
        } catch (e: Exception) {
            logger.error("步入函数失败: {}", e.message, e)
            return DataResult.error("500", "步入函数失败: ${e.message}")
        }
    }

    /**
     * 步出函数
     */
    @PostMapping("/debug/step-out")
    fun stepOut(@RequestBody request: SessionRequest): DataResult<DebugStatus> {
        try {
            val status = scriptDebugger.stepOut(request.sessionId)

            return DataResult.of(status)
        } catch (e: Exception) {
            logger.error("步出函数失败: {}", e.message, e)
            return DataResult.error("500", "步出函数失败: ${e.message}")
        }
    }

    /**
     * 暂停执行
     */
    @PostMapping("/debug/pause")
    fun pause(@RequestBody request: SessionRequest): DataResult<DebugStatus> {
        try {
            val status = scriptDebugger.pause(request.sessionId)

            return DataResult.of(status)
        } catch (e: Exception) {
            logger.error("暂停执行失败: {}", e.message, e)
            return DataResult.error("500", "暂停执行失败: ${e.message}")
        }
    }

    /**
     * 停止执行
     */
    @PostMapping("/debug/stop")
    fun stop(@RequestBody request: SessionRequest): DataResult<Boolean> {
        try {
            val result = scriptDebugger.stop(request.sessionId)

            return DataResult.of(result)
        } catch (e: Exception) {
            logger.error("停止执行失败: {}", e.message, e)
            return DataResult.error("500", "停止执行失败: ${e.message}")
        }
    }

    /**
     * 获取变量
     */
    @GetMapping("/debug/variable/{sessionId}/{variableName}")
    fun getVariable(
        @PathVariable sessionId: String,
        @PathVariable variableName: String
    ): DataResult<Any?> {
        try {
            val value = scriptDebugger.getVariable(sessionId, variableName)

            return DataResult.of(value)
        } catch (e: Exception) {
            logger.error("获取变量失败: {}", e.message, e)
            return DataResult.error("500", "获取变量失败: ${e.message}")
        }
    }

    /**
     * 获取所有变量
     */
    @GetMapping("/debug/variables/{sessionId}")
    fun getVariables(@PathVariable sessionId: String): DataResult<Map<String, Any?>> {
        try {
            val variables = scriptDebugger.getVariables(sessionId)

            return DataResult.of(variables)
        } catch (e: Exception) {
            logger.error("获取变量失败: {}", e.message, e)
            return DataResult.error("500", "获取变量失败: ${e.message}")
        }
    }

    /**
     * 计算表达式
     */
    @PostMapping("/debug/evaluate")
    fun evaluate(@RequestBody request: EvaluateRequest): DataResult<Any?> {
        try {
            val result = scriptDebugger.evaluate(request.sessionId, request.expression)

            return DataResult.of(result)
        } catch (e: Exception) {
            logger.error("计算表达式失败: {}", e.message, e)
            return DataResult.error("500", "计算表达式失败: ${e.message}")
        }
    }

    /**
     * 获取调用栈
     */
    @GetMapping("/debug/call-stack/{sessionId}")
    fun getCallStack(@PathVariable sessionId: String): DataResult<List<StackFrame>> {
        try {
            val callStack = scriptDebugger.getCallStack(sessionId)

            return DataResult.of(callStack)
        } catch (e: Exception) {
            logger.error("获取调用栈失败: {}", e.message, e)
            return DataResult.error("500", "获取调用栈失败: ${e.message}")
        }
    }

    /**
     * 获取状态
     */
    @GetMapping("/debug/status/{sessionId}")
    fun getStatus(@PathVariable sessionId: String): DataResult<DebugStatus> {
        try {
            val status = scriptDebugger.getStatus(sessionId)

            return DataResult.of(status)
        } catch (e: Exception) {
            logger.error("获取状态失败: {}", e.message, e)
            return DataResult.error("500", "获取状态失败: ${e.message}")
        }
    }

    /**
     * 关闭会话
     */
    @DeleteMapping("/debug/session/{sessionId}")
    fun closeSession(@PathVariable sessionId: String): DataResult<Boolean> {
        try {
            val result = scriptDebugger.closeSession(sessionId)

            return DataResult.of(result)
        } catch (e: Exception) {
            logger.error("关闭会话失败: {}", e.message, e)
            return DataResult.error("500", "关闭会话失败: ${e.message}")
        }
    }

    /**
     * 创建会话请求
     */
    data class CreateSessionRequest(
        val script: String,
        val language: String,
        val context: Map<String, Any?>?
    )

    /**
     * 设置断点请求
     */
    data class SetBreakpointRequest(
        val sessionId: String,
        val lineNumber: Int,
        val condition: String?
    )

    /**
     * 删除断点请求
     */
    data class RemoveBreakpointRequest(
        val sessionId: String,
        val breakpointId: String
    )

    /**
     * 会话请求
     */
    data class SessionRequest(
        val sessionId: String
    )

    /**
     * 计算表达式请求
     */
    data class EvaluateRequest(
        val sessionId: String,
        val expression: String
    )
    */
}