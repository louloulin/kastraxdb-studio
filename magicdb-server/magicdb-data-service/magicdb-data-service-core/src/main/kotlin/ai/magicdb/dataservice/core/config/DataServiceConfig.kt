package ai.magicdb.dataservice.core.config

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceManager
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.core.executor.DefaultDataServiceExecutor
import ai.magicdb.dataservice.core.manager.DefaultDataServiceManager
import ai.magicdb.dataservice.core.repository.MemoryDataServiceRepository
import ai.magicdb.script.api.ScriptExecutor
import ai.magicdb.script.engine.GraalVMScriptExecutor
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 数据服务配置
 *
 * @author magicdb
 */
@Configuration
@EnableCaching
class DataServiceConfig {
    
    @Bean
    @ConditionalOnMissingBean
    fun dataServiceRepository(): DataServiceRepository {
        return MemoryDataServiceRepository()
    }
    
    @Bean
    @ConditionalOnMissingBean
    fun scriptExecutor(): ScriptExecutor {
        return GraalVMScriptExecutor()
    }
    
    @Bean
    @ConditionalOnMissingBean
    fun cacheManager(): CacheManager {
        return ConcurrentMapCacheManager("dataService")
    }
    
    @Bean
    @ConditionalOnMissingBean
    fun dataServiceExecutor(
        repository: DataServiceRepository,
        scriptExecutor: ScriptExecutor,
        cacheManager: CacheManager
    ): DataServiceExecutor {
        return DefaultDataServiceExecutor(repository, scriptExecutor, cacheManager)
    }
    
    @Bean
    @ConditionalOnMissingBean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }
    
    @Bean
    @ConditionalOnMissingBean
    fun dataServiceManager(
        repository: DataServiceRepository,
        executor: DataServiceExecutor,
        objectMapper: ObjectMapper
    ): DataServiceManager {
        return DefaultDataServiceManager(repository, executor, objectMapper)
    }
}
