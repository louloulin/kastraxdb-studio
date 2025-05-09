package ai.magicdb.ai.api.model

/**
 * 智能体类型枚举
 */
enum class AgentType {
    /**
     * 查询智能体 - 专注于优化和执行SQL查询
     */
    QUERY,
    
    /**
     * 管理智能体 - 负责数据库管理、备份和维护任务
     */
    MANAGEMENT,
    
    /**
     * 分析智能体 - 专注于数据分析和洞察生成
     */
    ANALYSIS,
    
    /**
     * 安全智能体 - 监控数据库安全，提供权限管理建议
     */
    SECURITY,
    
    /**
     * 协作智能体 - 促进团队协作，管理共享查询和结果
     */
    COLLABORATION,
    
    /**
     * 学习智能体 - 从用户交互中学习，不断改进其他智能体的表现
     */
    LEARNING
}
