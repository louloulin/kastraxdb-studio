package ai.magicdb.script.api.model

import java.io.Serializable
import java.util.*

/**
 * 脚本版本
 *
 * @author magicdb
 */
data class ScriptVersion(
    /**
     * 版本ID
     */
    var id: String = UUID.randomUUID().toString(),

    /**
     * 脚本ID
     */
    var scriptId: String = "",

    /**
     * 版本号
     */
    var version: Int = 1,

    /**
     * 脚本内容
     */
    var content: String = "",

    /**
     * 脚本语言
     */
    var language: String = "js",

    /**
     * 版本描述
     */
    var description: String = "",

    /**
     * 创建时间
     */
    var createTime: Date = Date(),

    /**
     * 创建者
     */
    var creator: String? = null,

    /**
     * 是否当前版本
     */
    var current: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
