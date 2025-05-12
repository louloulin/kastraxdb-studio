package ai.magicdb.data.service.core.cluster

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * 负载均衡器
 * 负责在集群中分发服务执行请求
 *
 * @author magicdb
 */
@Component
class LoadBalancer(
    private val clusterManager: ClusterManager
) {
    private val logger = LoggerFactory.getLogger(LoadBalancer::class.java)
    
    // 节点计数器
    private val nodeCounters = ConcurrentHashMap<String, AtomicInteger>()
    
    // 节点权重
    private val nodeWeights = ConcurrentHashMap<String, Int>()
    
    // 上次更新时间
    private val lastUpdateTime = AtomicLong(0)
    
    // 更新间隔（毫秒）
    private val UPDATE_INTERVAL = 5000L
    
    /**
     * 选择节点
     *
     * @param serviceId 服务ID
     * @return 节点ID
     */
    fun selectNode(serviceId: String): String {
        // 获取活跃节点
        val nodes = getActiveNodes()
        
        // 如果只有一个节点，直接返回
        if (nodes.size <= 1) {
            return clusterManager.getCurrentNodeId()
        }
        
        // 根据策略选择节点
        return when (getLoadBalancingStrategy()) {
            LoadBalancingStrategy.ROUND_ROBIN -> selectNodeByRoundRobin(nodes)
            LoadBalancingStrategy.LEAST_CONNECTIONS -> selectNodeByLeastConnections(nodes)
            LoadBalancingStrategy.WEIGHTED -> selectNodeByWeighted(nodes)
            LoadBalancingStrategy.RANDOM -> selectNodeByRandom(nodes)
            LoadBalancingStrategy.CONSISTENT_HASH -> selectNodeByConsistentHash(nodes, serviceId)
        }
    }
    
    /**
     * 获取活跃节点
     *
     * @return 活跃节点ID列表
     */
    private fun getActiveNodes(): List<String> {
        // 检查是否需要更新节点列表
        val now = System.currentTimeMillis()
        if (now - lastUpdateTime.get() > UPDATE_INTERVAL) {
            updateNodes()
            lastUpdateTime.set(now)
        }
        
        return clusterManager.getActiveNodes().map { it.id }
    }
    
    /**
     * 更新节点列表
     */
    private fun updateNodes() {
        try {
            // 获取活跃节点
            val activeNodes = clusterManager.getActiveNodes()
            
            // 更新节点权重
            activeNodes.forEach { node ->
                // 默认权重为1，可以根据节点性能等因素调整
                nodeWeights[node.id] = 1
            }
            
            // 移除不活跃的节点
            val activeNodeIds = activeNodes.map { it.id }.toSet()
            nodeCounters.keys.retainAll(activeNodeIds)
            nodeWeights.keys.retainAll(activeNodeIds)
            
            logger.debug("更新节点列表，活跃节点数: {}", activeNodes.size)
        } catch (e: Exception) {
            logger.error("更新节点列表失败", e)
        }
    }
    
    /**
     * 增加节点计数
     *
     * @param nodeId 节点ID
     */
    fun incrementNodeCounter(nodeId: String) {
        nodeCounters.computeIfAbsent(nodeId) { AtomicInteger(0) }.incrementAndGet()
    }
    
    /**
     * 减少节点计数
     *
     * @param nodeId 节点ID
     */
    fun decrementNodeCounter(nodeId: String) {
        nodeCounters[nodeId]?.decrementAndGet()
    }
    
    /**
     * 获取负载均衡策略
     *
     * @return 负载均衡策略
     */
    private fun getLoadBalancingStrategy(): LoadBalancingStrategy {
        // 这里可以从配置中获取策略
        return LoadBalancingStrategy.ROUND_ROBIN
    }
    
    /**
     * 轮询策略选择节点
     *
     * @param nodes 节点列表
     * @return 节点ID
     */
    private fun selectNodeByRoundRobin(nodes: List<String>): String {
        // 使用静态计数器
        val counter = nodeCounters.computeIfAbsent("round_robin") { AtomicInteger(0) }
        val index = counter.getAndIncrement() % nodes.size
        return nodes[index]
    }
    
    /**
     * 最少连接策略选择节点
     *
     * @param nodes 节点列表
     * @return 节点ID
     */
    private fun selectNodeByLeastConnections(nodes: List<String>): String {
        // 找到连接数最少的节点
        var minCount = Int.MAX_VALUE
        var selectedNode = nodes[0]
        
        nodes.forEach { nodeId ->
            val count = nodeCounters.computeIfAbsent(nodeId) { AtomicInteger(0) }.get()
            if (count < minCount) {
                minCount = count
                selectedNode = nodeId
            }
        }
        
        return selectedNode
    }
    
    /**
     * 加权策略选择节点
     *
     * @param nodes 节点列表
     * @return 节点ID
     */
    private fun selectNodeByWeighted(nodes: List<String>): String {
        // 计算总权重
        var totalWeight = 0
        nodes.forEach { nodeId ->
            totalWeight += nodeWeights.getOrDefault(nodeId, 1)
        }
        
        // 随机选择
        var randomWeight = (Math.random() * totalWeight).toInt()
        
        for (nodeId in nodes) {
            val weight = nodeWeights.getOrDefault(nodeId, 1)
            randomWeight -= weight
            if (randomWeight < 0) {
                return nodeId
            }
        }
        
        // 默认返回第一个节点
        return nodes[0]
    }
    
    /**
     * 随机策略选择节点
     *
     * @param nodes 节点列表
     * @return 节点ID
     */
    private fun selectNodeByRandom(nodes: List<String>): String {
        val index = (Math.random() * nodes.size).toInt()
        return nodes[index]
    }
    
    /**
     * 一致性哈希策略选择节点
     *
     * @param nodes 节点列表
     * @param serviceId 服务ID
     * @return 节点ID
     */
    private fun selectNodeByConsistentHash(nodes: List<String>, serviceId: String): String {
        // 简单的哈希算法，可以替换为更复杂的一致性哈希算法
        val hash = serviceId.hashCode()
        val index = Math.abs(hash) % nodes.size
        return nodes[index]
    }
    
    /**
     * 负载均衡策略
     */
    enum class LoadBalancingStrategy {
        ROUND_ROBIN,         // 轮询
        LEAST_CONNECTIONS,   // 最少连接
        WEIGHTED,            // 加权
        RANDOM,              // 随机
        CONSISTENT_HASH      // 一致性哈希
    }
}
