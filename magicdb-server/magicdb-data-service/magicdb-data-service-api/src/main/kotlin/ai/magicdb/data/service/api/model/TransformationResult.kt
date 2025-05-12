package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 转换结果
 *
 * @author magicdb
 */
data class TransformationResult(
    /**
     * 是否成功
     */
    val success: Boolean,
    
    /**
     * 目标数据
     */
    val targetData: Any? = null,
    
    /**
     * 目标格式
     */
    val targetFormat: String = "",
    
    /**
     * 错误信息
     */
    val errorMessage: String? = null,
    
    /**
     * 警告信息
     */
    val warnings: List<String> = emptyList(),
    
    /**
     * 转换耗时（毫秒）
     */
    val duration: Long = 0,
    
    /**
     * 转换统计信息
     */
    val statistics: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
        
        /**
         * 创建成功结果
         */
        fun success(targetData: Any?, targetFormat: String, duration: Long = 0, warnings: List<String> = emptyList(), statistics: Map<String, Any?> = emptyMap()): TransformationResult {
            return TransformationResult(
                success = true,
                targetData = targetData,
                targetFormat = targetFormat,
                duration = duration,
                warnings = warnings,
                statistics = statistics
            )
        }
        
        /**
         * 创建失败结果
         */
        fun failure(errorMessage: String, warnings: List<String> = emptyList()): TransformationResult {
            return TransformationResult(
                success = false,
                errorMessage = errorMessage,
                warnings = warnings
            )
        }
    }
}
