package ai.magicdb.dataservice.core.document

import ai.magicdb.dataservice.api.model.ServiceDocument
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFRun
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

/**
 * 文档导出器
 *
 * @author magicdb
 */
@Component
class DocumentExporter {
    private val logger = LoggerFactory.getLogger(DocumentExporter::class.java)
    
    /**
     * 导出格式
     */
    enum class ExportFormat {
        PDF, HTML, WORD
    }
    
    /**
     * 导出文档
     *
     * @param document 文档
     * @param format 导出格式
     * @param outputStream 输出流
     */
    fun export(document: ServiceDocument, format: ExportFormat, outputStream: OutputStream) {
        try {
            when (format) {
                ExportFormat.PDF -> exportToPdf(document, outputStream)
                ExportFormat.HTML -> exportToHtml(document, outputStream)
                ExportFormat.WORD -> exportToWord(document, outputStream)
            }
        } catch (e: Exception) {
            logger.error("导出文档失败: {}", document.id, e)
            throw e
        }
    }
    
    /**
     * 导出为PDF
     *
     * @param document 文档
     * @param outputStream 输出流
     */
    private fun exportToPdf(document: ServiceDocument, outputStream: OutputStream) {
        // 将Markdown转换为HTML
        val html = markdownToHtml(document.content)
        
        // 创建PDF文档
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        
        // 将HTML转换为PDF
        HtmlConverter.convertToPdf(
            ByteArrayInputStream(html.toByteArray(StandardCharsets.UTF_8)),
            pdfDocument
        )
        
        // 关闭文档
        pdfDocument.close()
    }
    
    /**
     * 导出为HTML
     *
     * @param document 文档
     * @param outputStream 输出流
     */
    private fun exportToHtml(document: ServiceDocument, outputStream: OutputStream) {
        // 将Markdown转换为HTML
        val html = markdownToHtml(document.content)
        
        // 添加HTML头和尾
        val fullHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>${document.title}</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        margin: 0;
                        padding: 20px;
                        color: #333;
                    }
                    h1, h2, h3, h4, h5, h6 {
                        margin-top: 24px;
                        margin-bottom: 16px;
                        font-weight: 600;
                        line-height: 1.25;
                    }
                    h1 {
                        font-size: 2em;
                        border-bottom: 1px solid #eaecef;
                        padding-bottom: 0.3em;
                    }
                    h2 {
                        font-size: 1.5em;
                        border-bottom: 1px solid #eaecef;
                        padding-bottom: 0.3em;
                    }
                    h3 {
                        font-size: 1.25em;
                    }
                    p, ul, ol {
                        margin-top: 0;
                        margin-bottom: 16px;
                    }
                    code {
                        font-family: SFMono-Regular, Consolas, Liberation Mono, Menlo, monospace;
                        padding: 0.2em 0.4em;
                        margin: 0;
                        font-size: 85%;
                        background-color: rgba(27, 31, 35, 0.05);
                        border-radius: 3px;
                    }
                    pre {
                        font-family: SFMono-Regular, Consolas, Liberation Mono, Menlo, monospace;
                        padding: 16px;
                        overflow: auto;
                        font-size: 85%;
                        line-height: 1.45;
                        background-color: #f6f8fa;
                        border-radius: 3px;
                    }
                    pre code {
                        background-color: transparent;
                        padding: 0;
                    }
                    table {
                        border-collapse: collapse;
                        width: 100%;
                        margin-bottom: 16px;
                    }
                    table th, table td {
                        padding: 6px 13px;
                        border: 1px solid #dfe2e5;
                    }
                    table tr {
                        background-color: #fff;
                        border-top: 1px solid #c6cbd1;
                    }
                    table tr:nth-child(2n) {
                        background-color: #f6f8fa;
                    }
                    blockquote {
                        padding: 0 1em;
                        color: #6a737d;
                        border-left: 0.25em solid #dfe2e5;
                        margin: 0 0 16px 0;
                    }
                </style>
            </head>
            <body>
                $html
            </body>
            </html>
        """.trimIndent()
        
        // 写入输出流
        outputStream.write(fullHtml.toByteArray(StandardCharsets.UTF_8))
    }
    
    /**
     * 导出为Word
     *
     * @param document 文档
     * @param outputStream 输出流
     */
    private fun exportToWord(document: ServiceDocument, outputStream: OutputStream) {
        // 创建Word文档
        val wordDocument = XWPFDocument()
        
        // 添加标题
        val titleParagraph = wordDocument.createParagraph()
        val titleRun = titleParagraph.createRun()
        titleRun.setText(document.title)
        titleRun.isBold = true
        titleRun.fontSize = 16
        
        // 解析Markdown内容
        val parser = Parser.builder().build()
        val node = parser.parse(document.content)
        
        // 简单处理Markdown内容（这里只是简单示例，实际应用中需要更复杂的处理）
        val lines = document.content.split("\n")
        for (line in lines) {
            val paragraph = wordDocument.createParagraph()
            val run = paragraph.createRun()
            
            // 简单处理标题
            if (line.startsWith("# ")) {
                run.setText(line.substring(2))
                run.isBold = true
                run.fontSize = 14
            } else if (line.startsWith("## ")) {
                run.setText(line.substring(3))
                run.isBold = true
                run.fontSize = 13
            } else if (line.startsWith("### ")) {
                run.setText(line.substring(4))
                run.isBold = true
                run.fontSize = 12
            } else {
                run.setText(line)
                run.fontSize = 11
            }
        }
        
        // 保存文档
        wordDocument.write(outputStream)
        wordDocument.close()
    }
    
    /**
     * 将Markdown转换为HTML
     *
     * @param markdown Markdown内容
     * @return HTML内容
     */
    private fun markdownToHtml(markdown: String): String {
        val parser = Parser.builder().build()
        val document = parser.parse(markdown)
        val renderer = HtmlRenderer.builder().build()
        return renderer.render(document)
    }
}
