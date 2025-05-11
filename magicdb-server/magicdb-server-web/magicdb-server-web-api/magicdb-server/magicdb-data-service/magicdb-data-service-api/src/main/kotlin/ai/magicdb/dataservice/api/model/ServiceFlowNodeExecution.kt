package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 服务流程节点执行模型
 *
 * @author magicdb
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
    var startTime: Long = 0,
    
    /**
     * 结束时间
     */
    var endTime: Long = 0,
    
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
     * 执行时长（毫秒）
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
