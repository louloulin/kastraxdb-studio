package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.model.FlowConnection
import ai.magicdb.dataservice.api.model.FlowExecution
import ai.magicdb.dataservice.api.model.FlowExecutionStatus
import ai.magicdb.dataservice.api.model.FlowNode
import ai.magicdb.dataservice.api.model.FlowNodeType
import ai.magicdb.dataservice.api.model.FlowParameter
import ai.magicdb.dataservice.api.model.NodeExecution
import ai.magicdb.dataservice.api.model.ServiceFlow
import ai.magicdb.dataservice.api.model.TriggerType
import ai.magicdb.dataservice.core.entity.FlowExecutionDO
import ai.magicdb.dataservice.core.entity.NodeExecutionDO
import ai.magicdb.dataservice.core.entity.ServiceFlowDO
import ai.magicdb.dataservice.core.mapper.FlowExecutionMapper
import ai.magicdb.dataservice.core.mapper.NodeExecutionMapper
import ai.magicdb.dataservice.core.mapper.ServiceFlowMapper
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.util.*

/**
 * MyBatis服务流程存储库实现
 *
 * @author magicdb
 */
@Repository
class MybatisServiceFlowRepository(
    private val flowMapper: ServiceFlowMapper,
    private val executionMapper: FlowExecutionMapper,
    private val nodeExecutionMapper: NodeExecutionMapper,
    private val objectMapper: ObjectMapper
) : ServiceFlowRepository {
    
    private val logger = LoggerFactory.getLogger(MybatisServiceFlowRepository::class.java)
    
    override fun saveFlow(flow: ServiceFlow): String {
        try {
            // 如果没有ID，生成一个新的ID
            if (flow.id.isEmpty()) {
                flow.id = UUID.randomUUID().toString()
                flow.createTime = System.currentTimeMillis()
            }
            
            // 更新时间
            flow.updateTime = System.currentTimeMillis()
            
            // 转换为DO对象
            val flowDO = convertToFlowDO(flow)
            
            // 保存或更新
            val existingFlow = flowMapper.selectById(flow.id)
            if (existingFlow == null) {
                flowMapper.insert(flowDO)
            } else {
                flowMapper.updateById(flowDO)
            }
            
            return flow.id
        } catch (e: Exception) {
            logger.error("保存流程失败: {}", flow.id, e)
            throw e
        }
    }
    
    override fun updateFlow(flow: ServiceFlow): Boolean {
        try {
            // 更新时间
            flow.updateTime = System.currentTimeMillis()
            
            // 转换为DO对象
            val flowDO = convertToFlowDO(flow)
            
            // 更新
            val result = flowMapper.updateById(flowDO)
            return result > 0
        } catch (e: Exception) {
            logger.error("更新流程失败: {}", flow.id, e)
            return false
        }
    }
    
    override fun deleteFlow(flowId: String): Boolean {
        try {
            // 删除流程
            val result = flowMapper.deleteById(flowId)
            return result > 0
        } catch (e: Exception) {
            logger.error("删除流程失败: {}", flowId, e)
            return false
        }
    }
    
    override fun getFlow(flowId: String): ServiceFlow? {
        try {
            // 查询流程
            val flowDO = flowMapper.selectById(flowId) ?: return null
            
            // 转换为模型对象
            return convertToFlow(flowDO)
        } catch (e: Exception) {
            logger.error("获取流程失败: {}", flowId, e)
            return null
        }
    }
    
    override fun getAllFlows(group: String?): List<ServiceFlow> {
        try {
            // 查询流程
            val flowDOs = if (group != null) {
                flowMapper.selectByGroup(group)
            } else {
                flowMapper.selectList(null)
            }
            
            // 转换为模型对象
            return flowDOs.map { convertToFlow(it) }
        } catch (e: Exception) {
            logger.error("获取所有流程失败", e)
            return emptyList()
        }
    }
    
    override fun updateFlowExecutionInfo(
        flowId: String,
        lastExecuteTime: Long,
        lastExecuteResult: Boolean,
        lastExecuteMessage: String?,
        lastExecuteDuration: Long
    ): Boolean {
        try {
            // 更新流程执行信息
            val result = flowMapper.updateExecutionInfo(
                flowId,
                lastExecuteTime,
                lastExecuteResult,
                lastExecuteMessage,
                lastExecuteDuration,
                System.currentTimeMillis()
            )
            return result > 0
        } catch (e: Exception) {
            logger.error("更新流程执行信息失败: {}", flowId, e)
            return false
        }
    }
    
    override fun updateFlowEnabled(flowId: String, enabled: Boolean): Boolean {
        try {
            // 更新流程启用状态
            val result = flowMapper.updateEnabled(flowId, enabled, System.currentTimeMillis())
            return result > 0
        } catch (e: Exception) {
            logger.error("更新流程启用状态失败: {}", flowId, e)
            return false
        }
    }
    
    override fun saveFlowExecution(execution: FlowExecution): String {
        try {
            // 如果没有ID，生成一个新的ID
            if (execution.id.isEmpty()) {
                execution.id = UUID.randomUUID().toString()
            }
            
            // 转换为DO对象
            val executionDO = convertToExecutionDO(execution)
            
            // 保存或更新
            val existingExecution = executionMapper.selectById(execution.id)
            if (existingExecution == null) {
                executionMapper.insert(executionDO)
            } else {
                executionMapper.updateById(executionDO)
            }
            
            // 保存节点执行记录
            for (nodeExecution in execution.nodeExecutions) {
                saveNodeExecution(nodeExecution, execution.id)
            }
            
            return execution.id
        } catch (e: Exception) {
            logger.error("保存流程执行记录失败: {}", execution.id, e)
            throw e
        }
    }
    
    override fun updateFlowExecutionStatus(executionId: String, status: FlowExecutionStatus): Boolean {
        try {
            // 更新执行记录状态
            val result = executionMapper.updateStatus(executionId, status.name)
            return result > 0
        } catch (e: Exception) {
            logger.error("更新流程执行记录状态失败: {}", executionId, e)
            return false
        }
    }
    
    override fun updateFlowExecutionResult(
        executionId: String,
        status: FlowExecutionStatus,
        result: Any?,
        errorMessage: String?,
        endTime: Long,
        duration: Long
    ): Boolean {
        try {
            // 转换结果为JSON
            val resultJson = if (result != null) {
                objectMapper.writeValueAsString(result)
            } else {
                null
            }
            
            // 更新执行记录结果
            val updateResult = executionMapper.updateResult(
                executionId,
                status.name,
                resultJson,
                errorMessage,
                endTime,
                duration
            )
            return updateResult > 0
        } catch (e: Exception) {
            logger.error("更新流程执行记录结果失败: {}", executionId, e)
            return false
        }
    }
    
    override fun getFlowExecution(executionId: String): FlowExecution? {
        try {
            // 查询执行记录
            val executionDO = executionMapper.selectById(executionId) ?: return null
            
            // 查询节点执行记录
            val nodeExecutionDOs = nodeExecutionMapper.selectByFlowExecutionId(executionId)
            
            // 转换为模型对象
            val execution = convertToExecution(executionDO)
            execution.nodeExecutions = nodeExecutionDOs.map { convertToNodeExecution(it) }
            
            return execution
        } catch (e: Exception) {
            logger.error("获取流程执行记录失败: {}", executionId, e)
            return null
        }
    }
    
    override fun getFlowExecutions(flowId: String, limit: Int, offset: Int): List<FlowExecution> {
        try {
            // 查询执行记录
            val executionDOs = executionMapper.selectByFlowId(flowId, limit, offset)
            
            // 转换为模型对象
            return executionDOs.map { executionDO ->
                val execution = convertToExecution(executionDO)
                
                // 查询节点执行记录
                val nodeExecutionDOs = nodeExecutionMapper.selectByFlowExecutionId(execution.id)
                execution.nodeExecutions = nodeExecutionDOs.map { convertToNodeExecution(it) }
                
                execution
            }
        } catch (e: Exception) {
            logger.error("获取流程执行记录列表失败: {}", flowId, e)
            return emptyList()
        }
    }
    
    override fun getLastFlowExecution(flowId: String): FlowExecution? {
        try {
            // 查询最后一次执行记录
            val executionDO = executionMapper.selectLastExecution(flowId) ?: return null
            
            // 查询节点执行记录
            val nodeExecutionDOs = nodeExecutionMapper.selectByFlowExecutionId(executionDO.id)
            
            // 转换为模型对象
            val execution = convertToExecution(executionDO)
            execution.nodeExecutions = nodeExecutionDOs.map { convertToNodeExecution(it) }
            
            return execution
        } catch (e: Exception) {
            logger.error("获取流程最后一次执行记录失败: {}", flowId, e)
            return null
        }
    }
    
    override fun saveNodeExecution(nodeExecution: NodeExecution, flowExecutionId: String): String {
        try {
            // 如果没有ID，生成一个新的ID
            if (nodeExecution.id.isEmpty()) {
                nodeExecution.id = UUID.randomUUID().toString()
            }
            
            // 转换为DO对象
            val nodeExecutionDO = convertToNodeExecutionDO(nodeExecution, flowExecutionId)
            
            // 保存或更新
            val existingNodeExecution = nodeExecutionMapper.selectById(nodeExecution.id)
            if (existingNodeExecution == null) {
                nodeExecutionMapper.insert(nodeExecutionDO)
            } else {
                nodeExecutionMapper.updateById(nodeExecutionDO)
            }
            
            return nodeExecution.id
        } catch (e: Exception) {
            logger.error("保存节点执行记录失败: {}", nodeExecution.id, e)
            throw e
        }
    }
    
    override fun updateNodeExecutionStatus(id: String, status: FlowExecutionStatus): Boolean {
        try {
            // 更新节点执行记录状态
            val result = nodeExecutionMapper.updateStatus(id, status.name)
            return result > 0
        } catch (e: Exception) {
            logger.error("更新节点执行记录状态失败: {}", id, e)
            return false
        }
    }
    
    override fun updateNodeExecutionResult(
        id: String,
        status: FlowExecutionStatus,
        result: Any?,
        errorMessage: String?,
        endTime: Long,
        duration: Long,
        output: Map<String, Any?>
    ): Boolean {
        try {
            // 转换结果为JSON
            val resultJson = if (result != null) {
                objectMapper.writeValueAsString(result)
            } else {
                null
            }
            
            // 转换输出为JSON
            val outputJson = if (output.isNotEmpty()) {
                objectMapper.writeValueAsString(output)
            } else {
                null
            }
            
            // 更新节点执行记录结果
            val updateResult = nodeExecutionMapper.updateResult(
                id,
                status.name,
                resultJson,
                errorMessage,
                endTime,
                duration,
                outputJson
            )
            return updateResult > 0
        } catch (e: Exception) {
            logger.error("更新节点执行记录结果失败: {}", id, e)
            return false
        }
    }
    
    override fun getNodeExecutions(flowExecutionId: String): List<NodeExecution> {
        try {
            // 查询节点执行记录
            val nodeExecutionDOs = nodeExecutionMapper.selectByFlowExecutionId(flowExecutionId)
            
            // 转换为模型对象
            return nodeExecutionDOs.map { convertToNodeExecution(it) }
        } catch (e: Exception) {
            logger.error("获取节点执行记录列表失败: {}", flowExecutionId, e)
            return emptyList()
        }
    }
    
    /**
     * 转换为流程DO对象
     */
    private fun convertToFlowDO(flow: ServiceFlow): ServiceFlowDO {
        val flowDO = ServiceFlowDO()
        flowDO.id = flow.id
        flowDO.name = flow.name
        flowDO.description = flow.description
        flowDO.nodes = if (flow.nodes.isEmpty()) null else objectMapper.writeValueAsString(flow.nodes)
        flowDO.connections = if (flow.connections.isEmpty()) null else objectMapper.writeValueAsString(flow.connections)
        flowDO.variables = if (flow.variables.isEmpty()) null else objectMapper.writeValueAsString(flow.variables)
        flowDO.parameters = if (flow.parameters.isEmpty()) null else objectMapper.writeValueAsString(flow.parameters)
        flowDO.createTime = flow.createTime
        flowDO.updateTime = flow.updateTime
        flowDO.createUserId = flow.createUserId
        flowDO.enabled = flow.enabled
        flowDO.tags = if (flow.tags.isEmpty()) null else objectMapper.writeValueAsString(flow.tags)
        flowDO.group = flow.group
        flowDO.timeout = flow.timeout
        flowDO.executeCount = flow.executeCount
        flowDO.successCount = flow.successCount
        flowDO.failCount = flow.failCount
        flowDO.lastExecuteTime = flow.lastExecuteTime
        flowDO.lastExecuteResult = flow.lastExecuteResult
        flowDO.lastExecuteMessage = flow.lastExecuteMessage
        flowDO.lastExecuteDuration = flow.lastExecuteDuration
        return flowDO
    }
    
    /**
     * 转换为流程模型对象
     */
    private fun convertToFlow(flowDO: ServiceFlowDO): ServiceFlow {
        val flow = ServiceFlow()
        flow.id = flowDO.id
        flow.name = flowDO.name
        flow.description = flowDO.description ?: ""
        flow.nodes = if (flowDO.nodes.isNullOrEmpty()) {
            emptyList()
        } else {
            objectMapper.readValue(flowDO.nodes, object : TypeReference<List<FlowNode>>() {})
        }
        flow.connections = if (flowDO.connections.isNullOrEmpty()) {
            emptyList()
        } else {
            objectMapper.readValue(flowDO.connections, object : TypeReference<List<FlowConnection>>() {})
        }
        flow.variables = if (flowDO.variables.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(flowDO.variables, object : TypeReference<Map<String, Any?>>() {})
        }
        flow.parameters = if (flowDO.parameters.isNullOrEmpty()) {
            emptyList()
        } else {
            objectMapper.readValue(flowDO.parameters, object : TypeReference<List<FlowParameter>>() {})
        }
        flow.createTime = flowDO.createTime ?: 0
        flow.updateTime = flowDO.updateTime ?: 0
        flow.createUserId = flowDO.createUserId ?: 0
        flow.enabled = flowDO.enabled ?: true
        flow.tags = if (flowDO.tags.isNullOrEmpty()) {
            emptyList()
        } else {
            objectMapper.readValue(flowDO.tags, object : TypeReference<List<String>>() {})
        }
        flow.group = flowDO.group ?: "default"
        flow.timeout = flowDO.timeout ?: 60000
        flow.executeCount = flowDO.executeCount ?: 0
        flow.successCount = flowDO.successCount ?: 0
        flow.failCount = flowDO.failCount ?: 0
        flow.lastExecuteTime = flowDO.lastExecuteTime ?: 0
        flow.lastExecuteResult = flowDO.lastExecuteResult ?: false
        flow.lastExecuteMessage = flowDO.lastExecuteMessage ?: ""
        flow.lastExecuteDuration = flowDO.lastExecuteDuration ?: 0
        return flow
    }
    
    /**
     * 转换为执行记录DO对象
     */
    private fun convertToExecutionDO(execution: FlowExecution): FlowExecutionDO {
        val executionDO = FlowExecutionDO()
        executionDO.id = execution.id
        executionDO.flowId = execution.flowId
        executionDO.flowName = execution.flowName
        executionDO.parameters = if (execution.parameters.isEmpty()) null else objectMapper.writeValueAsString(execution.parameters)
        executionDO.variables = if (execution.variables.isEmpty()) null else objectMapper.writeValueAsString(execution.variables)
        executionDO.startTime = execution.startTime
        executionDO.endTime = execution.endTime
        executionDO.duration = execution.duration
        executionDO.status = execution.status.name
        executionDO.result = if (execution.result == null) null else objectMapper.writeValueAsString(execution.result)
        executionDO.errorMessage = execution.errorMessage
        executionDO.triggerType = execution.triggerType.name
        executionDO.triggerId = execution.triggerId
        return executionDO
    }
    
    /**
     * 转换为执行记录模型对象
     */
    private fun convertToExecution(executionDO: FlowExecutionDO): FlowExecution {
        val execution = FlowExecution()
        execution.id = executionDO.id
        execution.flowId = executionDO.flowId
        execution.flowName = executionDO.flowName ?: ""
        execution.parameters = if (executionDO.parameters.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(executionDO.parameters, object : TypeReference<Map<String, Any?>>() {})
        }
        execution.variables = if (executionDO.variables.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(executionDO.variables, object : TypeReference<Map<String, Any?>>() {})
        }
        execution.startTime = executionDO.startTime ?: 0
        execution.endTime = executionDO.endTime ?: 0
        execution.duration = executionDO.duration ?: 0
        execution.status = if (executionDO.status.isNullOrEmpty()) {
            FlowExecutionStatus.PENDING
        } else {
            FlowExecutionStatus.valueOf(executionDO.status!!)
        }
        execution.result = if (executionDO.result.isNullOrEmpty()) {
            null
        } else {
            objectMapper.readValue(executionDO.result, Any::class.java)
        }
        execution.errorMessage = executionDO.errorMessage ?: ""
        execution.triggerType = if (executionDO.triggerType.isNullOrEmpty()) {
            TriggerType.MANUAL
        } else {
            TriggerType.valueOf(executionDO.triggerType!!)
        }
        execution.triggerId = executionDO.triggerId ?: ""
        return execution
    }
    
    /**
     * 转换为节点执行记录DO对象
     */
    private fun convertToNodeExecutionDO(nodeExecution: NodeExecution, flowExecutionId: String): NodeExecutionDO {
        val nodeExecutionDO = NodeExecutionDO()
        nodeExecutionDO.id = nodeExecution.id
        nodeExecutionDO.flowExecutionId = flowExecutionId
        nodeExecutionDO.nodeId = nodeExecution.nodeId
        nodeExecutionDO.nodeName = nodeExecution.nodeName
        nodeExecutionDO.nodeType = nodeExecution.nodeType.name
        nodeExecutionDO.startTime = nodeExecution.startTime
        nodeExecutionDO.endTime = nodeExecution.endTime
        nodeExecutionDO.duration = nodeExecution.duration
        nodeExecutionDO.status = nodeExecution.status.name
        nodeExecutionDO.result = if (nodeExecution.result == null) null else objectMapper.writeValueAsString(nodeExecution.result)
        nodeExecutionDO.errorMessage = nodeExecution.errorMessage
        nodeExecutionDO.input = if (nodeExecution.input.isEmpty()) null else objectMapper.writeValueAsString(nodeExecution.input)
        nodeExecutionDO.output = if (nodeExecution.output.isEmpty()) null else objectMapper.writeValueAsString(nodeExecution.output)
        return nodeExecutionDO
    }
    
    /**
     * 转换为节点执行记录模型对象
     */
    private fun convertToNodeExecution(nodeExecutionDO: NodeExecutionDO): NodeExecution {
        val nodeExecution = NodeExecution()
        nodeExecution.id = nodeExecutionDO.id
        nodeExecution.nodeId = nodeExecutionDO.nodeId
        nodeExecution.nodeName = nodeExecutionDO.nodeName ?: ""
        nodeExecution.nodeType = if (nodeExecutionDO.nodeType.isNullOrEmpty()) {
            FlowNodeType.SERVICE
        } else {
            FlowNodeType.valueOf(nodeExecutionDO.nodeType!!)
        }
        nodeExecution.startTime = nodeExecutionDO.startTime ?: 0
        nodeExecution.endTime = nodeExecutionDO.endTime ?: 0
        nodeExecution.duration = nodeExecutionDO.duration ?: 0
        nodeExecution.status = if (nodeExecutionDO.status.isNullOrEmpty()) {
            FlowExecutionStatus.PENDING
        } else {
            FlowExecutionStatus.valueOf(nodeExecutionDO.status!!)
        }
        nodeExecution.result = if (nodeExecutionDO.result.isNullOrEmpty()) {
            null
        } else {
            objectMapper.readValue(nodeExecutionDO.result, Any::class.java)
        }
        nodeExecution.errorMessage = nodeExecutionDO.errorMessage ?: ""
        nodeExecution.input = if (nodeExecutionDO.input.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(nodeExecutionDO.input, object : TypeReference<Map<String, Any?>>() {})
        }
        nodeExecution.output = if (nodeExecutionDO.output.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(nodeExecutionDO.output, object : TypeReference<Map<String, Any?>>() {})
        }
        return nodeExecution
    }
}
