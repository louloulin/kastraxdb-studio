package ai.magicdb.dataservice.web.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Web MVC 配置
 */
@Configuration
class WebMvcConfig : WebMvcConfigurer {

    /**
     * 配置静态资源路径
     */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/static/data-service/**")
            .addResourceLocations("classpath:/static/data-service/")
    }
}
