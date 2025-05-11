package ai.magicdb.dataservice.core.async

import ai.magicdb.dataservice.api.AsyncTaskExecutor
import ai.magicdb.dataservice.api.model.AsyncTask
import ai.magicdb.dataservice.api.model.TaskStatus
import ai.magicdb.dataservice.api.model.TaskType
import ai.magicdb.dataservice.core.script.ScriptExecutor
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 默认异步任务执行器实现
 *
 * @author magicdb
 */
@Service
class DefaultAsyncTaskExecutor(
    private val scriptExecutor: ScriptExecutor
) : AsyncTaskExecutor {
    private val logger = LoggerFactory.getLogger(DefaultAsyncTaskExecutor::class.java)
    
    // 任务存储
    private val tasks = ConcurrentHashMap<String, AsyncTask>()
    
    // 任务执行器
    private val executor = Executors.newFixedThreadPool(10)
    
    // 任务调度器
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(2)
    
    // 任务Future
    private val futures = ConcurrentHashMap<String, Future<*>>()
    
    /**
     * 提交任务
     */
    override fun submitTask(task: AsyncTask): String {
        // 生成任务ID
        val taskId = task.id.ifEmpty { UUID.randomUUID().toString() }
        
        // 创建任务副本
        val newTask = task.copy(
            id = taskId,
            status = TaskStatus.PENDING,
            createTime = Date()
        )
        
        // 存储任务
        tasks[taskId] = newTask
        
        // 提交任务
        val future = executor.submit {
            try {
                // 更新任务状态
                updateTaskStatus(taskId, TaskStatus.RUNNING)
                
                // 设置开始时间
                updateTaskStartTime(taskId, Date())
                
                // 执行任务
                val result = executeTask(newTask)
                
                // 更新任务结果
                updateTaskResult(taskId, result)
                
                // 更新任务状态
                updateTaskStatus(taskId, TaskStatus.COMPLETED)
                
                // 设置结束时间
                updateTaskEndTime(taskId, Date())
                
                // 记录日志
                addTaskLog(taskId, "任务执行完成")
            } catch (e: Exception) {
                // 记录错误
                logger.error("任务执行出错: {}", e.message, e)
                
                // 更新任务状态
                updateTaskStatus(taskId, TaskStatus.FAILED)
                
                // 设置结束时间
                updateTaskEndTime(taskId, Date())
                
                // 设置错误信息
                updateTaskError(taskId, e.message ?: "未知错误")
                
                // 记录日志
                addTaskLog(taskId, "任务执行出错: ${e.message}")
            }
        }
        
        // 存储Future
        futures[taskId] = future
        
        // 设置超时处理
        if (newTask.timeout > 0) {
            scheduler.schedule({
                if (getTaskStatus(taskId) == TaskStatus.RUNNING) {
                    // 取消任务
                    cancelTask(taskId)
                    
                    // 更新任务状态
                    updateTaskStatus(taskId, TaskStatus.TIMEOUT)
                    
                    // 设置结束时间
                    updateTaskEndTime(taskId, Date())
                    
                    // 设置错误信息
                    updateTaskError(taskId, "任务执行超时")
                    
                    // 记录日志
                    addTaskLog(taskId, "任务执行超时")
                }
            }, newTask.timeout, TimeUnit.MILLISECONDS)
        }
        
        return taskId
    }
    
    /**
     * 取消任务
     */
    override fun cancelTask(taskId: String): Boolean {
        val task = tasks[taskId] ?: return false
        
        // 检查任务状态
        if (task.status != TaskStatus.RUNNING && task.status != TaskStatus.PENDING) {
            return false
        }
        
        // 取消Future
        val future = futures[taskId]
        if (future != null && !future.isDone && !future.isCancelled) {
            future.cancel(true)
        }
        
        // 更新任务状态
        updateTaskStatus(taskId, TaskStatus.CANCELLED)
        
        // 设置结束时间
        updateTaskEndTime(taskId, Date())
        
        // 记录日志
        addTaskLog(taskId, "任务已取消")
        
        return true
    }
    
    /**
     * 暂停任务
     */
    override fun pauseTask(taskId: String): Boolean {
        // 当前实现不支持暂停任务
        return false
    }
    
    /**
     * 恢复任务
     */
    override fun resumeTask(taskId: String): Boolean {
        // 当前实现不支持恢复任务
        return false
    }
    
    /**
     * 获取任务状态
     */
    override fun getTaskStatus(taskId: String): TaskStatus {
        return tasks[taskId]?.status ?: throw IllegalArgumentException("任务不存在: $taskId")
    }
    
    /**
     * 获取任务结果
     */
    override fun getTaskResult(taskId: String): Any? {
        val task = tasks[taskId] ?: throw IllegalArgumentException("任务不存在: $taskId")
        
        // 检查任务状态
        if (task.status != TaskStatus.COMPLETED) {
            throw IllegalStateException("任务未完成，无法获取结果: $taskId")
        }
        
        return task.result
    }
    
    /**
     * 获取所有任务
     */
    override fun getAllTasks(): List<AsyncTask> {
        return tasks.values.toList()
    }
    
    /**
     * 清理已完成任务
     */
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    override fun cleanupCompletedTasks(maxAge: Long): Int {
        val now = System.currentTimeMillis()
        var count = 0
        
        // 查找已完成的任务
        val completedTasks = tasks.entries.filter { (_, task) ->
            val isCompleted = task.status == TaskStatus.COMPLETED || 
                             task.status == TaskStatus.FAILED || 
                             task.status == TaskStatus.CANCELLED ||
                             task.status == TaskStatus.TIMEOUT
            
            val isExpired = task.endTime != null && 
                           (now - task.endTime!!.time) > maxAge
            
            isCompleted && isExpired
        }
        
        // 删除任务
        completedTasks.forEach { (taskId, _) ->
            tasks.remove(taskId)
            futures.remove(taskId)
            count++
        }
        
        return count
    }
    
    /**
     * 执行任务
     */
    private fun executeTask(task: AsyncTask): Any? {
        return when (task.type) {
            TaskType.SCRIPT_EXECUTION -> executeScriptTask(task)
            TaskType.DATA_IMPORT -> executeDataImportTask(task)
            TaskType.DATA_EXPORT -> executeDataExportTask(task)
            TaskType.DATA_SYNC -> executeDataSyncTask(task)
            TaskType.DATA_PROCESSING -> executeDataProcessingTask(task)
            TaskType.OTHER -> executeOtherTask(task)
        }
    }
    
    /**
     * 执行脚本任务
     */
    private fun executeScriptTask(task: AsyncTask): Any? {
        // 获取脚本参数
        val script = task.parameters["script"] as? String
            ?: throw IllegalArgumentException("脚本参数缺失")
        
        val language = task.parameters["language"] as? String
            ?: throw IllegalArgumentException("语言参数缺失")
        
        val context = task.parameters["context"] as? Map<String, Any?>
            ?: emptyMap()
        
        // 记录日志
        addTaskLog(task.id, "开始执行脚本: $language")
        
        // 执行脚本
        val result = scriptExecutor.execute(script, language, context)
        
        // 记录日志
        addTaskLog(task.id, "脚本执行完成")
        
        return result
    }
    
    /**
     * 执行数据导入任务
     */
    private fun executeDataImportTask(task: AsyncTask): Any? {
        // TODO: 实现数据导入任务
        addTaskLog(task.id, "数据导入功能尚未实现")
        return null
    }
    
    /**
     * 执行数据导出任务
     */
    private fun executeDataExportTask(task: AsyncTask): Any? {
        // TODO: 实现数据导出任务
        addTaskLog(task.id, "数据导出功能尚未实现")
        return null
    }
    
    /**
     * 执行数据同步任务
     */
    private fun executeDataSyncTask(task: AsyncTask): Any? {
        // TODO: 实现数据同步任务
        addTaskLog(task.id, "数据同步功能尚未实现")
        return null
    }
    
    /**
     * 执行数据处理任务
     */
    private fun executeDataProcessingTask(task: AsyncTask): Any? {
        // TODO: 实现数据处理任务
        addTaskLog(task.id, "数据处理功能尚未实现")
        return null
    }
    
    /**
     * 执行其他任务
     */
    private fun executeOtherTask(task: AsyncTask): Any? {
        // TODO: 实现其他任务
        addTaskLog(task.id, "其他任务类型尚未实现")
        return null
    }
    
    /**
     * 更新任务状态
     */
    private fun updateTaskStatus(taskId: String, status: TaskStatus) {
        val task = tasks[taskId] ?: return
        task.status = status
    }
    
    /**
     * 更新任务开始时间
     */
    private fun updateTaskStartTime(taskId: String, startTime: Date) {
        val task = tasks[taskId] ?: return
        task.startTime = startTime
    }
    
    /**
     * 更新任务结束时间
     */
    private fun updateTaskEndTime(taskId: String, endTime: Date) {
        val task = tasks[taskId] ?: return
        task.endTime = endTime
    }
    
    /**
     * 更新任务结果
     */
    private fun updateTaskResult(taskId: String, result: Any?) {
        val task = tasks[taskId] ?: return
        task.result = result
    }
    
    /**
     * 更新任务错误信息
     */
    private fun updateTaskError(taskId: String, error: String) {
        val task = tasks[taskId] ?: return
        task.error = error
    }
    
    /**
     * 更新任务进度
     */
    private fun updateTaskProgress(taskId: String, progress: Int) {
        val task = tasks[taskId] ?: return
        task.progress = progress
    }
    
    /**
     * 添加任务日志
     */
    private fun addTaskLog(taskId: String, log: String) {
        val task = tasks[taskId] ?: return
        task.logs.add("[${Date()}] $log")
    }
}
