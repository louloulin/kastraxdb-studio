package ai.magicdb.script.runtime

import ai.magicdb.script.api.ScriptExecutor
import ai.magicdb.script.engine.GraalVMScriptExecutor
import ai.magicdb.spi.CommandExecutor
import ai.magicdb.spi.model.Command
import ai.magicdb.spi.model.ExecuteResult
import ai.magicdb.spi.model.Header
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

/**
 * 脚本命令执行器
 *
 * @author magicdb
 */
class ScriptCommandExecutor(private val delegateExecutor: CommandExecutor) : CommandExecutor {
    private val logger = LoggerFactory.getLogger(ScriptCommandExecutor::class.java)
    private val scriptExecutor: ScriptExecutor = GraalVMScriptExecutor()

    override fun execute(command: Command): List<ExecuteResult> {
        // 检查是否是脚本命令
        return if (isScriptCommand(command)) {
            executeScript(command)
        } else {
            // 否则委托给原始执行器
            delegateExecutor.execute(command)
        }
    }

    @Throws(SQLException::class)
    override fun executeUpdate(sql: String, connection: Connection, n: Int): ExecuteResult {
        return delegateExecutor.executeUpdate(sql, connection, n)
    }

    override fun executeSelectTable(command: Command): List<ExecuteResult> {
        return delegateExecutor.executeSelectTable(command)
    }

    @Throws(SQLException::class)
    override fun execute(sql: String, connection: Connection, limitRowSize: Boolean, offset: Int?, count: Int?): ExecuteResult {
        return delegateExecutor.execute(sql, connection, limitRowSize, offset, count)
    }

    /**
     * 判断是否是脚本命令
     *
     * @param command 命令
     * @return 是否是脚本命令
     */
    private fun isScriptCommand(command: Command): Boolean {
        val script = command.script ?: return false
        
        // 检查脚本是否以语言标记开头
        return scriptExecutor.getSupportedLanguages().any { language ->
            script.trim().startsWith("#!$language") || 
            script.trim().startsWith("// $language") ||
            script.trim().startsWith("/* $language */")
        }
    }

    /**
     * 执行脚本命令
     *
     * @param command 命令
     * @return 执行结果
     */
    private fun executeScript(command: Command): List<ExecuteResult> {
        val results = mutableListOf<ExecuteResult>()
        
        try {
            // 解析脚本语言
            val script = command.script
            val language = parseLanguage(script)
            val processedScript = removeLanguageMarker(script, language)
            
            // 准备上下文
            val context = mutableMapOf<String, Any?>()
            context["command"] = command
            context["dataSourceId"] = command.dataSourceId
            context["databaseName"] = command.databaseName
            context["schemaName"] = command.schemaName
            context["tableName"] = command.tableName
            
            // 执行脚本
            val startTime = System.currentTimeMillis()
            val result = scriptExecutor.execute(language, processedScript, context)
            val duration = System.currentTimeMillis() - startTime
            
            // 构建执行结果
            val executeResult = buildExecuteResult(processedScript, language, result, duration)
            results.add(executeResult)
            
        } catch (e: Exception) {
            logger.error("执行脚本出错: {}", e.message, e)
            val errorResult = ExecuteResult.builder()
                .success(false)
                .message(e.message)
                .sql(command.script)
                .description("脚本执行出错")
                .build()
            results.add(errorResult)
        }
        
        return results
    }

    /**
     * 解析脚本语言
     *
     * @param script 脚本
     * @return 语言
     */
    private fun parseLanguage(script: String): String {
        val trimmedScript = script.trim()
        
        for (language in scriptExecutor.getSupportedLanguages()) {
            if (trimmedScript.startsWith("#!$language")) {
                return language
            }
            if (trimmedScript.startsWith("// $language")) {
                return language
            }
            if (trimmedScript.startsWith("/* $language */")) {
                return language
            }
        }
        
        throw UnsupportedOperationException("Unsupported script language")
    }

    /**
     * 移除语言标记
     *
     * @param script 脚本
     * @param language 语言
     * @return 处理后的脚本
     */
    private fun removeLanguageMarker(script: String, language: String): String {
        val trimmedScript = script.trim()
        
        return when {
            trimmedScript.startsWith("#!$language") -> 
                trimmedScript.substring(("#!$language").length).trim()
            trimmedScript.startsWith("// $language") -> 
                trimmedScript.substring(("// $language").length).trim()
            trimmedScript.startsWith("/* $language */") -> 
                trimmedScript.substring(("/* $language */").length).trim()
            else -> trimmedScript
        }
    }

    /**
     * 构建执行结果
     *
     * @param script 脚本
     * @param language 语言
     * @param result 执行结果
     * @param duration 执行时间
     * @return 执行结果
     */
    private fun buildExecuteResult(script: String, language: String, result: Any?, duration: Long): ExecuteResult {
        val builder = ExecuteResult.builder()
            .success(true)
            .sql(script)
            .description("脚本执行成功")
            .duration(duration)
        
        // 处理结果
        when (result) {
            null -> {
                builder.dataList(ArrayList())
                builder.headerList(ArrayList())
            }
            is List<*> -> {
                val listResult = result
                val dataList = ArrayList<List<String>>()
                val headerList = ArrayList<Header>()
                
                // 如果是列表结果，尝试转换为表格形式
                if (listResult.isNotEmpty()) {
                    val firstItem = listResult[0]
                    if (firstItem is Map<*, *>) {
                        // 从第一个元素提取表头
                        val firstMap = firstItem
                        for (key in firstMap.keys) {
                            val header = Header.builder()
                                .name(key.toString())
                                .dataType("STRING")
                                .build()
                            headerList.add(header)
                        }
                        
                        // 提取数据
                        for (item in listResult) {
                            if (item is Map<*, *>) {
                                val map = item
                                val row = ArrayList<String>()
                                for (key in firstMap.keys) {
                                    val value = map[key]
                                    row.add(value?.toString() ?: "null")
                                }
                                dataList.add(row)
                            }
                        }
                    } else {
                        // 简单列表，只有一列
                        val header = Header.builder()
                            .name("value")
                            .dataType("STRING")
                            .build()
                        headerList.add(header)
                        
                        for (item in listResult) {
                            val row = ArrayList<String>()
                            row.add(item?.toString() ?: "null")
                            dataList.add(row)
                        }
                    }
                }
                
                builder.dataList(dataList)
                builder.headerList(headerList)
            }
            is Map<*, *> -> {
                val mapResult = result
                val dataList = ArrayList<List<String>>()
                val headerList = ArrayList<Header>()
                
                // 从Map提取表头
                for (key in mapResult.keys) {
                    val header = Header.builder()
                        .name(key.toString())
                        .dataType("STRING")
                        .build()
                    headerList.add(header)
                }
                
                // 提取数据
                val row = ArrayList<String>()
                for (key in mapResult.keys) {
                    val value = mapResult[key]
                    row.add(value?.toString() ?: "null")
                }
                dataList.add(row)
                
                builder.dataList(dataList)
                builder.headerList(headerList)
            }
            else -> {
                // 简单值，只有一行一列
                val dataList = ArrayList<List<String>>()
                val headerList = ArrayList<Header>()
                
                val header = Header.builder()
                    .name("result")
                    .dataType("STRING")
                    .build()
                headerList.add(header)
                
                val row = ArrayList<String>()
                row.add(result.toString())
                dataList.add(row)
                
                builder.dataList(dataList)
                builder.headerList(headerList)
            }
        }
        
        return builder.build()
    }
}
