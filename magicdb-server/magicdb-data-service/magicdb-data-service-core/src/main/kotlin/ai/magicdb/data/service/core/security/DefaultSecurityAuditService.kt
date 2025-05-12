package ai.magicdb.data.service.core.security

import ai.magicdb.data.service.api.SecurityAuditService
import ai.magicdb.data.service.api.model.SecurityAuditRecord
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.UUID

/**
 * 安全审计服务默认实现
 *
 * @author magicdb
 */
@Service
class DefaultSecurityAuditService(
    private val jdbcTemplate: JdbcTemplate,
    private val securityAuditExporter: SecurityAuditExporter
) : SecurityAuditService {
    
    private val logger = LoggerFactory.getLogger(DefaultSecurityAuditService::class.java)
    
    /**
     * 初始化表结构
     */
    init {
        try {
            // 创建安全审计记录表
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS security_audit_record (
                    id VARCHAR(64) PRIMARY KEY,
                    operation_type VARCHAR(32) NOT NULL,
                    target_type VARCHAR(32) NOT NULL,
                    target_id VARCHAR(64) NOT NULL,
                    user_id VARCHAR(64) NOT NULL,
                    username VARCHAR(128) NOT NULL,
                    ip_address VARCHAR(64) NOT NULL,
                    operation_time TIMESTAMP NOT NULL,
                    details TEXT,
                    result VARCHAR(32) NOT NULL,
                    risk_level VARCHAR(32) NOT NULL,
                    reviewed BOOLEAN NOT NULL DEFAULT FALSE,
                    reviewer_id VARCHAR(64),
                    review_time TIMESTAMP,
                    review_notes TEXT
                )
            """.trimIndent())
            
            // 创建索引
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_security_audit_operation_time ON security_audit_record(operation_time)")
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_security_audit_user_id ON security_audit_record(user_id)")
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_security_audit_target_id ON security_audit_record(target_id)")
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_security_audit_operation_type ON security_audit_record(operation_type)")
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_security_audit_target_type ON security_audit_record(target_type)")
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_security_audit_result ON security_audit_record(result)")
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_security_audit_risk_level ON security_audit_record(risk_level)")
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_security_audit_reviewed ON security_audit_record(reviewed)")
            
            logger.info("安全审计表初始化完成")
        } catch (e: Exception) {
            logger.error("安全审计表初始化失败", e)
        }
    }
    
    @Transactional
    override fun recordAudit(record: SecurityAuditRecord): String {
        val id = record.id.ifBlank { UUID.randomUUID().toString() }
        
        jdbcTemplate.update("""
            INSERT INTO security_audit_record (
                id, operation_type, target_type, target_id, user_id, username, ip_address,
                operation_time, details, result, risk_level, reviewed, reviewer_id, review_time, review_notes
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent(),
            id,
            record.operationType.name,
            record.targetType.name,
            record.targetId,
            record.userId,
            record.username,
            record.ipAddress,
            record.operationTime,
            record.details,
            record.result.name,
            record.riskLevel.name,
            record.reviewed,
            record.reviewerId,
            record.reviewTime,
            record.reviewNotes
        )
        
        logger.debug("记录安全审计: {}", id)
        return id
    }
    
    override fun getAuditRecord(id: String): SecurityAuditRecord? {
        try {
            return jdbcTemplate.queryForObject("""
                SELECT * FROM security_audit_record WHERE id = ?
            """.trimIndent(), SecurityAuditRecordRowMapper(), id)
        } catch (e: Exception) {
            logger.error("获取安全审计记录失败: {}", id, e)
            return null
        }
    }
    
    override fun queryAuditRecords(
        operationType: SecurityAuditRecord.OperationType?,
        targetType: SecurityAuditRecord.TargetType?,
        targetId: String?,
        userId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        result: SecurityAuditRecord.OperationResult?,
        riskLevel: SecurityAuditRecord.RiskLevel?,
        reviewed: Boolean?,
        page: Int,
        size: Int
    ): List<SecurityAuditRecord> {
        val conditions = mutableListOf<String>()
        val params = mutableListOf<Any>()
        
        buildQueryConditions(
            conditions, params, operationType, targetType, targetId, userId,
            startTime, endTime, result, riskLevel, reviewed
        )
        
        val whereClause = if (conditions.isNotEmpty()) "WHERE ${conditions.joinToString(" AND ")}" else ""
        
        val sql = """
            SELECT * FROM security_audit_record
            $whereClause
            ORDER BY operation_time DESC
            LIMIT ? OFFSET ?
        """.trimIndent()
        
        params.add(size)
        params.add(page * size)
        
        return jdbcTemplate.query(sql, SecurityAuditRecordRowMapper(), *params.toTypedArray())
    }
    
    override fun countAuditRecords(
        operationType: SecurityAuditRecord.OperationType?,
        targetType: SecurityAuditRecord.TargetType?,
        targetId: String?,
        userId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        result: SecurityAuditRecord.OperationResult?,
        riskLevel: SecurityAuditRecord.RiskLevel?,
        reviewed: Boolean?
    ): Long {
        val conditions = mutableListOf<String>()
        val params = mutableListOf<Any>()
        
        buildQueryConditions(
            conditions, params, operationType, targetType, targetId, userId,
            startTime, endTime, result, riskLevel, reviewed
        )
        
        val whereClause = if (conditions.isNotEmpty()) "WHERE ${conditions.joinToString(" AND ")}" else ""
        
        val sql = """
            SELECT COUNT(*) FROM security_audit_record
            $whereClause
        """.trimIndent()
        
        return jdbcTemplate.queryForObject(sql, Long::class.java, *params.toTypedArray()) ?: 0
    }
    
    @Transactional
    override fun reviewAuditRecord(id: String, reviewerId: String, reviewNotes: String?): Boolean {
        val rowsAffected = jdbcTemplate.update("""
            UPDATE security_audit_record
            SET reviewed = TRUE, reviewer_id = ?, review_time = ?, review_notes = ?
            WHERE id = ?
        """.trimIndent(),
            reviewerId,
            LocalDateTime.now(),
            reviewNotes,
            id
        )
        
        return rowsAffected > 0
    }
    
    @Transactional
    override fun batchReviewAuditRecords(ids: List<String>, reviewerId: String, reviewNotes: String?): Int {
        if (ids.isEmpty()) {
            return 0
        }
        
        val placeholders = ids.joinToString(",") { "?" }
        val params = mutableListOf<Any>()
        params.add(reviewerId)
        params.add(LocalDateTime.now())
        params.add(reviewNotes ?: "")
        params.addAll(ids)
        
        val sql = """
            UPDATE security_audit_record
            SET reviewed = TRUE, reviewer_id = ?, review_time = ?, review_notes = ?
            WHERE id IN ($placeholders)
        """.trimIndent()
        
        return jdbcTemplate.update(sql, *params.toTypedArray())
    }
    
    @Transactional
    override fun deleteAuditRecord(id: String): Boolean {
        val rowsAffected = jdbcTemplate.update("DELETE FROM security_audit_record WHERE id = ?", id)
        return rowsAffected > 0
    }
    
    @Transactional
    override fun batchDeleteAuditRecords(ids: List<String>): Int {
        if (ids.isEmpty()) {
            return 0
        }
        
        val placeholders = ids.joinToString(",") { "?" }
        val sql = "DELETE FROM security_audit_record WHERE id IN ($placeholders)"
        
        return jdbcTemplate.update(sql, *ids.toTypedArray())
    }
    
    @Transactional
    override fun cleanupAuditRecords(beforeTime: LocalDateTime): Int {
        return jdbcTemplate.update(
            "DELETE FROM security_audit_record WHERE operation_time < ?",
            beforeTime
        )
    }
    
    override fun exportAuditRecords(ids: List<String>?, format: String): ByteArray {
        val records = if (ids.isNullOrEmpty()) {
            jdbcTemplate.query("SELECT * FROM security_audit_record ORDER BY operation_time DESC", SecurityAuditRecordRowMapper())
        } else {
            val placeholders = ids.joinToString(",") { "?" }
            jdbcTemplate.query(
                "SELECT * FROM security_audit_record WHERE id IN ($placeholders) ORDER BY operation_time DESC",
                SecurityAuditRecordRowMapper(),
                *ids.toTypedArray()
            )
        }
        
        return securityAuditExporter.export(records, format)
    }
    
    override fun getAuditStatistics(startTime: LocalDateTime?, endTime: LocalDateTime?): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        
        // 构建时间条件
        val timeConditions = mutableListOf<String>()
        val timeParams = mutableListOf<Any>()
        
        if (startTime != null) {
            timeConditions.add("operation_time >= ?")
            timeParams.add(startTime)
        }
        
        if (endTime != null) {
            timeConditions.add("operation_time <= ?")
            timeParams.add(endTime)
        }
        
        val whereClause = if (timeConditions.isNotEmpty()) "WHERE ${timeConditions.joinToString(" AND ")}" else ""
        
        // 总记录数
        val totalCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM security_audit_record $whereClause",
            Long::class.java,
            *timeParams.toTypedArray()
        ) ?: 0
        result["totalCount"] = totalCount
        
        // 按操作类型统计
        val operationTypeStats = jdbcTemplate.query(
            """
            SELECT operation_type, COUNT(*) as count
            FROM security_audit_record
            $whereClause
            GROUP BY operation_type
            ORDER BY count DESC
            """.trimIndent(),
            { rs, _ -> Pair(rs.getString("operation_type"), rs.getLong("count")) },
            *timeParams.toTypedArray()
        )
        result["operationTypeStats"] = operationTypeStats
        
        // 按目标类型统计
        val targetTypeStats = jdbcTemplate.query(
            """
            SELECT target_type, COUNT(*) as count
            FROM security_audit_record
            $whereClause
            GROUP BY target_type
            ORDER BY count DESC
            """.trimIndent(),
            { rs, _ -> Pair(rs.getString("target_type"), rs.getLong("count")) },
            *timeParams.toTypedArray()
        )
        result["targetTypeStats"] = targetTypeStats
        
        // 按结果统计
        val resultStats = jdbcTemplate.query(
            """
            SELECT result, COUNT(*) as count
            FROM security_audit_record
            $whereClause
            GROUP BY result
            ORDER BY count DESC
            """.trimIndent(),
            { rs, _ -> Pair(rs.getString("result"), rs.getLong("count")) },
            *timeParams.toTypedArray()
        )
        result["resultStats"] = resultStats
        
        // 按风险级别统计
        val riskLevelStats = jdbcTemplate.query(
            """
            SELECT risk_level, COUNT(*) as count
            FROM security_audit_record
            $whereClause
            GROUP BY risk_level
            ORDER BY count DESC
            """.trimIndent(),
            { rs, _ -> Pair(rs.getString("risk_level"), rs.getLong("count")) },
            *timeParams.toTypedArray()
        )
        result["riskLevelStats"] = riskLevelStats
        
        // 按用户统计
        val userStats = jdbcTemplate.query(
            """
            SELECT user_id, username, COUNT(*) as count
            FROM security_audit_record
            $whereClause
            GROUP BY user_id, username
            ORDER BY count DESC
            LIMIT 10
            """.trimIndent(),
            { rs, _ -> 
                mapOf(
                    "userId" to rs.getString("user_id"),
                    "username" to rs.getString("username"),
                    "count" to rs.getLong("count")
                )
            },
            *timeParams.toTypedArray()
        )
        result["userStats"] = userStats
        
        // 按时间统计（最近30天）
        val timeStats = if (startTime == null && endTime == null) {
            jdbcTemplate.query(
                """
                SELECT DATE(operation_time) as date, COUNT(*) as count
                FROM security_audit_record
                WHERE operation_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
                GROUP BY DATE(operation_time)
                ORDER BY date
                """.trimIndent(),
                { rs, _ -> Pair(rs.getString("date"), rs.getLong("count")) }
            )
        } else {
            jdbcTemplate.query(
                """
                SELECT DATE(operation_time) as date, COUNT(*) as count
                FROM security_audit_record
                $whereClause
                GROUP BY DATE(operation_time)
                ORDER BY date
                """.trimIndent(),
                { rs, _ -> Pair(rs.getString("date"), rs.getLong("count")) },
                *timeParams.toTypedArray()
            )
        }
        result["timeStats"] = timeStats
        
        return result
    }
    
    /**
     * 构建查询条件
     */
    private fun buildQueryConditions(
        conditions: MutableList<String>,
        params: MutableList<Any>,
        operationType: SecurityAuditRecord.OperationType?,
        targetType: SecurityAuditRecord.TargetType?,
        targetId: String?,
        userId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        result: SecurityAuditRecord.OperationResult?,
        riskLevel: SecurityAuditRecord.RiskLevel?,
        reviewed: Boolean?
    ) {
        if (operationType != null) {
            conditions.add("operation_type = ?")
            params.add(operationType.name)
        }
        
        if (targetType != null) {
            conditions.add("target_type = ?")
            params.add(targetType.name)
        }
        
        if (!targetId.isNullOrBlank()) {
            conditions.add("target_id = ?")
            params.add(targetId)
        }
        
        if (!userId.isNullOrBlank()) {
            conditions.add("user_id = ?")
            params.add(userId)
        }
        
        if (startTime != null) {
            conditions.add("operation_time >= ?")
            params.add(startTime)
        }
        
        if (endTime != null) {
            conditions.add("operation_time <= ?")
            params.add(endTime)
        }
        
        if (result != null) {
            conditions.add("result = ?")
            params.add(result.name)
        }
        
        if (riskLevel != null) {
            conditions.add("risk_level = ?")
            params.add(riskLevel.name)
        }
        
        if (reviewed != null) {
            conditions.add("reviewed = ?")
            params.add(reviewed)
        }
    }
    
    /**
     * 安全审计记录行映射器
     */
    private class SecurityAuditRecordRowMapper : RowMapper<SecurityAuditRecord> {
        override fun mapRow(rs: ResultSet, rowNum: Int): SecurityAuditRecord {
            return SecurityAuditRecord(
                id = rs.getString("id"),
                operationType = SecurityAuditRecord.OperationType.valueOf(rs.getString("operation_type")),
                targetType = SecurityAuditRecord.TargetType.valueOf(rs.getString("target_type")),
                targetId = rs.getString("target_id"),
                userId = rs.getString("user_id"),
                username = rs.getString("username"),
                ipAddress = rs.getString("ip_address"),
                operationTime = rs.getTimestamp("operation_time").toLocalDateTime(),
                details = rs.getString("details"),
                result = SecurityAuditRecord.OperationResult.valueOf(rs.getString("result")),
                riskLevel = SecurityAuditRecord.RiskLevel.valueOf(rs.getString("risk_level")),
                reviewed = rs.getBoolean("reviewed"),
                reviewerId = rs.getString("reviewer_id"),
                reviewTime = rs.getTimestamp("review_time")?.toLocalDateTime(),
                reviewNotes = rs.getString("review_notes")
            )
        }
    }
}
