package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.ExecutionStatus
import ai.magicdb.dataservice.api.model.ScheduledTask
import ai.magicdb.dataservice.api.model.ScheduledTaskStatus
import ai.magicdb.dataservice.api.model.TaskExecution

/**
 * 任务存储库接口
 *
 * @author magicdb
 */
interface TaskRepository {
    /**
     * 保存任务
     *
     * @param task 任务信息
     * @return 任务ID
     */
    fun saveTask(task: ScheduledTask): String
    
    /**
     * 更新任务
     *
     * @param task 任务信息
     * @return 是否成功
     */
    fun updateTask(task: ScheduledTask): Boolean
    
    /**
     * 删除任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    fun deleteTask(taskId: String): Boolean
    
    /**
     * 获取任务
     *
     * @param taskId 任务ID
     * @return 任务信息
     */
    fun getTask(taskId: String): ScheduledTask?
    
    /**
     * 获取所有任务
     *
     * @param status 任务状态，可选
     * @param group 任务分组，可选
     * @return 任务列表
     */
    fun getAllTasks(status: ScheduledTaskStatus? = null, group: String? = null): List<ScheduledTask>
    
    /**
     * 获取分组任务
     *
     * @param group 分组
     * @return 任务列表
     */
    fun getTasksByGroup(group: String): List<ScheduledTask>
    
    /**
     * 获取状态任务
     *
     * @param status 状态
     * @return 任务列表
     */
    fun getTasksByStatus(status: ScheduledTaskStatus): List<ScheduledTask>
    
    /**
     * 获取标签任务
     *
     * @param tag 标签
     * @return 任务列表
     */
    fun getTasksByTag(tag: String): List<ScheduledTask>
    
    /**
     * 获取服务任务
     *
     * @param serviceId 服务ID
     * @return 任务列表
     */
    fun getTasksByService(serviceId: String): List<ScheduledTask>
    
    /**
     * 获取用户任务
     *
     * @param userId 用户ID
     * @return 任务列表
     */
    fun getTasksByUser(userId: Long): List<ScheduledTask>
    
    /**
     * 获取分页任务
     *
     * @param page 页码
     * @param size 每页大小
     * @return 任务列表
     */
    fun getTasksByPage(page: Int, size: Int): List<ScheduledTask>
    
    /**
     * 获取任务数量
     *
     * @return 任务数量
     */
    fun getTaskCount(): Long
    
    /**
     * 获取状态任务数量
     *
     * @param status 状态
     * @return 任务数量
     */
    fun getTaskCountByStatus(status: ScheduledTaskStatus): Long
    
    /**
     * 获取分组任务数量
     *
     * @param group 分组
     * @return 任务数量
     */
    fun getTaskCountByGroup(group: String): Long
    
    /**
     * 保存执行记录
     *
     * @param execution 执行记录
     * @return 执行ID
     */
    fun saveExecution(execution: TaskExecution): String
    
    /**
     * 更新执行记录
     *
     * @param execution 执行记录
     * @return 是否成功
     */
    fun updateExecution(execution: TaskExecution): Boolean
    
    /**
     * 获取执行记录
     *
     * @param executionId 执行ID
     * @return 执行记录
     */
    fun getExecution(executionId: String): TaskExecution?
    
    /**
     * 获取任务执行记录
     *
     * @param taskId 任务ID
     * @return 执行记录列表
     */
    fun getExecutionsByTask(taskId: String): List<TaskExecution>
    
    /**
     * 获取状态执行记录
     *
     * @param status 状态
     * @return 执行记录列表
     */
    fun getExecutionsByStatus(status: ExecutionStatus): List<TaskExecution>
    
    /**
     * 获取分页执行记录
     *
     * @param page 页码
     * @param size 每页大小
     * @return 执行记录列表
     */
    fun getExecutionsByPage(page: Int, size: Int): List<TaskExecution>
    
    /**
     * 获取执行记录数量
     *
     * @return 执行记录数量
     */
    fun getExecutionCount(): Long
    
    /**
     * 获取状态执行记录数量
     *
     * @param status 状态
     * @return 执行记录数量
     */
    fun getExecutionCountByStatus(status: ExecutionStatus): Long
    
    /**
     * 获取任务执行记录数量
     *
     * @param taskId 任务ID
     * @return 执行记录数量
     */
    fun getExecutionCountByTask(taskId: String): Long
}
