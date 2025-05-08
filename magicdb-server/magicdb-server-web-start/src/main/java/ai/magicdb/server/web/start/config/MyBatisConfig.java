package ai.magicdb.server.web.start.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis configuration
 *
 * Note: We're using Spring Boot's auto-configuration for MyBatis
 * instead of manually configuring it to avoid compatibility issues.
 */
@Configuration
@EnableTransactionManagement
@MapperScan("ai.magicdb.server.domain.repository.mapper")
public class MyBatisConfig {
    // Spring Boot will auto-configure MyBatis
}
