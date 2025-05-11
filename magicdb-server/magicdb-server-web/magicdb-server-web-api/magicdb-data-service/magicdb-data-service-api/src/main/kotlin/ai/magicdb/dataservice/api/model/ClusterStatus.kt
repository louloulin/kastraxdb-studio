package ai.magicdb.dataservice.api.model

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 集群状态
 *
 * @author magicdb
 */
data class ClusterStatus(
    /**
     * 集群ID
     */
    val clusterId: String,
    
    /**
     * 集群名称
     */
    val clusterName: String,
    
    /**
     * 是否启用集群模式
     */
    val clusterEnabled: Boolean,
    
    /**
     * 集群状态
     */
    val status: Status,
    
    /**
     * 节点数量
     */
    val nodeCount: Int,
    
    /**
     * 在线节点数量
     */
    val onlineNodeCount: Int,
    
    /**
     * 主节点ID
     */
    val masterNodeId: String,
    
    /**
     * 当前节点ID
     */
    val currentNodeId: String,
    
    /**
     * 集群启动时间
     */
    val startTime: LocalDateTime,
    
    /**
     * 最后同步时间
     */
    val lastSyncTime: LocalDateTime?,
    
    /**
     * 集群版本
     */
    val version: String,
    
    /**
     * 集群配置
     */
    val config: Map<String, String>,
    
    /**
     * 集群统计信息
     */
    val statistics: Map<String, Any>
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
    
    /**
     * 集群状态枚举
     */
    enum class Status {
        /**
         * 正常
         */
        NORMAL,
        
        /**
         * 降级
         */
        DEGRADED,
        
        /**
         * 故障
         */
        FAULT,
        
        /**
         * 恢复中
         */
        RECOVERING,
        
        /**
         * 初始化中
         */
        INITIALIZING,
        
        /**
         * 关闭中
         */
        SHUTTING_DOWN,
        
        /**
         * 已关闭
         */
        SHUTDOWN
    }
}
