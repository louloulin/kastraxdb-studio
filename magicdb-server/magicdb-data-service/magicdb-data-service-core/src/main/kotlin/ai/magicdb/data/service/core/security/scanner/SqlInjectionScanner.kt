package ai.magicdb.data.service.core.security.scanner

import ai.magicdb.data.service.api.DataServiceRepository
import ai.magicdb.data.service.api.VulnerabilityScanService
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.VulnerabilityRecord
import ai.magicdb.data.service.core.security.VulnerabilityScanner
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID
import java.util.regex.Pattern

/**
 * SQL注入漏洞扫描器
 *
 * @author magicdb
 */
@Component
class SqlInjectionScanner(
    private val dataServiceRepository: DataServiceRepository
) : VulnerabilityScanner {
    
    private val logger = LoggerFactory.getLogger(SqlInjectionScanner::class.java)
    
    // SQL注入风险模式
    private val sqlInjectionPatterns = listOf(
        Pattern.compile(".*\\s+or\\s+['\"\\s]*?[0-9].*?[=><].*?['\"\\s]*?[0-9]", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+and\\s+['\"\\s]*?[0-9].*?[=><].*?['\"\\s]*?[0-9]", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+union\\s+select\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+insert\\s+into\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+update\\s+.*?\\s+set\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+delete\\s+from\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+drop\\s+table\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+drop\\s+database\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+truncate\\s+table\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+exec\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+execute\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+xp_cmdshell", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+information_schema", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+concat\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+group_concat", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\s+load_file\\s*\\(", Pattern.CASE_INSENSITIVE)
    )
    
    // 参数化查询模式
    private val parameterizedQueryPatterns = listOf(
        Pattern.compile("\\?", Pattern.CASE_INSENSITIVE),
        Pattern.compile(":[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\$[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE)
    )
    
    override fun getSupportedVulnerabilityType(): VulnerabilityRecord.VulnerabilityType {
        return VulnerabilityRecord.VulnerabilityType.SQL_INJECTION
    }
    
    override fun supportsTargetType(targetType: VulnerabilityRecord.TargetType): Boolean {
        return targetType == VulnerabilityRecord.TargetType.DATA_SERVICE
    }
    
    override fun scan(targetType: VulnerabilityRecord.TargetType, targetId: String): List<VulnerabilityRecord> {
        if (targetType != VulnerabilityRecord.TargetType.DATA_SERVICE) {
            logger.warn("SQL注入扫描器不支持目标类型: {}", targetType)
            return emptyList()
        }
        
        // 获取数据服务
        val service = dataServiceRepository.getService(targetId)
        if (service == null) {
            logger.warn("数据服务不存在: {}", targetId)
            return emptyList()
        }
        
        // 检查服务类型
        if (service.type != "SQL") {
            logger.debug("跳过非SQL类型的服务: {}, type={}", targetId, service.type)
            return emptyList()
        }
        
        // 扫描SQL注入漏洞
        return scanSqlInjectionVulnerabilities(service)
    }
    
    override fun getFixSuggestions(record: VulnerabilityRecord): List<VulnerabilityScanService.FixSuggestion> {
        if (record.type != VulnerabilityRecord.VulnerabilityType.SQL_INJECTION) {
            return emptyList()
        }
        
        return listOf(
            VulnerabilityScanService.FixSuggestion(
                id = UUID.randomUUID().toString(),
                title = "使用参数化查询",
                description = "使用参数化查询（预处理语句）代替直接拼接SQL语句，可以有效防止SQL注入攻击。",
                fixCode = """
                    // 不安全的写法
                    String sql = "SELECT * FROM users WHERE username = '" + username + "'";
                    
                    // 安全的写法
                    String sql = "SELECT * FROM users WHERE username = ?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, username);
                """.trimIndent(),
                referenceLinks = listOf(
                    "https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html",
                    "https://owasp.org/www-community/attacks/SQL_Injection"
                ),
                autoFixable = false
            ),
            VulnerabilityScanService.FixSuggestion(
                id = UUID.randomUUID().toString(),
                title = "输入验证和过滤",
                description = "对用户输入进行严格的验证和过滤，拒绝包含可疑SQL关键字或特殊字符的输入。",
                fixCode = """
                    // 简单的输入验证示例
                    if (input.matches(".*[;'\"].*")) {
                        throw new IllegalArgumentException("输入包含非法字符");
                    }
                    
                    // 使用正则表达式验证输入是否符合预期格式
                    if (!input.matches("^[a-zA-Z0-9_]+$")) {
                        throw new IllegalArgumentException("输入格式不正确");
                    }
                """.trimIndent(),
                referenceLinks = listOf(
                    "https://cheatsheetseries.owasp.org/cheatsheets/Input_Validation_Cheat_Sheet.html"
                ),
                autoFixable = false
            ),
            VulnerabilityScanService.FixSuggestion(
                id = UUID.randomUUID().toString(),
                title = "最小权限原则",
                description = "为数据库连接使用最小权限的账户，限制数据库操作的范围，减少SQL注入攻击的影响。",
                fixCode = null,
                referenceLinks = listOf(
                    "https://cheatsheetseries.owasp.org/cheatsheets/Database_Security_Cheat_Sheet.html"
                ),
                autoFixable = false
            )
        )
    }
    
    override fun canAutoFix(record: VulnerabilityRecord): Boolean {
        // SQL注入漏洞通常需要手动修复
        return false
    }
    
    override fun autoFix(record: VulnerabilityRecord): VulnerabilityScanner.FixResult {
        return VulnerabilityScanner.FixResult(
            success = false,
            notes = "SQL注入漏洞需要手动修复，请参考修复建议。"
        )
    }
    
    /**
     * 扫描SQL注入漏洞
     *
     * @param service 数据服务
     * @return 漏洞记录列表
     */
    private fun scanSqlInjectionVulnerabilities(service: DataService): List<VulnerabilityRecord> {
        val vulnerabilities = mutableListOf<VulnerabilityRecord>()
        
        try {
            // 获取SQL语句
            val sqlScript = service.script
            if (sqlScript.isBlank()) {
                logger.debug("SQL语句为空: {}", service.id)
                return emptyList()
            }
            
            // 检查是否使用参数化查询
            val usesParameterizedQuery = parameterizedQueryPatterns.any { it.matcher(sqlScript).find() }
            
            // 检查是否存在SQL注入风险
            val hasSqlInjectionRisk = sqlInjectionPatterns.any { it.matcher(sqlScript).find() }
            
            // 检查是否直接拼接用户输入
            val directConcatenation = sqlScript.contains("\${") || sqlScript.contains("$")
            
            // 如果存在SQL注入风险或直接拼接用户输入，且没有使用参数化查询，则创建漏洞记录
            if ((hasSqlInjectionRisk || directConcatenation) && !usesParameterizedQuery) {
                val riskLevel = if (hasSqlInjectionRisk) {
                    VulnerabilityRecord.RiskLevel.HIGH
                } else {
                    VulnerabilityRecord.RiskLevel.MEDIUM
                }
                
                val description = if (hasSqlInjectionRisk) {
                    "SQL语句中存在潜在的SQL注入风险"
                } else {
                    "SQL语句中直接拼接用户输入，可能导致SQL注入"
                }
                
                vulnerabilities.add(
                    VulnerabilityRecord(
                        id = UUID.randomUUID().toString(),
                        type = VulnerabilityRecord.VulnerabilityType.SQL_INJECTION,
                        targetType = VulnerabilityRecord.TargetType.DATA_SERVICE,
                        targetId = service.id,
                        description = description,
                        details = """
                            服务名称: ${service.name}
                            服务类型: ${service.type}
                            SQL语句: $sqlScript
                            
                            风险分析:
                            - 使用参数化查询: ${if (usesParameterizedQuery) "是" else "否"}
                            - 存在SQL注入风险模式: ${if (hasSqlInjectionRisk) "是" else "否"}
                            - 直接拼接用户输入: ${if (directConcatenation) "是" else "否"}
                            
                            建议:
                            1. 使用参数化查询代替直接拼接SQL语句
                            2. 对用户输入进行严格的验证和过滤
                            3. 使用最小权限的数据库账户
                        """.trimIndent(),
                        riskLevel = riskLevel,
                        discoveredTime = LocalDateTime.now(),
                        cvssScore = if (riskLevel == VulnerabilityRecord.RiskLevel.HIGH) 8.5 else 6.5
                    )
                )
            }
        } catch (e: Exception) {
            logger.error("扫描SQL注入漏洞异常: {}", service.id, e)
        }
        
        return vulnerabilities
    }
}
