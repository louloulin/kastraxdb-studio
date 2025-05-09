package ai.magicdb.script.api.linq

/**
 * LINQ查询工厂
 *
 * @author magicdb
 */
object LinqQueryFactory {
    /**
     * 从指定表中查询数据
     *
     * @param tableName 表名
     * @return 查询对象
     */
    fun from(tableName: String): LinqQuery<Map<String, Any?>> {
        return LinqQueryImpl(tableName)
    }

    /**
     * 从指定表中查询数据
     *
     * @param databaseName 数据库名
     * @param tableName 表名
     * @return 查询对象
     */
    fun from(databaseName: String, tableName: String): LinqQuery<Map<String, Any?>> {
        return LinqQueryImpl(databaseName, tableName)
    }

    /**
     * 从指定表中查询数据
     *
     * @param databaseName 数据库名
     * @param schemaName schema名
     * @param tableName 表名
     * @return 查询对象
     */
    fun from(databaseName: String, schemaName: String, tableName: String): LinqQuery<Map<String, Any?>> {
        return LinqQueryImpl(databaseName, schemaName, tableName)
    }
}
