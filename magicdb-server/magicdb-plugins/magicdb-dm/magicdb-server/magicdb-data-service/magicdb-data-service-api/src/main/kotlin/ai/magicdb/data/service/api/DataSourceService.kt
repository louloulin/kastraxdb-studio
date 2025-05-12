package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.ColumnInfo
import ai.magicdb.data.service.api.model.DatabaseInfo
import ai.magicdb.data.service.api.model.DataSourceInfo
import ai.magicdb.data.service.api.model.TableInfo

/**
 * 数据源服务接口
 * 提供数据源、数据库、表和列的元数据信息
 */
interface DataSourceService {
    /**
     * 获取所有数据源
     *
     * @return 数据源列表
     */
    fun getAllDataSources(): List<DataSourceInfo>
    
    /**
     * 获取数据源信息
     *
     * @param dataSourceId 数据源ID
     * @return 数据源信息
     */
    fun getDataSource(dataSourceId: Long): DataSourceInfo?
    
    /**
     * 获取数据库列表
     *
     * @param dataSourceId 数据源ID
     * @return 数据库列表
     */
    fun getDatabases(dataSourceId: Long): List<DatabaseInfo>
    
    /**
     * 获取表列表
     *
     * @param dataSourceId 数据源ID
     * @param databaseName 数据库名称
     * @return 表列表
     */
    fun getTables(dataSourceId: Long, databaseName: String): List<TableInfo>
    
    /**
     * 获取列信息
     *
     * @param dataSourceId 数据源ID
     * @param databaseName 数据库名称
     * @param tableName 表名
     * @return 列信息列表
     */
    fun getColumns(dataSourceId: Long, databaseName: String, tableName: String): List<ColumnInfo>
    
    /**
     * 执行查询
     *
     * @param dataSourceId 数据源ID
     * @param databaseName 数据库名称
     * @param sql SQL语句
     * @return 查询结果
     */
    fun executeQuery(dataSourceId: Long, databaseName: String, sql: String): Map<String, Any>
}
