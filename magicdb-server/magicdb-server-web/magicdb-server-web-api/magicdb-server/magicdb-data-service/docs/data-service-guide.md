# MagicDB 数据服务使用指南

## 1. 概述

MagicDB 数据服务是一个强大的低代码开发平台，允许用户通过编写脚本快速创建和管理 API 接口，无需编写传统的 Controller、Service、DAO 等组件。本指南将帮助您了解数据服务的核心概念、架构和使用方法。

### 1.1 核心特性

- **低代码开发**：通过编写脚本快速创建 API 接口
- **多语言支持**：支持 JavaScript、Python、Kotlin 等多种脚本语言
- **数据源集成**：支持多种数据库和数据源
- **服务编排**：支持服务之间的调用和编排
- **缓存机制**：支持可配置的缓存策略
- **事务支持**：支持脚本级别的事务控制
- **异步执行**：支持长时间运行任务的异步执行
- **监控和统计**：提供服务调用统计和性能监控
- **文档生成**：自动生成 API 文档

## 2. 架构

### 2.1 核心组件

MagicDB 数据服务由以下核心组件组成：

- **DataServiceManager**：数据服务管理接口，负责服务的 CRUD 操作
- **DataServiceRepository**：数据服务存储接口，负责服务的持久化
- **DataServiceExecutor**：数据服务执行接口，负责执行脚本
- **DocumentGenerator**：文档生成器，负责生成 API 文档
- **MonitoringService**：监控服务，负责服务调用统计和性能监控

### 2.2 数据模型

- **DataService**：数据服务定义，包含服务的基本信息、脚本内容、参数定义等
- **ServiceGroup**：服务分组，用于组织服务
- **ServiceParameter**：服务参数定义
- **ServiceResult**：服务执行结果
- **ServiceDocument**：服务文档

## 3. 快速开始

### 3.1 创建服务

#### 3.1.1 通过 API 创建服务

```http
POST /api/data-service/service
Content-Type: application/json

{
  "name": "Hello World",
  "description": "简单的 Hello World 服务",
  "type": "query",
  "script": "function execute(params) { return { message: 'Hello, ' + (params.name || 'World') + '!' }; }",
  "language": "js",
  "parameters": [
    {
      "name": "name",
      "type": "string",
      "description": "名称",
      "required": false,
      "defaultValue": "World"
    }
  ],
  "groupId": "sample-basic",
  "tags": ["sample", "hello-world"]
}
```

#### 3.1.2 通过 Web 界面创建服务

1. 登录 MagicDB 管理控制台
2. 导航到"数据服务" > "服务管理"
3. 点击"新建服务"按钮
4. 填写服务信息和脚本内容
5. 点击"保存"按钮

### 3.2 执行服务

#### 3.2.1 通过 API 执行服务

```http
POST /api/data-service/run/{serviceId}
Content-Type: application/json

{
  "name": "MagicDB"
}
```

#### 3.2.2 通过 Web 界面执行服务

1. 登录 MagicDB 管理控制台
2. 导航到"数据服务" > "服务测试"
3. 选择要测试的服务
4. 填写参数值
5. 点击"执行"按钮

## 4. 脚本编写指南

### 4.1 JavaScript 脚本

JavaScript 是数据服务默认支持的脚本语言，下面是一个简单的 JavaScript 脚本示例：

```javascript
/**
 * 执行函数
 * @param {Object} params - 请求参数
 * @returns {Object} - 返回结果
 */
function execute(params) {
    // 获取参数
    var name = params.name || "World";
    
    // 业务逻辑
    var message = "Hello, " + name + "!";
    var timestamp = new Date().getTime();
    
    // 返回结果
    return {
        message: message,
        timestamp: timestamp
    };
}
```

### 4.2 数据库操作

数据服务提供了内置的数据库操作 API，可以在脚本中使用：

```javascript
function execute(params) {
    // 查询数据
    var result = db.query("SELECT * FROM users WHERE id = ?", [params.id]);
    
    // 更新数据
    var updateResult = db.update("UPDATE users SET name = ? WHERE id = ?", [params.name, params.id]);
    
    // 事务操作
    db.beginTransaction();
    try {
        db.update("INSERT INTO orders (user_id, product_id, quantity) VALUES (?, ?, ?)", 
            [params.userId, params.productId, params.quantity]);
        db.update("UPDATE products SET stock = stock - ? WHERE id = ?", 
            [params.quantity, params.productId]);
        db.commit();
    } catch (e) {
        db.rollback();
        throw e;
    }
    
    return {
        success: true,
        data: result.data
    };
}
```

### 4.3 服务调用

数据服务支持在脚本中调用其他服务：

```javascript
function execute(params) {
    // 调用其他服务
    var result = callService("other-service-id", {
        param1: params.value1,
        param2: params.value2
    });
    
    // 处理结果
    return {
        originalParams: params,
        serviceResult: result
    };
}
```

### 4.4 异步执行

对于长时间运行的任务，可以使用异步执行：

```javascript
function execute(params) {
    // 创建异步任务
    var taskId = createAsyncTask(function() {
        // 长时间运行的任务
        var result = performLongRunningTask(params);
        // 保存任务结果
        saveTaskResult(taskId, result);
    });
    
    // 立即返回任务ID
    return {
        taskId: taskId,
        status: "running"
    };
}
```

## 5. 高级功能

### 5.1 缓存机制

数据服务支持缓存机制，可以通过设置服务的 `cacheTime` 属性来启用缓存：

```javascript
{
    "name": "Cached Service",
    "description": "带缓存的服务",
    "cacheTime": 60000, // 缓存60秒
    "script": "function execute(params) { ... }"
}
```

### 5.2 服务编排

