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
     * @param templateId 模板ID，如果为null则使用默认模板
     * @return 服务文档
     */
    fun generateServiceDocument(serviceId: String, templateId: String? = null): ServiceDocument
    
    /**
     * 生成分组文档
     *
     * @param groupId 分组ID
     * @param templateId 模板ID，如果为null则使用默认模板
     * @return 分组文档
     */
    fun generateGroupDocument(groupId: String, templateId: String? = null): ServiceDocument
    
    /**
     * 生成所有服务文档
     *
     * @param templateId 模板ID，如果为null则使用默认模板
     * @return 服务文档列表
     */
    fun generateAllServiceDocuments(templateId: String? = null): List<ServiceDocument>
    
    /**
     * 生成所有分组文档
     *
     * @param templateId 模板ID，如果为null则使用默认模板
     * @return 分组文档列表
     */
    fun generateAllGroupDocuments(templateId: String? = null): List<ServiceDocument>
    
    /**
     * 生成API文档
     *
     * @param templateId 模板ID，如果为null则使用默认模板
     * @return API文档
     */
    fun generateApiDocument(templateId: String? = null): ServiceDocument
    
    /**
     * 保存文档
     *
     * @param document 文档
     * @return 文档ID
     */
    fun saveDocument(document: ServiceDocument): String
    
    /**
     * 获取文档
     *
     * @param documentId 文档ID
     * @return 文档
     */
    fun getDocument(documentId: String): ServiceDocument?
    
    /**
     * 删除文档
     *
     * @param documentId 文档ID
     * @return 是否成功
     */
    fun deleteDocument(documentId: String): Boolean
    
    /**
     * 获取服务文档
     *
     * @param serviceId 服务ID
     * @return 文档
     */
    fun getServiceDocument(serviceId: String): ServiceDocument?
    
    /**
     * 获取分组文档
     *
     * @param groupId 分组ID
     * @return 文档
     */
    fun getGroupDocument(groupId: String): ServiceDocument?
    
    /**
     * 获取API文档
     *
     * @return 文档
     */
    fun getApiDocument(): ServiceDocument?
    
    /**
     * 获取所有文档
     *
     * @return 文档列表
     */
    fun getAllDocuments(): List<ServiceDocument>
    
    /**
     * 获取文档模板
     *
     * @param templateId 模板ID
     * @return 模板
     */
    fun getTemplate(templateId: String): DocumentTemplate?
    
    /**
     * 保存文档模板
     *
     * @param template 模板
     * @return 模板ID
     */
    fun saveTemplate(template: DocumentTemplate): String
    
    /**
     * 删除文档模板
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    fun deleteTemplate(templateId: String): Boolean
    
    /**
     * 获取所有文档模板
     *
     * @param type 模板类型，如果为null则获取所有类型
     * @return 模板列表
     */
    fun getAllTemplates(type: String? = null): List<DocumentTemplate>
    
    /**
     * 获取默认文档模板
     *
     * @param type 模板类型
     * @return 默认模板
     */
    fun getDefaultTemplate(type: String): DocumentTemplate
}
