package ai.magicdb.dataservice.api.model

import java.io.Serializable
import java.util.Date

/**
 * 服务流程执行
 *
 * @author magicdb
 */
data class ServiceFlowExecution(
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
     * 执行状态
     */
    var status: ServiceFlowExecutionStatus = ServiceFlowExecutionStatus.PENDING,
    
    /**
     * 开始时间
     */
    var startTime: Long = 0,
    
    /**
     * 结束时间
     */
    var endTime: Long? = null,
    
    /**
     * 执行参数
     */
    var parameters: Map<String, Any?> = emptyMap(),
    
    /**
     * 执行结果
     */
    var result: Any? = null,
    
    /**
     * 错误信息
     */
    var error: String? = null,
    
    /**
     * 节点执行状态
     */
    var nodeExecutions: List<ServiceFlowNodeExecution> = emptyList(),
    
    /**
     * 当前执行节点ID
     */
    var currentNodeId: String? = null,
    
    /**
     * 执行日志
     */
    var logs: List<String> = emptyList(),
    
    /**
     * 执行用户ID
     */
    var userId: Long = 0,
    
    /**
     * 执行耗时（毫秒）
     */
    var duration: Long = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 服务流程节点执行
 */
data class ServiceFlowNodeExecution(
    /**
     * 节点ID
     */
    var nodeId: String = "",
    
    /**
     * 节点名称
     */
    var nodeName: String = "",
    
    /**
     * 执行状态
     */
    var status: ServiceFlowExecutionStatus = ServiceFlowExecutionStatus.PENDING,
    
    /**
     * 开始时间
     */
    var startTime: Long? = null,
    
    /**
     * 结束时间
     */
    var endTime: Long? = null,
    
    /**
     * 执行结果
     */
    var result: Any? = null,
    
    /**
     * 错误信息
     */
    var error: String? = null,
    
    /**
     * 执行日志
     */
    var logs: List<String> = emptyList(),
    
    /**
     * 执行耗时（毫秒）
     */
    var duration: Long = 0,
    
    /**
     * 重试次数
     */
    var retryCount: Int = 0,
    
    /**
     * 最大重试次数
     */
    var maxRetryCount: Int = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 服务流程执行状态
 */
enum class ServiceFlowExecutionStatus {
    /**
     * 等待中
     */
    PENDING,
    
    /**
     * 运行中
     */
    RUNNING,
    
    /**
     * 已完成
     */
    COMPLETED,
    
    /**
     * 已失败
     */
    FAILED,
    
    /**
     * 已取消
     */
    CANCELLED,
    
    /**
     * 已跳过
     */
    SKIPPED,
    
    /**
     * 已暂停
     */
    PAUSED,
    
    /**
     * 已超时
     */
    TIMEOUT
}
