package ai.magicdb.data.service.core.sample

import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceGroup
import ai.magicdb.data.service.api.model.ServiceParameter
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

/**
 * Sample data service for initializing example data
 */
@Component
class SampleDataService(
    private val dataServiceManager: DataServiceManager
) : CommandLineRunner {
    
    override fun run(vararg args: String?) {
        // Check if we already have data
        val existingServices = dataServiceManager.getAllServices()
        if (existingServices.isNotEmpty()) {
            return
        }
        
        // Create sample groups
        val basicGroup = createBasicGroup()
        val advancedGroup = createAdvancedGroup()
        
        // Create sample services
        createHelloWorldService(basicGroup.id)
        createCalculatorService(basicGroup.id)
        createDataQueryService(advancedGroup.id)
    }
    
    private fun createBasicGroup(): ServiceGroup {
        val now = LocalDateTime.now()
        val group = ServiceGroup(
            id = "sample-basic",
            name = "Basic Examples",
            description = "Basic example services",
            parentId = null,
            orderNum = 1,
            gmtCreate = now,
            gmtModified = now,
            createUserId = 1,
            modifiedUserId = 1
        )
        
        return dataServiceManager.createGroup(group)
    }
    
    private fun createAdvancedGroup(): ServiceGroup {
        val now = LocalDateTime.now()
        val group = ServiceGroup(
            id = "sample-advanced",
            name = "Advanced Examples",
            description = "Advanced example services",
            parentId = null,
            orderNum = 2,
            gmtCreate = now,
            gmtModified = now,
            createUserId = 1,
            modifiedUserId = 1
        )
        
        return dataServiceManager.createGroup(group)
    }
    
    private fun createHelloWorldService(groupId: String): DataService {
        val now = LocalDateTime.now()
        val service = DataService(
            id = "sample-hello-world",
            name = "Hello World",
            description = "A simple Hello World service",
            type = "query",
            script = """
                function execute(params) {
                    var name = params.name || "World";
                    return {
                        message: "Hello, " + name + "!",
                        timestamp: new Date().getTime()
                    };
                }
            """.trimIndent(),
            language = "js",
            groupId = groupId,
            gmtCreate = now,
            gmtModified = now,
            enabled = true,
            timeout = 5000,
            cacheTime = 0,
            createUserId = 1,
            modifiedUserId = 1,
            tags = listOf("sample", "hello-world"),
            parameters = listOf(
                ServiceParameter(
                    serviceId = "sample-hello-world",
                    name = "name",
                    type = "string",
                    description = "Name to greet",
                    defaultValue = "World",
                    required = false,
                    orderNum = 1
                )
            )
        )
        
        return dataServiceManager.createService(service)
    }
    
    private fun createCalculatorService(groupId: String): DataService {
        val now = LocalDateTime.now()
        val service = DataService(
            id = "sample-calculator",
            name = "Calculator",
            description = "A simple calculator service",
            type = "query",
            script = """
                function execute(params) {
                    var a = parseFloat(params.a) || 0;
                    var b = parseFloat(params.b) || 0;
                    var operation = params.operation || "add";
                    
                    var result = 0;
                    switch (operation) {
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
                                throw new Error("Cannot divide by zero");
                            }
                            result = a / b;
                            break;
                        default:
                            throw new Error("Unsupported operation: " + operation);
                    }
                    
                    return {
                        a: a,
                        b: b,
                        operation: operation,
                        result: result
                    };
                }
            """.trimIndent(),
            language = "js",
            groupId = groupId,
            gmtCreate = now,
            gmtModified = now,
            enabled = true,
            timeout = 5000,
            cacheTime = 0,
            createUserId = 1,
            modifiedUserId = 1,
            tags = listOf("sample", "calculator"),
            parameters = listOf(
                ServiceParameter(
                    serviceId = "sample-calculator",
                    name = "a",
                    type = "number",
                    description = "First operand",
                    defaultValue = "0",
                    required = true,
                    orderNum = 1
                ),
                ServiceParameter(
                    serviceId = "sample-calculator",
                    name = "b",
                    type = "number",
                    description = "Second operand",
                    defaultValue = "0",
                    required = true,
                    orderNum = 2
                ),
                ServiceParameter(
                    serviceId = "sample-calculator",
                    name = "operation",
                    type = "string",
                    description = "Operation to perform (add, subtract, multiply, divide)",
                    defaultValue = "add",
                    required = true,
                    orderNum = 3
                )
            )
        )
        
        return dataServiceManager.createService(service)
    }
    
    private fun createDataQueryService(groupId: String): DataService {
        val now = LocalDateTime.now()
        val service = DataService(
            id = "sample-data-query",
            name = "Data Query",
            description = "A sample data query service",
            type = "query",
            script = """
                function execute(params) {
                    // This is a sample script that would normally query a database
                    // For demonstration, we'll return mock data
                    
                    var page = params.page || 1;
                    var pageSize = params.pageSize || 10;
                    
                    // Generate mock data
                    var data = [];
                    for (var i = 0; i < pageSize; i++) {
                        var index = (page - 1) * pageSize + i + 1;
                        data.push({
                            id: index,
                            name: "Item " + index,
                            value: Math.round(Math.random() * 1000) / 10,
                            date: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString()
                        });
                    }
                    
                    return {
                        page: page,
                        pageSize: pageSize,
                        totalCount: 100,
                        data: data
                    };
                }
            """.trimIndent(),
            language = "js",
            groupId = groupId,
            gmtCreate = now,
            gmtModified = now,
            enabled = true,
            timeout = 10000,
            cacheTime = 60000, // 1 minute cache
            createUserId = 1,
            modifiedUserId = 1,
            tags = listOf("sample", "data", "query"),
            parameters = listOf(
                ServiceParameter(
                    serviceId = "sample-data-query",
                    name = "page",
                    type = "number",
                    description = "Page number",
                    defaultValue = "1",
                    required = false,
                    orderNum = 1
                ),
                ServiceParameter(
                    serviceId = "sample-data-query",
                    name = "pageSize",
                    type = "number",
                    description = "Page size",
                    defaultValue = "10",
                    required = false,
                    orderNum = 2
                )
            )
        )
        
        return dataServiceManager.createService(service)
    }
}
