package ai.magicdb.data.service.api.model

import java.io.Serializable
import java.util.Date

/**
 * 异步任务
 *
 * @author magicdb
 */
data class AsyncTask(
    /**
     * 任务ID
     */
    val id: String,
    
    /**
     * 任务名称
     */
    val name: String,
    
    /**
     * 任务类型
     */
    val type: TaskType,
    
    /**
     * 任务参数
     */
    val parameters: Map<String, Any?>,
    
    /**
     * 任务优先级
     */
    val priority: Int = 0,
    
    /**
     * 超时时间（毫秒）
     */
    val timeout: Long = 0,
    
    /**
     * 创建时间
     */
    val createTime: Date = Date(),
    
    /**
     * 开始时间
     */
    var startTime: Date? = null,
    
    /**
     * 结束时间
     */
    var endTime: Date? = null,
    
    /**
     * 任务状态
     */
    var status: TaskStatus = TaskStatus.PENDING,
    
    /**
     * 任务结果
     */
    var result: Any? = null,
    
    /**
     * 错误信息
     */
    var error: String? = null,
    
    /**
     * 进度（0-100）
     */
    var progress: Int = 0,
    
    /**
     * 日志
     */
    var logs: MutableList<String> = mutableListOf()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 任务类型
 */
enum class TaskType {
    /**
     * 脚本执行
     */
    SCRIPT_EXECUTION,
    
    /**
     * 数据导入
     */
    DATA_IMPORT,
    
    /**
     * 数据导出
     */
    DATA_EXPORT,
    
    /**
     * 数据同步
     */
    DATA_SYNC,
    
    /**
     * 数据处理
     */
    DATA_PROCESSING,
    
    /**
     * 其他
     */
    OTHER
}

/**
 * 任务状态
 */
enum class TaskStatus {
    /**
     * 等待中
     */
    PENDING,
    
    /**
     * 运行中
     */
    RUNNING,
    
    /**
     * 已暂停
     */
    PAUSED,
    
    /**
     * 已取消
     */
    CANCELLED,
    
    /**
     * 已完成
     */
    COMPLETED,
    
    /**
     * 失败
     */
    FAILED,
    
    /**
     * 超时
     */
    TIMEOUT
}
