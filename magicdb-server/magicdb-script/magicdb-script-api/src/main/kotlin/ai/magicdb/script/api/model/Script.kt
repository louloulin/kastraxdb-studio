package ai.magicdb.script.api.model

import java.io.Serializable
import java.util.*

/**
 * 脚本模型
 *
 * @author magicdb
 */
data class Script(
    /**
     * 脚本ID
     */
    var id: String = UUID.randomUUID().toString(),

    /**
     * 脚本名称
     */
    var name: String = "",

    /**
     * 脚本内容
     */
    var content: String = "",

    /**
     * 脚本语言
     */
    var language: String = "js",

    /**
     * 脚本描述
     */
    var description: String = "",

    /**
     * 脚本分组ID
     */
    var groupId: String? = null,

    /**
     * 脚本标签
     */
    var tags: List<String> = emptyList(),

    /**
     * 脚本参数
     */
    var parameters: List<ScriptParameter> = emptyList(),

    /**
     * 脚本返回类型
     */
    var returnType: String? = null,

    /**
     * 脚本版本
     */
    var version: Int = 1,

    /**
     * 创建时间
     */
    var createTime: Date = Date(),

    /**
     * 更新时间
     */
    var updateTime: Date = Date(),

    /**
     * 创建者
     */
    var creator: String? = null,

    /**
     * 更新者
     */
    var updater: String? = null,

    /**
     * 是否启用
     */
    var enabled: Boolean = true,

    /**
     * 元数据
     */
    var metadata: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
