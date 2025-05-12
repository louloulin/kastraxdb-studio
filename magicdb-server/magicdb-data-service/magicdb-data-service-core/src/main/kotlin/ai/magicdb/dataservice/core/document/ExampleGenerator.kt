package ai.magicdb.dataservice.core.document

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.DocumentExample
import ai.magicdb.dataservice.api.model.ServiceResult
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * 示例生成器
 * 用于生成服务调用示例
 *
 * @author magicdb
 */
@Component
class ExampleGenerator(
    private val dataServiceExecutor: DataServiceExecutor,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(ExampleGenerator::class.java)
    
    /**
     * 生成示例
     *
     * @param service 服务
     * @param parameters 参数
     * @return 示例
     */
    fun generateExample(service: DataService, parameters: Map<String, Any?>): DocumentExample {
        try {
            // 执行服务
            val result = dataServiceExecutor.execute(service.id, parameters)
            
            // 创建示例
            return DocumentExample(
                id = UUID.randomUUID().toString(),
                description = "示例调用",
                requestParams = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parameters),
                responseData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result.data)
            )
        } catch (e: Exception) {
            logger.error("生成示例失败: {}", service.id, e)
            
            // 创建错误示例
            return DocumentExample(
                id = UUID.randomUUID().toString(),
                description = "示例调用（失败）",
                requestParams = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parameters),
                responseData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                    ServiceResult(
                        success = false,
                        message = e.message ?: "未知错误"
                    )
                )
            )
        }
    }
    
    /**
     * 生成默认示例
     *
     * @param service 服务
     * @return 示例
     */
    fun generateDefaultExample(service: DataService): DocumentExample {
        // 创建默认参数
        val parameters = mutableMapOf<String, Any?>()
        
        // 根据服务类型生成不同的默认参数
        when (service.type) {
            "SQL" -> {
                parameters["sql"] = "SELECT * FROM users LIMIT 10"
                parameters["dataSourceId"] = "default"
            }
            "HTTP" -> {
                parameters["url"] = "https://api.example.com/data"
                parameters["method"] = "GET"
                parameters["headers"] = mapOf("Content-Type" to "application/json")
            }
            else -> {
                // 默认空参数
            }
        }
        
        return generateExample(service, parameters)
    }
    
    /**
     * 生成多个示例
     *
     * @param service 服务
     * @param count 示例数量
     * @return 示例列表
     */
    fun generateExamples(service: DataService, count: Int): List<DocumentExample> {
        val examples = mutableListOf<DocumentExample>()
        
        // 生成默认示例
        examples.add(generateDefaultExample(service))
        
        // 生成其他示例
        for (i in 1 until count) {
            // 创建不同的参数
            val parameters = mutableMapOf<String, Any?>()
            
            // 根据服务类型生成不同的参数
            when (service.type) {
                "SQL" -> {
                    parameters["sql"] = "SELECT * FROM products WHERE category = 'electronics' LIMIT ${i * 5}"
                    parameters["dataSourceId"] = "default"
                }
                "HTTP" -> {
                    parameters["url"] = "https://api.example.com/data?page=$i"
                    parameters["method"] = "GET"
                    parameters["headers"] = mapOf("Content-Type" to "application/json")
                }
                else -> {
                    // 默认参数
                    parameters["param$i"] = "value$i"
                }
            }
            
            try {
                examples.add(generateExample(service, parameters))
            } catch (e: Exception) {
                logger.error("生成示例失败: {}", service.id, e)
            }
        }
        
        return examples
    }
}
