package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 转换请求
 *
 * @author magicdb
 */
data class TransformationRequest(
    /**
     * 源数据
     */
    val sourceData: Any,
    
    /**
     * 源格式
     */
    val sourceFormat: String,
    
    /**
     * 目标格式
     */
    val targetFormat: String,
    
    /**
     * 转换类型
     */
    val transformationType: String = "default",
    
    /**
     * 转换规则
     */
    val rules: Map<String, Any?> = emptyMap(),
    
    /**
     * 转换选项
     */
    val options: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
