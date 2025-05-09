package ai.magicdb.script.api.model

import java.io.Serializable

/**
 * 脚本执行结果
 *
 * @author magicdb
 */
data class ScriptResult(
    /**
     * 是否成功
     */
    var success: Boolean? = null,

    /**
     * 错误消息
     */
    var message: String? = null,

    /**
     * 执行的脚本
     */
    var script: String? = null,

    /**
     * 脚本语言
     */
    var language: String? = null,

    /**
     * 执行结果
     */
    var result: Any? = null,

    /**
     * 执行时间（毫秒）
     */
    var duration: Long? = null,

    /**
     * 额外信息
     */
    var extra: Map<String, Any?>? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
