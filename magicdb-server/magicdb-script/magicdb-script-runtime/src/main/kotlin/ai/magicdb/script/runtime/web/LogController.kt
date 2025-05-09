package ai.magicdb.script.runtime.web

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * 日志查看控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/log")
class LogController {
    private val logger = LoggerFactory.getLogger(LogController::class.java)
    
    // 日志目录
    private val logDir = System.getProperty("user.dir") + "/logs"

    /**
     * 获取日志文件列表
     */
    @GetMapping
    fun getLogFileList(): ResponseEntity<List<LogFile>> {
        logger.info("获取日志文件列表")
        
        val logDirFile = File(logDir)
        if (!logDirFile.exists() || !logDirFile.isDirectory) {
            return ResponseEntity.ok(emptyList())
        }
        
        val logFiles = logDirFile.listFiles()
            ?.filter { it.isFile && it.name.endsWith(".log") }
            ?.map {
                LogFile(
                    name = it.name,
                    path = it.absolutePath,
                    size = it.length(),
                    lastModified = Date(it.lastModified())
                )
            }
            ?.sortedByDescending { it.lastModified }
            ?: emptyList()
        
        return ResponseEntity.ok(logFiles)
    }

    /**
     * 获取日志文件内容
     */
    @GetMapping("/{fileName}")
    fun getLogFileContent(
        @PathVariable fileName: String,
        @RequestParam(required = false, defaultValue = "1000") lines: Int
    ): ResponseEntity<LogContent> {
        logger.info("获取日志文件内容: {}, lines: {}", fileName, lines)
        
        val logFile = File("$logDir/$fileName")
        if (!logFile.exists() || !logFile.isFile) {
            return ResponseEntity.notFound().build()
        }
        
        // 读取日志文件最后N行
        val content = readLastNLines(logFile, lines)
        
        return ResponseEntity.ok(
            LogContent(
                fileName = fileName,
                content = content,
                size = logFile.length(),
                lastModified = Date(logFile.lastModified())
            )
        )
    }

    /**
     * 读取文件最后N行
     */
    private fun readLastNLines(file: File, n: Int): String {
        val lines = mutableListOf<String>()
        
        try {
            val allLines = Files.readAllLines(Paths.get(file.absolutePath))
            val startIndex = maxOf(0, allLines.size - n)
            
            for (i in startIndex until allLines.size) {
                lines.add(allLines[i])
            }
        } catch (e: Exception) {
            logger.error("读取日志文件出错: {}", e.message, e)
            return "读取日志文件出错: ${e.message}"
        }
        
        return lines.joinToString("\n")
    }
}

/**
 * 日志文件
 */
data class LogFile(
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: Date
)

/**
 * 日志内容
 */
data class LogContent(
    val fileName: String,
    val content: String,
    val size: Long,
    val lastModified: Date
)
