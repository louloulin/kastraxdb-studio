package ai.magicdb.server.web.start.controller;

import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试路由控制器
 * 用于验证路由配置是否正确
 * 
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/test-route")
public class TestRouteController {

    /**
     * 测试路由
     */
    @GetMapping
    public DataResult<Map<String, Object>> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Test route is working correctly");
        result.put("timestamp", System.currentTimeMillis());
        
        return DataResult.of(result);
    }
}
