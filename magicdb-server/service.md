# MagicDB 数据服务模块实现计划

## 概述

本文档概述了实现 MagicDB 数据服务模块的计划，该模块受 magic-api 框架启发。该模块将提供一个低代码平台，通过 Web 界面创建和管理数据服务，并由 GraalVM 提供脚本执行能力。

## 背景

[magic-api](https://github.com/ssssssss-team/magic-api) 是一个基于 Java 的快速 API 开发框架，允许开发人员通过 Web 界面创建 HTTP API，无需定义传统的 Java 组件，如 Controllers、Services、DAOs 等。API 通过脚本和配置创建，并自动映射为 HTTP 端点。

MagicDB 目前提供数据库连接和管理功能。添加具有脚本支持的数据服务层将使用户能够直接在 MagicDB 平台内创建自定义数据处理逻辑、转换和 API。

## 架构

### 核心组件

1. **脚本引擎模块**
   - 基于 GraalVM 的多语言脚本执行引擎
   - 支持多种语言（Kotlin、JavaScript、Python、WebAssembly）
   - 支持 LINQ 风格的查询和数据操作
   - 脚本编译和缓存以提高性能

2. **Web 界面**
   - 具有语法高亮和代码补全功能的脚本编辑器
   - API 管理界面
   - 测试和调试工具

3. **API 运行时**
   - HTTP 端点映射
   - 请求/响应处理
   - 参数验证和转换

4. **数据访问层**
   - 与现有 MagicDB 数据库连接集成
   - 数据查询和操作工具
   - 事务管理

5. **安全层**
   - 身份验证和授权
   - API 访问控制
   - 脚本执行沙箱

## 实施计划

### 阶段 1：核心脚本引擎

1. **创建脚本引擎模块** ✅
   - 实现 `magicdb-script-engine` 模块 ✅
   - 添加 GraalVM 依赖 ✅
   - 创建脚本执行上下文和 API ✅
   - 实现对 Kotlin、JavaScript、Python 和 WebAssembly 的支持 ✅

2. **实现基本脚本功能** ✅
   - 数据库访问工具 ✅
   - 通用数据转换函数 ✅
   - HTTP 客户端功能 ✅
   - LINQ 风格的查询接口 ✅

3. **创建脚本管理** ✅
   - 脚本存储和检索 ✅
   - 版本控制 ✅
   - 脚本元数据 ✅

### 阶段 2：API 运行时

1. **实现 API 映射** ✅
   - HTTP 端点注册 ✅
   - 请求参数绑定 ✅
   - 响应格式化 ✅

2. **添加 API 管理** ✅
   - API 分组和组织 ✅
   - API 文档生成 ✅
   - API 测试工具 ✅

3. **实现安全控制** ✅
   - API 访问控制 ✅
   - 脚本执行权限 ✅
   - 输入验证 ✅

### 阶段 3：Web 界面

1. **创建脚本编辑器**
   - 具有语法高亮的代码编辑器
   - 自动完成
   - 实时验证
   - 多语言支持（Kotlin、JavaScript、Python、WebAssembly）

2. **实现 API 测试界面**
   - 请求构建器
   - 响应查看器
   - 调试工具

3. **添加文档功能**
   - API 文档生成
   - Swagger/OpenAPI 集成
   - 示例生成

### 阶段 4：性能优化

1. **实现脚本编译和缓存** ✅
   - 脚本预编译 ✅
   - 缓存编译结果 ✅
   - 缓存失效策略 ✅

2. **添加连接池和事务管理** ✅
   - 数据库连接池 ✅
   - 事务管理 ✅
   - 多数据源支持 ✅

3. **实现资源限制和断路器** ✅
   - 并发限制 ✅
   - 资源限制 ✅
   - 断路器机制 ✅

## 技术细节

### GraalVM 集成

我们将使用 GraalVM 的 Polyglot API 嵌入脚本功能：

```java
public class ScriptExecutor {
    private final Context context;

    public ScriptExecutor() {
        this.context = Context.newBuilder()
            .allowAllAccess(true)
            .allowPolyglotAccess(PolyglotAccess.ALL)
            .allowHostAccess(HostAccess.ALL)
            .build();
    }

    public Object executeScript(String language, String script, Map<String, Object> params) {
        // 绑定参数到脚本上下文
        Value bindings = context.getBindings(language);
        params.forEach(bindings::putMember);

        // 执行脚本
        Value result = context.eval(language, script);

        // 将结果转换为 Java 对象
        return result.as(Object.class);
    }

    public void close() {
        context.close();
    }
}
```

### 脚本 API 设计

脚本将可以访问丰富的数据库操作 API：

#### JavaScript 示例
```javascript
// JavaScript API 使用示例
var result = db.query("SELECT * FROM users WHERE age > ?", [18]);
var transformed = result.map(user => {
    return {
        id: user.id,
        fullName: user.first_name + " " + user.last_name,
        isAdult: user.age >= 18
    };
});
return transformed;
```

#### Kotlin 示例
```kotlin
// Kotlin API 使用示例
val result = db.query("SELECT * FROM users WHERE age > ?", listOf(18))
val transformed = result.map { user ->
    mapOf(
        "id" to user.id,
        "fullName" to "${user.first_name} ${user.last_name}",
        "isAdult" to (user.age >= 18)
    )
}
return transformed
```

#### Python 示例
```python
# Python API 使用示例
result = db.query("SELECT * FROM users WHERE age > ?", [18])
transformed = [{
    "id": user.id,
    "fullName": user.first_name + " " + user.last_name,
    "isAdult": user.age >= 18
} for user in result]
return transformed
```

#### LINQ 风格查询示例
```kotlin
// LINQ 风格查询示例
val users = db.from("users")
    .where("age > ?", 18)
    .select()

val transformed = users.map { user ->
    mapOf(
        "id" to user.id,
        "fullName" to "${user.first_name} ${user.last_name}",
        "isAdult" to true
    )
}
return transformed
```

### 模块结构

```
magicdb-server/
├── magicdb-script/
│   ├── magicdb-script-engine/       # 核心脚本执行引擎
│   ├── magicdb-script-api/          # 脚本 API 定义
│   ├── magicdb-script-web/          # 脚本管理的 Web 界面
│   └── magicdb-script-runtime/      # API 运行时和执行
└── magicdb-data-service/
    ├── magicdb-data-service-api/    # 数据服务 API
    ├── magicdb-data-service-core/   # 核心数据服务实现
    └── magicdb-data-service-web/    # 数据服务的 Web 界面
```

## 与现有 MagicDB 组件的集成

1. **数据库连接**
   - 利用现有的 `magicdb-spi` 和数据库插件
   - 扩展 `CommandExecutor` 以支持基于脚本的执行
   - 将脚本上下文添加到数据库操作中

2. **用户界面**
   - 与现有 Web UI 集成
   - 添加脚本编辑器和 API 管理页面
   - 扩展导航和菜单结构

3. **安全**
   - 与现有身份验证系统集成
   - 添加脚本特定权限
   - 实现 API 访问控制

## 性能考虑

1. **脚本编译**
   - 将脚本编译为字节码以加快执行速度
   - 缓存已编译的脚本
   - 实现脚本依赖跟踪

2. **连接池**
   - 重用数据库连接
   - 为脚本实现连接池
   - 添加事务管理

3. **资源限制**
   - 设置脚本执行时间限制
   - 限制内存使用
   - 为外部调用实现断路器

## 安全考虑

1. **脚本沙箱**
   - 限制对系统资源的访问
   - 实现类白名单
   - 防止未授权的文件系统访问

2. **输入验证**
   - 验证所有脚本输入
   - 实现 SQL 注入防护
   - 为数据库查询添加参数绑定

3. **访问控制**
   - 实现细粒度权限
   - 添加审计日志
   - 强制执行 API 速率限制

## 开发路线图

### 里程碑 1：基本脚本引擎（2 周）✅
- 设置 GraalVM 集成 ✅
- 实现基本脚本执行 ✅
- 创建数据库访问工具 ✅
- 实现对 Kotlin、JavaScript、Python 和 WebAssembly 的支持 ✅
- 实现 LINQ 风格查询接口 ✅

### 里程碑 2：脚本管理（2 周）✅
- 实现脚本存储 ✅
- 添加脚本版本控制 ✅
- 创建脚本元数据管理 ✅

### 里程碑 3：API 运行时（3 周）✅
- 实现 HTTP 端点映射 ✅
- 添加请求/响应处理 ✅
- 创建参数绑定 ✅

### 里程碑 4：Web 界面（3 周）
- 开发多语言脚本编辑器
- 实现 API 测试工具
- 添加文档功能

### 里程碑 5：安全和性能（2 周）✅
- 实现安全控制 ✅
- 添加性能优化 ✅
- 创建监控和日志记录 ✅

## 结论

MagicDB 数据服务模块将提供一个强大的低代码平台，用于创建和管理数据服务。通过利用 GraalVM 的脚本功能并与现有 MagicDB 基础设施集成，我们可以创建一个灵活而强大的系统，用于数据转换、API 创建和业务逻辑实现。

该实现将显著增强 MagicDB 的功能，使用户不仅可以连接和查询数据库，还可以创建自定义数据服务和 API，而无需编写传统的 Java 代码。通过支持 Kotlin、JavaScript、Python 和 WebAssembly 等多种语言，以及 LINQ 风格的查询接口，用户可以使用最适合其需求的语言和范式来开发数据服务。

## 实现进度

### 已完成

- ✅ 核心脚本引擎模块（magicdb-script-engine）
- ✅ 脚本 API 定义（magicdb-script-api）
- ✅ 脚本运行时（magicdb-script-runtime）
- ✅ 多语言支持（Kotlin、JavaScript、Python、WebAssembly）
- ✅ LINQ 风格查询接口
- ✅ 脚本命令执行器
- ✅ 脚本插件
- ✅ 全部使用 Kotlin 语言实现
- ✅ 添加单元测试
- ✅ API 映射（HTTP 端点注册、请求参数绑定、响应格式化）
- ✅ API 管理（API 分组和组织、API 文档生成、API 测试工具）
- ✅ 安全控制（API 访问控制、脚本执行权限、输入验证）
- ✅ 数据服务 API（数据服务模型、数据服务存储、数据服务执行器）
- ✅ 数据服务核心实现（数据服务管理、数据服务缓存、数据服务导入导出）
- ✅ 数据服务 Web 界面（数据服务控制器、数据服务运行控制器）
- ✅ 性能优化（脚本编译和缓存、连接池和事务管理、资源限制和断路器）
- ✅ 脚本管理（脚本存储、脚本版本控制、脚本元数据管理）
- ✅ 集成测试（脚本集成测试、数据服务集成测试）
- ✅ API 文档（Swagger/OpenAPI 文档）
- ✅ 示例脚本（JavaScript、Kotlin、Python）

### 待完成

- ⬜ Web 界面（在 magicdb-client 中实现）
