package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.AsyncTask
import ai.magicdb.data.service.api.model.TaskStatus

/**
 * 异步任务执行器接口
 *
 * @author magicdb
 */
interface AsyncTaskExecutor {
    /**
     * 提交任务
     *
     * @param task 任务
     * @return 任务ID
     */
    fun submitTask(task: AsyncTask): String
    
    /**
     * 取消任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    fun cancelTask(taskId: String): Boolean
    
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
     * 获取任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态
     */
    fun getTaskStatus(taskId: String): TaskStatus
    
    /**
     * 获取任务结果
     *
     * @param taskId 任务ID
     * @return 任务结果
     */
    fun getTaskResult(taskId: String): Any?
    
    /**
     * 获取所有任务
     *
     * @return 任务列表
     */
    fun getAllTasks(): List<AsyncTask>
    
    /**
     * 清理已完成任务
     *
     * @param maxAge 最大保留时间（毫秒）
     * @return 清理的任务数量
     */
    fun cleanupCompletedTasks(maxAge: Long): Int
}
