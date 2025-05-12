package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 调度器状态
 *
 * @author magicdb
 */
enum class SchedulerStatus {
    /**
     * 运行中
     */
    RUNNING,
    
    /**
     * 已暂停
     */
    PAUSED,
    
    /**
     * 已关闭
     */
    SHUTDOWN,
    
    /**
     * 待机中
     */
    STANDBY,
    
    /**
     * 错误
     */
    ERROR
}

/**
 * 调度器统计信息
 *
 * @author magicdb
 */
data class SchedulerStats(
    /**
     * 总任务数
     */
    var totalTaskCount: Int = 0,
    
    /**
     * 运行中任务数
     */
    var runningTaskCount: Int = 0,
    
    /**
     * 等待中任务数
     */
    var waitingTaskCount: Int = 0,
    
    /**
     * 暂停任务数
     */
    var pausedTaskCount: Int = 0,
    
    /**
     * 失败任务数
     */
    var failedTaskCount: Int = 0,
    
    /**
     * 总执行次数
     */
    var totalExecutionCount: Long = 0,
    
    /**
     * 成功执行次数
     */
    var successExecutionCount: Long = 0,
    
    /**
     * 失败执行次数
     */
    var failedExecutionCount: Long = 0,
    
    /**
     * 平均执行时间（毫秒）
     */
    var averageExecutionTime: Long = 0,
    
    /**
     * 最长执行时间（毫秒）
     */
    var maxExecutionTime: Long = 0,
    
    /**
     * 最短执行时间（毫秒）
     */
    var minExecutionTime: Long = 0,
    
    /**
     * 调度器启动时间
     */
    var startTime: Long = 0,
    
    /**
     * 调度器运行时间（毫秒）
     */
    var uptime: Long = 0,
    
    /**
     * 线程池大小
     */
    var threadPoolSize: Int = 0,
    
    /**
     * 活跃线程数
     */
    var activeThreadCount: Int = 0,
    
    /**
     * 队列中的任务数
     */
    var queuedTaskCount: Int = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
