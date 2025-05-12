package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.AsyncTaskExecutor
import ai.magicdb.data.service.api.model.AsyncTask
import ai.magicdb.data.service.api.model.TaskStatus
import ai.magicdb.data.service.api.model.TaskType
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.Date
import java.util.UUID

/**
 * 异步任务控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/async")
class AsyncTaskController(private val asyncTaskExecutor: AsyncTaskExecutor) {
    private val logger = LoggerFactory.getLogger(AsyncTaskController::class.java)
    
    /**
     * 提交脚本执行任务
     */
    @PostMapping("/script")
    fun submitScriptTask(@RequestBody request: ScriptTaskRequest): DataResult<String> {
        try {
            // 创建任务
            val task = AsyncTask(
                id = UUID.randomUUID().toString(),
                name = request.name,
                type = TaskType.SCRIPT_EXECUTION,
                parameters = mapOf(
                    "script" to request.script,
                    "language" to request.language,
                    "context" to (request.context ?: emptyMap())
                ),
                priority = request.priority ?: 0,
                timeout = request.timeout ?: 0,
                createTime = Date()
            )
            
            // 提交任务
            val taskId = asyncTaskExecutor.submitTask(task)
            
            return DataResult.of(taskId)
        } catch (e: Exception) {
            logger.error("提交脚本执行任务失败: {}", e.message, e)
            return DataResult.error("500", "提交脚本执行任务失败: ${e.message}")
        }
    }
    
    /**
     * 提交数据导入任务
     */
    @PostMapping("/import")
    fun submitImportTask(@RequestBody request: ImportTaskRequest): DataResult<String> {
        try {
            // 创建任务
            val task = AsyncTask(
                id = UUID.randomUUID().toString(),
                name = request.name,
                type = TaskType.DATA_IMPORT,
                parameters = mapOf(
                    "dataSourceId" to request.dataSourceId,
                    "tableName" to request.tableName,
                    "data" to request.data,
                    "options" to (request.options ?: emptyMap())
                ),
                priority = request.priority ?: 0,
                timeout = request.timeout ?: 0,
                createTime = Date()
            )
            
            // 提交任务
            val taskId = asyncTaskExecutor.submitTask(task)
            
            return DataResult.of(taskId)
        } catch (e: Exception) {
            logger.error("提交数据导入任务失败: {}", e.message, e)
            return DataResult.error("500", "提交数据导入任务失败: ${e.message}")
        }
    }
    
    /**
     * 提交数据导出任务
     */
    @PostMapping("/export")
    fun submitExportTask(@RequestBody request: ExportTaskRequest): DataResult<String> {
        try {
            // 创建任务
            val task = AsyncTask(
                id = UUID.randomUUID().toString(),
                name = request.name,
                type = TaskType.DATA_EXPORT,
                parameters = mapOf(
                    "dataSourceId" to request.dataSourceId,
                    "sql" to request.sql,
                    "format" to request.format,
                    "options" to (request.options ?: emptyMap())
                ),
                priority = request.priority ?: 0,
                timeout = request.timeout ?: 0,
                createTime = Date()
            )
            
            // 提交任务
            val taskId = asyncTaskExecutor.submitTask(task)
            
            return DataResult.of(taskId)
        } catch (e: Exception) {
            logger.error("提交数据导出任务失败: {}", e.message, e)
            return DataResult.error("500", "提交数据导出任务失败: ${e.message}")
        }
    }
    
    /**
     * 取消任务
     */
    @PostMapping("/cancel/{taskId}")
    fun cancelTask(@PathVariable taskId: String): DataResult<Boolean> {
        try {
            val result = asyncTaskExecutor.cancelTask(taskId)
            return DataResult.of(result)
        } catch (e: Exception) {
            logger.error("取消任务失败: {}", e.message, e)
            return DataResult.error("500", "取消任务失败: ${e.message}")
        }
    }
    
    /**
     * 获取任务状态
     */
    @GetMapping("/status/{taskId}")
    fun getTaskStatus(@PathVariable taskId: String): DataResult<TaskStatus> {
        try {
            val status = asyncTaskExecutor.getTaskStatus(taskId)
            return DataResult.of(status)
        } catch (e: Exception) {
            logger.error("获取任务状态失败: {}", e.message, e)
            return DataResult.error("500", "获取任务状态失败: ${e.message}")
        }
    }
    
    /**
     * 获取任务结果
     */
    @GetMapping("/result/{taskId}")
    fun getTaskResult(@PathVariable taskId: String): DataResult<Any?> {
        try {
            val result = asyncTaskExecutor.getTaskResult(taskId)
            return DataResult.of(result)
        } catch (e: Exception) {
            logger.error("获取任务结果失败: {}", e.message, e)
            return DataResult.error("500", "获取任务结果失败: ${e.message}")
        }
    }
    
    /**
     * 获取任务详情
     */
    @GetMapping("/task/{taskId}")
    fun getTask(@PathVariable taskId: String): DataResult<AsyncTask> {
        try {
            val tasks = asyncTaskExecutor.getAllTasks()
            val task = tasks.find { it.id == taskId }
                ?: throw IllegalArgumentException("任务不存在: $taskId")
            
            return DataResult.of(task)
        } catch (e: Exception) {
            logger.error("获取任务详情失败: {}", e.message, e)
            return DataResult.error("500", "获取任务详情失败: ${e.message}")
        }
    }
    
    /**
     * 获取所有任务
     */
    @GetMapping("/tasks")
    fun getAllTasks(): ListResult<AsyncTask> {
        try {
            val tasks = asyncTaskExecutor.getAllTasks()
            return ListResult.of(tasks)
        } catch (e: Exception) {
            logger.error("获取所有任务失败: {}", e.message, e)
            return ListResult.error("500", "获取所有任务失败: ${e.message}")
        }
    }
    
    /**
     * 清理已完成任务
     */
    @PostMapping("/cleanup")
    fun cleanupCompletedTasks(@RequestBody request: CleanupRequest): DataResult<Int> {
        try {
            val count = asyncTaskExecutor.cleanupCompletedTasks(request.maxAge)
            return DataResult.of(count)
        } catch (e: Exception) {
            logger.error("清理已完成任务失败: {}", e.message, e)
            return DataResult.error("500", "清理已完成任务失败: ${e.message}")
        }
    }
    
    /**
     * 脚本任务请求
     */
    data class ScriptTaskRequest(
        val name: String,
        val script: String,
        val language: String,
        val context: Map<String, Any?>? = null,
        val priority: Int? = null,
        val timeout: Long? = null
    )
    
    /**
     * 数据导入任务请求
     */
    data class ImportTaskRequest(
        val name: String,
        val dataSourceId: Long,
        val tableName: String,
        val data: List<Map<String, Any?>>,
        val options: Map<String, Any?>? = null,
        val priority: Int? = null,
        val timeout: Long? = null
    )
    
    /**
     * 数据导出任务请求
     */
    data class ExportTaskRequest(
        val name: String,
        val dataSourceId: Long,
        val sql: String,
        val format: String,
        val options: Map<String, Any?>? = null,
        val priority: Int? = null,
        val timeout: Long? = null
    )
    
    /**
     * 清理请求
     */
    data class CleanupRequest(
        val maxAge: Long
    )
}
