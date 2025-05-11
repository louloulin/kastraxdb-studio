package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.model.ExecutionStatus
import ai.magicdb.dataservice.api.model.ScheduledTask
import ai.magicdb.dataservice.api.model.TaskExecution
import ai.magicdb.dataservice.api.model.TaskStatus
import ai.magicdb.dataservice.api.model.TriggerType
import ai.magicdb.dataservice.core.entity.ScheduledTaskDO
import ai.magicdb.dataservice.core.entity.TaskExecutionDO
import ai.magicdb.dataservice.core.mapper.ScheduledTaskMapper
import ai.magicdb.dataservice.core.mapper.TaskExecutionMapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.*

/**
 * MyBatis任务存储库测试
 *
 * @author magicdb
 */
class MybatisTaskRepositoryTest {
    
    private lateinit var taskRepository: MybatisTaskRepository
    private lateinit var taskMapper: ScheduledTaskMapper
    private lateinit var executionMapper: TaskExecutionMapper
    private lateinit var objectMapper: ObjectMapper
    
    @BeforeEach
    fun setUp() {
        taskMapper = mock(ScheduledTaskMapper::class.java)
        executionMapper = mock(TaskExecutionMapper::class.java)
        objectMapper = ObjectMapper()
        
        taskRepository = MybatisTaskRepository(
            taskMapper,
            executionMapper,
            objectMapper
        )
    }
    
    @Test
    fun testSaveTask() {
        // 准备测试数据
        val task = createTestTask()
        
        // 设置模拟对象的行为
        `when`(taskMapper.selectById(task.id)).thenReturn(null)
        `when`(taskMapper.insert(any())).thenReturn(1)
        
        // 执行测试
        val taskId = taskRepository.saveTask(task)
        
        // 验证结果
        assertEquals(task.id, taskId)
        
        // 验证方法调用
        verify(taskMapper).selectById(task.id)
        verify(taskMapper).insert(any())
    }
    
    @Test
    fun testUpdateTask() {
        // 准备测试数据
        val task = createTestTask()
        
        // 设置模拟对象的行为
        `when`(taskMapper.updateById(any())).thenReturn(1)
        
        // 执行测试
        val result = taskRepository.updateTask(task)
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(taskMapper).updateById(any())
    }
    
    @Test
    fun testDeleteTask() {
        // 准备测试数据
        val taskId = "task-123"
        
        // 设置模拟对象的行为
        `when`(taskMapper.deleteById(taskId)).thenReturn(1)
        
        // 执行测试
        val result = taskRepository.deleteTask(taskId)
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(taskMapper).deleteById(taskId)
    }
    
    @Test
    fun testGetTask() {
        // 准备测试数据
        val taskId = "task-123"
        val taskDO = createTestTaskDO()
        
        // 设置模拟对象的行为
        `when`(taskMapper.selectById(taskId)).thenReturn(taskDO)
        
        // 执行测试
        val task = taskRepository.getTask(taskId)
        
        // 验证结果
        assertNotNull(task)
        assertEquals(taskId, task?.id)
        assertEquals("测试任务", task?.name)
        
        // 验证方法调用
        verify(taskMapper).selectById(taskId)
    }
    
    @Test
    fun testGetAllTasks() {
        // 准备测试数据
        val taskDO1 = createTestTaskDO("task-123", "测试任务1")
        val taskDO2 = createTestTaskDO("task-456", "测试任务2")
        val taskDOs = listOf(taskDO1, taskDO2)
        
        // 设置模拟对象的行为
        `when`(taskMapper.selectList(null)).thenReturn(taskDOs)
        
        // 执行测试
        val tasks = taskRepository.getAllTasks()
        
        // 验证结果
        assertEquals(2, tasks.size)
        assertEquals("task-123", tasks[0].id)
        assertEquals("task-456", tasks[1].id)
        
        // 验证方法调用
        verify(taskMapper).selectList(null)
    }
    
    @Test
    fun testGetAllTasksByStatus() {
        // 准备测试数据
        val status = TaskStatus.RUNNING
        val taskDO1 = createTestTaskDO("task-123", "测试任务1")
        val taskDO2 = createTestTaskDO("task-456", "测试任务2")
        val taskDOs = listOf(taskDO1, taskDO2)
        
        // 设置模拟对象的行为
        `when`(taskMapper.selectByStatus(status.name)).thenReturn(taskDOs)
        
        // 执行测试
        val tasks = taskRepository.getAllTasks(status)
        
        // 验证结果
        assertEquals(2, tasks.size)
        
        // 验证方法调用
        verify(taskMapper).selectByStatus(status.name)
    }
    
