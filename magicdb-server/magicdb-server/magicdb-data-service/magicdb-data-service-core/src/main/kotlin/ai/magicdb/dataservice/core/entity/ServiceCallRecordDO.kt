package ai.magicdb.dataservice.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

/**
 * 服务调用记录实体类
 *
 * @author magicdb
 */
@TableName("data_service_call_record")
data class ServiceCallRecordDO(
    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    var id: String,
    
    /**
     * 服务ID
     */
    @TableField("service_id")
    var serviceId: String,
    
    /**
     * 服务名称
     */
    @TableField("service_name")
    var serviceName: String?,
    
    /**
     * 调用时间
     */
    @TableField("call_time")
    var callTime: LocalDateTime,
    
    /**
     * 执行时间（毫秒）
     */
    @TableField("execution_time")
    var executionTime: Long,
    
    /**
     * 是否成功
     */
    @TableField("success")
    var success: Boolean,
    
    /**
     * 错误消息
     */
    @TableField("error_message")
    var errorMessage: String?,
    
    /**
     * 错误类型
     */
    @TableField("error_type")
    var errorType: String?,
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    var userId: Long?,
    
    /**
     * 客户端IP
     */
    @TableField("client_ip")
    var clientIp: String?,
    
    /**
     * 请求参数
     */
    @TableField("request_params")
    var requestParams: String?,
    
    /**
     * 响应数据
     */
    @TableField("response_data")
    var responseData: String?,
    
    /**
     * 调用方法
     */
    @TableField("method")
    var method: String?,
    
    /**
     * 调用路径
     */
    @TableField("path")
    var path: String?,
    
    /**
     * 调用来源
     */
    @TableField("source")
    var source: String?
)