服务编排允许您组合多个服务来创建复杂的业务流程：

```javascript
function execute(params) {
    // 步骤1：调用服务A
    var resultA = callService("service-a", { param: params.value1 });
    
    // 步骤2：根据结果调用不同的服务
    var resultB;
    if (resultA.success) {
        resultB = callService("service-b", { data: resultA.data });
    } else {
        resultB = callService("service-c", { error: resultA.error });
    }
    
    // 步骤3：汇总结果
    return {
        finalResult: resultB.data,
        status: "completed"
    };
}
```

### 5.3 定时任务

数据服务支持定时任务，可以通过 API 或 Web 界面创建定时任务：

```http
POST /api/data-service/scheduler/task
Content-Type: application/json

{
  "name": "Daily Report",
  "description": "生成每日报表",
  "serviceId": "report-service",
  "parameters": {
    "reportType": "daily"
  },
  "cronExpression": "0 0 0 * * ?", // 每天零点执行
  "enabled": true
}
```

### 5.4 监控和统计

数据服务提供了监控和统计功能，可以通过 API 或 Web 界面查看服务调用统计和性能指标：

```http
GET /api/data-service/monitoring/statistics?serviceId=sample-service
```

## 6. 最佳实践

### 6.1 脚本设计

- **保持简单**：脚本应该简单明了，专注于业务逻辑
- **错误处理**：妥善处理异常情况，提供有意义的错误信息
- **参数验证**：在脚本开始时验证参数的有效性
- **注释**：添加适当的注释，说明脚本的功能和参数

### 6.2 性能优化

- **使用缓存**：对于频繁调用且数据变化不频繁的服务，启用缓存
- **减少数据库操作**：尽量减少数据库操作次数，合并查询
- **异步处理**：对于长时间运行的任务，使用异步执行
- **监控性能**：定期检查服务的性能指标，及时优化

### 6.3 安全性

- **参数校验**：严格校验输入参数，防止注入攻击
- **权限控制**：合理设置服务的访问权限
- **敏感数据处理**：不要在脚本中硬编码敏感信息，如密码、密钥等
- **日志记录**：记录关键操作的日志，便于审计和问题排查

## 7. 示例

### 7.1 基础示例

#### 7.1.1 Hello World

```javascript
function execute(params) {
    var name = params.name || "World";
    return {
        message: "Hello, " + name + "!",
        timestamp: new Date().getTime()
    };
}
```

#### 7.1.2 计算器

```javascript
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
```

### 7.2 数据库示例

#### 7.2.1 查询数据

```javascript
function execute(params) {
    var sql = "SELECT * FROM users WHERE 1=1";
    var parameters = [];
    
    if (params.name) {
        sql += " AND name LIKE ?";
        parameters.push("%" + params.name + "%");
    }
    
    if (params.age) {
        sql += " AND age >= ?";
        parameters.push(params.age);
    }
    
    var limit = params.limit || 10;
    var offset = params.offset || 0;
    sql += " LIMIT ? OFFSET ?";
    parameters.push(limit, offset);
    
    var result = db.query(sql, parameters);
    
    return {
        data: result.data,
        total: result.total,
        limit: limit,
        offset: offset
    };
}
```

#### 7.2.2 事务处理

```javascript
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
```

### 7.3 高级示例

#### 7.3.1 服务编排

```javascript
function execute(params) {
    var userId = params.userId;
    
    // 获取用户信息
    var userInfo = callService("get-user-info", { userId: userId });
    
    // 获取用户订单
    var userOrders = callService("get-user-orders", { userId: userId });
    
    // 获取用户积分
    var userPoints = callService("get-user-points", { userId: userId });
    
    // 汇总用户数据
    return {
        userId: userId,
        userInfo: userInfo.data,
        orders: userOrders.data,
        points: userPoints.data,
        timestamp: new Date().getTime()
    };
}
```

#### 7.3.2 异步任务

```javascript
function execute(params) {
    var reportType = params.reportType;
    var startDate = params.startDate;
    var endDate = params.endDate;
    
    // 创建异步任务
    var taskId = createAsyncTask(function() {
        // 收集数据
        var salesData = collectSalesData(startDate, endDate);
        var userData = collectUserData(startDate, endDate);
        
        // 生成报表
        var report = generateReport(reportType, salesData, userData);
        
        // 保存报表
        saveReport(report);
        
        // 发送通知
        sendNotification("Report " + reportType + " is ready");
        
        // 保存任务结果
        saveTaskResult(taskId, {
            reportType: reportType,
            reportId: report.id,
            status: "completed"
        });
    });
    
    // 立即返回任务ID
    return {
        taskId: taskId,
        status: "running",
        startTime: new Date().getTime()
    };
}
```

## 8. 常见问题

### 8.1 脚本执行失败

**问题**：脚本执行失败，返回错误信息。

**解决方案**：
1. 检查脚本语法是否正确
2. 检查参数是否符合要求
3. 检查数据库连接是否正常
4. 查看日志获取详细错误信息

### 8.2 性能问题

**问题**：服务执行速度慢。

**解决方案**：
1. 优化脚本逻辑，减少不必要的操作
2. 优化数据库查询，添加适当的索引
3. 启用缓存机制
4. 使用异步执行处理长时间运行的任务

### 8.3 权限问题

**问题**：无法访问或执行服务。

**解决方案**：
1. 检查用户权限设置
2. 确保服务已启用
3. 检查服务的访问控制配置
4. 联系管理员获取必要的权限

## 9. 参考资料

- [MagicDB 官方文档](https://magicdb.io/docs)
- [JavaScript 参考手册](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference)
- [SQL 参考手册](https://www.w3schools.com/sql/)
