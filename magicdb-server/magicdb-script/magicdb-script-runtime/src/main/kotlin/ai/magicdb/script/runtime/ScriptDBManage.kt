package ai.magicdb.script.runtime

import ai.magicdb.spi.DBManage
import ai.magicdb.spi.model.ExecuteResult
import java.sql.Connection

/**
 * 脚本数据库管理
 *
 * @author magicdb
 */
class ScriptDBManage : DBManage {
    override fun createDatabase(connection: Connection, databaseName: String): ExecuteResult {
        // 创建脚本数据库
        return ExecuteResult.builder()
            .success(true)
            .description("创建脚本数据库成功")
            .build()
    }

    override fun dropDatabase(connection: Connection, databaseName: String): ExecuteResult {
        // 删除脚本数据库
        return ExecuteResult.builder()
            .success(true)
            .description("删除脚本数据库成功")
            .build()
    }

    override fun createSchema(connection: Connection, databaseName: String, schemaName: String): ExecuteResult {
        // 创建脚本schema
        return ExecuteResult.builder()
            .success(true)
            .description("创建脚本schema成功")
            .build()
    }

    override fun dropSchema(connection: Connection, databaseName: String, schemaName: String): ExecuteResult {
        // 删除脚本schema
        return ExecuteResult.builder()
            .success(true)
            .description("删除脚本schema成功")
            .build()
    }

    override fun createTable(connection: Connection, databaseName: String, schemaName: String, ddl: String): ExecuteResult {
        // 创建脚本表
        return ExecuteResult.builder()
            .success(true)
            .description("创建脚本表成功")
            .build()
    }

    override fun dropTable(connection: Connection, databaseName: String, schemaName: String, tableName: String): ExecuteResult {
        // 删除脚本表
        return ExecuteResult.builder()
            .success(true)
            .description("删除脚本表成功")
            .build()
    }

    override fun renameTable(connection: Connection, databaseName: String, schemaName: String, oldTableName: String, newTableName: String): ExecuteResult {
        // 重命名脚本表
        return ExecuteResult.builder()
            .success(true)
            .description("重命名脚本表成功")
            .build()
    }

    override fun truncateTable(connection: Connection, databaseName: String, schemaName: String, tableName: String): ExecuteResult {
        // 清空脚本表
        return ExecuteResult.builder()
            .success(true)
            .description("清空脚本表成功")
            .build()
    }
}
