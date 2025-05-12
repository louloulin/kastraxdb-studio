package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 流程执行状态
 *
 * @author magicdb
 */
enum class FlowExecutionStatus {
    /**
     * 等待执行
     */
    PENDING,

    /**
     * 执行中
     */
    RUNNING,

    /**
     * 执行成功
     */
    SUCCESS,

    /**
     * 执行失败
     */
    FAILED,

    /**
     * 已取消
     */
    CANCELED,

    /**
     * 已超时
     */
    TIMEOUT,

    /**
     * 已暂停
     */
    PAUSED,

    /**
     * 未知状态
     */
    UNKNOWN
}

/**
 * 流程执行模型
 *
 * @author magicdb
 */
data class FlowExecution(
    /**
     * 执行ID
     */
    var id: String = "",

    /**
     * 流程ID
     */
    var flowId: String = "",

    /**
     * 流程名称
     */
    var flowName: String = "",

    /**
     * 执行参数
     */
    var parameters: Map<String, Any?> = emptyMap(),

    /**
     * 执行变量
     */
    var variables: MutableMap<String, Any?> = mutableMapOf(),

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
     * 节点执行记录
     */
    var nodeExecutions: List<NodeExecution> = emptyList(),

    /**
     * 触发类型
     */
    var triggerType: TriggerType = TriggerType.MANUAL,

    /**
     * 触发ID
     */
    var triggerId: String = ""
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 节点执行记录
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
     * 执行耗时（毫秒）
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
     * 错误消息
     */
    var errorMessage: String = "",

    /**
     * 输入数据
     */
    var input: Map<String, Any?> = emptyMap(),

    /**
     * 输出数据
     */
    var output: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
