package ai.magicdb.dataservice.api.model

/**
 * 服务文档
 *
 * @author magicdb
 */
data class ServiceDocument(
    /**
     * 文档标题
     */
    val title: String,
    
    /**
     * 文档描述
     */
    val description: String? = null,
    
    /**
     * 参数列表
     */
    val parameters: List<ParameterDocument> = emptyList(),
    
    /**
     * 返回字段列表
     */
    val returnFields: List<FieldDocument> = emptyList(),
    
    /**
     * 示例列表
     */
    val examples: List<DocumentExample> = emptyList(),
    
    /**
     * 注意事项
     */
    val notes: String? = null
)

/**
 * 参数文档
 */
data class ParameterDocument(
    /**
     * 参数名
     */
    val name: String,
    
    /**
     * 参数类型
     */
    val type: String,
    
    /**
     * 是否必填
     */
    val required: Boolean = false,
    
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
 * 字段文档
 */
data class FieldDocument(
    /**
     * 字段名
     */
    val name: String,
    
    /**
     * 字段类型
     */
    val type: String,
    
    /**
     * 字段描述
     */
    val description: String? = null
)
