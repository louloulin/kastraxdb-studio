package ai.magicdb.dataservice.core.datasource

import ai.magicdb.dataservice.api.model.DataSourceInfo
import ai.magicdb.dataservice.api.model.DatabaseInfo
import ai.magicdb.server.domain.api.model.DataSource
import ai.magicdb.server.domain.api.service.DataSourceService as DomainDataSourceService
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import ai.magicdb.spi.model.Database
import ai.magicdb.spi.model.KeyValue
import ai.magicdb.spi.model.SSHInfo
import ai.magicdb.spi.model.SSLInfo
import ai.magicdb.spi.model.Table
import ai.magicdb.spi.model.TableColumn
import ai.magicdb.spi.sql.ConnectInfo
import ai.magicdb.spi.sql.MagicDBContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import java.time.LocalDateTime
import java.util.*

/**
 * 默认数据源服务测试
 *
 * @author magicdb
 */
class DefaultDataSourceServiceTest {
    
    private lateinit var dataSourceService: DefaultDataSourceService
    private lateinit var domainDataSourceService: DomainDataSourceService
    
    @BeforeEach
    fun setUp() {
        domainDataSourceService = mock(DomainDataSourceService::class.java)
        dataSourceService = DefaultDataSourceService(domainDataSourceService)
        
        // 模拟MagicDBContext
        mockStatic(MagicDBContext::class.java).use { mockedStatic ->
            // 模拟MagicDBContext.putContext
            mockedStatic.`when`<Any> { MagicDBContext.putContext(any()) }.then { }
            
            // 模拟MagicDBContext.removeContext
            mockedStatic.`when`<Any> { MagicDBContext.removeContext() }.then { }
        }
    }
    
    @Test
    fun testGetAllDataSources() {
        // 准备测试数据
        val dataSource1 = createTestDataSource(1L, "MySQL数据源")
        val dataSource2 = createTestDataSource(2L, "PostgreSQL数据源")
        val dataSourceListResult = ListResult.of(listOf(dataSource1, dataSource2))
        
        // 设置模拟对象的行为
        `when`(domainDataSourceService.queryByIds(null)).thenReturn(dataSourceListResult)
        
        // 执行测试
        val result = dataSourceService.getAllDataSources()
        
        // 验证结果
        assertEquals(2, result.size)
        assertEquals("MySQL数据源", result[0].name)
        assertEquals("PostgreSQL数据源", result[1].name)
        
        // 验证方法调用
        verify(domainDataSourceService).queryByIds(null)
    }
    
    @Test
    fun testGetDataSource() {
        // 准备测试数据
        val dataSourceId = 1L
        val dataSource = createTestDataSource(dataSourceId, "MySQL数据源")
        val dataSourceResult = DataResult.of(dataSource)
        
        // 设置模拟对象的行为
        `when`(domainDataSourceService.queryById(dataSourceId)).thenReturn(dataSourceResult)
        
        // 执行测试
        val result = dataSourceService.getDataSource(dataSourceId)
        
        // 验证结果
        assertNotNull(result)
        assertEquals("MySQL数据源", result?.name)
        assertEquals(dataSourceId, result?.id)
        
        // 验证方法调用
        verify(domainDataSourceService).queryById(dataSourceId)
    }
    
    @Test
    fun testGetDatabases() {
        // 准备测试数据
        val dataSourceId = 1L
        val dataSource = createTestDataSource(dataSourceId, "MySQL数据源")
        val dataSourceResult = DataResult.of(dataSource)
        
        val database1 = createTestDatabase("db1")
        val database2 = createTestDatabase("db2")
        val databases = listOf(database1, database2)
        
        // 设置模拟对象的行为
        `when`(domainDataSourceService.queryById(dataSourceId)).thenReturn(dataSourceResult)
        
        // 模拟MagicDBContext
        mockStatic(MagicDBContext::class.java).use { mockedStatic ->
            // 模拟MagicDBContext.putContext
            mockedStatic.`when`<Any> { MagicDBContext.putContext(any()) }.then { }
            
            // 模拟MagicDBContext.removeContext
            mockedStatic.`when`<Any> { MagicDBContext.removeContext() }.then { }
            
            // 模拟MagicDBContext.getConnection
            val connection = mock(Connection::class.java)
            mockedStatic.`when`<Connection> { MagicDBContext.getConnection() }.thenReturn(connection)
            
            // 模拟MagicDBContext.getMetaData
            val metaData = mock(ai.magicdb.spi.MetaData::class.java)
            mockedStatic.`when`<ai.magicdb.spi.MetaData> { MagicDBContext.getMetaData() }.thenReturn(metaData)
            
            // 模拟metaData.databases
            `when`(metaData.databases(connection)).thenReturn(databases)
            
            // 执行测试
            val result = dataSourceService.getDatabases(dataSourceId)
            
            // 验证结果
            assertEquals(2, result.size)
            assertEquals("db1", result[0].name)
            assertEquals("db2", result[1].name)
            
            // 验证方法调用
            verify(domainDataSourceService).queryById(dataSourceId)
            verify(metaData).databases(connection)
        }
    }
    
