package ai.magicdb.data.service.api.model

import java.io.Serializable
import java.util.Date

/**
 * 代码片段
 */
data class CodeSnippet(
    /**
     * 代码片段 ID
     */
    val id: String,
    
    /**
     * 代码片段名称
     */
    val name: String,
    
    /**
     * 代码片段描述
     */
    val description: String? = null,
    
    /**
     * 代码片段内容
     */
    val content: String,
    
    /**
     * 代码片段语言
     */
    val language: String,
    
    /**
     * 代码片段标签
     */
    val tags: List<String> = emptyList(),
    
    /**
     * 是否为系统预定义
     */
    val system: Boolean = false,
    
    /**
     * 创建者
     */
    val creator: String? = null,
    
    /**
     * 创建时间
     */
    val createTime: Date = Date(),
    
    /**
     * 更新时间
     */
    val updateTime: Date = Date()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
