package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.ScheduledTask

/**
 * 调度器服务接口
 *
 * @author magicdb
 */
interface SchedulerService {
    /**
     * 调度任务
     *
     * @param task 任务信息
     * @return 是否成功
     */
    fun scheduleTask(task: ScheduledTask): Boolean
    
    /**
     * 取消任务调度
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    fun unscheduleTask(taskId: String): Boolean
    
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
     * 执行任务
     *
     * @param taskId 任务ID
     * @param parameters 参数，可选
     * @return 执行ID
     */
    fun executeTask(taskId: String, parameters: Map<String, Any?>? = null): String
    
    /**
     * 启用任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    fun enableTask(taskId: String): Boolean
    
    /**
     * 禁用任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    fun disableTask(taskId: String): Boolean
    
    /**
     * 获取调度器信息
     *
     * @return 调度器信息
     */
    fun getSchedulerInfo(): Map<String, Any>
    
    /**
     * 获取正在执行的任务
     *
     * @return 任务列表
     */
    fun getRunningTasks(): List<ScheduledTask>
    
    /**
     * 获取所有任务
     *
     * @return 任务列表
     */
    fun getAllJobs(): List<Map<String, Any>>
}
