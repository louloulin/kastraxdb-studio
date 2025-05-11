package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 服务文档
 *
 * @author magicdb
 */
data class ServiceDocument(
    /**
     * 文档ID
     */
    var id: String = "",
    
    /**
     * 文档标题
     */
    var title: String = "",
    
    /**
     * 文档内容
     */
    var content: String = "",
    
    /**
     * 文档格式（如：markdown, html）
     */
    var format: String = "markdown",
    
    /**
     * 服务ID
     */
    var serviceId: String? = null,
    
    /**
     * 分组ID
     */
    var groupId: String? = null,
    
    /**
     * 创建时间
     */
    var createTime: Long = 0,
    
    /**
     * 更新时间
     */
    var updateTime: Long = 0,
    
    /**
     * 创建用户ID
     */
    var createUserId: Long = 0,
    
    /**
     * 标签
     */
    var tags: List<String> = emptyList(),
    
    /**
     * 是否公开
     */
    var isPublic: Boolean = true,
    
    /**
     * 排序
     */
    var sort: Int = 0,
    
    /**
     * 元数据
     */
    var metadata: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
