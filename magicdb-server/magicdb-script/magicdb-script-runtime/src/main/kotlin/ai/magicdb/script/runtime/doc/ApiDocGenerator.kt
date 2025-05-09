package ai.magicdb.script.runtime.doc

import ai.magicdb.script.api.ApiService
import ai.magicdb.script.api.model.ApiGroupInfo
import ai.magicdb.script.api.model.ApiInfo
import org.slf4j.LoggerFactory
import java.util.*

/**
 * API文档生成器
 *
 * @author magicdb
 */
class ApiDocGenerator(private val apiService: ApiService) {
    private val logger = LoggerFactory.getLogger(ApiDocGenerator::class.java)

    /**
     * 生成Markdown文档
     */
    fun generateMarkdownDoc(): String {
        val sb = StringBuilder()
        
        // 添加标题
        sb.append("# API文档\n\n")
        sb.append("生成时间: ${Date()}\n\n")
        
        // 获取所有分组
        val groups = apiService.getAllGroups()
        
        // 生成目录
        sb.append("## 目录\n\n")
        groups.forEach { group ->
            sb.append("- [${group.name}](#${group.name.toLowerCase().replace(" ", "-")})\n")
            
            // 获取分组下的API
            val apis = apiService.getApisByGroupId(group.id)
            apis.forEach { api ->
                sb.append("  - [${api.name}](#${api.name.toLowerCase().replace(" ", "-")})\n")
            }
        }
        sb.append("\n")
        
        // 生成分组和API详情
        groups.forEach { group ->
            generateGroupDoc(sb, group)
        }
        
        return sb.toString()
    }

    /**
     * 生成OpenAPI文档
     */
    fun generateOpenApiDoc(): Map<String, Any> {
        val openapi = mutableMapOf<String, Any>()
        
        // 添加基本信息
        openapi["openapi"] = "3.0.0"
        openapi["info"] = mapOf(
            "title" to "MagicDB API",
            "description" to "MagicDB API文档",
            "version" to "1.0.0"
        )
        
        // 添加服务器信息
        openapi["servers"] = listOf(
            mapOf(
                "url" to "/script-api",
                "description" to "MagicDB API服务器"
            )
        )
        
        // 添加路径信息
        val paths = mutableMapOf<String, Any>()
        
        // 获取所有API
        val apis = apiService.getAllApis()
        apis.forEach { api ->
            val pathItem = mutableMapOf<String, Any>()
            val operation = mutableMapOf<String, Any>()
            
            // 添加基本信息
            operation["summary"] = api.name
            operation["description"] = api.description
            
            // 添加参数信息
            val parameters = mutableListOf<Map<String, Any>>()
            api.parameters.forEach { param ->
                val parameter = mutableMapOf<String, Any>()
                parameter["name"] = param.name
                parameter["in"] = param.position
                parameter["description"] = param.description
                parameter["required"] = param.required
                parameter["schema"] = mapOf("type" to param.type)
                if (param.example != null) {
                    parameter["example"] = param.example!!
                }
                parameters.add(parameter)
            }
            operation["parameters"] = parameters
            
            // 添加请求体信息
            if (api.requestBody != null) {
                val requestBody = mutableMapOf<String, Any>()
                requestBody["description"] = api.requestBody!!.description
                requestBody["required"] = true
                
                val content = mutableMapOf<String, Any>()
                val mediaType = mutableMapOf<String, Any>()
                
                if (api.requestBody!!.schema != null) {
                    mediaType["schema"] = api.requestBody!!.schema!!
                }
                if (api.requestBody!!.example != null) {
                    mediaType["example"] = api.requestBody!!.example!!
                }
                
                content[api.requestBody!!.type] = mediaType
                requestBody["content"] = content
                
                operation["requestBody"] = requestBody
            }
            
            // 添加响应信息
            val responses = mutableMapOf<String, Any>()
            val response = mutableMapOf<String, Any>()
            
            if (api.responseInfo != null) {
                response["description"] = api.responseInfo!!.description
                
                val content = mutableMapOf<String, Any>()
                val mediaType = mutableMapOf<String, Any>()
                
                if (api.responseInfo!!.schema != null) {
                    mediaType["schema"] = api.responseInfo!!.schema!!
                }
                if (api.responseInfo!!.example != null) {
                    mediaType["example"] = api.responseInfo!!.example!!
                }
                
                content[api.responseInfo!!.type] = mediaType
                response["content"] = content
            } else {
                response["description"] = "成功响应"
            }
            
            responses["200"] = response
            operation["responses"] = responses
            
            // 添加标签信息
            val group = apiService.getGroup(api.groupId)
            if (group != null) {
                operation["tags"] = listOf(group.name)
            }
            
            // 添加到路径
            pathItem[api.method.toLowerCase()] = operation
            
            // 如果路径已存在，合并操作
            if (paths.containsKey(api.path)) {
                val existingPathItem = paths[api.path] as MutableMap<String, Any>
                existingPathItem.putAll(pathItem)
            } else {
                paths[api.path] = pathItem
            }
        }
        
        openapi["paths"] = paths
        
        // 添加标签信息
        val tags = mutableListOf<Map<String, String>>()
        val groups = apiService.getAllGroups()
        groups.forEach { group ->
            tags.add(mapOf(
                "name" to group.name,
                "description" to group.description
            ))
        }
        openapi["tags"] = tags
        
        return openapi
    }

