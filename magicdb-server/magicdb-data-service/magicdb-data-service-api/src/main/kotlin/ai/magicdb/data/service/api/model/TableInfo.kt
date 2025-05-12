package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 表信息
 *
 * @author magicdb
 */
data class TableInfo(
    /**
     * 表名
     */
    val name: String = "",
    
    /**
     * 表描述
     */
    val description: String = "",
    
    /**
     * 表类型
     */
    val type: String = "",
    
    /**
     * 表引擎
     */
    val engine: String = "",
    
    /**
     * 表大小
     */
    val size: Long = 0,
    
    /**
     * 行数
     */
    val rowCount: Long = 0,
    
    /**
     * 字符集
     */
    val charset: String = "",
    
    /**
     * 排序规则
     */
    val collation: String = "",
    
    /**
     * 创建时间
     */
    val createTime: Long = 0,
    
    /**
     * 更新时间
     */
    val updateTime: Long = 0,
    
    /**
     * 扩展信息
     */
    val extendInfo: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
