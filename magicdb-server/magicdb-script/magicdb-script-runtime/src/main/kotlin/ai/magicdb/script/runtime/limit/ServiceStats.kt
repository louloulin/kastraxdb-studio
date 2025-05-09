package ai.magicdb.script.runtime.limit

/**
 * 服务统计
 *
 * @author magicdb
 */
data class ServiceStats(
    /**
     * 服务ID
     */
    val serviceId: String,
    
    /**
     * 调用次数
     */
    val callCount: Int,
    
    /**
     * 错误次数
     */
    val errorCount: Int,
    
    /**
     * 最后错误时间
     */
    val lastErrorTime: Long,
    
    /**
     * 断路器状态
     */
    val circuitBreakerStatus: CircuitBreakerStatus,
    
    /**
     * 并发限制
     */
    val concurrentLimit: Int
) {
    /**
     * 错误率
     */
    val errorRate: Double
        get() = if (callCount > 0) errorCount.toDouble() / callCount.toDouble() else 0.0
}
