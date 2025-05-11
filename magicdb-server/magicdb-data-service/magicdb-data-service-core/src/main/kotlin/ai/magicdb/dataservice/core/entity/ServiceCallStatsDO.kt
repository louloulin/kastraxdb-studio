package ai.magicdb.dataservice.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

/**
 * 服务调用统计实体
 *
 * @author magicdb
 */
@TableName("data_service_call_stats")
data class ServiceCallStatsDO(
    /**
     * 统计ID
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
     * 统计时间
     */
    @TableField("stats_time")
    var statsTime: LocalDateTime = LocalDateTime.now(),
    
    /**
     * 总调用次数
     */
    @TableField("total_calls")
    var totalCalls: Long = 0,
    
    /**
     * 成功调用次数
     */
    @TableField("success_calls")
    var successCalls: Long = 0,
    
    /**
     * 失败调用次数
     */
    @TableField("failed_calls")
    var failedCalls: Long = 0,
    
    /**
     * 成功率
     */
    @TableField("success_rate")
    var successRate: Double = 0.0,
    
    /**
     * 平均执行时间（毫秒）
     */
    @TableField("avg_execution_time")
    var avgExecutionTime: Double = 0.0,
    
    /**
     * 最大执行时间（毫秒）
     */
    @TableField("max_execution_time")
    var maxExecutionTime: Long = 0,
    
    /**
     * 最小执行时间（毫秒）
     */
    @TableField("min_execution_time")
    var minExecutionTime: Long = 0,
    
    /**
     * 唯一用户数
     */
    @TableField("unique_users")
    var uniqueUsers: Int = 0,
    
    /**
     * 唯一IP数
     */
    @TableField("unique_ips")
    var uniqueIps: Int = 0,
    
    /**
     * 错误类型统计（JSON格式）
     */
    @TableField("error_types")
    var errorTypes: String? = null,
    
    /**
     * 最常见错误（JSON格式）
     */
    @TableField("common_errors")
    var commonErrors: String? = null,
    
    /**
     * 统计周期（小时）
     */
    @TableField("stats_period")
    var statsPeriod: Int = 1,
    
    /**
     * 元数据
     */
    @TableField("metadata")
    var metadata: String? = null
)
