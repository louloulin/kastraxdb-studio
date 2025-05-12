package ai.magicdb.data.service.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.util.*

/**
 * 服务参数实体
 *
 * @author magicdb
 */
@TableName("DS_SERVICE_PARAMETER")
data class ServiceParameterDO(
    /**
     * 参数ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    var id: Long? = null,

    /**
     * 服务ID
     */
    var serviceId: String = "",

    /**
     * 参数名称
     */
    var name: String = "",

    /**
     * 参数类型
     */
    var type: String = "string",

    /**
     * 参数描述
     */
    var description: String = "",

    /**
     * 默认值
     */
    var defaultValue: String? = null,

    /**
     * 是否必填
     */
    var required: Boolean = false,

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
    var gmtModified: Date = Date()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
