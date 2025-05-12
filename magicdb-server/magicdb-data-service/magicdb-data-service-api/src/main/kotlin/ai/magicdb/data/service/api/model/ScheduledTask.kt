package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 定时任务
 *
 * @author magicdb
 */
data class ScheduledTask(
    /**
     * 任务ID
     */
    var id: String = "",
    
    /**
     * 任务名称
     */
    var name: String = "",
    
    /**
     * 任务描述
     */
    var description: String = "",
    
    /**
     * 服务ID
     */
    var serviceId: String = "",
    
    /**
     * 服务名称
     */
    var serviceName: String = "",
    
    /**
     * 服务参数
     */
    var parameters: Map<String, Any?> = emptyMap(),
    
    /**
     * Cron表达式
     */
    var cronExpression: String = "",
    
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
     * 创建用户ID
     */
    var createUserId: Long = 0,
    
    /**
     * 最后执行时间
     */
    var lastExecuteTime: Long = 0,
    
    /**
     * 下次执行时间
     */
    var nextExecuteTime: Long = 0,
    
    /**
     * 执行次数
     */
    var executeCount: Long = 0,
    
    /**
     * 成功次数
     */
    var successCount: Long = 0,
    
    /**
     * 失败次数
     */
    var failCount: Long = 0,
    
    /**
     * 最后执行结果
     */
    var lastExecuteResult: Boolean = false,
    
    /**
     * 最后执行消息
     */
    var lastExecuteMessage: String = "",
    
    /**
     * 最后执行耗时（毫秒）
     */
    var lastExecuteDuration: Long = 0,
    
    /**
     * 任务状态
     */
    var status: ScheduledTaskStatus = ScheduledTaskStatus.PENDING,
    
    /**
     * 任务标签
     */
    var tags: List<String> = emptyList(),
    
    /**
     * 任务超时时间（毫秒）
     */
    var timeout: Long = 60000,
    
    /**
     * 失败重试次数
     */
    var retryCount: Int = 0,
    
    /**
     * 失败重试间隔（秒）
     */
    var retryInterval: Int = 60,
    
    /**
     * 是否允许并发执行
     */
    var allowConcurrent: Boolean = false,
    
    /**
     * 是否发送通知
     */
    var sendNotification: Boolean = false,
    
    /**
     * 通知类型（如：email, sms, webhook）
     */
    var notificationType: String = "",
    
    /**
     * 通知接收者
     */
    var notificationReceivers: List<String> = emptyList(),
    
    /**
     * 任务优先级
     */
    var priority: Int = 5,
    
    /**
     * 任务分组
     */
    var group: String = "default"
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 定时任务状态
 */
enum class ScheduledTaskStatus {
    /**
     * 等待执行
     */
    PENDING,
    
    /**
     * 执行中
     */
    RUNNING,
    
    /**
     * 已暂停
     */
    PAUSED,
    
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
    CANCELED
}
