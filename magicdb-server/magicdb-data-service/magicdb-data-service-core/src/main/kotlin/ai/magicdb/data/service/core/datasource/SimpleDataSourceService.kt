package ai.magicdb.data.service.core.datasource

import ai.magicdb.data.service.api.DataSourceService
import ai.magicdb.data.service.api.model.ColumnInfo
import ai.magicdb.data.service.api.model.DataSourceInfo
import ai.magicdb.data.service.api.model.DatabaseInfo
import ai.magicdb.data.service.api.model.TableInfo
import ai.magicdb.server.domain.api.service.DataSourceService as DomainDataSourceService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 简单数据源服务实现
 *
 * @author magicdb
 */
@Service
class SimpleDataSourceService(
    private val domainDataSourceService: DomainDataSourceService
) : DataSourceService {

    private val logger = LoggerFactory.getLogger(SimpleDataSourceService::class.java)

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
                    host = it.host,
                    port = it.port,
                    username = it.userName,
                    databaseName = "",
                    driverClassName = it.driver,
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
                host = dataSource.host,
                port = dataSource.port,
                username = dataSource.userName,
                databaseName = "",
                driverClassName = dataSource.driver,
                createUserId = dataSource.userId
            )
        } catch (e: Exception) {
            logger.error("获取数据源失败: {}", dataSourceId, e)
            return null
        }
    }

    override fun getDatabases(dataSourceId: Long): List<DatabaseInfo> {
        try {
            // 简化实现，返回一个包含当前数据库的列表
            val dataSourceResult = domainDataSourceService.queryById(dataSourceId)

            if (dataSourceResult.getData() == null) {
                return emptyList()
            }

            val dataSource = dataSourceResult.getData()
            return listOf(
                DatabaseInfo(
                    name = "default",
                    description = "默认数据库",
                    type = dataSource.type
                )
            )
        } catch (e: Exception) {
            logger.error("获取数据库列表失败: {}", dataSourceId, e)
            return emptyList()
        }
    }

    override fun getTables(dataSourceId: Long, databaseName: String): List<TableInfo> {
        // 简化实现，返回空列表
        logger.info("获取表列表: {}, {}", dataSourceId, databaseName)
        return emptyList()
    }

    override fun getColumns(dataSourceId: Long, databaseName: String, tableName: String): List<ColumnInfo> {
        // 简化实现，返回空列表
        logger.info("获取列信息: {}, {}, {}", dataSourceId, databaseName, tableName)
        return emptyList()
    }

    override fun executeQuery(dataSourceId: Long, databaseName: String, sql: String): Map<String, Any> {
        // 简化实现，返回模拟数据
        logger.info("执行SQL查询: {}, {}, {}", dataSourceId, databaseName, sql)

        return mapOf(
            "success" to true,
            "data" to listOf(
                mapOf("id" to 1, "name" to "示例数据1"),
                mapOf("id" to 2, "name" to "示例数据2")
            ),
            "columns" to listOf("id", "name"),
            "total" to 2,
            "sql" to sql,
            "executionTime" to System.currentTimeMillis()
        )
    }

    override fun testConnection(dataSourceId: Long): Boolean {
        try {
            // 简化实现，只检查数据源是否存在
            val dataSourceResult = domainDataSourceService.queryById(dataSourceId)
            return dataSourceResult.getData() != null
        } catch (e: Exception) {
            logger.error("测试数据源连接失败: {}", dataSourceId, e)
            return false
        }
    }
}