    @Test
    fun testGetTables() {
        // 准备测试数据
        val dataSourceId = 1L
        val databaseName = "test"
        val dataSource = createTestDataSource(dataSourceId, "MySQL数据源")
        val dataSourceResult = DataResult.of(dataSource)
        
        val table1 = createTestTable("table1")
        val table2 = createTestTable("table2")
        val tables = listOf(table1, table2)
        
        // 设置模拟对象的行为
        `when`(domainDataSourceService.queryById(dataSourceId)).thenReturn(dataSourceResult)
        
        // 模拟MagicDBContext
        mockStatic(MagicDBContext::class.java).use { mockedStatic ->
            // 模拟MagicDBContext.putContext
            mockedStatic.`when`<Any> { MagicDBContext.putContext(any()) }.then { }
            
            // 模拟MagicDBContext.removeContext
            mockedStatic.`when`<Any> { MagicDBContext.removeContext() }.then { }
            
            // 模拟MagicDBContext.getConnection
            val connection = mock(Connection::class.java)
            mockedStatic.`when`<Connection> { MagicDBContext.getConnection() }.thenReturn(connection)
            
            // 模拟MagicDBContext.getMetaData
            val metaData = mock(ai.magicdb.spi.MetaData::class.java)
            mockedStatic.`when`<ai.magicdb.spi.MetaData> { MagicDBContext.getMetaData() }.thenReturn(metaData)
            
            // 模拟metaData.tables
            `when`(metaData.tables(connection, databaseName, null, null)).thenReturn(tables)
            
            // 执行测试
            val result = dataSourceService.getTables(dataSourceId, databaseName)
            
            // 验证结果
            assertEquals(2, result.size)
            assertEquals("table1", result[0].name)
            assertEquals("table2", result[1].name)
            
            // 验证方法调用
            verify(domainDataSourceService).queryById(dataSourceId)
            verify(metaData).tables(connection, databaseName, null, null)
        }
    }
    
    @Test
    fun testGetColumns() {
        // 准备测试数据
        val dataSourceId = 1L
        val databaseName = "test"
        val tableName = "users"
        val dataSource = createTestDataSource(dataSourceId, "MySQL数据源")
        val dataSourceResult = DataResult.of(dataSource)
        
        val column1 = createTestColumn("id")
        val column2 = createTestColumn("name")
        val columns = listOf(column1, column2)
        
        // 设置模拟对象的行为
        `when`(domainDataSourceService.queryById(dataSourceId)).thenReturn(dataSourceResult)
        
        // 模拟MagicDBContext
        mockStatic(MagicDBContext::class.java).use { mockedStatic ->
            // 模拟MagicDBContext.putContext
            mockedStatic.`when`<Any> { MagicDBContext.putContext(any()) }.then { }
            
            // 模拟MagicDBContext.removeContext
            mockedStatic.`when`<Any> { MagicDBContext.removeContext() }.then { }
            
            // 模拟MagicDBContext.getConnection
            val connection = mock(Connection::class.java)
            mockedStatic.`when`<Connection> { MagicDBContext.getConnection() }.thenReturn(connection)
            
            // 模拟MagicDBContext.getMetaData
            val metaData = mock(ai.magicdb.spi.MetaData::class.java)
            mockedStatic.`when`<ai.magicdb.spi.MetaData> { MagicDBContext.getMetaData() }.thenReturn(metaData)
            
            // 模拟metaData.columns
            `when`(metaData.columns(connection, databaseName, null, tableName)).thenReturn(columns)
            
            // 执行测试
            val result = dataSourceService.getColumns(dataSourceId, databaseName, tableName)
            
            // 验证结果
            assertEquals(2, result.size)
            assertEquals("id", result[0].name)
            assertEquals("name", result[1].name)
            
            // 验证方法调用
            verify(domainDataSourceService).queryById(dataSourceId)
            verify(metaData).columns(connection, databaseName, null, tableName)
        }
    }
    
