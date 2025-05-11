package ai.magicdb.dataservice.core.document

import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.DocumentGenerator
import ai.magicdb.dataservice.api.model.DocumentTemplate
import ai.magicdb.dataservice.api.model.ServiceDocument
import ai.magicdb.dataservice.core.entity.DocumentTemplateDO
import ai.magicdb.dataservice.core.entity.ServiceDocumentDO
import ai.magicdb.dataservice.core.mapper.DocumentTemplateMapper
import ai.magicdb.dataservice.core.mapper.ServiceDocumentMapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID


/**
 * 默认文档生成器实现
 *
 * @author magicdb
 */
@Service
class DefaultDocumentGenerator(
    private val serviceRepository: DataServiceRepository,
    private val serviceDocumentMapper: ServiceDocumentMapper,
    private val documentTemplateMapper: DocumentTemplateMapper,
    private val objectMapper: ObjectMapper
) : DocumentGenerator {

    private val logger = LoggerFactory.getLogger(DefaultDocumentGenerator::class.java)

    override fun generateServiceDocument(serviceId: String, templateId: String?): ServiceDocument {
        try {
            // 获取服务信息
            val service = serviceRepository.getService(serviceId)
                ?: throw IllegalArgumentException("服务不存在: $serviceId")

            // 获取模板
            val template = if (templateId != null) {
                getTemplate(templateId) ?: getDefaultTemplate("service")
            } else {
                getDefaultTemplate("service")
            }

            // 生成文档内容
            val content = generateServiceContent(service, template)

            // 创建文档
            val document = ServiceDocument(
                id = UUID.randomUUID().toString(),
                title = "${service.name} - 接口文档",
                content = content,
                format = template.format,
                serviceId = serviceId,
                createTime = System.currentTimeMillis(),
                updateTime = System.currentTimeMillis(),
                createUserId = 1, // 系统用户
                isPublic = true
            )

            // 保存文档
            saveDocument(document)

            return document
        } catch (e: Exception) {
            logger.error("生成服务文档失败: {}", serviceId, e)
            throw e
        }
    }

    override fun generateGroupDocument(groupId: String, templateId: String?): ServiceDocument {
        try {
            // 获取分组信息
            val group = serviceRepository.getGroup(groupId)
                ?: throw IllegalArgumentException("分组不存在: $groupId")

            // 获取分组下的所有服务
            val services = serviceRepository.getServicesByGroup(groupId)

            // 获取模板
            val template = if (templateId != null) {
                getTemplate(templateId) ?: getDefaultTemplate("group")
            } else {
                getDefaultTemplate("group")
            }

            // 生成文档内容
            val content = generateGroupContent(group, services, template)

            // 创建文档
            val document = ServiceDocument(
                id = UUID.randomUUID().toString(),
                title = "${group.name} - 接口文档",
                content = content,
                format = template.format,
                groupId = groupId,
                createTime = System.currentTimeMillis(),
                updateTime = System.currentTimeMillis(),
                createUserId = 1, // 系统用户
                isPublic = true
            )

            // 保存文档
            saveDocument(document)

            return document
        } catch (e: Exception) {
            logger.error("生成分组文档失败: {}", groupId, e)
            throw e
        }
    }

    override fun generateAllServiceDocuments(templateId: String?): List<ServiceDocument> {
        try {
            // 获取所有服务
            val services = serviceRepository.getAllServices()

            // 生成所有服务文档
            return services.map { generateServiceDocument(it.id, templateId) }
        } catch (e: Exception) {
            logger.error("生成所有服务文档失败", e)
            return emptyList()
        }
    }

    override fun generateAllGroupDocuments(templateId: String?): List<ServiceDocument> {
        try {
            // 获取所有分组
            val groups = serviceRepository.getAllGroups()

            // 生成所有分组文档
            return groups.map { generateGroupDocument(it.id, templateId) }
        } catch (e: Exception) {
            logger.error("生成所有分组文档失败", e)
            return emptyList()
        }
    }

    override fun generateApiDocument(templateId: String?): ServiceDocument {
        try {
            // 获取所有分组
            val groups = serviceRepository.getAllGroups()

            // 获取所有服务
            val services = serviceRepository.getAllServices()

            // 获取模板
            val template = if (templateId != null) {
                getTemplate(templateId) ?: getDefaultTemplate("api")
            } else {
                getDefaultTemplate("api")
            }

            // 生成文档内容
            val content = generateApiContent(groups, services, template)

            // 创建文档
            val document = ServiceDocument(
                id = UUID.randomUUID().toString(),
                title = "API接口文档",
                content = content,
                format = template.format,
                createTime = System.currentTimeMillis(),
                updateTime = System.currentTimeMillis(),
                createUserId = 1, // 系统用户
                isPublic = true
            )

            // 保存文档
            saveDocument(document)

            return document
        } catch (e: Exception) {
            logger.error("生成API文档失败", e)
            throw e
        }
    }

    override fun saveDocument(document: ServiceDocument): String {
        try {
            // 如果没有ID，生成一个新的ID
            if (document.id.isEmpty()) {
                document.id = UUID.randomUUID().toString()
                document.createTime = System.currentTimeMillis()
            }

            // 更新时间
            document.updateTime = System.currentTimeMillis()

            // 转换为DO对象
            val documentDO = convertToDocumentDO(document)

            // 保存或更新
            val existingDocument = serviceDocumentMapper.selectById(document.id)
            if (existingDocument == null) {
                serviceDocumentMapper.insert(documentDO)
            } else {
                serviceDocumentMapper.updateById(documentDO)
            }

            return document.id
        } catch (e: Exception) {
            logger.error("保存文档失败: {}", document.id, e)
            throw e
        }
    }

    override fun getDocument(documentId: String): ServiceDocument? {
        try {
            // 查询文档
            val documentDO = serviceDocumentMapper.selectById(documentId) ?: return null

            // 转换为模型对象
            return convertToDocument(documentDO)
        } catch (e: Exception) {
            logger.error("获取文档失败: {}", documentId, e)
            return null
        }
    }

    override fun deleteDocument(documentId: String): Boolean {
        try {
            // 删除文档
            val result = serviceDocumentMapper.deleteById(documentId)
            return result > 0
        } catch (e: Exception) {
            logger.error("删除文档失败: {}", documentId, e)
            return false
        }
    }

    override fun getServiceDocument(serviceId: String): ServiceDocument? {
        try {
            // 查询服务文档
            val documentDO = serviceDocumentMapper.selectByServiceId(serviceId) ?: return null

            // 转换为模型对象
            return convertToDocument(documentDO)
        } catch (e: Exception) {
            logger.error("获取服务文档失败: {}", serviceId, e)
            return null
        }
    }

    override fun getGroupDocument(groupId: String): ServiceDocument? {
        try {
            // 查询分组文档
            val documentDO = serviceDocumentMapper.selectByGroupId(groupId) ?: return null

            // 转换为模型对象
            return convertToDocument(documentDO)
        } catch (e: Exception) {
            logger.error("获取分组文档失败: {}", groupId, e)
            return null
        }
    }

    override fun getApiDocument(): ServiceDocument? {
        try {
            // 查询API文档
            val documentDO = serviceDocumentMapper.selectApiDocument() ?: return null

            // 转换为模型对象
            return convertToDocument(documentDO)
        } catch (e: Exception) {
            logger.error("获取API文档失败", e)
            return null
        }
    }

    override fun getAllDocuments(): List<ServiceDocument> {
        try {
            // 查询所有文档
            val documentDOs = serviceDocumentMapper.selectList(null)

            // 转换为模型对象
            return documentDOs.map { convertToDocument(it) }
        } catch (e: Exception) {
            logger.error("获取所有文档失败", e)
            return emptyList()
        }
    }

    override fun getTemplate(templateId: String): DocumentTemplate? {
        try {
            // 查询模板
            val templateDO = documentTemplateMapper.selectById(templateId) ?: return null

            // 转换为模型对象
            return convertToTemplate(templateDO)
        } catch (e: Exception) {
            logger.error("获取模板失败: {}", templateId, e)
            return null
        }
    }

    override fun saveTemplate(template: DocumentTemplate): String {
        try {
            // 如果没有ID，生成一个新的ID
            if (template.id.isEmpty()) {
                template.id = UUID.randomUUID().toString()
                template.createTime = System.currentTimeMillis()
            }

            // 更新时间
            template.updateTime = System.currentTimeMillis()

            // 转换为DO对象
            val templateDO = convertToTemplateDO(template)

            // 保存或更新
            val existingTemplate = documentTemplateMapper.selectById(template.id)
            if (existingTemplate == null) {
                documentTemplateMapper.insert(templateDO)
            } else {
                documentTemplateMapper.updateById(templateDO)
            }

            return template.id
        } catch (e: Exception) {
            logger.error("保存模板失败: {}", template.id, e)
            throw e
        }
    }

    override fun deleteTemplate(templateId: String): Boolean {
        try {
            // 删除模板
            val result = documentTemplateMapper.deleteById(templateId)
            return result > 0
        } catch (e: Exception) {
            logger.error("删除模板失败: {}", templateId, e)
            return false
        }
    }

    override fun getAllTemplates(type: String?): List<DocumentTemplate> {
        try {
            // 查询模板
            val templateDOs = if (type != null) {
                documentTemplateMapper.selectByType(type)
            } else {
                documentTemplateMapper.selectList(null)
            }

            // 转换为模型对象
            return templateDOs.map { convertToTemplate(it) }
        } catch (e: Exception) {
            logger.error("获取所有模板失败", e)
            return emptyList()
        }
    }

    override fun getDefaultTemplate(type: String): DocumentTemplate {
        try {
            // 查询默认模板
            val templateDO = documentTemplateMapper.selectDefaultByType(type)

            // 如果没有默认模板，则创建一个
            if (templateDO == null) {
                val template = createDefaultTemplate(type)
                saveTemplate(template)
                return template
            }

            // 转换为模型对象
            return convertToTemplate(templateDO)
        } catch (e: Exception) {
            logger.error("获取默认模板失败: {}", type, e)

            // 创建一个默认模板
            val template = createDefaultTemplate(type)
            try {
                saveTemplate(template)
            } catch (ex: Exception) {
                logger.error("保存默认模板失败", ex)
            }

            return template
        }
    }

    /**
     * 生成服务文档内容
     */
    private fun generateServiceContent(service: ai.magicdb.dataservice.api.model.DataService, template: DocumentTemplate): String {
        // 替换模板中的变量
        var content = template.content

        // 替换服务信息
        content = content.replace("{{service.id}}", service.id)
            .replace("{{service.name}}", service.name)
            .replace("{{service.description}}", service.description ?: "")
            .replace("{{service.script}}", service.script ?: "")
            .replace("{{service.createTime}}", service.createTime.toString())
            .replace("{{service.updateTime}}", service.updateTime.toString())

        // 替换参数信息
        val parametersContent = service.parameters.joinToString("\n") { parameter ->
            """
            | 参数名 | 类型 | 必填 | 默认值 | 描述 |
            | --- | --- | --- | --- | --- |
            | ${parameter.name} | ${parameter.type} | ${if (parameter.required) "是" else "否"} | ${parameter.defaultValue ?: ""} | ${parameter.description ?: ""} |
            """.trimIndent()
        }
        content = content.replace("{{service.parameters}}", parametersContent)

        // 替换返回值信息
        val returnContent = """
        | 字段名 | 类型 | 描述 |
        | --- | --- | --- |
        | success | Boolean | 是否成功 |
        | data | Object | 返回数据 |
        | message | String | 错误信息 |
        | duration | Long | 执行时间（毫秒） |
        """.trimIndent()
        content = content.replace("{{service.return}}", returnContent)

        return content
    }

    /**
     * 生成分组文档内容
     */
    private fun generateGroupContent(
        group: ai.magicdb.dataservice.api.model.ServiceGroup,
        services: List<ai.magicdb.dataservice.api.model.DataService>,
        template: DocumentTemplate
    ): String {
        // 替换模板中的变量
        var content = template.content

        // 替换分组信息
        content = content.replace("{{group.id}}", group.id)
            .replace("{{group.name}}", group.name)
            .replace("{{group.description}}", group.description ?: "")
            .replace("{{group.createTime}}", group.createTime.toString())
            .replace("{{group.updateTime}}", group.updateTime.toString())

        // 替换服务列表
        val servicesContent = services.joinToString("\n") { service ->
            """
            ## ${service.name}

            **ID**: ${service.id}

            **描述**: ${service.description ?: ""}

            **创建时间**: ${service.createTime}

            **更新时间**: ${service.updateTime}

            ### 参数

            | 参数名 | 类型 | 必填 | 默认值 | 描述 |
            | --- | --- | --- | --- | --- |
            ${service.parameters.joinToString("\n") { parameter ->
                "| ${parameter.name} | ${parameter.type} | ${if (parameter.required) "是" else "否"} | ${parameter.defaultValue ?: ""} | ${parameter.description ?: ""} |"
            }}

            ### 返回值

            | 字段名 | 类型 | 描述 |
            | --- | --- | --- |
            | success | Boolean | 是否成功 |
            | data | Object | 返回数据 |
            | message | String | 错误信息 |
            | duration | Long | 执行时间（毫秒） |
            """.trimIndent()
        }
        content = content.replace("{{group.services}}", servicesContent)

        return content
    }

    /**
     * 生成API文档内容
     */
    private fun generateApiContent(
        groups: List<ai.magicdb.dataservice.api.model.ServiceGroup>,
        services: List<ai.magicdb.dataservice.api.model.DataService>,
        template: DocumentTemplate
    ): String {
        // 替换模板中的变量
        var content = template.content

        // 替换分组列表
        val groupsContent = groups.joinToString("\n") { group ->
            """
            ## ${group.name}

            **ID**: ${group.id}

            **描述**: ${group.description ?: ""}

            **创建时间**: ${group.createTime}

            **更新时间**: ${group.updateTime}

            ### 服务列表

            ${services.filter { it.groupId == group.id }.joinToString("\n") { service ->
                "- [${service.name}](#${service.name.replace(" ", "-").lowercase()})"
            }}
            """.trimIndent()
        }
        content = content.replace("{{api.groups}}", groupsContent)

        // 替换服务列表
        val servicesContent = services.joinToString("\n") { service ->
            """
            ## ${service.name}

            **ID**: ${service.id}

            **分组**: ${groups.find { it.id == service.groupId }?.name ?: ""}

            **描述**: ${service.description ?: ""}

            **创建时间**: ${service.createTime}

            **更新时间**: ${service.updateTime}

            ### 参数

            | 参数名 | 类型 | 必填 | 默认值 | 描述 |
            | --- | --- | --- | --- | --- |
            ${service.parameters.joinToString("\n") { parameter ->
                "| ${parameter.name} | ${parameter.type} | ${if (parameter.required) "是" else "否"} | ${parameter.defaultValue ?: ""} | ${parameter.description ?: ""} |"
            }}

            ### 返回值

            | 字段名 | 类型 | 描述 |
            | --- | --- | --- |
            | success | Boolean | 是否成功 |
            | data | Object | 返回数据 |
            | message | String | 错误信息 |
            | duration | Long | 执行时间（毫秒） |
            """.trimIndent()
        }
        content = content.replace("{{api.services}}", servicesContent)

        return content
    }

    /**
     * 创建默认模板
     */
    private fun createDefaultTemplate(type: String): DocumentTemplate {
        return when (type) {
            "service" -> createDefaultServiceTemplate()
            "group" -> createDefaultGroupTemplate()
            "api" -> createDefaultApiTemplate()
            else -> createDefaultServiceTemplate()
        }
    }

    /**
     * 创建默认服务模板
     */
    private fun createDefaultServiceTemplate(): DocumentTemplate {
        return DocumentTemplate(
            id = UUID.randomUUID().toString(),
            name = "默认服务文档模板",
            content = """
            # {{service.name}}

            **ID**: {{service.id}}

            **描述**: {{service.description}}

            **创建时间**: {{service.createTime}}

            **更新时间**: {{service.updateTime}}

            ## 参数

            {{service.parameters}}

            ## 返回值

            {{service.return}}

            ## 示例代码

            ```javascript
            {{service.script}}
            ```
            """.trimIndent(),
            format = "markdown",
            type = "service",
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1, // 系统用户
            isSystem = true,
            enabled = true,
            sort = 0,
            description = "默认服务文档模板"
        )
    }

    /**
     * 创建默认分组模板
     */
    private fun createDefaultGroupTemplate(): DocumentTemplate {
        return DocumentTemplate(
            id = UUID.randomUUID().toString(),
            name = "默认分组文档模板",
            content = """
            # {{group.name}}

            **ID**: {{group.id}}

            **描述**: {{group.description}}

            **创建时间**: {{group.createTime}}

            **更新时间**: {{group.updateTime}}

            ## 服务列表

            {{group.services}}
            """.trimIndent(),
            format = "markdown",
            type = "group",
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1, // 系统用户
            isSystem = true,
            enabled = true,
            sort = 0,
            description = "默认分组文档模板"
        )
    }

    /**
     * 创建默认API模板
     */
    private fun createDefaultApiTemplate(): DocumentTemplate {
        return DocumentTemplate(
            id = UUID.randomUUID().toString(),
            name = "默认API文档模板",
            content = """
            # API接口文档

            ## 目录

            {{api.groups}}

            ## 接口详情

            {{api.services}}
            """.trimIndent(),
            format = "markdown",
            type = "api",
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis(),
            createUserId = 1, // 系统用户
            isSystem = true,
            enabled = true,
            sort = 0,
            description = "默认API文档模板"
        )
    }



    /**
     * 转换为文档DO对象
     */
    private fun convertToDocumentDO(document: ServiceDocument): ServiceDocumentDO {
        val documentDO = ServiceDocumentDO()
        documentDO.id = document.id
        documentDO.title = document.title
        documentDO.content = document.content
        documentDO.format = document.format
        documentDO.serviceId = document.serviceId
        documentDO.groupId = document.groupId
        documentDO.createTime = document.createTime
        documentDO.updateTime = document.updateTime
        documentDO.createUserId = document.createUserId
        documentDO.tags = if (document.tags.isEmpty()) null else objectMapper.writeValueAsString(document.tags)
        documentDO.isPublic = document.isPublic
        documentDO.sort = document.sort
        documentDO.metadata = if (document.metadata.isEmpty()) null else objectMapper.writeValueAsString(document.metadata)
        return documentDO
    }

    /**
     * 转换为文档模型对象
     */
    private fun convertToDocument(documentDO: ServiceDocumentDO): ServiceDocument {
        val document = ServiceDocument()
        document.id = documentDO.id
        document.title = documentDO.title
        document.content = documentDO.content
        document.format = documentDO.format
        document.serviceId = documentDO.serviceId
        document.groupId = documentDO.groupId
        document.createTime = documentDO.createTime ?: 0
        document.updateTime = documentDO.updateTime ?: 0
        document.createUserId = documentDO.createUserId ?: 0
        document.tags = if (documentDO.tags.isNullOrEmpty()) {
            emptyList()
        } else {
            objectMapper.readValue(documentDO.tags, List::class.java) as List<String>
        }
        document.isPublic = documentDO.isPublic ?: true
        document.sort = documentDO.sort ?: 0
        document.metadata = if (documentDO.metadata.isNullOrEmpty()) {
            emptyMap()
        } else {
            objectMapper.readValue(documentDO.metadata, Map::class.java) as Map<String, Any?>
        }
        return document
    }

    /**
     * 转换为模板DO对象
     */
    private fun convertToTemplateDO(template: DocumentTemplate): DocumentTemplateDO {
        val templateDO = DocumentTemplateDO()
        templateDO.id = template.id
        templateDO.name = template.name
        templateDO.content = template.content
        templateDO.format = template.format
        templateDO.type = template.type
        templateDO.createTime = template.createTime
        templateDO.updateTime = template.updateTime
        templateDO.createUserId = template.createUserId
        templateDO.isSystem = template.isSystem
        templateDO.enabled = template.enabled
        templateDO.sort = template.sort
        templateDO.description = template.description
        return templateDO
    }

    /**
     * 转换为模板模型对象
     */
    private fun convertToTemplate(templateDO: DocumentTemplateDO): DocumentTemplate {
        val template = DocumentTemplate()
        template.id = templateDO.id
        template.name = templateDO.name
        template.content = templateDO.content
        template.format = templateDO.format
        template.type = templateDO.type
        template.createTime = templateDO.createTime ?: 0
        template.updateTime = templateDO.updateTime ?: 0
        template.createUserId = templateDO.createUserId ?: 0
        template.isSystem = templateDO.isSystem ?: false
        template.enabled = templateDO.enabled ?: true
        template.sort = templateDO.sort ?: 0
        template.description = templateDO.description ?: ""
        return template
    }
}
