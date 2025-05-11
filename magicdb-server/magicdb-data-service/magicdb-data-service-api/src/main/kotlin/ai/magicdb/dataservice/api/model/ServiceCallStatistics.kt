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
    val serviceId: String = "",

    /**
     * 服务名称
     */
    val serviceName: String = "",

    /**
     * 总调用次数
     */
    val totalCalls: Long = 0,

    /**
     * 成功调用次数
     */
    val successCalls: Long = 0,

    /**
     * 失败调用次数
     */
    val failedCalls: Long = 0,

    /**
     * 成功率
     */
    val successRate: Double = 0.0,

    /**
     * 平均执行时间（毫秒）
     */
    val avgExecutionTime: Double = 0.0,

    /**
     * 最大执行时间（毫秒）
     */
    val maxExecutionTime: Long = 0,

    /**
     * 最小执行时间（毫秒）
     */
    val minExecutionTime: Long = 0,

    /**
     * 最后调用时间
     */
    val lastCalledTime: LocalDateTime? = null,

    /**
     * 统计开始时间
     */
    val startTime: LocalDateTime? = null,

    /**
     * 统计结束时间
     */
    val endTime: LocalDateTime? = null,

    /**
     * 调用用户数
     */
    val uniqueUsers: Int = 0,

    /**
     * 调用IP数
     */
    val uniqueIps: Int = 0,

    /**
     * 按小时统计的调用次数
     */
    val callsByHour: Map<Int, Long> = emptyMap(),

    /**
     * 按天统计的调用次数
     */
    val callsByDay: Map<String, Long> = emptyMap(),

    /**
     * 错误类型统计
     */
    val errorTypes: Map<String, Long> = emptyMap(),

    /**
     * 最常见错误
     */
    val mostCommonErrors: List<Pair<String, Long>> = emptyList(),

    /**
     * 统计时间
     */
    val statsTime: LocalDateTime? = null,

    /**
     * 统计周期（小时）
     */
    val statsPeriod: Int = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
