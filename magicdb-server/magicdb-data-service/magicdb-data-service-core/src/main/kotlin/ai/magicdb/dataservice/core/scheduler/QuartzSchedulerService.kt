package ai.magicdb.dataservice.core.scheduler

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.SchedulerService
import ai.magicdb.dataservice.api.model.ExecutionStatus
import ai.magicdb.dataservice.api.model.ScheduledTask
import ai.magicdb.dataservice.api.model.SchedulerStatus
import ai.magicdb.dataservice.api.model.SchedulerStats
import ai.magicdb.dataservice.api.model.TaskExecution
import ai.magicdb.dataservice.api.model.TaskStatus
import ai.magicdb.dataservice.api.model.TriggerType
import ai.magicdb.dataservice.core.repository.TaskRepository
import org.quartz.CronScheduleBuilder
import org.quartz.CronTrigger
import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.quartz.JobExecutionContext
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.SchedulerMetaData
import org.quartz.SimpleScheduleBuilder
import org.quartz.SimpleTrigger
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.quartz.TriggerKey
import org.quartz.impl.matchers.GroupMatcher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Quartz调度器服务实现
 *
 * @author magicdb
 */
@Service
class QuartzSchedulerService(
    private val scheduler: Scheduler,
    private val taskRepository: TaskRepository,
    private val serviceRepository: DataServiceRepository,
    private val serviceExecutor: DataServiceExecutor
) : SchedulerService {

    private val logger = LoggerFactory.getLogger(QuartzSchedulerService::class.java)

    // 执行中的任务映射表
    private val runningExecutions = ConcurrentHashMap<String, TaskExecution>()

    // 调度器启动时间
    private var startTime: Long = 0

    @PostConstruct
    fun init() {
        try {
            // 启动调度器
            startScheduler()

            // 加载所有启用的任务
            loadEnabledTasks()

            logger.info("调度器初始化完成")
        } catch (e: Exception) {
            logger.error("调度器初始化失败", e)
        }
    }

    @PreDestroy
    fun destroy() {
        try {
            // 关闭调度器
            shutdownScheduler()

            logger.info("调度器已关闭")
        } catch (e: Exception) {
            logger.error("调度器关闭失败", e)
        }
    }

    override fun createTask(task: ScheduledTask): String {
        try {
            // 保存任务
            val taskId = taskRepository.saveTask(task)

            // 如果任务已启用，则调度任务
            if (task.enabled) {
                scheduleTask(task)
            }

            return taskId
        } catch (e: Exception) {
            logger.error("创建任务失败: {}", task.name, e)
            throw e
        }
    }

    override fun updateTask(task: ScheduledTask): Boolean {
        try {
            // 更新任务
            val result = taskRepository.updateTask(task)

            if (result) {
                // 如果任务已调度，则取消调度
                unscheduleTask(task.id)

                // 如果任务已启用，则重新调度任务
                if (task.enabled) {
                    scheduleTask(task)
                }
            }

            return result
        } catch (e: Exception) {
            logger.error("更新任务失败: {}", task.id, e)
            return false
        }
    }

    override fun deleteTask(taskId: String): Boolean {
        try {
            // 取消调度
            unscheduleTask(taskId)

            // 删除任务
            return taskRepository.deleteTask(taskId)
        } catch (e: Exception) {
            logger.error("删除任务失败: {}", taskId, e)
            return false
        }
    }

    override fun getTask(taskId: String): ScheduledTask? {
        return taskRepository.getTask(taskId)
    }

    override fun getAllTasks(status: TaskStatus?, group: String?): List<ScheduledTask> {
        return taskRepository.getAllTasks(status, group)
    }

    override fun getTasksByService(serviceId: String): List<ScheduledTask> {
        return taskRepository.getTasksByService(serviceId)
    }

    override fun startTask(taskId: String): Boolean {
        try {
            // 获取任务
            val task = taskRepository.getTask(taskId) ?: return false

            // 更新任务状态
            taskRepository.updateTaskStatus(taskId, TaskStatus.WAITING)

            // 更新任务启用状态
            taskRepository.updateTaskEnabled(taskId, true)

            // 调度任务
            scheduleTask(task)

            return true
        } catch (e: Exception) {
            logger.error("启动任务失败: {}", taskId, e)
            return false
        }
    }

    override fun pauseTask(taskId: String): Boolean {
        try {
            // 暂停任务
            scheduler.pauseJob(JobKey.jobKey(taskId))

            // 更新任务状态
            taskRepository.updateTaskStatus(taskId, TaskStatus.PAUSED)

            return true
        } catch (e: Exception) {
            logger.error("暂停任务失败: {}", taskId, e)
            return false
        }
    }

    override fun resumeTask(taskId: String): Boolean {
        try {
            // 恢复任务
            scheduler.resumeJob(JobKey.jobKey(taskId))

            // 更新任务状态
            taskRepository.updateTaskStatus(taskId, TaskStatus.WAITING)

            return true
        } catch (e: Exception) {
            logger.error("恢复任务失败: {}", taskId, e)
            return false
        }
    }

    override fun executeTask(taskId: String, parameters: Map<String, Any?>?): String {
        try {
            // 获取任务
            val task = taskRepository.getTask(taskId) ?: throw IllegalArgumentException("任务不存在: $taskId")

            // 创建执行记录
            val execution = TaskExecution(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                taskName = task.name,
                serviceId = task.serviceId,
                serviceName = task.serviceName,
                parameters = parameters ?: task.parameters,
                startTime = System.currentTimeMillis(),
                status = ExecutionStatus.PENDING,
                triggerType = TriggerType.MANUAL,
                triggerId = "user"
            )

            // 保存执行记录
            taskRepository.saveTaskExecution(execution)

            // 创建一次性触发器
            val jobDataMap = JobDataMap()
            jobDataMap["executionId"] = execution.id

            val jobDetail = JobBuilder.newJob(TaskExecutorJob::class.java)
                .withIdentity("manual-${execution.id}")
                .usingJobData(jobDataMap)
                .build()

            val trigger = TriggerBuilder.newTrigger()
                .withIdentity("manual-trigger-${execution.id}")
                .startNow()
                .build()

            // 调度任务
            scheduler.scheduleJob(jobDetail, trigger)

            return execution.id
        } catch (e: Exception) {
            logger.error("执行任务失败: {}", taskId, e)
            throw e
        }
    }

    override fun getTaskExecution(executionId: String): TaskExecution? {
        // 先从内存中查找
        val runningExecution = runningExecutions[executionId]
        if (runningExecution != null) {
            return runningExecution
        }

        // 从数据库中查找
        return taskRepository.getTaskExecution(executionId)
    }

    override fun getTaskExecutions(taskId: String, limit: Int, offset: Int): List<TaskExecution> {
        return taskRepository.getTaskExecutions(taskId, limit, offset)
    }

    override fun cancelExecution(executionId: String): Boolean {
        try {
            // 获取执行记录
            val execution = getTaskExecution(executionId) ?: return false

            // 如果执行已完成，则无法取消
            if (execution.status == ExecutionStatus.SUCCESS || execution.status == ExecutionStatus.FAILED) {
                return false
            }

            // 更新执行记录状态
            taskRepository.updateTaskExecutionStatus(executionId, ExecutionStatus.CANCELED)

            // 从内存中移除
            runningExecutions.remove(executionId)

            return true
        } catch (e: Exception) {
            logger.error("取消执行失败: {}", executionId, e)
            return false
        }
    }

    override fun startScheduler(): Boolean {
        try {
            if (!scheduler.isStarted) {
                scheduler.start()
                startTime = System.currentTimeMillis()
                logger.info("调度器已启动")
            }
            return true
        } catch (e: Exception) {
            logger.error("启动调度器失败", e)
            return false
        }
    }

    override fun shutdownScheduler(): Boolean {
        try {
            if (!scheduler.isShutdown) {
                scheduler.shutdown(true)
                logger.info("调度器已关闭")
            }
            return true
        } catch (e: Exception) {
            logger.error("关闭调度器失败", e)
            return false
        }
    }

    override fun pauseScheduler(): Boolean {
        try {
            if (!scheduler.isInStandbyMode) {
                scheduler.standby()
                logger.info("调度器已暂停")
            }
            return true
        } catch (e: Exception) {
            logger.error("暂停调度器失败", e)
            return false
        }
    }

    override fun resumeScheduler(): Boolean {
        try {
            if (scheduler.isInStandbyMode) {
                scheduler.start()
                logger.info("调度器已恢复")
            }
            return true
        } catch (e: Exception) {
            logger.error("恢复调度器失败", e)
            return false
        }
    }

    override fun getSchedulerStatus(): SchedulerStatus {
        try {
            return when {
                scheduler.isShutdown -> SchedulerStatus.SHUTDOWN
                scheduler.isInStandbyMode -> SchedulerStatus.STANDBY
                scheduler.isStarted -> SchedulerStatus.RUNNING
                else -> SchedulerStatus.ERROR
            }
        } catch (e: Exception) {
            logger.error("获取调度器状态失败", e)
            return SchedulerStatus.ERROR
        }
    }

    override fun getSchedulerStats(): SchedulerStats {
        try {
            // 获取调度器元数据
            val metaData = scheduler.metaData

            // 获取任务统计信息
            val tasks = taskRepository.getAllTasks()
            val runningTasks = tasks.filter { it.status == TaskStatus.RUNNING }
            val waitingTasks = tasks.filter { it.status == TaskStatus.WAITING }
            val pausedTasks = tasks.filter { it.status == TaskStatus.PAUSED }
            val failedTasks = tasks.filter { it.status == TaskStatus.FAILED }

            // 计算执行统计信息
            val totalExecutionCount = tasks.sumOf { it.executeCount }
            val successExecutionCount = tasks.sumOf { it.successCount }
            val failedExecutionCount = tasks.sumOf { it.failCount }

            // 计算平均执行时间
            val totalDuration = tasks.sumOf { it.lastExecuteDuration }
            val averageExecutionTime = if (totalExecutionCount > 0) {
                totalDuration / totalExecutionCount
            } else {
                0
            }

            // 计算最长和最短执行时间
            val maxExecutionTime = tasks.maxOfOrNull { it.lastExecuteDuration } ?: 0
            val minExecutionTime = tasks.filter { it.lastExecuteDuration > 0 }.minOfOrNull { it.lastExecuteDuration } ?: 0

            // 创建统计信息
            return SchedulerStats(
                totalTaskCount = tasks.size,
                runningTaskCount = runningTasks.size,
                waitingTaskCount = waitingTasks.size,
                pausedTaskCount = pausedTasks.size,
                failedTaskCount = failedTasks.size,
                totalExecutionCount = totalExecutionCount,
                successExecutionCount = successExecutionCount,
                failedExecutionCount = failedExecutionCount,
                averageExecutionTime = averageExecutionTime,
                maxExecutionTime = maxExecutionTime,
                minExecutionTime = minExecutionTime,
                startTime = startTime,
                uptime = System.currentTimeMillis() - startTime,
                threadPoolSize = metaData.threadPoolSize,
                activeThreadCount = 0, // 不支持获取活跃线程数
                queuedTaskCount = 0 // 不支持获取队列中的任务数
            )
        } catch (e: Exception) {
            logger.error("获取调度器统计信息失败", e)
            return SchedulerStats()
        }
    }

    /**
     * 加载所有启用的任务
     */
    private fun loadEnabledTasks() {
        try {
            // 获取所有启用的任务
            val tasks = taskRepository.getAllTasks().filter { it.enabled }

            // 调度所有任务
            for (task in tasks) {
                scheduleTask(task)
            }

            logger.info("已加载 {} 个任务", tasks.size)
        } catch (e: Exception) {
            logger.error("加载任务失败", e)
        }
    }

    /**
     * 调度任务
     */
    private fun scheduleTask(task: ScheduledTask) {
        try {
            // 创建任务详情
            val jobDataMap = JobDataMap()
            jobDataMap["taskId"] = task.id

            val jobDetail = JobBuilder.newJob(TaskExecutorJob::class.java)
                .withIdentity(task.id)
                .usingJobData(jobDataMap)
                .build()

            // 创建触发器
            val trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger-${task.id}")
                .withSchedule(CronScheduleBuilder.cronSchedule(task.cronExpression))
                .build()

            // 调度任务
            scheduler.scheduleJob(jobDetail, trigger)

            // 更新任务状态
            taskRepository.updateTaskStatus(task.id, TaskStatus.WAITING)

            // 计算下次执行时间
            val nextFireTime = trigger.nextFireTime?.time ?: 0
            if (nextFireTime > 0) {
                task.nextExecuteTime = nextFireTime
                taskRepository.updateTask(task)
            }

            logger.info("任务已调度: {}", task.name)
        } catch (e: Exception) {
            logger.error("调度任务失败: {}", task.name, e)

            // 更新任务状态
            taskRepository.updateTaskStatus(task.id, TaskStatus.FAILED)
        }
    }

    /**
     * 取消任务调度
     */
    private fun unscheduleTask(taskId: String) {
        try {
            // 删除任务
            scheduler.deleteJob(JobKey.jobKey(taskId))

            logger.info("任务调度已取消: {}", taskId)
        } catch (e: Exception) {
            logger.error("取消任务调度失败: {}", taskId, e)
        }
    }

    /**
     * 任务执行器Job
     */
    class TaskExecutorJob : Job {

        @Autowired
        private lateinit var taskRepository: TaskRepository

        @Autowired
        private lateinit var serviceRepository: DataServiceRepository

        @Autowired
        private lateinit var serviceExecutor: DataServiceExecutor

        private val logger = LoggerFactory.getLogger(TaskExecutorJob::class.java)

        override fun execute(context: JobExecutionContext) {
            // 获取任务ID
            val jobDataMap = context.jobDetail.jobDataMap
            val taskId = jobDataMap.getString("taskId")
            val executionId = jobDataMap.getString("executionId")

            // 如果是手动执行
            if (executionId != null) {
                executeTaskWithExecution(executionId)
                return
            }

            // 获取任务
            val task = taskRepository.getTask(taskId) ?: return

            // 创建执行记录
            val execution = TaskExecution(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                taskName = task.name,
                serviceId = task.serviceId,
                serviceName = task.serviceName,
                parameters = task.parameters,
                startTime = System.currentTimeMillis(),
                status = ExecutionStatus.PENDING,
                triggerType = TriggerType.SCHEDULED,
                triggerId = "scheduler"
            )

            // 保存执行记录
            taskRepository.saveTaskExecution(execution)

            // 执行任务
            executeTaskWithExecution(execution.id)
        }

        /**
         * 执行任务
         */
        private fun executeTaskWithExecution(executionId: String) {
            try {
                // 获取执行记录
                val execution = taskRepository.getTaskExecution(executionId) ?: return

                // 获取任务
                val task = taskRepository.getTask(execution.taskId) ?: return

                // 获取服务
                val service = serviceRepository.getService(execution.serviceId) ?: return

                // 更新执行记录状态
                taskRepository.updateTaskExecutionStatus(executionId, ExecutionStatus.RUNNING)

                // 更新任务状态
                taskRepository.updateTaskStatus(task.id, TaskStatus.RUNNING)

                // 执行服务
                val startTime = System.currentTimeMillis()
                val result = serviceExecutor.execute(service.id, execution.parameters)
                val endTime = System.currentTimeMillis()
                val duration = endTime - startTime

                // 更新执行记录结果
                taskRepository.updateTaskExecutionResult(
                    executionId,
                    ExecutionStatus.SUCCESS,
                    result,
                    null,
                    endTime,
                    duration
                )

                // 更新任务执行信息
                taskRepository.updateTaskExecutionInfo(
                    task.id,
                    startTime,
                    0, // 下次执行时间将在任务调度时更新
                    true,
                    null,
                    duration
                )

                // 更新任务状态
                taskRepository.updateTaskStatus(task.id, TaskStatus.WAITING)

                logger.info("任务执行成功: {}, 耗时: {}ms", task.name, duration)
            } catch (e: Exception) {
                logger.error("任务执行失败: {}", executionId, e)

                try {
                    // 获取执行记录
                    val execution = taskRepository.getTaskExecution(executionId) ?: return

                    // 获取任务
                    val task = taskRepository.getTask(execution.taskId) ?: return

                    // 更新执行记录结果
                    taskRepository.updateTaskExecutionResult(
                        executionId,
                        ExecutionStatus.FAILED,
                        null,
                        e.message,
                        System.currentTimeMillis(),
                        System.currentTimeMillis() - execution.startTime
                    )

                    // 更新任务执行信息
                    taskRepository.updateTaskExecutionInfo(
                        task.id,
                        execution.startTime,
                        0, // 下次执行时间将在任务调度时更新
                        false,
                        e.message,
                        System.currentTimeMillis() - execution.startTime
                    )

                    // 更新任务状态
                    taskRepository.updateTaskStatus(task.id, TaskStatus.WAITING)
                } catch (ex: Exception) {
                    logger.error("更新任务执行结果失败: {}", executionId, ex)
                }
            }
        }
    }
}
