package ai.magicdb.script.runtime

import java.sql.*
import java.util.*
import java.util.logging.Logger

/**
 * 脚本JDBC驱动
 *
 * @author magicdb
 */
class ScriptDriver : Driver {
    companion object {
        init {
            try {
                DriverManager.registerDriver(ScriptDriver())
            } catch (e: SQLException) {
                throw RuntimeException("Can't register Script JDBC driver", e)
            }
        }
    }

    @Throws(SQLException::class)
    override fun connect(url: String, info: Properties): Connection? {
        if (!acceptsURL(url)) {
            return null
        }
        return ScriptConnection()
    }

    @Throws(SQLException::class)
    override fun acceptsURL(url: String): Boolean {
        return url.startsWith("script:")
    }

    @Throws(SQLException::class)
    override fun getPropertyInfo(url: String, info: Properties): Array<DriverPropertyInfo> {
        return arrayOf()
    }

    override fun getMajorVersion(): Int {
        return 1
    }

    override fun getMinorVersion(): Int {
        return 0
    }

    override fun jdbcCompliant(): Boolean {
        return false
    }

    override fun getParentLogger(): Logger {
        throw SQLFeatureNotSupportedException("getParentLogger not supported")
    }

    /**
     * 脚本连接
     */
    private class ScriptConnection : Connection {
        private var closed = false
        private var autoCommit = true
        private var readOnly = false
        private var transactionIsolation = Connection.TRANSACTION_NONE
        private val typeMap = HashMap<String, Class<*>>()
        private var holdability = ResultSet.HOLD_CURSORS_OVER_COMMIT
        private val warnings = SQLWarning()

        @Throws(SQLException::class)
        override fun createStatement(): Statement {
            checkClosed()
            return ScriptStatement(this)
        }

        @Throws(SQLException::class)
        override fun prepareStatement(sql: String): PreparedStatement {
            checkClosed()
            throw SQLFeatureNotSupportedException("prepareStatement not supported")
        }

        @Throws(SQLException::class)
        override fun prepareCall(sql: String): CallableStatement {
            checkClosed()
            throw SQLFeatureNotSupportedException("prepareCall not supported")
        }

        @Throws(SQLException::class)
        override fun nativeSQL(sql: String): String {
            checkClosed()
            return sql
        }

        @Throws(SQLException::class)
        override fun setAutoCommit(autoCommit: Boolean) {
            checkClosed()
            this.autoCommit = autoCommit
        }

        @Throws(SQLException::class)
        override fun getAutoCommit(): Boolean {
            checkClosed()
            return autoCommit
        }

        @Throws(SQLException::class)
        override fun commit() {
            checkClosed()
            if (autoCommit) {
                throw SQLException("Can't call commit in auto-commit mode")
            }
        }

        @Throws(SQLException::class)
        override fun rollback() {
            checkClosed()
            if (autoCommit) {
                throw SQLException("Can't call rollback in auto-commit mode")
            }
        }

        @Throws(SQLException::class)
        override fun close() {
            closed = true
        }

        @Throws(SQLException::class)
        override fun isClosed(): Boolean {
            return closed
        }

        @Throws(SQLException::class)
        override fun getMetaData(): DatabaseMetaData {
            checkClosed()
            throw SQLFeatureNotSupportedException("getMetaData not supported")
        }

        @Throws(SQLException::class)
        override fun setReadOnly(readOnly: Boolean) {
            checkClosed()
            this.readOnly = readOnly
        }

        @Throws(SQLException::class)
        override fun isReadOnly(): Boolean {
            checkClosed()
            return readOnly
        }

        @Throws(SQLException::class)
        override fun setCatalog(catalog: String) {
            checkClosed()
        }

        @Throws(SQLException::class)
        override fun getCatalog(): String? {
            checkClosed()
            return null
        }

        @Throws(SQLException::class)
        override fun setTransactionIsolation(level: Int) {
            checkClosed()
            this.transactionIsolation = level
        }

        @Throws(SQLException::class)
        override fun getTransactionIsolation(): Int {
            checkClosed()
            return transactionIsolation
        }

        @Throws(SQLException::class)
        override fun getWarnings(): SQLWarning? {
            checkClosed()
            return warnings
        }

        @Throws(SQLException::class)
        override fun clearWarnings() {
            checkClosed()
        }

