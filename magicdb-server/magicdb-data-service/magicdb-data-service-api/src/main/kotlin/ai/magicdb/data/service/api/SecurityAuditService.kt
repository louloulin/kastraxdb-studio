package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.SecurityAuditRecord
import java.time.LocalDateTime

/**
 * 安全审计服务接口
 *
 * @author magicdb
 */
interface SecurityAuditService {
    
    /**
     * 记录安全审计
     *
     * @param record 安全审计记录
     * @return 记录ID
     */
    fun recordAudit(record: SecurityAuditRecord): String
    
    /**
     * 获取安全审计记录
     *
     * @param id 记录ID
     * @return 安全审计记录
     */
    fun getAuditRecord(id: String): SecurityAuditRecord?
    
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
    fun queryAuditRecords(
        operationType: SecurityAuditRecord.OperationType? = null,
        targetType: SecurityAuditRecord.TargetType? = null,
        targetId: String? = null,
        userId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        result: SecurityAuditRecord.OperationResult? = null,
        riskLevel: SecurityAuditRecord.RiskLevel? = null,
        reviewed: Boolean? = null,
        page: Int = 0,
        size: Int = 20
    ): List<SecurityAuditRecord>
    
    /**
     * 查询安全审计记录总数
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
     * @return 记录总数
     */
    fun countAuditRecords(
        operationType: SecurityAuditRecord.OperationType? = null,
        targetType: SecurityAuditRecord.TargetType? = null,
        targetId: String? = null,
        userId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        result: SecurityAuditRecord.OperationResult? = null,
        riskLevel: SecurityAuditRecord.RiskLevel? = null,
        reviewed: Boolean? = null
    ): Long
    
    /**
     * 审核安全审计记录
     *
     * @param id 记录ID
     * @param reviewerId 审核人ID
     * @param reviewNotes 审核备注
     * @return 是否成功
     */
    fun reviewAuditRecord(id: String, reviewerId: String, reviewNotes: String?): Boolean
    
    /**
     * 批量审核安全审计记录
     *
     * @param ids 记录ID列表
     * @param reviewerId 审核人ID
     * @param reviewNotes 审核备注
     * @return 成功审核的记录数
     */
    fun batchReviewAuditRecords(ids: List<String>, reviewerId: String, reviewNotes: String?): Int
    
    /**
     * 删除安全审计记录
     *
     * @param id 记录ID
     * @return 是否成功
     */
    fun deleteAuditRecord(id: String): Boolean
    
    /**
     * 批量删除安全审计记录
     *
     * @param ids 记录ID列表
     * @return 成功删除的记录数
     */
    fun batchDeleteAuditRecords(ids: List<String>): Int
    
    /**
     * 清理过期的安全审计记录
     *
     * @param beforeTime 清理该时间之前的记录
     * @return 清理的记录数
     */
    fun cleanupAuditRecords(beforeTime: LocalDateTime): Int
    
    /**
     * 导出安全审计记录
     *
     * @param ids 记录ID列表，为空则导出所有记录
     * @param format 导出格式，支持CSV、EXCEL、PDF
     * @return 导出的文件内容
     */
    fun exportAuditRecords(ids: List<String>? = null, format: String = "CSV"): ByteArray
    
    /**
     * 获取安全审计统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    fun getAuditStatistics(startTime: LocalDateTime? = null, endTime: LocalDateTime? = null): Map<String, Any>
}
