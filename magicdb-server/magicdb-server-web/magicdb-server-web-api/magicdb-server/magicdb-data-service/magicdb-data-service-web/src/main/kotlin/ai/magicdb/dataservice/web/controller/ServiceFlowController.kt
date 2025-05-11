package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.ServiceOrchestrator
import ai.magicdb.dataservice.api.model.ServiceFlow
import ai.magicdb.dataservice.api.model.ServiceFlowExecution
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

/**
 * 服务流程控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/flow")
class ServiceFlowController(private val serviceOrchestrator: ServiceOrchestrator) {
    private val logger = LoggerFactory.getLogger(ServiceFlowController::class.java)

    /**
     * 创建服务流程
     *
     * @param flow 服务流程
     * @return 流程ID
     */
    @PostMapping
    fun createFlow(@RequestBody flow: ServiceFlow): DataResult<String> {
        logger.info("创建服务流程: {}", flow.name)
        val flowId = serviceOrchestrator.createFlow(flow)
        return DataResult.of(flowId)
    }

    /**
     * 更新服务流程
     *
     * @param flow 服务流程
     * @return 是否成功
     */
    @PutMapping
    fun updateFlow(@RequestBody flow: ServiceFlow): ActionResult {
        logger.info("更新服务流程: {}", flow.id)
        val success = serviceOrchestrator.updateFlow(flow)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "更新服务流程失败", "")
        }
    }

    /**
     * 删除服务流程
     *
     * @param flowId 流程ID
     * @return 是否成功
     */
    @DeleteMapping("/{flowId}")
    fun deleteFlow(@PathVariable flowId: String): ActionResult {
        logger.info("删除服务流程: {}", flowId)
        val success = serviceOrchestrator.deleteFlow(flowId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "删除服务流程失败", "")
        }
    }

    /**
     * 获取服务流程
     *
     * @param flowId 流程ID
     * @return 服务流程
     */
    @GetMapping("/{flowId}")
    fun getFlow(@PathVariable flowId: String): DataResult<ServiceFlow> {
        logger.info("获取服务流程: {}", flowId)
        val flow = serviceOrchestrator.getFlow(flowId)
        return DataResult.of(flow)
    }

    /**
     * 获取所有服务流程
     *
     * @return 服务流程列表
     */
    @GetMapping
    fun getAllFlows(): ListResult<ServiceFlow> {
        logger.info("获取所有服务流程")
        val flows = serviceOrchestrator.getAllFlows()
        return ListResult.of(flows)
    }

    /**
     * 执行服务流程
     *
     * @param flowId 流程ID
     * @param parameters 参数
     * @return 执行ID
     */
    @PostMapping("/{flowId}/execute")
    fun executeFlow(
        @PathVariable flowId: String,
        @RequestBody parameters: Map<String, Any?>
    ): DataResult<String> {
        logger.info("执行服务流程: {}", flowId)
        val executionId = serviceOrchestrator.executeFlow(flowId, parameters)
        return DataResult.of(executionId)
    }

    /**
     * 取消流程执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    @PostMapping("/execution/{executionId}/cancel")
    fun cancelExecution(@PathVariable executionId: String): ActionResult {
        logger.info("取消流程执行: {}", executionId)
        val success = serviceOrchestrator.cancelExecution(executionId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "取消流程执行失败", "")
        }
    }

    /**
     * 获取流程执行状态
     *
     * @param executionId 执行ID
     * @return 执行状态
     */
    @GetMapping("/execution/{executionId}")
    fun getExecutionStatus(@PathVariable executionId: String): DataResult<ServiceFlowExecution> {
        logger.info("获取流程执行状态: {}", executionId)
        val execution = serviceOrchestrator.getExecutionStatus(executionId)
        return DataResult.of(execution)
    }

    /**
     * 获取流程执行结果
     *
     * @param executionId 执行ID
     * @return 执行结果
     */
    @GetMapping("/execution/{executionId}/result")
    fun getExecutionResult(@PathVariable executionId: String): DataResult<Any> {
        logger.info("获取流程执行结果: {}", executionId)
        val result = serviceOrchestrator.getExecutionResult(executionId)
        return DataResult.of(result)
    }

    /**
     * 获取所有流程执行
     *
     * @return 执行列表
     */
    @GetMapping("/execution")
    fun getAllExecutions(): ListResult<ServiceFlowExecution> {
        logger.info("获取所有流程执行")
        val executions = serviceOrchestrator.getAllExecutions()
        return ListResult.of(executions)
    }

    /**
     * 验证服务流程
     *
     * @param flow 服务流程
     * @return 验证结果
     */
    @PostMapping("/validate")
    fun validateFlow(@RequestBody flow: ServiceFlow): ListResult<String> {
        logger.info("验证服务流程: {}", flow.name)
        val errors = serviceOrchestrator.validateFlow(flow)
        return ListResult.of(errors)
    }
}
