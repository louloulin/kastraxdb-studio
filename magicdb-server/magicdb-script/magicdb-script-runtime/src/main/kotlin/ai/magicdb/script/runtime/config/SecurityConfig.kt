package ai.magicdb.script.runtime.config

import ai.magicdb.script.api.ApiService
import ai.magicdb.script.api.security.SecurityManager
import ai.magicdb.script.runtime.security.DefaultSecurityManager
import ai.magicdb.script.runtime.security.SecurityInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * 安全配置
 *
 * @author magicdb
 */
@Configuration
class SecurityConfig(private val apiService: ApiService) : WebMvcConfigurer {
    
    @Bean
    fun securityManager(): SecurityManager {
        return DefaultSecurityManager()
    }
    
    @Bean
    fun securityInterceptor(securityManager: SecurityManager): SecurityInterceptor {
        return SecurityInterceptor(apiService, securityManager)
    }
    
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(securityInterceptor(securityManager()))
            .addPathPatterns("/script-api/**")
    }
}
