package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.TaskRepository
import ai.magicdb.dataservice.api.model.ScheduledTask
import ai.magicdb.dataservice.api.model.ScheduledTaskStatus
import ai.magicdb.dataservice.api.model.TaskExecution
import ai.magicdb.dataservice.api.model.ExecutionStatus
import ai.magicdb.dataservice.api.model.TriggerType
import ai.magicdb.dataservice.core.entity.ScheduledTaskDO
import ai.magicdb.dataservice.core.entity.TaskExecutionDO
import ai.magicdb.dataservice.core.mapper.ScheduledTaskMapper
import ai.magicdb.dataservice.core.mapper.TaskExecutionMapper
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * MyBatis任务存储库实现
 *
 * @author magicdb
 */
@Repository
class MybatisTaskRepository(
    private val taskMapper: ScheduledTaskMapper,
    private val executionMapper: TaskExecutionMapper,
    private val objectMapper: ObjectMapper
) : TaskRepository {
    private val logger = LoggerFactory.getLogger(MybatisTaskRepository::class.java)
    
    @Transactional
    override fun saveTask(task: ScheduledTask): String {
        try {
            // 如果ID为空，生成新ID
            if (task.id.isBlank()) {
                task.id = UUID.randomUUID().toString()
            }
            
            // 转换为DO对象
            val taskDO = convertToTaskDO(task)
            
            // 保存任务
            taskMapper.insert(taskDO)
            
            return task.id
        } catch (e: Exception) {
            logger.error("保存任务失败: {}", task.name, e)
            throw e
        }
    }
    
    @Transactional
    override fun updateTask(task: ScheduledTask): Boolean {
        try {
            // 检查任务是否存在
            val existingTask = taskMapper.selectById(task.id)
                ?: return false
            
            // 转换为DO对象
            val taskDO = convertToTaskDO(task)
            
            // 更新任务
            taskMapper.updateById(taskDO)
            
            return true
        } catch (e: Exception) {
            logger.error("更新任务失败: {}", task.id, e)
            return false
        }
    }
    
    @Transactional
    override fun deleteTask(taskId: String): Boolean {
        try {
            // 删除任务
            val result = taskMapper.deleteById(taskId)
            
            return result > 0
        } catch (e: Exception) {
            logger.error("删除任务失败: {}", taskId, e)
            return false
        }
    }
    
    override fun getTask(taskId: String): ScheduledTask? {
        try {
            // 获取任务
            val taskDO = taskMapper.selectById(taskId)
                ?: return null
            
            // 转换为模型对象
            return convertToTask(taskDO)
        } catch (e: Exception) {
            logger.error("获取任务失败: {}", taskId, e)
            return null
        }
    }
    
    override fun getAllTasks(): List<ScheduledTask> {
        try {
            // 获取所有任务
            val taskDOs = taskMapper.selectList(null)
            
            // 转换为模型对象
            return taskDOs.map { convertToTask(it) }
        } catch (e: Exception) {
            logger.error("获取所有任务失败", e)
            return emptyList()
        }
    }
    
    override fun getTasksByGroup(group: String): List<ScheduledTask> {
        try {
            // 创建查询条件
            val wrapper = QueryWrapper<ScheduledTaskDO>()
            wrapper.eq("group", group)
            
            // 获取任务
            val taskDOs = taskMapper.selectList(wrapper)
            
            // 转换为模型对象
            return taskDOs.map { convertToTask(it) }
        } catch (e: Exception) {
            logger.error("获取分组任务失败: {}", group, e)
            return emptyList()
        }
    }
    
    override fun getTasksByStatus(status: ScheduledTaskStatus): List<ScheduledTask> {
        try {
            // 创建查询条件
            val wrapper = QueryWrapper<ScheduledTaskDO>()
            wrapper.eq("status", status.name)
            
            // 获取任务
            val taskDOs = taskMapper.selectList(wrapper)
            
            // 转换为模型对象
            return taskDOs.map { convertToTask(it) }
        } catch (e: Exception) {
            logger.error("获取状态任务失败: {}", status, e)
            return emptyList()
        }
    }
    
    override fun getTasksByTag(tag: String): List<ScheduledTask> {
        try {
            // 创建查询条件
            val wrapper = QueryWrapper<ScheduledTaskDO>()
            wrapper.like("tags", tag)
            
            // 获取任务
            val taskDOs = taskMapper.selectList(wrapper)
            
            // 转换为模型对象
            return taskDOs.map { convertToTask(it) }
        } catch (e: Exception) {
            logger.error("获取标签任务失败: {}", tag, e)
            return emptyList()
        }
    }
    
    override fun getTasksByService(serviceId: String): List<ScheduledTask> {
        try {
            // 创建查询条件
            val wrapper = QueryWrapper<ScheduledTaskDO>()
            wrapper.eq("service_id", serviceId)
            
            // 获取任务
            val taskDOs = taskMapper.selectList(wrapper)
            
            // 转换为模型对象
            return taskDOs.map { convertToTask(it) }
        } catch (e: Exception) {
            logger.error("获取服务任务失败: {}", serviceId, e)
            return emptyList()
        }
    }
    
    override fun getTasksByUser(userId: Long): List<ScheduledTask> {
        try {
            // 创建查询条件
            val wrapper = QueryWrapper<ScheduledTaskDO>()
            wrapper.eq("create_user_id", userId)
            
            // 获取任务
            val taskDOs = taskMapper.selectList(wrapper)
            
            // 转换为模型对象
            return taskDOs.map { convertToTask(it) }
        } catch (e: Exception) {
            logger.error("获取用户任务失败: {}", userId, e)
            return emptyList()
        }
    }
    
    override fun getTasksByPage(page: Int, size: Int): List<ScheduledTask> {
        try {
            // 创建分页对象
            val pageObj = Page<ScheduledTaskDO>(page.toLong(), size.toLong())
            
            // 获取任务
            val taskDOs = taskMapper.selectPage(pageObj, null)
            
            // 转换为模型对象
            return taskDOs.records.map { convertToTask(it) }
        } catch (e: Exception) {
            logger.error("获取分页任务失败: {}, {}", page, size, e)
            return emptyList()
        }
    }
    
    override fun getTaskCount(): Long {
        try {
            // 获取任务数量
            return taskMapper.selectCount(null)
        } catch (e: Exception) {
            logger.error("获取任务数量失败", e)
            return 0
        }
    }
    
    override fun getTaskCountByStatus(status: ScheduledTaskStatus): Long {
        try {
            // 创建查询条件
            val wrapper = QueryWrapper<ScheduledTaskDO>()
            wrapper.eq("status", status.name)
            
            // 获取任务数量
            return taskMapper.selectCount(wrapper)
        } catch (e: Exception) {
            logger.error("获取状态任务数量失败: {}", status, e)
            return 0
        }
    }
    
    override fun getTaskCountByGroup(group: String): Long {
        try {
            // 创建查询条件
            val wrapper = QueryWrapper<ScheduledTaskDO>()
            wrapper.eq("group", group)
            
            // 获取任务数量
            return taskMapper.selectCount(wrapper)
        } catch (e: Exception) {
            logger.error("获取分组任务数量失败: {}", group, e)
            return 0
        }
    }
    
    @Transactional
    override fun saveExecution(execution: TaskExecution): String {
        try {
            // 如果ID为空，生成新ID
            if (execution.id.isBlank()) {
                execution.id = UUID.randomUUID().toString()
            }
            
            // 转换为DO对象
            val executionDO = convertToExecutionDO(execution)
            
            // 保存执行记录
            executionMapper.insert(executionDO)
            
            return execution.id
        } catch (e: Exception) {
            logger.error("保存执行记录失败: {}", execution.taskId, e)
            throw e
        }
    }
    
    @Transactional
    override fun updateExecution(execution: TaskExecution): Boolean {
        try {
            // 检查执行记录是否存在
            val existingExecution = executionMapper.selectById(execution.id)
                ?: return false
            
            // 转换为DO对象
            val executionDO = convertToExecutionDO(execution)
            
            // 更新执行记录
            executionMapper.updateById(executionDO)
            
            return true
        } catch (e: Exception) {
            logger.error("更新执行记录失败: {}", execution.id, e)
            return false
        }
    }
    
    override fun getExecution(executionId: String): TaskExecution? {
        try {
            // 获取执行记录
            val executionDO = executionMapper.selectById(executionId)
                ?: return null
            
            // 转换为模型对象
            return convertToExecution(executionDO)
        } catch (e: Exception) {
            logger.error("获取执行记录失败: {}", executionId, e)
            return null
        }
    }
    
    override fun getExecutionsByTask(taskId: String): List<TaskExecution> {
        try {
            // 创建查询条件
            val wrapper = QueryWrapper<TaskExecutionDO>()
            wrapper.eq("task_id", taskId)
            
            // 获取执行记录
            val executionDOs = executionMapper.selectList(wrapper)
            
            // 转换为模型对象
            return executionDOs.map { convertToExecution(it) }
        } catch (e: Exception) {
            logger.error("获取任务执行记录失败: {}", taskId, e)
            return emptyList()
        }
    }
    
    override fun getExecutionsByStatus(status: ExecutionStatus): List<TaskExecution> {
        try {
            // 创建查询条件
            val wrapper = QueryWrapper<TaskExecutionDO>()
            wrapper.eq("status", status.name)
            
            // 获取执行记录
            val executionDOs = executionMapper.selectList(wrapper)
            
            // 转换为模型对象
            return executionDOs.map { convertToExecution(it) }
        } catch (e: Exception) {
            logger.error("获取状态执行记录失败: {}", status, e)
            return emptyList()
        }
    }
    
    override fun getExecutionsByPage(page: Int, size: Int): List<TaskExecution> {
        try {
            // 创建分页对象
            val pageObj = Page<TaskExecutionDO>(page.toLong(), size.toLong())
            
            // 获取执行记录
            val executionDOs = executionMapper.selectPage(pageObj, null)
            
            // 转换为模型对象
            return executionDOs.records.map { convertToExecution(it) }
        } catch (e: Exception) {
            logger.error("获取分页执行记录失败: {}, {}", page, size, e)
            return emptyList()
        }
    }
    
    override fun getExecutionCount(): Long {
        try {
            // 获取执行记录数量
            return executionMapper.selectCount(null)
        } catch (e: Exception) {
            logger.error("获取执行记录数量失败", e)
            return 0
        }
    }
    
    override fun getExecutionCountByStatus(status: ExecutionStatus): Long {
        try {
            // 创建查询条件
            val wrapper = QueryWrapper<TaskExecutionDO>()
            wrapper.eq("status", status.name)
            
            // 获取执行记录数量
            return executionMapper.selectCount(wrapper)
        } catch (e: Exception) {
            logger.error("获取状态执行记录数量失败: {}", status, e)
            return 0
        }
    }
    
    override fun getExecutionCountByTask(taskId: String): Long {
        try {
            // 创建查询条件
            val wrapper = QueryWrapper<TaskExecutionDO>()
            wrapper.eq("task_id", taskId)
            
            // 获取执行记录数量
            return executionMapper.selectCount(wrapper)
        } catch (e: Exception) {
            logger.error("获取任务执行记录数量失败: {}", taskId, e)
            return 0
        }
    }
    
    /**
     * 转换为任务DO对象
     */
    private fun convertToTaskDO(task: ScheduledTask): ScheduledTaskDO {
        val taskDO = ScheduledTaskDO()
        
        taskDO.id = task.id
        taskDO.name = task.name
        taskDO.description = task.description
        taskDO.serviceId = task.serviceId
        taskDO.serviceName = task.serviceName
        taskDO.parameters = objectMapper.writeValueAsString(task.parameters)
        taskDO.cronExpression = task.cronExpression
        taskDO.enabled = task.enabled
        taskDO.createTime = task.createTime
        taskDO.updateTime = task.updateTime
        taskDO.createUserId = task.createUserId
        taskDO.lastExecuteTime = task.lastExecuteTime
        taskDO.nextExecuteTime = task.nextExecuteTime
        taskDO.executeCount = task.executeCount
        taskDO.successCount = task.successCount
        taskDO.failCount = task.failCount
        taskDO.lastExecuteResult = task.lastExecuteResult
        taskDO.lastExecuteMessage = task.lastExecuteMessage
        taskDO.lastExecuteDuration = task.lastExecuteDuration
        taskDO.status = task.status.name
        taskDO.tags = objectMapper.writeValueAsString(task.tags)
        taskDO.timeout = task.timeout
        taskDO.retryCount = task.retryCount
        taskDO.retryInterval = task.retryInterval
        taskDO.allowConcurrent = task.allowConcurrent
        taskDO.sendNotification = task.sendNotification
        taskDO.notificationType = task.notificationType
        taskDO.notificationReceivers = objectMapper.writeValueAsString(task.notificationReceivers)
        taskDO.priority = task.priority
        taskDO.group = task.group
        
        return taskDO
    }
    
    /**
     * 转换为任务模型对象
     */
    private fun convertToTask(taskDO: ScheduledTaskDO): ScheduledTask {
        val task = ScheduledTask()
        
        task.id = taskDO.id
        task.name = taskDO.name
        task.description = taskDO.description ?: ""
        task.serviceId = taskDO.serviceId
        task.serviceName = taskDO.serviceName ?: ""
        task.parameters = if (taskDO.parameters.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(taskDO.parameters, Map::class.java) as Map<String, Any?>
        }
        task.cronExpression = taskDO.cronExpression
        task.enabled = taskDO.enabled ?: true
        task.createTime = taskDO.createTime ?: 0
        task.updateTime = taskDO.updateTime ?: 0
        task.createUserId = taskDO.createUserId ?: 0
        task.lastExecuteTime = taskDO.lastExecuteTime ?: 0
        task.nextExecuteTime = taskDO.nextExecuteTime ?: 0
        task.executeCount = taskDO.executeCount ?: 0
        task.successCount = taskDO.successCount ?: 0
        task.failCount = taskDO.failCount ?: 0
        task.lastExecuteResult = taskDO.lastExecuteResult ?: false
        task.lastExecuteMessage = taskDO.lastExecuteMessage ?: ""
        task.lastExecuteDuration = taskDO.lastExecuteDuration ?: 0
        task.status = if (taskDO.status.isNullOrEmpty()) {
            ScheduledTaskStatus.PENDING
        } else {
            ScheduledTaskStatus.valueOf(taskDO.status!!)
        }
        task.tags = if (taskDO.tags.isNullOrEmpty()) {
            emptyList()
        } else {
            objectMapper.readValue(taskDO.tags, List::class.java) as List<String>
        }
        task.timeout = taskDO.timeout ?: 60000
        task.retryCount = taskDO.retryCount ?: 0
        task.retryInterval = taskDO.retryInterval ?: 60
        task.allowConcurrent = taskDO.allowConcurrent ?: false
        task.sendNotification = taskDO.sendNotification ?: false
        task.notificationType = taskDO.notificationType ?: ""
        task.notificationReceivers = if (taskDO.notificationReceivers.isNullOrEmpty()) {
            emptyList()
        } else {
            objectMapper.readValue(taskDO.notificationReceivers, List::class.java) as List<String>
        }
        task.priority = taskDO.priority ?: 5
        task.group = taskDO.group ?: "default"
        
        return task
    }
    
    /**
     * 转换为执行记录DO对象
     */
    private fun convertToExecutionDO(execution: TaskExecution): TaskExecutionDO {
        val executionDO = TaskExecutionDO()
        
        executionDO.id = execution.id
        executionDO.taskId = execution.taskId
        executionDO.taskName = execution.taskName
        executionDO.serviceId = execution.serviceId
        executionDO.serviceName = execution.serviceName
        executionDO.parameters = objectMapper.writeValueAsString(execution.parameters)
        executionDO.startTime = execution.startTime
        executionDO.endTime = execution.endTime
        executionDO.duration = execution.duration
        executionDO.status = execution.status.name
        executionDO.result = if (execution.result == null) null else objectMapper.writeValueAsString(execution.result)
        executionDO.errorMessage = execution.errorMessage
        executionDO.executorNode = execution.executorNode
        executionDO.retryCount = execution.retryCount
        executionDO.triggerType = execution.triggerType.name
        executionDO.triggerId = execution.triggerId
        
        return executionDO
    }
    
    /**
     * 转换为执行记录模型对象
     */
    private fun convertToExecution(executionDO: TaskExecutionDO): TaskExecution {
        val execution = TaskExecution(
            id = executionDO.id,
            taskId = executionDO.taskId,
            taskName = executionDO.taskName ?: "",
            serviceId = executionDO.serviceId ?: "",
            serviceName = executionDO.serviceName ?: "",
            parameters = if (executionDO.parameters.isNullOrEmpty()) {
                emptyMap()
            } else {
                objectMapper.readValue(executionDO.parameters, Map::class.java) as Map<String, Any?>
            },
            startTime = executionDO.startTime ?: 0,
            endTime = executionDO.endTime ?: 0,
            duration = executionDO.duration ?: 0,
            status = if (executionDO.status.isNullOrEmpty()) {
                ExecutionStatus.PENDING
            } else {
                ExecutionStatus.valueOf(executionDO.status!!)
            },
            result = if (executionDO.result.isNullOrEmpty()) {
                null
            } else {
                objectMapper.readValue(executionDO.result, Map::class.java)
            },
            errorMessage = executionDO.errorMessage ?: "",
            executorNode = executionDO.executorNode ?: "",
            retryCount = executionDO.retryCount ?: 0,
            triggerType = if (executionDO.triggerType.isNullOrEmpty()) {
                TriggerType.SCHEDULED
            } else {
                TriggerType.valueOf(executionDO.triggerType!!)
            },
            triggerId = executionDO.triggerId ?: ""
        )
        
        return execution
    }
}
