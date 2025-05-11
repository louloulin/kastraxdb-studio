package ai.magicdb.dataservice.api.model

import java.time.LocalDateTime

/**
 * 服务性能记录
 *
 * @author magicdb
 */
data class ServicePerformanceRecord(
    /**
     * 记录ID
     */
    val id: String,
    
    /**
     * 服务ID
     */
    val serviceId: String,
    
    /**
     * 服务名称
     */
    val serviceName: String,
    
    /**
     * 记录时间
     */
    val recordTime: LocalDateTime,
    
    /**
     * 内存使用（MB）
     */
    val memoryUsage: Double,
    
    /**
     * CPU使用（%）
     */
    val cpuUsage: Double,
    
    /**
     * 线程数
     */
    val threadCount: Int,
    
    /**
     * 活跃连接数
     */
    val activeConnections: Int,
    
    /**
     * 每秒请求数
     */
    val requestsPerSecond: Double,
    
    /**
     * 平均响应时间（毫秒）
     */
    val avgResponseTime: Double,
    
    /**
     * 其他元数据
     */
    val metadata: Map<String, Any>? = null
)
