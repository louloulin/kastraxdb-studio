package ai.magicdb.data.service.core.snippet

import ai.magicdb.data.service.api.CodeSnippetService
import ai.magicdb.data.service.api.model.CodeSnippet
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 默认代码片段服务实现
 */
@Service
class DefaultCodeSnippetService : CodeSnippetService {

    private val logger = LoggerFactory.getLogger(DefaultCodeSnippetService::class.java)

    // 代码片段存储
    private val snippets = ConcurrentHashMap<String, CodeSnippet>()

    init {
        // 初始化预定义代码片段
        initPredefinedSnippets()
    }

    override fun getAllSnippets(language: String?): List<CodeSnippet> {
        return snippets.values.filter { language == null || it.language == language }
            .sortedBy { it.name }
    }

    override fun getSnippet(id: String): CodeSnippet? {
        return snippets[id]
    }

    override fun createSnippet(snippet: CodeSnippet): CodeSnippet {
        val id = snippet.id.ifBlank { UUID.randomUUID().toString() }
        val now = Date()

        val newSnippet = snippet.copy(
            id = id,
            createTime = now,
            updateTime = now
        )

        snippets[id] = newSnippet
        logger.info("创建代码片段: {}", id)

        return newSnippet
    }

    override fun updateSnippet(id: String, snippet: CodeSnippet): CodeSnippet? {
        val existingSnippet = snippets[id] ?: return null

        // 不允许更新系统预定义的代码片段
        if (existingSnippet.system) {
            logger.warn("尝试更新系统预定义代码片段: {}", id)
            return null
        }

        val updatedSnippet = snippet.copy(
            id = id,
            createTime = existingSnippet.createTime,
            updateTime = Date()
        )

        snippets[id] = updatedSnippet
        logger.info("更新代码片段: {}", id)

        return updatedSnippet
    }

    override fun deleteSnippet(id: String): Boolean {
        val snippet = snippets[id]

        // 不允许删除系统预定义的代码片段
        if (snippet?.system == true) {
            logger.warn("尝试删除系统预定义代码片段: {}", id)
            return false
        }

        val removed = snippets.remove(id) != null
        if (removed) {
            logger.info("删除代码片段: {}", id)
        } else {
            logger.warn("删除代码片段失败，未找到代码片段: {}", id)
        }

        return removed
    }

    override fun searchSnippets(keyword: String, language: String?): List<CodeSnippet> {
        val lowercaseKeyword = keyword.lowercase()

        return snippets.values.filter { snippet ->
            (language == null || snippet.language == language) &&
                (snippet.name.lowercase().contains(lowercaseKeyword) ||
                    snippet.description?.lowercase()?.contains(lowercaseKeyword) == true ||
                    snippet.tags.any { it.lowercase().contains(lowercaseKeyword) })
        }.sortedBy { it.name }
    }