        @Throws(SQLException::class)
        override fun createStatement(resultSetType: Int, resultSetConcurrency: Int): Statement {
            checkClosed()
            return ScriptStatement(this)
        }

        @Throws(SQLException::class)
        override fun prepareStatement(sql: String, resultSetType: Int, resultSetConcurrency: Int): PreparedStatement {
            checkClosed()
            throw SQLFeatureNotSupportedException("prepareStatement not supported")
        }

        @Throws(SQLException::class)
        override fun prepareCall(sql: String, resultSetType: Int, resultSetConcurrency: Int): CallableStatement {
            checkClosed()
            throw SQLFeatureNotSupportedException("prepareCall not supported")
        }

        @Throws(SQLException::class)
        override fun getTypeMap(): Map<String, Class<*>> {
            checkClosed()
            return typeMap
        }

        @Throws(SQLException::class)
        override fun setTypeMap(map: Map<String, Class<*>>) {
            checkClosed()
            typeMap.clear()
            typeMap.putAll(map)
        }

        @Throws(SQLException::class)
        override fun setHoldability(holdability: Int) {
            checkClosed()
            this.holdability = holdability
        }

        @Throws(SQLException::class)
        override fun getHoldability(): Int {
            checkClosed()
            return holdability
        }

        @Throws(SQLException::class)
        override fun setSavepoint(): Savepoint {
            checkClosed()
            throw SQLFeatureNotSupportedException("setSavepoint not supported")
        }

        @Throws(SQLException::class)
        override fun setSavepoint(name: String): Savepoint {
            checkClosed()
            throw SQLFeatureNotSupportedException("setSavepoint not supported")
        }

        @Throws(SQLException::class)
        override fun rollback(savepoint: Savepoint) {
            checkClosed()
            throw SQLFeatureNotSupportedException("rollback with savepoint not supported")
        }

        @Throws(SQLException::class)
        override fun releaseSavepoint(savepoint: Savepoint) {
            checkClosed()
            throw SQLFeatureNotSupportedException("releaseSavepoint not supported")
        }

        @Throws(SQLException::class)
        override fun createStatement(resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int): Statement {
            checkClosed()
            return ScriptStatement(this)
        }

        @Throws(SQLException::class)
        override fun prepareStatement(sql: String, resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int): PreparedStatement {
            checkClosed()
            throw SQLFeatureNotSupportedException("prepareStatement not supported")
        }

        @Throws(SQLException::class)
        override fun prepareCall(sql: String, resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int): CallableStatement {
            checkClosed()
            throw SQLFeatureNotSupportedException("prepareCall not supported")
        }

        @Throws(SQLException::class)
        override fun prepareStatement(sql: String, autoGeneratedKeys: Int): PreparedStatement {
            checkClosed()
            throw SQLFeatureNotSupportedException("prepareStatement not supported")
        }

        @Throws(SQLException::class)
        override fun prepareStatement(sql: String, columnIndexes: IntArray): PreparedStatement {
            checkClosed()
            throw SQLFeatureNotSupportedException("prepareStatement not supported")
        }

        @Throws(SQLException::class)
        override fun prepareStatement(sql: String, columnNames: Array<String>): PreparedStatement {
            checkClosed()
            throw SQLFeatureNotSupportedException("prepareStatement not supported")
        }

        @Throws(SQLException::class)
        override fun createClob(): Clob {
            checkClosed()
            throw SQLFeatureNotSupportedException("createClob not supported")
        }

        @Throws(SQLException::class)
        override fun createBlob(): Blob {
            checkClosed()
            throw SQLFeatureNotSupportedException("createBlob not supported")
        }

        @Throws(SQLException::class)
        override fun createNClob(): NClob {
            checkClosed()
            throw SQLFeatureNotSupportedException("createNClob not supported")
        }

        @Throws(SQLException::class)
        override fun createSQLXML(): SQLXML {
            checkClosed()
            throw SQLFeatureNotSupportedException("createSQLXML not supported")
        }

        @Throws(SQLException::class)
        override fun isValid(timeout: Int): Boolean {
            return !closed
        }

        @Throws(SQLException::class)
        override fun setClientInfo(name: String, value: String) {
            checkClosed()
        }

        @Throws(SQLException::class)
        override fun setClientInfo(properties: Properties) {
            checkClosed()
        }

        @Throws(SQLException::class)
        override fun getClientInfo(name: String): String? {
            checkClosed()
            return null
        }

