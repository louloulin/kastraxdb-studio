package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceDocument

/**
 * 文档生成器接口
 */
interface DocumentGenerator {
    /**
     * 生成服务文档
     *
     * @param service 数据服务
     * @return 服务文档
     */
    fun generateDocument(service: DataService): ServiceDocument
    
    /**
     * 生成 OpenAPI 规范文档
     *
     * @param service 数据服务
     * @return OpenAPI 规范 JSON 字符串
     */
    fun generateOpenApiSpec(service: DataService): String
    
    /**
     * 生成 Markdown 格式文档
     *
     * @param service 数据服务
     * @return Markdown 格式文档
     */
    fun generateMarkdownDoc(service: DataService): String
    
    /**
     * 生成 HTML 格式文档
     *
     * @param service 数据服务
     * @return HTML 格式文档
     */
    fun generateHtmlDoc(service: DataService): String
    
    /**
     * 生成 PDF 格式文档
     *
     * @param service 数据服务
     * @return PDF 文件的字节数组
     */
    fun generatePdfDoc(service: DataService): ByteArray
}
