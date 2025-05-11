package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 文档模板
 *
 * @author magicdb
 */
data class DocumentTemplate(
    /**
     * 模板ID
     */
    var id: String = "",
    
    /**
     * 模板名称
     */
    var name: String = "",
    
    /**
     * 模板内容
     */
    var content: String = "",
    
    /**
     * 模板格式（如：markdown, html）
     */
    var format: String = "markdown",
    
    /**
     * 模板类型（如：service, group, api）
     */
    var type: String = "service",
    
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
     * 是否系统默认
     */
    var isSystem: Boolean = false,
    
    /**
     * 是否启用
     */
    var enabled: Boolean = true,
    
    /**
     * 排序
     */
    var sort: Int = 0,
    
    /**
     * 描述
     */
    var description: String = ""
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
