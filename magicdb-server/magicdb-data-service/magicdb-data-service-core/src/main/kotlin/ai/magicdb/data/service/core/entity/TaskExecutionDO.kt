package ai.magicdb.data.service.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * 任务执行记录实体类
 *
 * @author magicdb
 */
@TableName("data_service_task_execution")
class TaskExecutionDO {
    
    /**
     * 执行ID
     */
    @TableId(type = IdType.INPUT)
    var id: String = ""
    
    /**
     * 任务ID
     */
    var taskId: String = ""
    
    /**
     * 任务名称
     */
    var taskName: String? = null
    
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
     * 开始时间
     */
    var startTime: Long? = null
    
    /**
     * 结束时间
     */
    var endTime: Long? = null
    
    /**
     * 执行耗时（毫秒）
     */
    var duration: Long? = null
    
    /**
     * 执行状态
     */
    var status: String? = null
    
    /**
     * 执行结果（JSON格式）
     */
    var result: String? = null
    
    /**
     * 错误消息
     */
    var errorMessage: String? = null
    
    /**
     * 执行节点
     */
    var executorNode: String? = null
    
    /**
     * 重试次数
     */
    var retryCount: Int? = 0
    
    /**
     * 触发类型
     */
    var triggerType: String? = null
    
    /**
     * 触发者ID
     */
    var triggerId: String? = null
}
