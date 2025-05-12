package ai.magicdb.data.service.core.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

/**
 * Configuration for data service module
 */
@Configuration
@EnableCaching
@ComponentScan(basePackages = ["ai.magicdb.data.service"])
class DataServiceConfig {
    
    /**
     * Create JdbcTemplate bean
     */
    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }
    
    /**
     * Create ObjectMapper bean if not already defined
     */
    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }
}
