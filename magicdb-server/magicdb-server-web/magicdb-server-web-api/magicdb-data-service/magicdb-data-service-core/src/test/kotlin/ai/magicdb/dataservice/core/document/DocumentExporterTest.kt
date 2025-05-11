package ai.magicdb.dataservice.core.document

import ai.magicdb.dataservice.api.model.ServiceDocument
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

/**
 * 文档导出器测试
 *
 * @author magicdb
 */
class DocumentExporterTest {
    
    private lateinit var documentExporter: DocumentExporter
    private lateinit var testDocument: ServiceDocument
    
    @BeforeEach
    fun setUp() {
        documentExporter = DocumentExporter()
        
        // 创建测试文档
        testDocument = ServiceDocument(
            id = "test-doc-id",
            title = "测试文档",
            content = """
                # 测试文档
                
                这是一个测试文档，用于测试文档导出功能。
                
                ## 章节一
                
                这是章节一的内容。
                
                ## 章节二
                
                这是章节二的内容。
                
                ### 子章节
                
                这是子章节的内容。
                
                ## 表格示例
                
                | 名称 | 类型 | 描述 |
                | --- | --- | --- |
                | id | string | 唯一标识符 |
                | name | string | 名称 |
                | age | number | 年龄 |
                
                ## 代码示例
                
                ```kotlin
                fun hello() {
                    println("Hello, World!")
                }
                ```
            """.trimIndent(),
            format = "markdown",
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1,
            isPublic = true
        )
    }
    
    @Test
    fun testExportToPdf() {
        // 准备输出流
        val outputStream = ByteArrayOutputStream()
        
        // 执行导出
        documentExporter.export(testDocument, DocumentExporter.ExportFormat.PDF, outputStream)
        
        // 验证结果
        val bytes = outputStream.toByteArray()
        assertNotNull(bytes)
        assertTrue(bytes.isNotEmpty())
        
        // 验证PDF文件头
        assertTrue(bytes.size > 4)
        assertTrue(bytes[0] == '%'.code.toByte())
        assertTrue(bytes[1] == 'P'.code.toByte())
        assertTrue(bytes[2] == 'D'.code.toByte())
        assertTrue(bytes[3] == 'F'.code.toByte())
    }
    
    @Test
    fun testExportToHtml() {
        // 准备输出流
        val outputStream = ByteArrayOutputStream()
        
        // 执行导出
        documentExporter.export(testDocument, DocumentExporter.ExportFormat.HTML, outputStream)
        
        // 验证结果
        val html = outputStream.toString("UTF-8")
        assertNotNull(html)
        assertTrue(html.isNotEmpty())
        
        // 验证HTML内容
        assertTrue(html.contains("<!DOCTYPE html>"))
        assertTrue(html.contains("<title>测试文档</title>"))
        assertTrue(html.contains("<h1>测试文档</h1>"))
        assertTrue(html.contains("<h2>章节一</h2>"))
        assertTrue(html.contains("<table>"))
        assertTrue(html.contains("<code>"))
    }
    
    @Test
    fun testExportToWord() {
        // 准备输出流
        val outputStream = ByteArrayOutputStream()
        
        // 执行导出
        documentExporter.export(testDocument, DocumentExporter.ExportFormat.WORD, outputStream)
        
        // 验证结果
        val bytes = outputStream.toByteArray()
        assertNotNull(bytes)
        assertTrue(bytes.isNotEmpty())
        
        // 验证Word文件大小（简单验证，不检查具体内容）
        assertTrue(bytes.size > 1000)
    }
}
