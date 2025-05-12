package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 服务测试
 *
 * @author magicdb
 */
data class ServiceTest(
    /**
     * 测试ID
     */
    var id: String = "",
    
    /**
     * 测试名称
     */
    var name: String = "",
    
    /**
     * 服务ID
     */
    var serviceId: String = "",
    
    /**
     * 测试参数
     */
    var parameters: Map<String, Any?> = emptyMap(),
    
    /**
     * 预期结果
     */
    var expectedResult: Map<String, Any?> = emptyMap(),
    
    /**
     * 测试描述
     */
    var description: String = "",
    
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
     * 标签
     */
    var tags: List<String> = emptyList(),
    
    /**
     * 是否启用
     */
    var enabled: Boolean = true,
    
    /**
     * 排序
     */
    var sort: Int = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
