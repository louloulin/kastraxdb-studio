package ai.magicdb.data.service.web.controller

import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 测试控制器
 * 用于验证组件扫描配置是否正确
 */
@RestController
@RequestMapping("/api/data-service/test")
class TestController {

    /**
     * 测试接口
     * 返回一个简单的成功消息
     */
    @GetMapping
    fun test(): DataResult<Map<String, String>> {
        val result = mapOf(
            "status" to "success",
            "message" to "Data service API is working correctly"
        )
        return DataResult.of(result)
    }
}
