package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 转换模板
 *
 * @author magicdb
 */
data class TransformationTemplate(
    /**
     * 模板ID
     */
    var id: String = "",
    
    /**
     * 模板名称
     */
    var name: String = "",
    
    /**
     * 模板描述
     */
    var description: String = "",
    
    /**
     * 源格式
     */
    var sourceFormat: String = "",
    
    /**
     * 目标格式
     */
    var targetFormat: String = "",
    
    /**
     * 转换类型
     */
    var transformationType: String = "default",
    
    /**
     * 转换规则
     */
    var rules: Map<String, Any?> = emptyMap(),
    
    /**
     * 转换选项
     */
    var options: Map<String, Any?> = emptyMap(),
    
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
     * 是否启用
     */
    var enabled: Boolean = true,
    
    /**
     * 模板标签
     */
    var tags: List<String> = emptyList(),
    
    /**
     * 使用次数
     */
    var useCount: Long = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
