package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 服务流程模型
 *
 * @author magicdb
 */
data class ServiceFlow(
    /**
     * 流程ID
     */
    var id: String = "",
    
    /**
     * 流程名称
     */
    var name: String = "",
    
    /**
     * 流程描述
     */
    var description: String = "",
    
    /**
     * 流程版本
     */
    var version: String = "1.0",
    
    /**
     * 流程节点列表
     */
    var nodes: List<FlowNode> = emptyList(),
    
    /**
     * 流程连接列表
     */
    var connections: List<FlowConnection> = emptyList(),
    
    /**
     * 流程参数列表
     */
    var parameters: List<FlowParameter> = emptyList(),
    
    /**
     * 流程标签
     */
    var tags: List<String> = emptyList(),
    
    /**
     * 是否启用
     */
    var enabled: Boolean = true,
    
    /**
     * 创建时间
     */
    var createTime: Long = 0,
    
    /**
     * 更新时间
     */
    var updateTime: Long = 0,
    
    /**
     * 创建者ID
     */
    var createUserId: Long = 0,
    
    /**
     * 执行次数
     */
    var executeCount: Long = 0,
    
    /**
     * 最后执行时间
     */
    var lastExecuteTime: Long = 0,
    
    /**
     * 元数据
     */
    var metadata: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
