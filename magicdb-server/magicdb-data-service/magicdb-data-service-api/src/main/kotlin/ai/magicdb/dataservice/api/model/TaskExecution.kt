package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 执行状态
 *
 * @author magicdb
 */
enum class ExecutionStatus {
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
    TIMEOUT
}

/**
 * 任务执行记录
 *
 * @author magicdb
 */
data class TaskExecution(
    /**
     * 执行ID
     */
    var id: String = "",
    
    /**
     * 任务ID
     */
    var taskId: String = "",
    
    /**
     * 任务名称
     */
    var taskName: String = "",
    
    /**
     * 服务ID
     */
    var serviceId: String = "",
    
    /**
     * 服务名称
     */
    var serviceName: String = "",
    
    /**
     * 执行参数
     */
    var parameters: Map<String, Any?> = emptyMap(),
    
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
    var status: ExecutionStatus = ExecutionStatus.PENDING,
    
    /**
     * 执行结果
     */
    var result: Any? = null,
    
    /**
     * 错误信息
     */
    var errorMessage: String = "",
    
    /**
     * 执行节点
     */
    var executorNode: String = "",
    
    /**
     * 重试次数
     */
    var retryCount: Int = 0,
    
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
