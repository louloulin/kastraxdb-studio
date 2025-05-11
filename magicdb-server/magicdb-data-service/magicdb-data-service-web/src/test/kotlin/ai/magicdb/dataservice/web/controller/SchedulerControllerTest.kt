package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.SchedulerService
import ai.magicdb.dataservice.api.model.ScheduledTask
import ai.magicdb.dataservice.api.model.SchedulerStatus
import ai.magicdb.dataservice.api.model.SchedulerStats
import ai.magicdb.dataservice.api.model.TaskExecution
import ai.magicdb.dataservice.api.model.TaskStatus
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * 调度器控制器测试
 *
 * @author magicdb
 */
@WebMvcTest(SchedulerController::class)
class SchedulerControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockBean
    private lateinit var schedulerService: SchedulerService
    
    @BeforeEach
    fun setUp() {
        // 设置模拟对象的行为
    }
    
    @Test
    fun testCreateTask() {
        // 准备测试数据
        val task = createTestTask()
        
        // 设置模拟对象的行为
        `when`(schedulerService.createTask(any())).thenReturn(task.id)
        
        // 执行请求
        val result = mockMvc.perform(post("/api/data-service/scheduler/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task)))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"task-123\""))
        
        // 验证方法调用
        verify(schedulerService).createTask(any())
    }
    
    @Test
    fun testUpdateTask() {
        // 准备测试数据
        val taskId = "task-123"
        val task = createTestTask()
        
        // 设置模拟对象的行为
        `when`(schedulerService.updateTask(any())).thenReturn(true)
        
        // 执行请求
        val result = mockMvc.perform(put("/api/data-service/scheduler/tasks/$taskId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task)))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(schedulerService).updateTask(any())
    }
    
    @Test
    fun testDeleteTask() {
        // 准备测试数据
        val taskId = "task-123"
        
        // 设置模拟对象的行为
        `when`(schedulerService.deleteTask(taskId)).thenReturn(true)
        
        // 执行请求
        val result = mockMvc.perform(delete("/api/data-service/scheduler/tasks/$taskId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(schedulerService).deleteTask(taskId)
    }
    
    @Test
    fun testGetTask() {
        // 准备测试数据
        val taskId = "task-123"
        val task = createTestTask()
        
        // 设置模拟对象的行为
        `when`(schedulerService.getTask(taskId)).thenReturn(task)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/scheduler/tasks/$taskId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试任务\""))
        
        // 验证方法调用
        verify(schedulerService).getTask(taskId)
    }
    
    @Test
    fun testGetAllTasks() {
        // 准备测试数据
        val task1 = createTestTask("task-123", "测试任务1")
        val task2 = createTestTask("task-456", "测试任务2")
        val tasks = listOf(task1, task2)
        
        // 设置模拟对象的行为
        `when`(schedulerService.getAllTasks(null, null)).thenReturn(tasks)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/scheduler/tasks")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"测试任务1\""))
        assert(response.contains("\"测试任务2\""))
        
        // 验证方法调用
        verify(schedulerService).getAllTasks(null, null)
    }
    
    @Test
    fun testStartTask() {
        // 准备测试数据
        val taskId = "task-123"
        
        // 设置模拟对象的行为
        `when`(schedulerService.startTask(taskId)).thenReturn(true)
        
        // 执行请求
        val result = mockMvc.perform(post("/api/data-service/scheduler/tasks/$taskId/start")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(schedulerService).startTask(taskId)
    }
    
    @Test
    fun testPauseTask() {
        // 准备测试数据
        val taskId = "task-123"
        
        // 设置模拟对象的行为
        `when`(schedulerService.pauseTask(taskId)).thenReturn(true)
        
        // 执行请求
        val result = mockMvc.perform(post("/api/data-service/scheduler/tasks/$taskId/pause")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(schedulerService).pauseTask(taskId)
    }
    
    @Test
    fun testResumeTask() {
        // 准备测试数据
        val taskId = "task-123"
        
        // 设置模拟对象的行为
        `when`(schedulerService.resumeTask(taskId)).thenReturn(true)
        
        // 执行请求
        val result = mockMvc.perform(post("/api/data-service/scheduler/tasks/$taskId/resume")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(schedulerService).resumeTask(taskId)
    }
    
    @Test
    fun testExecuteTask() {
        // 准备测试数据
        val taskId = "task-123"
        val executionId = "exec-123"
        val parameters = mapOf("param1" to "value1", "param2" to 123)
        
        // 设置模拟对象的行为
        `when`(schedulerService.executeTask(taskId, parameters)).thenReturn(executionId)
        
        // 执行请求
        val result = mockMvc.perform(post("/api/data-service/scheduler/tasks/$taskId/execute")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(parameters)))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"exec-123\""))
        
        // 验证方法调用
        verify(schedulerService).executeTask(taskId, parameters)
    }
    
    @Test
    fun testGetSchedulerStatus() {
        // 准备测试数据
        val status = SchedulerStatus.RUNNING
        
        // 设置模拟对象的行为
        `when`(schedulerService.getSchedulerStatus()).thenReturn(status)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/scheduler/status")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"RUNNING\""))
        
        // 验证方法调用
        verify(schedulerService).getSchedulerStatus()
    }
    
    @Test
    fun testGetSchedulerStats() {
        // 准备测试数据
        val stats = SchedulerStats(
            totalTaskCount = 10,
            runningTaskCount = 2,
            waitingTaskCount = 5,
            pausedTaskCount = 3,
            totalExecutionCount = 100,
            successExecutionCount = 90,
            failedExecutionCount = 10
        )
        
        // 设置模拟对象的行为
        `when`(schedulerService.getSchedulerStats()).thenReturn(stats)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/scheduler/stats")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"totalTaskCount\":10"))
        
        // 验证方法调用
        verify(schedulerService).getSchedulerStats()
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
    
    /**
     * 创建测试执行记录
     */
    private fun createTestExecution(id: String = "exec-123", taskId: String = "task-123"): TaskExecution {
        return TaskExecution(
            id = id,
            taskId = taskId,
            taskName = "测试任务",
            serviceId = "service-123",
            serviceName = "测试服务",
            parameters = mapOf("param1" to "value1", "param2" to 123),
            startTime = System.currentTimeMillis(),
            status = ai.magicdb.dataservice.api.model.ExecutionStatus.RUNNING,
            triggerType = ai.magicdb.dataservice.api.model.TriggerType.SCHEDULED
        )
    }
}
