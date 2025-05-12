package ai.magicdb.data.service.core.security

import ai.magicdb.data.service.api.model.VulnerabilityRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * 扫描器注册表
 *
 * @author magicdb
 */
@Component
class ScannerRegistry(
    private val scanners: List<VulnerabilityScanner>
) {
    
    private val logger = LoggerFactory.getLogger(ScannerRegistry::class.java)
    
    // 扫描器映射表，按漏洞类型索引
    private val scannerMap = mutableMapOf<VulnerabilityRecord.VulnerabilityType, VulnerabilityScanner>()
    
    /**
     * 初始化
     */
    @PostConstruct
    fun init() {
        scanners.forEach { scanner ->
            val type = scanner.getSupportedVulnerabilityType()
            scannerMap[type] = scanner
            logger.info("注册扫描器: {} -> {}", type, scanner.javaClass.simpleName)
        }
    }
    
    /**
     * 获取指定类型的扫描器
     *
     * @param type 漏洞类型
     * @return 扫描器
     */
    fun getScanner(type: VulnerabilityRecord.VulnerabilityType): VulnerabilityScanner? {
        return scannerMap[type]
    }
    
    /**
     * 获取所有扫描器
     *
     * @return 扫描器列表
     */
    fun getAllScanners(): List<VulnerabilityScanner> {
        return scannerMap.values.toList()
    }
}
