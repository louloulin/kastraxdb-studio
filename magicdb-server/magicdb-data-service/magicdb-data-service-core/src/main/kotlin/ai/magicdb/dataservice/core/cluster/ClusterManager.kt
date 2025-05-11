package ai.magicdb.dataservice.core.cluster

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * 集群管理器
 * 负责管理集群中的节点状态
 *
 * @author magicdb
 */
@Component
class ClusterManager(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val logger = LoggerFactory.getLogger(ClusterManager::class.java)
    
    // 节点ID
    private val nodeId = UUID.randomUUID().toString()
    
    // 节点信息
    private lateinit var nodeInfo: NodeInfo
    
    // 节点键前缀
    private val NODE_PREFIX = "data_service:cluster:node:"
    
    // 节点过期时间（秒）
    private val NODE_EXPIRE_SECONDS = 30L
    
    /**
     * 初始化
     */
    @PostConstruct
    fun init() {
        try {
            // 创建节点信息
            val hostName = InetAddress.getLocalHost().hostName
            val hostAddress = InetAddress.getLocalHost().hostAddress
            
            nodeInfo = NodeInfo(
                id = nodeId,
                host = hostName,
                ip = hostAddress,
                startTime = System.currentTimeMillis()
            )
            
            // 注册节点
            registerNode()
            
            logger.info("集群管理器初始化完成，节点ID: {}, 主机名: {}, IP: {}", nodeId, hostName, hostAddress)
        } catch (e: Exception) {
            logger.error("集群管理器初始化失败", e)
        }
    }
    
    /**
     * 销毁
     */
    @PreDestroy
    fun destroy() {
        try {
            // 注销节点
            unregisterNode()
            
            logger.info("集群管理器已销毁，节点ID: {}", nodeId)
        } catch (e: Exception) {
            logger.error("集群管理器销毁失败", e)
        }
    }
    
    /**
     * 注册节点
     */
    private fun registerNode() {
        val key = NODE_PREFIX + nodeId
        redisTemplate.opsForValue().set(key, nodeInfo, NODE_EXPIRE_SECONDS, TimeUnit.SECONDS)
        logger.debug("注册节点: {}", nodeId)
    }
    
    /**
     * 注销节点
     */
    private fun unregisterNode() {
        val key = NODE_PREFIX + nodeId
        redisTemplate.delete(key)
        logger.debug("注销节点: {}", nodeId)
    }
    
    /**
     * 定时更新节点状态
     */
    @Scheduled(fixedRate = 10000) // 每10秒更新一次
    fun updateNodeStatus() {
        try {
            // 更新节点信息
            nodeInfo.lastHeartbeatTime = System.currentTimeMillis()
            
            // 更新Redis中的节点信息
            registerNode()
        } catch (e: Exception) {
            logger.error("更新节点状态失败", e)
        }
    }
    
    /**
     * 获取所有活跃节点
     *
     * @return 活跃节点列表
     */
    fun getActiveNodes(): List<NodeInfo> {
        try {
            // 获取所有节点键
            val keys = redisTemplate.keys("$NODE_PREFIX*")
            
            // 获取所有节点信息
            return keys.mapNotNull { key ->
                redisTemplate.opsForValue().get(key) as? NodeInfo
            }
        } catch (e: Exception) {
            logger.error("获取活跃节点失败", e)
            return emptyList()
        }
    }
    
    /**
     * 获取当前节点ID
     *
     * @return 节点ID
     */
    fun getCurrentNodeId(): String {
        return nodeId
    }
    
    /**
     * 获取当前节点信息
     *
     * @return 节点信息
     */
    fun getCurrentNodeInfo(): NodeInfo {
        return nodeInfo
    }
    
    /**
     * 节点信息
     */
    data class NodeInfo(
        val id: String,
        val host: String,
        val ip: String,
        val startTime: Long,
        var lastHeartbeatTime: Long = System.currentTimeMillis(),
        var status: NodeStatus = NodeStatus.ACTIVE
    ) {
        /**
         * 获取运行时间（毫秒）
         */
        fun getUptime(): Long {
            return System.currentTimeMillis() - startTime
        }
    }
    
    /**
     * 节点状态
     */
    enum class NodeStatus {
        ACTIVE, INACTIVE
    }
}
