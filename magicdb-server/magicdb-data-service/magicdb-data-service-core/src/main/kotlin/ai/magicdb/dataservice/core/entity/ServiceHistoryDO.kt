package ai.magicdb.dataservice.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.util.*

/**
 * 服务历史记录实体
 *
 * @author magicdb
 */
@TableName("DS_SERVICE_HISTORY")
data class ServiceHistoryDO(
    /**
     * 历史记录ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    var id: Long? = null,

    /**
     * 服务ID
     */
    var serviceId: String = "",

    /**
     * 版本号
     */
    var version: Int = 1,

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
    var type: String = "query",

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
     * 创建者ID
     */
    var createUserId: Long? = null,

    /**
     * 修改说明
     */
    var comment: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
