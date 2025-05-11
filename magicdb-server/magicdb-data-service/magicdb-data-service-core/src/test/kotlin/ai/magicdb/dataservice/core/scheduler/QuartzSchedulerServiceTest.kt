package ai.magicdb.dataservice.core.scheduler

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.model.ScheduledTask
import ai.magicdb.dataservice.api.model.SchedulerStatus
import ai.magicdb.dataservice.api.model.TaskExecution
import ai.magicdb.dataservice.api.model.TaskStatus
import ai.magicdb.dataservice.core.repository.TaskRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.impl.matchers.GroupMatcher
import java.util.*

/**
 * Quartz调度器服务测试
 *
 * @author magicdb
 */
class QuartzSchedulerServiceTest {
    
    private lateinit var schedulerService: QuartzSchedulerService
    private lateinit var scheduler: Scheduler
    private lateinit var taskRepository: TaskRepository
    private lateinit var serviceRepository: DataServiceRepository
    private lateinit var serviceExecutor: DataServiceExecutor
    
    @BeforeEach
    fun setUp() {
        scheduler = mock(Scheduler::class.java)
        taskRepository = mock(TaskRepository::class.java)
        serviceRepository = mock(DataServiceRepository::class.java)
        serviceExecutor = mock(DataServiceExecutor::class.java)
        
        schedulerService = QuartzSchedulerService(
            scheduler,
            taskRepository,
            serviceRepository,
            serviceExecutor
        )
    }
    
    @Test
    fun testCreateTask() {
        // 准备测试数据
        val task = createTestTask()
        
        // 设置模拟对象的行为
        `when`(taskRepository.saveTask(any())).thenReturn(task.id)
        
        // 执行测试
        val taskId = schedulerService.createTask(task)
        
        // 验证结果
        assertEquals(task.id, taskId)
        
        // 验证方法调用
        verify(taskRepository).saveTask(any())
    }
    
    @Test
    fun testUpdateTask() {
        // 准备测试数据
        val task = createTestTask()
        
        // 设置模拟对象的行为
        `when`(taskRepository.updateTask(any())).thenReturn(true)
        
        // 执行测试
        val result = schedulerService.updateTask(task)
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(taskRepository).updateTask(any())
    }
    
    @Test
    fun testDeleteTask() {
        // 准备测试数据
        val taskId = "task-123"
        
        // 设置模拟对象的行为
        `when`(taskRepository.deleteTask(taskId)).thenReturn(true)
        
        // 执行测试
        val result = schedulerService.deleteTask(taskId)
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(taskRepository).deleteTask(taskId)
    }
    
    @Test
    fun testGetTask() {
        // 准备测试数据
        val taskId = "task-123"
        val task = createTestTask()
        
        // 设置模拟对象的行为
        `when`(taskRepository.getTask(taskId)).thenReturn(task)
        
        // 执行测试
        val result = schedulerService.getTask(taskId)
        
        // 验证结果
        assertNotNull(result)
        assertEquals(task.id, result?.id)
        
        // 验证方法调用
        verify(taskRepository).getTask(taskId)
    }
    
    @Test
    fun testGetAllTasks() {
        // 准备测试数据
        val task1 = createTestTask("task-123", "测试任务1")
        val task2 = createTestTask("task-456", "测试任务2")
        val tasks = listOf(task1, task2)
        
        // 设置模拟对象的行为
        `when`(taskRepository.getAllTasks(null, null)).thenReturn(tasks)
        
        // 执行测试
        val result = schedulerService.getAllTasks()
        
        // 验证结果
        assertEquals(2, result.size)
        assertEquals("task-123", result[0].id)
        assertEquals("task-456", result[1].id)
        
        // 验证方法调用
        verify(taskRepository).getAllTasks(null, null)
    }
    
