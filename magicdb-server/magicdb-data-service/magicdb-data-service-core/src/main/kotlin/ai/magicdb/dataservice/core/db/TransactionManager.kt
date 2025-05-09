package ai.magicdb.dataservice.core.db

import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

/**
 * 事务管理器
 *
 * @author magicdb
 */
class TransactionManager(private val connectionManager: ConnectionManager) {
    private val logger = LoggerFactory.getLogger(TransactionManager::class.java)
    
    /**
     * 在事务中执行
     *
     * @param dataSourceId 数据源ID
     * @param action 执行操作
     * @return 执行结果
     */
    @Throws(Exception::class)
    fun <T> executeInTransaction(dataSourceId: Long, action: (Connection) -> T): T {
        // 开始事务
        val connection = connectionManager.beginTransaction(dataSourceId)
        
        try {
            // 执行操作
            val result = action(connection)
            
            // 提交事务
            connectionManager.commitTransaction(dataSourceId)
            
            return result
        } catch (e: Exception) {
            // 回滚事务
            try {
                connectionManager.rollbackTransaction(dataSourceId)
            } catch (rollbackEx: SQLException) {
                logger.error("回滚事务出错: {}", rollbackEx.message, rollbackEx)
            }
            
            throw e
        }
    }
    
    /**
     * 在多数据源事务中执行
     *
     * @param dataSourceIds 数据源ID列表
     * @param action 执行操作
     * @return 执行结果
     */
    @Throws(Exception::class)
    fun <T> executeInMultiTransaction(dataSourceIds: List<Long>, action: (Map<Long, Connection>) -> T): T {
        // 开始事务
        val connections = mutableMapOf<Long, Connection>()
        for (dataSourceId in dataSourceIds) {
            connections[dataSourceId] = connectionManager.beginTransaction(dataSourceId)
        }
        
        try {
            // 执行操作
            val result = action(connections)
            
            // 提交事务
            for (dataSourceId in dataSourceIds) {
                connectionManager.commitTransaction(dataSourceId)
            }
            
            return result
        } catch (e: Exception) {
            // 回滚事务
            for (dataSourceId in dataSourceIds) {
                try {
                    connectionManager.rollbackTransaction(dataSourceId)
                } catch (rollbackEx: SQLException) {
                    logger.error("回滚事务出错: {}", rollbackEx.message, rollbackEx)
                }
            }
            
            throw e
        }
    }
}
