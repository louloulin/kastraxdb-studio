package ai.magicdb.dataservice.core.orchestration

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.ServiceFlowExecutor
import ai.magicdb.dataservice.api.model.FlowExecution
import ai.magicdb.dataservice.api.model.FlowExecutionStatus
import ai.magicdb.dataservice.api.model.FlowNode
import ai.magicdb.dataservice.api.model.FlowNodeType
import ai.magicdb.dataservice.api.model.NodeExecution
import ai.magicdb.dataservice.api.model.ServiceFlow
import ai.magicdb.dataservice.api.model.TriggerType
import ai.magicdb.script.api.ScriptExecutor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * 默认服务流程执行器实现
 *
 * @author magicdb
 */
@Service
class DefaultServiceFlowExecutor(
    private val serviceExecutor: DataServiceExecutor,
    private val scriptExecutor: ScriptExecutor
) : ServiceFlowExecutor {
    private val logger = LoggerFactory.getLogger(DefaultServiceFlowExecutor::class.java)
    
    // 执行线程池
    private val executorService: ExecutorService = Executors.newFixedThreadPool(10)
    
    // 执行记录缓存
    private val executionCache = ConcurrentHashMap<String, FlowExecution>()
    
    override fun execute(flow: ServiceFlow, parameters: Map<String, Any?>, executionId: String?): FlowExecution {
        val actualExecutionId = executionId ?: UUID.randomUUID().toString()
        
        try {
            // 创建执行记录
            val execution = createExecution(flow, parameters, actualExecutionId)
            
            // 保存执行记录
            executionCache[actualExecutionId] = execution
            
            // 执行流程
            val result = executeFlow(flow, parameters, execution)
            
            // 更新执行记录
            execution.status = FlowExecutionStatus.SUCCESS
            execution.result = result
            execution.endTime = System.currentTimeMillis()
            execution.duration = execution.endTime - execution.startTime
            
            // 保存执行记录
            executionCache[actualExecutionId] = execution
            
            return execution
        } catch (e: Exception) {
            logger.error("执行流程失败: {}", flow.id, e)
            
            // 获取执行记录
            val execution = executionCache[actualExecutionId] ?: createExecution(flow, parameters, actualExecutionId)
            
            // 更新执行记录
            execution.status = FlowExecutionStatus.FAILED
            execution.errorMessage = e.message ?: "执行流程失败"
            execution.endTime = System.currentTimeMillis()
            execution.duration = execution.endTime - execution.startTime
            
            // 保存执行记录
            executionCache[actualExecutionId] = execution
            
            return execution
        }
    }
    
    override fun executeAsync(flow: ServiceFlow, parameters: Map<String, Any?>, executionId: String?): String {
        val actualExecutionId = executionId ?: UUID.randomUUID().toString()
        
        // 创建执行记录
        val execution = createExecution(flow, parameters, actualExecutionId)
        
        // 保存执行记录
        executionCache[actualExecutionId] = execution
        
        // 异步执行流程
        executorService.submit {
            try {
                // 执行流程
                val result = executeFlow(flow, parameters, execution)
                
                // 更新执行记录
                execution.status = FlowExecutionStatus.SUCCESS
                execution.result = result
                execution.endTime = System.currentTimeMillis()
                execution.duration = execution.endTime - execution.startTime
                
                // 保存执行记录
                executionCache[actualExecutionId] = execution
            } catch (e: Exception) {
                logger.error("异步执行流程失败: {}", flow.id, e)
                
                // 更新执行记录
                execution.status = FlowExecutionStatus.FAILED
                execution.errorMessage = e.message ?: "执行流程失败"
                execution.endTime = System.currentTimeMillis()
                execution.duration = execution.endTime - execution.startTime
                
                // 保存执行记录
                executionCache[actualExecutionId] = execution
            }
        }
        
        return actualExecutionId
    }
    
    override fun cancel(executionId: String): Boolean {
        // 获取执行记录
        val execution = executionCache[executionId] ?: return false
        
        // 检查执行状态
        if (execution.status != FlowExecutionStatus.RUNNING && execution.status != FlowExecutionStatus.PENDING) {
            return false
        }
        
        // 更新执行记录
        execution.status = FlowExecutionStatus.CANCELED
        execution.endTime = System.currentTimeMillis()
        execution.duration = execution.endTime - execution.startTime
        
        // 保存执行记录
        executionCache[executionId] = execution
        
        return true
    }
    
    override fun pause(executionId: String): Boolean {
        // 获取执行记录
        val execution = executionCache[executionId] ?: return false
        
        // 检查执行状态
        if (execution.status != FlowExecutionStatus.RUNNING) {
            return false
        }
        
        // 更新执行记录
        execution.status = FlowExecutionStatus.PAUSED
        
        // 保存执行记录
        executionCache[executionId] = execution
        
        return true
    }
    
    override fun resume(executionId: String): Boolean {
        // 获取执行记录
        val execution = executionCache[executionId] ?: return false
        
        // 检查执行状态
        if (execution.status != FlowExecutionStatus.PAUSED) {
            return false
        }
        
        // 更新执行记录
        execution.status = FlowExecutionStatus.RUNNING
        
        // 保存执行记录
        executionCache[executionId] = execution
        
        return true
    }
    
    override fun getStatus(executionId: String): FlowExecutionStatus {
        // 获取执行记录
        val execution = executionCache[executionId] ?: return FlowExecutionStatus.FAILED
        
        return execution.status
    }
    
    override fun getExecution(executionId: String): FlowExecution? {
        return executionCache[executionId]
    }
    
    override fun getRunningCount(): Int {
        return executionCache.values.count { it.status == FlowExecutionStatus.RUNNING }
    }
    
    override fun getRunningExecutionIds(): List<String> {
        return executionCache.entries
            .filter { it.value.status == FlowExecutionStatus.RUNNING }
            .map { it.key }
    }
    
    override fun getAllExecutions(): List<FlowExecution> {
        return executionCache.values.toList()
    }
    
    /**
     * 创建执行记录
     */
    private fun createExecution(flow: ServiceFlow, parameters: Map<String, Any?>, executionId: String): FlowExecution {
        return FlowExecution(
            id = executionId,
            flowId = flow.id,
            flowName = flow.name,
            parameters = parameters,
            variables = mutableMapOf(),
            startTime = System.currentTimeMillis(),
            endTime = 0,
            duration = 0,
            status = FlowExecutionStatus.RUNNING,
            result = null,
            errorMessage = "",
            nodeExecutions = mutableListOf(),
            triggerType = TriggerType.MANUAL,
            triggerId = ""
        )
    }
    
    /**
     * 执行流程
     */
    private fun executeFlow(flow: ServiceFlow, parameters: Map<String, Any?>, execution: FlowExecution): Any? {
        // 初始化变量
        val variables = mutableMapOf<String, Any?>()
        variables.putAll(parameters)
        variables["flow"] = flow
        variables["execution"] = execution
        
        // 获取开始节点
        val startNode = flow.nodes.find { it.type == FlowNodeType.START }
            ?: throw IllegalStateException("流程没有开始节点")
        
        // 执行节点
        return executeNode(startNode, flow, variables, execution)
    }
    
    /**
     * 执行节点
     */
    private fun executeNode(node: FlowNode, flow: ServiceFlow, variables: MutableMap<String, Any?>, execution: FlowExecution): Any? {
        // 创建节点执行记录
        val nodeExecution = createNodeExecution(node, execution.id)
        
        try {
            // 更新节点执行状态
            nodeExecution.status = FlowExecutionStatus.RUNNING
            nodeExecution.startTime = System.currentTimeMillis()
            
            // 添加节点执行记录
            (execution.nodeExecutions as MutableList<NodeExecution>).add(nodeExecution)
            
            // 执行节点
            val result = when (node.type) {
                FlowNodeType.START -> executeStartNode(node, flow, variables, execution)
                FlowNodeType.END -> executeEndNode(node, flow, variables, execution)
                FlowNodeType.SERVICE -> executeServiceNode(node, flow, variables, execution)
                FlowNodeType.CONDITION -> executeConditionNode(node, flow, variables, execution)
                FlowNodeType.SCRIPT -> executeScriptNode(node, flow, variables, execution)
                FlowNodeType.TRANSFORM -> executeTransformNode(node, flow, variables, execution)
                FlowNodeType.DELAY -> executeDelayNode(node, flow, variables, execution)
                FlowNodeType.SUB_FLOW -> executeSubFlowNode(node, flow, variables, execution)
                else -> throw IllegalStateException("不支持的节点类型: ${node.type}")
            }
            
            // 更新节点执行记录
            nodeExecution.status = FlowExecutionStatus.SUCCESS
            nodeExecution.result = result
            nodeExecution.endTime = System.currentTimeMillis()
            nodeExecution.duration = nodeExecution.endTime - nodeExecution.startTime
            
            return result
        } catch (e: Exception) {
            logger.error("执行节点失败: {}", node.id, e)
            
            // 更新节点执行记录
            nodeExecution.status = FlowExecutionStatus.FAILED
            nodeExecution.errorMessage = e.message ?: "执行节点失败"
            nodeExecution.endTime = System.currentTimeMillis()
            nodeExecution.duration = nodeExecution.endTime - nodeExecution.startTime
            
            throw e
        }
    }
    
    /**
     * 创建节点执行记录
     */
    private fun createNodeExecution(node: FlowNode, executionId: String): NodeExecution {
        return NodeExecution(
            id = UUID.randomUUID().toString(),
            nodeId = node.id,
            nodeName = node.name,
            nodeType = node.type,
            startTime = 0,
            endTime = 0,
            duration = 0,
            status = FlowExecutionStatus.PENDING,
            result = null,
            errorMessage = "",
            input = emptyMap(),
            output = emptyMap()
        )
    }
    
    /**
     * 执行开始节点
     */
    private fun executeStartNode(node: FlowNode, flow: ServiceFlow, variables: MutableMap<String, Any?>, execution: FlowExecution): Any? {
        // 获取下一个节点
        val nextNode = getNextNode(node, flow, variables)
            ?: throw IllegalStateException("开始节点没有下一个节点")
        
        // 执行下一个节点
        return executeNode(nextNode, flow, variables, execution)
    }
    
    /**
     * 执行结束节点
     */
    private fun executeEndNode(node: FlowNode, flow: ServiceFlow, variables: MutableMap<String, Any?>, execution: FlowExecution): Any? {
        // 获取结果
        return node.config["result"] ?: variables["result"]
    }
    
    /**
     * 执行服务节点
     */
    private fun executeServiceNode(node: FlowNode, flow: ServiceFlow, variables: MutableMap<String, Any?>, execution: FlowExecution): Any? {
        // 获取服务ID
        val serviceId = node.config["serviceId"] as? String
            ?: throw IllegalStateException("服务节点没有指定服务ID")
        
        // 获取服务参数
        val serviceParams = mutableMapOf<String, Any?>()
        
        // 添加全局变量
        serviceParams.putAll(variables)
        
        // 添加节点配置的参数
        val nodeParams = node.config["parameters"] as? Map<String, Any?> ?: emptyMap()
        serviceParams.putAll(nodeParams)
        
        // 执行服务
        val result = serviceExecutor.execute(serviceId, serviceParams)
        
        // 保存结果
        variables["result"] = result.data
        
        // 获取下一个节点
        val nextNode = getNextNode(node, flow, variables)
            ?: return result.data
        
        // 执行下一个节点
        return executeNode(nextNode, flow, variables, execution)
    }
    
    /**
     * 执行条件节点
     */
    private fun executeConditionNode(node: FlowNode, flow: ServiceFlow, variables: MutableMap<String, Any?>, execution: FlowExecution): Any? {
        // 获取条件表达式
        val condition = node.config["condition"] as? String
            ?: throw IllegalStateException("条件节点没有指定条件表达式")
        
        // 执行条件表达式
        val result = evaluateCondition(condition, variables)
        
        // 保存结果
        variables["result"] = result
        
        // 获取下一个节点
        val nextNode = getNextNodeByCondition(node, flow, variables, result)
            ?: return result
        
        // 执行下一个节点
        return executeNode(nextNode, flow, variables, execution)
    }
    
    /**
     * 执行脚本节点
     */
    private fun executeScriptNode(node: FlowNode, flow: ServiceFlow, variables: MutableMap<String, Any?>, execution: FlowExecution): Any? {
        // 获取脚本内容
        val script = node.config["script"] as? String
            ?: throw IllegalStateException("脚本节点没有指定脚本内容")
        
        // 获取脚本语言
        val language = node.config["language"] as? String
            ?: throw IllegalStateException("脚本节点没有指定脚本语言")
        
        // 执行脚本
        val result = scriptExecutor.execute(language, script, variables)
        
        // 保存结果
        variables["result"] = result
        
        // 获取下一个节点
        val nextNode = getNextNode(node, flow, variables)
            ?: return result
        
        // 执行下一个节点
        return executeNode(nextNode, flow, variables, execution)
    }
    
    /**
     * 执行转换节点
     */
    private fun executeTransformNode(node: FlowNode, flow: ServiceFlow, variables: MutableMap<String, Any?>, execution: FlowExecution): Any? {
        // 获取转换表达式
        val transform = node.config["transform"] as? String
            ?: throw IllegalStateException("转换节点没有指定转换表达式")
        
        // 执行转换表达式
        val result = evaluateTransform(transform, variables)
        
        // 保存结果
        variables["result"] = result
        
        // 获取下一个节点
        val nextNode = getNextNode(node, flow, variables)
            ?: return result
        
        // 执行下一个节点
        return executeNode(nextNode, flow, variables, execution)
    }
    
    /**
     * 执行延时节点
     */
    private fun executeDelayNode(node: FlowNode, flow: ServiceFlow, variables: MutableMap<String, Any?>, execution: FlowExecution): Any? {
        // 获取延时时间
        val delay = node.config["delay"] as? Long
            ?: throw IllegalStateException("延时节点没有指定延时时间")
        
        // 延时
        Thread.sleep(delay)
        
        // 获取下一个节点
        val nextNode = getNextNode(node, flow, variables)
            ?: return null
        
        // 执行下一个节点
        return executeNode(nextNode, flow, variables, execution)
    }
    
    /**
     * 执行子流程节点
     */
    private fun executeSubFlowNode(node: FlowNode, flow: ServiceFlow, variables: MutableMap<String, Any?>, execution: FlowExecution): Any? {
        // 获取子流程ID
        val subFlowId = node.config["flowId"] as? String
            ?: throw IllegalStateException("子流程节点没有指定子流程ID")
        
        // 获取子流程参数
        val subFlowParams = mutableMapOf<String, Any?>()
        
        // 添加全局变量
        subFlowParams.putAll(variables)
        
        // 添加节点配置的参数
        val nodeParams = node.config["parameters"] as? Map<String, Any?> ?: emptyMap()
        subFlowParams.putAll(nodeParams)
        
        // 执行子流程
        // 注意：这里应该调用流程存储库获取子流程，然后执行
        // 但由于我们没有流程存储库的引用，所以这里只是模拟执行
        val result = mapOf("subFlowId" to subFlowId, "parameters" to subFlowParams)
        
        // 保存结果
        variables["result"] = result
        
        // 获取下一个节点
        val nextNode = getNextNode(node, flow, variables)
            ?: return result
        
        // 执行下一个节点
        return executeNode(nextNode, flow, variables, execution)
    }
    
    /**
     * 获取下一个节点
     */
    private fun getNextNode(node: FlowNode, flow: ServiceFlow, variables: Map<String, Any?>): FlowNode? {
        // 获取当前节点的所有出连接
        val outConnections = flow.connections.filter { it.sourceId == node.id }
        
        // 如果没有出连接，返回null
        if (outConnections.isEmpty()) {
            return null
        }
        
        // 如果只有一个出连接，返回目标节点
        if (outConnections.size == 1) {
            val targetId = outConnections[0].targetId
            return flow.nodes.find { it.id == targetId }
        }
        
        // 如果有多个出连接，根据条件选择
        for (connection in outConnections) {
            // 获取连接条件
            val condition = connection.condition
            
            // 如果没有条件，或者条件为true，返回目标节点
            if (condition.isBlank() || evaluateCondition(condition, variables)) {
                val targetId = connection.targetId
                return flow.nodes.find { it.id == targetId }
            }
        }
        
        // 如果没有符合条件的连接，返回null
        return null
    }
    
    /**
     * 根据条件获取下一个节点
     */
    private fun getNextNodeByCondition(node: FlowNode, flow: ServiceFlow, variables: Map<String, Any?>, condition: Boolean): FlowNode? {
        // 获取当前节点的所有出连接
        val outConnections = flow.connections.filter { it.sourceId == node.id }
        
        // 如果没有出连接，返回null
        if (outConnections.isEmpty()) {
            return null
        }
        
        // 根据条件选择连接
        for (connection in outConnections) {
            // 获取连接标签
            val label = connection.label
            
            // 如果标签为"true"且条件为true，或者标签为"false"且条件为false，返回目标节点
            if ((label == "true" && condition) || (label == "false" && !condition)) {
                val targetId = connection.targetId
                return flow.nodes.find { it.id == targetId }
            }
        }
        
        // 如果没有符合条件的连接，返回null
        return null
    }
    
    /**
     * 评估条件表达式
     */
    private fun evaluateCondition(condition: String, variables: Map<String, Any?>): Boolean {
        try {
            // 简单实现，仅支持基本比较
            // 实际应用中应该使用表达式引擎或脚本引擎
            
            // 替换变量
            var expr = condition
            for ((key, value) in variables) {
                expr = expr.replace("\${$key}", value.toString())
            }
            
            // 解析表达式
            if (expr == "true") return true
            if (expr == "false") return false
            
            // 简单比较
            if (expr.contains("==")) {
                val parts = expr.split("==")
                return parts[0].trim() == parts[1].trim()
            }
            
            if (expr.contains("!=")) {
                val parts = expr.split("!=")
                return parts[0].trim() != parts[1].trim()
            }
            
            if (expr.contains(">")) {
                val parts = expr.split(">")
                return parts[0].trim().toDouble() > parts[1].trim().toDouble()
            }
            
            if (expr.contains("<")) {
                val parts = expr.split("<")
                return parts[0].trim().toDouble() < parts[1].trim().toDouble()
            }
            
            if (expr.contains(">=")) {
                val parts = expr.split(">=")
                return parts[0].trim().toDouble() >= parts[1].trim().toDouble()
            }
            
            if (expr.contains("<=")) {
                val parts = expr.split("<=")
                return parts[0].trim().toDouble() <= parts[1].trim().toDouble()
            }
            
            // 默认返回false
            return false
        } catch (e: Exception) {
            logger.error("评估条件表达式失败: {}", condition, e)
            return false
        }
    }
    
    /**
     * 评估转换表达式
     */
    private fun evaluateTransform(transform: String, variables: Map<String, Any?>): Any? {
        try {
            // 简单实现，仅支持基本转换
            // 实际应用中应该使用表达式引擎或脚本引擎
            
            // 替换变量
            var expr = transform
            for ((key, value) in variables) {
                expr = expr.replace("\${$key}", value.toString())
            }
            
            // 返回表达式结果
            return expr
        } catch (e: Exception) {
            logger.error("评估转换表达式失败: {}", transform, e)
            return null
        }
    }
}
