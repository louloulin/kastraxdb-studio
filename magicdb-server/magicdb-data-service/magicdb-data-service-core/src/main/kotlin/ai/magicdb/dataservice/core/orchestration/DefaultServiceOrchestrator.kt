package ai.magicdb.dataservice.core.orchestration

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.ServiceFlowExecutor
import ai.magicdb.dataservice.api.ServiceFlowRepository
import ai.magicdb.dataservice.api.ServiceOrchestrator
import ai.magicdb.dataservice.api.model.FlowExecution
import ai.magicdb.dataservice.api.model.FlowExecutionStatus
import ai.magicdb.dataservice.api.model.ServiceFlow
import ai.magicdb.dataservice.api.model.ServiceFlowExecution
import ai.magicdb.dataservice.api.model.ServiceFlowExecutionStatus
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 默认服务编排器实现
 *
 * @author magicdb
 */
@Component
class DefaultServiceOrchestrator(
    private val flowRepository: ServiceFlowRepository,
    private val flowExecutor: ServiceFlowExecutor,
    private val serviceRepository: DataServiceRepository,
    private val serviceExecutor: DataServiceExecutor,
    private val objectMapper: ObjectMapper
) : ServiceOrchestrator {
    private val logger = LoggerFactory.getLogger(DefaultServiceOrchestrator::class.java)

    override fun createFlow(flow: ServiceFlow): String {
        try {
            // 创建流程
            return flowRepository.saveFlow(flow)
        } catch (e: Exception) {
            logger.error("创建流程失败: {}", e.message, e)
            throw e
        }
    }

    override fun updateFlow(flow: ServiceFlow): Boolean {
        try {
            // 更新流程
            val flowId = flowRepository.saveFlow(flow)
            return flowId.isNotEmpty()
        } catch (e: Exception) {
            logger.error("更新流程失败: {}", e.message, e)
            return false
        }
    }

    override fun getFlow(flowId: String): ServiceFlow {
        try {
            return flowRepository.getFlow(flowId) ?: throw IllegalArgumentException("流程不存在: $flowId")
        } catch (e: Exception) {
            logger.error("获取流程失败: {}", flowId, e)
            throw e
        }
    }



    override fun deleteFlow(flowId: String): Boolean {
        try {
            return flowRepository.deleteFlow(flowId)
        } catch (e: Exception) {
            logger.error("删除流程失败: {}", flowId, e)
            return false
        }
    }











    override fun executeFlow(flowId: String, parameters: Map<String, Any?>): String {
        try {
            // 获取流程
            val flow = getFlow(flowId)

            // 异步执行流程
            return flowExecutor.executeAsync(flow, parameters)
        } catch (e: Exception) {
            logger.error("执行流程失败: {}", e.message, e)
            throw e
        }
    }





    override fun cancelExecution(executionId: String): Boolean {
        try {
            // 取消执行
            return flowExecutor.cancel(executionId)
        } catch (e: Exception) {
            logger.error("取消流程执行失败: {}", e.message, e)
            return false
        }
    }

    override fun getExecutionStatus(executionId: String): ServiceFlowExecution {
        try {
            // 获取执行记录
            val execution = flowRepository.getFlowExecution(executionId)
                ?: throw IllegalArgumentException("执行记录不存在: $executionId")

            // 转换为ServiceFlowExecution
            return convertToServiceFlowExecution(execution)
        } catch (e: Exception) {
            logger.error("获取流程执行状态失败: {}", e.message, e)
            throw e
        }
    }

    override fun getExecutionResult(executionId: String): Any? {
        try {
            // 获取执行记录
            val execution = flowRepository.getFlowExecution(executionId)
                ?: throw IllegalArgumentException("执行记录不存在: $executionId")

            return execution.result
        } catch (e: Exception) {
            logger.error("获取流程执行结果失败: {}", e.message, e)
            return null
        }
    }

    override fun getAllFlows(): List<ServiceFlow> {
        try {
            return flowRepository.getFlows(null, null, null, 1000, 0)
        } catch (e: Exception) {
            logger.error("获取所有流程失败", e)
            return emptyList()
        }
    }

    override fun getAllExecutions(): List<ServiceFlowExecution> {
        try {
            val executions = flowRepository.getFlowExecutions(null, null, null, null, 1000, 0)
            return executions.map { convertToServiceFlowExecution(it) }
        } catch (e: Exception) {
            logger.error("获取所有流程执行失败", e)
            return emptyList()
        }
    }

    override fun validateFlow(flow: ServiceFlow): List<String> {
        try {
            val errors = mutableListOf<String>()

            // 验证流程名称
            if (flow.name.isBlank()) {
                errors.add("流程名称不能为空")
            }

            // 验证流程节点
            if (flow.nodes.isEmpty()) {
                errors.add("流程节点不能为空")
            }

            return errors
        } catch (e: Exception) {
            logger.error("验证流程失败: {}", e.message, e)
            return listOf("验证流程失败: ${e.message}")
        }
    }

    /**
     * 转换为服务流程执行模型
     */
    private fun convertToServiceFlowExecution(execution: FlowExecution): ServiceFlowExecution {
        return ServiceFlowExecution(
            id = execution.id,
            flowId = execution.flowId,
            flowName = execution.flowName,
            status = convertStatus(execution.status),
            startTime = execution.startTime,
            endTime = execution.endTime,
            parameters = execution.parameters,
            result = execution.result,
            error = execution.errorMessage,
            duration = execution.duration
        )
    }

    /**
     * 转换状态
     */
    private fun convertStatus(status: FlowExecutionStatus): ServiceFlowExecutionStatus {
        return when (status) {
            FlowExecutionStatus.PENDING -> ServiceFlowExecutionStatus.PENDING
            FlowExecutionStatus.RUNNING -> ServiceFlowExecutionStatus.RUNNING
            FlowExecutionStatus.SUCCESS -> ServiceFlowExecutionStatus.COMPLETED
            FlowExecutionStatus.FAILED -> ServiceFlowExecutionStatus.FAILED
            FlowExecutionStatus.CANCELED -> ServiceFlowExecutionStatus.CANCELLED
            FlowExecutionStatus.TIMEOUT -> ServiceFlowExecutionStatus.TIMEOUT
            FlowExecutionStatus.PAUSED -> ServiceFlowExecutionStatus.PAUSED
            else -> ServiceFlowExecutionStatus.PENDING
        }
    }
}
