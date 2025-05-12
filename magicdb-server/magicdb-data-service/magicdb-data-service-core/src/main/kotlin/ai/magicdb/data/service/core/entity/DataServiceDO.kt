package ai.magicdb.data.service.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.util.*

/**
 * 数据服务实体
 *
 * @author magicdb
 */
@TableName("DS_DATA_SERVICE")
data class DataServiceDO(
    /**
     * 服务ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    var id: String = "",

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
     * 分组ID
     */
    var groupId: String? = null,

    /**
     * 创建时间
     */
    var gmtCreate: Date = Date(),

    /**
     * 更新时间
     */
    var gmtModified: Date = Date(),

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
     * 创建者ID
     */
    var createUserId: Long? = null,

    /**
     * 更新者ID
     */
    var modifiedUserId: Long? = null,

    /**
     * 标签，JSON数组格式
     */
    var tags: String = "[]",

    /**
     * 元数据，JSON格式
     */
    var metadata: String = "{}"
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
