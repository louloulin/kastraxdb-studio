package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 服务执行结果
 *
 * @author magicdb
 */
data class ServiceResult(
    /**
     * 是否成功
     */
    var success: Boolean = true,

    /**
     * 错误消息
     */
    var message: String? = null,

    /**
     * 结果数据
     */
    var data: Any? = null,

    /**
     * 执行时间（毫秒）
     */
    var duration: Long = 0,

    /**
     * 是否来自缓存
     */
    var fromCache: Boolean = false,

    /**
     * 缓存过期时间（毫秒时间戳）
     */
    var cacheExpireTime: Long? = null,

    /**
     * 缓存键
     */
    var cacheKey: String? = null,

    /**
     * 元数据
     */
    var metadata: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
