package ai.magicdb.script.api.model

import java.io.Serializable
import java.util.*

/**
 * 脚本元数据
 *
 * @author magicdb
 */
data class ScriptMetadata(
    /**
     * 脚本ID
     */
    var scriptId: String = "",

    /**
     * 创建时间
     */
    var createTime: Date = Date(),

    /**
     * 更新时间
     */
    var updateTime: Date = Date(),

    /**
     * 最后执行时间
     */
    var lastExecuteTime: Date? = null,

    /**
     * 执行次数
     */
    var executeCount: Long = 0,

    /**
     * 成功次数
     */
    var successCount: Long = 0,

    /**
     * 失败次数
     */
    var failCount: Long = 0,

    /**
     * 平均执行时间（毫秒）
     */
    var avgExecuteTime: Long = 0,

    /**
     * 最大执行时间（毫秒）
     */
    var maxExecuteTime: Long = 0,

    /**
     * 最小执行时间（毫秒）
     */
    var minExecuteTime: Long = 0,

    /**
     * 依赖脚本ID列表
     */
    var dependencies: List<String> = emptyList(),

    /**
     * 被依赖脚本ID列表
     */
    var dependents: List<String> = emptyList(),

    /**
     * 自定义属性
     */
    var properties: Map<String, String> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
