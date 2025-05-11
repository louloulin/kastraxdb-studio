package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.DataSourceService
import ai.magicdb.dataservice.api.model.ColumnInfo
import ai.magicdb.dataservice.api.model.DataSourceInfo
import ai.magicdb.dataservice.api.model.DatabaseInfo
import ai.magicdb.dataservice.api.model.TableInfo
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

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
    
    private lateinit var testDataSource: DataSourceInfo
    private lateinit var testDatabase: DatabaseInfo
    private lateinit var testTable: TableInfo
    private lateinit var testColumn: ColumnInfo
    
    @BeforeEach
    fun setUp() {
        // 创建测试数据
        testDataSource = createTestDataSource(1L)
        testDatabase = createTestDatabaseInfo("test_db")
        testTable = createTestTableInfo("test_table")
        testColumn = createTestColumnInfo("test_column")
    }
    
    @Test
    fun testGetAllDataSources() {
        // 设置模拟对象的行为
        `when`(dataSourceService.getAllDataSources()).thenReturn(listOf(testDataSource))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/datasource")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].name").value("测试数据源"))
        
        // 验证方法调用
        verify(dataSourceService).getAllDataSources()
    }
    
    @Test
    fun testGetDataSource() {
        // 设置模拟对象的行为
        `when`(dataSourceService.getDataSource(1L)).thenReturn(testDataSource)
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/datasource/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("测试数据源"))
        
        // 验证方法调用
        verify(dataSourceService).getDataSource(1L)
    }
    
    @Test
    fun testGetDataSourceNotFound() {
        // 设置模拟对象的行为
        `when`(dataSourceService.getDataSource(999L)).thenReturn(null)
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/datasource/999")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isEmpty)
        
        // 验证方法调用
        verify(dataSourceService).getDataSource(999L)
    }
    
    @Test
    fun testGetDatabases() {
        // 设置模拟对象的行为
        `when`(dataSourceService.getDatabases(1L)).thenReturn(listOf(testDatabase))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/datasource/1/databases")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].name").value("test_db"))
        
        // 验证方法调用
        verify(dataSourceService).getDatabases(1L)
    }
    
    @Test
    fun testGetTables() {
        // 设置模拟对象的行为
        `when`(dataSourceService.getTables(1L, "test_db")).thenReturn(listOf(testTable))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/datasource/1/databases/test_db/tables")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].name").value("test_table"))
        
        // 验证方法调用
        verify(dataSourceService).getTables(1L, "test_db")
    }
    
    @Test
    fun testGetColumns() {
        // 设置模拟对象的行为
        `when`(dataSourceService.getColumns(1L, "test_db", "test_table")).thenReturn(listOf(testColumn))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/datasource/1/databases/test_db/tables/test_table/columns")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].name").value("test_column"))
        
        // 验证方法调用
        verify(dataSourceService).getColumns(1L, "test_db", "test_table")
    }
    
    @Test
    fun testExecuteQuery() {
        // 准备测试数据
        val sql = "SELECT * FROM test_table"
        val queryResult = mapOf(
            "success" to true,
            "data" to listOf(mapOf("id" to 1, "name" to "测试数据")),
            "columns" to listOf("id", "name"),
            "total" to 1
        )
        
        // 设置模拟对象的行为
        `when`(dataSourceService.executeQuery(1L, "test_db", sql)).thenReturn(queryResult)
        
        // 执行请求
        mockMvc.perform(post("/api/data-service/datasource/1/databases/test_db/execute")
            .contentType(MediaType.APPLICATION_JSON)
            .content(sql))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.success").value(true))
            .andExpect(jsonPath("$.data.data[0].id").value(1))
        
        // 验证方法调用
        verify(dataSourceService).executeQuery(1L, "test_db", sql)
    }
    
    @Test
    fun testTestConnection() {
        // 设置模拟对象的行为
        `when`(dataSourceService.testConnection(1L)).thenReturn(true)
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/datasource/1/test")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(true))
        
        // 验证方法调用
        verify(dataSourceService).testConnection(1L)
    }
    
    /**
     * 创建测试数据源信息
     */
    private fun createTestDataSource(id: Long): DataSourceInfo {
        return DataSourceInfo(
            id = id,
            name = "测试数据源",
            type = "MySQL",
            url = "jdbc:mysql://localhost:3306/test",
            host = "localhost",
            port = "3306",
            username = "root",
            password = "password",
            databaseName = "test",
            driverClassName = "com.mysql.cj.jdbc.Driver",
            description = "测试数据源描述",
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1,
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
            primaryKey = false,
            nullable = true,
            defaultValue = null,
            autoIncrement = false,
            position = 1,
            extendInfo = mapOf("key1" to "value1")
        )
    }
}
