package ai.magicdb.dataservice.api.model

/**
 * 触发类型枚举
 *
 * @author magicdb
 */
enum class TriggerType {
    /**
     * 手动触发
     */
    MANUAL,
    
    /**
     * 定时触发
     */
    SCHEDULED,
    
    /**
     * 事件触发
     */
    EVENT,
    
    /**
     * API触发
     */
    API,
    
    /**
     * 其他触发
     */
    OTHER
}
