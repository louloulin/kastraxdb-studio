package ai.magicdb.plugin.dm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 数据服务配置类
 * 确保控制器被正确扫描
 */
@Configuration
@ComponentScan(basePackages = {
    "ai.magicdb.plugin.dm.controller"
})
public class DataServiceConfig {
    // 配置类，用于确保控制器被正确扫描
}
