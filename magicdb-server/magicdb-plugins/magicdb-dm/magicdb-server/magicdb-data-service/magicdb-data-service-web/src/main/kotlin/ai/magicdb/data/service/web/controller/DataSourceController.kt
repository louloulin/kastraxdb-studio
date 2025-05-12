package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.DataSourceService
import ai.magicdb.data.service.api.model.ColumnInfo
import ai.magicdb.data.service.api.model.DatabaseInfo
import ai.magicdb.data.service.api.model.DataSourceInfo
import ai.magicdb.data.service.api.model.TableInfo
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 数据源控制器
 */
@RestController
@RequestMapping("/api/data-source")
class DataSourceController(
    private val dataSourceService: DataSourceService
) {
    
    /**
     * 获取所有数据源
     */
    @GetMapping("/list")
    fun getAllDataSources(): DataResult<List<DataSourceInfo>> {
        val dataSources = dataSourceService.getAllDataSources()
        return DataResult.of(dataSources)
    }
    
    /**
     * 获取数据源信息
     */
    @GetMapping("/{id}")
    fun getDataSource(@PathVariable("id") dataSourceId: Long): DataResult<DataSourceInfo?> {
        val dataSource = dataSourceService.getDataSource(dataSourceId)
        return DataResult.of(dataSource)
    }
    
    /**
     * 获取数据库列表
     */
    @GetMapping("/{id}/databases")
    fun getDatabases(@PathVariable("id") dataSourceId: Long): DataResult<List<DatabaseInfo>> {
        val databases = dataSourceService.getDatabases(dataSourceId)
        return DataResult.of(databases)
    }
    
    /**
     * 获取表列表
     */
    @GetMapping("/{id}/databases/{databaseName}/tables")
    fun getTables(
        @PathVariable("id") dataSourceId: Long,
        @PathVariable("databaseName") databaseName: String
    ): DataResult<List<TableInfo>> {
        val tables = dataSourceService.getTables(dataSourceId, databaseName)
        return DataResult.of(tables)
    }
    
    /**
     * 获取列信息
     */
    @GetMapping("/{id}/databases/{databaseName}/tables/{tableName}/columns")
    fun getColumns(
        @PathVariable("id") dataSourceId: Long,
        @PathVariable("databaseName") databaseName: String,
        @PathVariable("tableName") tableName: String
    ): DataResult<List<ColumnInfo>> {
        val columns = dataSourceService.getColumns(dataSourceId, databaseName, tableName)
        return DataResult.of(columns)
    }
    
    /**
     * 执行查询
     */
    @PostMapping("/{id}/databases/{databaseName}/query")
    fun executeQuery(
        @PathVariable("id") dataSourceId: Long,
        @PathVariable("databaseName") databaseName: String,
        @RequestBody request: QueryRequest
    ): DataResult<Map<String, Any>> {
        val result = dataSourceService.executeQuery(dataSourceId, databaseName, request.sql)
        return DataResult.of(result)
    }
    
    /**
     * 查询请求
     */
    data class QueryRequest(
        val sql: String
    )
}
