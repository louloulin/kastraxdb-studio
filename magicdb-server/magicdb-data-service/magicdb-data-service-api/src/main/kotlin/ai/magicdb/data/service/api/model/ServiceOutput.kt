package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 服务输出定义
 *
 * @author magicdb
 */
data class ServiceOutput(
    /**
     * 输出类型
     */
    var type: String = "json", // json, csv, xml, etc.

    /**
     * 输出描述
     */
    var description: String = "",

    /**
     * 输出示例
     */
    var example: String? = null,

    /**
     * 输出模式
     */
    var schema: Map<String, Any?>? = null,

    /**
     * 输出元数据
     */
    var metadata: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
