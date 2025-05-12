package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 文档模板
 */
data class DocumentTemplate(
    /**
     * 模板 ID
     */
    val id: String,
    
    /**
     * 模板名称
     */
    val name: String,
    
    /**
     * 模板描述
     */
    val description: String? = null,
    
    /**
     * 模板类型
     */
    val type: String,
    
    /**
     * 模板内容
     */
    val content: String,
    
    /**
     * 是否默认模板
     */
    val isDefault: Boolean = false,
    
    /**
     * 创建时间
     */
    val createTime: Long = System.currentTimeMillis(),
    
    /**
     * 更新时间
     */
    val updateTime: Long = System.currentTimeMillis()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
