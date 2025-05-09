package ai.magicdb.script.runtime.doc

import ai.magicdb.script.api.model.ApiGroupInfo
import ai.magicdb.script.api.model.ApiInfo
import ai.magicdb.script.api.model.ParameterInfo
import ai.magicdb.script.engine.GraalVMScriptExecutor
import ai.magicdb.script.runtime.repository.MemoryApiRepository
import ai.magicdb.script.runtime.service.DefaultApiService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * API文档生成器测试
 *
 * @author magicdb
 */
class ApiDocGeneratorTest {
    
    private lateinit var apiDocGenerator: ApiDocGenerator
    private lateinit var apiService: DefaultApiService
    private lateinit var apiRepository: MemoryApiRepository
    
    @BeforeEach
    fun setUp() {
        apiRepository = MemoryApiRepository()
        apiService = DefaultApiService(apiRepository, GraalVMScriptExecutor())
        apiDocGenerator = ApiDocGenerator(apiService)
        
        // 创建测试分组
        val group = ApiGroupInfo(
            id = "test-group",
            name = "测试分组",
            path = "/test",
            description = "测试分组描述"
        )
        apiRepository.saveGroup(group)
        
        // 创建测试API
        val api = ApiInfo(
            id = "test-api",
            name = "测试API",
            path = "/hello",
            method = "GET",
            groupId = "test-group",
            script = "// js\nvar message = 'Hello, ' + name;\nreturn message;",
            language = "js",
            parameters = listOf(
                ParameterInfo(
                    name = "name",
                    type = "string",
                    description = "名称",
                    required = true
                )
            ),
            description = "测试API描述"
        )
        apiRepository.saveApi(api)
    }
    
    @Test
    fun testGenerateMarkdownDoc() {
        val markdown = apiDocGenerator.generateMarkdownDoc()
        
        // 检查标题
        assertTrue(markdown.contains("# API文档"))
        
        // 检查目录
        assertTrue(markdown.contains("## 目录"))
        assertTrue(markdown.contains("- [测试分组]"))
        assertTrue(markdown.contains("  - [测试API]"))
        
        // 检查分组
        assertTrue(markdown.contains("## 测试分组"))
        assertTrue(markdown.contains("测试分组描述"))
        assertTrue(markdown.contains("基础路径: /test"))
        
        // 检查API
        assertTrue(markdown.contains("### 测试API"))
        assertTrue(markdown.contains("测试API描述"))
        assertTrue(markdown.contains("- 路径: /hello"))
        assertTrue(markdown.contains("- 方法: GET"))
        assertTrue(markdown.contains("- 脚本语言: js"))
        
        // 检查参数
        assertTrue(markdown.contains("#### 参数"))
        assertTrue(markdown.contains("| 名称 | 位置 | 类型 | 必须 | 描述 |"))
        assertTrue(markdown.contains("| name | query | string | 是 | 名称 |"))
        
        // 检查脚本
        assertTrue(markdown.contains("#### 脚本"))
        assertTrue(markdown.contains("```js"))
        assertTrue(markdown.contains("var message = 'Hello, ' + name;"))
        assertTrue(markdown.contains("return message;"))
    }
    
    @Test
    fun testGenerateOpenApiDoc() {
        val openapi = apiDocGenerator.generateOpenApiDoc()
        
        // 检查基本信息
        assertEquals("3.0.0", openapi["openapi"])
        assertTrue(openapi["info"] is Map<*, *>)
        assertTrue(openapi["servers"] is List<*>)
        assertTrue(openapi["paths"] is Map<*, *>)
        assertTrue(openapi["tags"] is List<*>)
        
        // 检查信息
        val info = openapi["info"] as Map<*, *>
        assertEquals("MagicDB API", info["title"])
        
        // 检查服务器
        val servers = openapi["servers"] as List<*>
        val server = servers[0] as Map<*, *>
        assertEquals("/script-api", server["url"])
        
        // 检查路径
        val paths = openapi["paths"] as Map<*, *>
        assertTrue(paths.containsKey("/hello"))
        
        val pathItem = paths["/hello"] as Map<*, *>
        assertTrue(pathItem.containsKey("get"))
        
        val operation = pathItem["get"] as Map<*, *>
        assertEquals("测试API", operation["summary"])
        assertEquals("测试API描述", operation["description"])
        
        // 检查参数
        val parameters = operation["parameters"] as List<*>
        val parameter = parameters[0] as Map<*, *>
        assertEquals("name", parameter["name"])
        assertEquals("query", parameter["in"])
        assertEquals("名称", parameter["description"])
        assertEquals(true, parameter["required"])
        
        // 检查标签
        val tags = openapi["tags"] as List<*>
        val tag = tags[0] as Map<*, *>
        assertEquals("测试分组", tag["name"])
        assertEquals("测试分组描述", tag["description"])
    }
}
