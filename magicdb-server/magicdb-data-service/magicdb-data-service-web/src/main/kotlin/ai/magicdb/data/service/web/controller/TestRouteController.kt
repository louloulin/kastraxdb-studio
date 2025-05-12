package ai.magicdb.data.service.web.controller

import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 测试路由控制器
 * 用于验证路由配置是否正确
 * 
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/test-route")
class TestRouteController {

    /**
     * 测试路由
     */
    @GetMapping
    fun test(): DataResult<Map<String, Any>> {
        val result = mapOf(
            "status" to "success",
            "message" to "Test route is working correctly",
            "timestamp" to System.currentTimeMillis()
        )
        
        return DataResult.of(result)
    }
}
