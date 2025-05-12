package ai.magicdb.data.service.core.scheduler

import ai.magicdb.data.service.api.DataServiceExecutor
import ai.magicdb.data.service.api.TaskRepository
import ai.magicdb.data.service.api.model.ScheduledTask
import ai.magicdb.data.service.api.model.ScheduledTaskStatus
import ai.magicdb.data.service.api.model.ServiceResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.quartz.JobKey
import org.quartz.Scheduler

/**
 * Quartz调度器服务测试
 *
 * @author magicdb
 */
class QuartzSchedulerServiceTest {
    
    private lateinit var schedulerService: QuartzSchedulerService
    private lateinit var scheduler: Scheduler
    private lateinit var taskRepository: TaskRepository
    private lateinit var serviceExecutor: DataServiceExecutor
    
    @BeforeEach
    fun setUp() {
        scheduler = mock(Scheduler::class.java)
        taskRepository = mock(TaskRepository::class.java)
        serviceExecutor = mock(DataServiceExecutor::class.java)
        
        schedulerService = QuartzSchedulerService(
            scheduler,
            taskRepository,
            serviceExecutor
        )
    }
    
    @Test
    fun testScheduleTask() {
        // 准备测试数据
        val task = createTestTask()
        
        // 设置模拟对象的行为
        `when`(taskRepository.saveTask(any())).thenReturn(task.id)
        
        // 执行测试
        val result = schedulerService.scheduleTask(task)
        
        // 验证结果
        assertTrue(result)
    }
    
    @Test
    fun testUnscheduleTask() {
        // 准备测试数据
        val taskId = "task-123"
        
        // 设置模拟对象的行为
        `when`(taskRepository.deleteTask(taskId)).thenReturn(true)
        
        // 执行测试
        val result = schedulerService.unscheduleTask(taskId)
        
        // 验证结果
        assertTrue(result)
    }
    
    @Test
    fun testPauseTask() {
        // 准备测试数据
        val taskId = "task-123"
        val task = createTestTask()
        
        // 设置模拟对象的行为
        `when`(taskRepository.getTask(taskId)).thenReturn(task)
        `when`(taskRepository.updateTask(any())).thenReturn(true)
        
        // 执行测试
        val result = schedulerService.pauseTask(taskId)
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(scheduler).pauseJob(JobKey.jobKey(taskId))
    }
    
    @Test
    fun testResumeTask() {
        // 准备测试数据
        val taskId = "task-123"
        val task = createTestTask()
        
        // 设置模拟对象的行为
        `when`(taskRepository.getTask(taskId)).thenReturn(task)
        `when`(taskRepository.updateTask(any())).thenReturn(true)
        
        // 执行测试
        val result = schedulerService.resumeTask(taskId)
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(scheduler).resumeJob(JobKey.jobKey(taskId))
    }
    
    @Test
    fun testExecuteTask() {
        // 准备测试数据
        val taskId = "task-123"
        val task = createTestTask()
        val parameters = mapOf("param1" to "value1")
        val serviceResult = ServiceResult(
            success = true,
            data = mapOf("result" to "success")
        )
        
        // 设置模拟对象的行为
        `when`(taskRepository.getTask(taskId)).thenReturn(task)
        `when`(serviceExecutor.execute(task.serviceId, parameters)).thenReturn(serviceResult)
        `when`(taskRepository.saveExecution(any())).thenReturn("exec-123")
        
        // 执行测试
        val executionId = schedulerService.executeTask(taskId, parameters)
        
        // 验证结果
        assertNotNull(executionId)
    }
    
    @Test
    fun testEnableTask() {
        // 准备测试数据
        val taskId = "task-123"
        val task = createTestTask()
        task.enabled = false
        
        // 设置模拟对象的行为
        `when`(taskRepository.getTask(taskId)).thenReturn(task)
        `when`(taskRepository.updateTask(any())).thenReturn(true)
        
        // 执行测试
        val result = schedulerService.enableTask(taskId)
        
        // 验证结果
        assertTrue(result)
    }
    
    @Test
    fun testDisableTask() {
        // 准备测试数据
        val taskId = "task-123"
        val task = createTestTask()
        task.enabled = true
        
        // 设置模拟对象的行为
        `when`(taskRepository.getTask(taskId)).thenReturn(task)
        `when`(taskRepository.updateTask(any())).thenReturn(true)
        
        // 执行测试
        val result = schedulerService.disableTask(taskId)
        
        // 验证结果
        assertTrue(result)
    }
    
    @Test
    fun testGetSchedulerInfo() {
        // 设置模拟对象的行为
        `when`(scheduler.isShutdown).thenReturn(false)
        `when`(scheduler.isInStandbyMode).thenReturn(false)
        `when`(scheduler.isStarted).thenReturn(true)
        
        // 执行测试
        val info = schedulerService.getSchedulerInfo()
        
        // 验证结果
        assertNotNull(info)
        assertTrue(info.containsKey("status"))
    }
    
    @Test
    fun testGetRunningTasks() {
        // 准备测试数据
        val task1 = createTestTask("task-123", "测试任务1")
        val task2 = createTestTask("task-456", "测试任务2")
        val tasks = listOf(task1, task2)
        
        // 设置模拟对象的行为
        `when`(taskRepository.getTasksByStatus(ScheduledTaskStatus.RUNNING)).thenReturn(tasks)
        
        // 执行测试
        val result = schedulerService.getRunningTasks()
        
        // 验证结果
        assertEquals(2, result.size)
    }
    
    @Test
    fun testGetAllJobs() {
        // 执行测试
        val jobs = schedulerService.getAllJobs()
        
        // 验证结果
        assertNotNull(jobs)
    }
    
    /**
     * 创建测试任务
     */
    private fun createTestTask(id: String = "task-123", name: String = "测试任务"): ScheduledTask {
        return ScheduledTask(
            id = id,
            name = name,
            description = "测试任务描述",
            serviceId = "service-123",
            serviceName = "测试服务",
            parameters = mapOf("param1" to "value1", "param2" to 123),
            cronExpression = "0 0 12 * * ?",
            enabled = true,
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1,
            status = ScheduledTaskStatus.PENDING,
            group = "test"
        )
    }
}
