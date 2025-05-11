package ai.magicdb.dataservice.core.sample

import ai.magicdb.dataservice.api.DataServiceManager
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceParameter
import ai.magicdb.dataservice.api.model.ServiceOutput
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.Date
import java.util.UUID

/**
 * 示例数据服务
 * 提供预定义的服务和分组示例
 *
 * @author magicdb
 */
@Component
@ConditionalOnProperty(name = ["magicdb.data-service.sample.enabled"], havingValue = "true", matchIfMissing = false)
class SampleDataService(private val dataServiceManager: DataServiceManager) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(SampleDataService::class.java)

    override fun run(vararg args: String?) {
        try {
            logger.info("初始化示例数据服务...")
            
            // 创建示例分组
            createSampleGroups()
            
            // 创建示例服务
            createSampleServices()
            
            logger.info("示例数据服务初始化完成")
        } catch (e: Exception) {
            logger.error("初始化示例数据服务失败", e)
        }
    }

    /**
     * 创建示例分组
     */
    private fun createSampleGroups() {
        // 创建根分组
        val rootGroup = ServiceGroup(
            id = "sample-root",
            name = "示例服务",
            description = "示例数据服务根分组",
            parentId = null,
            createTime = Date(),
            updateTime = Date()
        )
        dataServiceManager.saveGroup(rootGroup)

        // 创建子分组
        val basicGroup = ServiceGroup(
            id = "sample-basic",
            name = "基础示例",
            description = "基础示例服务分组",
            parentId = "sample-root",
            createTime = Date(),
            updateTime = Date()
        )
        dataServiceManager.saveGroup(basicGroup)

        val advancedGroup = ServiceGroup(
            id = "sample-advanced",
            name = "高级示例",
            description = "高级示例服务分组",
            parentId = "sample-root",
            createTime = Date(),
            updateTime = Date()
        )
        dataServiceManager.saveGroup(advancedGroup)

        val databaseGroup = ServiceGroup(
            id = "sample-database",
            name = "数据库示例",
            description = "数据库操作示例服务分组",
            parentId = "sample-root",
            createTime = Date(),
            updateTime = Date()
        )
        dataServiceManager.saveGroup(databaseGroup)
    }

    /**
     * 创建示例服务
     */
    private fun createSampleServices() {
        // 创建基础示例服务
        createBasicSampleServices()
        
        // 创建高级示例服务
        createAdvancedSampleServices()
        
        // 创建数据库示例服务
        createDatabaseSampleServices()
    }

    /**
     * 创建基础示例服务
     */
    private fun createBasicSampleServices() {
        // 1. Hello World 服务
        val helloWorldService = DataService(
            id = "sample-hello-world",
            name = "Hello World",
            description = "简单的Hello World示例服务",
            type = "query",
            script = """
                // 简单的Hello World示例
                function execute(params) {
                    var name = params.name || "World";
                    return {
                        message: "Hello, " + name + "!",
                        timestamp: new Date().getTime()
                    };
                }
            """.trimIndent(),
            language = "js",
            groupId = "sample-basic",
            parameters = listOf(
                ServiceParameter(
                    name = "name",
                    type = "string",
                    description = "名称",
                    required = false,
                    defaultValue = "World"
                )
            ),
            tags = listOf("sample", "basic", "hello-world"),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )
        dataServiceManager.saveService(helloWorldService)

        // 2. 计算器服务
        val calculatorService = DataService(
            id = "sample-calculator",
            name = "计算器",
            description = "简单的计算器示例服务",
            type = "query",
            script = """
                // 简单的计算器示例
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
                                throw new Error("除数不能为0");
                            }
                            result = a / b;
                            break;
                        default:
                            throw new Error("不支持的操作: " + operation);
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
            groupId = "sample-basic",
            parameters = listOf(
                ServiceParameter(
                    name = "a",
                    type = "number",
                    description = "第一个操作数",
                    required = true
                ),
                ServiceParameter(
                    name = "b",
                    type = "number",
                    description = "第二个操作数",
                    required = true
                ),
                ServiceParameter(
                    name = "operation",
                    type = "string",
                    description = "操作类型（add, subtract, multiply, divide）",
                    required = false,
                    defaultValue = "add"
                )
            ),
            tags = listOf("sample", "basic", "calculator"),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )
        dataServiceManager.saveService(calculatorService)

        // 3. 字符串处理服务
        val stringService = DataService(
            id = "sample-string",
            name = "字符串处理",
            description = "字符串处理示例服务",
            type = "query",
            script = """
                // 字符串处理示例
                function execute(params) {
                    var text = params.text || "";
                    var operation = params.operation || "length";
                    
                    var result = null;
                    switch (operation) {
                        case "length":
                            result = text.length;
                            break;
                        case "uppercase":
                            result = text.toUpperCase();
                            break;
                        case "lowercase":
                            result = text.toLowerCase();
                            break;
                        case "reverse":
                            result = text.split("").reverse().join("");
                            break;
                        default:
                            throw new Error("不支持的操作: " + operation);
                    }
                    
                    return {
                        text: text,
                        operation: operation,
                        result: result
                    };
                }
            """.trimIndent(),
            language = "js",
            groupId = "sample-basic",
            parameters = listOf(
                ServiceParameter(
                    name = "text",
                    type = "string",
                    description = "要处理的文本",
                    required = true
                ),
                ServiceParameter(
                    name = "operation",
                    type = "string",
                    description = "操作类型（length, uppercase, lowercase, reverse）",
                    required = false,
                    defaultValue = "length"
                )
            ),
            tags = listOf("sample", "basic", "string"),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )
        dataServiceManager.saveService(stringService)
    }

    /**
     * 创建高级示例服务
     */
    private fun createAdvancedSampleServices() {
        // 1. 数据转换服务
        val dataTransformService = DataService(
            id = "sample-data-transform",
            name = "数据转换",
            description = "数据转换示例服务",
            type = "transform",
            script = """
                // 数据转换示例
                function execute(params) {
                    var data = params.data || [];
                    var transformType = params.transformType || "map";
                    
                    if (!Array.isArray(data)) {
                        throw new Error("数据必须是数组");
                    }
                    
                    var result = null;
                    switch (transformType) {
                        case "map":
                            // 将每个元素转换为新格式
                            result = data.map(function(item) {
                                return {
                                    id: item.id,
                                    fullName: item.firstName + " " + item.lastName,
                                    age: item.age,
                                    isAdult: item.age >= 18
                                };
                            });
                            break;
                        case "filter":
                            // 过滤出成年人
                            result = data.filter(function(item) {
                                return item.age >= 18;
                            });
                            break;
                        case "reduce":
                            // 计算平均年龄
                            if (data.length === 0) {
                                result = 0;
                            } else {
                                var sum = data.reduce(function(acc, item) {
                                    return acc + item.age;
                                }, 0);
                                result = sum / data.length;
                            }
                            break;
                        default:
                            throw new Error("不支持的转换类型: " + transformType);
                    }
                    
                    return {
                        originalData: data,
                        transformType: transformType,
                        result: result
                    };
                }
            """.trimIndent(),
            language = "js",
            groupId = "sample-advanced",
            parameters = listOf(
                ServiceParameter(
                    name = "data",
                    type = "array",
                    description = "要转换的数据数组",
                    required = true
                ),
                ServiceParameter(
                    name = "transformType",
                    type = "string",
                    description = "转换类型（map, filter, reduce）",
                    required = false,
                    defaultValue = "map"
                )
            ),
            tags = listOf("sample", "advanced", "transform"),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )
        dataServiceManager.saveService(dataTransformService)

        // 2. 异步任务服务
        val asyncTaskService = DataService(
            id = "sample-async-task",
            name = "异步任务",
            description = "异步任务示例服务",
            type = "async",
            script = """
                // 异步任务示例
                function execute(params) {
                    var delay = params.delay || 5000;
                    var taskId = "task-" + new Date().getTime();
                    
                    // 模拟长时间运行的任务
                    setTimeout(function() {
                        console.log("异步任务 " + taskId + " 完成");
                    }, delay);
                    
                    return {
                        taskId: taskId,
                        status: "running",
                        startTime: new Date().getTime(),
                        estimatedCompletionTime: new Date().getTime() + delay
                    };
                }
            """.trimIndent(),
            language = "js",
            groupId = "sample-advanced",
            parameters = listOf(
                ServiceParameter(
                    name = "delay",
                    type = "number",
                    description = "任务延迟时间（毫秒）",
                    required = false,
                    defaultValue = "5000"
                )
            ),
            tags = listOf("sample", "advanced", "async"),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )
        dataServiceManager.saveService(asyncTaskService)

        // 3. 服务编排示例
        val orchestrationService = DataService(
            id = "sample-orchestration",
            name = "服务编排",
            description = "服务编排示例服务",
            type = "orchestration",
            script = """
                // 服务编排示例
                function execute(params) {
                    var name = params.name || "World";
                    
                    // 调用Hello World服务
                    var helloResult = callService("sample-hello-world", { name: name });
                    
                    // 调用字符串处理服务
                    var stringResult = callService("sample-string", { 
                        text: helloResult.message, 
                        operation: "uppercase" 
                    });
                    
                    // 调用计算器服务
                    var calculatorResult = callService("sample-calculator", { 
                        a: name.length, 
                        b: 10, 
                        operation: "multiply" 
                    });
                    
                    return {
                        name: name,
                        helloResult: helloResult,
                        stringResult: stringResult,
                        calculatorResult: calculatorResult,
                        timestamp: new Date().getTime()
                    };
                }
            """.trimIndent(),
            language = "js",
            groupId = "sample-advanced",
            parameters = listOf(
                ServiceParameter(
                    name = "name",
                    type = "string",
                    description = "名称",
                    required = false,
                    defaultValue = "World"
                )
            ),
            tags = listOf("sample", "advanced", "orchestration"),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )
        dataServiceManager.saveService(orchestrationService)
    }

    /**
     * 创建数据库示例服务
     */
    private fun createDatabaseSampleServices() {
        // 1. 查询数据示例
        val queryService = DataService(
            id = "sample-query",
            name = "查询数据",
            description = "数据库查询示例服务",
            type = "query",
            script = """
                // 数据库查询示例
                function execute(params) {
                    var tableName = params.tableName || "users";
                    var limit = params.limit || 10;
                    var offset = params.offset || 0;
                    
                    // 构建SQL查询
                    var sql = "SELECT * FROM " + tableName + " LIMIT " + limit + " OFFSET " + offset;
                    
                    // 执行SQL查询
                    var result = db.query(sql);
                    
                    return {
                        tableName: tableName,
                        limit: limit,
                        offset: offset,
                        sql: sql,
                        data: result.data,
                        total: result.total
                    };
                }
            """.trimIndent(),
            language = "js",
            groupId = "sample-database",
            parameters = listOf(
                ServiceParameter(
                    name = "tableName",
                    type = "string",
                    description = "表名",
                    required = false,
                    defaultValue = "users"
                ),
                ServiceParameter(
                    name = "limit",
                    type = "number",
                    description = "限制数量",
                    required = false,
                    defaultValue = "10"
                ),
                ServiceParameter(
                    name = "offset",
                    type = "number",
                    description = "偏移量",
                    required = false,
                    defaultValue = "0"
                )
            ),
            tags = listOf("sample", "database", "query"),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )
        dataServiceManager.saveService(queryService)

        // 2. 插入数据示例
        val insertService = DataService(
            id = "sample-insert",
            name = "插入数据",
            description = "数据库插入示例服务",
            type = "update",
            script = """
                // 数据库插入示例
                function execute(params) {
                    var tableName = params.tableName || "users";
                    var data = params.data || {};
                    
                    // 构建SQL插入语句
                    var columns = Object.keys(data);
                    var values = columns.map(function(column) {
                        return data[column];
                    });
                    
                    var sql = "INSERT INTO " + tableName + " (" + columns.join(", ") + ") VALUES (";
                    for (var i = 0; i < values.length; i++) {
                        sql += (i > 0 ? ", " : "") + "?";
                    }
                    sql += ")";
                    
                    // 执行SQL插入
                    var result = db.update(sql, values);
                    
                    return {
                        tableName: tableName,
                        data: data,
                        sql: sql,
                        affectedRows: result.affectedRows,
                        generatedKey: result.generatedKey
                    };
                }
            """.trimIndent(),
            language = "js",
            groupId = "sample-database",
            parameters = listOf(
                ServiceParameter(
                    name = "tableName",
                    type = "string",
                    description = "表名",
                    required = false,
                    defaultValue = "users"
                ),
                ServiceParameter(
                    name = "data",
                    type = "object",
                    description = "要插入的数据",
                    required = true
                )
            ),
            tags = listOf("sample", "database", "insert"),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )
        dataServiceManager.saveService(insertService)

        // 3. 事务示例
        val transactionService = DataService(
            id = "sample-transaction",
            name = "事务处理",
            description = "数据库事务示例服务",
            type = "transaction",
            script = """
                // 数据库事务示例
                function execute(params) {
                    var sourceAccount = params.sourceAccount;
                    var targetAccount = params.targetAccount;
                    var amount = params.amount;
                    
                    if (!sourceAccount || !targetAccount || !amount) {
                        throw new Error("源账户、目标账户和金额都不能为空");
                    }
                    
                    if (amount <= 0) {
                        throw new Error("金额必须大于0");
                    }
                    
                    // 开始事务
                    db.beginTransaction();
                    
                    try {
                        // 检查源账户余额
                        var sourceResult = db.query("SELECT balance FROM accounts WHERE account_id = ?", [sourceAccount]);
                        if (sourceResult.data.length === 0) {
                            throw new Error("源账户不存在");
                        }
                        
                        var sourceBalance = sourceResult.data[0].balance;
                        if (sourceBalance < amount) {
                            throw new Error("源账户余额不足");
                        }
                        
                        // 检查目标账户
                        var targetResult = db.query("SELECT balance FROM accounts WHERE account_id = ?", [targetAccount]);
                        if (targetResult.data.length === 0) {
                            throw new Error("目标账户不存在");
                        }
                        
                        // 更新源账户余额
                        db.update("UPDATE accounts SET balance = balance - ? WHERE account_id = ?", [amount, sourceAccount]);
                        
                        // 更新目标账户余额
                        db.update("UPDATE accounts SET balance = balance + ? WHERE account_id = ?", [amount, targetAccount]);
                        
                        // 记录交易
                        db.update(
                            "INSERT INTO transactions (source_account, target_account, amount, transaction_time) VALUES (?, ?, ?, NOW())",
                            [sourceAccount, targetAccount, amount]
                        );
                        
                        // 提交事务
                        db.commit();
                        
                        return {
                            success: true,
                            sourceAccount: sourceAccount,
                            targetAccount: targetAccount,
                            amount: amount,
                            transactionTime: new Date().getTime()
                        };
                    } catch (e) {
                        // 回滚事务
                        db.rollback();
                        throw e;
                    }
                }
            """.trimIndent(),
            language = "js",
            groupId = "sample-database",
            parameters = listOf(
                ServiceParameter(
                    name = "sourceAccount",
                    type = "string",
                    description = "源账户",
                    required = true
                ),
                ServiceParameter(
                    name = "targetAccount",
                    type = "string",
                    description = "目标账户",
                    required = true
                ),
                ServiceParameter(
                    name = "amount",
                    type = "number",
                    description = "转账金额",
                    required = true
                )
            ),
            tags = listOf("sample", "database", "transaction"),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )
        dataServiceManager.saveService(transactionService)
    }
}
