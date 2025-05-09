package ai.magicdb.script.runtime.example

import ai.magicdb.script.api.ScriptManager
import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptGroup
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

/**
 * 示例脚本加载器
 *
 * @author magicdb
 */
@Component
@Profile("dev")
class ExampleScriptLoader(private val scriptManager: ScriptManager) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(ExampleScriptLoader::class.java)
    
    override fun run(vararg args: String) {
        loadExampleScripts()
    }
    
    /**
     * 加载示例脚本
     */
    fun loadExampleScripts() {
        logger.info("开始加载示例脚本...")
        
        try {
            // 创建示例分组
            createExampleGroups()
            
            // 加载JavaScript示例
            loadScriptsFromDirectory("examples/javascript", "js", "javascript-examples")
            
            // 加载Kotlin示例
            loadScriptsFromDirectory("examples/kotlin", "kotlin", "kotlin-examples")
            
            // 加载Python示例
            loadScriptsFromDirectory("examples/python", "python", "python-examples")
            
            logger.info("示例脚本加载完成")
        } catch (e: Exception) {
            logger.error("加载示例脚本出错: {}", e.message, e)
        }
    }
    
    /**
     * 创建示例分组
     */
    private fun createExampleGroups() {
        // 创建根分组
        val rootGroup = ScriptGroup(
            id = "examples",
            name = "示例脚本",
            description = "MagicDB示例脚本集合"
        )
        scriptManager.saveGroup(rootGroup)
        
        // 创建JavaScript分组
        val jsGroup = ScriptGroup(
            id = "javascript-examples",
            name = "JavaScript示例",
            description = "JavaScript示例脚本",
            parentId = "examples"
        )
        scriptManager.saveGroup(jsGroup)
        
        // 创建Kotlin分组
        val kotlinGroup = ScriptGroup(
            id = "kotlin-examples",
            name = "Kotlin示例",
            description = "Kotlin示例脚本",
            parentId = "examples"
        )
        scriptManager.saveGroup(kotlinGroup)
        
        // 创建Python分组
        val pythonGroup = ScriptGroup(
            id = "python-examples",
            name = "Python示例",
            description = "Python示例脚本",
            parentId = "examples"
        )
        scriptManager.saveGroup(pythonGroup)
    }
    
    /**
     * 从目录加载脚本
     *
     * @param directory 目录
     * @param language 语言
     * @param groupId 分组ID
     */
    private fun loadScriptsFromDirectory(directory: String, language: String, groupId: String) {
        val resolver = PathMatchingResourcePatternResolver()
        val resources = resolver.getResources("classpath:$directory/**")
        
        for (resource in resources) {
            try {
                loadScript(resource, language, groupId)
            } catch (e: Exception) {
                logger.error("加载脚本出错: {}", e.message, e)
            }
        }
    }
    
    /**
     * 加载脚本
     *
     * @param resource 资源
     * @param language 语言
     * @param groupId 分组ID
     */
    private fun loadScript(resource: Resource, language: String, groupId: String) {
        val filename = resource.filename ?: return
        if (!resource.isReadable) return
        
        // 读取脚本内容
        val content = resource.inputStream.readAllBytes().toString(StandardCharsets.UTF_8)
        
        // 解析脚本名称和描述
        val name = filename.substringBeforeLast(".")
            .split("-")
            .joinToString(" ") { it.capitalize() }
        
        val description = parseDescription(content)
        
        // 创建脚本
        val script = Script(
            name = name,
            content = content,
            language = language,
            description = description,
            groupId = groupId,
            tags = listOf("example", language)
        )
        
        // 保存脚本
        val savedScript = scriptManager.saveScript(script)
        logger.info("加载示例脚本: {}", savedScript.name)
    }
    
    /**
     * 解析脚本描述
     *
     * @param content 脚本内容
     * @return 描述
     */
    private fun parseDescription(content: String): String {
        // 尝试从注释中提取描述
        val lines = content.lines()
        val commentLines = mutableListOf<String>()
        
        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.startsWith("//") || trimmedLine.startsWith("#")) {
                val comment = trimmedLine.substring(2).trim()
                if (comment.isNotBlank()) {
                    commentLines.add(comment)
                }
            } else if (commentLines.isNotEmpty()) {
                break
            }
        }
        
        return if (commentLines.isNotEmpty()) {
            commentLines.joinToString("\n")
        } else {
            "示例脚本"
        }
    }
}
