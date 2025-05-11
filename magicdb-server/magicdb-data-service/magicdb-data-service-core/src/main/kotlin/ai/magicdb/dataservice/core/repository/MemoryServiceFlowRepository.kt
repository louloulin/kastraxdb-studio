package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.ServiceFlowRepository
import ai.magicdb.dataservice.api.model.FlowExecution
import ai.magicdb.dataservice.api.model.ServiceFlow
import ai.magicdb.dataservice.api.model.ServiceFlowGroup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID
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

    // 流程分组存储
    private val flowGroups = ConcurrentHashMap<String, ServiceFlowGroup>()

    // 流程执行记录存储
    private val flowExecutions = ConcurrentHashMap<String, FlowExecution>()

    override fun saveFlow(flow: ServiceFlow): String {
        try {
            // 生成ID
            val id = flow.id.ifEmpty { UUID.randomUUID().toString() }

            // 创建新流程
            val newFlow = if (flow.id.isEmpty()) {
                flow.copy(
                    id = id,
                    createTime = System.currentTimeMillis(),
                    updateTime = System.currentTimeMillis()
                )
            } else {
                flow.copy(updateTime = System.currentTimeMillis())
            }

            // 保存流程
            flows[id] = newFlow

            return id
        } catch (e: Exception) {
            logger.error("保存流程失败: {}", e.message, e)
            throw e
        }
    }

    override fun getFlow(flowId: String): ServiceFlow? {
        try {
            return flows[flowId]
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
            // 过滤流程
            val filteredFlows = flows.values.filter { flow ->
                (groupId == null || flow.group == groupId) &&
                (name == null || flow.name.contains(name, ignoreCase = true)) &&
                (status == null || flow.enabled.toString() == status)
            }

            // 排序流程（按更新时间降序）
            val sortedFlows = filteredFlows.sortedByDescending { it.updateTime }

            // 分页
            return sortedFlows.drop(offset).take(limit)
        } catch (e: Exception) {
            logger.error("获取流程列表失败", e)
            return emptyList()
        }
    }

    override fun deleteFlow(flowId: String): Boolean {
        try {
            return flows.remove(flowId) != null
        } catch (e: Exception) {
            logger.error("删除流程失败: {}", flowId, e)
            return false
        }
    }

    override fun saveFlowGroup(group: ServiceFlowGroup): String {
        try {
            // 生成ID
            val id = group.id.ifEmpty { UUID.randomUUID().toString() }

            // 创建新分组
            val newGroup = if (group.id.isEmpty()) {
                group.copy(
                    id = id,
                    createTime = LocalDateTime.now(),
                    updateTime = LocalDateTime.now()
                )
            } else {
                group.copy(updateTime = LocalDateTime.now())
            }

            // 保存分组
            flowGroups[id] = newGroup

            return id
        } catch (e: Exception) {
            logger.error("保存流程分组失败: {}", e.message, e)
            throw e
        }
    }

    override fun getFlowGroup(groupId: String): ServiceFlowGroup? {
        try {
            return flowGroups[groupId]
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
            // 过滤分组
            val filteredGroups = flowGroups.values.filter { group ->
                (parentId == null || group.parentId == parentId) &&
                (name == null || group.name.contains(name, ignoreCase = true))
            }

            // 排序分组（按更新时间降序）
            val sortedGroups = filteredGroups.sortedByDescending { it.updateTime }

            // 分页
            return sortedGroups.drop(offset).take(limit)
        } catch (e: Exception) {
            logger.error("获取流程分组列表失败", e)
            return emptyList()
        }
    }

    override fun deleteFlowGroup(groupId: String): Boolean {
        try {
            return flowGroups.remove(groupId) != null
        } catch (e: Exception) {
            logger.error("删除流程分组失败: {}", groupId, e)
            return false
        }
    }

    override fun saveFlowExecution(execution: FlowExecution): String {
        try {
            // 生成ID
            val id = execution.id.ifEmpty { UUID.randomUUID().toString() }

            // 创建新执行记录
            val newExecution = if (execution.id.isEmpty()) {
                execution.copy(id = id)
            } else {
                execution
            }

            // 保存执行记录
            flowExecutions[id] = newExecution

            return id
        } catch (e: Exception) {
            logger.error("保存流程执行记录失败: {}", e.message, e)
            throw e
        }
    }

    override fun getFlowExecution(executionId: String): FlowExecution? {
        try {
            return flowExecutions[executionId]
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
            // 过滤执行记录
            val filteredExecutions = flowExecutions.values.filter { execution ->
                (flowId == null || execution.flowId == flowId) &&
                (status == null || execution.status.toString() == status) &&
                (startTime == null || execution.startTime >= startTime) &&
                (endTime == null || execution.endTime <= endTime)
            }

            // 排序执行记录（按开始时间降序）
            val sortedExecutions = filteredExecutions.sortedByDescending { it.startTime }

            // 分页
            return sortedExecutions.drop(offset).take(limit)
        } catch (e: Exception) {
            logger.error("获取流程执行记录列表失败", e)
            return emptyList()
        }
    }

    override fun deleteFlowExecution(executionId: String): Boolean {
        try {
            return flowExecutions.remove(executionId) != null
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
            // 获取要删除的执行记录ID
            val executionsToRemove = flowExecutions.values
                .filter { execution ->
                    (flowId == null || execution.flowId == flowId) &&
                    (before == null || execution.endTime < before)
                }
                .map { it.id }

            // 删除执行记录
            executionsToRemove.forEach { flowExecutions.remove(it) }

            return executionsToRemove.size
        } catch (e: Exception) {
            logger.error("清除流程执行记录失败", e)
            return 0
        }
    }
}
