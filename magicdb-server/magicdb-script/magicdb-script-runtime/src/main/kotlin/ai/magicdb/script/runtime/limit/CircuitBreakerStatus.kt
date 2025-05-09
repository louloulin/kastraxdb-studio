package ai.magicdb.script.runtime.limit

/**
 * 断路器状态
 *
 * @author magicdb
 */
enum class CircuitBreakerStatus {
    /**
     * 关闭状态，允许请求通过
     */
    CLOSED,
    
    /**
     * 打开状态，拒绝所有请求
     */
    OPEN,
    
    /**
     * 半开状态，允许部分请求通过，用于测试服务是否恢复
     */
    HALF_OPEN
}
