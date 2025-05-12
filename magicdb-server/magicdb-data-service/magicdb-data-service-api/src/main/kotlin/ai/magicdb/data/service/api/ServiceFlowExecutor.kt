package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.FlowExecution
import ai.magicdb.data.service.api.model.FlowExecutionStatus
import ai.magicdb.data.service.api.model.ServiceFlow

/**
 * 服务流程执行器接口
 *
 * @author magicdb
 */
interface ServiceFlowExecutor {
    
    /**
     * 执行流程
     *
     * @param flow 流程信息
     * @param parameters 执行参数
     * @param executionId 执行ID，如果为null则自动生成
     * @return 执行记录
     */
    fun execute(flow: ServiceFlow, parameters: Map<String, Any?> = emptyMap(), executionId: String? = null): FlowExecution
    
    /**
     * 异步执行流程
     *
     * @param flow 流程信息
     * @param parameters 执行参数
     * @param executionId 执行ID，如果为null则自动生成
     * @return 执行ID
     */
    fun executeAsync(flow: ServiceFlow, parameters: Map<String, Any?> = emptyMap(), executionId: String? = null): String
    
    /**
     * 取消流程执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    fun cancel(executionId: String): Boolean
    
    /**
     * 暂停流程执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    fun pause(executionId: String): Boolean
    
    /**
     * 恢复流程执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    fun resume(executionId: String): Boolean
    
    /**
     * 获取流程执行状态
     *
     * @param executionId 执行ID
     * @return 执行状态
     */
    fun getStatus(executionId: String): FlowExecutionStatus
    
    /**
     * 获取流程执行记录
     *
     * @param executionId 执行ID
     * @return 执行记录
     */
    fun getExecution(executionId: String): FlowExecution?
    
    /**
     * 获取正在执行的流程数量
     *
     * @return 执行数量
     */
    fun getRunningCount(): Int
    
    /**
     * 获取所有正在执行的流程ID
     *
     * @return 执行ID列表
     */
    fun getRunningExecutionIds(): List<String>
}
