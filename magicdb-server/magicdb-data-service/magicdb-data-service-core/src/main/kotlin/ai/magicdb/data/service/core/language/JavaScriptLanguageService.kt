package ai.magicdb.data.service.core.language

import ai.magicdb.data.service.api.ScriptLanguageService
import ai.magicdb.data.service.api.model.CompletionItem
import ai.magicdb.data.service.api.model.CompletionItemKind
import ai.magicdb.data.service.api.model.DiagnosticItem
import ai.magicdb.data.service.api.model.DiagnosticSeverity
import ai.magicdb.data.service.api.model.HoverInfo
import ai.magicdb.data.service.api.model.InsertTextFormat
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * JavaScript 语言服务实现
 */
@Service
class JavaScriptLanguageService : ScriptLanguageService {

    private val logger = LoggerFactory.getLogger(JavaScriptLanguageService::class.java)

    // JavaScript 关键字
    private val keywords = listOf(
        "break", "case", "catch", "class", "const", "continue", "debugger", "default", "delete",
        "do", "else", "export", "extends", "false", "finally", "for", "function", "if", "import",
        "in", "instanceof", "new", "null", "return", "super", "switch", "this", "throw", "true",
        "try", "typeof", "var", "void", "while", "with", "yield", "let", "static", "enum", "await",
        "implements", "package", "protected", "interface", "private", "public"
    )

    // JavaScript 内置对象
    private val builtInObjects = listOf(
        "Array", "Boolean", "Date", "Error", "Function", "JSON", "Math", "Number", "Object",
        "RegExp", "String", "Symbol", "Map", "Set", "WeakMap", "WeakSet", "Promise", "Proxy",
        "Reflect", "console", "document", "window", "global", "process"
    )

    // JavaScript 内置函数
    private val builtInFunctions = listOf(
        "parseInt", "parseFloat", "isNaN", "isFinite", "decodeURI", "decodeURIComponent",
        "encodeURI", "encodeURIComponent", "eval", "setTimeout", "setInterval", "clearTimeout",
        "clearInterval", "require"
    )

    // 数据服务 API
    private val dataServiceApis = listOf(
        "executeQuery", "executeUpdate", "getConnection", "getDataSource", "getMetaData",
        "getParameters", "getResult", "getResultSet", "getRowCount", "getStatement",
        "setParameter", "setParameters", "beginTransaction", "commit", "rollback"
    )

    override fun getCompletionItems(language: String, script: String, position: Int): List<CompletionItem> {
        if (language != "js" && language != "javascript") {
            return emptyList()
        }

        try {
            val completionItems = mutableListOf<CompletionItem>()

            // 添加关键字
            completionItems.addAll(keywords.map { keyword ->
                CompletionItem(
                    label = keyword,
                    insertText = keyword,
                    kind = CompletionItemKind.KEYWORD,
                    detail = "JavaScript keyword"
                )
            })

            // 添加内置对象
            completionItems.addAll(builtInObjects.map { obj ->
                CompletionItem(
                    label = obj,
                    insertText = obj,
                    kind = CompletionItemKind.CLASS,
                    detail = "JavaScript built-in object"
                )
            })

            // 添加内置函数
            completionItems.addAll(builtInFunctions.map { func ->
                CompletionItem(
                    label = func,
                    insertText = func,
                    kind = CompletionItemKind.FUNCTION,
                    detail = "JavaScript built-in function"
                )
            })

            // 添加数据服务 API
            completionItems.addAll(dataServiceApis.map { api ->
                CompletionItem(
                    label = api,
                    insertText = api,
                    kind = CompletionItemKind.METHOD,
                    detail = "MagicDB Data Service API"
                )
            })

            // 添加代码片段
            completionItems.add(
                CompletionItem(
                    label = "function",
                    insertText = "function \${1:name}(\${2:params}) {\n\t\${0}\n}",
                    kind = CompletionItemKind.SNIPPET,
                    detail = "Function declaration",
                    insertTextFormat = InsertTextFormat.SNIPPET
                )
            )

            completionItems.add(
                CompletionItem(
                    label = "arrow",
                    insertText = "(\${1:params}) => {\n\t\${0}\n}",
                    kind = CompletionItemKind.SNIPPET,
                    detail = "Arrow function",
                    insertTextFormat = InsertTextFormat.SNIPPET
                )
            )

            completionItems.add(
                CompletionItem(
                    label = "if",
                    insertText = "if (\${1:condition}) {\n\t\${0}\n}",
                    kind = CompletionItemKind.SNIPPET,
                    detail = "If statement",
                    insertTextFormat = InsertTextFormat.SNIPPET
                )
            )

            completionItems.add(
                CompletionItem(
                    label = "ifelse",
                    insertText = "if (\${1:condition}) {\n\t\${2}\n} else {\n\t\${0}\n}",
                    kind = CompletionItemKind.SNIPPET,
                    detail = "If-else statement",
                    insertTextFormat = InsertTextFormat.SNIPPET
                )
            )

            completionItems.add(
                CompletionItem(
                    label = "for",
                    insertText = "for (let \${1:i} = 0; \${1:i} < \${2:array}.length; \${1:i}++) {\n\t\${0}\n}",
                    kind = CompletionItemKind.SNIPPET,
                    detail = "For loop",
                    insertTextFormat = InsertTextFormat.SNIPPET
                )
            )

            completionItems.add(
                CompletionItem(
                    label = "forin",
                    insertText = "for (const \${1:key} in \${2:object}) {\n\t\${0}\n}",
                    kind = CompletionItemKind.SNIPPET,
                    detail = "For-in loop",
                    insertTextFormat = InsertTextFormat.SNIPPET
                )
            )

            completionItems.add(
                CompletionItem(
                    label = "forof",
                    insertText = "for (const \${1:item} of \${2:array}) {\n\t\${0}\n}",
                    kind = CompletionItemKind.SNIPPET,
                    detail = "For-of loop",
                    insertTextFormat = InsertTextFormat.SNIPPET
                )
            )

            completionItems.add(
                CompletionItem(
                    label = "try",
                    insertText = "try {\n\t\${1}\n} catch (\${2:error}) {\n\t\${0}\n}",
                    kind = CompletionItemKind.SNIPPET,
                    detail = "Try-catch statement",
                    insertTextFormat = InsertTextFormat.SNIPPET
                )
            )

            return completionItems
        } catch (e: Exception) {
            logger.error("Error getting completion items for JavaScript", e)
            return emptyList()
        }
    }

