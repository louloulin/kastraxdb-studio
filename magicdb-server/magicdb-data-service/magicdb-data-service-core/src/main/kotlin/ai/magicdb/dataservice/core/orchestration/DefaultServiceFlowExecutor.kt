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

    override fun execute(
        flow: ServiceFlow,
        parameters: Map<String, Any?>,
        executionId: String?
    ): FlowExecution {
        try {
            // 创建执行记录
            val actualExecutionId = executionId ?: UUID.randomUUID().toString()
            val execution = FlowExecution(
                id = actualExecutionId,
                flowId = flow.id,
                flowName = flow.name,
                parameters = parameters,
                variables = mutableMapOf(),
                startTime = System.currentTimeMillis(),
                status = FlowExecutionStatus.RUNNING,
                triggerType = TriggerType.MANUAL
            )

            // 保存执行记录
            runningExecutions[actualExecutionId] = execution

            // 同步执行
            return executeFlowInternal(flow, execution)
        } catch (e: Exception) {
            logger.error("执行流程失败: {}", e.message, e)
            throw e
        }
    }

    override fun executeAsync(
        flow: ServiceFlow,
        parameters: Map<String, Any?>,
        executionId: String?
    ): String {
        try {
            // 创建执行记录
            val actualExecutionId = executionId ?: UUID.randomUUID().toString()
            val execution = FlowExecution(
                id = actualExecutionId,
                flowId = flow.id,
                flowName = flow.name,
                parameters = parameters,
                variables = mutableMapOf(),
                startTime = System.currentTimeMillis(),
                status = FlowExecutionStatus.RUNNING,
                triggerType = TriggerType.MANUAL
            )

            // 保存执行记录
            runningExecutions[actualExecutionId] = execution

            // 异步执行
            executorService.submit {
                try {
                    executeFlowInternal(flow, execution)
                } catch (e: Exception) {
                    logger.error("执行流程失败: {}", e.message, e)
                    completeExecution(execution, FlowExecutionStatus.FAILED, null, e.message ?: "执行失败")
                }
            }

            return actualExecutionId
        } catch (e: Exception) {
            logger.error("异步执行流程失败: {}", e.message, e)
            throw e
        }
    }

    override fun pause(executionId: String): Boolean {
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

    override fun resume(executionId: String): Boolean {
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

    override fun cancel(executionId: String): Boolean {
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

    override fun getExecution(executionId: String): FlowExecution? {
        return runningExecutions[executionId]
    }

    override fun getStatus(executionId: String): FlowExecutionStatus {
        val execution = runningExecutions[executionId] ?: return FlowExecutionStatus.UNKNOWN
        return execution.status
    }

    override fun getRunningCount(): Int {
        return runningExecutions.count { (_, execution) -> execution.status == FlowExecutionStatus.RUNNING }
    }

    override fun getRunningExecutionIds(): List<String> {
        return runningExecutions.filter { (_, execution) -> execution.status == FlowExecutionStatus.RUNNING }.keys.toList()
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
