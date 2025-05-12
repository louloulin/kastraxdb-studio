package ai.magicdb.dataservice.api.model

/**
 * 文档模板
 *
 * @author magicdb
 */
data class DocumentTemplate(
    /**
     * 模板ID
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
     * 标题模板
     */
    val titleTemplate: String? = null,
    
    /**
     * 描述模板
     */
    val descriptionTemplate: String? = null,
    
    /**
     * 参数模板
     */
    val parameterTemplates: List<ParameterTemplate> = emptyList(),
    
    /**
     * 返回字段模板
     */
    val returnFieldTemplates: List<FieldTemplate> = emptyList(),
    
    /**
     * 注意事项模板
     */
    val notesTemplate: String? = null,
    
    /**
     * 创建时间
     */
    val createTime: Long = System.currentTimeMillis(),
    
    /**
     * 更新时间
     */
    val updateTime: Long = System.currentTimeMillis()
)

/**
 * 参数模板
 */
data class ParameterTemplate(
    /**
     * 参数名模式
     */
    val namePattern: String,
    
    /**
     * 参数类型
     */
    val type: String? = null,
    
    /**
     * 是否必填
     */
    val required: Boolean? = null,
    
    /**
     * 默认值
     */
    val defaultValue: String? = null,
    
    /**
     * 参数描述
     */
    val description: String? = null
)

/**
 * 字段模板
 */
data class FieldTemplate(
    /**
     * 字段名模式
     */
    val namePattern: String,
    
    /**
     * 字段类型
     */
    val type: String? = null,
    
    /**
     * 字段描述
     */
    val description: String? = null
)
