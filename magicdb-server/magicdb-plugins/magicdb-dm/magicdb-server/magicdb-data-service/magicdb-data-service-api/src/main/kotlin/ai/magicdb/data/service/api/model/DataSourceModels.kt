package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 数据源信息
 */
data class DataSourceInfo(
    /**
     * 数据源ID
     */
    val id: Long,
    
    /**
     * 数据源名称
     */
    val name: String,
    
    /**
     * 数据源类型
     */
    val type: String,
    
    /**
     * 数据源URL
     */
    val url: String,
    
    /**
     * 主机
     */
    val host: String,
    
    /**
     * 端口
     */
    val port: Int,
    
    /**
     * 用户名
     */
    val username: String,
    
    /**
     * 数据库名称
     */
    val databaseName: String,
    
    /**
     * 驱动类名
     */
    val driverClassName: String,
    
    /**
     * 创建用户ID
     */
    val createUserId: Long
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 数据库信息
 */
data class DatabaseInfo(
    /**
     * 数据库名称
     */
    val name: String,
    
    /**
     * 数据库描述
     */
    val description: String? = null,
    
    /**
     * 数据库类型
     */
    val type: String
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 表信息
 */
data class TableInfo(
    /**
     * 表名
     */
    val name: String,
    
    /**
     * 表类型（TABLE, VIEW, SYSTEM_TABLE等）
     */
    val type: String,
    
    /**
     * 表描述
     */
    val description: String? = null,
    
    /**
     * 所属数据库
     */
    val databaseName: String,
    
    /**
     * 所属模式
     */
    val schemaName: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 列信息
 */
data class ColumnInfo(
    /**
     * 列名
     */
    val name: String,
    
    /**
     * 列类型
     */
    val type: String,
    
    /**
     * 列描述
     */
    val description: String? = null,
    
    /**
     * 是否为主键
     */
    val isPrimaryKey: Boolean = false,
    
    /**
     * 是否允许为空
     */
    val isNullable: Boolean = true,
    
    /**
     * 默认值
     */
    val defaultValue: String? = null,
    
    /**
     * 列长度
     */
    val length: Int? = null,
    
    /**
     * 列精度
     */
    val precision: Int? = null,
    
    /**
     * 列小数位数
     */
    val scale: Int? = null,
    
    /**
     * 所属表
     */
    val tableName: String,
    
    /**
     * 所属数据库
     */
    val databaseName: String,
    
    /**
     * 所属模式
     */
    val schemaName: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
