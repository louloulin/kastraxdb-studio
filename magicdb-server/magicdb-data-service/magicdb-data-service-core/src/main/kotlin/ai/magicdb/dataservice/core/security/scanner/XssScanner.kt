package ai.magicdb.dataservice.core.security.scanner

import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.VulnerabilityScanService
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.VulnerabilityRecord
import ai.magicdb.dataservice.core.security.VulnerabilityScanner
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID
import java.util.regex.Pattern

/**
 * XSS漏洞扫描器
 *
 * @author magicdb
 */
@Component
class XssScanner(
    private val dataServiceRepository: DataServiceRepository
) : VulnerabilityScanner {
    
    private val logger = LoggerFactory.getLogger(XssScanner::class.java)
    
    // XSS风险模式
    private val xssPatterns = listOf(
        Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<img[^>]*src[^>]*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<iframe[^>]*src[^>]*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<embed[^>]*src[^>]*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<object[^>]*data[^>]*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\\\x[0-9A-Fa-f]{2}", Pattern.CASE_INSENSITIVE),
        Pattern.compile("&#x?[0-9A-Fa-f]+;", Pattern.CASE_INSENSITIVE)
    )
    
    // 输出编码模式
    private val encodingPatterns = listOf(
        Pattern.compile("escapeHtml", Pattern.CASE_INSENSITIVE),
        Pattern.compile("htmlspecialchars", Pattern.CASE_INSENSITIVE),
        Pattern.compile("htmlentities", Pattern.CASE_INSENSITIVE),
        Pattern.compile("encodeURIComponent", Pattern.CASE_INSENSITIVE),
        Pattern.compile("encodeURI", Pattern.CASE_INSENSITIVE),
        Pattern.compile("sanitize", Pattern.CASE_INSENSITIVE)
    )
    
    override fun getSupportedVulnerabilityType(): VulnerabilityRecord.VulnerabilityType {
        return VulnerabilityRecord.VulnerabilityType.XSS
    }
    
    override fun supportsTargetType(targetType: VulnerabilityRecord.TargetType): Boolean {
        return targetType == VulnerabilityRecord.TargetType.DATA_SERVICE || 
               targetType == VulnerabilityRecord.TargetType.API
    }
    
    override fun scan(targetType: VulnerabilityRecord.TargetType, targetId: String): List<VulnerabilityRecord> {
        if (targetType == VulnerabilityRecord.TargetType.DATA_SERVICE) {
            // 扫描数据服务
            val service = dataServiceRepository.getService(targetId)
            if (service == null) {
                logger.warn("数据服务不存在: {}", targetId)
                return emptyList()
            }
            
            return scanDataServiceForXss(service)
        } else if (targetType == VulnerabilityRecord.TargetType.API) {
            // 扫描API接口
            // 这里简化处理，实际应该从API仓库获取API信息
            logger.warn("API扫描未实现")
            return emptyList()
        }
        
        return emptyList()
    }
    
    override fun getFixSuggestions(record: VulnerabilityRecord): List<VulnerabilityScanService.FixSuggestion> {
        if (record.type != VulnerabilityRecord.VulnerabilityType.XSS) {
            return emptyList()
        }
        
        return listOf(
            VulnerabilityScanService.FixSuggestion(
                id = UUID.randomUUID().toString(),
                title = "输出编码",
                description = "对输出到HTML页面的数据进行适当的编码，防止浏览器将其解释为HTML或JavaScript代码。",
                fixCode = """
                    // 不安全的写法
                    response.write("<div>" + userInput + "</div>");
                    
                    // 安全的写法
                    response.write("<div>" + escapeHtml(userInput) + "</div>");
                    
                    // 编码函数示例
                    function escapeHtml(str) {
                        return str
                            .replace(/&/g, "&amp;")
                            .replace(/</g, "&lt;")
                            .replace(/>/g, "&gt;")
                            .replace(/"/g, "&quot;")
                            .replace(/'/g, "&#039;");
                    }
                """.trimIndent(),
                referenceLinks = listOf(
                    "https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html",
                    "https://owasp.org/www-community/attacks/xss/"
                ),
                autoFixable = true
            ),
            VulnerabilityScanService.FixSuggestion(
                id = UUID.randomUUID().toString(),
                title = "内容安全策略 (CSP)",
                description = "使用内容安全策略限制页面可以加载的资源，防止XSS攻击。",
                fixCode = """
                    // 在HTTP响应头中添加CSP
                    Content-Security-Policy: default-src 'self'; script-src 'self' https://trusted-cdn.com
                    
                    // 或在HTML中添加CSP
                    <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self' https://trusted-cdn.com">
                """.trimIndent(),
                referenceLinks = listOf(
                    "https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP",
                    "https://cheatsheetseries.owasp.org/cheatsheets/Content_Security_Policy_Cheat_Sheet.html"
                ),
                autoFixable = false
            ),
            VulnerabilityScanService.FixSuggestion(
                id = UUID.randomUUID().toString(),
                title = "使用XSS过滤库",
                description = "使用成熟的XSS过滤库对用户输入进行过滤，如DOMPurify、js-xss等。",
                fixCode = """
                    // 使用DOMPurify过滤HTML
                    const clean = DOMPurify.sanitize(userInput);
                    element.innerHTML = clean;
                    
                    // 使用js-xss过滤HTML
                    const xss = require('xss');
                    const clean = xss(userInput);
                    element.innerHTML = clean;
                """.trimIndent(),
                referenceLinks = listOf(
                    "https://github.com/cure53/DOMPurify",
                    "https://github.com/leizongmin/js-xss"
                ),
                autoFixable = false
            )
        )
    }
    
    override fun canAutoFix(record: VulnerabilityRecord): Boolean {
        // 某些XSS漏洞可以自动修复
        return record.type == VulnerabilityRecord.VulnerabilityType.XSS && 
               record.targetType == VulnerabilityRecord.TargetType.DATA_SERVICE
    }
    
    override fun autoFix(record: VulnerabilityRecord): VulnerabilityScanner.FixResult {
        if (!canAutoFix(record)) {
            return VulnerabilityScanner.FixResult(
                success = false,
                notes = "此XSS漏洞不支持自动修复，请参考修复建议手动修复。"
            )
        }
        
        try {
            // 获取数据服务
            val service = dataServiceRepository.getService(record.targetId)
            if (service == null) {
                return VulnerabilityScanner.FixResult(
                    success = false,
                    notes = "数据服务不存在: ${record.targetId}"
                )
            }
            
            // 获取服务配置
            val metadata = service.metadata.toMutableMap()
            
            // 添加XSS防护配置
            metadata["enableXssProtection"] = true
            metadata["sanitizeOutput"] = true
            
            // 更新服务配置
            val updatedService = service.copy(
                metadata = metadata,
                updateTime = Date()
            )
            
            // 保存更新后的服务
            dataServiceRepository.saveService(updatedService)
            
            return VulnerabilityScanner.FixResult(
                success = true,
                notes = "已自动添加XSS防护配置，启用输出净化功能。"
            )
        } catch (e: Exception) {
            logger.error("自动修复XSS漏洞异常: {}", record.id, e)
            return VulnerabilityScanner.FixResult(
                success = false,
                notes = "自动修复失败: ${e.message}"
            )
        }
    }
    
    /**
     * 扫描数据服务的XSS漏洞
     *
     * @param service 数据服务
     * @return 漏洞记录列表
     */
    private fun scanDataServiceForXss(service: DataService): List<VulnerabilityRecord> {
        val vulnerabilities = mutableListOf<VulnerabilityRecord>()
        
        try {
            // 获取服务配置
            val metadata = service.metadata
            
            // 检查是否启用XSS防护
            val enableXssProtection = metadata["enableXssProtection"] as? Boolean ?: false
            val sanitizeOutput = metadata["sanitizeOutput"] as? Boolean ?: false
            
            // 检查响应处理逻辑
            val responseHandler = service.script
            val hasXssRisk = responseHandler.isNotEmpty() && xssPatterns.any { it.matcher(responseHandler).find() }
            
            // 检查是否使用输出编码
            val usesEncoding = responseHandler.isNotEmpty() && encodingPatterns.any { it.matcher(responseHandler).find() }
            
            // 如果存在XSS风险且没有启用防护措施，则创建漏洞记录
            if ((hasXssRisk || !usesEncoding) && (!enableXssProtection || !sanitizeOutput)) {
                val riskLevel = if (hasXssRisk) {
                    VulnerabilityRecord.RiskLevel.HIGH
                } else {
                    VulnerabilityRecord.RiskLevel.MEDIUM
                }
                
                val description = if (hasXssRisk) {
                    "响应处理逻辑中存在潜在的XSS风险"
                } else {
                    "未对输出进行适当的编码，可能导致XSS攻击"
                }
                
                vulnerabilities.add(
                    VulnerabilityRecord(
                        id = UUID.randomUUID().toString(),
                        type = VulnerabilityRecord.VulnerabilityType.XSS,
                        targetType = VulnerabilityRecord.TargetType.DATA_SERVICE,
                        targetId = service.id,
                        description = description,
                        details = """
                            服务名称: ${service.name}
                            服务类型: ${service.type}
                            
                            风险分析:
                            - 启用XSS防护: ${if (enableXssProtection) "是" else "否"}
                            - 启用输出净化: ${if (sanitizeOutput) "是" else "否"}
                            - 使用输出编码: ${if (usesEncoding) "是" else "否"}
                            - 存在XSS风险模式: ${if (hasXssRisk) "是" else "否"}
                            
                            建议:
                            1. 启用XSS防护功能
                            2. 对输出进行适当的编码
                            3. 使用内容安全策略(CSP)
                            4. 使用成熟的XSS过滤库
                        """.trimIndent(),
                        riskLevel = riskLevel,
                        discoveredTime = LocalDateTime.now(),
                        cvssScore = if (riskLevel == VulnerabilityRecord.RiskLevel.HIGH) 7.5 else 5.5
                    )
                )
            }
        } catch (e: Exception) {
            logger.error("扫描XSS漏洞异常: {}", service.id, e)
        }
        
        return vulnerabilities
    }
}
