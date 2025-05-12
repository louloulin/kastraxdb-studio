package ai.magicdb.data.service.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * 定时任务实体类
 *
 * @author magicdb
 */
@TableName("data_service_scheduled_task")
class ScheduledTaskDO {
    
    /**
     * 任务ID
     */
    @TableId(type = IdType.INPUT)
    var id: String = ""
    
    /**
     * 任务名称
     */
    var name: String = ""
    
    /**
     * 任务描述
     */
    var description: String? = null
    
    /**
     * 服务ID
     */
    var serviceId: String = ""
    
    /**
     * 服务名称
     */
    var serviceName: String? = null
    
    /**
     * 服务参数（JSON格式）
     */
    var parameters: String? = null
    
    /**
     * Cron表达式
     */
    var cronExpression: String = ""
    
    /**
     * 是否启用
     */
    var enabled: Boolean? = true
    
    /**
     * 创建时间
     */
    var createTime: Long? = null
    
    /**
     * 更新时间
     */
    var updateTime: Long? = null
    
    /**
     * 创建用户ID
     */
    var createUserId: Long? = null
    
    /**
     * 最后执行时间
     */
    var lastExecuteTime: Long? = null
    
    /**
     * 下次执行时间
     */
    var nextExecuteTime: Long? = null
    
    /**
     * 执行次数
     */
    var executeCount: Long? = 0
    
    /**
     * 成功次数
     */
    var successCount: Long? = 0
    
    /**
     * 失败次数
     */
    var failCount: Long? = 0
    
    /**
     * 最后执行结果
     */
    var lastExecuteResult: Boolean? = null
    
    /**
     * 最后执行消息
     */
    var lastExecuteMessage: String? = null
    
    /**
     * 最后执行耗时（毫秒）
     */
    var lastExecuteDuration: Long? = null
    
    /**
     * 任务状态
     */
    var status: String? = null
    
    /**
     * 任务标签（JSON格式）
     */
    var tags: String? = null
    
    /**
     * 任务超时时间（毫秒）
     */
    var timeout: Long? = 60000
    
    /**
     * 失败重试次数
     */
    var retryCount: Int? = 0
    
    /**
     * 失败重试间隔（秒）
     */
    var retryInterval: Int? = 60
    
    /**
     * 是否允许并发执行
     */
    var allowConcurrent: Boolean? = false
    
    /**
     * 是否发送通知
     */
    var sendNotification: Boolean? = false
    
    /**
     * 通知类型（如：email, sms, webhook）
     */
    var notificationType: String? = null
    
    /**
     * 通知接收者（JSON格式）
     */
    var notificationReceivers: String? = null
    
    /**
     * 任务优先级
     */
    var priority: Int? = 5
    
    /**
     * 任务分组
     */
    var group: String? = "default"
}
