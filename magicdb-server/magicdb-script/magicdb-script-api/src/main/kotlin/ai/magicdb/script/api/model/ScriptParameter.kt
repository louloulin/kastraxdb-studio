package ai.magicdb.script.api.model

import java.io.Serializable

/**
 * 脚本参数
 *
 * @author magicdb
 */
data class ScriptParameter(
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
    var example: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
