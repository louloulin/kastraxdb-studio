package ai.magicdb.data.service.api.model

import java.io.Serializable
import java.util.Date

/**
 * API 测试用例
 */
data class ApiTestCase(
    /**
     * 测试用例 ID
     */
    val id: String,
    
    /**
     * 测试用例名称
     */
    val name: String,
    
    /**
     * 服务 ID
     */
    val serviceId: String,
    
    /**
     * 参数
     */
    val parameters: Map<String, Any?>,
    
    /**
     * 描述
     */
    val description: String? = null,
    
    /**
     * 创建时间
     */
    val createTime: Date = Date(),
    
    /**
     * 更新时间
     */
    val updateTime: Date = Date(),
    
    /**
     * 创建者
     */
    val creator: String? = null,
    
    /**
     * 标签
     */
    val tags: List<String> = emptyList()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * API 测试结果
 */
data class ApiTestResult(
    /**
     * 是否成功
     */
    val success: Boolean,
    
    /**
     * 结果数据
     */
    val data: Any?,
    
    /**
     * 错误消息
     */
    val errorMessage: String? = null,
    
    /**
     * 执行时间（毫秒）
     */
    val executionTime: Long,
    
    /**
     * 请求参数
     */
    val parameters: Map<String, Any?>,
    
    /**
     * 服务 ID
     */
    val serviceId: String,
    
    /**
     * 执行时间
     */
    val timestamp: Date = Date(),
    
    /**
     * 日志
     */
    val logs: List<String> = emptyList()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
