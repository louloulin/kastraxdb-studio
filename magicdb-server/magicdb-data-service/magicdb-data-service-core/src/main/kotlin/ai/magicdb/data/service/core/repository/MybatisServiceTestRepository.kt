package ai.magicdb.data.service.core.repository

import ai.magicdb.data.service.api.ServiceTestRepository
import ai.magicdb.data.service.api.model.ServiceTest
import ai.magicdb.data.service.api.model.ServiceTestResult
import ai.magicdb.data.service.core.entity.ServiceTestDO
import ai.magicdb.data.service.core.entity.ServiceTestResultDO
import ai.magicdb.data.service.core.mapper.ServiceTestMapper
import ai.magicdb.data.service.core.mapper.ServiceTestResultMapper

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.util.*

/**
 * MyBatis服务测试存储库实现
 *
 * @author magicdb
 */
@Repository
class MybatisServiceTestRepository(
    private val serviceTestMapper: ServiceTestMapper,
    private val serviceTestResultMapper: ServiceTestResultMapper,
    private val objectMapper: ObjectMapper
) : ServiceTestRepository {

    private val logger = LoggerFactory.getLogger(MybatisServiceTestRepository::class.java)

    override fun saveTest(test: ServiceTest): String {
        try {
            // 如果没有ID，生成一个新的ID
            if (test.id.isEmpty()) {
                test.id = UUID.randomUUID().toString()
                test.createTime = System.currentTimeMillis()
                test.createUserId = 1
            }

            // 更新时间
            test.updateTime = System.currentTimeMillis()

            // 转换为DO对象
            val testDO = convertToTestDO(test)

            // 保存或更新
            val existingTest = serviceTestMapper.selectById(test.id)
            if (existingTest == null) {
                serviceTestMapper.insert(testDO)
            } else {
                serviceTestMapper.updateById(testDO)
            }

            return test.id
        } catch (e: Exception) {
            logger.error("保存测试失败: {}", test.id, e)
            throw e
        }
    }

    override fun deleteTest(testId: String): Boolean {
        try {
            // 删除测试
            val result = serviceTestMapper.deleteById(testId)

            // 删除测试结果
            serviceTestResultMapper.deleteByTestId(testId)

            return result > 0
        } catch (e: Exception) {
            logger.error("删除测试失败: {}", testId, e)
            return false
        }
    }

    override fun getTest(testId: String): ServiceTest? {
        try {
            // 查询测试
            val testDO = serviceTestMapper.selectById(testId) ?: return null

            // 转换为模型对象
            return convertToTest(testDO)
        } catch (e: Exception) {
            logger.error("获取测试失败: {}", testId, e)
            return null
        }
    }

    override fun getTestsByService(serviceId: String): List<ServiceTest> {
        try {
            // 查询服务的所有测试
            val testDOs = serviceTestMapper.selectByServiceId(serviceId)

            // 转换为模型对象
            return testDOs.map { convertToTest(it) }
        } catch (e: Exception) {
            logger.error("获取服务测试列表失败: {}", serviceId, e)
            return emptyList()
        }
    }

    override fun saveTestResult(result: ServiceTestResult): Boolean {
        try {
            // 设置执行时间戳
            if (result.executionTimestamp == 0L) {
                result.executionTimestamp = System.currentTimeMillis()
            }

            // 设置执行用户ID
            if (result.executionUserId == 0L) {
                result.executionUserId = 1
            }

            // 转换为DO对象
            val resultDO = convertToTestResultDO(result)

            // 保存或更新
            val existingResult = serviceTestResultMapper.selectByTestId(result.testId)
            if (existingResult == null) {
                serviceTestResultMapper.insert(resultDO)
            } else {
                serviceTestResultMapper.updateByTestId(resultDO)
            }

            return true
        } catch (e: Exception) {
            logger.error("保存测试结果失败: {}", result.testId, e)
            return false
        }
    }

    override fun getTestResult(testId: String): ServiceTestResult? {
        try {
            // 查询测试结果
            val resultDO = serviceTestResultMapper.selectByTestId(testId) ?: return null

            // 转换为模型对象
            return convertToTestResult(resultDO)
        } catch (e: Exception) {
            logger.error("获取测试结果失败: {}", testId, e)
            return null
        }
    }

    override fun getTestResultsByService(serviceId: String): List<ServiceTestResult> {
        try {
            // 查询服务的所有测试结果
            val resultDOs = serviceTestResultMapper.selectByServiceId(serviceId)

            // 转换为模型对象
            return resultDOs.map { convertToTestResult(it) }
        } catch (e: Exception) {
            logger.error("获取服务测试结果列表失败: {}", serviceId, e)
            return emptyList()
        }
    }

    /**
     * 转换为测试DO对象
     */
    private fun convertToTestDO(test: ServiceTest): ServiceTestDO {
        val testDO = ServiceTestDO()
        testDO.id = test.id
        testDO.name = test.name
        testDO.serviceId = test.serviceId
        testDO.parameters = objectMapper.writeValueAsString(test.parameters)
        testDO.expectedResult = objectMapper.writeValueAsString(test.expectedResult)
        testDO.description = test.description
        testDO.createTime = test.createTime
        testDO.updateTime = test.updateTime
        testDO.createUserId = test.createUserId
        testDO.tags = if (test.tags.isEmpty()) null else objectMapper.writeValueAsString(test.tags)
        testDO.enabled = test.enabled
        testDO.sort = test.sort
        return testDO
    }

    /**
     * 转换为测试模型对象
     */
    private fun convertToTest(testDO: ServiceTestDO): ServiceTest {
        val test = ServiceTest()
        test.id = testDO.id
        test.name = testDO.name
        test.serviceId = testDO.serviceId
        test.parameters = if (testDO.parameters.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(testDO.parameters, Map::class.java) as Map<String, Any?>
        }
        test.expectedResult = if (testDO.expectedResult.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(testDO.expectedResult, Map::class.java) as Map<String, Any?>
        }
        test.description = testDO.description ?: ""
        test.createTime = testDO.createTime ?: 0
        test.updateTime = testDO.updateTime ?: 0
        test.createUserId = testDO.createUserId ?: 0
        test.tags = if (testDO.tags.isNullOrEmpty()) {
            emptyList()
        } else {
            objectMapper.readValue(testDO.tags, List::class.java) as List<String>
        }
        test.enabled = testDO.enabled ?: true
        test.sort = testDO.sort ?: 0
        return test
    }

    /**
     * 转换为测试结果DO对象
     */
    private fun convertToTestResultDO(result: ServiceTestResult): ServiceTestResultDO {
        val resultDO = ServiceTestResultDO()
        resultDO.testId = result.testId
        resultDO.testName = result.testName
        resultDO.serviceId = result.serviceId
        resultDO.parameters = objectMapper.writeValueAsString(result.parameters)
        resultDO.expectedResult = objectMapper.writeValueAsString(result.expectedResult)
        resultDO.actualResult = objectMapper.writeValueAsString(result.actualResult)
        resultDO.passed = result.passed
        resultDO.errorMessage = result.errorMessage
        resultDO.executionTime = result.executionTime
        resultDO.executionTimestamp = result.executionTimestamp
        resultDO.executionUserId = result.executionUserId
        return resultDO
    }

    /**
     * 转换为测试结果模型对象
     */
    private fun convertToTestResult(resultDO: ServiceTestResultDO): ServiceTestResult {
        val result = ServiceTestResult()
        result.testId = resultDO.testId
        result.testName = resultDO.testName ?: ""
        result.serviceId = resultDO.serviceId
        result.parameters = if (resultDO.parameters.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(resultDO.parameters, Map::class.java) as Map<String, Any?>
        }
        result.expectedResult = if (resultDO.expectedResult.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(resultDO.expectedResult, Map::class.java) as Map<String, Any?>
        }
        result.actualResult = if (resultDO.actualResult.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(resultDO.actualResult, Map::class.java) as Map<String, Any?>
        }
        result.passed = resultDO.passed ?: false
        result.errorMessage = resultDO.errorMessage
        result.executionTime = resultDO.executionTime ?: 0
        result.executionTimestamp = resultDO.executionTimestamp ?: 0
        result.executionUserId = resultDO.executionUserId ?: 0
        return result
    }
}
