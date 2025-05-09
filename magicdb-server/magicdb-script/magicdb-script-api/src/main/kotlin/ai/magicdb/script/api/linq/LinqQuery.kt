package ai.magicdb.script.api.linq

import java.util.function.Function
import java.util.function.Predicate

/**
 * LINQ风格查询接口
 *
 * @author magicdb
 */
interface LinqQuery<T> {
    /**
     * 条件查询
     *
     * @param condition 条件
     * @return 查询对象
     */
    fun where(condition: String): LinqQuery<T>

    /**
     * 条件查询
     *
     * @param condition 条件
     * @param params 参数
     * @return 查询对象
     */
    fun where(condition: String, vararg params: Any?): LinqQuery<T>

    /**
     * 条件查询
     *
     * @param predicate 条件函数
     * @return 查询对象
     */
    fun where(predicate: (T) -> Boolean): LinqQuery<T>

    /**
     * 选择指定列
     *
     * @param columns 列名
     * @return 查询对象
     */
    fun select(vararg columns: String): LinqQuery<T>

    /**
     * 选择指定列
     *
     * @param selector 选择函数
     * @return 查询对象
     */
    fun <R> select(selector: (T) -> R): LinqQuery<R>

    /**
     * 排序
     *
     * @param column 列名
     * @return 查询对象
     */
    fun orderBy(column: String): LinqQuery<T>

    /**
     * 降序排序
     *
     * @param column 列名
     * @return 查询对象
     */
    fun orderByDescending(column: String): LinqQuery<T>

    /**
     * 分组
     *
     * @param column 列名
     * @return 查询对象
     */
    fun groupBy(column: String): LinqQuery<T>

    /**
     * 限制结果数量
     *
     * @param count 数量
     * @return 查询对象
     */
    fun limit(count: Int): LinqQuery<T>

    /**
     * 跳过指定数量的结果
     *
     * @param count 数量
     * @return 查询对象
     */
    fun skip(count: Int): LinqQuery<T>

    /**
     * 执行查询并返回结果
     *
     * @return 查询结果
     */
    fun toList(): List<T>

    /**
     * 执行查询并返回第一个结果
     *
     * @return 第一个结果
     */
    fun first(): T

    /**
     * 执行查询并返回第一个结果，如果没有结果则返回null
     *
     * @return 第一个结果或null
     */
    fun firstOrNull(): T?

    /**
     * 执行查询并返回单个结果
     *
     * @return 单个结果
     */
    fun single(): T

    /**
     * 执行查询并返回单个结果，如果没有结果则返回null
     *
     * @return 单个结果或null
     */
    fun singleOrNull(): T?

    /**
     * 执行查询并返回结果数量
     *
     * @return 结果数量
     */
    fun count(): Int

    /**
     * 执行查询并判断是否存在结果
     *
     * @return 是否存在结果
     */
    fun any(): Boolean

    /**
     * 执行查询并判断是否存在满足条件的结果
     *
     * @param predicate 条件函数
     * @return 是否存在满足条件的结果
     */
    fun any(predicate: (T) -> Boolean): Boolean

    companion object {
        /**
         * 从指定表中查询数据
         *
         * @param tableName 表名
         * @return 查询对象
         */
        fun from(tableName: String): LinqQuery<Map<String, Any?>> {
            return LinqQueryFactory.from(tableName)
        }

        /**
         * 从指定表中查询数据
         *
         * @param databaseName 数据库名
         * @param tableName 表名
         * @return 查询对象
         */
        fun from(databaseName: String, tableName: String): LinqQuery<Map<String, Any?>> {
            return LinqQueryFactory.from(databaseName, tableName)
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
            return LinqQueryFactory.from(databaseName, schemaName, tableName)
        }
    }
}
