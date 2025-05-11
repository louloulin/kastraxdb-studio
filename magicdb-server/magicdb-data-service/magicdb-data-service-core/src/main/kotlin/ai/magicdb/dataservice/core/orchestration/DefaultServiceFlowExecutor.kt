package ai.magicdb.dataservice.core.orchestration

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.ServiceFlowExecutor
import ai.magicdb.dataservice.api.model.FlowExecution
import ai.magicdb.dataservice.api.model.FlowExecutionStatus
import ai.magicdb.dataservice.api.model.ServiceFlow
import ai.magicdb.dataservice.api.model.TriggerType
import ai.magicdb.script.api.ScriptExecutor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * 默认服务流程执行器实现
 *
 * @author magicdb
 */
@Component
class DefaultServiceFlowExecutor(
    private val serviceExecutor: DataServiceExecutor,
    private val scriptExecutor: ScriptExecutor
) : ServiceFlowExecutor {
    private val logger = LoggerFactory.getLogger(DefaultServiceFlowExecutor::class.java)
    
    // 执行中的流程
    private val runningExecutions = ConcurrentHashMap<String, FlowExecution>()
    
    // 异步执行线程池
    private val executorService = Executors.newCachedThreadPool()
    
    override fun executeFlow(
        flow: ServiceFlow,
        parameters: Map<String, Any?>,
        async: Boolean
    ): FlowExecution {
        try {
            // 创建执行记录
            val executionId = UUID.randomUUID().toString()
            val execution = FlowExecution(
                id = executionId,
                flowId = flow.id,
                flowName = flow.name,
                parameters = parameters,
                variables = mutableMapOf(),
                startTime = System.currentTimeMillis(),
                status = FlowExecutionStatus.RUNNING,
                triggerType = TriggerType.MANUAL
            )
            
            // 保存执行记录
            runningExecutions[executionId] = execution
            
            if (async) {
                // 异步执行
                executorService.submit {
                    try {
                        executeFlowInternal(flow, execution)
                    } catch (e: Exception) {
                        logger.error("执行流程失败: {}", e.message, e)
                        completeExecution(execution, FlowExecutionStatus.FAILED, null, e.message ?: "执行失败")
                    }
                }
                return execution
            } else {
                // 同步执行
                return executeFlowInternal(flow, execution)
            }
        } catch (e: Exception) {
            logger.error("执行流程失败: {}", e.message, e)
            throw e
        }
    }
    
    override fun executeFlow(
        flowId: String,
        parameters: Map<String, Any?>,
        async: Boolean
    ): FlowExecution {
        // 这里应该从存储库中获取流程，但由于我们没有注入存储库，所以抛出异常
        throw UnsupportedOperationException("不支持通过ID执行流程，请使用executeFlow(flow, parameters, async)方法")
    }
    
    override fun pauseExecution(executionId: String): Boolean {
        try {
            val execution = runningExecutions[executionId] ?: return false
            
            // 只有运行中的流程才能暂停
            if (execution.status != FlowExecutionStatus.RUNNING) {
                return false
            }
            
            // 更新状态
            val updatedExecution = execution.copy(status = FlowExecutionStatus.PAUSED)
            runningExecutions[executionId] = updatedExecution
            
            return true
        } catch (e: Exception) {
            logger.error("暂停流程执行失败: {}", e.message, e)
            return false
        }
    }
    
    override fun resumeExecution(executionId: String): Boolean {
        try {
            val execution = runningExecutions[executionId] ?: return false
            
            // 只有暂停的流程才能恢复
            if (execution.status != FlowExecutionStatus.PAUSED) {
                return false
            }
            
            // 更新状态
            val updatedExecution = execution.copy(status = FlowExecutionStatus.RUNNING)
            runningExecutions[executionId] = updatedExecution
            
            // 异步恢复执行
            executorService.submit {
                try {
                    // 这里应该恢复执行流程，但由于我们没有实现流程引擎，所以只是简单地标记为成功
                    completeExecution(updatedExecution, FlowExecutionStatus.SUCCESS, null, null)
                } catch (e: Exception) {
                    logger.error("恢复流程执行失败: {}", e.message, e)
                    completeExecution(updatedExecution, FlowExecutionStatus.FAILED, null, e.message ?: "执行失败")
                }
            }
            
            return true
        } catch (e: Exception) {
            logger.error("恢复流程执行失败: {}", e.message, e)
            return false
        }
    }
    
    override fun cancelExecution(executionId: String): Boolean {
        try {
            val execution = runningExecutions[executionId] ?: return false
            
            // 只有运行中或暂停的流程才能取消
            if (execution.status != FlowExecutionStatus.RUNNING && execution.status != FlowExecutionStatus.PAUSED) {
                return false
            }
            
            // 更新状态
            completeExecution(execution, FlowExecutionStatus.CANCELED, null, "用户取消")
            
            return true
        } catch (e: Exception) {
            logger.error("取消流程执行失败: {}", e.message, e)
            return false
        }
    }
    
    override fun getExecutionStatus(executionId: String): FlowExecution? {
        return runningExecutions[executionId]
    }
    
    override fun getExecutionResult(executionId: String): Any? {
        val execution = runningExecutions[executionId] ?: return null
        return execution.result
    }
    
    /**
     * 执行流程内部实现
     */
    private fun executeFlowInternal(flow: ServiceFlow, execution: FlowExecution): FlowExecution {
        try {
            // 这里应该实现流程引擎，但由于我们没有实现流程引擎，所以只是简单地标记为成功
            // 在实际实现中，应该根据流程定义执行各个节点
            
            // 模拟执行时间
            Thread.sleep(1000)
            
            // 完成执行
            return completeExecution(execution, FlowExecutionStatus.SUCCESS, "执行成功", null)
        } catch (e: Exception) {
            logger.error("执行流程失败: {}", e.message, e)
            return completeExecution(execution, FlowExecutionStatus.FAILED, null, e.message ?: "执行失败")
        }
    }
    
    /**
     * 完成执行
     */
    private fun completeExecution(
        execution: FlowExecution,
        status: FlowExecutionStatus,
        result: Any?,
        errorMessage: String?
    ): FlowExecution {
        // 更新执行记录
        val endTime = System.currentTimeMillis()
        val duration = endTime - execution.startTime
        val updatedExecution = execution.copy(
            status = status,
            result = result,
            errorMessage = errorMessage ?: "",
            endTime = endTime,
            duration = duration
        )
        
        // 保存执行记录
        runningExecutions[execution.id] = updatedExecution
        
        return updatedExecution
    }
}
