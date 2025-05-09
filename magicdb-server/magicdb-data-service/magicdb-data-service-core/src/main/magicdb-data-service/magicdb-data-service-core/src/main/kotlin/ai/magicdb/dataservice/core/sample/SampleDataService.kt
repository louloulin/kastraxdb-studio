package ai.magicdb.dataservice.core.sample

import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceParameter
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.*

/**
 * 示例数据服务
 *
 * @author magicdb
 */
@Component
class SampleDataService(private val repository: DataServiceRepository) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // 创建示例分组
        val rootGroup = ServiceGroup(
            id = "root",
            name = "根分组",
            description = "根分组",
            parentId = null,
            createTime = Date(),
            updateTime = Date()
        )
        repository.saveGroup(rootGroup)

        val queryGroup = ServiceGroup(
            id = "query",
            name = "查询服务",
            description = "查询服务分组",
            parentId = "root",
            createTime = Date(),
            updateTime = Date()
        )
        repository.saveGroup(queryGroup)

        val transformGroup = ServiceGroup(
            id = "transform",
            name = "转换服务",
            description = "转换服务分组",
            parentId = "root",
            createTime = Date(),
            updateTime = Date()
        )
        repository.saveGroup(transformGroup)

        // 创建示例服务
        val helloService = DataService(
            id = "hello",
            name = "Hello服务",
            description = "一个简单的Hello World服务",
            type = "query",
            script = """
                function execute(params) {
                    return {
                        message: "Hello, " + (params.name || "World") + "!",
                        timestamp: new Date().toISOString()
                    };
                }
            """.trimIndent(),
            language = "js",
            parameters = listOf(
                ServiceParameter("name", "string", "姓名", "World", false)
            ),
            groupId = "query",
            tags = listOf("示例", "hello"),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )
        repository.saveService(helloService)

        val upperCaseService = DataService(
            id = "uppercase",
            name = "转大写服务",
            description = "将文本转换为大写",
            type = "transform",
            script = """
                function execute(params) {
                    if (!params.text) {
                        return {
                            error: "Missing required parameter: text"
                        };
                    }
                    return {
                        original: params.text,
                        result: params.text.toUpperCase(),
                        timestamp: new Date().toISOString()
                    };
                }
            """.trimIndent(),
            language = "js",
            parameters = listOf(
                ServiceParameter("text", "string", "文本", "", true)
            ),
            groupId = "transform",
            tags = listOf("示例", "转换", "文本"),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )
        repository.saveService(upperCaseService)

        val calculatorService = DataService(
            id = "calculator",
            name = "计算器服务",
            description = "简单的四则运算",
            type = "transform",
            script = """
                function execute(params) {
                    if (!params.a || !params.b || !params.operation) {
                        return {
                            error: "Missing required parameters: a, b, operation"
                        };
                    }
                    
                    const a = parseFloat(params.a);
                    const b = parseFloat(params.b);
                    
                    if (isNaN(a) || isNaN(b)) {
                        return {
                            error: "Invalid numbers"
                        };
                    }
                    
                    let result;
                    switch (params.operation) {
                        case "add":
                            result = a + b;
                            break;
                        case "subtract":
                            result = a - b;
                            break;
                        case "multiply":
                            result = a * b;
                            break;
                        case "divide":
                            if (b === 0) {
                                return {
                                    error: "Division by zero"
                                };
                            }
                            result = a / b;
                            break;
                        default:
                            return {
                                error: "Invalid operation. Supported operations: add, subtract, multiply, divide"
                            };
                    }
                    
                    return {
                        a: a,
                        b: b,
                        operation: params.operation,
                        result: result,
                        timestamp: new Date().toISOString()
                    };
                }
            """.trimIndent(),
            language = "js",
            parameters = listOf(
                ServiceParameter("a", "number", "第一个数", "", true),
                ServiceParameter("b", "number", "第二个数", "", true),
                ServiceParameter("operation", "string", "操作（add, subtract, multiply, divide）", "", true)
            ),
            groupId = "transform",
            tags = listOf("示例", "计算", "数学"),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )
        repository.saveService(calculatorService)
    }
}
