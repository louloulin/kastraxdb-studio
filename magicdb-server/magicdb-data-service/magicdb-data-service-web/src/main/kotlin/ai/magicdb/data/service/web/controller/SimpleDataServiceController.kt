package ai.magicdb.data.service.web.controller

import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 简单数据服务控制器
 * 用于处理 /api/data-service 路径的请求
 * 
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service")
class SimpleDataServiceController {

    /**
     * 获取数据服务信息
     */
    @GetMapping
    fun getDataServiceInfo(): DataResult<Map<String, Any>> {
        val info = mapOf(
            "status" to "active",
            "version" to "1.0.0",
            "message" to "Data service is working correctly"
        )
        
        return DataResult.of(info)
    }
}
