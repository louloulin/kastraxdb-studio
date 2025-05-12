package ai.magicdb.data.service.core.test

import ai.magicdb.data.service.api.ApiTestService
import ai.magicdb.data.service.api.DataServiceExecutor
import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.model.ApiTestCase
import ai.magicdb.data.service.api.model.ApiTestResult
import ai.magicdb.data.service.api.model.ServiceParameter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 默认 API 测试服务实现
 */
@Service
class DefaultApiTestService(
    private val dataServiceManager: DataServiceManager,
    private val dataServiceExecutor: DataServiceExecutor
) : ApiTestService {

    private val logger = LoggerFactory.getLogger(DefaultApiTestService::class.java)

    // 测试用例存储
    private val testCases = ConcurrentHashMap<String, ApiTestCase>()

    override fun executeTest(serviceId: String, parameters: Map<String, Any?>): ApiTestResult {
        try {
            // 获取服务
            val service = dataServiceManager.getService(serviceId)
                ?: return ApiTestResult(
                    success = false,
                    data = null,
                    errorMessage = "Service not found: $serviceId",
                    executionTime = 0,
                    parameters = parameters,
                    serviceId = serviceId
                )

            // 验证参数
            val validationErrors = validateParameters(serviceId, parameters)
            if (validationErrors.isNotEmpty()) {
                return ApiTestResult(
                    success = false,
                    data = null,
                    errorMessage = "Parameter validation failed: ${validationErrors.values.joinToString(", ")}",
                    executionTime = 0,
                    parameters = parameters,
                    serviceId = serviceId
                )
            }

            // 执行服务
            val startTime = System.currentTimeMillis()
            val result = dataServiceExecutor.execute(service, parameters)
            val executionTime = System.currentTimeMillis() - startTime

            // 创建测试结果
            return ApiTestResult(
                success = true,
                data = result,
                executionTime = executionTime,
                parameters = parameters,
                serviceId = serviceId
            )
        } catch (e: Exception) {
            logger.error("执行 API 测试失败: {}", e.message, e)

            // 创建错误结果
            return ApiTestResult(
                success = false,
                data = null,
                errorMessage = e.message,
                executionTime = 0,
                parameters = parameters,
                serviceId = serviceId
            )
        }
    }

    override fun saveTestCase(testCase: ApiTestCase): ApiTestCase {
        val id = testCase.id.ifBlank { UUID.randomUUID().toString() }
        val now = Date()

        val updatedTestCase = testCase.copy(
            id = id,
            updateTime = now,
            createTime = testCases[id]?.createTime ?: now
        )

        testCases[id] = updatedTestCase
        logger.info("保存测试用例: {}", id)

        return updatedTestCase
    }

    override fun getTestCase(id: String): ApiTestCase? {
        return testCases[id]
    }

    override fun getTestCasesByService(serviceId: String): List<ApiTestCase> {
        return testCases.values.filter { it.serviceId == serviceId }
    }

    override fun deleteTestCase(id: String): Boolean {
        val removed = testCases.remove(id) != null
        if (removed) {
            logger.info("删除测试用例: {}", id)
        } else {
            logger.warn("删除测试用例失败，未找到测试用例: {}", id)
        }
        return removed
    }

    override fun validateParameters(serviceId: String, parameters: Map<String, Any?>): Map<String, String> {
        val service = dataServiceManager.getService(serviceId) ?: return mapOf("service" to "Service not found: $serviceId")

        val errors = mutableMapOf<String, String>()

        // 检查必填参数
        service.parameters.filter { it.required }.forEach { param ->
            if (!parameters.containsKey(param.name) || parameters[param.name] == null) {
                errors[param.name] = "Parameter is required: ${param.name}"
            }
        }

        // 检查参数类型
        parameters.forEach { (name, value) ->
            val param = service.parameters.find { it.name == name }
            if (param != null && value != null) {
                if (!isValidType(value, param)) {
                    errors[name] = "Invalid type for parameter: $name, expected: ${param.type}"
                }
            }
        }

        return errors
    }

    /**
     * 检查参数类型是否有效
     */
    private fun isValidType(value: Any, param: ServiceParameter): Boolean {
        return when (param.type.lowercase()) {
            "string" -> value is String
            "number", "integer" -> value is Number
            "boolean" -> value is Boolean
            "array" -> value is List<*>
            "object" -> value is Map<*, *>
            else -> true // 其他类型不做严格检查
        }
    }
}
