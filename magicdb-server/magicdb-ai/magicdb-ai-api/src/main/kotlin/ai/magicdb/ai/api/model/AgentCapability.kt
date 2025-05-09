package ai.magicdb.ai.api.model

/**
 * 智能体能力枚举
 */
enum class AgentCapability {
    /**
     * SQL生成 - 根据自然语言生成SQL查询
     */
    SQL_GENERATION,
    
    /**
     * SQL优化 - 优化现有SQL查询以提高性能
     */
    SQL_OPTIMIZATION,
    
    /**
     * 数据分析 - 分析数据并生成洞察
     */
    DATA_ANALYSIS,
    
    /**
     * 模式分析 - 分析数据库模式并提供建议
     */
    SCHEMA_ANALYSIS,
    
    /**
     * 性能监控 - 监控数据库性能并提供建议
     */
    PERFORMANCE_MONITORING,
    
    /**
     * 安全审计 - 审计数据库安全并提供建议
     */
    SECURITY_AUDIT,
    
    /**
     * 数据质量 - 检查数据质量并提供建议
     */
    DATA_QUALITY,
    
    /**
     * 文档生成 - 为数据库对象生成文档
     */
    DOCUMENTATION,
    
    /**
     * 协作管理 - 管理团队协作和共享
     */
    COLLABORATION_MANAGEMENT,
    
    /**
     * 学习改进 - 从用户交互中学习并改进
     */
    LEARNING_IMPROVEMENT
}