    /**
     * 初始化预定义代码片段
     */
    private fun initPredefinedSnippets() {
        // JavaScript 代码片段
        createPredefinedSnippet(
            name = "SQL 查询",
            description = "执行 SQL 查询并返回结果",
            content = """
                function execute(params) {
                  // 获取数据源和 SQL
                  const dataSourceId = params.dataSourceId;
                  const sql = params.sql;

                  if (!dataSourceId) {
                    return {
                      success: false,
                      message: "数据源 ID 不能为空"
                    };
                  }

                  if (!sql) {
                    return {
                      success: false,
                      message: "SQL 不能为空"
                    };
                  }

                  try {
                    // 执行 SQL 查询
                    const result = executeQuery(dataSourceId, sql);

                    return {
                      success: true,
                      data: result,
                      message: "查询成功"
                    };
                  } catch (error) {
                    return {
                      success: false,
                      message: "查询失败: " + error.message
                    };
                  }
                }
            """.trimIndent(),
            language = "javascript",
            tags = listOf("SQL", "查询", "数据库")
        )

        createPredefinedSnippet(
            name = "HTTP 请求",
            description = "发送 HTTP 请求并返回结果",
            content = """
                function execute(params) {
                  // 获取请求参数
                  const url = params.url;
                  const method = params.method || "GET";
                  const headers = params.headers || {};
                  const body = params.body;

                  if (!url) {
                    return {
                      success: false,
                      message: "URL 不能为空"
                    };
                  }

                  try {
                    // 发送 HTTP 请求
                    const response = httpRequest(url, {
                      method: method,
                      headers: headers,
                      body: body ? JSON.stringify(body) : undefined
                    });

                    return {
                      success: true,
                      data: response,
                      message: "请求成功"
                    };
                  } catch (error) {
                    return {
                      success: false,
                      message: "请求失败: " + error.message
                    };
                  }
                }
            """.trimIndent(),
            language = "javascript",
            tags = listOf("HTTP", "API", "请求")
        )

        createPredefinedSnippet(
            name = "数据转换",
            description = "转换数据格式",
            content = """
                function execute(params) {
                  // 获取数据
                  const data = params.data;
                  const format = params.format || "json";

                  if (!data) {
                    return {
                      success: false,
                      message: "数据不能为空"
                    };
                  }

                  try {
                    let result;

                    switch (format.toLowerCase()) {
                      case "json":
                        // 转换为 JSON
                        result = typeof data === "string" ? JSON.parse(data) : data;
                        break;
                      case "xml":
                        // 转换为 XML
                        result = "XML转换功能暂未实现";
                        break;
                      case "csv":
                        // 转换为 CSV
                        result = "CSV转换功能暂未实现";
                        break;
                      default:
                        return {
                          success: false,
                          message: "不支持的格式: " + format
                        };
                    }

                    return {
                      success: true,
                      data: result,
                      message: "转换成功"
                    };
                  } catch (error) {
                    return {
                      success: false,
                      message: "转换失败: " + error.message
                    };
                  }
                }
            """.trimIndent(),
            language = "javascript",
            tags = listOf("数据转换", "JSON", "XML", "CSV")
        )

        // Kotlin 代码片段
        createPredefinedSnippet(
            name = "SQL 查询 (Kotlin)",
            description = "执行 SQL 查询并返回结果",
            content = """
                fun execute(params: Map<String, Any?>): Map<String, Any?> {
                    // 获取数据源和 SQL
                    val dataSourceId = params["dataSourceId"] as? Long
                    val sql = params["sql"] as? String

                    if (dataSourceId == null) {
                        return mapOf(
                            "success" to false,
                            "message" to "数据源 ID 不能为空"
                        )
                    }

                    if (sql.isNullOrBlank()) {
                        return mapOf(
                            "success" to false,
                            "message" to "SQL 不能为空"
                        )
                    }

                    return try {
                        // 执行 SQL 查询
                        val result = executeQuery(dataSourceId, sql)

                        mapOf(
                            "success" to true,
                            "data" to result,
                            "message" to "查询成功"
                        )
                    } catch (e: Exception) {
                        mapOf(
                            "success" to false,
                            "message" to "查询失败: " + e.message
                        )
                    }
                }
            """.trimIndent(),
            language = "kotlin",
            tags = listOf("SQL", "查询", "数据库", "Kotlin")
        )

        createPredefinedSnippet(
            name = "HTTP 请求 (Kotlin)",
            description = "发送 HTTP 请求并返回结果",
            content = """
                fun execute(params: Map<String, Any?>): Map<String, Any?> {
                    // 获取请求参数
                    val url = params["url"] as? String
                    val method = params["method"] as? String ?: "GET"
                    val headers = params["headers"] as? Map<String, String> ?: emptyMap()
                    val body = params["body"]

                    if (url.isNullOrBlank()) {
                        return mapOf(
                            "success" to false,
                            "message" to "URL 不能为空"
                        )
                    }

                    return try {
                        // 发送 HTTP 请求
                        val response = httpRequest(url, mapOf(
                            "method" to method,
                            "headers" to headers,
                            "body" to body?.let { it.toString() }
                        ))

                        mapOf(
                            "success" to true,
                            "data" to response,
                            "message" to "请求成功"
                        )
                    } catch (e: Exception) {
                        mapOf(
                            "success" to false,
                            "message" to "请求失败: " + e.message
                        )
                    }
                }
            """.trimIndent(),
            language = "kotlin",
            tags = listOf("HTTP", "API", "请求", "Kotlin")
        )

        // Python 代码片段
        createPredefinedSnippet(
            name = "SQL 查询 (Python)",
            description = "执行 SQL 查询并返回结果",
            content = """
                def execute(params):
                    # 获取数据源和 SQL
                    data_source_id = params.get('dataSourceId')
                    sql = params.get('sql')

                    if not data_source_id:
                        return {
                            'success': False,
                            'message': '数据源 ID 不能为空'
                        }

                    if not sql:
                        return {
                            'success': False,
                            'message': 'SQL 不能为空'
                        }

                    try:
                        # 执行 SQL 查询
                        result = execute_query(data_source_id, sql)

                        return {
                            'success': True,
                            'data': result,
                            'message': '查询成功'
                        }
                    except Exception as e:
                        return {
                            'success': False,
                            'message': f'查询失败: {str(e)}'
                        }
            """.trimIndent(),
            language = "python",
            tags = listOf("SQL", "查询", "数据库", "Python")
        )

        createPredefinedSnippet(
            name = "HTTP 请求 (Python)",
            description = "发送 HTTP 请求并返回结果",
            content = """
                def execute(params):
                    # 获取请求参数
                    url = params.get('url')
                    method = params.get('method', 'GET')
                    headers = params.get('headers', {})
                    body = params.get('body')

                    if not url:
                        return {
                            'success': False,
                            'message': 'URL 不能为空'
                        }

                    try:
                        # 发送 HTTP 请求
                        response = http_request(url, {
                            'method': method,
                            'headers': headers,
                            'body': json.dumps(body) if body else None
                        })

                        return {
                            'success': True,
                            'data': response,
                            'message': '请求成功'
                        }
                    except Exception as e:
                        return {
                            'success': False,
                            'message': f'请求失败: {str(e)}'
                        }
            """.trimIndent(),
            language = "python",
            tags = listOf("HTTP", "API", "请求", "Python")
        )
    }

    /**
     * 创建预定义代码片段
     */
    private fun createPredefinedSnippet(
        name: String,
        description: String,
        content: String,
        language: String,
        tags: List<String> = emptyList()
    ) {
        val id = UUID.randomUUID().toString()
        val now = Date()

        val snippet = CodeSnippet(
            id = id,
            name = name,
            description = description,
            content = content,
            language = language,
            tags = tags,
            system = true,
            creator = "system",
            createTime = now,
            updateTime = now
        )

        snippets[id] = snippet
    }
}
