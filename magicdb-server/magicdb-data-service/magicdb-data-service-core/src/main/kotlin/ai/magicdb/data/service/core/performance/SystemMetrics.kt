package ai.magicdb.data.service.core.performance

/**
 * 系统性能指标
 *
 * @author magicdb
 */
data class SystemMetrics(
    val cpuUsage: Double = 0.0,
    val memoryUsage: Double = 0.0,
    val diskUsage: Double = 0.0,
    val networkIn: Double = 0.0,
    val networkOut: Double = 0.0,
    val activeConnections: Int = 0,
    val activeThreads: Int = 0,
    val queuedRequests: Int = 0,
    val requestsPerSecond: Double = 0.0,
    val averageResponseTime: Double = 0.0,
    val errorRate: Double = 0.0,
    val jvmHeapUsage: Double = 0.0,
    val jvmNonHeapUsage: Double = 0.0,
    val jvmThreadCount: Int = 0,
    val jvmGcCount: Int = 0,
    val jvmGcTime: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)
