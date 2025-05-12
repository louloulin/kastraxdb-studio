package ai.magicdb.data.service.api.model

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 服务调用记录
 *
 * @author magicdb
 */
data class ServiceCallRecord(
    /**
     * 记录ID
     */
    val id: String = "",

    /**
     * 服务ID
     */
    val serviceId: String = "",

    /**
     * 服务名称
     */
    val serviceName: String? = null,

    /**
     * 调用时间
     */
    val callTime: LocalDateTime = LocalDateTime.now(),

    /**
     * 执行时间（毫秒）
     */
    val executionTime: Long = 0,

    /**
     * 是否成功
     */
    val success: Boolean = true,

    /**
     * 错误消息
     */
    val errorMessage: String? = null,

    /**
     * 错误类型
     */
    val errorType: String? = null,

    /**
     * 用户ID
     */
    val userId: Long? = null,

    /**
     * 客户端IP
     */
    val clientIp: String? = null,

    /**
     * 请求参数
     */
    val parameters: Map<String, Any?>? = null,

    /**
     * 响应结果
     */
    val result: Any? = null,

    /**
     * 调用方法
     */
    val method: String? = null,

    /**
     * 调用路径
     */
    val path: String? = null,

    /**
     * 调用来源
     */
    val source: String? = null,

    /**
     * 会话ID
     */
    val sessionId: String? = null,

    /**
     * 触发类型
     */
    val triggerType: String? = null,

    /**
     * 触发ID
     */
    val triggerId: String? = null,

    /**
     * 元数据
     */
    val metadata: Map<String, Any>? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
