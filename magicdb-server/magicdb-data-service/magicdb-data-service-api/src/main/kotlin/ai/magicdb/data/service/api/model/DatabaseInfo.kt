package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 数据库信息
 *
 * @author magicdb
 */
data class DatabaseInfo(
    /**
     * 数据库名称
     */
    val name: String = "",
    
    /**
     * 数据库描述
     */
    val description: String = "",
    
    /**
     * 数据库类型
     */
    val type: String = "",
    
    /**
     * 数据库版本
     */
    val version: String = "",
    
    /**
     * 数据库大小
     */
    val size: Long = 0,
    
    /**
     * 表数量
     */
    val tableCount: Int = 0,
    
    /**
     * 字符集
     */
    val charset: String = "",
    
    /**
     * 排序规则
     */
    val collation: String = "",
    
    /**
     * 扩展信息
     */
    val extendInfo: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
