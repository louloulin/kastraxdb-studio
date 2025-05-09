package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 服务参数定义
 *
 * @author magicdb
 */
data class ServiceParameter(
    /**
     * 参数名
     */
    var name: String = "",

    /**
     * 参数类型
     */
    var type: String = "string", // string, number, boolean, object, array

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
     * 验证规则
     */
    var validation: String? = null,

    /**
     * 参数元数据
     */
    var metadata: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
