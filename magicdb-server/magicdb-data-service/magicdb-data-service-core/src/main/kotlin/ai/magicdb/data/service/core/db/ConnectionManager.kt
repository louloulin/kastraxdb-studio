package ai.magicdb.data.service.core.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.ConcurrentHashMap

/**
 * 数据库连接管理器
 *
 * @author magicdb
 */
class ConnectionManager {
    private val logger = LoggerFactory.getLogger(ConnectionManager::class.java)
    
    // 数据源缓存
    private val dataSources = ConcurrentHashMap<Long, HikariDataSource>()
    
    // 线程本地连接
    private val threadLocalConnections = ThreadLocal<MutableMap<Long, Connection>>()
    
    /**
     * 获取数据库连接
     *
     * @param dataSourceId 数据源ID
     * @return 数据库连接
     */
    @Throws(SQLException::class)
    fun getConnection(dataSourceId: Long): Connection {
        // 检查线程本地连接
        val localConnections = threadLocalConnections.get()
        if (localConnections != null && localConnections.containsKey(dataSourceId)) {
            val connection = localConnections[dataSourceId]
            if (connection != null && !connection.isClosed) {
                return connection
            }
        }
        
        // 获取数据源
        val dataSource = dataSources[dataSourceId]
            ?: throw SQLException("数据源不存在: $dataSourceId")
        
        // 获取新连接
        return dataSource.connection
    }
    
    /**
     * 开始事务
     *
     * @param dataSourceId 数据源ID
     * @return 数据库连接
     */
    @Throws(SQLException::class)
    fun beginTransaction(dataSourceId: Long): Connection {
        // 获取数据源
        val dataSource = dataSources[dataSourceId]
            ?: throw SQLException("数据源不存在: $dataSourceId")
        
        // 获取新连接
        val connection = dataSource.connection
        
        // 设置自动提交为false
        connection.autoCommit = false
        
        // 保存到线程本地变量
        var localConnections = threadLocalConnections.get()
        if (localConnections == null) {
            localConnections = mutableMapOf()
            threadLocalConnections.set(localConnections)
        }
        localConnections[dataSourceId] = connection
        
        return connection
    }
    
    /**
     * 提交事务
     *
     * @param dataSourceId 数据源ID
     */
    @Throws(SQLException::class)
    fun commitTransaction(dataSourceId: Long) {
        val localConnections = threadLocalConnections.get()
        if (localConnections != null && localConnections.containsKey(dataSourceId)) {
            val connection = localConnections[dataSourceId]
            if (connection != null && !connection.isClosed) {
                try {
                    connection.commit()
                } finally {
                    closeConnection(connection)
                    localConnections.remove(dataSourceId)
                }
            }
        }
    }
    
    /**
     * 回滚事务
     *
     * @param dataSourceId 数据源ID
     */
    @Throws(SQLException::class)
    fun rollbackTransaction(dataSourceId: Long) {
        val localConnections = threadLocalConnections.get()
        if (localConnections != null && localConnections.containsKey(dataSourceId)) {
            val connection = localConnections[dataSourceId]
            if (connection != null && !connection.isClosed) {
                try {
                    connection.rollback()
                } finally {
                    closeConnection(connection)
                    localConnections.remove(dataSourceId)
                }
            }
        }
    }
    
    /**
     * 关闭连接
     *
     * @param connection 数据库连接
     */
    fun closeConnection(connection: Connection) {
        try {
            if (!connection.isClosed) {
                connection.close()
            }
        } catch (e: SQLException) {
            logger.error("关闭数据库连接出错: {}", e.message, e)
        }
    }
    
    /**
     * 注册数据源
     *
     * @param dataSourceId 数据源ID
     * @param jdbcUrl JDBC URL
     * @param username 用户名
     * @param password 密码
     * @param driverClassName 驱动类名
     * @param maxPoolSize 最大连接数
     * @param minIdle 最小空闲连接数
     * @param connectionTimeout 连接超时时间
     * @param idleTimeout 空闲超时时间
     * @param maxLifetime 最大生命周期
     */
    fun registerDataSource(
        dataSourceId: Long,
        jdbcUrl: String,
        username: String,
        password: String,
        driverClassName: String,
        maxPoolSize: Int = 10,
        minIdle: Int = 5,
        connectionTimeout: Long = 30000,
        idleTimeout: Long = 600000,
        maxLifetime: Long = 1800000
    ) {
        // 关闭已存在的数据源
        val existingDataSource = dataSources[dataSourceId]
        if (existingDataSource != null && !existingDataSource.isClosed) {
            existingDataSource.close()
        }
        
        // 创建HikariCP配置
        val config = HikariConfig()
        config.jdbcUrl = jdbcUrl
        config.username = username
        config.password = password
        config.driverClassName = driverClassName
        config.maximumPoolSize = maxPoolSize
        config.minimumIdle = minIdle
        config.connectionTimeout = connectionTimeout
        config.idleTimeout = idleTimeout
        config.maxLifetime = maxLifetime
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        
        // 创建数据源
        val dataSource = HikariDataSource(config)
        
        // 缓存数据源
        dataSources[dataSourceId] = dataSource
        
        logger.info("注册数据源: {}, URL: {}", dataSourceId, jdbcUrl)
    }
    
    /**
     * 注销数据源
     *
     * @param dataSourceId 数据源ID
     */
    fun unregisterDataSource(dataSourceId: Long) {
        val dataSource = dataSources.remove(dataSourceId)
        if (dataSource != null && !dataSource.isClosed) {
            dataSource.close()
            logger.info("注销数据源: {}", dataSourceId)
        }
    }
    
    /**
     * 关闭所有数据源
     */
    fun closeAll() {
        dataSources.forEach { (id, dataSource) ->
            if (!dataSource.isClosed) {
                dataSource.close()
                logger.info("关闭数据源: {}", id)
            }
        }
        dataSources.clear()
    }
}
