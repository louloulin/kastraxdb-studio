package ai.magicdb.dataservice.web.controller;

import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单数据服务控制器
 * 用于处理 /api/data-service 路径的请求
 * 
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service")
public class SimpleDataServiceController {

    /**
     * 获取数据服务信息
     */
    @GetMapping
    public DataResult<Map<String, Object>> getDataServiceInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("status", "active");
        info.put("version", "1.0.0");
        info.put("message", "Data service is working correctly");
        
        return DataResult.of(info);
    }
}
