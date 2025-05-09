package ai.magicdb.script.api.model

import java.io.Serializable

/**
 * 响应信息
 *
 * @author magicdb
 */
data class ResponseInfo(
    /**
     * 响应类型
     */
    var type: String = "application/json",

    /**
     * 响应描述
     */
    var description: String = "",

    /**
     * 响应示例
     */
    var example: String? = null,

    /**
     * 响应模式
     */
    var schema: Map<String, Any>? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
