package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.ServiceRepository
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ConcurrentHashMap

/**
 * API测试控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/test")
class ApiTestController(
    private val serviceRepository: ServiceRepository,
    private val dataServiceExecutor: DataServiceExecutor
) {
    private val logger = LoggerFactory.getLogger(ApiTestController::class.java)
    private val restTemplate = RestTemplate()
    private val testHistory = ConcurrentHashMap<String, List<ApiTestRequest>>()
    
    /**
     * 测试请求
     */
    data class ApiTestRequest(
        /**
         * 服务ID
         */
        val serviceId: String? = null,
        
        /**
         * 服务路径
         */
        val path: String? = null,
        
        /**
         * 请求方法
         */
        val method: String = "GET",
        
        /**
         * 请求参数
         */
        val parameters: Map<String, Any?> = emptyMap(),
        
        /**
         * 请求头
         */
        val headers: Map<String, String> = emptyMap(),
        
        /**
         * 请求体
         */
        val body: Any? = null,
        
        /**
         * 请求URL
         */
        val url: String? = null,
        
        /**
         * 测试名称
         */
        val name: String? = null,
        
        /**
         * 测试描述
         */
        val description: String? = null,
        
        /**
         * 创建时间
         */
        val createTime: Long = System.currentTimeMillis()
    )
    
    /**
     * 测试结果
     */
    data class ApiTestResult(
        /**
         * 状态码
         */
        val statusCode: Int,
        
        /**
         * 响应头
         */
        val headers: Map<String, String>,
        
        /**
         * 响应体
         */
        val body: Any?,
        
        /**
         * 执行时间（毫秒）
         */
        val duration: Long,
        
        /**
         * 错误信息
         */
        val error: String? = null
    )
    
    /**
     * 测试服务
     *
     * @param request 测试请求
     * @return 测试结果
     */
    @PostMapping
    fun testService(@RequestBody request: ApiTestRequest): DataResult<ApiTestResult> {
        try {
            // 记录测试历史
            val userId = "1" // 从当前用户上下文获取
            val userHistory = testHistory.getOrDefault(userId, emptyList())
            testHistory[userId] = userHistory + request
            
            // 执行测试
            val startTime = System.currentTimeMillis()
            val result = if (request.serviceId != null) {
                // 通过服务ID执行
                executeServiceById(request)
            } else if (request.path != null) {
                // 通过服务路径执行
                executeServiceByPath(request)
            } else if (request.url != null) {
                // 通过URL执行
                executeServiceByUrl(request)
            } else {
                throw IllegalArgumentException("必须提供serviceId、path或url")
            }
            val duration = System.currentTimeMillis() - startTime
            
            // 构建测试结果
            val testResult = ApiTestResult(
                statusCode = result.statusCode.value(),
                headers = result.headers.entries.associate { it.key to it.value.joinToString(", ") },
                body = result.body,
                duration = duration
            )
            
            return DataResult.of(testResult)
        } catch (e: Exception) {
            logger.error("测试服务失败: {}", e.message, e)
            return DataResult.error("test.error", "测试服务失败: ${e.message}")
        }
    }
    
    /**
     * 获取测试历史
     *
     * @return 测试历史
     */
    @GetMapping("/history")
    fun getTestHistory(): DataResult<List<ApiTestRequest>> {
        val userId = "1" // 从当前用户上下文获取
        val userHistory = testHistory.getOrDefault(userId, emptyList())
        return DataResult.of(userHistory)
    }
    
    /**
     * 清除测试历史
     *
     * @return 是否成功
     */
    @DeleteMapping("/history")
    fun clearTestHistory(): DataResult<Boolean> {
        val userId = "1" // 从当前用户上下文获取
        testHistory.remove(userId)
        return DataResult.of(true)
    }
    
    /**
     * 通过服务ID执行
     */
    private fun executeServiceById(request: ApiTestRequest): ResponseEntity<Any> {
        // 获取服务
        val service = serviceRepository.getService(request.serviceId!!)
            ?: throw IllegalArgumentException("服务不存在: ${request.serviceId}")
        
        // 执行服务
        val result = dataServiceExecutor.execute(
            serviceId = service.id,
            parameters = request.parameters,
            options = mapOf(
                "headers" to request.headers
            )
        )
        
        // 构建响应
        val headers = HttpHeaders()
        headers.add("Content-Type", "application/json")
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(result)
    }
    
    /**
     * 通过服务路径执行
     */
    private fun executeServiceByPath(request: ApiTestRequest): ResponseEntity<Any> {
        // 获取服务
        val service = serviceRepository.getServiceByPath(request.path!!, request.method)
            ?: throw IllegalArgumentException("服务不存在: ${request.path}")
        
        // 执行服务
        val result = dataServiceExecutor.execute(
            serviceId = service.id,
            parameters = request.parameters,
            options = mapOf(
                "headers" to request.headers
            )
        )
        
        // 构建响应
        val headers = HttpHeaders()
        headers.add("Content-Type", "application/json")
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(result)
    }
    
    /**
     * 通过URL执行
     */
    private fun executeServiceByUrl(request: ApiTestRequest): ResponseEntity<Any> {
        // 构建请求头
        val headers = HttpHeaders()
        request.headers.forEach { (key, value) ->
            headers.add(key, value)
        }
        
        // 构建请求体
        val httpEntity = HttpEntity<Any>(request.body, headers)
        
        // 执行请求
        return restTemplate.exchange(
            request.url!!,
            HttpMethod.valueOf(request.method),
            httpEntity,
            Any::class.java
        )
    }
}
