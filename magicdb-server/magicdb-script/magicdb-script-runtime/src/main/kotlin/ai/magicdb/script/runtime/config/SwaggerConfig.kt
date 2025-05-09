package ai.magicdb.script.runtime.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Swagger配置
 *
 * @author magicdb
 */
@Configuration
class SwaggerConfig {
    
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("MagicDB API")
                    .description("MagicDB脚本和数据服务API文档")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("MagicDB Team")
                            .email("support@magicdb.ai")
                            .url("https://magicdb.ai")
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                    )
            )
    }
}
