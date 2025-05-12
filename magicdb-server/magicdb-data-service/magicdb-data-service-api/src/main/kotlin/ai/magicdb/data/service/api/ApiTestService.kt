package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.ApiTestCase
import ai.magicdb.data.service.api.model.ApiTestResult

/**
 * API 测试服务接口
 * 提供 API 测试相关功能
 */
interface ApiTestService {
    /**
     * 执行 API 测试
     *
     * @param serviceId 服务 ID
     * @param parameters 参数
     * @return 测试结果
     */
    fun executeTest(serviceId: String, parameters: Map<String, Any?>): ApiTestResult
    
    /**
     * 保存测试用例
     *
     * @param testCase 测试用例
     * @return 保存后的测试用例
     */
    fun saveTestCase(testCase: ApiTestCase): ApiTestCase
    
    /**
     * 获取测试用例
     *
     * @param id 测试用例 ID
     * @return 测试用例
     */
    fun getTestCase(id: String): ApiTestCase?
    
    /**
     * 获取服务的所有测试用例
     *
     * @param serviceId 服务 ID
     * @return 测试用例列表
     */
    fun getTestCasesByService(serviceId: String): List<ApiTestCase>
    
    /**
     * 删除测试用例
     *
     * @param id 测试用例 ID
     * @return 是否删除成功
     */
    fun deleteTestCase(id: String): Boolean
    
    /**
     * 验证参数
     *
     * @param serviceId 服务 ID
     * @param parameters 参数
     * @return 验证结果
     */
    fun validateParameters(serviceId: String, parameters: Map<String, Any?>): Map<String, String>
}
