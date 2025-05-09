package ai.magicdb.script.api.model

import java.io.Serializable
import java.util.*

/**
 * 脚本分组
 *
 * @author magicdb
 */
data class ScriptGroup(
    /**
     * 分组ID
     */
    var id: String = UUID.randomUUID().toString(),

    /**
     * 分组名称
     */
    var name: String = "",

    /**
     * 分组描述
     */
    var description: String = "",

    /**
     * 父分组ID
     */
    var parentId: String? = null,

    /**
     * 排序
     */
    var order: Int = 0,

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
    var updater: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
