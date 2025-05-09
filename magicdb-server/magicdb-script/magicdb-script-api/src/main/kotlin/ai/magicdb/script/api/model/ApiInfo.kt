package ai.magicdb.script.api.model

import java.io.Serializable
import java.util.*

/**
 * API信息
 *
 * @author magicdb
 */
data class ApiInfo(
    /**
     * API ID
     */
    var id: String = UUID.randomUUID().toString(),

    /**
     * API名称
     */
    var name: String = "",

    /**
     * API路径
     */
    var path: String = "",

    /**
     * 请求方法
     */
    var method: String = "GET",

    /**
     * 分组ID
     */
    var groupId: String = "",

    /**
     * 脚本内容
     */
    var script: String = "",

    /**
     * 脚本语言
     */
    var language: String = "js",

    /**
     * 请求参数
     */
    var parameters: List<ParameterInfo> = emptyList(),

    /**
     * 请求体
     */
    var requestBody: RequestBodyInfo? = null,

    /**
     * 响应信息
     */
    var responseInfo: ResponseInfo? = null,

    /**
     * 描述
     */
    var description: String = "",

    /**
     * 创建时间
     */
    var createTime: Date = Date(),

    /**
     * 更新时间
     */
    var updateTime: Date = Date()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
