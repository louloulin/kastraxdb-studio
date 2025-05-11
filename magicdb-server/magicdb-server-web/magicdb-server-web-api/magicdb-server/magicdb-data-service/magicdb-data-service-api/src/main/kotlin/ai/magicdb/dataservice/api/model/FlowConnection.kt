package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 流程连接模型
 *
 * @author magicdb
 */
data class FlowConnection(
    /**
     * 连接ID
     */
    var id: String = "",
    
    /**
     * 源节点ID
     */
    var sourceId: String = "",
    
    /**
     * 目标节点ID
     */
    var targetId: String = "",
    
    /**
     * 连接标签
     */
    var label: String = "",
    
    /**
     * 连接条件
     */
    var condition: String = "",
    
    /**
     * 连接样式
     */
    var style: Map<String, Any?> = emptyMap(),
    
    /**
     * 连接点
     */
    var points: List<Map<String, Int>> = emptyList()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
