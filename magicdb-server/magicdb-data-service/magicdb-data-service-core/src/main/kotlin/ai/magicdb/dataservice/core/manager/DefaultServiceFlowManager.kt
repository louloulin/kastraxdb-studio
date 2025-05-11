package ai.magicdb.dataservice.core.manager

import ai.magicdb.dataservice.api.ServiceFlowExecutor
import ai.magicdb.dataservice.api.ServiceFlowManager
import ai.magicdb.dataservice.api.model.FlowExecution
import ai.magicdb.dataservice.api.model.ServiceFlow
import ai.magicdb.dataservice.core.repository.ServiceFlowRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 默认服务流程管理器实现
 *
 * @author magicdb
 */
@Service
class DefaultServiceFlowManager(
    private val flowRepository: ServiceFlowRepository,
    private val flowExecutor: ServiceFlowExecutor,
    private val objectMapper: ObjectMapper
) : ServiceFlowManager {
    
    private val logger = LoggerFactory.getLogger(DefaultServiceFlowManager::class.java)
    
    override fun createFlow(flow: ServiceFlow): String {
        try {
            // 验证流程
            val validationResult = validateFlow(flow)
            if (validationResult != null) {
                throw IllegalArgumentException(validationResult)
            }
            
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
            val validationResult = validateFlow(flow)
            if (validationResult != null) {
                throw IllegalArgumentException(validationResult)
            }
            
            // 更新流程
            return flowRepository.updateFlow(flow)
        } catch (e: Exception) {
            logger.error("更新流程失败: {}", flow.id, e)
            throw e
        }
    }
    
    override fun deleteFlow(flowId: String): Boolean {
        try {
            // 删除流程
            return flowRepository.deleteFlow(flowId)
        } catch (e: Exception) {
            logger.error("删除流程失败: {}", flowId, e)
            throw e
        }
    }
    
    override fun getFlow(flowId: String): ServiceFlow? {
        try {
            // 获取流程
            return flowRepository.getFlow(flowId)
        } catch (e: Exception) {
            logger.error("获取流程失败: {}", flowId, e)
            throw e
        }
    }
    
    override fun getAllFlows(group: String?): List<ServiceFlow> {
        try {
            // 获取所有流程
            return flowRepository.getAllFlows(group)
        } catch (e: Exception) {
            logger.error("获取所有流程失败", e)
            throw e
        }
    }
    
    override fun executeFlow(flowId: String, parameters: Map<String, Any?>): String {
        try {
            // 获取流程
            val flow = flowRepository.getFlow(flowId) ?: throw IllegalArgumentException("流程不存在: $flowId")
            
            // 异步执行流程
            return flowExecutor.executeAsync(flow, parameters)
        } catch (e: Exception) {
            logger.error("执行流程失败: {}", flowId, e)
            throw e
        }
    }
    
    override fun getFlowExecution(executionId: String): FlowExecution? {
        try {
            // 获取流程执行记录
            return flowExecutor.getExecution(executionId)
        } catch (e: Exception) {
            logger.error("获取流程执行记录失败: {}", executionId, e)
            throw e
        }
    }
    
    override fun getFlowExecutions(flowId: String, limit: Int, offset: Int): List<FlowExecution> {
        try {
            // 获取流程执行记录列表
            return flowRepository.getFlowExecutions(flowId, limit, offset)
        } catch (e: Exception) {
            logger.error("获取流程执行记录列表失败: {}", flowId, e)
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
    
    override fun pauseExecution(executionId: String): Boolean {
        try {
            // 暂停流程执行
            return flowExecutor.pause(executionId)
        } catch (e: Exception) {
            logger.error("暂停流程执行失败: {}", executionId, e)
            throw e
        }
    }
    
    override fun resumeExecution(executionId: String): Boolean {
        try {
            // 恢复流程执行
            return flowExecutor.resume(executionId)
        } catch (e: Exception) {
            logger.error("恢复流程执行失败: {}", executionId, e)
            throw e
        }
    }
    
    override fun validateFlow(flow: ServiceFlow): String? {
        try {
            // 验证流程名称
            if (flow.name.isBlank()) {
                return "流程名称不能为空"
            }
            
            // 验证流程节点
            if (flow.nodes.isEmpty()) {
                return "流程节点不能为空"
            }
            
            // 验证开始节点和结束节点
            val startNodes = flow.nodes.filter { it.type == ai.magicdb.dataservice.api.model.FlowNodeType.START }
            if (startNodes.isEmpty()) {
                return "流程必须包含开始节点"
            }
            if (startNodes.size > 1) {
                return "流程只能包含一个开始节点"
            }
            
            val endNodes = flow.nodes.filter { it.type == ai.magicdb.dataservice.api.model.FlowNodeType.END }
            if (endNodes.isEmpty()) {
                return "流程必须包含结束节点"
            }
            
            // 验证节点ID唯一性
            val nodeIds = flow.nodes.map { it.id }
            if (nodeIds.size != nodeIds.distinct().size) {
                return "流程节点ID必须唯一"
            }
            
            // 验证连接
            if (flow.connections.isEmpty()) {
                return "流程连接不能为空"
            }
            
            // 验证连接ID唯一性
            val connectionIds = flow.connections.map { it.id }
            if (connectionIds.size != connectionIds.distinct().size) {
                return "流程连接ID必须唯一"
            }
            
            // 验证连接的源节点和目标节点存在
            for (connection in flow.connections) {
                if (flow.nodes.none { it.id == connection.sourceId }) {
                    return "连接的源节点不存在: ${connection.sourceId}"
                }
                if (flow.nodes.none { it.id == connection.targetId }) {
                    return "连接的目标节点不存在: ${connection.targetId}"
                }
            }
            
            // 验证开始节点有出连接
            val startNodeId = startNodes.first().id
            if (flow.connections.none { it.sourceId == startNodeId }) {
                return "开始节点必须有出连接"
            }
            
            // 验证结束节点有入连接
            for (endNode in endNodes) {
                if (flow.connections.none { it.targetId == endNode.id }) {
                    return "结束节点必须有入连接: ${endNode.id}"
                }
            }
            
            // 验证所有节点都可达
            val reachableNodes = mutableSetOf<String>()
            val queue = ArrayDeque<String>()
            queue.add(startNodeId)
            reachableNodes.add(startNodeId)
            
            while (queue.isNotEmpty()) {
                val nodeId = queue.removeFirst()
                val outConnections = flow.connections.filter { it.sourceId == nodeId }
                
                for (connection in outConnections) {
                    val targetId = connection.targetId
                    if (targetId !in reachableNodes) {
                        reachableNodes.add(targetId)
                        queue.add(targetId)
                    }
                }
            }
            
            val unreachableNodes = flow.nodes.filter { it.id !in reachableNodes }
            if (unreachableNodes.isNotEmpty()) {
                return "存在不可达的节点: ${unreachableNodes.joinToString(", ") { it.name }}"
            }
            
            // 验证参数
            for (parameter in flow.parameters) {
                if (parameter.name.isBlank()) {
                    return "参数名称不能为空"
                }
            }
            
            // 验证参数名称唯一性
            val parameterNames = flow.parameters.map { it.name }
            if (parameterNames.size != parameterNames.distinct().size) {
                return "参数名称必须唯一"
            }
            
            return null
        } catch (e: Exception) {
            logger.error("验证流程失败: {}", flow.id, e)
            return "验证流程失败: ${e.message}"
        }
    }
    
    override fun exportFlow(flowId: String): String {
        try {
            // 获取流程
            val flow = flowRepository.getFlow(flowId) ?: throw IllegalArgumentException("流程不存在: $flowId")
            
            // 转换为JSON
            return objectMapper.writeValueAsString(flow)
        } catch (e: Exception) {
            logger.error("导出流程失败: {}", flowId, e)
            throw e
        }
    }
    
    override fun importFlow(flowJson: String): String {
        try {
            // 解析JSON
            val flow = objectMapper.readValue(flowJson, ServiceFlow::class.java)
            
            // 验证流程
            val validationResult = validateFlow(flow)
            if (validationResult != null) {
                throw IllegalArgumentException(validationResult)
            }
            
            // 清空ID，生成新的ID
            flow.id = ""
            
            // 保存流程
            return flowRepository.saveFlow(flow)
        } catch (e: Exception) {
            logger.error("导入流程失败", e)
            throw e
        }
    }
    
    override fun copyFlow(flowId: String, newName: String): String {
        try {
            // 获取流程
            val flow = flowRepository.getFlow(flowId) ?: throw IllegalArgumentException("流程不存在: $flowId")
            
            // 创建副本
            val copy = flow.copy(
                id = "",
                name = newName,
                createTime = 0,
                updateTime = 0,
                executeCount = 0,
                successCount = 0,
                failCount = 0,
                lastExecuteTime = 0,
                lastExecuteResult = false,
                lastExecuteMessage = "",
                lastExecuteDuration = 0
            )
            
            // 保存流程
            return flowRepository.saveFlow(copy)
        } catch (e: Exception) {
            logger.error("复制流程失败: {}", flowId, e)
            throw e
        }
    }
}
