package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.ScheduledTask
import ai.magicdb.dataservice.api.model.SchedulerStats
import ai.magicdb.dataservice.api.model.SchedulerStatus
import ai.magicdb.dataservice.api.model.TaskExecution
import ai.magicdb.dataservice.api.model.TaskStatus
import ai.magicdb.dataservice.api.model.TriggerType

/**
 * 调度服务接口
 *
 * @author magicdb
 */
interface SchedulerService {

    /**
     * 创建任务
     *
     * @param task 任务信息
     * @return 任务ID
     */
    fun createTask(task: ScheduledTask): String

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
     * 启动任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    fun startTask(taskId: String): Boolean

    /**
     * 暂停任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    fun pauseTask(taskId: String): Boolean

    /**
     * 恢复任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    fun resumeTask(taskId: String): Boolean

    /**
     * 立即执行任务
     *
     * @param taskId 任务ID
     * @param parameters 执行参数，如果为null则使用任务默认参数
     * @return 执行ID
     */
    fun executeTask(taskId: String, parameters: Map<String, Any?>? = null): String

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
     * @param limit 限制数量，默认为10
     * @param offset 偏移量，默认为0
     * @return 执行记录列表
     */
    fun getTaskExecutions(taskId: String, limit: Int = 10, offset: Int = 0): List<TaskExecution>

    /**
     * 取消任务执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    fun cancelExecution(executionId: String): Boolean

    /**
     * 启动调度器
     *
     * @return 是否成功
     */
    fun startScheduler(): Boolean

    /**
     * 关闭调度器
     *
     * @return 是否成功
     */
    fun shutdownScheduler(): Boolean

    /**
     * 暂停调度器
     *
     * @return 是否成功
     */
    fun pauseScheduler(): Boolean

    /**
     * 恢复调度器
     *
     * @return 是否成功
     */
    fun resumeScheduler(): Boolean

    /**
     * 获取调度器状态
     *
     * @return 调度器状态
     */
    fun getSchedulerStatus(): SchedulerStatus

    /**
     * 获取调度器统计信息
     *
     * @return 调度器统计信息
     */
    fun getSchedulerStats(): SchedulerStats
}
