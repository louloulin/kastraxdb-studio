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
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.util.*

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
    
    override fun saveTask(task: ScheduledTask): String {
        try {
            // 如果没有ID，生成一个新的ID
            if (task.id.isEmpty()) {
                task.id = UUID.randomUUID().toString()
                task.createTime = System.currentTimeMillis()
            }
            
            // 更新时间
            task.updateTime = System.currentTimeMillis()
            
            // 转换为DO对象
            val taskDO = convertToTaskDO(task)
            
            // 保存或更新
            val existingTask = taskMapper.selectById(task.id)
            if (existingTask == null) {
                taskMapper.insert(taskDO)
            } else {
                taskMapper.updateById(taskDO)
            }
            
            return task.id
        } catch (e: Exception) {
            logger.error("保存任务失败: {}", task.id, e)
            throw e
        }
    }
    
    override fun updateTask(task: ScheduledTask): Boolean {
        try {
            // 更新时间
            task.updateTime = System.currentTimeMillis()
            
            // 转换为DO对象
            val taskDO = convertToTaskDO(task)
            
            // 更新
            val result = taskMapper.updateById(taskDO)
            return result > 0
        } catch (e: Exception) {
            logger.error("更新任务失败: {}", task.id, e)
            return false
        }
    }
    
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
            // 查询任务
            val taskDO = taskMapper.selectById(taskId) ?: return null
            
            // 转换为模型对象
            return convertToTask(taskDO)
        } catch (e: Exception) {
            logger.error("获取任务失败: {}", taskId, e)
            return null
        }
    }
    
    override fun getAllTasks(status: TaskStatus?, group: String?): List<ScheduledTask> {
        try {
            // 查询任务
            val taskDOs = when {
                status != null && group != null -> {
                    taskMapper.selectByStatusAndGroup(status.name, group)
                }
                status != null -> {
                    taskMapper.selectByStatus(status.name)
                }
                group != null -> {
                    taskMapper.selectByGroup(group)
                }
                else -> {
                    taskMapper.selectList(null)
                }
            }
            
            // 转换为模型对象
            return taskDOs.map { convertToTask(it) }
        } catch (e: Exception) {
            logger.error("获取所有任务失败", e)
            return emptyList()
        }
    }
    
    override fun getTasksByService(serviceId: String): List<ScheduledTask> {
        try {
            // 查询任务
            val taskDOs = taskMapper.selectByServiceId(serviceId)
            
            // 转换为模型对象
            return taskDOs.map { convertToTask(it) }
        } catch (e: Exception) {
            logger.error("获取服务任务失败: {}", serviceId, e)
            return emptyList()
        }
    }
    
    override fun updateTaskStatus(taskId: String, status: TaskStatus): Boolean {
        try {
            // 更新任务状态
            val result = taskMapper.updateStatus(taskId, status.name, System.currentTimeMillis())
            return result > 0
        } catch (e: Exception) {
            logger.error("更新任务状态失败: {}", taskId, e)
            return false
        }
    }
    
    override fun updateTaskExecutionInfo(
        taskId: String,
        lastExecuteTime: Long,
        nextExecuteTime: Long,
        lastExecuteResult: Boolean,
        lastExecuteMessage: String?,
        lastExecuteDuration: Long
    ): Boolean {
        try {
            // 更新任务执行信息
            val result = taskMapper.updateExecutionInfo(
                taskId,
                lastExecuteTime,
                nextExecuteTime,
                lastExecuteResult,
                lastExecuteMessage,
                lastExecuteDuration,
                System.currentTimeMillis()
            )
            return result > 0
        } catch (e: Exception) {
            logger.error("更新任务执行信息失败: {}", taskId, e)
            return false
        }
    }
    
    override fun updateTaskEnabled(taskId: String, enabled: Boolean): Boolean {
        try {
            // 更新任务启用状态
            val result = taskMapper.updateEnabled(taskId, enabled, System.currentTimeMillis())
            return result > 0
        } catch (e: Exception) {
            logger.error("更新任务启用状态失败: {}", taskId, e)
            return false
        }
    }
    
    override fun saveTaskExecution(execution: TaskExecution): String {
        try {
            // 如果没有ID，生成一个新的ID
            if (execution.id.isEmpty()) {
                execution.id = UUID.randomUUID().toString()
            }
            
            // 转换为DO对象
            val executionDO = convertToExecutionDO(execution)
            
            // 保存或更新
            val existingExecution = executionMapper.selectById(execution.id)
            if (existingExecution == null) {
                executionMapper.insert(executionDO)
            } else {
                executionMapper.updateById(executionDO)
            }
            
            return execution.id
        } catch (e: Exception) {
            logger.error("保存任务执行记录失败: {}", execution.id, e)
            throw e
        }
    }
    
    override fun updateTaskExecutionStatus(executionId: String, status: ExecutionStatus): Boolean {
        try {
            // 更新执行记录状态
            val result = executionMapper.updateStatus(executionId, status.name)
            return result > 0
        } catch (e: Exception) {
            logger.error("更新任务执行记录状态失败: {}", executionId, e)
            return false
        }
    }
    
    override fun updateTaskExecutionResult(
        executionId: String,
        status: ExecutionStatus,
        result: Any?,
        errorMessage: String?,
        endTime: Long,
        duration: Long
    ): Boolean {
        try {
            // 转换结果为JSON
            val resultJson = if (result != null) {
                objectMapper.writeValueAsString(result)
            } else {
                null
            }
            
            // 更新执行记录结果
            val updateResult = executionMapper.updateResult(
                executionId,
                status.name,
                resultJson,
                errorMessage,
                endTime,
                duration
            )
            return updateResult > 0
        } catch (e: Exception) {
            logger.error("更新任务执行记录结果失败: {}", executionId, e)
            return false
        }
    }
    
    override fun getTaskExecution(executionId: String): TaskExecution? {
        try {
            // 查询执行记录
            val executionDO = executionMapper.selectById(executionId) ?: return null
            
            // 转换为模型对象
            return convertToExecution(executionDO)
        } catch (e: Exception) {
            logger.error("获取任务执行记录失败: {}", executionId, e)
            return null
        }
    }
    
    override fun getTaskExecutions(taskId: String, limit: Int, offset: Int): List<TaskExecution> {
        try {
            // 查询执行记录
            val executionDOs = executionMapper.selectByTaskId(taskId, limit, offset)
            
            // 转换为模型对象
            return executionDOs.map { convertToExecution(it) }
        } catch (e: Exception) {
            logger.error("获取任务执行记录列表失败: {}", taskId, e)
            return emptyList()
        }
    }
    
    override fun getLastTaskExecution(taskId: String): TaskExecution? {
        try {
            // 查询最后一次执行记录
            val executionDO = executionMapper.selectLastExecution(taskId) ?: return null
            
            // 转换为模型对象
            return convertToExecution(executionDO)
        } catch (e: Exception) {
            logger.error("获取任务最后一次执行记录失败: {}", taskId, e)
            return null
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
        taskDO.parameters = if (task.parameters.isEmpty()) null else objectMapper.writeValueAsString(task.parameters)
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
        taskDO.tags = if (task.tags.isEmpty()) null else objectMapper.writeValueAsString(task.tags)
        taskDO.timeout = task.timeout
        taskDO.retryCount = task.retryCount
        taskDO.retryInterval = task.retryInterval
        taskDO.allowConcurrent = task.allowConcurrent
        taskDO.sendNotification = task.sendNotification
        taskDO.notificationType = task.notificationType
        taskDO.notificationReceivers = if (task.notificationReceivers.isEmpty()) null else objectMapper.writeValueAsString(task.notificationReceivers)
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
            TaskStatus.WAITING
        } else {
            TaskStatus.valueOf(taskDO.status!!)
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
        executionDO.parameters = if (execution.parameters.isEmpty()) null else objectMapper.writeValueAsString(execution.parameters)
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
        val execution = TaskExecution()
        execution.id = executionDO.id
        execution.taskId = executionDO.taskId
        execution.taskName = executionDO.taskName ?: ""
        execution.serviceId = executionDO.serviceId
        execution.serviceName = executionDO.serviceName ?: ""
        execution.parameters = if (executionDO.parameters.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(executionDO.parameters, Map::class.java) as Map<String, Any?>
        }
        execution.startTime = executionDO.startTime ?: 0
        execution.endTime = executionDO.endTime ?: 0
        execution.duration = executionDO.duration ?: 0
        execution.status = if (executionDO.status.isNullOrEmpty()) {
            ExecutionStatus.PENDING
        } else {
            ExecutionStatus.valueOf(executionDO.status!!)
        }
        execution.result = if (executionDO.result.isNullOrEmpty()) {
            null
        } else {
            objectMapper.readValue(executionDO.result, Any::class.java)
        }
        execution.errorMessage = executionDO.errorMessage ?: ""
        execution.executorNode = executionDO.executorNode ?: ""
        execution.retryCount = executionDO.retryCount ?: 0
        execution.triggerType = if (executionDO.triggerType.isNullOrEmpty()) {
            TriggerType.SCHEDULED
        } else {
            TriggerType.valueOf(executionDO.triggerType!!)
        }
        execution.triggerId = executionDO.triggerId ?: ""
        return execution
    }
}
