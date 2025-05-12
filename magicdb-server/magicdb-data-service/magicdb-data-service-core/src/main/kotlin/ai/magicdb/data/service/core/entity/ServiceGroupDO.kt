package ai.magicdb.data.service.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.util.*

/**
 * 服务分组实体
 *
 * @author magicdb
 */
@TableName("DS_SERVICE_GROUP")
data class ServiceGroupDO(
    /**
     * 分组ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    var id: String = "",

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
    var orderNum: Int = 0,

    /**
     * 创建时间
     */
    var gmtCreate: Date = Date(),

    /**
     * 更新时间
     */
    var gmtModified: Date = Date(),

    /**
     * 创建者ID
     */
    var createUserId: Long? = null,

    /**
     * 更新者ID
     */
    var modifiedUserId: Long? = null,

    /**
     * 元数据，JSON格式
     */
    var metadata: String = "{}"
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
