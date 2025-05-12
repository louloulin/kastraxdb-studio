package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 服务测试结果
 *
 * @author magicdb
 */
data class ServiceTestResult(
    /**
     * 测试ID
     */
    var testId: String = "",
    
    /**
     * 测试名称
     */
    var testName: String = "",
    
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
     * 实际结果
     */
    var actualResult: Map<String, Any?> = emptyMap(),
    
    /**
     * 是否通过
     */
    var passed: Boolean = false,
    
    /**
     * 错误信息
     */
    var errorMessage: String? = null,
    
    /**
     * 执行时间（毫秒）
     */
    var executionTime: Long = 0,
    
    /**
     * 执行时间戳
     */
    var executionTimestamp: Long = 0,
    
    /**
     * 执行用户ID
     */
    var executionUserId: Long = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
