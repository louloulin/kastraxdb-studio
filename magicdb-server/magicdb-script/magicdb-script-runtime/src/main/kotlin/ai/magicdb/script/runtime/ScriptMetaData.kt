package ai.magicdb.script.runtime

import ai.magicdb.spi.MetaData
import ai.magicdb.spi.model.Database
import ai.magicdb.spi.model.Schema
import ai.magicdb.spi.model.Table
import ai.magicdb.spi.model.TableColumn
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

    override fun tables(connection: Connection, databaseName: String, schemaName: String): List<Table> {
        // 返回脚本支持的表列表
        val table = Table()
        table.name = "scripts"
        table.type = "TABLE"
        return listOf(table)
    }

    override fun tableColumns(connection: Connection, databaseName: String, schemaName: String, tableName: String): List<TableColumn> {
        // 返回脚本表的列信息
        val columns = ArrayList<TableColumn>()
        
        val idColumn = TableColumn()
        idColumn.name = "id"
        idColumn.typeName = "INTEGER"
        idColumn.isPrimaryKey = true
        columns.add(idColumn)
        
        val nameColumn = TableColumn()
        nameColumn.name = "name"
        nameColumn.typeName = "VARCHAR"
        columns.add(nameColumn)
        
        val languageColumn = TableColumn()
        languageColumn.name = "language"
        languageColumn.typeName = "VARCHAR"
        columns.add(languageColumn)
        
        val contentColumn = TableColumn()
        contentColumn.name = "content"
        contentColumn.typeName = "TEXT"
        columns.add(contentColumn)
        
        val createdAtColumn = TableColumn()
        createdAtColumn.name = "created_at"
        createdAtColumn.typeName = "TIMESTAMP"
        columns.add(createdAtColumn)
        
        val updatedAtColumn = TableColumn()
        updatedAtColumn.name = "updated_at"
        updatedAtColumn.typeName = "TIMESTAMP"
        columns.add(updatedAtColumn)
        
        return columns
    }
}