    @Test
    fun testGetTasksByService() {
        // 准备测试数据
        val serviceId = "service-123"
        val task1 = createTestTask("task-123", "测试任务1")
        val task2 = createTestTask("task-456", "测试任务2")
        val tasks = listOf(task1, task2)
        
        // 设置模拟对象的行为
        `when`(taskRepository.getTasksByService(serviceId)).thenReturn(tasks)
        
        // 执行测试
        val result = schedulerService.getTasksByService(serviceId)
        
        // 验证结果
        assertEquals(2, result.size)
        
        // 验证方法调用
        verify(taskRepository).getTasksByService(serviceId)
    }
    
    @Test
    fun testStartTask() {
        // 准备测试数据
        val taskId = "task-123"
        val task = createTestTask()
        
        // 设置模拟对象的行为
        `when`(taskRepository.getTask(taskId)).thenReturn(task)
        `when`(taskRepository.updateTaskStatus(taskId, TaskStatus.WAITING)).thenReturn(true)
        `when`(taskRepository.updateTaskEnabled(taskId, true)).thenReturn(true)
        
        // 执行测试
        val result = schedulerService.startTask(taskId)
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(taskRepository).getTask(taskId)
        verify(taskRepository).updateTaskStatus(taskId, TaskStatus.WAITING)
        verify(taskRepository).updateTaskEnabled(taskId, true)
    }
    
    @Test
    fun testPauseTask() {
        // 准备测试数据
        val taskId = "task-123"
        
        // 设置模拟对象的行为
        `when`(taskRepository.updateTaskStatus(taskId, TaskStatus.PAUSED)).thenReturn(true)
        
        // 执行测试
        val result = schedulerService.pauseTask(taskId)
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(scheduler).pauseJob(JobKey.jobKey(taskId))
        verify(taskRepository).updateTaskStatus(taskId, TaskStatus.PAUSED)
    }
    
    @Test
    fun testResumeTask() {
        // 准备测试数据
        val taskId = "task-123"
        
        // 设置模拟对象的行为
        `when`(taskRepository.updateTaskStatus(taskId, TaskStatus.WAITING)).thenReturn(true)
        
        // 执行测试
        val result = schedulerService.resumeTask(taskId)
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(scheduler).resumeJob(JobKey.jobKey(taskId))
        verify(taskRepository).updateTaskStatus(taskId, TaskStatus.WAITING)
    }
    
    @Test
    fun testGetSchedulerStatus() {
        // 设置模拟对象的行为
        `when`(scheduler.isShutdown).thenReturn(false)
        `when`(scheduler.isInStandbyMode).thenReturn(false)
        `when`(scheduler.isStarted).thenReturn(true)
        
        // 执行测试
        val status = schedulerService.getSchedulerStatus()
        
        // 验证结果
        assertEquals(SchedulerStatus.RUNNING, status)
    }
    
    @Test
    fun testStartScheduler() {
        // 设置模拟对象的行为
        `when`(scheduler.isStarted).thenReturn(false)
        
        // 执行测试
        val result = schedulerService.startScheduler()
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(scheduler).start()
    }
    
    @Test
    fun testShutdownScheduler() {
        // 设置模拟对象的行为
        `when`(scheduler.isShutdown).thenReturn(false)
        
        // 执行测试
        val result = schedulerService.shutdownScheduler()
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(scheduler).shutdown(true)
    }
    
    @Test
    fun testPauseScheduler() {
        // 设置模拟对象的行为
        `when`(scheduler.isInStandbyMode).thenReturn(false)
        
        // 执行测试
        val result = schedulerService.pauseScheduler()
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(scheduler).standby()
    }
    
    @Test
    fun testResumeScheduler() {
        // 设置模拟对象的行为
        `when`(scheduler.isInStandbyMode).thenReturn(true)
        
        // 执行测试
        val result = schedulerService.resumeScheduler()
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(scheduler).start()
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
            status = TaskStatus.WAITING,
            group = "test"
        )
    }
}
