package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.SecurityAuditService
import ai.magicdb.dataservice.api.model.SecurityAuditRecord
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import ai.magicdb.server.tools.base.wrapper.result.PageResult
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

/**
 * 安全审计控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/security/audit")
class SecurityAuditController(
    private val securityAuditService: SecurityAuditService
) {
    
    private val logger = LoggerFactory.getLogger(SecurityAuditController::class.java)
    
    /**
     * 记录安全审计
     *
     * @param record 安全审计记录
     * @return 记录ID
     */
    @PostMapping
    fun recordAudit(@RequestBody record: SecurityAuditRecord): DataResult<String> {
        try {
            val id = securityAuditService.recordAudit(record)
            return DataResult.of(id)
        } catch (e: Exception) {
            logger.error("记录安全审计失败", e)
            return DataResult.error("RECORD_FAILED", "记录安全审计失败: ${e.message}")
        }
    }
    
    /**
     * 获取安全审计记录
     *
     * @param id 记录ID
     * @return 安全审计记录
     */
    @GetMapping("/{id}")
    fun getAuditRecord(@PathVariable id: String): DataResult<SecurityAuditRecord> {
        val record = securityAuditService.getAuditRecord(id)
        return if (record != null) {
            DataResult.of(record)
        } else {
            DataResult.error("RECORD_NOT_FOUND", "安全审计记录不存在")
        }
    }
    
    /**
     * 查询安全审计记录
     *
     * @param operationType 操作类型
     * @param targetType 操作对象类型
     * @param targetId 操作对象ID
     * @param userId 操作用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param result 操作结果
     * @param riskLevel 风险级别
     * @param reviewed 是否已审核
     * @param page 页码
     * @param size 每页大小
     * @return 安全审计记录列表
     */
    @GetMapping("/list")
    fun queryAuditRecords(
        @RequestParam(required = false) operationType: SecurityAuditRecord.OperationType?,
        @RequestParam(required = false) targetType: SecurityAuditRecord.TargetType?,
        @RequestParam(required = false) targetId: String?,
        @RequestParam(required = false) userId: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime?,
        @RequestParam(required = false) result: SecurityAuditRecord.OperationResult?,
        @RequestParam(required = false) riskLevel: SecurityAuditRecord.RiskLevel?,
        @RequestParam(required = false) reviewed: Boolean?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): PageResult<SecurityAuditRecord> {
        try {
            val records = securityAuditService.queryAuditRecords(
                operationType, targetType, targetId, userId, startTime, endTime,
                result, riskLevel, reviewed, page, size
            )
            
            val total = securityAuditService.countAuditRecords(
                operationType, targetType, targetId, userId, startTime, endTime,
                result, riskLevel, reviewed
            )
            
            return PageResult.of(records, total, page, size)
        } catch (e: Exception) {
            logger.error("查询安全审计记录失败", e)
            return PageResult.error("QUERY_FAILED", "查询安全审计记录失败: ${e.message}")
        }
    }
    
    /**
     * 审核安全审计记录
     *
     * @param id 记录ID
     * @param reviewerId 审核人ID
     * @param reviewNotes 审核备注
     * @return 是否成功
     */
    @PostMapping("/{id}/review")
    fun reviewAuditRecord(
        @PathVariable id: String,
        @RequestParam reviewerId: String,
        @RequestParam(required = false) reviewNotes: String?
    ): ActionResult {
        try {
            val success = securityAuditService.reviewAuditRecord(id, reviewerId, reviewNotes)
            return if (success) {
                ActionResult.isSuccess()
            } else {
                ActionResult.fail("REVIEW_FAILED", "审核安全审计记录失败", "")
            }
        } catch (e: Exception) {
            logger.error("审核安全审计记录失败: {}", id, e)
            return ActionResult.fail("REVIEW_FAILED", "审核安全审计记录失败: ${e.message}", "")
        }
    }
    
    /**
     * 批量审核安全审计记录
     *
     * @param ids 记录ID列表
     * @param reviewerId 审核人ID
     * @param reviewNotes 审核备注
     * @return 成功审核的记录数
     */
    @PostMapping("/batch-review")
    fun batchReviewAuditRecords(
        @RequestBody ids: List<String>,
        @RequestParam reviewerId: String,
        @RequestParam(required = false) reviewNotes: String?
    ): DataResult<Int> {
        try {
            val count = securityAuditService.batchReviewAuditRecords(ids, reviewerId, reviewNotes)
            return DataResult.of(count)
        } catch (e: Exception) {
            logger.error("批量审核安全审计记录失败", e)
            return DataResult.error("BATCH_REVIEW_FAILED", "批量审核安全审计记录失败: ${e.message}")
        }
    }
    
    /**
     * 删除安全审计记录
     *
     * @param id 记录ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    fun deleteAuditRecord(@PathVariable id: String): ActionResult {
        try {
            val success = securityAuditService.deleteAuditRecord(id)
            return if (success) {
                ActionResult.isSuccess()
            } else {
                ActionResult.fail("DELETE_FAILED", "删除安全审计记录失败", "")
            }
        } catch (e: Exception) {
            logger.error("删除安全审计记录失败: {}", id, e)
            return ActionResult.fail("DELETE_FAILED", "删除安全审计记录失败: ${e.message}", "")
        }
    }
    
    /**
     * 批量删除安全审计记录
     *
     * @param ids 记录ID列表
     * @return 成功删除的记录数
     */
    @DeleteMapping("/batch")
    fun batchDeleteAuditRecords(@RequestBody ids: List<String>): DataResult<Int> {
        try {
            val count = securityAuditService.batchDeleteAuditRecords(ids)
            return DataResult.of(count)
        } catch (e: Exception) {
            logger.error("批量删除安全审计记录失败", e)
            return DataResult.error("BATCH_DELETE_FAILED", "批量删除安全审计记录失败: ${e.message}")
        }
    }
    
    /**
     * 清理过期的安全审计记录
     *
     * @param beforeTime 清理该时间之前的记录
     * @return 清理的记录数
     */
    @DeleteMapping("/cleanup")
    fun cleanupAuditRecords(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) beforeTime: LocalDateTime
    ): DataResult<Int> {
        try {
            val count = securityAuditService.cleanupAuditRecords(beforeTime)
            return DataResult.of(count)
        } catch (e: Exception) {
            logger.error("清理过期的安全审计记录失败", e)
            return DataResult.error("CLEANUP_FAILED", "清理过期的安全审计记录失败: ${e.message}")
        }
    }
    
    /**
     * 导出安全审计记录
     *
     * @param ids 记录ID列表，为空则导出所有记录
     * @param format 导出格式，支持CSV、EXCEL、PDF
     * @return 导出的文件内容
     */
    @GetMapping("/export")
    fun exportAuditRecords(
        @RequestParam(required = false) ids: List<String>?,
        @RequestParam(defaultValue = "CSV") format: String
    ): ResponseEntity<ByteArray> {
        try {
            val content = securityAuditService.exportAuditRecords(ids, format)
            
            val extension = when (format.uppercase()) {
                "EXCEL" -> "xlsx"
                "PDF" -> "pdf"
                else -> "csv"
            }
            
            val contentType = when (format.uppercase()) {
                "EXCEL" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                "PDF" -> "application/pdf"
                else -> "text/csv"
            }
            
            val filename = URLEncoder.encode("security_audit_records.$extension", StandardCharsets.UTF_8.name())
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$filename")
                .contentType(MediaType.parseMediaType(contentType))
                .body(content)
        } catch (e: Exception) {
            logger.error("导出安全审计记录失败", e)
            return ResponseEntity.badRequest().build()
        }
    }
    
    /**
     * 获取安全审计统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    @GetMapping("/statistics")
    fun getAuditStatistics(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime?
    ): DataResult<Map<String, Any>> {
        try {
            val statistics = securityAuditService.getAuditStatistics(startTime, endTime)
            return DataResult.of(statistics)
        } catch (e: Exception) {
            logger.error("获取安全审计统计信息失败", e)
            return DataResult.error("STATISTICS_FAILED", "获取安全审计统计信息失败: ${e.message}")
        }
    }
}
