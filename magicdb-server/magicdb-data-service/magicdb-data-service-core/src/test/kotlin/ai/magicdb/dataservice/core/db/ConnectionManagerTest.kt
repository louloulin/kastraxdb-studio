package ai.magicdb.dataservice.core.db

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.sql.SQLException

/**
 * 数据库连接管理器测试
 *
 * @author magicdb
 */
class ConnectionManagerTest {
    
    private lateinit var connectionManager: ConnectionManager
    
    @BeforeEach
    fun setUp() {
        connectionManager = ConnectionManager()
        
        // 注册H2内存数据库
        connectionManager.registerDataSource(
            dataSourceId = 1L,
            jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            username = "sa",
            password = "",
            driverClassName = "org.h2.Driver",
            maxPoolSize = 5,
            minIdle = 2
        )
    }
    
    @AfterEach
    fun tearDown() {
        connectionManager.closeAll()
    }
    
    @Test
    fun testGetConnection() {
        // 获取连接
        val connection = connectionManager.getConnection(1L)
        
        // 验证连接
        assertNotNull(connection)
        assertFalse(connection.isClosed)
        
        // 关闭连接
        connection.close()
    }
    
    @Test
    fun testGetConnectionInvalidDataSource() {
        // 获取不存在的数据源连接
        assertThrows<SQLException> {
            connectionManager.getConnection(999L)
        }
    }
    
    @Test
    fun testBeginTransaction() {
        // 开始事务
        val connection = connectionManager.beginTransaction(1L)
        
        // 验证连接
        assertNotNull(connection)
        assertFalse(connection.isClosed)
        assertFalse(connection.autoCommit)
        
        // 提交事务
        connectionManager.commitTransaction(1L)
    }
    
    @Test
    fun testCommitTransaction() {
        // 开始事务
        val connection = connectionManager.beginTransaction(1L)
        
        // 创建表
        val statement = connection.createStatement()
        statement.execute("CREATE TABLE test (id INT, name VARCHAR(255))")
        statement.execute("INSERT INTO test VALUES (1, 'Test')")
        
        // 提交事务
        connectionManager.commitTransaction(1L)
        
        // 验证数据已提交
        val newConnection = connectionManager.getConnection(1L)
        val resultSet = newConnection.createStatement().executeQuery("SELECT * FROM test")
        assertTrue(resultSet.next())
        assertEquals(1, resultSet.getInt("id"))
        assertEquals("Test", resultSet.getString("name"))
        
        // 关闭连接
        newConnection.close()
    }
    
    @Test
    fun testRollbackTransaction() {
        // 开始事务
        val connection = connectionManager.beginTransaction(1L)
        
        // 创建表
        val statement = connection.createStatement()
        statement.execute("CREATE TABLE test2 (id INT, name VARCHAR(255))")
        statement.execute("INSERT INTO test2 VALUES (1, 'Test')")
        
        // 回滚事务
        connectionManager.rollbackTransaction(1L)
        
        // 验证数据已回滚
        val newConnection = connectionManager.getConnection(1L)
        
        // 表应该不存在
        assertThrows<SQLException> {
            newConnection.createStatement().executeQuery("SELECT * FROM test2")
        }
        
        // 关闭连接
        newConnection.close()
    }
    
    @Test
    fun testUnregisterDataSource() {
        // 注册新数据源
        connectionManager.registerDataSource(
            dataSourceId = 2L,
            jdbcUrl = "jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1",
            username = "sa",
            password = "",
            driverClassName = "org.h2.Driver"
        )
        
        // 验证可以获取连接
        val connection = connectionManager.getConnection(2L)
        assertNotNull(connection)
        connection.close()
        
        // 注销数据源
        connectionManager.unregisterDataSource(2L)
        
        // 验证无法获取连接
        assertThrows<SQLException> {
            connectionManager.getConnection(2L)
        }
    }
    
    @Test
    fun testCloseAll() {
        // 注册多个数据源
        connectionManager.registerDataSource(
            dataSourceId = 2L,
            jdbcUrl = "jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1",
            username = "sa",
            password = "",
            driverClassName = "org.h2.Driver"
        )
        
        connectionManager.registerDataSource(
            dataSourceId = 3L,
            jdbcUrl = "jdbc:h2:mem:test3;DB_CLOSE_DELAY=-1",
            username = "sa",
            password = "",
            driverClassName = "org.h2.Driver"
        )
        
        // 验证可以获取连接
        val connection1 = connectionManager.getConnection(1L)
        val connection2 = connectionManager.getConnection(2L)
        val connection3 = connectionManager.getConnection(3L)
        
        assertNotNull(connection1)
        assertNotNull(connection2)
        assertNotNull(connection3)
        
        connection1.close()
        connection2.close()
        connection3.close()
        
        // 关闭所有数据源
        connectionManager.closeAll()
        
        // 验证无法获取连接
        assertThrows<SQLException> {
            connectionManager.getConnection(1L)
        }
        
        assertThrows<SQLException> {
            connectionManager.getConnection(2L)
        }
        
        assertThrows<SQLException> {
            connectionManager.getConnection(3L)
        }
    }
}
