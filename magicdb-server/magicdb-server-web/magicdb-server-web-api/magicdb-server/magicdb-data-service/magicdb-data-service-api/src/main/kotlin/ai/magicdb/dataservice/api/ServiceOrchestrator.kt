package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.ServiceFlow
import ai.magicdb.dataservice.api.model.ServiceFlowExecution

/**
 * 服务编排器接口
 *
 * @author magicdb
 */
interface ServiceOrchestrator {
    /**
     * 创建流程
     *
     * @param flow 流程信息
     * @return 流程ID
     */
    fun createFlow(flow: ServiceFlow): String
    
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
    fun getFlow(flowId: String): ServiceFlow
    
    /**
     * 获取所有流程
     *
     * @return 流程列表
     */
    fun getAllFlows(): List<ServiceFlow>
    
    /**
     * 执行流程
     *
     * @param flowId 流程ID
     * @param parameters 参数
     * @return 执行ID
     */
    fun executeFlow(flowId: String, parameters: Map<String, Any?>): String
    
    /**
     * 取消流程执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    fun cancelExecution(executionId: String): Boolean
    
    /**
     * 获取流程执行状态
     *
     * @param executionId 执行ID
     * @return 执行状态
     */
    fun getExecutionStatus(executionId: String): ServiceFlowExecution
    
    /**
     * 获取流程执行结果
     *
     * @param executionId 执行ID
     * @return 执行结果
     */
    fun getExecutionResult(executionId: String): Any?
    
    /**
     * 获取所有流程执行
     *
     * @return 执行列表
     */
    fun getAllExecutions(): List<ServiceFlowExecution>
    
    /**
     * 验证流程
     *
     * @param flow 流程信息
     * @return 错误列表，如果为空则表示验证通过
     */
    fun validateFlow(flow: ServiceFlow): List<String>
}
