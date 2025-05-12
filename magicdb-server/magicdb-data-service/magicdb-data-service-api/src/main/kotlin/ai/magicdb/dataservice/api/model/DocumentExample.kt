package ai.magicdb.dataservice.api.model

/**
 * 文档示例
 *
 * @author magicdb
 */
data class DocumentExample(
    /**
     * 示例ID
     */
    val id: String,
    
    /**
     * 示例描述
     */
    val description: String? = null,
    
    /**
     * 请求参数
     */
    val requestParams: String? = null,
    
    /**
     * 响应数据
     */
    val responseData: String? = null
)
