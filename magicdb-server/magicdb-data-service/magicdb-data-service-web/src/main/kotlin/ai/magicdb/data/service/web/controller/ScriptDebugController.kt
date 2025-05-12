package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.ScriptDebugService
import ai.magicdb.data.service.api.model.BreakpointInfo
import ai.magicdb.data.service.api.model.DebugSession
import ai.magicdb.data.service.api.model.DebugStepResult
import ai.magicdb.data.service.api.model.VariableInfo
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 脚本调试控制器
 */
@RestController
@RequestMapping("/api/script-debug")
class ScriptDebugController(
    private val scriptDebugService: ScriptDebugService
) {

    /**
     * 创建调试会话
     */
    @PostMapping("/session")
    fun createDebugSession(@RequestBody request: CreateSessionRequest): DataResult<DebugSession> {
        val session = scriptDebugService.createDebugSession(request.serviceId, request.parameters)
        return DataResult.of(session)
    }

    /**
     * 获取调试会话
     */
    @GetMapping("/session/{id}")
    fun getDebugSession(@PathVariable("id") sessionId: String): DataResult<DebugSession?> {
        val session = scriptDebugService.getDebugSession(sessionId)
        return DataResult.of(session)
    }

    /**
     * 结束调试会话
     */
    @DeleteMapping("/session/{id}")
    fun terminateDebugSession(@PathVariable("id") sessionId: String): DataResult<Boolean> {
        val result = scriptDebugService.terminateDebugSession(sessionId)
        return DataResult.of(result)
    }

    /**
     * 添加断点
     */
    @PostMapping("/session/{id}/breakpoint")
    fun addBreakpoint(
        @PathVariable("id") sessionId: String,
        @RequestBody breakpoint: BreakpointInfo
    ): DataResult<Boolean> {
        val result = scriptDebugService.addBreakpoint(sessionId, breakpoint)
        return DataResult.of(result)
    }

    /**
     * 删除断点
     */
    @DeleteMapping("/session/{id}/breakpoint/{breakpointId}")
    fun removeBreakpoint(
        @PathVariable("id") sessionId: String,
        @PathVariable("breakpointId") breakpointId: String
    ): DataResult<Boolean> {
        val result = scriptDebugService.removeBreakpoint(sessionId, breakpointId)
        return DataResult.of(result)
    }

    /**
     * 获取所有断点
     */
    @GetMapping("/session/{id}/breakpoints")
    fun getBreakpoints(@PathVariable("id") sessionId: String): DataResult<List<BreakpointInfo>> {
        val breakpoints = scriptDebugService.getBreakpoints(sessionId)
        return DataResult.of(breakpoints)
    }

    /**
     * 单步执行
     */
    @PostMapping("/session/{id}/step-over")
    fun stepOver(@PathVariable("id") sessionId: String): DataResult<DebugStepResult> {
        val result = scriptDebugService.stepOver(sessionId)
        return DataResult.of(result)
    }

    /**
     * 单步进入
     */
    @PostMapping("/session/{id}/step-into")
    fun stepInto(@PathVariable("id") sessionId: String): DataResult<DebugStepResult> {
        val result = scriptDebugService.stepInto(sessionId)
        return DataResult.of(result)
    }

    /**
     * 单步跳出
     */
    @PostMapping("/session/{id}/step-out")
    fun stepOut(@PathVariable("id") sessionId: String): DataResult<DebugStepResult> {
        val result = scriptDebugService.stepOut(sessionId)
        return DataResult.of(result)
    }

    /**
     * 继续执行
     */
    @PostMapping("/session/{id}/continue")
    fun continueExecution(@PathVariable("id") sessionId: String): DataResult<DebugStepResult> {
        val result = scriptDebugService.continueExecution(sessionId)
        return DataResult.of(result)
    }

    /**
     * 获取变量
     */
    @GetMapping("/session/{id}/variables")
    fun getVariables(@PathVariable("id") sessionId: String): DataResult<List<VariableInfo>> {
        val variables = scriptDebugService.getVariables(sessionId)
        return DataResult.of(variables)
    }

    /**
     * 获取调用栈
     */
    @GetMapping("/session/{id}/call-stack")
    fun getCallStack(@PathVariable("id") sessionId: String): DataResult<List<Map<String, Any?>>> {
        val callStack = scriptDebugService.getCallStack(sessionId)
        return DataResult.of(callStack)
    }

    /**
     * 执行表达式
     */
    @PostMapping("/session/{id}/evaluate")
    fun evaluateExpression(
        @PathVariable("id") sessionId: String,
        @RequestBody request: EvaluateRequest
    ): DataResult<Any?> {
        val result = scriptDebugService.evaluateExpression(sessionId, request.expression)
        return DataResult.of(result)
    }

    /**
     * 创建会话请求
     */
    data class CreateSessionRequest(
        val serviceId: String,
        val parameters: Map<String, Any?> = emptyMap()
    )

    /**
     * 执行表达式请求
     */
    data class EvaluateRequest(
        val expression: String
    )
}
