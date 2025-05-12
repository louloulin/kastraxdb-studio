package ai.magicdb.data.service.core.datasource

import ai.magicdb.data.service.api.DataSourceService
import ai.magicdb.data.service.api.model.ColumnInfo
import ai.magicdb.data.service.api.model.DatabaseInfo
import ai.magicdb.data.service.api.model.DataSourceInfo
import ai.magicdb.data.service.api.model.TableInfo
import ai.magicdb.server.domain.api.service.DataSourceService as DomainDataSourceService
import ai.magicdb.server.domain.api.service.DatabaseService
import ai.magicdb.spi.model.Database
import ai.magicdb.spi.model.Schema
import ai.magicdb.spi.model.Table
import ai.magicdb.spi.model.TableColumn
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.concurrent.ConcurrentHashMap

/**
 * 默认数据源服务实现
 */
@Service
class DefaultDataSourceService(
    private val domainDataSourceService: DomainDataSourceService,
    private val databaseService: DatabaseService
) : DataSourceService {
    
    private val logger = LoggerFactory.getLogger(DefaultDataSourceService::class.java)
    
    // 连接缓存
    private val connectionCache = ConcurrentHashMap<Long, Connection>()
    
    override fun getAllDataSources(): List<DataSourceInfo> {
        try {
            // 获取所有数据源
            val dataSourceListResult = domainDataSourceService.queryByIds(null)
            
            if (dataSourceListResult.getData().isEmpty()) {
                return emptyList()
            }
            
            return dataSourceListResult.getData().map {
                DataSourceInfo(
                    id = it.id,
                    name = it.alias,
                    type = it.type,
                    url = it.url,
                    host = it.host ?: "",
                    port = it.port ?: 0,
                    username = it.userName ?: "",
                    databaseName = "",
                    driverClassName = it.driver ?: "",
                    createUserId = it.userId
                )
            }
        } catch (e: Exception) {
            logger.error("获取所有数据源失败", e)
            return emptyList()
        }
    }
    
    override fun getDataSource(dataSourceId: Long): DataSourceInfo? {
        try {
            // 获取数据源详情
            val dataSourceResult = domainDataSourceService.queryById(dataSourceId)
            
            if (dataSourceResult.getData() == null) {
                return null
            }
            
            val dataSource = dataSourceResult.getData()
            return DataSourceInfo(
                id = dataSource.id,
                name = dataSource.alias,
                type = dataSource.type,
                url = dataSource.url,
                host = dataSource.host ?: "",
                port = dataSource.port ?: 0,
                username = dataSource.userName ?: "",
                databaseName = "",
                driverClassName = dataSource.driver ?: "",
                createUserId = dataSource.userId
            )
        } catch (e: Exception) {
            logger.error("获取数据源失败: {}", dataSourceId, e)
            return null
        }
    }
    
    override fun getDatabases(dataSourceId: Long): List<DatabaseInfo> {
        try {
            // 获取数据库列表
            val connection = getConnection(dataSourceId)
            val databasesResult = domainDataSourceService.connect(dataSourceId)
            
            if (databasesResult.getData().isEmpty()) {
                return emptyList()
            }
            
            return databasesResult.getData().map { database ->
                DatabaseInfo(
                    name = database.name,
                    description = database.description,
                    type = getDataSourceType(dataSourceId)
                )
            }
        } catch (e: Exception) {
            logger.error("获取数据库列表失败: {}", dataSourceId, e)
            return emptyList()
        }
    }
    
    override fun getTables(dataSourceId: Long, databaseName: String): List<TableInfo> {
        try {
            // 获取表列表
            val connection = getConnection(dataSourceId)
            val schemaQueryParam = ai.magicdb.server.domain.api.param.database.SchemaQueryParam()
            schemaQueryParam.dataSourceId = dataSourceId
            schemaQueryParam.dataBaseName = databaseName
            
            val schemasResult = databaseService.querySchema(schemaQueryParam)
            
            if (schemasResult.getData().isEmpty()) {
                return emptyList()
            }
            
            val tables = mutableListOf<TableInfo>()
            
            for (schema in schemasResult.getData()) {
                val tableQueryParam = ai.magicdb.server.domain.api.param.database.TableQueryParam()
                tableQueryParam.dataSourceId = dataSourceId
                tableQueryParam.dataBaseName = databaseName
                tableQueryParam.schemaName = schema.name
                
                val tablesResult = databaseService.queryTable(tableQueryParam)
                
                if (tablesResult.getData().isNotEmpty()) {
                    tables.addAll(tablesResult.getData().map { table ->
                        TableInfo(
                            name = table.name,
                            type = table.type ?: "TABLE",
                            description = table.description,
                            databaseName = databaseName,
                            schemaName = schema.name
                        )
                    })
                }
            }
            
            return tables
        } catch (e: Exception) {
            logger.error("获取表列表失败: {}, {}", dataSourceId, databaseName, e)
            return emptyList()
        }
    }
    
    override fun getColumns(dataSourceId: Long, databaseName: String, tableName: String): List<ColumnInfo> {
        try {
            // 获取列信息
            val connection = getConnection(dataSourceId)
            val columnQueryParam = ai.magicdb.server.domain.api.param.database.ColumnQueryParam()
            columnQueryParam.dataSourceId = dataSourceId
            columnQueryParam.dataBaseName = databaseName
            columnQueryParam.tableName = tableName
            
            val columnsResult = databaseService.queryColumn(columnQueryParam)
            
            if (columnsResult.getData().isEmpty()) {
                return emptyList()
            }
            
            return columnsResult.getData().map { column ->
                ColumnInfo(
                    name = column.name,
                    type = column.type ?: "",
                    description = column.description,
                    isPrimaryKey = column.primaryKey ?: false,
                    isNullable = column.nullable ?: true,
                    defaultValue = column.defaultValue,
                    length = column.length,
                    precision = column.precision,
                    scale = column.scale,
                    tableName = tableName,
                    databaseName = databaseName,
                    schemaName = column.schemaName
                )
            }
        } catch (e: Exception) {
            logger.error("获取列信息失败: {}, {}, {}", dataSourceId, databaseName, tableName, e)
            return emptyList()
        }
    }
    
    override fun executeQuery(dataSourceId: Long, databaseName: String, sql: String): Map<String, Any> {
        try {
            // 执行查询
            val connection = getConnection(dataSourceId)
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(sql)
            
            val columns = mutableListOf<String>()
            val data = mutableListOf<Map<String, Any?>>()
            
            val metaData = resultSet.metaData
            val columnCount = metaData.columnCount
            
            // 获取列名
            for (i in 1..columnCount) {
                columns.add(metaData.getColumnName(i))
            }
            
            // 获取数据
            while (resultSet.next()) {
                val row = mutableMapOf<String, Any?>()
                for (i in 1..columnCount) {
                    val columnName = metaData.getColumnName(i)
                    val value = resultSet.getObject(i)
                    row[columnName] = value
                }
                data.add(row)
            }
            
            resultSet.close()
            statement.close()
            
            return mapOf(
                "success" to true,
                "data" to data,
                "columns" to columns,
                "total" to data.size,
                "sql" to sql,
                "executionTime" to System.currentTimeMillis()
            )
        } catch (e: Exception) {
            logger.error("执行查询失败: {}, {}, {}", dataSourceId, databaseName, sql, e)
            return mapOf(
                "success" to false,
                "error" to (e.message ?: "Unknown error"),
                "sql" to sql,
                "executionTime" to System.currentTimeMillis()
            )
        }
    }
    
    /**
     * 获取数据源连接
     */
    private fun getConnection(dataSourceId: Long): Connection {
        // 检查缓存
        val cachedConnection = connectionCache[dataSourceId]
        if (cachedConnection != null && !cachedConnection.isClosed) {
            return cachedConnection
        }
        
        // 获取数据源信息
        val dataSourceResult = domainDataSourceService.queryById(dataSourceId)
        val dataSource = dataSourceResult.getData() ?: throw SQLException("数据源不存在: $dataSourceId")
        
        // 创建新连接
        val connection = DriverManager.getConnection(
            dataSource.url,
            dataSource.userName,
            dataSource.password
        )
        
        // 缓存连接
        connectionCache[dataSourceId] = connection
        
        return connection
    }
    
    /**
     * 获取数据源类型
     */
    private fun getDataSourceType(dataSourceId: Long): String {
        val dataSourceResult = domainDataSourceService.queryById(dataSourceId)
        return dataSourceResult.getData()?.type ?: "unknown"
    }
}
