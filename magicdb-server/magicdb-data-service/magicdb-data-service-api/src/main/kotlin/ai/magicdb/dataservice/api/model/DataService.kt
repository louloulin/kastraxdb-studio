package ai.magicdb.dataservice.api.model

import java.io.Serializable
import java.util.*

/**
 * 数据服务定义
 *
 * @author magicdb
 */
data class DataService(
    /**
     * 服务ID
     */
    var id: String = UUID.randomUUID().toString(),

    /**
     * 服务名称
     */
    var name: String = "",

    /**
     * 服务描述
     */
    var description: String = "",

    /**
     * 服务类型
     */
    var type: String = "query", // query, transform, aggregate, etc.

    /**
     * 数据源ID
     */
    var dataSourceId: Long? = null,

    /**
     * 数据库名称
     */
    var databaseName: String? = null,

    /**
     * schema名称
     */
    var schemaName: String? = null,

    /**
     * 表名
     */
    var tableName: String? = null,

    /**
     * 脚本内容
     */
    var script: String = "",

    /**
     * 脚本语言
     */
    var language: String = "js",

    /**
     * 参数定义
     */
    var parameters: List<ServiceParameter> = emptyList(),

    /**
     * 输出定义
     */
    var output: ServiceOutput? = null,

    /**
     * 分组ID
     */
    var groupId: String? = null,

    /**
     * 标签
     */
    var tags: List<String> = emptyList(),

    /**
     * 创建时间
     */
    var createTime: Date = Date(),

    /**
     * 更新时间
     */
    var updateTime: Date = Date(),

    /**
     * 是否启用
     */
    var enabled: Boolean = true,

    /**
     * 执行超时时间（毫秒）
     */
    var timeout: Long = 30000,

    /**
     * 缓存时间（毫秒），0表示不缓存
     */
    var cacheTime: Long = 0,

    /**
     * 并发限制，0表示不限制
     */
    var concurrentLimit: Int = 10,

    /**
     * 元数据
     */
    var metadata: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
