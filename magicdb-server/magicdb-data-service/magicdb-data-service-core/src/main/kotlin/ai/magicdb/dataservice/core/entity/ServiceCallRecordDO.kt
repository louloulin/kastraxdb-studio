package ai.magicdb.dataservice.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

/**
 * 服务调用记录实体
 *
 * @author magicdb
 */
@TableName("data_service_call_record")
data class ServiceCallRecordDO(
    /**
     * 记录ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    var id: String = "",
    
    /**
     * 服务ID
     */
    @TableField("service_id")
    var serviceId: String = "",
    
    /**
     * 服务名称
     */
    @TableField("service_name")
    var serviceName: String = "",
    
    /**
     * 调用时间
     */
    @TableField("call_time")
    var callTime: LocalDateTime = LocalDateTime.now(),
    
    /**
     * 执行时间（毫秒）
     */
    @TableField("execution_time")
    var executionTime: Long = 0,
    
    /**
     * 是否成功
     */
    @TableField("success")
    var success: Boolean = true,
    
    /**
     * 错误类型
     */
    @TableField("error_type")
    var errorType: String? = null,
    
    /**
     * 错误消息
     */
    @TableField("error_message")
    var errorMessage: String? = null,
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    var userId: Long? = null,
    
    /**
     * 客户端IP
     */
    @TableField("client_ip")
    var clientIp: String? = null,
    
    /**
     * 请求参数
     */
    @TableField("request_params")
    var requestParams: String? = null,
    
    /**
     * 响应结果
     */
    @TableField("response_result")
    var responseResult: String? = null,
    
    /**
     * 触发类型
     */
    @TableField("trigger_type")
    var triggerType: String = "MANUAL",
    
    /**
     * 触发ID
     */
    @TableField("trigger_id")
    var triggerId: String? = null,
    
    /**
     * 元数据
     */
    @TableField("metadata")
    var metadata: String? = null
)
