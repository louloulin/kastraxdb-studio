package ai.magicdb.dataservice.core.config

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceManager
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.DataSourceService
import ai.magicdb.dataservice.core.cache.DataServiceCacheManager
import ai.magicdb.dataservice.core.converter.DataServiceConverter
import ai.magicdb.dataservice.core.converter.ServiceGroupConverter
import ai.magicdb.dataservice.core.datasource.SimpleDataSourceService
import ai.magicdb.dataservice.core.executor.DefaultDataServiceExecutor
import ai.magicdb.dataservice.core.manager.DefaultDataServiceManager
import ai.magicdb.dataservice.core.mapper.*
import ai.magicdb.dataservice.core.repository.MemoryDataServiceRepository
import ai.magicdb.dataservice.core.repository.MybatisDataServiceRepository
import ai.magicdb.script.api.ScriptExecutor
import ai.magicdb.script.engine.GraalVMScriptExecutor
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * 数据服务配置
 *
 * @author magicdb
 */
@Configuration
class DataServiceConfig {

    /**
     * 内存数据服务存储库
     * 仅在开发和测试环境使用
     */
    @Bean
    @ConditionalOnProperty(name = ["magicdb.data-service.repository"], havingValue = "memory", matchIfMissing = false)
    fun memoryDataServiceRepository(): DataServiceRepository {
        return MemoryDataServiceRepository()
    }

    /**
     * MyBatis数据服务存储库
     * 默认使用
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(DataServiceRepository::class)
    fun mybatisDataServiceRepository(
        dataServiceMapper: DataServiceMapper,
        serviceGroupMapper: ServiceGroupMapper,
        serviceParameterMapper: ServiceParameterMapper,
        serviceHistoryMapper: ServiceHistoryMapper,
        serviceTagMapper: ServiceTagMapper,
        dataServiceConverter: DataServiceConverter,
        serviceGroupConverter: ServiceGroupConverter
    ): DataServiceRepository {
        return MybatisDataServiceRepository(
            dataServiceMapper,
            serviceGroupMapper,
            serviceParameterMapper,
            serviceHistoryMapper,
            serviceTagMapper,
            dataServiceConverter,
            serviceGroupConverter
        )
    }

    @Bean
    @ConditionalOnMissingBean
    fun scriptExecutor(): ScriptExecutor {
        return GraalVMScriptExecutor()
    }

    @Bean
    @ConditionalOnMissingBean
    fun dataServiceCacheManager(): DataServiceCacheManager {
        return DataServiceCacheManager()
    }

    @Bean
    @ConditionalOnMissingBean
    fun dataServiceExecutor(
        repository: DataServiceRepository,
        scriptExecutor: ScriptExecutor,
        cacheManager: DataServiceCacheManager,
        dataSourceService: DataSourceService
    ): DataServiceExecutor {
        return DefaultDataServiceExecutor(repository, scriptExecutor, cacheManager, dataSourceService)
    }

    @Bean
    @ConditionalOnMissingBean
    fun dataSourceService(
        domainDataSourceService: ai.magicdb.server.domain.api.service.DataSourceService
    ): DataSourceService {
        return SimpleDataSourceService(domainDataSourceService)
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
