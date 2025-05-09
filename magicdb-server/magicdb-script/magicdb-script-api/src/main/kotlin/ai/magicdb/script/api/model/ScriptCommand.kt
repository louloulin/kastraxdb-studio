package ai.magicdb.script.api.model

import java.io.Serializable

/**
 * 脚本执行命令
 *
 * @author magicdb
 */
data class ScriptCommand(
    /**
     * 脚本语言
     */
    var language: String? = null,

    /**
     * 脚本内容
     */
    var script: String? = null,

    /**
     * 脚本参数
     */
    var parameters: Map<String, Any?>? = null,

    /**
     * 数据源ID
     */
    var dataSourceId: Long? = null,

    /**
     * 数据库名称
     */
    var databaseName: String? = null,

    /**
     * schema名称
     */
    var schemaName: String? = null,

    /**
     * 表名
     */
    var tableName: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
