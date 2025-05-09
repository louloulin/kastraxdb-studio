package ai.magicdb.script.runtime.web

import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.util.*

/**
 * API测试控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/test")
class ApiTestController {
    private val logger = LoggerFactory.getLogger(ApiTestController::class.java)
    private val restTemplate = RestTemplate()

    /**
     * 测试API
     */
    @PostMapping
    fun testApi(@RequestBody request: ApiTestRequest): ResponseEntity<ApiTestResponse> {
        logger.info("测试API: {}", request)
        
        try {
            // 创建请求头
            val headers = HttpHeaders()
            request.headers?.forEach { (key, value) ->
                headers.add(key, value.toString())
            }
            
            // 设置Content-Type
            if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
                headers.contentType = MediaType.APPLICATION_JSON
            }
            
            // 创建请求体
            val body = request.body
            
            // 创建请求实体
            val entity = HttpEntity(body, headers)
            
            // 构建URL（包含查询参数）
            val uriBuilder = UriComponentsBuilder.fromUriString(request.url)
            request.params?.forEach { (key, value) ->
                uriBuilder.queryParam(key, value)
            }
            val uri = URI(uriBuilder.toUriString())
            
            // 记录开始时间
            val startTime = System.currentTimeMillis()
            
            // 发送请求
            val method = HttpMethod.valueOf(request.method)
            val response = restTemplate.exchange(uri, method, entity, String::class.java)
            
            // 计算耗时
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            
            // 构建响应
            val responseHeaders = mutableMapOf<String, String>()
            response.headers.forEach { (key, values) ->
                responseHeaders[key] = values.joinToString(", ")
            }
            
            return ResponseEntity.ok(
                ApiTestResponse(
                    status = response.statusCodeValue,
                    statusText = response.statusCode.reasonPhrase,
                    headers = responseHeaders,
                    data = response.body,
                    time = duration
                )
            )
        } catch (e: Exception) {
            logger.error("测试API出错: {}", e.message, e)
            
            return ResponseEntity.ok(
                ApiTestResponse(
                    status = 500,
                    statusText = "Internal Server Error",
                    headers = emptyMap(),
                    data = mapOf("error" to e.message),
                    time = 0
                )
            )
        }
    }
}

/**
 * API测试请求
 */
data class ApiTestRequest(
    val method: String,
    val url: String,
    val params: Map<String, Any?>? = null,
    val headers: Map<String, Any?>? = null,
    val body: Any? = null
)

/**
 * API测试响应
 */
data class ApiTestResponse(
    val status: Int,
    val statusText: String,
    val headers: Map<String, String>,
    val data: Any?,
    val time: Long
)