    @Test
    fun testGetTasksByService() {
        // 准备测试数据
        val serviceId = "service-123"
        val taskDO1 = createTestTaskDO("task-123", "测试任务1")
        val taskDO2 = createTestTaskDO("task-456", "测试任务2")
        val taskDOs = listOf(taskDO1, taskDO2)
        
        // 设置模拟对象的行为
        `when`(taskMapper.selectByServiceId(serviceId)).thenReturn(taskDOs)
        
        // 执行测试
        val tasks = taskRepository.getTasksByService(serviceId)
        
        // 验证结果
        assertEquals(2, tasks.size)
        
        // 验证方法调用
        verify(taskMapper).selectByServiceId(serviceId)
    }
    
    @Test
    fun testUpdateTaskStatus() {
        // 准备测试数据
        val taskId = "task-123"
        val status = TaskStatus.RUNNING
        
        // 设置模拟对象的行为
        `when`(taskMapper.updateStatus(eq(taskId), eq(status.name), anyLong())).thenReturn(1)
        
        // 执行测试
        val result = taskRepository.updateTaskStatus(taskId, status)
        
        // 验证结果
        assertTrue(result)
        
        // 验证方法调用
        verify(taskMapper).updateStatus(eq(taskId), eq(status.name), anyLong())
    }
    
    @Test
    fun testSaveTaskExecution() {
        // 准备测试数据
        val execution = createTestExecution()
        
        // 设置模拟对象的行为
        `when`(executionMapper.selectById(execution.id)).thenReturn(null)
        `when`(executionMapper.insert(any())).thenReturn(1)
        
        // 执行测试
        val executionId = taskRepository.saveTaskExecution(execution)
        
        // 验证结果
        assertEquals(execution.id, executionId)
        
        // 验证方法调用
        verify(executionMapper).selectById(execution.id)
        verify(executionMapper).insert(any())
    }
    
    @Test
    fun testGetTaskExecution() {
        // 准备测试数据
        val executionId = "exec-123"
        val executionDO = createTestExecutionDO()
        
        // 设置模拟对象的行为
        `when`(executionMapper.selectById(executionId)).thenReturn(executionDO)
        
        // 执行测试
        val execution = taskRepository.getTaskExecution(executionId)
        
        // 验证结果
        assertNotNull(execution)
        assertEquals(executionId, execution?.id)
        assertEquals("task-123", execution?.taskId)
        
        // 验证方法调用
        verify(executionMapper).selectById(executionId)
    }
    
    @Test
    fun testGetTaskExecutions() {
        // 准备测试数据
        val taskId = "task-123"
        val limit = 10
        val offset = 0
        val executionDO1 = createTestExecutionDO("exec-123", taskId)
        val executionDO2 = createTestExecutionDO("exec-456", taskId)
        val executionDOs = listOf(executionDO1, executionDO2)
        
        // 设置模拟对象的行为
        `when`(executionMapper.selectByTaskId(taskId, limit, offset)).thenReturn(executionDOs)
        
        // 执行测试
        val executions = taskRepository.getTaskExecutions(taskId, limit, offset)
        
        // 验证结果
        assertEquals(2, executions.size)
        assertEquals("exec-123", executions[0].id)
        assertEquals("exec-456", executions[1].id)
        
        // 验证方法调用
        verify(executionMapper).selectByTaskId(taskId, limit, offset)
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
     * 创建测试任务DO
     */
    private fun createTestTaskDO(id: String = "task-123", name: String = "测试任务"): ScheduledTaskDO {
        val taskDO = ScheduledTaskDO()
        taskDO.id = id
        taskDO.name = name
        taskDO.description = "测试任务描述"
        taskDO.serviceId = "service-123"
        taskDO.serviceName = "测试服务"
        taskDO.parameters = "{\"param1\":\"value1\",\"param2\":123}"
        taskDO.cronExpression = "0 0 12 * * ?"
        taskDO.enabled = true
        taskDO.createTime = System.currentTimeMillis()
        taskDO.updateTime = System.currentTimeMillis()
        taskDO.createUserId = 1
        taskDO.status = TaskStatus.WAITING.name
        taskDO.group = "test"
        return taskDO
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
            status = ExecutionStatus.RUNNING,
            triggerType = TriggerType.SCHEDULED
        )
    }
    
    /**
     * 创建测试执行记录DO
     */
    private fun createTestExecutionDO(id: String = "exec-123", taskId: String = "task-123"): TaskExecutionDO {
        val executionDO = TaskExecutionDO()
        executionDO.id = id
        executionDO.taskId = taskId
        executionDO.taskName = "测试任务"
        executionDO.serviceId = "service-123"
        executionDO.serviceName = "测试服务"
        executionDO.parameters = "{\"param1\":\"value1\",\"param2\":123}"
        executionDO.startTime = System.currentTimeMillis()
        executionDO.status = ExecutionStatus.RUNNING.name
        executionDO.triggerType = TriggerType.SCHEDULED.name
        return executionDO
    }
}
