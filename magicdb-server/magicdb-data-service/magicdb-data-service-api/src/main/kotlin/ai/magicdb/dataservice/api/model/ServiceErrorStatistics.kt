package ai.magicdb.dataservice.api.model

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 服务错误统计
 *
 * @author magicdb
 */
data class ServiceErrorStatistics(
    /**
     * 服务ID
     */
    val serviceId: String,
    
    /**
     * 服务名称
     */
    val serviceName: String,
    
    /**
     * 总错误次数
     */
    val totalErrors: Long,
    
    /**
     * 错误率
     */
    val errorRate: Double,
    
    /**
     * 错误类型统计
     */
    val errorTypes: Map<String, Long>,
    
    /**
     * 最常见的错误消息
     */
    val mostCommonErrors: List<ErrorInfo>,
    
    /**
     * 最后错误时间
     */
    val lastErrorTime: LocalDateTime,
    
    /**
     * 统计开始时间
     */
    val startTime: LocalDateTime,
    
    /**
     * 统计结束时间
     */
    val endTime: LocalDateTime,
    
    /**
     * 按小时统计的错误次数
     */
    val errorsByHour: Map<Int, Long> = emptyMap(),
    
    /**
     * 按天统计的错误次数
     */
    val errorsByDay: Map<String, Long> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
    
    /**
     * 错误信息
     */
    data class ErrorInfo(
        /**
         * 错误消息
         */
        val message: String,
        
        /**
         * 出现次数
         */
        val count: Long,
        
        /**
         * 最后出现时间
         */
        val lastOccurrence: LocalDateTime
    ) : Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }
}
