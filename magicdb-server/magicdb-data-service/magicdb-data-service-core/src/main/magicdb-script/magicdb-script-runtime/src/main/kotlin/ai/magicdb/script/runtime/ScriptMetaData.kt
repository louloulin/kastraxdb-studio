package ai.magicdb.script.runtime

import ai.magicdb.server.tools.base.wrapper.result.PageResult
import ai.magicdb.spi.CommandExecutor
import ai.magicdb.spi.MetaData
import ai.magicdb.spi.SqlBuilder
import ai.magicdb.spi.ValueProcessor
import ai.magicdb.spi.model.*
import java.sql.Connection

/**
 * 脚本元数据
 *
 * @author magicdb
 */
class ScriptMetaData : MetaData {
    override fun databases(connection: Connection): List<Database> {
        // 返回脚本支持的数据库列表
        val database = Database()
        database.name = "script"
        return listOf(database)
    }

    override fun schemas(connection: Connection, databaseName: String): List<Schema> {
        // 返回脚本支持的schema列表
        val schema = Schema()
        schema.name = "public"
        return listOf(schema)
    }

    override fun tableDDL(connection: Connection, databaseName: String, schemaName: String, tableName: String): String {
        // 返回表的DDL
        return "CREATE TABLE scripts (\n" +
                "  id INTEGER PRIMARY KEY,\n" +
                "  name VARCHAR(255) NOT NULL,\n" +
                "  language VARCHAR(50) NOT NULL,\n" +
                "  content TEXT,\n" +
                "  created_at TIMESTAMP,\n" +
                "  updated_at TIMESTAMP\n" +
                ");"
    }

    override fun tables(connection: Connection, databaseName: String, schemaName: String, tableName: String): List<Table> {
        // 返回脚本支持的表列表
        val table = Table()
        table.name = "scripts"
        table.type = "TABLE"
        return listOf(table)
    }

    override fun tableNames(connection: Connection, databaseName: String, schemaName: String, tableName: String): List<String> {
        // 返回表名列表
        return listOf("scripts")
    }

    override fun tables(connection: Connection, databaseName: String, schemaName: String, tableNamePattern: String, pageNo: Int, pageSize: Int): PageResult<Table> {
        // 分页返回表列表
        val table = Table()
        table.name = "scripts"
        table.type = "TABLE"
        
        val pageResult = PageResult<Table>()
        pageResult.data = listOf(table)
        pageResult.total = 1
        pageResult.pageSize = pageSize
        pageResult.pageNo = pageNo
        return pageResult
    }

    override fun view(connection: Connection, databaseName: String, schemaName: String, viewName: String): Table? {
        // 脚本不支持视图
        return null
    }

    override fun viewNames(connection: Connection, databaseName: String, schemaName: String): List<String> {
        // 脚本不支持视图
        return emptyList()
    }

    override fun views(connection: Connection, databaseName: String, schemaName: String): List<Table> {
        // 脚本不支持视图
        return emptyList()
    }

    override fun functions(connection: Connection, databaseName: String, schemaName: String): List<Function> {
        // 脚本不支持函数
        return emptyList()
    }

    override fun triggers(connection: Connection, databaseName: String, schemaName: String): List<Trigger> {
        // 脚本不支持触发器
        return emptyList()
    }

    override fun procedures(connection: Connection, databaseName: String, schemaName: String): List<Procedure> {
        // 脚本不支持存储过程
        return emptyList()
    }

    override fun columns(connection: Connection, databaseName: String, schemaName: String, tableName: String): List<TableColumn> {
        // 返回脚本表的列信息
        val columns = ArrayList<TableColumn>()
        
        val idColumn = TableColumn()
        idColumn.name = "id"
        idColumn.columnType = "INTEGER"
        idColumn.primaryKey = true
        columns.add(idColumn)
        
        val nameColumn = TableColumn()
        nameColumn.name = "name"
        nameColumn.columnType = "VARCHAR"
        columns.add(nameColumn)
        
        val languageColumn = TableColumn()
        languageColumn.name = "language"
        languageColumn.columnType = "VARCHAR"
        columns.add(languageColumn)
        
        val contentColumn = TableColumn()
        contentColumn.name = "content"
        contentColumn.columnType = "TEXT"
        columns.add(contentColumn)
        
        val createdAtColumn = TableColumn()
        createdAtColumn.name = "created_at"
        createdAtColumn.columnType = "TIMESTAMP"
        columns.add(createdAtColumn)
        
        val updatedAtColumn = TableColumn()
        updatedAtColumn.name = "updated_at"
        updatedAtColumn.columnType = "TIMESTAMP"
        columns.add(updatedAtColumn)
        
        return columns
    }

    override fun columns(connection: Connection, databaseName: String, schemaName: String, tableName: String, columnName: String): List<TableColumn> {
        // 返回指定列的信息
        return columns(connection, databaseName, schemaName, tableName).filter { it.name == columnName }
    }

    override fun indexes(connection: Connection, databaseName: String, schemaName: String, tableName: String): List<TableIndex> {
        // 脚本不支持索引
        return emptyList()
    }

    override fun function(connection: Connection, databaseName: String, schemaName: String, functionName: String): Function? {
        // 脚本不支持函数
        return null
    }

    override fun trigger(connection: Connection, databaseName: String, schemaName: String, triggerName: String): Trigger? {
        // 脚本不支持触发器
        return null
    }

    override fun procedure(connection: Connection, databaseName: String, schemaName: String, procedureName: String): Procedure? {
        // 脚本不支持存储过程
        return null
    }

    override fun types(connection: Connection): List<Type> {
        // 返回支持的类型
        return emptyList()
    }

    override fun getSqlBuilder(): SqlBuilder? {
        // 脚本不支持SQL构建器
        return null
    }

    override fun getTableMeta(databaseName: String, schemaName: String, tableName: String): TableMeta? {
        // 返回表元数据
        return null
    }

    override fun getMetaDataName(vararg names: String): String {
        // 返回元数据名称
        return "script"
    }

    override fun getValueProcessor(): ValueProcessor? {
        // 脚本不支持值处理器
        return null
    }

    override fun getCommandExecutor(): CommandExecutor? {
        // 脚本不支持命令执行器
        return null
    }

    override fun getSystemDatabases(): List<String> {
        // 返回系统数据库
        return emptyList()
    }

    override fun getSystemSchemas(): List<String> {
        // 返回系统schema
        return emptyList()
    }
}
