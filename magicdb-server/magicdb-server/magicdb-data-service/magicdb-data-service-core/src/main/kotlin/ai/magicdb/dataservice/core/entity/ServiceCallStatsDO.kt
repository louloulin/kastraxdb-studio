package ai.magicdb.dataservice.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

/**
 * 服务调用统计实体类
 *
 * @author magicdb
 */
@TableName("data_service_call_stats")
data class ServiceCallStatsDO(
    /**
     * 统计ID
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
     * 统计类型
     */
    @TableField("stat_type")
    var statType: String,
    
    /**
     * 统计周期
     */
    @TableField("stat_period")
    var statPeriod: String,
    
    /**
     * 开始时间
     */
    @TableField("start_time")
    var startTime: LocalDateTime,
    
    /**
     * 结束时间
     */
    @TableField("end_time")
    var endTime: LocalDateTime,
    
    /**
     * 总调用次数
     */
    @TableField("total_calls")
    var totalCalls: Long,
    
    /**
     * 成功调用次数
     */
    @TableField("success_calls")
    var successCalls: Long,
    
    /**
     * 失败调用次数
     */
    @TableField("failed_calls")
    var failedCalls: Long,
    
    /**
     * 平均执行时间（毫秒）
     */
    @TableField("avg_execution_time")
    var avgExecutionTime: Double,
    
    /**
     * 最大执行时间（毫秒）
     */
    @TableField("max_execution_time")
    var maxExecutionTime: Long,
    
    /**
     * 最小执行时间（毫秒）
     */
    @TableField("min_execution_time")
    var minExecutionTime: Long,
    
    /**
     * 调用用户数
     */
    @TableField("unique_users")
    var uniqueUsers: Int,
    
    /**
     * 调用IP数
     */
    @TableField("unique_ips")
    var uniqueIps: Int,
    
    /**
     * 错误类型统计（JSON格式）
     */
    @TableField("error_types")
    var errorTypes: String?,
    
    /**
     * 常见错误（JSON格式）
     */
    @TableField("common_errors")
    var commonErrors: String?,
    
    /**
     * 按小时统计的调用次数（JSON格式）
     */
    @TableField("calls_by_hour")
    var callsByHour: String?,
    
    /**
     * 按天统计的调用次数（JSON格式）
     */
    @TableField("calls_by_day")
    var callsByDay: String?,
    
    /**
     * 更新时间
     */
    @TableField("update_time")
    var updateTime: LocalDateTime
)
