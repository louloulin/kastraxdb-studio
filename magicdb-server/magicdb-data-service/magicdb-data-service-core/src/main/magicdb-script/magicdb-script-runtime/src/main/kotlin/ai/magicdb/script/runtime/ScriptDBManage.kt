package ai.magicdb.script.runtime

import ai.magicdb.spi.DBManage
import ai.magicdb.spi.model.AsyncContext
import ai.magicdb.spi.model.Function
import ai.magicdb.spi.model.Procedure
import ai.magicdb.spi.sql.ConnectInfo
import java.sql.Connection
import java.sql.SQLException

/**
 * 脚本数据库管理
 *
 * @author magicdb
 */
class ScriptDBManage : DBManage {
    override fun getConnection(connectInfo: ConnectInfo): Connection {
        // 获取连接
        throw UnsupportedOperationException("脚本数据库不支持获取连接")
    }

    override fun connectDatabase(connection: Connection, database: String) {
        // 连接数据库
        // 不需要实现
    }

    override fun modifyDatabase(connection: Connection, databaseName: String, newDatabaseName: String) {
        // 修改数据库名
        // 不需要实现
    }

    override fun createDatabase(connection: Connection, databaseName: String) {
        // 创建脚本数据库
        // 不需要实现
    }

    override fun dropDatabase(connection: Connection, databaseName: String) {
        // 删除脚本数据库
        // 不需要实现
    }

    override fun createSchema(connection: Connection, databaseName: String, schemaName: String) {
        // 创建schema
        // 不需要实现
    }

    override fun dropSchema(connection: Connection, databaseName: String, schemaName: String) {
        // 删除脚本schema
        // 不需要实现
    }

    override fun modifySchema(connection: Connection, databaseName: String, schemaName: String, newSchemaName: String) {
        // 修改schema
        // 不需要实现
    }

    override fun dropTable(connection: Connection, databaseName: String, schemaName: String, tableName: String) {
        // 删除脚本表
        // 不需要实现
    }

    override fun dropFunction(connection: Connection, databaseName: String, schemaName: String, functionName: String) {
        // 删除函数
        // 不需要实现
    }

    override fun dropTrigger(connection: Connection, databaseName: String, schemaName: String, triggerName: String) {
        // 删除触发器
        // 不需要实现
    }

    override fun dropProcedure(connection: Connection, databaseName: String, schemaName: String, triggerName: String) {
        // 删除存储过程
        // 不需要实现
    }

    override fun updateProcedure(connection: Connection, databaseName: String, schemaName: String, procedure: Procedure) {
        // 更新存储过程
        // 不需要实现
    }

    override fun exportDatabase(connection: Connection, databaseName: String, schemaName: String, asyncContext: AsyncContext) {
        // 导出数据库
        // 不需要实现
    }

    override fun exportTable(connection: Connection, databaseName: String, schemaName: String, tableName: String, asyncContext: AsyncContext) {
        // 导出表
        // 不需要实现
    }

    override fun truncateTable(connection: Connection, databaseName: String, schemaName: String, tableName: String) {
        // 清空表
        // 不需要实现
    }

    override fun copyTable(connection: Connection, databaseName: String, schemaName: String, tableName: String, newTableName: String, copyData: Boolean) {
        // 复制表
        // 不需要实现
    }

    override fun deleteProcedure(connection: Connection, databaseName: String, schemaName: String, procedure: Procedure) {
        // 删除存储过程
        // 不需要实现
    }

    override fun deleteFunction(connection: Connection, databaseName: String, schemaName: String, function: Function) {
        // 删除函数
        // 不需要实现
    }
}
