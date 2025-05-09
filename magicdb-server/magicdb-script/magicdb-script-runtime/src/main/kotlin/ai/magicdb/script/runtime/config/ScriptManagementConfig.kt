package ai.magicdb.script.runtime.config

import ai.magicdb.script.api.*
import ai.magicdb.script.engine.GraalVMScriptExecutor
import ai.magicdb.script.runtime.metadata.DefaultScriptMetadataManager
import ai.magicdb.script.runtime.repository.MemoryScriptRepository
import ai.magicdb.script.runtime.service.DefaultScriptManager
import ai.magicdb.script.runtime.version.DefaultScriptVersionControl
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 脚本管理配置
 *
 * @author magicdb
 */
@Configuration
class ScriptManagementConfig {
    
    @Bean
    @ConditionalOnMissingBean
    fun scriptRepository(): ScriptRepository {
        return MemoryScriptRepository()
    }
    
    @Bean
    @ConditionalOnMissingBean
    fun scriptVersionControl(scriptRepository: ScriptRepository): ScriptVersionControl {
        return DefaultScriptVersionControl(scriptRepository)
    }
    
    @Bean
    @ConditionalOnMissingBean
    fun scriptMetadataManager(scriptRepository: ScriptRepository): ScriptMetadataManager {
        return DefaultScriptMetadataManager(scriptRepository)
    }
    
    @Bean
    @ConditionalOnMissingBean
    fun scriptExecutor(): ScriptExecutor {
        return GraalVMScriptExecutor()
    }
    
    @Bean
    @ConditionalOnMissingBean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }
    
    @Bean
    @ConditionalOnMissingBean
    fun scriptManager(
        scriptRepository: ScriptRepository,
        scriptVersionControl: ScriptVersionControl,
        scriptMetadataManager: ScriptMetadataManager,
        scriptExecutor: ScriptExecutor,
        objectMapper: ObjectMapper
    ): ScriptManager {
        return DefaultScriptManager(
            scriptRepository,
            scriptVersionControl,
            scriptMetadataManager,
            scriptExecutor,
            objectMapper
        )
    }
}
