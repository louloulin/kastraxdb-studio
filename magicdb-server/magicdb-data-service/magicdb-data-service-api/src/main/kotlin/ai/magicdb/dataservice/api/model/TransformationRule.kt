package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 转换规则类型
 *
 * @author magicdb
 */
enum class TransformationRuleType {
    /**
     * 字段映射
     */
    FIELD_MAPPING,
    
    /**
     * 值转换
     */
    VALUE_CONVERSION,
    
    /**
     * 条件转换
     */
    CONDITIONAL,
    
    /**
     * 聚合转换
     */
    AGGREGATION,
    
    /**
     * 分组转换
     */
    GROUPING,
    
    /**
     * 过滤转换
     */
    FILTERING,
    
    /**
     * 排序转换
     */
    SORTING,
    
    /**
     * 脚本转换
     */
    SCRIPT,
    
    /**
     * 自定义转换
     */
    CUSTOM
}

/**
 * 转换规则
 *
 * @author magicdb
 */
data class TransformationRule(
    /**
     * 规则ID
     */
    var id: String = "",
    
    /**
     * 规则名称
     */
    var name: String = "",
    
    /**
     * 规则描述
     */
    var description: String = "",
    
    /**
     * 规则类型
     */
    var type: TransformationRuleType = TransformationRuleType.FIELD_MAPPING,
    
    /**
     * 源格式
     */
    var sourceFormat: String = "",
    
    /**
     * 目标格式
     */
    var targetFormat: String = "",
    
    /**
     * 规则配置
     */
    var config: Map<String, Any?> = emptyMap(),
    
    /**
     * 创建时间
     */
    var createTime: Long = 0,
    
    /**
     * 更新时间
     */
    var updateTime: Long = 0,
    
    /**
     * 创建用户ID
     */
    var createUserId: Long = 0,
    
    /**
     * 是否启用
     */
    var enabled: Boolean = true,
    
    /**
     * 规则标签
     */
    var tags: List<String> = emptyList()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
