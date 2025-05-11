package ai.magicdb.dataservice.api.model

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 服务流程分组模型
 *
 * @author magicdb
 */
data class ServiceFlowGroup(
    /**
     * 分组ID
     */
    val id: String = "",
    
    /**
     * 分组名称
     */
    val name: String = "",
    
    /**
     * 分组描述
     */
    val description: String = "",
    
    /**
     * 父分组ID
     */
    val parentId: String = "",
    
    /**
     * 分组路径
     */
    val path: String = "",
    
    /**
     * 排序
     */
    val sort: Int = 0,
    
    /**
     * 创建时间
     */
    val createTime: LocalDateTime = LocalDateTime.now(),
    
    /**
     * 更新时间
     */
    val updateTime: LocalDateTime = LocalDateTime.now(),
    
    /**
     * 创建用户
     */
    val createUser: String = "",
    
    /**
     * 更新用户
     */
    val updateUser: String = "",
    
    /**
     * 元数据
     */
    val metadata: Map<String, Any>? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
