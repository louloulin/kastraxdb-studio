package ai.magicdb.data.service.core.document

import org.springframework.stereotype.Component

/**
 * 文档导出器
 * 用于将文档导出为不同格式
 *
 * @author magicdb
 */
@Component
class DocumentExporter {

    /**
     * 导出文档
     *
     * @param documentId 文档ID
     * @param format 导出格式
     * @return 导出的文档内容
     */
    fun export(documentId: String, format: ExportFormat): ByteArray {
        // 实现导出文档的逻辑
        return ByteArray(0)
    }
}
