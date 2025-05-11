package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.FlowExecution
import ai.magicdb.dataservice.api.model.ServiceFlow

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
     * @return 流程列表
     */
    fun getAllFlows(): List<ServiceFlow>
    
    /**
     * 保存执行记录
     *
     * @param execution 执行记录
     * @return 执行ID
     */
    fun saveExecution(execution: FlowExecution): String
    
    /**
     * 更新执行记录
     *
     * @param execution 执行记录
     * @return 是否成功
     */
    fun updateExecution(execution: FlowExecution): Boolean
    
    /**
     * 删除执行记录
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    fun deleteExecution(executionId: String): Boolean
    
    /**
     * 获取执行记录
     *
     * @param executionId 执行ID
     * @return 执行记录
     */
    fun getExecution(executionId: String): FlowExecution?
    
    /**
     * 获取所有执行记录
     *
     * @return 执行记录列表
     */
    fun getAllExecutions(): List<FlowExecution>
    
    /**
     * 获取流程的所有执行记录
     *
     * @param flowId 流程ID
     * @return 执行记录列表
     */
    fun getExecutionsByFlowId(flowId: String): List<FlowExecution>
}
