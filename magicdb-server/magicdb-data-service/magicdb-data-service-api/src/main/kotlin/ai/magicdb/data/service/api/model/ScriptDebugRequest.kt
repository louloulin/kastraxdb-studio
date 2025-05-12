package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 脚本调试请求
 *
 * @author magicdb
 */
data class ScriptDebugRequest(
    /**
     * 脚本内容
     */
    var script: String = "",
    
    /**
     * 脚本语言
     */
    var language: String = "js",
    
    /**
     * 调试参数
     */
    var parameters: Map<String, Any?> = emptyMap(),
    
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
    var tableName: String? = null,
    
    /**
     * 超时时间（毫秒）
     */
    var timeout: Long = 30000
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
