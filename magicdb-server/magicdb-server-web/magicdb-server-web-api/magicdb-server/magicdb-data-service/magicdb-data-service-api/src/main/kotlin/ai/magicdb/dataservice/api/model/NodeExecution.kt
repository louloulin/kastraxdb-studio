package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 节点执行模型
 *
 * @author magicdb
 */
data class NodeExecution(
    /**
     * 执行ID
     */
    var id: String = "",
    
    /**
     * 节点ID
     */
    var nodeId: String = "",
    
    /**
     * 节点名称
     */
    var nodeName: String = "",
    
    /**
     * 节点类型
     */
    var nodeType: FlowNodeType = FlowNodeType.SERVICE,
    
    /**
     * 开始时间
     */
    var startTime: Long = 0,
    
    /**
     * 结束时间
     */
    var endTime: Long = 0,
    
    /**
     * 执行时长（毫秒）
     */
    var duration: Long = 0,
    
    /**
     * 执行状态
     */
    var status: FlowExecutionStatus = FlowExecutionStatus.PENDING,
    
    /**
     * 执行结果
     */
    var result: Any? = null,
    
    /**
     * 错误信息
     */
    var errorMessage: String = "",
    
    /**
     * 输入参数
     */
    var input: Map<String, Any?> = emptyMap(),
    
    /**
     * 输出参数
     */
    var output: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
