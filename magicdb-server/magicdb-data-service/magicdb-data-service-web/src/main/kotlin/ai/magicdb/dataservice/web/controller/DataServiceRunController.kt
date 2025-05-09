package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.DataServiceManager
import ai.magicdb.dataservice.api.model.ServiceResult
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

/**
 * 数据服务运行控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/run")
class DataServiceRunController(private val dataServiceManager: DataServiceManager) {
    private val logger = LoggerFactory.getLogger(DataServiceRunController::class.java)

    /**
     * 运行数据服务
     */
    @RequestMapping("/{id}/**")
    fun runService(
        @PathVariable id: String,
        request: HttpServletRequest,
        @RequestParam allParams: Map<String, String>,
        @RequestBody(required = false) body: String?
    ): ResponseEntity<Any> {
        try {
            // 获取服务
            val service = dataServiceManager.getService(id)
                ?: return ResponseEntity.notFound().build()
            
            // 检查服务是否启用
            if (!service.enabled) {
                return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "服务已禁用: $id"
                ))
            }
            
            // 准备参数
            val parameters = mutableMapOf<String, Any?>()
            
            // 添加查询参数
            parameters.putAll(allParams)
            
            // 添加请求体
            if (body != null && body.isNotBlank()) {
                parameters["body"] = body
            }
            
            // 添加请求头
            val headers = mutableMapOf<String, String>()
            val headerNames = request.headerNames
            while (headerNames.hasMoreElements()) {
                val headerName = headerNames.nextElement()
                headers[headerName] = request.getHeader(headerName)
            }
            parameters["headers"] = headers
            
            // 添加请求信息
            parameters["method"] = request.method
            parameters["path"] = request.requestURI
            
            // 执行服务
            val result = dataServiceManager.executeService(id, parameters)
            
            // 处理结果
            return if (result.success) {
                ResponseEntity.ok(result.data)
            } else {
                ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to result.message
                ))
            }
        } catch (e: Exception) {
            logger.error("运行服务出错: {}", e.message, e)
            return ResponseEntity.badRequest().body(mapOf(
                "success" to false,
                "message" to "运行服务出错: ${e.message}"
            ))
        }
    }

    /**
     * 测试运行数据服务
     */
    @PostMapping("/test/{id}")
    fun testService(
        @PathVariable id: String,
        @RequestBody parameters: Map<String, Any?>
    ): ResponseEntity<ServiceResult> {
        return ResponseEntity.ok(dataServiceManager.executeService(id, parameters))
    }
}
