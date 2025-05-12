package ai.magicdb.data.service.api.model

import java.io.Serializable
import java.util.Date

/**
 * 服务统计信息
 */
data class ServiceStatistics(
    /**
     * 服务ID
     */
    val serviceId: String,
    
    /**
     * 服务名称
     */
    val serviceName: String,
    
    /**
     * 总执行次数
     */
    val totalExecutions: Long,
    
    /**
     * 成功执行次数
     */
    val successfulExecutions: Long,
    
    /**
     * 失败执行次数
     */
    val failedExecutions: Long,
    
    /**
     * 平均执行时间（毫秒）
     */
    val averageExecutionTime: Double,
    
    /**
     * 最长执行时间（毫秒）
     */
    val maxExecutionTime: Long,
    
    /**
     * 最短执行时间（毫秒）
     */
    val minExecutionTime: Long,
    
    /**
     * 最后执行时间
     */
    val lastExecutionTime: Date
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 执行记录
 */
data class ExecutionRecord(
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
     * 执行时间（毫秒）
     */
    val executionTime: Long,
    
    /**
     * 是否成功
     */
    val success: Boolean,
    
    /**
     * 错误信息（如果失败）
     */
    val errorMessage: String?,
    
    /**
     * 执行时间
     */
    val timestamp: Date
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 错误统计
 */
data class ErrorStatistics(
    /**
     * 服务ID
     */
    val serviceId: String,
    
    /**
     * 服务名称
     */
    val serviceName: String,
    
    /**
     * 错误消息
     */
    val errorMessage: String,
    
    /**
     * 出现次数
     */
    val occurrences: Long,
    
    /**
     * 首次出现时间
     */
    val firstOccurrence: Date,
    
    /**
     * 最后出现时间
     */
    val lastOccurrence: Date
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
