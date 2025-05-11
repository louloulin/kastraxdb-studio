package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.ServiceTest
import ai.magicdb.dataservice.api.model.ServiceTestResult

/**
 * 服务测试存储库接口
 *
 * @author magicdb
 */
interface ServiceTestRepository {
    
    /**
     * 保存测试
     *
     * @param test 测试信息
     * @return 测试ID
     */
    fun saveTest(test: ServiceTest): String
    
    /**
     * 删除测试
     *
     * @param testId 测试ID
     * @return 是否成功
     */
    fun deleteTest(testId: String): Boolean
    
    /**
     * 获取测试
     *
     * @param testId 测试ID
     * @return 测试信息
     */
    fun getTest(testId: String): ServiceTest?
    
    /**
     * 获取服务的所有测试
     *
     * @param serviceId 服务ID
     * @return 测试列表
     */
    fun getTestsByService(serviceId: String): List<ServiceTest>
    
    /**
     * 保存测试结果
     *
     * @param result 测试结果
     * @return 是否成功
     */
    fun saveTestResult(result: ServiceTestResult): Boolean
    
    /**
     * 获取测试结果
     *
     * @param testId 测试ID
     * @return 测试结果
     */
    fun getTestResult(testId: String): ServiceTestResult?
    
    /**
     * 获取服务的所有测试结果
     *
     * @param serviceId 服务ID
     * @return 测试结果列表
     */
    fun getTestResultsByService(serviceId: String): List<ServiceTestResult>
}
