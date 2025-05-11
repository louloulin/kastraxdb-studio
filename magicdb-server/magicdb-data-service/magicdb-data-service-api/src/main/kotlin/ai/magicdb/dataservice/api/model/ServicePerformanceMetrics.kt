package ai.magicdb.dataservice.api.model

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 服务性能指标
 *
 * @author magicdb
 */
data class ServicePerformanceMetrics(
    /**
     * 服务ID
     */
    val serviceId: String,
    
    /**
     * 服务名称
     */
    val serviceName: String,
    
    /**
     * 平均响应时间（毫秒）
     */
    val avgResponseTime: Double,
    
    /**
     * 最大响应时间（毫秒）
     */
    val maxResponseTime: Long,
    
    /**
     * 最小响应时间（毫秒）
     */
    val minResponseTime: Long,
    
    /**
     * 响应时间百分位数（毫秒）
     */
    val percentiles: Map<String, Long>,
    
    /**
     * 每秒请求数
     */
    val requestsPerSecond: Double,
    
    /**
     * 并发用户数
     */
    val concurrentUsers: Int,
    
    /**
     * 内存使用（MB）
     */
    val memoryUsage: Double,
    
    /**
     * CPU使用（%）
     */
    val cpuUsage: Double,
    
    /**
     * 统计开始时间
     */
    val startTime: LocalDateTime,
    
    /**
     * 统计结束时间
     */
    val endTime: LocalDateTime,
    
    /**
     * 按时间段的平均响应时间
     */
    val responseTimeByTimeSlot: Map<String, Double> = emptyMap(),
    
    /**
     * 按时间段的请求数
     */
    val requestsByTimeSlot: Map<String, Long> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
