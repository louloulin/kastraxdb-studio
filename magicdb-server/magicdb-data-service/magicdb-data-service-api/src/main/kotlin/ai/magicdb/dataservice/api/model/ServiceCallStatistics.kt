package ai.magicdb.dataservice.api.model

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 服务调用统计
 *
 * @author magicdb
 */
data class ServiceCallStatistics(
    /**
     * 服务ID
     */
    val serviceId: String,
    
    /**
     * 服务名称
     */
    val serviceName: String,
    
    /**
     * 总调用次数
     */
    val totalCalls: Long,
    
    /**
     * 成功调用次数
     */
    val successCalls: Long,
    
    /**
     * 失败调用次数
     */
    val failedCalls: Long,
    
    /**
     * 成功率
     */
    val successRate: Double,
    
    /**
     * 平均执行时间（毫秒）
     */
    val avgExecutionTime: Double,
    
    /**
     * 最大执行时间（毫秒）
     */
    val maxExecutionTime: Long,
    
    /**
     * 最小执行时间（毫秒）
     */
    val minExecutionTime: Long,
    
    /**
     * 最后调用时间
     */
    val lastCalledTime: LocalDateTime,
    
    /**
     * 统计开始时间
     */
    val startTime: LocalDateTime,
    
    /**
     * 统计结束时间
     */
    val endTime: LocalDateTime,
    
    /**
     * 调用用户数
     */
    val uniqueUsers: Int,
    
    /**
     * 调用IP数
     */
    val uniqueIps: Int,
    
    /**
     * 按小时统计的调用次数
     */
    val callsByHour: Map<Int, Long> = emptyMap(),
    
    /**
     * 按天统计的调用次数
     */
    val callsByDay: Map<String, Long> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
