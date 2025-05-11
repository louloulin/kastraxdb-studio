package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.FlowExecution
import ai.magicdb.dataservice.api.model.ServiceFlow

/**
 * 服务流程管理器接口
 *
 * @author magicdb
 */
interface ServiceFlowManager {
    
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
    fun getFlow(flowId: String): ServiceFlow?
    
    /**
     * 获取所有流程
     *
     * @param group 分组，如果为null则获取所有分组的流程
     * @return 流程列表
     */
    fun getAllFlows(group: String? = null): List<ServiceFlow>
    
    /**
     * 执行流程
     *
     * @param flowId 流程ID
     * @param parameters 执行参数
     * @return 执行ID
     */
    fun executeFlow(flowId: String, parameters: Map<String, Any?> = emptyMap()): String
    
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
     * @param limit 限制数量，默认为10
     * @param offset 偏移量，默认为0
     * @return 执行记录列表
     */
    fun getFlowExecutions(flowId: String, limit: Int = 10, offset: Int = 0): List<FlowExecution>
    
    /**
     * 取消流程执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    fun cancelExecution(executionId: String): Boolean
    
    /**
     * 暂停流程执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    fun pauseExecution(executionId: String): Boolean
    
    /**
     * 恢复流程执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    fun resumeExecution(executionId: String): Boolean
    
    /**
     * 验证流程
     *
     * @param flow 流程信息
     * @return 验证结果，如果验证通过则返回null，否则返回错误信息
     */
    fun validateFlow(flow: ServiceFlow): String?
    
    /**
     * 导出流程
     *
     * @param flowId 流程ID
     * @return 流程JSON字符串
     */
    fun exportFlow(flowId: String): String
    
    /**
     * 导入流程
     *
     * @param flowJson 流程JSON字符串
     * @return 流程ID
     */
    fun importFlow(flowJson: String): String
    
    /**
     * 复制流程
     *
     * @param flowId 流程ID
     * @param newName 新流程名称
     * @return 新流程ID
     */
    fun copyFlow(flowId: String, newName: String): String
}
