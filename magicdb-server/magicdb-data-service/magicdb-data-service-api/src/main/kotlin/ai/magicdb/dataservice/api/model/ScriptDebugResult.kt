package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 脚本调试结果
 *
 * @author magicdb
 */
data class ScriptDebugResult(
    /**
     * 是否成功
     */
    var success: Boolean = false,
    
    /**
     * 结果数据
     */
    var data: Any? = null,
    
    /**
     * 错误信息
     */
    var message: String? = null,
    
    /**
     * 执行时间（毫秒）
     */
    var duration: Long = 0,
    
    /**
     * 日志信息
     */
    var logs: List<String> = emptyList(),
    
    /**
     * 控制台输出
     */
    var console: List<String> = emptyList(),
    
    /**
     * 错误堆栈
     */
    var stackTrace: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
