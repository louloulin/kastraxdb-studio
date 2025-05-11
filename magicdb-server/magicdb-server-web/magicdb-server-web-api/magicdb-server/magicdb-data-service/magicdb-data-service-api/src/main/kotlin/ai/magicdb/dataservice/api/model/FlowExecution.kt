package ai.magicdb.dataservice.api.model

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
     * 正在执行
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
    PAUSED
}

/**
 * 触发类型
 *
 * @author magicdb
 */
enum class TriggerType {
    /**
     * 手动触发
     */
    MANUAL,
    
    /**
     * 定时触发
     */
    SCHEDULED,
    
    /**
     * 事件触发
     */
    EVENT,
    
    /**
     * API触发
     */
    API,
    
    /**
     * 其他触发
     */
    OTHER
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
