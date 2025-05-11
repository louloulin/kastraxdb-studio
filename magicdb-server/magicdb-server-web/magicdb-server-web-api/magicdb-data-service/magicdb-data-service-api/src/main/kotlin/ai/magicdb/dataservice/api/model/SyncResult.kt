package ai.magicdb.dataservice.api.model

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 同步结果
 *
 * @author magicdb
 */
data class SyncResult(
    /**
     * 同步ID
     */
    val syncId: String,
    
    /**
     * 同步时间
     */
    val syncTime: LocalDateTime,
    
    /**
     * 源节点ID
     */
    val sourceNodeId: String,
    
    /**
     * 目标节点ID列表
     */
    val targetNodeIds: List<String>,
    
    /**
     * 同步的服务ID列表
     */
    val serviceIds: List<String>,
    
    /**
     * 同步的服务组ID列表
     */
    val groupIds: List<String>,
    
    /**
     * 同步结果（节点ID -> 结果）
     */
    val results: Map<String, NodeSyncResult>,
    
    /**
     * 同步耗时（毫秒）
     */
    val duration: Long,
    
    /**
     * 是否全部成功
     */
    val allSuccess: Boolean,
    
    /**
     * 成功节点数
     */
    val successCount: Int,
    
    /**
     * 失败节点数
     */
    val failCount: Int,
    
    /**
     * 同步的服务数量
     */
    val serviceCount: Int,
    
    /**
     * 同步的服务组数量
     */
    val groupCount: Int
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
    
    /**
     * 节点同步结果
     */
    data class NodeSyncResult(
        /**
         * 节点ID
         */
        val nodeId: String,
        
        /**
         * 是否成功
         */
        val success: Boolean,
        
        /**
         * 错误消息
         */
        val errorMessage: String?,
        
        /**
         * 同步的服务ID列表
         */
        val serviceIds: List<String>,
        
        /**
         * 同步的服务组ID列表
         */
        val groupIds: List<String>,
        
        /**
         * 同步耗时（毫秒）
         */
        val duration: Long,
        
        /**
         * 同步详情
         */
        val details: Map<String, Any>
    ) : Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }
}