    @Test
    fun testExecuteQuery() {
        // 准备测试数据
        val dataSourceId = 1L
        val databaseName = "test"
        val sql = "SELECT * FROM users"
        val dataSource = createTestDataSource(dataSourceId, "MySQL数据源")
        val dataSourceResult = DataResult.of(dataSource)
        
        // 设置模拟对象的行为
        `when`(domainDataSourceService.queryById(dataSourceId)).thenReturn(dataSourceResult)
        
        // 模拟MagicDBContext
        mockStatic(MagicDBContext::class.java).use { mockedStatic ->
            // 模拟MagicDBContext.putContext
            mockedStatic.`when`<Any> { MagicDBContext.putContext(any()) }.then { }
            
            // 模拟MagicDBContext.removeContext
            mockedStatic.`when`<Any> { MagicDBContext.removeContext() }.then { }
            
            // 模拟MagicDBContext.getConnection
            val connection = mock(Connection::class.java)
            mockedStatic.`when`<Connection> { MagicDBContext.getConnection() }.thenReturn(connection)
            
            // 模拟Statement
            val statement = mock(Statement::class.java)
            `when`(connection.createStatement()).thenReturn(statement)
            `when`(statement.execute(sql)).thenReturn(true)
            
            // 模拟ResultSet
            val resultSet = mock(ResultSet::class.java)
            `when`(statement.resultSet).thenReturn(resultSet)
            
            // 模拟ResultSetMetaData
            val metaData = mock(ResultSetMetaData::class.java)
            `when`(resultSet.metaData).thenReturn(metaData)
            `when`(metaData.columnCount).thenReturn(2)
            `when`(metaData.getColumnName(1)).thenReturn("id")
            `when`(metaData.getColumnName(2)).thenReturn("name")
            
            // 模拟ResultSet.next()
            `when`(resultSet.next()).thenReturn(true).thenReturn(false)
            `when`(resultSet.getObject(1)).thenReturn(1)
            `when`(resultSet.getObject(2)).thenReturn("John")
            
            // 执行测试
            val result = dataSourceService.executeQuery(dataSourceId, databaseName, sql)
            
            // 验证结果
            assertEquals(true, result["success"])
            
            @Suppress("UNCHECKED_CAST")
            val data = result["data"] as List<Map<String, Any?>>
            assertEquals(1, data.size)
            assertEquals(1, data[0]["id"])
            assertEquals("John", data[0]["name"])
            
            @Suppress("UNCHECKED_CAST")
            val columns = result["columns"] as List<String>
            assertEquals(2, columns.size)
            assertEquals("id", columns[0])
            assertEquals("name", columns[1])
            
            // 验证方法调用
            verify(domainDataSourceService).queryById(dataSourceId)
            verify(connection).createStatement()
            verify(statement).execute(sql)
            verify(resultSet).next()
            verify(resultSet).getObject(1)
            verify(resultSet).getObject(2)
        }
    }
    
    @Test
    fun testTestConnection() {
        // 准备测试数据
        val dataSourceId = 1L
        val dataSource = createTestDataSource(dataSourceId, "MySQL数据源")
        val dataSourceResult = DataResult.of(dataSource)
        
        // 设置模拟对象的行为
        `when`(domainDataSourceService.queryById(dataSourceId)).thenReturn(dataSourceResult)
        
        // 模拟MagicDBContext
        mockStatic(MagicDBContext::class.java).use { mockedStatic ->
            // 模拟MagicDBContext.putContext
            mockedStatic.`when`<Any> { MagicDBContext.putContext(any()) }.then { }
            
            // 模拟MagicDBContext.removeContext
            mockedStatic.`when`<Any> { MagicDBContext.removeContext() }.then { }
            
            // 模拟MagicDBContext.getConnection
            val connection = mock(Connection::class.java)
            mockedStatic.`when`<Connection> { MagicDBContext.getConnection() }.thenReturn(connection)
            
            // 模拟connection.isValid
            `when`(connection.isValid(5)).thenReturn(true)
            
            // 执行测试
            val result = dataSourceService.testConnection(dataSourceId)
            
            // 验证结果
            assertTrue(result)
            
            // 验证方法调用
            verify(domainDataSourceService).queryById(dataSourceId)
            verify(connection).isValid(5)
        }
    }
    
    /**
     * 创建测试数据源
     */
    private fun createTestDataSource(id: Long, name: String): DataSource {
        val dataSource = DataSource()
        dataSource.id = id
        dataSource.alias = name
        dataSource.type = "MySQL"
        dataSource.url = "jdbc:mysql://localhost:3306/test"
        dataSource.host = "localhost"
        dataSource.port = "3306"
        dataSource.userName = "root"
        dataSource.password = "password"
        dataSource.databaseName = "test"
        dataSource.driver = "com.mysql.cj.jdbc.Driver"
        dataSource.description = "测试数据源"
        dataSource.gmtCreate = LocalDateTime.now()
        dataSource.gmtModified = LocalDateTime.now()
        dataSource.userId = 1L
        dataSource.ssh = SSHInfo()
        dataSource.ssl = SSLInfo()
        dataSource.jdbc = "8.0"
        
        val extendInfo = ArrayList<KeyValue>()
        extendInfo.add(KeyValue("key1", "value1"))
        dataSource.extendInfo = extendInfo
        
        return dataSource
    }
    
    /**
     * 创建测试数据库
     */
    private fun createTestDatabase(name: String): Database {
        val database = Database()
        database.name = name
        database.comment = "测试数据库"
        database.charset = "utf8mb4"
        database.collation = "utf8mb4_general_ci"
        
        return database
    }
    
    /**
     * 创建测试表
     */
    private fun createTestTable(name: String): Table {
        val table = Table()
        table.name = name
        table.comment = "测试表"
        table.type = "BASE TABLE"
        
        return table
    }
    
    /**
     * 创建测试列
     */
    private fun createTestColumn(name: String): TableColumn {
        val column = TableColumn()
        column.name = name
        column.comment = "测试列"
        column.type = "VARCHAR"
        column.length = 255
        column.precision = 0
        column.scale = 0
        column.primaryKey = name == "id"
        column.nullable = name != "id"
        column.defaultValue = null
        column.autoIncrement = name == "id"
        column.position = if (name == "id") 1 else 2
        
        return column
    }
}
