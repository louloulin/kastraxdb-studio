package ai.magicdb.data.service.api.model

import java.time.LocalDateTime

/**
 * 安全审计记录
 *
 * @author magicdb
 */
data class SecurityAuditRecord(
    /**
     * 记录ID
     */
    val id: String,
    
    /**
     * 操作类型
     */
    val operationType: OperationType,
    
    /**
     * 操作对象类型
     */
    val targetType: TargetType,
    
    /**
     * 操作对象ID
     */
    val targetId: String,
    
    /**
     * 操作用户ID
     */
    val userId: String,
    
    /**
     * 用户名
     */
    val username: String,
    
    /**
     * 操作IP
     */
    val ipAddress: String,
    
    /**
     * 操作时间
     */
    val operationTime: LocalDateTime = LocalDateTime.now(),
    
    /**
     * 操作详情
     */
    val details: String? = null,
    
    /**
     * 操作结果
     */
    val result: OperationResult,
    
    /**
     * 风险级别
     */
    val riskLevel: RiskLevel = RiskLevel.LOW,
    
    /**
     * 是否已审核
     */
    val reviewed: Boolean = false,
    
    /**
     * 审核人ID
     */
    val reviewerId: String? = null,
    
    /**
     * 审核时间
     */
    val reviewTime: LocalDateTime? = null,
    
    /**
     * 审核备注
     */
    val reviewNotes: String? = null
) {
    /**
     * 操作类型
     */
    enum class OperationType {
        /**
         * 登录
         */
        LOGIN,
        
        /**
         * 登出
         */
        LOGOUT,
        
        /**
         * 创建
         */
        CREATE,
        
        /**
         * 读取
         */
        READ,
        
        /**
         * 更新
         */
        UPDATE,
        
        /**
         * 删除
         */
        DELETE,
        
        /**
         * 执行
         */
        EXECUTE,
        
        /**
         * 导入
         */
        IMPORT,
        
        /**
         * 导出
         */
        EXPORT,
        
        /**
         * 授权
         */
        GRANT,
        
        /**
         * 撤销授权
         */
        REVOKE,
        
        /**
         * 系统配置
         */
        CONFIGURE,
        
        /**
         * 其他
         */
        OTHER
    }
    
    /**
     * 操作对象类型
     */
    enum class TargetType {
        /**
         * 数据服务
         */
        DATA_SERVICE,
        
        /**
         * 服务分组
         */
        SERVICE_GROUP,
        
        /**
         * 数据源
         */
        DATA_SOURCE,
        
        /**
         * 用户
         */
        USER,
        
        /**
         * 角色
         */
        ROLE,
        
        /**
         * 权限
         */
        PERMISSION,
        
        /**
         * 系统配置
         */
        SYSTEM_CONFIG,
        
        /**
         * 脚本
         */
        SCRIPT,
        
        /**
         * 定时任务
         */
        SCHEDULED_TASK,
        
        /**
         * 文档
         */
        DOCUMENT,
        
        /**
         * 其他
         */
        OTHER
    }
    
    /**
     * 操作结果
     */
    enum class OperationResult {
        /**
         * 成功
         */
        SUCCESS,
        
        /**
         * 失败
         */
        FAILURE,
        
        /**
         * 警告
         */
        WARNING,
        
        /**
         * 拒绝
         */
        DENIED
    }
    
    /**
     * 风险级别
     */
    enum class RiskLevel {
        /**
         * 低风险
         */
        LOW,
        
        /**
         * 中风险
         */
        MEDIUM,
        
        /**
         * 高风险
         */
        HIGH,
        
        /**
         * 严重风险
         */
        CRITICAL
    }
}
