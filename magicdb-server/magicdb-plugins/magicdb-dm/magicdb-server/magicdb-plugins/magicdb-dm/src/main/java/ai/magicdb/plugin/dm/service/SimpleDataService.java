package ai.magicdb.plugin.dm.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单数据服务实现
 * 用于提供基本的数据服务功能
 */
@Service
public class SimpleDataService {

    /**
     * 获取数据服务信息
     */
    public Map<String, Object> getServiceInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "DM Data Service");
        info.put("version", "1.0.0");
        info.put("status", "active");
        return info;
    }

    /**
     * 执行简单查询
     */
    public Map<String, Object> executeQuery(String query) {
        Map<String, Object> result = new HashMap<>();
        result.put("query", query);
        result.put("status", "executed");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
