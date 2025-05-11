package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.ServiceFlowRepository
import ai.magicdb.dataservice.api.model.FlowExecution
import ai.magicdb.dataservice.api.model.ServiceFlow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

/**
 * 内存服务流程存储库实现
 *
 * @author magicdb
 */
@Repository
class MemoryServiceFlowRepository : ServiceFlowRepository {
    private val logger = LoggerFactory.getLogger(MemoryServiceFlowRepository::class.java)
    
    // 流程存储
    private val flows = ConcurrentHashMap<String, ServiceFlow>()
    
    // 执行记录存储
    private val executions = ConcurrentHashMap<String, FlowExecution>()
    
    override fun saveFlow(flow: ServiceFlow): String {
        try {
            // 保存流程
            flows[flow.id] = flow
            
            return flow.id
        } catch (e: Exception) {
            logger.error("保存流程失败: {}", flow.id, e)
            throw e
        }
    }
    
    override fun updateFlow(flow: ServiceFlow): Boolean {
        try {
            // 检查流程是否存在
            if (!flows.containsKey(flow.id)) {
                return false
            }
            
            // 更新流程
            flows[flow.id] = flow
            
            return true
        } catch (e: Exception) {
            logger.error("更新流程失败: {}", flow.id, e)
            return false
        }
    }
    
    override fun deleteFlow(flowId: String): Boolean {
        try {
            // 删除流程
            return flows.remove(flowId) != null
        } catch (e: Exception) {
            logger.error("删除流程失败: {}", flowId, e)
            return false
        }
    }
    
    override fun getFlow(flowId: String): ServiceFlow? {
        try {
            // 获取流程
            return flows[flowId]
        } catch (e: Exception) {
            logger.error("获取流程失败: {}", flowId, e)
            return null
        }
    }
    
    override fun getAllFlows(): List<ServiceFlow> {
        try {
            // 获取所有流程
            return flows.values.toList()
        } catch (e: Exception) {
            logger.error("获取所有流程失败", e)
            return emptyList()
        }
    }
    
    override fun saveExecution(execution: FlowExecution): String {
        try {
            // 保存执行记录
            executions[execution.id] = execution
            
            return execution.id
        } catch (e: Exception) {
            logger.error("保存执行记录失败: {}", execution.id, e)
            throw e
        }
    }
    
    override fun updateExecution(execution: FlowExecution): Boolean {
        try {
            // 检查执行记录是否存在
            if (!executions.containsKey(execution.id)) {
                return false
            }
            
            // 更新执行记录
            executions[execution.id] = execution
            
            return true
        } catch (e: Exception) {
            logger.error("更新执行记录失败: {}", execution.id, e)
            return false
        }
    }
    
    override fun deleteExecution(executionId: String): Boolean {
        try {
            // 删除执行记录
            return executions.remove(executionId) != null
        } catch (e: Exception) {
            logger.error("删除执行记录失败: {}", executionId, e)
            return false
        }
    }
    
    override fun getExecution(executionId: String): FlowExecution? {
        try {
            // 获取执行记录
            return executions[executionId]
        } catch (e: Exception) {
            logger.error("获取执行记录失败: {}", executionId, e)
            return null
        }
    }
    
    override fun getAllExecutions(): List<FlowExecution> {
        try {
            // 获取所有执行记录
            return executions.values.toList()
        } catch (e: Exception) {
            logger.error("获取所有执行记录失败", e)
            return emptyList()
        }
    }
    
    override fun getExecutionsByFlowId(flowId: String): List<FlowExecution> {
        try {
            // 获取流程的所有执行记录
            return executions.values.filter { it.flowId == flowId }
        } catch (e: Exception) {
            logger.error("获取流程执行记录失败: {}", flowId, e)
            return emptyList()
        }
    }
}
