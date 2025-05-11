package ai.magicdb.dataservice.core.orchestration

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.ServiceFlowExecutor
import ai.magicdb.dataservice.api.ServiceFlowRepository
import ai.magicdb.dataservice.api.ServiceOrchestrator
import ai.magicdb.dataservice.api.model.FlowExecution
import ai.magicdb.dataservice.api.model.ServiceFlow
import ai.magicdb.dataservice.api.model.ServiceFlowGroup
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 默认服务编排器实现
 *
 * @author magicdb
 */
@Component
class DefaultServiceOrchestrator(
    private val flowRepository: ServiceFlowRepository,
    private val flowExecutor: ServiceFlowExecutor,
    private val serviceRepository: DataServiceRepository,
    private val serviceExecutor: DataServiceExecutor,
    private val objectMapper: ObjectMapper
) : ServiceOrchestrator {
    private val logger = LoggerFactory.getLogger(DefaultServiceOrchestrator::class.java)
    
    override fun createFlow(flow: ServiceFlow): String {
        try {
            // 创建流程
            return flowRepository.saveFlow(flow)
        } catch (e: Exception) {
            logger.error("创建流程失败: {}", e.message, e)
            throw e
        }
    }
    
    override fun updateFlow(flow: ServiceFlow): Boolean {
        try {
            // 更新流程
            val flowId = flowRepository.saveFlow(flow)
            return flowId.isNotEmpty()
        } catch (e: Exception) {
            logger.error("更新流程失败: {}", e.message, e)
            return false
        }
    }
    
    override fun getFlow(flowId: String): ServiceFlow? {
        try {
            return flowRepository.getFlow(flowId)
        } catch (e: Exception) {
            logger.error("获取流程失败: {}", flowId, e)
            return null
        }
    }
    
    override fun getFlows(
        groupId: String?,
        name: String?,
        status: String?,
        limit: Int,
        offset: Int
    ): List<ServiceFlow> {
        try {
            return flowRepository.getFlows(groupId, name, status, limit, offset)
        } catch (e: Exception) {
            logger.error("获取流程列表失败", e)
            return emptyList()
        }
    }
    
    override fun deleteFlow(flowId: String): Boolean {
        try {
            return flowRepository.deleteFlow(flowId)
        } catch (e: Exception) {
            logger.error("删除流程失败: {}", flowId, e)
            return false
        }
    }
    
    override fun createFlowGroup(group: ServiceFlowGroup): String {
        try {
            // 创建分组
            return flowRepository.saveFlowGroup(group)
        } catch (e: Exception) {
            logger.error("创建流程分组失败: {}", e.message, e)
            throw e
        }
    }
    
    override fun updateFlowGroup(group: ServiceFlowGroup): Boolean {
        try {
            // 更新分组
            val groupId = flowRepository.saveFlowGroup(group)
            return groupId.isNotEmpty()
        } catch (e: Exception) {
            logger.error("更新流程分组失败: {}", e.message, e)
            return false
        }
    }
    
    override fun getFlowGroup(groupId: String): ServiceFlowGroup? {
        try {
            return flowRepository.getFlowGroup(groupId)
        } catch (e: Exception) {
            logger.error("获取流程分组失败: {}", groupId, e)
            return null
        }
    }
    
    override fun getFlowGroups(
        parentId: String?,
        name: String?,
        limit: Int,
        offset: Int
    ): List<ServiceFlowGroup> {
        try {
            return flowRepository.getFlowGroups(parentId, name, limit, offset)
        } catch (e: Exception) {
            logger.error("获取流程分组列表失败", e)
            return emptyList()
        }
    }
    
    override fun deleteFlowGroup(groupId: String): Boolean {
        try {
            return flowRepository.deleteFlowGroup(groupId)
        } catch (e: Exception) {
            logger.error("删除流程分组失败: {}", groupId, e)
            return false
        }
    }
    
    override fun executeFlow(
        flowId: String,
        parameters: Map<String, Any?>,
        async: Boolean
    ): FlowExecution {
        try {
            // 获取流程
            val flow = flowRepository.getFlow(flowId)
                ?: throw IllegalArgumentException("流程不存在: $flowId")
            
            // 执行流程
            val execution = flowExecutor.executeFlow(flow, parameters, async)
            
            // 保存执行记录
            flowRepository.saveFlowExecution(execution)
            
            return execution
        } catch (e: Exception) {
            logger.error("执行流程失败: {}", e.message, e)
            throw e
        }
    }
    
    override fun getFlowExecution(executionId: String): FlowExecution? {
        try {
            return flowRepository.getFlowExecution(executionId)
        } catch (e: Exception) {
            logger.error("获取流程执行记录失败: {}", executionId, e)
            return null
        }
    }
    
    override fun getFlowExecutions(
        flowId: String?,
        status: String?,
        startTime: Long?,
        endTime: Long?,
        limit: Int,
        offset: Int
    ): List<FlowExecution> {
        try {
            return flowRepository.getFlowExecutions(flowId, status, startTime, endTime, limit, offset)
        } catch (e: Exception) {
            logger.error("获取流程执行记录列表失败", e)
            return emptyList()
        }
    }
    
    override fun pauseFlowExecution(executionId: String): Boolean {
        try {
            // 暂停执行
            val success = flowExecutor.pauseExecution(executionId)
            
            if (success) {
                // 更新执行记录
                val execution = flowExecutor.getExecutionStatus(executionId)
                if (execution != null) {
                    flowRepository.saveFlowExecution(execution)
                }
            }
            
            return success
        } catch (e: Exception) {
            logger.error("暂停流程执行失败: {}", e.message, e)
            return false
        }
    }
    
    override fun resumeFlowExecution(executionId: String): Boolean {
        try {
            // 恢复执行
            val success = flowExecutor.resumeExecution(executionId)
            
            if (success) {
                // 更新执行记录
                val execution = flowExecutor.getExecutionStatus(executionId)
                if (execution != null) {
                    flowRepository.saveFlowExecution(execution)
                }
            }
            
            return success
        } catch (e: Exception) {
            logger.error("恢复流程执行失败: {}", e.message, e)
            return false
        }
    }
    
    override fun cancelFlowExecution(executionId: String): Boolean {
        try {
            // 取消执行
            val success = flowExecutor.cancelExecution(executionId)
            
            if (success) {
                // 更新执行记录
                val execution = flowExecutor.getExecutionStatus(executionId)
                if (execution != null) {
                    flowRepository.saveFlowExecution(execution)
                }
            }
            
            return success
        } catch (e: Exception) {
            logger.error("取消流程执行失败: {}", e.message, e)
            return false
        }
    }
    
    override fun getFlowExecutionResult(executionId: String): Any? {
        try {
            return flowExecutor.getExecutionResult(executionId)
        } catch (e: Exception) {
            logger.error("获取流程执行结果失败: {}", executionId, e)
            return null
        }
    }
    
    override fun deleteFlowExecution(executionId: String): Boolean {
        try {
            return flowRepository.deleteFlowExecution(executionId)
        } catch (e: Exception) {
            logger.error("删除流程执行记录失败: {}", executionId, e)
            return false
        }
    }
    
    override fun clearFlowExecutions(
        flowId: String?,
        before: Long?
    ): Int {
        try {
            return flowRepository.clearFlowExecutions(flowId, before)
        } catch (e: Exception) {
            logger.error("清除流程执行记录失败", e)
            return 0
        }
    }
}
