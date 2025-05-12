package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 列信息
 *
 * @author magicdb
 */
data class ColumnInfo(
    /**
     * 列名
     */
    val name: String = "",
    
    /**
     * 列描述
     */
    val description: String = "",
    
    /**
     * 列类型
     */
    val type: String = "",
    
    /**
     * 列长度
     */
    val length: Int = 0,
    
    /**
     * 列精度
     */
    val precision: Int = 0,
    
    /**
     * 列小数位数
     */
    val scale: Int = 0,
    
    /**
     * 是否为主键
     */
    val primaryKey: Boolean = false,
    
    /**
     * 是否允许为空
     */
    val nullable: Boolean = true,
    
    /**
     * 默认值
     */
    val defaultValue: String? = null,
    
    /**
     * 是否自增
     */
    val autoIncrement: Boolean = false,
    
    /**
     * 列位置
     */
    val position: Int = 0,
    
    /**
     * 扩展信息
     */
    val extendInfo: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
