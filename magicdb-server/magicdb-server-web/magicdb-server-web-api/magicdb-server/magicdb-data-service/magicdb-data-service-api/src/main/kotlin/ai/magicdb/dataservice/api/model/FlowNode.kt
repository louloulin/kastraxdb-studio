package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 流程节点类型
 *
 * @author magicdb
 */
enum class FlowNodeType {
    /**
     * 开始节点
     */
    START,
    
    /**
     * 结束节点
     */
    END,
    
    /**
     * 服务节点
     */
    SERVICE,
    
    /**
     * 条件节点
     */
    CONDITION,
    
    /**
     * 脚本节点
     */
    SCRIPT,
    
    /**
     * 转换节点
     */
    TRANSFORM,
    
    /**
     * 延时节点
     */
    DELAY,
    
    /**
     * 子流程节点
     */
    SUB_FLOW
}

/**
 * 流程节点模型
 *
 * @author magicdb
 */
data class FlowNode(
    /**
     * 节点ID
     */
    var id: String = "",
    
    /**
     * 节点名称
     */
    var name: String = "",
    
    /**
     * 节点类型
     */
    var type: FlowNodeType = FlowNodeType.SERVICE,
    
    /**
     * 节点描述
     */
    var description: String = "",
    
    /**
     * 节点配置
     */
    var config: Map<String, Any?> = emptyMap(),
    
    /**
     * 节点位置X坐标
     */
    var x: Int = 0,
    
    /**
     * 节点位置Y坐标
     */
    var y: Int = 0,
    
    /**
     * 节点宽度
     */
    var width: Int = 0,
    
    /**
     * 节点高度
     */
    var height: Int = 0,
    
    /**
     * 节点样式
     */
    var style: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
