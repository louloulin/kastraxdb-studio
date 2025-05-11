package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.ClusterRepository
import ai.magicdb.dataservice.api.model.ClusterNode
import ai.magicdb.dataservice.api.model.ClusterStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * 内存集群存储库实现
 *
 * @author magicdb
 */
@Repository
class MemoryClusterRepository : ClusterRepository {
    private val logger = LoggerFactory.getLogger(MemoryClusterRepository::class.java)
    
    // 节点存储
    private val nodes = ConcurrentHashMap<String, ClusterNode>()
    
    // 服务节点映射（服务ID -> 节点ID列表）
    private val serviceNodeMappings = ConcurrentHashMap<String, MutableSet<String>>()
    
    // 集群配置
    private val clusterConfig = ConcurrentHashMap<String, String>()
    
    // 集群状态
    private var clusterStatus: ClusterStatus? = null
    
    override fun saveNode(node: ClusterNode): Boolean {
        try {
            nodes[node.id] = node
            return true
        } catch (e: Exception) {
            logger.error("保存节点失败: {}", node.id, e)
            return false
        }
    }
    
    override fun updateNode(node: ClusterNode): Boolean {
        try {
            if (!nodes.containsKey(node.id)) {
                return false
            }
            
            nodes[node.id] = node
            return true
        } catch (e: Exception) {
            logger.error("更新节点失败: {}", node.id, e)
            return false
        }
    }
    
    override fun deleteNode(nodeId: String): Boolean {
        try {
            // 删除节点
            val removed = nodes.remove(nodeId) != null
            
            // 删除服务节点映射
            serviceNodeMappings.values.forEach { it.remove(nodeId) }
            
            return removed
        } catch (e: Exception) {
            logger.error("删除节点失败: {}", nodeId, e)
            return false
        }
    }
    
    override fun getNode(nodeId: String): ClusterNode? {
        try {
            return nodes[nodeId]
        } catch (e: Exception) {
            logger.error("获取节点失败: {}", nodeId, e)
            return null
        }
    }
    
    override fun getAllNodes(includeOffline: Boolean): List<ClusterNode> {
        try {
            return if (includeOffline) {
                nodes.values.toList()
            } else {
                nodes.values.filter { it.online }
            }
        } catch (e: Exception) {
            logger.error("获取所有节点失败", e)
            return emptyList()
        }
    }
    
    override fun updateNodeStatus(nodeId: String, online: Boolean): Boolean {
        try {
            val node = nodes[nodeId] ?: return false
            
            // 更新节点状态
            nodes[nodeId] = node.copy(online = online)
            
            return true
        } catch (e: Exception) {
            logger.error("更新节点状态失败: {}", nodeId, e)
            return false
        }
    }
    
    override fun updateNodeHeartbeat(nodeId: String): Boolean {
        try {
            val node = nodes[nodeId] ?: return false
            
            // 更新节点心跳时间
            nodes[nodeId] = node.copy(lastHeartbeatTime = LocalDateTime.now())
            
            return true
        } catch (e: Exception) {
            logger.error("更新节点心跳时间失败: {}", nodeId, e)
            return false
        }
    }
    
    override fun getMasterNode(): ClusterNode? {
        try {
            return nodes.values.find { it.master }
        } catch (e: Exception) {
            logger.error("获取主节点失败", e)
            return null
        }
    }
    
    override fun setMasterNode(nodeId: String): Boolean {
        try {
            val node = nodes[nodeId] ?: return false
            
            // 清除其他节点的主节点标志
            nodes.values.forEach {
                if (it.master && it.id != nodeId) {
                    nodes[it.id] = it.copy(master = false)
                }
            }
            
            // 设置新的主节点
            nodes[nodeId] = node.copy(master = true)
            
            return true
        } catch (e: Exception) {
            logger.error("设置主节点失败: {}", nodeId, e)
            return false
        }
    }
    
    override fun saveClusterStatus(status: ClusterStatus): Boolean {
        try {
            clusterStatus = status
            return true
        } catch (e: Exception) {
            logger.error("保存集群状态失败", e)
            return false
        }
    }
    
    override fun getClusterStatus(): ClusterStatus? {
        try {
            return clusterStatus
        } catch (e: Exception) {
            logger.error("获取集群状态失败", e)
            return null
        }
    }
    
    override fun saveServiceNodeMapping(serviceId: String, nodeId: String): Boolean {
        try {
            // 获取服务的节点映射
            val nodeIds = serviceNodeMappings.computeIfAbsent(serviceId) { mutableSetOf() }
            
            // 添加节点ID
            nodeIds.add(nodeId)
            
            return true
        } catch (e: Exception) {
            logger.error("保存服务节点映射失败: {} -> {}", serviceId, nodeId, e)
            return false
        }
    }
    
    override fun deleteServiceNodeMapping(serviceId: String, nodeId: String): Boolean {
        try {
            // 获取服务的节点映射
            val nodeIds = serviceNodeMappings[serviceId] ?: return false
            
            // 删除节点ID
            val removed = nodeIds.remove(nodeId)
            
            // 如果节点列表为空，则删除服务映射
            if (nodeIds.isEmpty()) {
                serviceNodeMappings.remove(serviceId)
            }
            
            return removed
        } catch (e: Exception) {
            logger.error("删除服务节点映射失败: {} -> {}", serviceId, nodeId, e)
            return false
        }
    }
    
    override fun getServiceNodes(serviceId: String): List<String> {
        try {
            return serviceNodeMappings[serviceId]?.toList() ?: emptyList()
        } catch (e: Exception) {
            logger.error("获取服务节点失败: {}", serviceId, e)
            return emptyList()
        }
    }
    
    override fun getNodeServices(nodeId: String): List<String> {
        try {
            return serviceNodeMappings.entries
                .filter { nodeId in it.value }
                .map { it.key }
        } catch (e: Exception) {
            logger.error("获取节点服务失败: {}", nodeId, e)
            return emptyList()
        }
    }
    
    override fun saveClusterConfig(key: String, value: String): Boolean {
        try {
            clusterConfig[key] = value
            return true
        } catch (e: Exception) {
            logger.error("保存集群配置失败: {}", key, e)
            return false
        }
    }
    
    override fun getClusterConfig(key: String): String? {
        try {
            return clusterConfig[key]
        } catch (e: Exception) {
            logger.error("获取集群配置失败: {}", key, e)
            return null
        }
    }
    
    override fun getAllClusterConfig(): Map<String, String> {
        try {
            return clusterConfig.toMap()
        } catch (e: Exception) {
            logger.error("获取所有集群配置失败", e)
            return emptyMap()
        }
    }
    
    override fun deleteClusterConfig(key: String): Boolean {
        try {
            return clusterConfig.remove(key) != null
        } catch (e: Exception) {
            logger.error("删除集群配置失败: {}", key, e)
            return false
        }
    }
}
