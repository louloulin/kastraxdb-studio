package ai.magicdb.dataservice.core.document

/**
 * 导出格式
 *
 * @author magicdb
 */
enum class ExportFormat(val extension: String, val contentType: String) {
    MARKDOWN("md", "text/markdown"),
    HTML("html", "text/html"),
    PDF("pdf", "application/pdf"),
    WORD("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    
    companion object {
        /**
         * 根据格式名称获取导出格式
         *
         * @param format 格式名称
         * @return 导出格式
         */
        fun fromString(format: String): ExportFormat {
            return when (format.lowercase()) {
                "markdown", "md" -> MARKDOWN
                "html" -> HTML
                "pdf" -> PDF
                "word", "docx" -> WORD
                else -> MARKDOWN
            }
        }
    }
}