        @Throws(SQLException::class)
        override fun getClientInfo(): Properties {
            checkClosed()
            return Properties()
        }

        @Throws(SQLException::class)
        override fun createArrayOf(typeName: String, elements: Array<Any>): java.sql.Array {
            checkClosed()
            throw SQLFeatureNotSupportedException("createArrayOf not supported")
        }

        @Throws(SQLException::class)
        override fun createStruct(typeName: String, attributes: Array<Any>): Struct {
            checkClosed()
            throw SQLFeatureNotSupportedException("createStruct not supported")
        }

        @Throws(SQLException::class)
        override fun setSchema(schema: String) {
            checkClosed()
        }

        @Throws(SQLException::class)
        override fun getSchema(): String? {
            checkClosed()
            return null
        }

        @Throws(SQLException::class)
        override fun abort(executor: java.util.concurrent.Executor) {
            closed = true
        }

        @Throws(SQLException::class)
        override fun setNetworkTimeout(executor: java.util.concurrent.Executor, milliseconds: Int) {
            checkClosed()
        }

        @Throws(SQLException::class)
        override fun getNetworkTimeout(): Int {
            checkClosed()
            return 0
        }

        @Throws(SQLException::class)
        override fun <T> unwrap(iface: Class<T>): T {
            checkClosed()
            if (iface.isAssignableFrom(javaClass)) {
                @Suppress("UNCHECKED_CAST")
                return this as T
            }
            throw SQLException("Cannot unwrap to " + iface.name)
        }

        @Throws(SQLException::class)
        override fun isWrapperFor(iface: Class<*>): Boolean {
            checkClosed()
            return iface.isAssignableFrom(javaClass)
        }

        @Throws(SQLException::class)
        private fun checkClosed() {
            if (closed) {
                throw SQLException("Connection is closed")
            }
        }
    }

    /**
     * 脚本语句
     */
    private class ScriptStatement(private val connection: Connection) : Statement {
        private var closed = false
        private var maxRows = 0
        private var maxFieldSize = 0
        private var queryTimeout = 0
        private var fetchDirection = ResultSet.FETCH_FORWARD
        private var fetchSize = 0
        private var resultSetType = ResultSet.TYPE_FORWARD_ONLY
        private var resultSetConcurrency = ResultSet.CONCUR_READ_ONLY
        private var resultSetHoldability = ResultSet.HOLD_CURSORS_OVER_COMMIT
        private var poolable = false
        private var closeOnCompletion = false
        private val warnings = SQLWarning()
        private var currentResultSet: ResultSet? = null
        private var updateCount = -1

        @Throws(SQLException::class)
        override fun executeQuery(sql: String): ResultSet {
            checkClosed()
            throw SQLFeatureNotSupportedException("executeQuery not supported")
        }

        @Throws(SQLException::class)
        override fun executeUpdate(sql: String): Int {
            checkClosed()
            return 0
        }

        @Throws(SQLException::class)
        override fun close() {
            closed = true
            currentResultSet?.close()
        }

        @Throws(SQLException::class)
        override fun getMaxFieldSize(): Int {
            checkClosed()
            return maxFieldSize
        }

        @Throws(SQLException::class)
        override fun setMaxFieldSize(max: Int) {
            checkClosed()
            maxFieldSize = max
        }

        @Throws(SQLException::class)
        override fun getMaxRows(): Int {
            checkClosed()
            return maxRows
        }

        @Throws(SQLException::class)
        override fun setMaxRows(max: Int) {
            checkClosed()
            maxRows = max
        }

        @Throws(SQLException::class)
        override fun setEscapeProcessing(enable: Boolean) {
            checkClosed()
        }

        @Throws(SQLException::class)
        override fun getQueryTimeout(): Int {
            checkClosed()
            return queryTimeout
        }

        @Throws(SQLException::class)
        override fun setQueryTimeout(seconds: Int) {
            checkClosed()
            queryTimeout = seconds
        }

        @Throws(SQLException::class)
        override fun cancel() {
            checkClosed()
        }

        @Throws(SQLException::class)
        override fun getWarnings(): SQLWarning? {
            checkClosed()
            return warnings
        }

        @Throws(SQLException::class)
        override fun clearWarnings() {
            checkClosed()
        }

        @Throws(SQLException::class)
        override fun setCursorName(name: String) {
            checkClosed()
        }

