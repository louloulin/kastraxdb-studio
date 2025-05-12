package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 数据源信息
 *
 * @author magicdb
 */
data class DataSourceInfo(
    /**
     * 数据源ID
     */
    val id: Long = 0,
    
    /**
     * 数据源名称
     */
    val name: String = "",
    
    /**
     * 数据源类型
     */
    val type: String = "",
    
    /**
     * 数据源URL
     */
    val url: String = "",
    
    /**
     * 主机地址
     */
    val host: String = "",
    
    /**
     * 端口
     */
    val port: String = "",
    
    /**
     * 用户名
     */
    val username: String = "",
    
    /**
     * 密码
     */
    val password: String = "",
    
    /**
     * 数据库名称
     */
    val databaseName: String = "",
    
    /**
     * 驱动类名
     */
    val driverClassName: String = "",
    
    /**
     * 描述
     */
    val description: String = "",
    
    /**
     * 创建时间
     */
    val createTime: Long = 0,
    
    /**
     * 更新时间
     */
    val updateTime: Long = 0,
    
    /**
     * 创建用户ID
     */
    val createUserId: Long = 0,
    
    /**
     * 是否启用
     */
    val enabled: Boolean = true,
    
    /**
     * 扩展信息
     */
    val extendInfo: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
