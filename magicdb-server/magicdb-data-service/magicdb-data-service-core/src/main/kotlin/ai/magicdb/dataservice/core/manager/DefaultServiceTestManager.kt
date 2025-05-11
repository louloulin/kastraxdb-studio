package ai.magicdb.dataservice.core.manager

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.ServiceTestManager
import ai.magicdb.dataservice.api.ServiceTestRepository
import ai.magicdb.dataservice.api.model.ServiceTest
import ai.magicdb.dataservice.api.model.ServiceTestResult
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 默认服务测试管理器实现
 *
 * @author magicdb
 */
@Service
class DefaultServiceTestManager(
    private val testRepository: ServiceTestRepository,
    private val serviceRepository: DataServiceRepository,
    private val serviceExecutor: DataServiceExecutor
) : ServiceTestManager {
    
    private val logger = LoggerFactory.getLogger(DefaultServiceTestManager::class.java)
    
    override fun createTest(test: ServiceTest): String {
        try {
            // 验证服务是否存在
            val service = serviceRepository.getService(test.serviceId)
                ?: throw IllegalArgumentException("服务不存在: ${test.serviceId}")
            
            // 保存测试
            return testRepository.saveTest(test)
        } catch (e: Exception) {
            logger.error("创建测试失败: {}", test.name, e)
            throw e
        }
    }
    
    override fun updateTest(test: ServiceTest): Boolean {
        try {
            // 验证测试是否存在
            val existingTest = testRepository.getTest(test.id)
                ?: throw IllegalArgumentException("测试不存在: ${test.id}")
            
            // 验证服务是否存在
            val service = serviceRepository.getService(test.serviceId)
                ?: throw IllegalArgumentException("服务不存在: ${test.serviceId}")
            
            // 保留创建时间和创建用户ID
            test.createTime = existingTest.createTime
            test.createUserId = existingTest.createUserId
            
            // 更新测试
            testRepository.saveTest(test)
            
            return true
        } catch (e: Exception) {
            logger.error("更新测试失败: {}", test.id, e)
            return false
        }
    }
    
    override fun deleteTest(testId: String): Boolean {
        try {
            // 删除测试
            return testRepository.deleteTest(testId)
        } catch (e: Exception) {
            logger.error("删除测试失败: {}", testId, e)
            return false
        }
    }
    
    override fun getTest(testId: String): ServiceTest? {
        try {
            // 获取测试
            return testRepository.getTest(testId)
        } catch (e: Exception) {
            logger.error("获取测试失败: {}", testId, e)
            return null
        }
    }
    
    override fun getTestsByService(serviceId: String): List<ServiceTest> {
        try {
            // 获取服务的所有测试
            return testRepository.getTestsByService(serviceId)
        } catch (e: Exception) {
            logger.error("获取服务测试列表失败: {}", serviceId, e)
            return emptyList()
        }
    }
    
    override fun runTest(testId: String): ServiceTestResult {
        try {
            // 获取测试
            val test = testRepository.getTest(testId)
                ?: throw IllegalArgumentException("测试不存在: $testId")
            
            // 创建测试结果
            val result = ServiceTestResult(
                testId = test.id,
                testName = test.name,
                serviceId = test.serviceId,
                parameters = test.parameters,
                expectedResult = test.expectedResult
            )
            
            // 执行测试
            val startTime = System.currentTimeMillis()
            try {
                // 执行服务
                val serviceResult = serviceExecutor.execute(test.serviceId, test.parameters)
                
                // 记录执行时间
                result.executionTime = System.currentTimeMillis() - startTime
                
                // 记录实际结果
                result.actualResult = mapOf(
                    "success" to serviceResult.success,
                    "data" to serviceResult.data,
                    "message" to serviceResult.message,
                    "duration" to serviceResult.duration
                )
                
                // 判断是否通过
                result.passed = compareResults(test.expectedResult, result.actualResult)
                
                if (!result.passed) {
                    result.errorMessage = "测试结果与预期不符"
                }
            } catch (e: Exception) {
                // 记录执行时间
                result.executionTime = System.currentTimeMillis() - startTime
                
                // 记录错误信息
                result.passed = false
                result.errorMessage = e.message ?: "执行测试失败"
                result.actualResult = mapOf(
                    "success" to false,
                    "message" to result.errorMessage
                )
            }
            
            // 保存测试结果
            testRepository.saveTestResult(result)
            
            return result
        } catch (e: Exception) {
            logger.error("执行测试失败: {}", testId, e)
            
            // 创建失败的测试结果
            val result = ServiceTestResult(
                testId = testId,
                passed = false,
                errorMessage = e.message ?: "执行测试失败",
                executionTimestamp = System.currentTimeMillis()
            )
            
            // 保存测试结果
            testRepository.saveTestResult(result)
            
            return result
        }
    }
    
    override fun runTestsByService(serviceId: String): List<ServiceTestResult> {
        try {
            // 获取服务的所有测试
            val tests = testRepository.getTestsByService(serviceId)
            
            // 执行所有测试
            return tests.map { runTest(it.id) }
        } catch (e: Exception) {
            logger.error("执行服务测试失败: {}", serviceId, e)
            return emptyList()
        }
    }
    
    override fun getTestResult(testId: String): ServiceTestResult? {
        try {
            // 获取测试结果
            return testRepository.getTestResult(testId)
        } catch (e: Exception) {
            logger.error("获取测试结果失败: {}", testId, e)
            return null
        }
    }
    
    override fun getTestResultsByService(serviceId: String): List<ServiceTestResult> {
        try {
            // 获取服务的所有测试结果
            return testRepository.getTestResultsByService(serviceId)
        } catch (e: Exception) {
            logger.error("获取服务测试结果列表失败: {}", serviceId, e)
            return emptyList()
        }
    }
    
    /**
     * 比较预期结果和实际结果
     */
    private fun compareResults(expected: Map<String, Any?>, actual: Map<String, Any?>): Boolean {
        // 如果预期结果为空，则认为测试通过
        if (expected.isEmpty()) {
            return true
        }
        
        // 检查实际结果是否包含预期结果中的所有键值对
        for ((key, value) in expected) {
            // 如果实际结果中不包含该键，则测试失败
            if (!actual.containsKey(key)) {
                return false
            }
            
            // 如果值不相等，则测试失败
            val actualValue = actual[key]
            if (value != actualValue) {
                // 如果值是Map类型，则递归比较
                if (value is Map<*, *> && actualValue is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    if (!compareResults(value as Map<String, Any?>, actualValue as Map<String, Any?>)) {
                        return false
                    }
                } else {
                    return false
                }
            }
        }
        
        return true
    }
}
