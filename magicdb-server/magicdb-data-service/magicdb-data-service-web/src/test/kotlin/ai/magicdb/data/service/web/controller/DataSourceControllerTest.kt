package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.DataSourceService
import ai.magicdb.data.service.api.model.ColumnInfo
import ai.magicdb.data.service.api.model.DataSourceInfo
import ai.magicdb.data.service.api.model.DatabaseInfo
import ai.magicdb.data.service.api.model.TableInfo
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * 数据源控制器测试
 *
 * @author magicdb
 */
@WebMvcTest(DataSourceController::class)
class DataSourceControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockBean
    private lateinit var dataSourceService: DataSourceService
    
    @BeforeEach
    fun setUp() {
        // 设置模拟对象的行为
    }
    
    @Test
    fun testGetAllDataSources() {
        // 准备测试数据
        val dataSource1 = createTestDataSourceInfo(1L, "MySQL数据源")
        val dataSource2 = createTestDataSourceInfo(2L, "PostgreSQL数据源")
        val dataSources = listOf(dataSource1, dataSource2)
        
        // 设置模拟对象的行为
        `when`(dataSourceService.getAllDataSources()).thenReturn(dataSources)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/datasource/list")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"MySQL数据源\""))
        assert(response.contains("\"PostgreSQL数据源\""))
        
        // 验证方法调用
        verify(dataSourceService).getAllDataSources()
    }
    
    @Test
    fun testGetDataSource() {
        // 准备测试数据
        val dataSourceId = 1L
        val dataSource = createTestDataSourceInfo(dataSourceId, "MySQL数据源")
        
        // 设置模拟对象的行为
        `when`(dataSourceService.getDataSource(dataSourceId)).thenReturn(dataSource)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/datasource/$dataSourceId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"MySQL数据源\""))
        
        // 验证方法调用
        verify(dataSourceService).getDataSource(dataSourceId)
    }
    
    @Test
    fun testGetDatabases() {
        // 准备测试数据
        val dataSourceId = 1L
        val database1 = createTestDatabaseInfo("db1")
        val database2 = createTestDatabaseInfo("db2")
        val databases = listOf(database1, database2)
        
        // 设置模拟对象的行为
        `when`(dataSourceService.getDatabases(dataSourceId)).thenReturn(databases)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/datasource/$dataSourceId/databases")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"db1\""))
        assert(response.contains("\"db2\""))
        
        // 验证方法调用
        verify(dataSourceService).getDatabases(dataSourceId)
    }
    
    @Test
    fun testGetTables() {
        // 准备测试数据
        val dataSourceId = 1L
        val databaseName = "test"
        val table1 = createTestTableInfo("table1")
        val table2 = createTestTableInfo("table2")
        val tables = listOf(table1, table2)
        
        // 设置模拟对象的行为
        `when`(dataSourceService.getTables(dataSourceId, databaseName)).thenReturn(tables)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/datasource/$dataSourceId/databases/$databaseName/tables")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"table1\""))
        assert(response.contains("\"table2\""))
        
        // 验证方法调用
        verify(dataSourceService).getTables(dataSourceId, databaseName)
    }
    
    @Test
    fun testGetColumns() {
        // 准备测试数据
        val dataSourceId = 1L
        val databaseName = "test"
        val tableName = "users"
        val column1 = createTestColumnInfo("id")
        val column2 = createTestColumnInfo("name")
        val columns = listOf(column1, column2)
        
        // 设置模拟对象的行为
        `when`(dataSourceService.getColumns(dataSourceId, databaseName, tableName)).thenReturn(columns)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/datasource/$dataSourceId/databases/$databaseName/tables/$tableName/columns")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"id\""))
        assert(response.contains("\"name\""))
        
        // 验证方法调用
        verify(dataSourceService).getColumns(dataSourceId, databaseName, tableName)
    }
    
    @Test
    fun testExecuteQuery() {
        // 准备测试数据
        val dataSourceId = 1L
        val databaseName = "test"
        val sql = "SELECT * FROM users"
        val result = mapOf(
            "success" to true,
            "data" to listOf(mapOf("id" to 1, "name" to "John")),
            "columns" to listOf("id", "name"),
            "total" to 1,
            "sql" to sql,
            "executionTime" to 10L
        )
        
        // 设置模拟对象的行为
        `when`(dataSourceService.executeQuery(dataSourceId, databaseName, sql)).thenReturn(result)
        
        // 执行请求
        val response = mockMvc.perform(post("/api/data-service/datasource/$dataSourceId/databases/$databaseName/query")
            .contentType(MediaType.APPLICATION_JSON)
            .content(sql))
            .andExpect(status().isOk())
            .andReturn()
            .response.contentAsString
        
        // 验证结果
        assert(response.contains("\"success\":true"))
        assert(response.contains("\"John\""))
        
        // 验证方法调用
        verify(dataSourceService).executeQuery(dataSourceId, databaseName, sql)
    }
    
    @Test
    fun testTestConnection() {
        // 准备测试数据
        val dataSourceId = 1L
        
        // 设置模拟对象的行为
        `when`(dataSourceService.testConnection(dataSourceId)).thenReturn(true)
        
        // 执行请求
        val result = mockMvc.perform(get("/api/data-service/datasource/$dataSourceId/test")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
        
        // 验证结果
        val response = result.response.contentAsString
        assert(response.contains("\"success\":true"))
        
        // 验证方法调用
        verify(dataSourceService).testConnection(dataSourceId)
    }
    
    /**
     * 创建测试数据源信息
     */
    private fun createTestDataSourceInfo(id: Long, name: String): DataSourceInfo {
        return DataSourceInfo(
            id = id,
            name = name,
            type = "MySQL",
            url = "jdbc:mysql://localhost:3306/test",
            host = "localhost",
            port = "3306",
            username = "root",
            password = "",
            databaseName = "test",
            driverClassName = "com.mysql.cj.jdbc.Driver",
            description = "测试数据源",
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1L,
            enabled = true,
            extendInfo = mapOf("key1" to "value1")
        )
    }
    
    /**
     * 创建测试数据库信息
     */
    private fun createTestDatabaseInfo(name: String): DatabaseInfo {
        return DatabaseInfo(
            name = name,
            description = "测试数据库",
            type = "MySQL",
            version = "8.0",
            size = 1024,
            tableCount = 10,
            charset = "utf8mb4",
            collation = "utf8mb4_general_ci",
            extendInfo = mapOf("key1" to "value1")
        )
    }
    
    /**
     * 创建测试表信息
     */
    private fun createTestTableInfo(name: String): TableInfo {
        return TableInfo(
            name = name,
            description = "测试表",
            type = "BASE TABLE",
            engine = "InnoDB",
            size = 1024,
            rowCount = 100,
            charset = "utf8mb4",
            collation = "utf8mb4_general_ci",
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            extendInfo = mapOf("key1" to "value1")
        )
    }
    
    /**
     * 创建测试列信息
     */
    private fun createTestColumnInfo(name: String): ColumnInfo {
        return ColumnInfo(
            name = name,
            description = "测试列",
            type = "VARCHAR",
            length = 255,
            precision = 0,
            scale = 0,
            primaryKey = name == "id",
            nullable = name != "id",
            defaultValue = null,
            autoIncrement = name == "id",
            position = if (name == "id") 1 else 2,
            extendInfo = mapOf("key1" to "value1")
        )
    }
}
