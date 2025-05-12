package ai.magicdb.data.service.core.repository

import ai.magicdb.data.service.api.model.FlowConnection
import ai.magicdb.data.service.api.model.FlowExecution
import ai.magicdb.data.service.api.model.FlowExecutionStatus
import ai.magicdb.data.service.api.model.FlowNode
import ai.magicdb.data.service.api.model.FlowNodeType
import ai.magicdb.data.service.api.model.FlowParameter
import ai.magicdb.data.service.api.model.NodeExecution
import ai.magicdb.data.service.api.model.ServiceFlow
import ai.magicdb.data.service.api.model.TriggerType

/**
 * 服务流程存储库接口
 *
 * @author magicdb
 */
interface ServiceFlowRepository {
    
    /**
     * 保存流程
     *
     * @param flow 流程信息
     * @return 流程ID
     */
    fun saveFlow(flow: ServiceFlow): String
    
    /**
     * 更新流程
     *
     * @param flow 流程信息
     * @return 是否成功
     */
    fun updateFlow(flow: ServiceFlow): Boolean
    
    /**
     * 删除流程
     *
     * @param flowId 流程ID
     * @return 是否成功
     */
    fun deleteFlow(flowId: String): Boolean
    
    /**
     * 获取流程
     *
     * @param flowId 流程ID
     * @return 流程信息
     */
    fun getFlow(flowId: String): ServiceFlow?
    
    /**
     * 获取所有流程
     *
     * @param group 分组，如果为null则获取所有分组的流程
     * @return 流程列表
     */
    fun getAllFlows(group: String? = null): List<ServiceFlow>
    
    /**
     * 更新流程执行信息
     *
     * @param flowId 流程ID
     * @param lastExecuteTime 最后执行时间
     * @param lastExecuteResult 最后执行结果
     * @param lastExecuteMessage 最后执行消息
     * @param lastExecuteDuration 最后执行耗时
     * @return 是否成功
     */
    fun updateFlowExecutionInfo(
        flowId: String,
        lastExecuteTime: Long,
        lastExecuteResult: Boolean,
        lastExecuteMessage: String?,
        lastExecuteDuration: Long
    ): Boolean
    
    /**
     * 更新流程启用状态
     *
     * @param flowId 流程ID
     * @param enabled 是否启用
     * @return 是否成功
     */
    fun updateFlowEnabled(flowId: String, enabled: Boolean): Boolean
    
    /**
     * 保存流程执行记录
     *
     * @param execution 执行记录
     * @return 执行ID
     */
    fun saveFlowExecution(execution: FlowExecution): String
    
    /**
     * 更新流程执行记录状态
     *
     * @param executionId 执行ID
     * @param status 执行状态
     * @return 是否成功
     */
    fun updateFlowExecutionStatus(executionId: String, status: FlowExecutionStatus): Boolean
    
    /**
     * 更新流程执行记录结果
     *
     * @param executionId 执行ID
     * @param status 执行状态
     * @param result 执行结果
     * @param errorMessage 错误消息
     * @param endTime 结束时间
     * @param duration 执行耗时
     * @return 是否成功
     */
    fun updateFlowExecutionResult(
        executionId: String,
        status: FlowExecutionStatus,
        result: Any?,
        errorMessage: String?,
        endTime: Long,
        duration: Long
    ): Boolean
    
    /**
     * 获取流程执行记录
     *
     * @param executionId 执行ID
     * @return 执行记录
     */
    fun getFlowExecution(executionId: String): FlowExecution?
    
    /**
     * 获取流程的所有执行记录
     *
     * @param flowId 流程ID
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 执行记录列表
     */
    fun getFlowExecutions(flowId: String, limit: Int = 10, offset: Int = 0): List<FlowExecution>
    
    /**
     * 获取流程的最后一次执行记录
     *
     * @param flowId 流程ID
     * @return 执行记录
     */
    fun getLastFlowExecution(flowId: String): FlowExecution?
    
    /**
     * 保存节点执行记录
     *
     * @param nodeExecution 节点执行记录
     * @param flowExecutionId 流程执行ID
     * @return 节点执行ID
     */
    fun saveNodeExecution(nodeExecution: NodeExecution, flowExecutionId: String): String
    
    /**
     * 更新节点执行记录状态
     *
     * @param id 节点执行ID
     * @param status 执行状态
     * @return 是否成功
     */
    fun updateNodeExecutionStatus(id: String, status: FlowExecutionStatus): Boolean
    
    /**
     * 更新节点执行记录结果
     *
     * @param id 节点执行ID
     * @param status 执行状态
     * @param result 执行结果
     * @param errorMessage 错误消息
     * @param endTime 结束时间
     * @param duration 执行耗时
     * @param output 输出数据
     * @return 是否成功
     */
    fun updateNodeExecutionResult(
        id: String,
        status: FlowExecutionStatus,
        result: Any?,
        errorMessage: String?,
        endTime: Long,
        duration: Long,
        output: Map<String, Any?>
    ): Boolean
    
    /**
     * 获取流程执行的所有节点执行记录
     *
     * @param flowExecutionId 流程执行ID
     * @return 节点执行记录列表
     */
    fun getNodeExecutions(flowExecutionId: String): List<NodeExecution>
}