        @Throws(SQLException::class)
        override fun execute(sql: String): Boolean {
            checkClosed()
            return false
        }

        @Throws(SQLException::class)
        override fun getResultSet(): ResultSet? {
            checkClosed()
            return currentResultSet
        }

        @Throws(SQLException::class)
        override fun getUpdateCount(): Int {
            checkClosed()
            return updateCount
        }

        @Throws(SQLException::class)
        override fun getMoreResults(): Boolean {
            checkClosed()
            return false
        }

        @Throws(SQLException::class)
        override fun setFetchDirection(direction: Int) {
            checkClosed()
            fetchDirection = direction
        }

        @Throws(SQLException::class)
        override fun getFetchDirection(): Int {
            checkClosed()
            return fetchDirection
        }

        @Throws(SQLException::class)
        override fun setFetchSize(rows: Int) {
            checkClosed()
            fetchSize = rows
        }

        @Throws(SQLException::class)
        override fun getFetchSize(): Int {
            checkClosed()
            return fetchSize
        }

        @Throws(SQLException::class)
        override fun getResultSetConcurrency(): Int {
            checkClosed()
            return resultSetConcurrency
        }

        @Throws(SQLException::class)
        override fun getResultSetType(): Int {
            checkClosed()
            return resultSetType
        }

        @Throws(SQLException::class)
        override fun addBatch(sql: String) {
            checkClosed()
        }

        @Throws(SQLException::class)
        override fun clearBatch() {
            checkClosed()
        }

        @Throws(SQLException::class)
        override fun executeBatch(): IntArray {
            checkClosed()
            return IntArray(0)
        }

        @Throws(SQLException::class)
        override fun getConnection(): Connection {
            checkClosed()
            return connection
        }

        @Throws(SQLException::class)
        override fun getMoreResults(current: Int): Boolean {
            checkClosed()
            return false
        }

        @Throws(SQLException::class)
        override fun getGeneratedKeys(): ResultSet {
            checkClosed()
            throw SQLFeatureNotSupportedException("getGeneratedKeys not supported")
        }

        @Throws(SQLException::class)
        override fun executeUpdate(sql: String, autoGeneratedKeys: Int): Int {
            checkClosed()
            return 0
        }

        @Throws(SQLException::class)
        override fun executeUpdate(sql: String, columnIndexes: IntArray): Int {
            checkClosed()
            return 0
        }

        @Throws(SQLException::class)
        override fun executeUpdate(sql: String, columnNames: Array<String>): Int {
            checkClosed()
            return 0
        }

        @Throws(SQLException::class)
        override fun execute(sql: String, autoGeneratedKeys: Int): Boolean {
            checkClosed()
            return false
        }

        @Throws(SQLException::class)
        override fun execute(sql: String, columnIndexes: IntArray): Boolean {
            checkClosed()
            return false
        }

        @Throws(SQLException::class)
        override fun execute(sql: String, columnNames: Array<String>): Boolean {
            checkClosed()
            return false
        }

        @Throws(SQLException::class)
        override fun getResultSetHoldability(): Int {
            checkClosed()
            return resultSetHoldability
        }

        @Throws(SQLException::class)
        override fun isClosed(): Boolean {
            return closed
        }

        @Throws(SQLException::class)
        override fun setPoolable(poolable: Boolean) {
            checkClosed()
            this.poolable = poolable
        }

        @Throws(SQLException::class)
        override fun isPoolable(): Boolean {
            checkClosed()
            return poolable
        }

        @Throws(SQLException::class)
        override fun closeOnCompletion() {
            checkClosed()
            closeOnCompletion = true
        }

        @Throws(SQLException::class)
        override fun isCloseOnCompletion(): Boolean {
            checkClosed()
            return closeOnCompletion
        }

        @Throws(SQLException::class)
        override fun <T> unwrap(iface: Class<T>): T {
            checkClosed()
            if (iface.isAssignableFrom(javaClass)) {
                @Suppress("UNCHECKED_CAST")
                return this as T
            }
            throw SQLException("Cannot unwrap to " + iface.name)
        }

        @Throws(SQLException::class)
        override fun isWrapperFor(iface: Class<*>): Boolean {
            checkClosed()
            return iface.isAssignableFrom(javaClass)
        }

        @Throws(SQLException::class)
        private fun checkClosed() {
            if (closed) {
                throw SQLException("Statement is closed")
            }
        }
    }
}
