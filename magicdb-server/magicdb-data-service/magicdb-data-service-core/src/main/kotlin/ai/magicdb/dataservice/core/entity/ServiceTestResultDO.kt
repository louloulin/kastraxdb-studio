package ai.magicdb.dataservice.core.entity

import com.baomidou.mybatisplus.annotation.TableName

/**
 * 服务测试结果实体类
 *
 * @author magicdb
 */
@TableName("data_service_test_result")
class ServiceTestResultDO {
    
    /**
     * 测试ID
     */
    var testId: String = ""
    
    /**
     * 测试名称
     */
    var testName: String? = null
    
    /**
     * 服务ID
     */
    var serviceId: String = ""
    
    /**
     * 测试参数（JSON格式）
     */
    var parameters: String? = null
    
    /**
     * 预期结果（JSON格式）
     */
    var expectedResult: String? = null
    
    /**
     * 实际结果（JSON格式）
     */
    var actualResult: String? = null
    
    /**
     * 是否通过
     */
    var passed: Boolean? = null
    
    /**
     * 错误信息
     */
    var errorMessage: String? = null
    
    /**
     * 执行时间（毫秒）
     */
    var executionTime: Long? = null
    
    /**
     * 执行时间戳
     */
    var executionTimestamp: Long? = null
    
    /**
     * 执行用户ID
     */
    var executionUserId: Long? = null
}
