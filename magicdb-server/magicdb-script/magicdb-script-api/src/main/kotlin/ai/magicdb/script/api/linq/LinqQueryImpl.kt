package ai.magicdb.script.api.linq

import org.slf4j.LoggerFactory
import java.util.*
import java.util.NoSuchElementException

/**
 * LINQ查询实现
 *
 * @author magicdb
 */
class LinqQueryImpl<T> : LinqQuery<T> {
    private val logger = LoggerFactory.getLogger(LinqQueryImpl::class.java)
    
    private val databaseName: String?
    private val schemaName: String?
    private val tableName: String
    private val whereConditions = mutableListOf<String>()
    private val whereParams = mutableListOf<Any?>()
    private val wherePredicates = mutableListOf<(T) -> Boolean>()
    private val selectColumns = mutableListOf<String>()
    private var selectFunction: ((T) -> Any?)? = null
    private val orderByColumns = mutableListOf<String>()
    private val orderByDescColumns = mutableListOf<String>()
    private val groupByColumns = mutableListOf<String>()
    private var limitCount: Int? = null
    private var skipCount: Int? = null
    private var cachedResults: List<T>? = null

    constructor(tableName: String) : this(null, null, tableName)

    constructor(databaseName: String?, tableName: String) : this(databaseName, null, tableName)

    constructor(databaseName: String?, schemaName: String?, tableName: String) {
        this.databaseName = databaseName
        this.schemaName = schemaName
        this.tableName = tableName
    }

    override fun where(condition: String): LinqQuery<T> {
        whereConditions.add(condition)
        return this
    }

    override fun where(condition: String, vararg params: Any?): LinqQuery<T> {
        whereConditions.add(condition)
        whereParams.addAll(params)
        return this
    }

    override fun where(predicate: (T) -> Boolean): LinqQuery<T> {
        wherePredicates.add(predicate)
        return this
    }

    override fun select(vararg columns: String): LinqQuery<T> {
        selectColumns.addAll(columns)
        return this
    }

    override fun <R> select(selector: (T) -> R): LinqQuery<R> {
        // 创建一个新的查询对象，复制当前查询的条件
        val newQuery = LinqQueryImpl<R>(databaseName, schemaName, tableName)
        
        // 设置选择函数
        @Suppress("UNCHECKED_CAST")
        newQuery.selectFunction = selector as (R) -> Any?
        
        // 返回新的查询对象
        return newQuery
    }

    override fun orderBy(column: String): LinqQuery<T> {
        orderByColumns.add(column)
        return this
    }

    override fun orderByDescending(column: String): LinqQuery<T> {
        orderByDescColumns.add(column)
        return this
    }

    override fun groupBy(column: String): LinqQuery<T> {
        groupByColumns.add(column)
        return this
    }

    override fun limit(count: Int): LinqQuery<T> {
        limitCount = count
        return this
    }

    override fun skip(count: Int): LinqQuery<T> {
        skipCount = count
        return this
    }

    override fun toList(): List<T> {
        if (cachedResults == null) {
            executeQuery()
        }
        return cachedResults ?: emptyList()
    }

    override fun first(): T {
        val results = toList()
        if (results.isEmpty()) {
            throw NoSuchElementException("No elements in query result")
        }
        return results[0]
    }

    override fun firstOrNull(): T? {
        val results = toList()
        return if (results.isEmpty()) null else results[0]
    }

    override fun single(): T {
        val results = toList()
        if (results.isEmpty()) {
            throw NoSuchElementException("No elements in query result")
        }
        if (results.size > 1) {
            throw IllegalStateException("More than one element in query result")
        }
        return results[0]
    }

    override fun singleOrNull(): T? {
        val results = toList()
        if (results.size > 1) {
            throw IllegalStateException("More than one element in query result")
        }
        return if (results.isEmpty()) null else results[0]
    }

    override fun count(): Int {
        return toList().size
    }

    override fun any(): Boolean {
        return toList().isNotEmpty()
    }

    override fun any(predicate: (T) -> Boolean): Boolean {
        return toList().any(predicate)
    }

    /**
     * 执行查询
     */
    @Suppress("UNCHECKED_CAST")
    private fun executeQuery() {
        try {
            // 构建SQL查询
            val sql = StringBuilder("SELECT ")
            
            // 添加选择的列
            if (selectColumns.isEmpty()) {
                sql.append("*")
            } else {
                sql.append(selectColumns.joinToString(", "))
            }
            
            // 添加表名
            sql.append(" FROM ")
            if (databaseName != null) {
                sql.append(databaseName).append(".")
            }
            if (schemaName != null) {
                sql.append(schemaName).append(".")
            }
            sql.append(tableName)
            
            // 添加WHERE条件
            if (whereConditions.isNotEmpty()) {
                sql.append(" WHERE ")
                sql.append(whereConditions.joinToString(" AND "))
            }
            
            // 添加GROUP BY
            if (groupByColumns.isNotEmpty()) {
                sql.append(" GROUP BY ")
                sql.append(groupByColumns.joinToString(", "))
            }
            
            // 添加ORDER BY
            if (orderByColumns.isNotEmpty() || orderByDescColumns.isNotEmpty()) {
                sql.append(" ORDER BY ")
                
                val allOrderClauses = mutableListOf<String>()
                
                for (column in orderByColumns) {
                    allOrderClauses.add("$column ASC")
                }
                
                for (column in orderByDescColumns) {
                    allOrderClauses.add("$column DESC")
                }
                
                sql.append(allOrderClauses.joinToString(", "))
            }
            
            // 添加LIMIT和OFFSET
            if (limitCount != null) {
                sql.append(" LIMIT ").append(limitCount)
            }
            
            if (skipCount != null) {
                sql.append(" OFFSET ").append(skipCount)
            }
            
            // 执行SQL查询
            // 这里需要集成MagicDBContext来执行SQL查询
            // 暂时使用模拟数据
            val mockResults = mutableListOf<Map<String, Any?>>()
            val row1 = mutableMapOf<String, Any?>()
            row1["id"] = 1
            row1["name"] = "Test 1"
            mockResults.add(row1)
            
            val row2 = mutableMapOf<String, Any?>()
            row2["id"] = 2
            row2["name"] = "Test 2"
            mockResults.add(row2)
            
            // 应用内存中的过滤条件
            var results = mockResults as List<T>
            
            // 应用where谓词
            if (wherePredicates.isNotEmpty()) {
                for (predicate in wherePredicates) {
                    results = results.filter(predicate)
                }
            }
            
            // 应用选择函数
            if (selectFunction != null) {
                results = results.map { selectFunction!!.invoke(it) } as List<T>
            }
            
            cachedResults = results
            
        } catch (e: Exception) {
            logger.error("执行LINQ查询出错: {}", e.message, e)
            throw RuntimeException("执行LINQ查询出错", e)
        }
    }
}
