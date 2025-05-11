package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.DataSourceService
import ai.magicdb.dataservice.api.model.ColumnInfo
import ai.magicdb.dataservice.api.model.DataSourceInfo
import ai.magicdb.dataservice.api.model.DatabaseInfo
import ai.magicdb.dataservice.api.model.TableInfo
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import org.springframework.web.bind.annotation.*

/**
 * 数据源控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/datasource")
class DataSourceController(private val dataSourceService: DataSourceService) {

    /**
     * 获取所有数据源
     */
    @GetMapping("/list")
    fun getAllDataSources(): ListResult<DataSourceInfo> {
        val dataSources = dataSourceService.getAllDataSources()
        return ListResult.of(dataSources)
    }

    /**
     * 获取数据源
     *
     * @param dataSourceId 数据源ID
     */
    @GetMapping("/{dataSourceId}")
    fun getDataSource(@PathVariable dataSourceId: Long): DataResult<DataSourceInfo> {
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
     */
    @GetMapping("/{dataSourceId}/databases")
    fun getDatabases(@PathVariable dataSourceId: Long): ListResult<DatabaseInfo> {
        val databases = dataSourceService.getDatabases(dataSourceId)
        return ListResult.of(databases)
    }

    /**
     * 获取表列表
     *
     * @param dataSourceId 数据源ID
     * @param databaseName 数据库名称
     */
    @GetMapping("/{dataSourceId}/databases/{databaseName}/tables")
    fun getTables(
        @PathVariable dataSourceId: Long,
        @PathVariable databaseName: String
    ): ListResult<TableInfo> {
        val tables = dataSourceService.getTables(dataSourceId, databaseName)
        return ListResult.of(tables)
    }

    /**
     * 获取列信息
     *
     * @param dataSourceId 数据源ID
     * @param databaseName 数据库名称
     * @param tableName 表名
     */
    @GetMapping("/{dataSourceId}/databases/{databaseName}/tables/{tableName}/columns")
    fun getColumns(
        @PathVariable dataSourceId: Long,
        @PathVariable databaseName: String,
        @PathVariable tableName: String
    ): ListResult<ColumnInfo> {
        val columns = dataSourceService.getColumns(dataSourceId, databaseName, tableName)
        return ListResult.of(columns)
    }

    /**
     * 执行SQL查询
     *
     * @param dataSourceId 数据源ID
     * @param databaseName 数据库名称
     * @param sql SQL语句
     */
    @PostMapping("/{dataSourceId}/databases/{databaseName}/query")
    fun executeQuery(
        @PathVariable dataSourceId: Long,
        @PathVariable databaseName: String,
        @RequestBody sql: String
    ): DataResult<Map<String, Any>> {
        val result = dataSourceService.executeQuery(dataSourceId, databaseName, sql)
        return DataResult.of(result)
    }

    /**
     * 测试数据源连接
     *
     * @param dataSourceId 数据源ID
     */
    @GetMapping("/{dataSourceId}/test")
    fun testConnection(@PathVariable dataSourceId: Long): ActionResult {
        val success = dataSourceService.testConnection(dataSourceId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "连接数据源失败", "")
        }
    }
}
