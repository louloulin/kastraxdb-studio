package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.DataSourceService
import ai.magicdb.dataservice.api.model.ColumnInfo
import ai.magicdb.dataservice.api.model.DataSourceInfo
import ai.magicdb.dataservice.api.model.DatabaseInfo
import ai.magicdb.dataservice.api.model.TableInfo
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

/**
 * 数据源控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/datasource")
class DataSourceController(private val dataSourceService: DataSourceService) {
    private val logger = LoggerFactory.getLogger(DataSourceController::class.java)

    /**
     * 获取所有数据源
     *
     * @return 数据源列表
     */
    @GetMapping
    fun getAllDataSources(): ListResult<DataSourceInfo> {
        logger.info("获取所有数据源")
        val dataSources = dataSourceService.getAllDataSources()
        return ListResult.of(dataSources)
    }

    /**
     * 获取数据源
     *
     * @param dataSourceId 数据源ID
     * @return 数据源信息
     */
    @GetMapping("/{dataSourceId}")
    fun getDataSource(@PathVariable dataSourceId: Long): DataResult<DataSourceInfo> {
        logger.info("获取数据源: {}", dataSourceId)
        val dataSource = dataSourceService.getDataSource(dataSourceId)
        return if (dataSource != null) {
            DataResult.of(dataSource)
        } else {
            DataResult.empty()
        }
    }

    /**
     * 获取数据库列表
     *
     * @param dataSourceId 数据源ID
     * @return 数据库列表
     */
    @GetMapping("/{dataSourceId}/databases")
    fun getDatabases(@PathVariable dataSourceId: Long): ListResult<DatabaseInfo> {
        logger.info("获取数据库列表: {}", dataSourceId)
        val databases = dataSourceService.getDatabases(dataSourceId)
        return ListResult.of(databases)
    }

    /**
     * 获取表列表
     *
     * @param dataSourceId 数据源ID
     * @param databaseName 数据库名称
     * @return 表列表
     */
    @GetMapping("/{dataSourceId}/databases/{databaseName}/tables")
    fun getTables(
        @PathVariable dataSourceId: Long,
        @PathVariable databaseName: String
    ): ListResult<TableInfo> {
        logger.info("获取表列表: {}, {}", dataSourceId, databaseName)
        val tables = dataSourceService.getTables(dataSourceId, databaseName)
        return ListResult.of(tables)
    }

    /**
     * 获取列信息
     *
     * @param dataSourceId 数据源ID
     * @param databaseName 数据库名称
     * @param tableName 表名
     * @return 列信息列表
     */
    @GetMapping("/{dataSourceId}/databases/{databaseName}/tables/{tableName}/columns")
    fun getColumns(
        @PathVariable dataSourceId: Long,
        @PathVariable databaseName: String,
        @PathVariable tableName: String
    ): ListResult<ColumnInfo> {
        logger.info("获取列信息: {}, {}, {}", dataSourceId, databaseName, tableName)
        val columns = dataSourceService.getColumns(dataSourceId, databaseName, tableName)
        return ListResult.of(columns)
    }

    /**
     * 执行SQL查询
     *
     * @param dataSourceId 数据源ID
     * @param databaseName 数据库名称
     * @param sql SQL语句
     * @return 查询结果
     */
    @PostMapping("/{dataSourceId}/databases/{databaseName}/execute")
    fun executeQuery(
        @PathVariable dataSourceId: Long,
        @PathVariable databaseName: String,
        @RequestBody sql: String
    ): DataResult<Map<String, Any>> {
        logger.info("执行SQL查询: {}, {}, {}", dataSourceId, databaseName, sql)
        val result = dataSourceService.executeQuery(dataSourceId, databaseName, sql)
        return DataResult.of(result)
    }

    /**
     * 测试数据源连接
     *
     * @param dataSourceId 数据源ID
     * @return 是否连接成功
     */
    @GetMapping("/{dataSourceId}/test")
    fun testConnection(@PathVariable dataSourceId: Long): DataResult<Boolean> {
        logger.info("测试数据源连接: {}", dataSourceId)
        val result = dataSourceService.testConnection(dataSourceId)
        return DataResult.of(result)
    }
}
