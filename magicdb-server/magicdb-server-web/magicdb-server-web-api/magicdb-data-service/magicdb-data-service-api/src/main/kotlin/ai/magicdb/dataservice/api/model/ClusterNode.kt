package ai.magicdb.dataservice.api.model

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 集群节点
 *
 * @author magicdb
 */
data class ClusterNode(
    /**
     * 节点ID
     */
    val id: String,
    
    /**
     * 节点名称
     */
    val name: String,
    
    /**
     * 节点地址
     */
    val address: String,
    
    /**
     * 节点端口
     */
    val port: Int,
    
    /**
     * 是否是主节点
     */
    val master: Boolean = false,
    
    /**
     * 是否在线
     */
    val online: Boolean = true,
    
    /**
     * 节点角色
     */
    val role: NodeRole = NodeRole.WORKER,
    
    /**
     * 节点权重
     */
    val weight: Int = 100,
    
    /**
     * 节点标签
     */
    val tags: Set<String> = emptySet(),
    
    /**
     * 注册时间
     */
    val registerTime: LocalDateTime = LocalDateTime.now(),
    
    /**
     * 最后心跳时间
     */
    val lastHeartbeatTime: LocalDateTime = LocalDateTime.now(),
    
    /**
     * 节点元数据
     */
    val metadata: Map<String, String> = emptyMap(),
    
    /**
     * 节点版本
     */
    val version: String = "1.0.0",
    
    /**
     * 节点负载
     */
    val load: Double = 0.0,
    
    /**
     * 服务数量
     */
    val serviceCount: Int = 0,
    
    /**
     * 调用次数
     */
    val callCount: Long = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 节点角色
 */
enum class NodeRole {
    /**
     * 主节点
     */
    MASTER,
    
    /**
     * 工作节点
     */
    WORKER,
    
    /**
     * 管理节点
     */
    MANAGER,
    
    /**
     * 只读节点
     */
    READONLY
}
