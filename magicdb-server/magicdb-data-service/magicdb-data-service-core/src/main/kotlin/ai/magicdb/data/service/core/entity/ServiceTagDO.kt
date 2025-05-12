package ai.magicdb.data.service.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
import java.util.*

/**
 * 服务标签实体
 *
 * @author magicdb
 */
@TableName("DS_SERVICE_TAG")
data class ServiceTagDO(
    /**
     * 标签ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    var id: Long? = null,

    /**
     * 服务ID
     */
    var serviceId: String = "",

    /**
     * 标签名称
     */
    var name: String = "",

    /**
     * 创建时间
     */
    var gmtCreate: Date = Date()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
