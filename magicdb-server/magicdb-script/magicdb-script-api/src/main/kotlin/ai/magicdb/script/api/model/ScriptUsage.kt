package ai.magicdb.script.api

import java.io.Serializable
import java.util.*

/**
 * 脚本使用情况
 *
 * @author magicdb
 */
data class ScriptUsage(
    /**
     * 脚本ID
     */
    val scriptId: String,

    /**
     * 执行次数
     */
    val executeCount: Long,

    /**
     * 成功次数
     */
    val successCount: Long,

    /**
     * 失败次数
     */
    val failCount: Long,

    /**
     * 平均执行时间（毫秒）
     */
    val avgExecuteTime: Long,

    /**
     * 最大执行时间（毫秒）
     */
    val maxExecuteTime: Long,

    /**
     * 最小执行时间（毫秒）
     */
    val minExecuteTime: Long,

    /**
     * 最后执行时间
     */
    val lastExecuteTime: Date?,

    /**
     * 最近执行记录
     */
    val recentExecutions: List<ExecutionRecord>
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
     * 执行时间
     */
    val executeTime: Date,

    /**
     * 是否成功
     */
    val success: Boolean,

    /**
     * 执行时长（毫秒）
     */
    val duration: Long,

    /**
     * 错误信息
     */
    val errorMessage: String?
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
