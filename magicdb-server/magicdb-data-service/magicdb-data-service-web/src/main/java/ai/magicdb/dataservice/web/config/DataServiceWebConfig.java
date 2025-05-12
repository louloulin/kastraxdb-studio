package ai.magicdb.dataservice.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 数据服务 Web 配置类
 * 确保控制器被正确扫描
 * 
 * @author magicdb
 */
@Configuration
@ComponentScan(basePackages = {
    "ai.magicdb.dataservice.web.controller"
})
public class DataServiceWebConfig {
    // 配置类，用于确保控制器被正确扫描
}
