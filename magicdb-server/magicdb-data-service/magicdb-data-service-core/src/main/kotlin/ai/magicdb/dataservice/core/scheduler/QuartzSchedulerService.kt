package ai.magicdb.dataservice.core.scheduler

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.SchedulerService
import ai.magicdb.dataservice.api.TaskRepository
import ai.magicdb.dataservice.api.model.ExecutionStatus
import ai.magicdb.dataservice.api.model.ScheduledTask
import ai.magicdb.dataservice.api.model.ScheduledTaskStatus
import ai.magicdb.dataservice.api.model.TaskExecution
import ai.magicdb.dataservice.api.model.TriggerType
import org.quartz.*
import org.quartz.impl.matchers.GroupMatcher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Quartz调度器服务实现
 *
 * @author magicdb
 */
@Service
class QuartzSchedulerService @Autowired constructor(
    private val scheduler: Scheduler,
    private val taskRepository: TaskRepository,
    private val serviceExecutor: DataServiceExecutor
) : SchedulerService {
    private val logger = LoggerFactory.getLogger(QuartzSchedulerService::class.java)
    
    @PostConstruct
    fun init() {
        try {
            // 启动调度器
            if (!scheduler.isStarted) {
                scheduler.start()
                logger.info("调度器已启动")
            }
            
            // 加载所有启用的任务
            loadEnabledTasks()
        } catch (e: Exception) {
            logger.error("初始化调度器失败", e)
        }
    }
    
    @PreDestroy
    fun destroy() {
        try {
            // 关闭调度器
            if (!scheduler.isShutdown) {
                scheduler.shutdown(true)
                logger.info("调度器已关闭")
            }
        } catch (e: Exception) {
            logger.error("关闭调度器失败", e)
        }
    }
    
    override fun scheduleTask(task: ScheduledTask): Boolean {
        try {
            // 创建任务
            val jobDetail = createJobDetail(task)
            
            // 创建触发器
            val trigger = createTrigger(task)
            
            // 调度任务
            scheduler.scheduleJob(jobDetail, trigger)
            
            // 更新任务状态
            task.status = ScheduledTaskStatus.PENDING
            taskRepository.updateTask(task)
            
            // 计算下次执行时间
            val nextFireTime = trigger.nextFireTime?.time ?: 0
            if (nextFireTime > 0) {
                task.nextExecuteTime = nextFireTime
                taskRepository.updateTask(task)
            }
            
            logger.info("任务已调度: {}", task.name)
            return true
        } catch (e: Exception) {
            logger.error("调度任务失败: {}", task.name, e)
            return false
        }
    }
    
    override fun unscheduleTask(taskId: String): Boolean {
        try {
            // 删除任务
            scheduler.deleteJob(JobKey.jobKey(taskId))
            
            logger.info("任务已取消调度: {}", taskId)
            return true
        } catch (e: Exception) {
            logger.error("取消任务调度失败: {}", taskId, e)
            return false
        }
    }
    
    override fun pauseTask(taskId: String): Boolean {
        try {
            // 暂停任务
            scheduler.pauseJob(JobKey.jobKey(taskId))
            
            // 更新任务状态
            val task = taskRepository.getTask(taskId) ?: return false
            task.status = ScheduledTaskStatus.PAUSED
            taskRepository.updateTask(task)
            
            logger.info("任务已暂停: {}", taskId)
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
            val task = taskRepository.getTask(taskId) ?: return false
            task.status = ScheduledTaskStatus.PENDING
            taskRepository.updateTask(task)
            
            return true
        } catch (e: Exception) {
            logger.error("恢复任务失败: {}", taskId, e)
            return false
        }
    }
    
    override fun executeTask(taskId: String): String {
        try {
            // 获取任务
            val task = taskRepository.getTask(taskId) ?: throw IllegalArgumentException("任务不存在: $taskId")
            
            // 创建执行记录
            val executionId = UUID.randomUUID().toString()
            val execution = TaskExecution(
                id = executionId,
                taskId = task.id,
                taskName = task.name,
                serviceId = task.serviceId,
                serviceName = task.serviceName,
                parameters = task.parameters,
                startTime = System.currentTimeMillis(),
                status = ExecutionStatus.RUNNING,
                triggerType = TriggerType.MANUAL,
                triggerId = "manual"
            )
            
            // 保存执行记录
            taskRepository.saveExecution(execution)
            
            // 更新任务状态
            task.status = ScheduledTaskStatus.RUNNING
            taskRepository.updateTask(task)
            
            // 执行任务
            executeTaskAsync(task, execution)
            
            logger.info("任务已手动执行: {}", task.name)
            return executionId
        } catch (e: Exception) {
            logger.error("手动执行任务失败: {}", taskId, e)
            throw e
        }
    }
    
    override fun enableTask(taskId: String): Boolean {
        try {
            // 获取任务
            val task = taskRepository.getTask(taskId) ?: return false
            
            // 更新任务状态
            task.status = ScheduledTaskStatus.PENDING
            
            // 更新任务启用状态
            task.enabled = true
            taskRepository.updateTask(task)
            
            // 调度任务
            return scheduleTask(task)
        } catch (e: Exception) {
            logger.error("启用任务失败: {}", taskId, e)
            return false
        }
    }
    
    override fun disableTask(taskId: String): Boolean {
        try {
            // 获取任务
            val task = taskRepository.getTask(taskId) ?: return false
            
            // 更新任务启用状态
            task.enabled = false
            taskRepository.updateTask(task)
            
            // 取消调度任务
            return unscheduleTask(taskId)
        } catch (e: Exception) {
            logger.error("禁用任务失败: {}", taskId, e)
            return false
        }
    }
    
    override fun getSchedulerInfo(): Map<String, Any> {
        try {
            // 获取调度器元数据
            val metaData = scheduler.metaData
            
            // 获取任务统计信息
            val tasks = taskRepository.getAllTasks()
            val runningTasks = tasks.filter { it.status == ScheduledTaskStatus.RUNNING }
            val waitingTasks = tasks.filter { it.status == ScheduledTaskStatus.PENDING }
            val pausedTasks = tasks.filter { it.status == ScheduledTaskStatus.PAUSED }
            val failedTasks = tasks.filter { it.status == ScheduledTaskStatus.FAILED }
            
            // 计算执行统计信息
            val totalExecutionCount = tasks.sumOf { it.executeCount }
            val successExecutionCount = tasks.sumOf { it.successCount }
            val failedExecutionCount = tasks.sumOf { it.failCount }
            
            // 构建调度器信息
            return mapOf(
                "name" to metaData.schedulerName,
                "instanceId" to metaData.schedulerInstanceId,
                "version" to metaData.version,
                "running" to metaData.isStarted,
                "standby" to metaData.isInStandbyMode,
                "shutdown" to metaData.isShutdown,
                "jobStoreClass" to metaData.jobStoreClass,
                "threadPoolClass" to metaData.threadPoolClass,
                "threadPoolSize" to metaData.threadPoolSize,
                "jobCount" to metaData.numberOfJobs,
                "triggerCount" to metaData.numberOfTriggers,
                "executedJobs" to metaData.numberOfJobsExecuted,
                "runningJobs" to scheduler.currentlyExecutingJobs.size,
                "taskCount" to tasks.size,
                "runningTaskCount" to runningTasks.size,
                "waitingTaskCount" to waitingTasks.size,
                "pausedTaskCount" to pausedTasks.size,
                "failedTaskCount" to failedTasks.size,
                "totalExecutionCount" to totalExecutionCount,
                "successExecutionCount" to successExecutionCount,
                "failedExecutionCount" to failedExecutionCount,
                "successRate" to if (totalExecutionCount > 0) {
                    successExecutionCount.toDouble() / totalExecutionCount
                } else {
                    0.0
                }
            )
        } catch (e: Exception) {
            logger.error("获取调度器信息失败", e)
            return emptyMap()
        }
    }
    
    override fun getRunningTasks(): List<ScheduledTask> {
        try {
            // 获取正在执行的任务
            val jobKeys = scheduler.currentlyExecutingJobs.map { it.jobDetail.key.name }
            
            // 获取任务信息
            return taskRepository.getAllTasks().filter { it.id in jobKeys }
        } catch (e: Exception) {
            logger.error("获取正在执行的任务失败", e)
            return emptyList()
        }
    }
    
    override fun getAllJobs(): List<Map<String, Any>> {
        try {
            // 获取所有任务组
            val jobGroups = scheduler.jobGroupNames
            
            // 获取所有任务
            val jobs = mutableListOf<Map<String, Any>>()
            
            for (group in jobGroups) {
                // 获取组内所有任务
                val jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group))
                
                for (jobKey in jobKeys) {
                    // 获取任务详情
                    val jobDetail = scheduler.getJobDetail(jobKey)
                    
                    // 获取任务触发器
                    val triggers = scheduler.getTriggersOfJob(jobKey)
                    
                    // 构建任务信息
                    val job = mutableMapOf<String, Any>(
                        "name" to jobKey.name,
                        "group" to jobKey.group,
                        "description" to (jobDetail.description ?: ""),
                        "jobClass" to jobDetail.jobClass.name,
                        "durability" to jobDetail.isDurable,
                        "requestsRecovery" to jobDetail.requestsRecovery(),
                        "jobDataMap" to jobDetail.jobDataMap,
                        "triggers" to triggers.map { trigger ->
                            mapOf(
                                "name" to trigger.key.name,
                                "group" to trigger.key.group,
                                "description" to (trigger.description ?: ""),
                                "startTime" to trigger.startTime,
                                "endTime" to trigger.endTime,
                                "nextFireTime" to trigger.nextFireTime,
                                "previousFireTime" to trigger.previousFireTime,
                                "priority" to trigger.priority,
                                "finalFireTime" to trigger.finalFireTime,
                                "misfireInstruction" to trigger.misfireInstruction,
                                "triggerType" to trigger.javaClass.simpleName
                            )
                        }
                    )
                    
                    jobs.add(job)
                }
            }
            
            return jobs
        } catch (e: Exception) {
            logger.error("获取所有任务失败", e)
            return emptyList()
        }
    }
    
    /**
     * 加载所有启用的任务
     */
    private fun loadEnabledTasks() {
        try {
            // 获取所有启用的任务
            val tasks = taskRepository.getAllTasks().filter { it.enabled }
            
            logger.info("加载启用的任务: {}", tasks.size)
            
            // 调度任务
            for (task in tasks) {
                try {
                    scheduleTask(task)
                } catch (e: Exception) {
                    logger.error("加载任务失败: {}", task.id, e)
                }
            }
        } catch (e: Exception) {
            logger.error("加载启用的任务失败", e)
        }
    }
    
    /**
     * 创建任务详情
     */
    private fun createJobDetail(task: ScheduledTask): JobDetail {
        // 创建任务数据
        val jobDataMap = JobDataMap()
        jobDataMap.put("taskId", task.id)
        
        // 创建任务详情
        return JobBuilder.newJob(TaskJob::class.java)
            .withIdentity(task.id)
            .withDescription(task.description)
            .usingJobData(jobDataMap)
            .storeDurably()
            .build()
    }
    
    /**
     * 创建触发器
     */
    private fun createTrigger(task: ScheduledTask): Trigger {
        // 创建触发器
        return TriggerBuilder.newTrigger()
            .withIdentity(task.id)
            .withDescription(task.description)
            .withSchedule(CronScheduleBuilder.cronSchedule(task.cronExpression)
                .withMisfireHandlingInstructionFireAndProceed())
            .build()
    }
    
    /**
     * 异步执行任务
     */
    private fun executeTaskAsync(task: ScheduledTask, execution: TaskExecution) {
        // 创建线程执行任务
        Thread {
            try {
                // 执行服务
                val startTime = System.currentTimeMillis()
                val result = serviceExecutor.execute(task.serviceId, task.parameters)
                val endTime = System.currentTimeMillis()
                val duration = endTime - startTime
                
                // 更新执行记录
                execution.endTime = endTime
                execution.duration = duration
                execution.status = ExecutionStatus.SUCCESS
                execution.result = result.data
                taskRepository.updateExecution(execution)
                
                // 更新任务信息
                task.lastExecuteTime = startTime
                task.executeCount++
                task.successCount++
                task.lastExecuteResult = true
                task.lastExecuteMessage = "执行成功"
                task.lastExecuteDuration = duration
                task.status = ScheduledTaskStatus.PENDING
                taskRepository.updateTask(task)
                
                logger.info("任务执行成功: {}, 耗时: {}ms", task.name, duration)
            } catch (e: Exception) {
                logger.error("任务执行失败: {}", execution.id, e)
                
                try {
                    // 更新执行记录
                    execution.endTime = System.currentTimeMillis()
                    execution.duration = execution.endTime - execution.startTime
                    execution.status = ExecutionStatus.FAILED
                    execution.errorMessage = e.message ?: "执行失败"
                    taskRepository.updateExecution(execution)
                    
                    // 更新任务信息
                    task.lastExecuteTime = execution.startTime
                    task.executeCount++
                    task.failCount++
                    task.lastExecuteResult = false
                    task.lastExecuteMessage = e.message ?: "执行失败"
                    task.lastExecuteDuration = execution.duration
                    task.status = ScheduledTaskStatus.PENDING
                    taskRepository.updateTask(task)
                } catch (ex: Exception) {
                    logger.error("更新任务执行结果失败: {}", execution.id, ex)
                }
            }
        }.start()
    }
    
    /**
     * 任务执行器
     */
    class TaskJob : Job {
        override fun execute(context: JobExecutionContext) {
            // 获取调度器服务
            val schedulerService = context.scheduler.context.get("schedulerService") as QuartzSchedulerService
            
            // 获取任务ID
            val taskId = context.jobDetail.jobDataMap.getString("taskId")
            
            try {
                // 执行任务
                schedulerService.executeTask(taskId)
            } catch (e: Exception) {
                // 忽略异常，已在executeTask中处理
            }
        }
    }
}
