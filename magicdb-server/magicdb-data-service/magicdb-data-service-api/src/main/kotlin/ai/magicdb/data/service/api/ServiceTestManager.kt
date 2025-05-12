package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.ServiceTest
import ai.magicdb.data.service.api.model.ServiceTestResult

/**
 * 服务测试管理器接口
 *
 * @author magicdb
 */
interface ServiceTestManager {
    
    /**
     * 创建测试
     *
     * @param test 测试信息
     * @return 测试ID
     */
    fun createTest(test: ServiceTest): String
    
    /**
     * 更新测试
     *
     * @param test 测试信息
     * @return 是否成功
     */
    fun updateTest(test: ServiceTest): Boolean
    
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
     * 执行测试
     *
     * @param testId 测试ID
     * @return 测试结果
     */
    fun runTest(testId: String): ServiceTestResult
    
    /**
     * 执行服务的所有测试
     *
     * @param serviceId 服务ID
     * @return 测试结果列表
     */
    fun runTestsByService(serviceId: String): List<ServiceTestResult>
    
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
