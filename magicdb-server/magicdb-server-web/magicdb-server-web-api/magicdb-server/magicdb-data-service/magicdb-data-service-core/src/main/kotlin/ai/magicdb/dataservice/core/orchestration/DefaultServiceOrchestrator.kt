package ai.magicdb.dataservice.core.orchestration

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.ServiceFlowExecutor
import ai.magicdb.dataservice.api.ServiceFlowRepository
import ai.magicdb.dataservice.api.ServiceOrchestrator
import ai.magicdb.dataservice.api.model.FlowExecution
import ai.magicdb.dataservice.api.model.ServiceFlow
import ai.magicdb.dataservice.api.model.ServiceFlowExecution
import ai.magicdb.dataservice.api.model.ServiceFlowExecutionStatus
import ai.magicdb.dataservice.api.model.ServiceFlowNodeExecution
import ai.magicdb.server.tools.base.exception.BusinessException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

/**
 * 默认服务编排器实现
 *
 * @author magicdb
 */
@Service
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
            // 验证流程
            val errors = validateFlow(flow)
            if (errors.isNotEmpty()) {
                throw BusinessException("flow.validation.error", "流程验证失败: ${errors.joinToString(", ")}")
            }

            // 设置创建时间和更新时间
            if (flow.id.isEmpty()) {
                flow.id = UUID.randomUUID().toString()
                flow.createTime = System.currentTimeMillis()
            }
            flow.updateTime = System.currentTimeMillis()

            // 保存流程
            return flowRepository.saveFlow(flow)
        } catch (e: Exception) {
            logger.error("创建流程失败: {}", flow.name, e)
            throw e
        }
    }

    override fun updateFlow(flow: ServiceFlow): Boolean {
        try {
            // 验证流程
            val errors = validateFlow(flow)
            if (errors.isNotEmpty()) {
                throw BusinessException("flow.validation.error", "流程验证失败: ${errors.joinToString(", ")}")
            }

            // 检查流程是否存在
            val existingFlow = flowRepository.getFlow(flow.id)
                ?: throw BusinessException("flow.not.found", "流程不存在: ${flow.id}")

            // 设置更新时间
            flow.updateTime = System.currentTimeMillis()
            flow.createTime = existingFlow.createTime

            // 更新流程
            return flowRepository.updateFlow(flow)
        } catch (e: Exception) {
            logger.error("更新流程失败: {}", flow.id, e)
            throw e
        }
    }

    override fun deleteFlow(flowId: String): Boolean {
        try {
            // 检查流程是否存在
            val existingFlow = flowRepository.getFlow(flowId)
                ?: throw BusinessException("flow.not.found", "流程不存在: $flowId")

            // 删除流程
            return flowRepository.deleteFlow(flowId)
        } catch (e: Exception) {
            logger.error("删除流程失败: {}", flowId, e)
            throw e
        }
    }

    override fun getFlow(flowId: String): ServiceFlow {
        try {
            // 获取流程
            return flowRepository.getFlow(flowId)
                ?: throw BusinessException("flow.not.found", "流程不存在: $flowId")
        } catch (e: Exception) {
            logger.error("获取流程失败: {}", flowId, e)
            throw e
        }
    }

    override fun getAllFlows(): List<ServiceFlow> {
        try {
            // 获取所有流程
            return flowRepository.getAllFlows()
        } catch (e: Exception) {
            logger.error("获取所有流程失败", e)
            throw e
        }
    }

    override fun executeFlow(flowId: String, parameters: Map<String, Any?>): String {
        try {
            // 获取流程
            val flow = flowRepository.getFlow(flowId)
                ?: throw BusinessException("flow.not.found", "流程不存在: $flowId")

            // 检查流程是否启用
            if (!flow.enabled) {
                throw BusinessException("flow.disabled", "流程已禁用: $flowId")
            }

            // 验证参数
            validateParameters(flow, parameters)

            // 执行流程
            val executionId = flowExecutor.executeAsync(flow, parameters)

            // 更新流程执行次数
            flow.executeCount++
            flow.lastExecuteTime = System.currentTimeMillis()
            flowRepository.updateFlow(flow)

            return executionId
        } catch (e: Exception) {
            logger.error("执行流程失败: {}", flowId, e)
            throw e
        }
    }

    override fun cancelExecution(executionId: String): Boolean {
        try {
            // 取消流程执行
            return flowExecutor.cancel(executionId)
        } catch (e: Exception) {
            logger.error("取消流程执行失败: {}", executionId, e)
            throw e
        }
    }

    override fun getExecutionStatus(executionId: String): ServiceFlowExecution {
        try {
            // 获取流程执行状态
            val execution = flowExecutor.getExecution(executionId)
                ?: throw BusinessException("flow.execution.not.found", "流程执行记录不存在: $executionId")

            // 转换为ServiceFlowExecution
            return convertToServiceFlowExecution(execution)
        } catch (e: Exception) {
            logger.error("获取流程执行状态失败: {}", executionId, e)
            throw e
        }
    }

    override fun getExecutionResult(executionId: String): Any? {
        try {
            // 获取流程执行记录
            val execution = flowExecutor.getExecution(executionId)
                ?: throw BusinessException("flow.execution.not.found", "流程执行记录不存在: $executionId")

            // 返回执行结果
            return execution.result
        } catch (e: Exception) {
            logger.error("获取流程执行结果失败: {}", executionId, e)
            throw e
        }
    }

    override fun getAllExecutions(): List<ServiceFlowExecution> {
        try {
            // 获取所有流程执行记录
            val executions = flowExecutor.getAllExecutions()

            // 转换为ServiceFlowExecution列表
            return executions.map { convertToServiceFlowExecution(it) }
        } catch (e: Exception) {
            logger.error("获取所有流程执行记录失败", e)
            throw e
        }
    }

    override fun validateFlow(flow: ServiceFlow): List<String> {
        val errors = mutableListOf<String>()

        try {
            // 验证流程名称
            if (flow.name.isBlank()) {
                errors.add("流程名称不能为空")
            }

            // 验证流程节点
            if (flow.nodes.isEmpty()) {
                errors.add("流程节点不能为空")
            }

            // 验证流程连接
            if (flow.connections.isEmpty() && flow.nodes.size > 1) {
                errors.add("多节点流程必须有连接")
            }

            // 验证开始节点和结束节点
            val startNodes = flow.nodes.filter { it.type == ai.magicdb.dataservice.api.model.FlowNodeType.START }
            val endNodes = flow.nodes.filter { it.type == ai.magicdb.dataservice.api.model.FlowNodeType.END }

            if (startNodes.isEmpty()) {
                errors.add("流程必须有开始节点")
            }

            if (startNodes.size > 1) {
                errors.add("流程只能有一个开始节点")
            }

            if (endNodes.isEmpty()) {
                errors.add("流程必须有结束节点")
            }

            // 验证服务节点
            flow.nodes.filter { it.type == ai.magicdb.dataservice.api.model.FlowNodeType.SERVICE }.forEach { node ->
                val serviceId = node.config["serviceId"] as? String
                if (serviceId.isNullOrBlank()) {
                    errors.add("服务节点 ${node.name} 必须指定服务ID")
                } else {
                    // 验证服务是否存在
                    val service = serviceRepository.getService(serviceId)
                    if (service == null) {
                        errors.add("服务节点 ${node.name} 指定的服务不存在: $serviceId")
                    } else if (!service.enabled) {
                        errors.add("服务节点 ${node.name} 指定的服务已禁用: $serviceId")
                    }
                }
            }

            // 验证脚本节点
            flow.nodes.filter { it.type == ai.magicdb.dataservice.api.model.FlowNodeType.SCRIPT }.forEach { node ->
                val script = node.config["script"] as? String
                val language = node.config["language"] as? String

                if (script.isNullOrBlank()) {
                    errors.add("脚本节点 ${node.name} 必须指定脚本内容")
                }

                if (language.isNullOrBlank()) {
                    errors.add("脚本节点 ${node.name} 必须指定脚本语言")
                }
            }

            // 验证条件节点
            flow.nodes.filter { it.type == ai.magicdb.dataservice.api.model.FlowNodeType.CONDITION }.forEach { node ->
                val condition = node.config["condition"] as? String
                if (condition.isNullOrBlank()) {
                    errors.add("条件节点 ${node.name} 必须指定条件表达式")
                }
            }

            // 验证子流程节点
            flow.nodes.filter { it.type == ai.magicdb.dataservice.api.model.FlowNodeType.SUB_FLOW }.forEach { node ->
                val subFlowId = node.config["flowId"] as? String
                if (subFlowId.isNullOrBlank()) {
                    errors.add("子流程节点 ${node.name} 必须指定子流程ID")
                } else {
                    // 验证子流程是否存在
                    val subFlow = flowRepository.getFlow(subFlowId)
                    if (subFlow == null) {
                        errors.add("子流程节点 ${node.name} 指定的子流程不存在: $subFlowId")
                    } else if (!subFlow.enabled) {
                        errors.add("子流程节点 ${node.name} 指定的子流程已禁用: $subFlowId")
                    }

                    // 检查循环引用
                    if (subFlowId == flow.id) {
                        errors.add("子流程节点 ${node.name} 不能引用自身")
                    }
                }
            }

            // 验证连接
            flow.connections.forEach { connection ->
                // 验证源节点和目标节点是否存在
                val sourceNode = flow.nodes.find { it.id == connection.sourceId }
                val targetNode = flow.nodes.find { it.id == connection.targetId }

                if (sourceNode == null) {
                    errors.add("连接 ${connection.id} 的源节点不存在: ${connection.sourceId}")
                }

                if (targetNode == null) {
                    errors.add("连接 ${connection.id} 的目标节点不存在: ${connection.targetId}")
                }

                // 验证开始节点不能作为目标节点
                if (targetNode?.type == ai.magicdb.dataservice.api.model.FlowNodeType.START) {
                    errors.add("连接 ${connection.id} 的目标节点不能是开始节点")
                }

                // 验证结束节点不能作为源节点
                if (sourceNode?.type == ai.magicdb.dataservice.api.model.FlowNodeType.END) {
                    errors.add("连接 ${connection.id} 的源节点不能是结束节点")
                }
            }

            // 验证流程参数
            flow.parameters.forEach { param ->
                if (param.name.isBlank()) {
                    errors.add("流程参数名称不能为空")
                }
            }

            // 验证流程参数名称唯一性
            val paramNames = flow.parameters.map { it.name }
            if (paramNames.size != paramNames.distinct().size) {
                errors.add("流程参数名称必须唯一")
            }
        } catch (e: Exception) {
            logger.error("验证流程失败: {}", flow.name, e)
            errors.add("验证流程失败: ${e.message}")
        }

        return errors
    }

    /**
     * 验证参数
     */
    private fun validateParameters(flow: ServiceFlow, parameters: Map<String, Any?>) {
        // 验证必填参数
        flow.parameters.filter { it.required }.forEach { param ->
            if (!parameters.containsKey(param.name)) {
                throw BusinessException("flow.parameter.required", "缺少必填参数: ${param.name}")
            }
        }
    }

    /**
     * 转换为ServiceFlowExecution
     */
    private fun convertToServiceFlowExecution(execution: FlowExecution): ServiceFlowExecution {
        val nodeExecutions = execution.nodeExecutions.map { nodeExecution ->
            ServiceFlowNodeExecution(
                nodeId = nodeExecution.nodeId,
                nodeName = nodeExecution.nodeName,
                status = when (nodeExecution.status) {
                    ai.magicdb.dataservice.api.model.FlowExecutionStatus.PENDING -> ServiceFlowExecutionStatus.PENDING
                    ai.magicdb.dataservice.api.model.FlowExecutionStatus.RUNNING -> ServiceFlowExecutionStatus.RUNNING
                    ai.magicdb.dataservice.api.model.FlowExecutionStatus.SUCCESS -> ServiceFlowExecutionStatus.COMPLETED
                    ai.magicdb.dataservice.api.model.FlowExecutionStatus.FAILED -> ServiceFlowExecutionStatus.FAILED
                    ai.magicdb.dataservice.api.model.FlowExecutionStatus.CANCELED -> ServiceFlowExecutionStatus.CANCELLED
                    ai.magicdb.dataservice.api.model.FlowExecutionStatus.TIMEOUT -> ServiceFlowExecutionStatus.TIMEOUT
                    ai.magicdb.dataservice.api.model.FlowExecutionStatus.PAUSED -> ServiceFlowExecutionStatus.PAUSED
                },
                startTime = nodeExecution.startTime,
                endTime = nodeExecution.endTime,
                result = nodeExecution.result,
                error = nodeExecution.errorMessage,
                logs = emptyList(),
                duration = nodeExecution.duration,
                retryCount = 0,
                maxRetryCount = 0
            )
        }

        return ServiceFlowExecution(
            id = execution.id,
            flowId = execution.flowId,
            flowName = execution.flowName,
            status = when (execution.status) {
                ai.magicdb.dataservice.api.model.FlowExecutionStatus.PENDING -> ServiceFlowExecutionStatus.PENDING
                ai.magicdb.dataservice.api.model.FlowExecutionStatus.RUNNING -> ServiceFlowExecutionStatus.RUNNING
                ai.magicdb.dataservice.api.model.FlowExecutionStatus.SUCCESS -> ServiceFlowExecutionStatus.COMPLETED
                ai.magicdb.dataservice.api.model.FlowExecutionStatus.FAILED -> ServiceFlowExecutionStatus.FAILED
                ai.magicdb.dataservice.api.model.FlowExecutionStatus.CANCELED -> ServiceFlowExecutionStatus.CANCELLED
                ai.magicdb.dataservice.api.model.FlowExecutionStatus.TIMEOUT -> ServiceFlowExecutionStatus.TIMEOUT
                ai.magicdb.dataservice.api.model.FlowExecutionStatus.PAUSED -> ServiceFlowExecutionStatus.PAUSED
            },
            startTime = execution.startTime,
            endTime = execution.endTime,
            parameters = execution.parameters,
            result = execution.result,
            error = execution.errorMessage,
            nodeExecutions = nodeExecutions,
            currentNodeId = null,
            logs = emptyList(),
            userId = 0,
            duration = execution.duration
        )
    }
}
