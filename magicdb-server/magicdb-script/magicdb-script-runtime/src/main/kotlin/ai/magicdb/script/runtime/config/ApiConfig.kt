package ai.magicdb.script.runtime.config

import ai.magicdb.script.api.ApiRepository
import ai.magicdb.script.api.ApiService
import ai.magicdb.script.api.ScriptExecutor
import ai.magicdb.script.engine.GraalVMScriptExecutor
import ai.magicdb.script.runtime.repository.MemoryApiRepository
import ai.magicdb.script.runtime.service.DefaultApiService
import ai.magicdb.script.runtime.web.ApiRequestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

/**
 * API配置
 *
 * @author magicdb
 */
@Configuration
class ApiConfig : WebMvcConfigurer {
    
    @Bean
    fun apiRepository(): ApiRepository {
        return MemoryApiRepository()
    }
    
    @Bean
    fun scriptExecutor(): ScriptExecutor {
        return GraalVMScriptExecutor()
    }
    
    @Bean
    fun apiService(apiRepository: ApiRepository, scriptExecutor: ScriptExecutor): ApiService {
        return DefaultApiService(apiRepository, scriptExecutor)
    }
    
    @Bean
    fun apiRequestHandler(apiService: ApiService, requestMappingHandlerMapping: RequestMappingHandlerMapping): ApiRequestHandler {
        return ApiRequestHandler(apiService, requestMappingHandlerMapping)
    }
}
