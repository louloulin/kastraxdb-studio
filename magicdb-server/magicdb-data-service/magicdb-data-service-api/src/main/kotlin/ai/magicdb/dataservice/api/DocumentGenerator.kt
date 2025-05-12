package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.DocumentTemplate
import ai.magicdb.dataservice.api.model.ServiceDocument

/**
 * 文档生成器接口
 *
 * @author magicdb
 */
interface DocumentGenerator {

    /**
     * 生成服务文档
     *
     * @param serviceId 服务ID
     * @return 服务文档
     */
    fun generateDocument(serviceId: String): ServiceDocument

    /**
     * 生成服务文档
     *
     * @param serviceId 服务ID
     * @param template 文档模板
     * @return 服务文档
     */
    fun generateDocument(serviceId: String, template: DocumentTemplate): ServiceDocument

    /**
     * 生成服务文档
     *
     * @param serviceId 服务ID
     * @param templateId 模板ID
     * @return 服务文档
     */
    fun generateServiceDocument(serviceId: String, templateId: String?): ServiceDocument

    /**
     * 生成分组文档
     *
     * @param groupId 分组ID
     * @param templateId 模板ID
     * @return 服务文档
     */
    fun generateGroupDocument(groupId: String, templateId: String?): ServiceDocument

    /**
     * 生成API文档
     *
     * @param apiId API ID
     * @param templateId 模板ID
     * @return 服务文档
     */
    fun generateApiDocument(apiId: String, templateId: String?): ServiceDocument

    /**
     * 获取文档
     *
     * @param documentId 文档ID
     * @return 服务文档
     */
    fun getDocument(documentId: String): ServiceDocument?

    /**
     * 获取服务文档
     *
     * @param serviceId 服务ID
     * @return 服务文档
     */
    fun getServiceDocument(serviceId: String): ServiceDocument?

    /**
     * 获取分组文档
     *
     * @param groupId 分组ID
     * @return 服务文档
     */
    fun getGroupDocument(groupId: String): ServiceDocument?

    /**
     * 获取API文档
     *
     * @param apiId API ID
     * @return 服务文档
     */
    fun getApiDocument(apiId: String): ServiceDocument?

    /**
     * 获取所有文档
     *
     * @param type 文档类型
     * @return 文档列表
     */
    fun getAllDocuments(type: String?): List<ServiceDocument>

    /**
     * 保存文档
     *
     * @param document 文档
     * @return 文档ID
     */
    fun saveDocument(document: ServiceDocument): String

    /**
     * 删除文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    fun deleteDocument(documentId: String): Boolean

    /**
     * 保存文档模板
     *
     * @param template 文档模板
     * @return 模板ID
     */
    fun saveTemplate(template: DocumentTemplate): String

    /**
     * 获取文档模板
     *
     * @param templateId 模板ID
     * @return 文档模板
     */
    fun getTemplate(templateId: String): DocumentTemplate?

    /**
     * 获取所有文档模板
     *
     * @return 文档模板列表
     */
    fun getAllTemplates(): List<DocumentTemplate>

    /**
     * 获取默认模板
     *
     * @return 默认模板
     */
    fun getDefaultTemplate(): DocumentTemplate

    /**
     * 删除文档模板
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    fun deleteTemplate(templateId: String): Boolean

    /**
     * 生成示例文档
     *
     * @param type 文档类型
     * @return 示例文档
     */
    fun generateExampleDocument(type: String): ServiceDocument

    /**
     * 生成示例服务
     *
     * @return 示例服务
     */
    fun generateExampleService(): ServiceDocument

    /**
     * 生成示例分组
     *
     * @return 示例分组
     */
    fun generateExampleGroup(): ServiceDocument
}
