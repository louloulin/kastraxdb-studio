package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.model.ScheduledTask
import ai.magicdb.dataservice.api.model.TaskExecution
import ai.magicdb.dataservice.api.model.TaskStatus

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
     * @param status 任务状态，如果为null则获取所有状态的任务
     * @param group 任务分组，如果为null则获取所有分组的任务
     * @return 任务列表
     */
    fun getAllTasks(status: TaskStatus? = null, group: String? = null): List<ScheduledTask>
    
    /**
     * 获取服务的所有任务
     *
     * @param serviceId 服务ID
     * @return 任务列表
     */
    fun getTasksByService(serviceId: String): List<ScheduledTask>
    
    /**
     * 更新任务状态
     *
     * @param taskId 任务ID
     * @param status 任务状态
     * @return 是否成功
     */
    fun updateTaskStatus(taskId: String, status: TaskStatus): Boolean
    
    /**
     * 更新任务执行信息
     *
     * @param taskId 任务ID
     * @param lastExecuteTime 最后执行时间
     * @param nextExecuteTime 下次执行时间
     * @param lastExecuteResult 最后执行结果
     * @param lastExecuteMessage 最后执行消息
     * @param lastExecuteDuration 最后执行耗时
     * @return 是否成功
     */
    fun updateTaskExecutionInfo(
        taskId: String,
        lastExecuteTime: Long,
        nextExecuteTime: Long,
        lastExecuteResult: Boolean,
        lastExecuteMessage: String?,
        lastExecuteDuration: Long
    ): Boolean
    
    /**
     * 更新任务启用状态
     *
     * @param taskId 任务ID
     * @param enabled 是否启用
     * @return 是否成功
     */
    fun updateTaskEnabled(taskId: String, enabled: Boolean): Boolean
    
    /**
     * 保存任务执行记录
     *
     * @param execution 执行记录
     * @return 执行ID
     */
    fun saveTaskExecution(execution: TaskExecution): String
    
    /**
     * 更新任务执行记录状态
     *
     * @param executionId 执行ID
     * @param status 执行状态
     * @return 是否成功
     */
    fun updateTaskExecutionStatus(executionId: String, status: ai.magicdb.dataservice.api.model.ExecutionStatus): Boolean
    
    /**
     * 更新任务执行记录结果
     *
     * @param executionId 执行ID
     * @param status 执行状态
     * @param result 执行结果
     * @param errorMessage 错误消息
     * @param endTime 结束时间
     * @param duration 执行耗时
     * @return 是否成功
     */
    fun updateTaskExecutionResult(
        executionId: String,
        status: ai.magicdb.dataservice.api.model.ExecutionStatus,
        result: Any?,
        errorMessage: String?,
        endTime: Long,
        duration: Long
    ): Boolean
    
    /**
     * 获取任务执行记录
     *
     * @param executionId 执行ID
     * @return 执行记录
     */
    fun getTaskExecution(executionId: String): TaskExecution?
    
    /**
     * 获取任务的所有执行记录
     *
     * @param taskId 任务ID
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 执行记录列表
     */
    fun getTaskExecutions(taskId: String, limit: Int = 10, offset: Int = 0): List<TaskExecution>
    
    /**
     * 获取任务的最后一次执行记录
     *
     * @param taskId 任务ID
     * @return 执行记录
     */
    fun getLastTaskExecution(taskId: String): TaskExecution?
}
