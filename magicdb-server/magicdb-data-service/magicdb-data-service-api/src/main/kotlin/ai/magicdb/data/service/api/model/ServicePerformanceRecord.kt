package ai.magicdb.data.service.api.model

import java.io.Serializable
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
    val id: String = "",
    
    /**
     * 服务ID
     */
    val serviceId: String = "",
    
    /**
     * 服务名称
     */
    val serviceName: String = "",
    
    /**
     * 记录时间
     */
    val recordTime: LocalDateTime = LocalDateTime.now(),
    
    /**
     * 内存使用（MB）
     */
    val memoryUsage: Double = 0.0,
    
    /**
     * CPU使用（%）
     */
    val cpuUsage: Double = 0.0,
    
    /**
     * 线程数
     */
    val threadCount: Int = 0,
    
    /**
     * 活跃连接数
     */
    val activeConnections: Int = 0,
    
    /**
     * 每秒请求数
     */
    val requestsPerSecond: Double = 0.0,
    
    /**
     * 平均响应时间（毫秒）
     */
    val avgResponseTime: Double = 0.0,
    
    /**
     * 元数据
     */
    val metadata: Map<String, Any>? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