    override fun getDiagnostics(language: String, script: String): List<DiagnosticItem> {
        if (language != "js" && language != "javascript") {
            return emptyList()
        }

        try {
            val diagnostics = mutableListOf<DiagnosticItem>()

            // 使用 GraalVM 解析脚本，检查语法错误
            try {
                val context = Context.newBuilder("js")
                    .option("engine.WarnInterpreterOnly", "false")
                    .build()

                val source = Source.newBuilder("js", script, "script.js").build()
                context.parse(source)
                context.close()
            } catch (e: Exception) {
                // 解析错误信息
                val errorMessage = e.message ?: "Unknown error"
                val errorPosition = extractErrorPosition(errorMessage)

                diagnostics.add(
                    DiagnosticItem(
                        message = errorMessage,
                        startPosition = errorPosition.first,
                        endPosition = errorPosition.second,
                        severity = DiagnosticSeverity.ERROR,
                        source = "JavaScript"
                    )
                )
            }

            return diagnostics
        } catch (e: Exception) {
            logger.error("Error getting diagnostics for JavaScript", e)
            return emptyList()
        }
    }

    override fun getHoverInfo(language: String, script: String, position: Int): HoverInfo? {
        if (language != "js" && language != "javascript") {
            return null
        }

        try {
            // 简单实现：查找光标位置的单词，并提供相关信息
            val word = extractWordAtPosition(script, position)

            if (word.isNotEmpty()) {
                when {
                    keywords.contains(word) -> {
                        return HoverInfo(
                            contents = listOf("JavaScript keyword: $word"),
                            rangeStart = position - word.length,
                            rangeEnd = position
                        )
                    }
                    builtInObjects.contains(word) -> {
                        return HoverInfo(
                            contents = listOf("JavaScript built-in object: $word"),
                            rangeStart = position - word.length,
                            rangeEnd = position
                        )
                    }
                    builtInFunctions.contains(word) -> {
                        return HoverInfo(
                            contents = listOf("JavaScript built-in function: $word"),
                            rangeStart = position - word.length,
                            rangeEnd = position
                        )
                    }
                    dataServiceApis.contains(word) -> {
                        return HoverInfo(
                            contents = listOf("MagicDB Data Service API: $word"),
                            rangeStart = position - word.length,
                            rangeEnd = position
                        )
                    }
                }
            }

            return null
        } catch (e: Exception) {
            logger.error("Error getting hover info for JavaScript", e)
            return null
        }
    }

    override fun formatCode(language: String, script: String): String {
        if (language != "js" && language != "javascript") {
            return script
        }

        try {
            // 简单实现：使用 GraalVM 解析和格式化代码
            // 实际应用中可能需要使用专门的格式化工具，如 prettier
            return script
        } catch (e: Exception) {
            logger.error("Error formatting JavaScript code", e)
            return script
        }
    }

    override fun getLanguageCapabilities(language: String): Map<String, Boolean> {
        if (language != "js" && language != "javascript") {
            return emptyMap()
        }

        return mapOf(
            "completion" to true,
            "diagnostics" to true,
            "hover" to true,
            "formatting" to true
        )
    }

    /**
     * 从错误消息中提取错误位置
     */
    private fun extractErrorPosition(errorMessage: String): Pair<Int, Int> {
        // 简单实现：尝试从错误消息中提取行号和列号
        val linePattern = "line (\\d+)".toRegex()
        val columnPattern = "column (\\d+)".toRegex()

        val lineMatch = linePattern.find(errorMessage)
        val columnMatch = columnPattern.find(errorMessage)

        val line = lineMatch?.groupValues?.get(1)?.toIntOrNull() ?: 1
        val column = columnMatch?.groupValues?.get(1)?.toIntOrNull() ?: 1

        // 将行号和列号转换为位置
        val position = calculatePosition(line, column)
        return Pair(position, position + 1)
    }

    /**
     * 计算位置
     */
    private fun calculatePosition(line: Int, column: Int): Int {
        // 简单实现：假设每行平均长度为 20 字符
        return (line - 1) * 20 + column - 1
    }

    /**
     * 提取光标位置的单词
     */
    private fun extractWordAtPosition(script: String, position: Int): String {
        if (position < 0 || position >= script.length) {
            return ""
        }

        var start = position
        while (start > 0 && isWordChar(script[start - 1])) {
            start--
        }

        var end = position
        while (end < script.length && isWordChar(script[end])) {
            end++
        }

        return if (start < end) script.substring(start, end) else ""
    }

    /**
     * 判断字符是否为单词字符
     */
    private fun isWordChar(c: Char): Boolean {
        return c.isLetterOrDigit() || c == '_' || c == '$'
    }
}
