package ai.magicdb.dataservice.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

/**
 * 服务性能监控实体类
 *
 * @author magicdb
 */
@TableName("data_service_performance")
data class ServicePerformanceDO(
    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    var id: String,
    
    /**
     * 服务ID
     */
    @TableField("service_id")
    var serviceId: String?,
    
    /**
     * 服务名称
     */
    @TableField("service_name")
    var serviceName: String?,
    
    /**
     * 记录时间
     */
    @TableField("record_time")
    var recordTime: LocalDateTime,
    
    /**
     * 内存使用（MB）
     */
    @TableField("memory_usage")
    var memoryUsage: Double,
    
    /**
     * CPU使用（%）
     */
    @TableField("cpu_usage")
    var cpuUsage: Double,
    
    /**
     * 线程数
     */
    @TableField("thread_count")
    var threadCount: Int,
    
    /**
     * 活跃连接数
     */
    @TableField("active_connections")
    var activeConnections: Int,
    
    /**
     * 每秒请求数
     */
    @TableField("requests_per_second")
    var requestsPerSecond: Double,
    
    /**
     * 平均响应时间（毫秒）
     */
    @TableField("avg_response_time")
    var avgResponseTime: Double,
    
    /**
     * 其他元数据（JSON格式）
     */
    @TableField("metadata")
    var metadata: String?
)
