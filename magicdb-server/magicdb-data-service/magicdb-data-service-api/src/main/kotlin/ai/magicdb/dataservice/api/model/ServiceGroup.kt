package ai.magicdb.dataservice.api.model

import java.io.Serializable
import java.util.*

/**
 * 服务分组
 *
 * @author magicdb
 */
data class ServiceGroup(
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
     * 元数据
     */
    var metadata: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
