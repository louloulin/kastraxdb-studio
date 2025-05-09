package ai.magicdb.script.runtime.web

import ai.magicdb.script.api.ApiService
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.HandlerMapping
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * API请求处理器
 *
 * @author magicdb
 */
@RestController
class ApiRequestHandler(
    private val apiService: ApiService,
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping
) {
    private val logger = LoggerFactory.getLogger(ApiRequestHandler::class.java)
    private val apiPrefix = "/script-api"

    /**
     * 处理API请求
     */
    fun handleRequest(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any?> {
        try {
            // 获取请求路径
            val path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE) as String
            val apiPath = path.substring(apiPrefix.length)
            
            // 获取请求方法
            val method = request.method
            
            // 获取请求参数
            val parameters = mutableMapOf<String, Any?>()
            
            // 添加查询参数
            request.parameterMap.forEach { (key, values) ->
                parameters[key] = if (values.size == 1) values[0] else values
            }
            
            // 添加路径变量
            val pathVariables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE) as? Map<String, String>
            pathVariables?.forEach { (key, value) ->
                parameters[key] = value
            }
            
            // 添加请求头
            val headers = mutableMapOf<String, String>()
            request.headerNames.asSequence().forEach { headerName ->
                headers[headerName] = request.getHeader(headerName)
            }
            parameters["headers"] = headers
            
            // 添加请求体
            if (request.contentType?.startsWith(MediaType.APPLICATION_JSON_VALUE) == true) {
                val requestBody = request.reader.readText()
                if (requestBody.isNotBlank()) {
                    try {
                        // 这里应该使用JSON解析库，但为了简单起见，我们直接添加原始字符串
                        parameters["body"] = requestBody
                    } catch (e: Exception) {
                        logger.warn("解析请求体失败: {}", e.message)
                        parameters["body"] = requestBody
                    }
                }
            }
            
            // 执行API
            val result = apiService.executeApi(apiPath, method, parameters)
            
            // 返回结果
            return ResponseEntity.ok(result)
        } catch (e: Exception) {
            logger.error("处理API请求出错: {}", e.message, e)
            return ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}
