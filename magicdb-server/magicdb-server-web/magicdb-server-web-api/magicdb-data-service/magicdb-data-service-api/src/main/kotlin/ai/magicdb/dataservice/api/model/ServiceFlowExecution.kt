package ai.magicdb.dataservice.api.model

/**
 * 服务流程执行模型
 *
 * @author magicdb
 */
data class ServiceFlowExecution(
    /**
     * 执行ID
     */
    val id: String,
    
    /**
     * 流程ID
     */
    val flowId: String,
    
    /**
     * 流程名称
     */
    val flowName: String,
    
    /**
     * 执行状态
     */
    val status: ServiceFlowExecutionStatus,
    
    /**
     * 开始时间
     */
    val startTime: Long,
    
    /**
     * 结束时间
     */
    val endTime: Long? = null,
    
    /**
     * 执行参数
     */
    val parameters: Map<String, Any?> = emptyMap(),
    
    /**
     * 执行结果
     */
    val result: Any? = null,
    
    /**
     * 错误信息
     */
    val error: String? = null,
    
    /**
     * 执行耗时（毫秒）
     */
    val duration: Long = 0
)

/**
 * 服务流程执行状态
 */
enum class ServiceFlowExecutionStatus {
    /**
     * 等待执行
     */
    PENDING,
    
    /**
     * 执行中
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
