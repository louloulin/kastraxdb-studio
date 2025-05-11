package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.SchedulerService
import ai.magicdb.dataservice.api.model.ScheduledTask
import ai.magicdb.dataservice.api.model.SchedulerStatus
import ai.magicdb.dataservice.api.model.SchedulerStats
import ai.magicdb.dataservice.api.model.TaskExecution
import ai.magicdb.dataservice.api.model.TaskStatus
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import org.springframework.web.bind.annotation.*

/**
 * 调度器控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/scheduler")
class SchedulerController(private val schedulerService: SchedulerService) {
    
    /**
     * 创建任务
     *
     * @param task 任务信息
     * @return 任务ID
     */
    @PostMapping("/tasks")
    fun createTask(@RequestBody task: ScheduledTask): DataResult<String> {
        val taskId = schedulerService.createTask(task)
        return DataResult.of(taskId)
    }
    
    /**
     * 更新任务
     *
     * @param taskId 任务ID
     * @param task 任务信息
     * @return 是否成功
     */
    @PutMapping("/tasks/{taskId}")
    fun updateTask(@PathVariable taskId: String, @RequestBody task: ScheduledTask): ActionResult {
        task.id = taskId
        val success = schedulerService.updateTask(task)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "更新任务失败", "")
        }
    }
    
    /**
     * 删除任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    @DeleteMapping("/tasks/{taskId}")
    fun deleteTask(@PathVariable taskId: String): ActionResult {
        val success = schedulerService.deleteTask(taskId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "删除任务失败", "")
        }
    }
    
    /**
     * 获取任务
     *
     * @param taskId 任务ID
     * @return 任务信息
     */
    @GetMapping("/tasks/{taskId}")
    fun getTask(@PathVariable taskId: String): DataResult<ScheduledTask> {
        val task = schedulerService.getTask(taskId)
        return if (task != null) {
            DataResult.of(task)
        } else {
            DataResult.empty()
        }
    }
    
    /**
     * 获取所有任务
     *
     * @param status 任务状态
     * @param group 任务分组
     * @return 任务列表
     */
    @GetMapping("/tasks")
    fun getAllTasks(
        @RequestParam(required = false) status: TaskStatus?,
        @RequestParam(required = false) group: String?
    ): ListResult<ScheduledTask> {
        val tasks = schedulerService.getAllTasks(status, group)
        return ListResult.of(tasks)
    }
    
    /**
     * 获取服务的所有任务
     *
     * @param serviceId 服务ID
     * @return 任务列表
     */
    @GetMapping("/tasks/service/{serviceId}")
    fun getTasksByService(@PathVariable serviceId: String): ListResult<ScheduledTask> {
        val tasks = schedulerService.getTasksByService(serviceId)
        return ListResult.of(tasks)
    }
    
    /**
     * 启动任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    @PostMapping("/tasks/{taskId}/start")
    fun startTask(@PathVariable taskId: String): ActionResult {
        val success = schedulerService.startTask(taskId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "启动任务失败", "")
        }
    }
    
    /**
     * 暂停任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    @PostMapping("/tasks/{taskId}/pause")
    fun pauseTask(@PathVariable taskId: String): ActionResult {
        val success = schedulerService.pauseTask(taskId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "暂停任务失败", "")
        }
    }
    
    /**
     * 恢复任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    @PostMapping("/tasks/{taskId}/resume")
    fun resumeTask(@PathVariable taskId: String): ActionResult {
        val success = schedulerService.resumeTask(taskId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "恢复任务失败", "")
        }
    }
    
    /**
     * 立即执行任务
     *
     * @param taskId 任务ID
     * @param parameters 执行参数
     * @return 执行ID
     */
    @PostMapping("/tasks/{taskId}/execute")
    fun executeTask(
        @PathVariable taskId: String,
        @RequestBody(required = false) parameters: Map<String, Any?>?
    ): DataResult<String> {
        val executionId = schedulerService.executeTask(taskId, parameters)
        return DataResult.of(executionId)
    }
    
    /**
     * 获取任务执行记录
     *
     * @param executionId 执行ID
     * @return 执行记录
     */
    @GetMapping("/executions/{executionId}")
    fun getTaskExecution(@PathVariable executionId: String): DataResult<TaskExecution> {
        val execution = schedulerService.getTaskExecution(executionId)
        return if (execution != null) {
            DataResult.of(execution)
        } else {
            DataResult.empty()
        }
    }
    
    /**
     * 获取任务的所有执行记录
     *
     * @param taskId 任务ID
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 执行记录列表
     */
    @GetMapping("/tasks/{taskId}/executions")
    fun getTaskExecutions(
        @PathVariable taskId: String,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ListResult<TaskExecution> {
        val executions = schedulerService.getTaskExecutions(taskId, limit, offset)
        return ListResult.of(executions)
    }
    
    /**
     * 取消任务执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    @PostMapping("/executions/{executionId}/cancel")
    fun cancelExecution(@PathVariable executionId: String): ActionResult {
        val success = schedulerService.cancelExecution(executionId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "取消执行失败", "")
        }
    }
    
    /**
     * 启动调度器
     *
     * @return 是否成功
     */
    @PostMapping("/start")
    fun startScheduler(): ActionResult {
        val success = schedulerService.startScheduler()
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "启动调度器失败", "")
        }
    }
    
    /**
     * 关闭调度器
     *
     * @return 是否成功
     */
    @PostMapping("/shutdown")
    fun shutdownScheduler(): ActionResult {
        val success = schedulerService.shutdownScheduler()
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "关闭调度器失败", "")
        }
    }
    
    /**
     * 暂停调度器
     *
     * @return 是否成功
     */
    @PostMapping("/pause")
    fun pauseScheduler(): ActionResult {
        val success = schedulerService.pauseScheduler()
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "暂停调度器失败", "")
        }
    }
    
    /**
     * 恢复调度器
     *
     * @return 是否成功
     */
    @PostMapping("/resume")
    fun resumeScheduler(): ActionResult {
        val success = schedulerService.resumeScheduler()
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "恢复调度器失败", "")
        }
    }
    
    /**
     * 获取调度器状态
     *
     * @return 调度器状态
     */
    @GetMapping("/status")
    fun getSchedulerStatus(): DataResult<SchedulerStatus> {
        val status = schedulerService.getSchedulerStatus()
        return DataResult.of(status)
    }
    
    /**
     * 获取调度器统计信息
     *
     * @return 调度器统计信息
     */
    @GetMapping("/stats")
    fun getSchedulerStats(): DataResult<SchedulerStats> {
        val stats = schedulerService.getSchedulerStats()
        return DataResult.of(stats)
    }
}
