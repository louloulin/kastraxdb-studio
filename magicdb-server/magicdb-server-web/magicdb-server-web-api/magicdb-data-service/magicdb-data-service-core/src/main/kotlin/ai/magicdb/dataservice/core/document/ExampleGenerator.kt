package ai.magicdb.dataservice.core.document

import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.Parameter
import ai.magicdb.dataservice.api.model.ServiceDocument
import ai.magicdb.dataservice.api.model.ServiceGroup
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * 示例生成器
 *
 * @author magicdb
 */
@Component
class ExampleGenerator {
    
    /**
     * 生成示例服务
     *
     * @return 示例服务
     */
    fun generateExampleService(): DataService {
        return DataService(
            id = UUID.randomUUID().toString(),
            name = "示例数据服务",
            description = "这是一个示例数据服务，用于演示数据服务的基本功能",
            groupId = "example-group",
            script = """
                /**
                 * 示例数据服务
                 * 
                 * @param name 姓名
                 * @param age 年龄
                 * @return 问候语
                 */
                function execute(name, age) {
                    // 参数验证
                    if (!name) {
                        return {
                            success: false,
                            message: "姓名不能为空"
                        };
                    }
                    
                    // 业务逻辑
                    let greeting = "你好，" + name;
                    if (age) {
                        greeting += "，你今年" + age + "岁了";
                    }
                    
                    // 返回结果
                    return {
                        success: true,
                        data: {
                            greeting: greeting,
                            timestamp: new Date().getTime()
                        }
                    };
                }
            """.trimIndent(),
            language = "js",
            parameters = listOf(
                Parameter(
                    name = "name",
                    type = "string",
                    description = "姓名",
                    required = true,
                    defaultValue = null
                ),
                Parameter(
                    name = "age",
                    type = "number",
                    description = "年龄",
                    required = false,
                    defaultValue = null
                )
            ),
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1,
            updateUserId = 1,
            status = 1,
            path = "/example/greeting",
            method = "GET",
            options = mapOf(
                "timeout" to 5000,
                "cache" to true,
                "cacheTime" to 60000
            )
        )
    }
    
    /**
     * 生成示例分组
     *
     * @return 示例分组
     */
    fun generateExampleGroup(): ServiceGroup {
        return ServiceGroup(
            id = "example-group",
            name = "示例分组",
            description = "这是一个示例分组，用于演示数据服务的分组功能",
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1,
            updateUserId = 1,
            parentId = null,
            path = "/example"
        )
    }
    
    /**
     * 生成示例文档
     *
     * @return 示例文档
     */
    fun generateExampleDocument(): ServiceDocument {
        return ServiceDocument(
            id = UUID.randomUUID().toString(),
            title = "示例数据服务文档",
            content = """
                # 示例数据服务文档
                
                这是一个示例数据服务文档，用于演示数据服务的文档功能。
                
                ## 服务信息
                
                - **名称**：示例数据服务
                - **描述**：这是一个示例数据服务，用于演示数据服务的基本功能
                - **路径**：/example/greeting
                - **方法**：GET
                - **语言**：JavaScript
                
                ## 参数
                
                | 参数名 | 类型 | 必填 | 默认值 | 描述 |
                | --- | --- | --- | --- | --- |
                | name | string | 是 | - | 姓名 |
                | age | number | 否 | - | 年龄 |
                
                ## 返回值
                
                成功时返回：
                
                ```json
                {
                  "success": true,
                  "data": {
                    "greeting": "你好，张三，你今年18岁了",
                    "timestamp": 1621234567890
                  }
                }
                ```
                
                失败时返回：
                
                ```json
                {
                  "success": false,
                  "message": "姓名不能为空"
                }
                ```
                
                ## 示例代码
                
                ### JavaScript
                
                ```javascript
                // 使用 fetch API 调用服务
                fetch('/api/data-service/execute/example/greeting?name=张三&age=18')
                  .then(response => response.json())
                  .then(data => {
                    console.log(data);
                  })
                  .catch(error => {
                    console.error('调用服务失败:', error);
                  });
                ```
                
                ### Java
                
                ```java
                // 使用 OkHttp 调用服务
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                  .url("http://localhost:8080/api/data-service/execute/example/greeting?name=张三&age=18")
                  .build();
                
                try (Response response = client.newCall(request).execute()) {
                  String responseBody = response.body().string();
                  System.out.println(responseBody);
                } catch (IOException e) {
                  e.printStackTrace();
                }
                ```
                
                ### Python
                
                ```python
                # 使用 requests 库调用服务
                import requests
                
                response = requests.get('http://localhost:8080/api/data-service/execute/example/greeting', 
                                       params={'name': '张三', 'age': 18})
                data = response.json()
                print(data)
                ```
                
                ### Kotlin
                
                ```kotlin
                // 使用 Retrofit 调用服务
                interface DataServiceApi {
                    @GET("/api/data-service/execute/example/greeting")
                    suspend fun greeting(@Query("name") name: String, @Query("age") age: Int?): Response<Map<String, Any>>
                }
                
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://localhost:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                
                val api = retrofit.create(DataServiceApi::class.java)
                
                // 在协程中调用
                GlobalScope.launch {
                    try {
                        val response = api.greeting("张三", 18)
                        if (response.isSuccessful) {
                            val data = response.body()
                            println(data)
                        } else {
                            println("调用服务失败: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        println("调用服务异常: ${e.message}")
                    }
                }
                ```
                
                ## 注意事项
                
                1. 参数 `name` 是必填的，如果不提供将返回错误
                2. 参数 `age` 是可选的，如果提供将在问候语中包含年龄信息
                3. 服务返回的时间戳是毫秒级的
                
                ## 更新历史
                
                | 版本 | 日期 | 更新内容 |
                | --- | --- | --- |
                | 1.0 | 2023-01-01 | 初始版本 |
                | 1.1 | 2023-02-01 | 添加时间戳返回 |
                
                ## 相关服务
                
                - [用户服务](/api/data-service/document/service/user-service)
                - [订单服务](/api/data-service/document/service/order-service)
                
                ## 联系我们
                
                如有问题，请联系 support@magicdb.ai
            """.trimIndent(),
            format = "markdown",
            serviceId = null,
            groupId = "example-group",
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1,
            updateUserId = 1,
            isPublic = true
        )
    }
}
