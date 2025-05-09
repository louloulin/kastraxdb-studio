package ai.magicdb.script.api.model

import java.io.Serializable

/**
 * 参数信息
 *
 * @author magicdb
 */
data class ParameterInfo(
    /**
     * 参数名
     */
    var name: String = "",

    /**
     * 参数类型
     */
    var type: String = "string",

    /**
     * 参数描述
     */
    var description: String = "",

    /**
     * 是否必须
     */
    var required: Boolean = false,

    /**
     * 默认值
     */
    var defaultValue: String? = null,

    /**
     * 示例值
     */
    var example: String? = null,

    /**
     * 参数位置
     */
    var position: String = "query"
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