    private fun generateGroupDoc(sb: StringBuilder, group: ApiGroupInfo) {
        sb.append("## ${group.name}\n\n")
        sb.append("${group.description}\n\n")
        sb.append("基础路径: ${group.path}\n\n")
        
        // 获取分组下的API
        val apis = apiService.getApisByGroupId(group.id)
        apis.forEach { api ->
            generateApiDoc(sb, api)
        }
    }

    private fun generateApiDoc(sb: StringBuilder, api: ApiInfo) {
        sb.append("### ${api.name}\n\n")
        sb.append("${api.description}\n\n")
        sb.append("- 路径: ${api.path}\n")
        sb.append("- 方法: ${api.method}\n")
        sb.append("- 脚本语言: ${api.language}\n\n")
        
        // 添加参数信息
        if (api.parameters.isNotEmpty()) {
            sb.append("#### 参数\n\n")
            sb.append("| 名称 | 位置 | 类型 | 必须 | 描述 |\n")
            sb.append("|------|------|------|------|------|\n")
            api.parameters.forEach { param ->
                sb.append("| ${param.name} | ${param.position} | ${param.type} | ${if (param.required) "是" else "否"} | ${param.description} |\n")
            }
            sb.append("\n")
        }
        
        // 添加请求体信息
        if (api.requestBody != null) {
            sb.append("#### 请求体\n\n")
            sb.append("- 类型: ${api.requestBody!!.type}\n")
            sb.append("- 描述: ${api.requestBody!!.description}\n\n")
            
            if (api.requestBody!!.example != null) {
                sb.append("示例:\n\n")
                sb.append("```json\n")
                sb.append(api.requestBody!!.example)
                sb.append("\n```\n\n")
            }
        }
        
        // 添加响应信息
        if (api.responseInfo != null) {
            sb.append("#### 响应\n\n")
            sb.append("- 类型: ${api.responseInfo!!.type}\n")
            sb.append("- 描述: ${api.responseInfo!!.description}\n\n")
            
            if (api.responseInfo!!.example != null) {
                sb.append("示例:\n\n")
                sb.append("```json\n")
                sb.append(api.responseInfo!!.example)
                sb.append("\n```\n\n")
            }
        }
        
        // 添加脚本信息
        sb.append("#### 脚本\n\n")
        sb.append("```${api.language}\n")
        sb.append(api.script)
        sb.append("\n```\n\n")
    }
}
