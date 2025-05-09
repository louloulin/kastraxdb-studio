package ai.magicdb.script.api.model

import java.io.Serializable
import java.util.*

/**
 * API分组信息
 *
 * @author magicdb
 */
data class ApiGroupInfo(
    /**
     * 分组ID
     */
    var id: String = UUID.randomUUID().toString(),

    /**
     * 分组名称
     */
    var name: String = "",

    /**
     * 分组路径
     */
    var path: String = "",

    /**
     * 父分组ID
     */
    var parentId: String? = null,

    /**
     * 描述
     */
    var description: String = "",

    /**
     * 创建时间
     */
    var createTime: Date = Date(),

    /**
     * 更新时间
     */
    var updateTime: Date = Date()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
