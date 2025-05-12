package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.FlowExecution
import ai.magicdb.data.service.api.model.ServiceFlow
import ai.magicdb.data.service.api.model.ServiceFlowGroup

/**
 * 服务流程存储库接口
 *
 * @author magicdb
 */
interface ServiceFlowRepository {
    /**
     * 保存服务流程
     *
     * @param flow 服务流程
     * @return 流程ID
     */
    fun saveFlow(flow: ServiceFlow): String
    
    /**
     * 获取服务流程
     *
     * @param flowId 流程ID
     * @return 服务流程
     */
    fun getFlow(flowId: String): ServiceFlow?
    
    /**
     * 获取服务流程列表
     *
     * @param groupId 分组ID
     * @param name 流程名称
     * @param status 流程状态
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 服务流程列表
     */
    fun getFlows(
        groupId: String? = null,
        name: String? = null,
        status: String? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<ServiceFlow>
    
    /**
     * 删除服务流程
     *
     * @param flowId 流程ID
     * @return 是否成功
     */
    fun deleteFlow(flowId: String): Boolean
    
    /**
     * 保存服务流程分组
     *
     * @param group 服务流程分组
     * @return 分组ID
     */
    fun saveFlowGroup(group: ServiceFlowGroup): String
    
    /**
     * 获取服务流程分组
     *
     * @param groupId 分组ID
     * @return 服务流程分组
     */
    fun getFlowGroup(groupId: String): ServiceFlowGroup?
    
    /**
     * 获取服务流程分组列表
     *
     * @param parentId 父分组ID
     * @param name 分组名称
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 服务流程分组列表
     */
    fun getFlowGroups(
        parentId: String? = null,
        name: String? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<ServiceFlowGroup>
    
    /**
     * 删除服务流程分组
     *
     * @param groupId 分组ID
     * @return 是否成功
     */
    fun deleteFlowGroup(groupId: String): Boolean
    
    /**
     * 保存流程执行记录
     *
     * @param execution 流程执行记录
     * @return 执行ID
     */
    fun saveFlowExecution(execution: FlowExecution): String
    
    /**
     * 获取流程执行记录
     *
     * @param executionId 执行ID
     * @return 流程执行记录
     */
    fun getFlowExecution(executionId: String): FlowExecution?
    
    /**
     * 获取流程执行记录列表
     *
     * @param flowId 流程ID
     * @param status 执行状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 流程执行记录列表
     */
    fun getFlowExecutions(
        flowId: String? = null,
        status: String? = null,
        startTime: Long? = null,
        endTime: Long? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<FlowExecution>
    
    /**
     * 删除流程执行记录
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    fun deleteFlowExecution(executionId: String): Boolean
    
    /**
     * 清除流程执行记录
     *
     * @param flowId 流程ID
     * @param before 清除此时间之前的记录
     * @return 清除的记录数
     */
    fun clearFlowExecutions(
        flowId: String? = null,
        before: Long? = null
    ): Int
}
