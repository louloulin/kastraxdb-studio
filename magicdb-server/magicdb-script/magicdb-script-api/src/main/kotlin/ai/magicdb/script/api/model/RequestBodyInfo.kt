package ai.magicdb.script.api.model

import java.io.Serializable

/**
 * 请求体信息
 *
 * @author magicdb
 */
data class RequestBodyInfo(
    /**
     * 请求体类型
     */
    var type: String = "application/json",

    /**
     * 请求体描述
     */
    var description: String = "",

    /**
     * 请求体示例
     */
    var example: String? = null,

    /**
     * 请求体模式
     */
    var schema: Map<String, Any>? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
