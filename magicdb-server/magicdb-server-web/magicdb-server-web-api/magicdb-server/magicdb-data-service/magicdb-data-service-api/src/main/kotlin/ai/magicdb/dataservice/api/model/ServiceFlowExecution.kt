package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 服务流程执行状态
 *
 * @author magicdb
 */
enum class ServiceFlowExecutionStatus {
    /**
     * 等待执行
     */
    PENDING,
    
    /**
     * 正在执行
     */
    RUNNING,
    
    /**
     * 执行完成
     */
    COMPLETED,
    
    /**
     * 执行失败
     */
    FAILED,
    
    /**
     * 已取消
     */
    CANCELLED,
    
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
 * 服务流程执行模型
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
    var endTime: Long = 0,
    
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
     * 节点执行记录
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
     * 用户ID
     */
    var userId: Long = 0,
    
    /**
     * 执行时长（毫秒）
     */
    var duration: Long = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
