package ai.magicdb.dataservice.core.config

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceManager
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.DataSourceService
import ai.magicdb.dataservice.api.DataTransformer
import ai.magicdb.dataservice.api.DocumentGenerator
import ai.magicdb.dataservice.api.AsyncTaskExecutor
import ai.magicdb.dataservice.api.MonitoringRepository
import ai.magicdb.dataservice.api.MonitoringService
import ai.magicdb.dataservice.api.ScriptDebugger
import ai.magicdb.dataservice.api.ServiceFlowExecutor
import ai.magicdb.dataservice.api.ServiceFlowRepository
import ai.magicdb.dataservice.api.ServiceOrchestrator
import ai.magicdb.dataservice.api.ServiceTestManager
import ai.magicdb.dataservice.api.ServiceTestRepository
import ai.magicdb.dataservice.api.TransformationRepository
import ai.magicdb.dataservice.core.cache.DataServiceCacheManager
import ai.magicdb.dataservice.core.converter.DataServiceConverter
import ai.magicdb.dataservice.core.converter.ServiceGroupConverter
import ai.magicdb.dataservice.core.datasource.SimpleDataSourceService
import ai.magicdb.dataservice.core.async.DefaultAsyncTaskExecutor
import ai.magicdb.dataservice.core.debug.DefaultScriptDebugger
import ai.magicdb.dataservice.core.debug.DefaultScriptDebuggerAdapter
import ai.magicdb.dataservice.core.document.DefaultDocumentGenerator
import ai.magicdb.dataservice.core.executor.DefaultDataServiceExecutor
import ai.magicdb.dataservice.core.manager.DefaultDataServiceManager
import ai.magicdb.dataservice.core.manager.DefaultServiceTestManager
import ai.magicdb.dataservice.core.mapper.*
import ai.magicdb.dataservice.core.orchestration.DefaultServiceFlowExecutor
import ai.magicdb.dataservice.core.orchestration.DefaultServiceOrchestrator
import ai.magicdb.dataservice.core.monitoring.DefaultMonitoringService
import ai.magicdb.dataservice.core.converter.MonitoringConverter
import ai.magicdb.dataservice.core.repository.MemoryDataServiceRepository
import ai.magicdb.dataservice.core.repository.MemoryMonitoringRepository
import ai.magicdb.dataservice.core.repository.MemoryServiceFlowRepository
import ai.magicdb.dataservice.core.repository.MemoryTransformationRepository
import ai.magicdb.dataservice.core.repository.MybatisDataServiceRepository
import ai.magicdb.dataservice.core.repository.MybatisMonitoringRepository
import ai.magicdb.dataservice.core.repository.MybatisServiceTestRepository
import ai.magicdb.dataservice.core.transform.DefaultDataTransformer
import ai.magicdb.script.api.ScriptExecutor as GraalScriptExecutor
import ai.magicdb.script.engine.GraalVMScriptExecutor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
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
    @ConditionalOnMissingBean(name = ["graalScriptExecutor"])
    fun graalScriptExecutor(): GraalScriptExecutor {
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
        scriptExecutor: GraalScriptExecutor,
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
    fun serviceTestRepository(
        serviceTestMapper: ServiceTestMapper,
        serviceTestResultMapper: ServiceTestResultMapper,
        objectMapper: ObjectMapper
    ): ServiceTestRepository {
        return MybatisServiceTestRepository(serviceTestMapper, serviceTestResultMapper, objectMapper)
    }

    @Bean
    @ConditionalOnMissingBean
    fun serviceTestManager(
        testRepository: ServiceTestRepository,
        serviceRepository: DataServiceRepository,
        serviceExecutor: DataServiceExecutor
    ): ServiceTestManager {
        return DefaultServiceTestManager(testRepository, serviceRepository, serviceExecutor)
    }

    @Bean
    @ConditionalOnMissingBean
    fun documentGenerator(
        serviceRepository: DataServiceRepository,
        serviceDocumentMapper: ServiceDocumentMapper,
        documentTemplateMapper: DocumentTemplateMapper,
        objectMapper: ObjectMapper
    ): DocumentGenerator {
        return DefaultDocumentGenerator(serviceRepository, serviceDocumentMapper, documentTemplateMapper, objectMapper)
    }

    @Bean
    @ConditionalOnMissingBean
    fun scriptDebugger(
        scriptExecutor: GraalScriptExecutor,
        dataSourceService: DataSourceService
    ): ScriptDebugger {
        // 创建原始调试器
        val originalDebugger = DefaultScriptDebugger(
            ai.magicdb.dataservice.core.script.DefaultScriptExecutor(scriptExecutor),
            dataSourceService
        )

        // 创建适配器
        val adapter = DefaultScriptDebuggerAdapter()

        return adapter
    }

    @Bean
    @ConditionalOnMissingBean
    fun asyncTaskExecutor(
        scriptExecutor: GraalScriptExecutor
    ): AsyncTaskExecutor {
        return DefaultAsyncTaskExecutor(
            ai.magicdb.dataservice.core.script.DefaultScriptExecutor(scriptExecutor)
        )
    }

    @Bean
    @ConditionalOnMissingBean
    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        return objectMapper
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

    @Bean
    @ConditionalOnMissingBean
    fun serviceFlowRepository(): ServiceFlowRepository {
        return MemoryServiceFlowRepository()
    }

    @Bean
    @ConditionalOnMissingBean
    fun serviceFlowExecutor(
        serviceExecutor: DataServiceExecutor,
        scriptExecutor: GraalScriptExecutor
    ): ServiceFlowExecutor {
        return DefaultServiceFlowExecutor(serviceExecutor, scriptExecutor)
    }

    @Bean
    @ConditionalOnMissingBean
    fun serviceOrchestrator(
        flowRepository: ServiceFlowRepository,
        flowExecutor: ServiceFlowExecutor,
        serviceRepository: DataServiceRepository,
        serviceExecutor: DataServiceExecutor,
        objectMapper: ObjectMapper
    ): ServiceOrchestrator {
        return DefaultServiceOrchestrator(flowRepository, flowExecutor, serviceRepository, serviceExecutor, objectMapper)
    }

    @Bean
    @ConditionalOnMissingBean
    fun transformationRepository(): TransformationRepository {
        return MemoryTransformationRepository()
    }

    @Bean
    @ConditionalOnMissingBean
    fun dataTransformer(
        scriptExecutor: GraalScriptExecutor,
        objectMapper: ObjectMapper
    ): DataTransformer {
        return DefaultDataTransformer(
            ai.magicdb.dataservice.core.script.DefaultScriptExecutor(scriptExecutor),
            objectMapper
        )
    }

    /**
     * 内存监控存储库
     * 仅在开发和测试环境使用
     */
    @Bean
    @ConditionalOnProperty(name = ["magicdb.data-service.monitoring.repository"], havingValue = "memory", matchIfMissing = false)
    fun memoryMonitoringRepository(): MonitoringRepository {
        return MemoryMonitoringRepository()
    }

    /**
     * MyBatis监控存储库
     * 默认使用
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(MonitoringRepository::class)
    fun mybatisMonitoringRepository(
        callRecordMapper: ServiceCallRecordMapper,
        callStatsMapper: ServiceCallStatsMapper,
        performanceMapper: ServicePerformanceMapper,
        monitoringConverter: MonitoringConverter
    ): MonitoringRepository {
        return MybatisMonitoringRepository(
            callRecordMapper,
            callStatsMapper,
            performanceMapper,
            monitoringConverter
        )
    }

    @Bean
    @ConditionalOnMissingBean
    fun monitoringService(
        monitoringRepository: MonitoringRepository,
        dataServiceRepository: DataServiceRepository
    ): MonitoringService {
        return DefaultMonitoringService(monitoringRepository, dataServiceRepository)
    }
}
