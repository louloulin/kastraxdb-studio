package ai.magicdb.data.service.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

/**
 * 服务性能实体
 *
 * @author magicdb
 */
@TableName("data_service_performance")
data class ServicePerformanceDO(
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
     * 记录时间
     */
    @TableField("record_time")
    var recordTime: LocalDateTime = LocalDateTime.now(),
    
    /**
     * 内存使用（MB）
     */
    @TableField("memory_usage")
    var memoryUsage: Double = 0.0,
    
    /**
     * CPU使用（%）
     */
    @TableField("cpu_usage")
    var cpuUsage: Double = 0.0,
    
    /**
     * 线程数
     */
    @TableField("thread_count")
    var threadCount: Int = 0,
    
    /**
     * 活跃连接数
     */
    @TableField("active_connections")
    var activeConnections: Int = 0,
    
    /**
     * 每秒请求数
     */
    @TableField("requests_per_second")
    var requestsPerSecond: Double = 0.0,
    
    /**
     * 平均响应时间（毫秒）
     */
    @TableField("avg_response_time")
    var avgResponseTime: Double = 0.0,
    
    /**
     * 百分位响应时间（JSON格式）
     */
    @TableField("percentiles")
    var percentiles: String? = null,
    
    /**
     * 时间段响应时间（JSON格式）
     */
    @TableField("response_time_by_time_slot")
    var responseTimeByTimeSlot: String? = null,
    
    /**
     * 时间段请求数（JSON格式）
     */
    @TableField("requests_by_time_slot")
    var requestsByTimeSlot: String? = null,
    
    /**
     * 元数据
     */
    @TableField("metadata")
    var metadata: String? = null
)
