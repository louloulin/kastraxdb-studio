package ai.magicdb.dataservice.core.document

import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.DocumentGenerator
import ai.magicdb.dataservice.api.model.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 默认文档生成器
 *
 * @author magicdb
 */
@Component
class DefaultDocumentGenerator(
    private val dataServiceRepository: DataServiceRepository,
    private val exampleGenerator: ExampleGenerator
) : DocumentGenerator {

    private val logger = LoggerFactory.getLogger(DefaultDocumentGenerator::class.java)

    // 模板存储
    private val templates = ConcurrentHashMap<String, DocumentTemplate>()

    init {
        // 添加默认模板
        val defaultTemplate = DocumentTemplate(
            id = "default",
            name = "默认模板",
            description = "默认文档模板",
            titleTemplate = "{{service.name}} API文档",
            descriptionTemplate = "{{service.description}}",
            parameterTemplates = listOf(
                ParameterTemplate(
                    namePattern = "*",
                    type = "string",
                    required = false,
                    description = "参数描述"
                )
            ),
            returnFieldTemplates = listOf(
                FieldTemplate(
                    namePattern = "*",
                    type = "string",
                    description = "字段描述"
                )
            ),
            notesTemplate = "请注意以下事项：\n1. 调用前请确保有足够的权限\n2. 请勿频繁调用此接口"
        )

        templates[defaultTemplate.id] = defaultTemplate
    }

    override fun generateDocument(serviceId: String): ServiceDocument {
        return generateDocument(serviceId, templates["default"]!!)
    }

    override fun generateDocument(serviceId: String, template: DocumentTemplate): ServiceDocument {
        return generateServiceDocument(serviceId, template)
    }

    override fun generateServiceDocument(serviceId: String, templateId: String?): ServiceDocument {
        val template = if (templateId != null) {
            getTemplate(templateId) ?: getDefaultTemplate()
        } else {
            getDefaultTemplate()
        }
        return generateDocument(serviceId, template)
    }

    override fun generateGroupDocument(groupId: String, templateId: String?): ServiceDocument {
        // 实际应该实现分组文档生成逻辑
        // 这里简单返回一个示例文档
        return ServiceDocument(
            title = "分组文档",
            description = "分组ID: $groupId"
        )
    }

    override fun generateApiDocument(apiId: String, templateId: String?): ServiceDocument {
        // 实际应该实现API文档生成逻辑
        // 这里简单返回一个示例文档
        return ServiceDocument(
            title = "API文档",
            description = "API ID: $apiId"
        )
    }

    override fun getDocument(documentId: String): ServiceDocument? {
        // 实际应该从数据库中获取文档
        // 这里简单返回一个示例文档
        return ServiceDocument(
            title = "文档",
            description = "文档ID: $documentId"
        )
    }

    override fun getServiceDocument(serviceId: String): ServiceDocument? {
        // 实际应该从数据库中获取服务文档
        // 这里简单返回生成的文档
        return generateDocument(serviceId)
    }

    override fun getGroupDocument(groupId: String): ServiceDocument? {
        // 实际应该从数据库中获取分组文档
        // 这里简单返回生成的文档
        return generateGroupDocument(groupId, null)
    }

    override fun getApiDocument(apiId: String): ServiceDocument? {
        // 实际应该从数据库中获取API文档
        // 这里简单返回生成的文档
        return generateApiDocument(apiId, null)
    }

    override fun getAllDocuments(type: String?): List<ServiceDocument> {
        // 实际应该从数据库中获取所有文档
        // 这里简单返回一个示例文档列表
        return listOf(
            ServiceDocument(
                title = "文档 1",
                description = "示例文档 1"
            ),
            ServiceDocument(
                title = "文档 2",
                description = "示例文档 2"
            )
        )
    }

    override fun saveDocument(document: ServiceDocument): String {
        // 实际应该将文档保存到数据库
        // 这里简单返回一个示例文档ID
        return "doc-" + System.currentTimeMillis()
    }

    override fun deleteDocument(documentId: String): Boolean {
        // 实际应该从数据库中删除文档
        // 这里简单返回成功
        return true
    }

    override fun getDefaultTemplate(): DocumentTemplate {
        return templates["default"]!!
    }

    override fun generateExampleDocument(type: String): ServiceDocument {
        // 生成示例文档
        return when (type) {
            "service" -> generateExampleService()
            "group" -> generateExampleGroup()
            else -> ServiceDocument(
                title = "示例文档",
                description = "示例文档描述"
            )
        }
    }

    override fun generateExampleService(): ServiceDocument {
        // 生成示例服务文档
        return ServiceDocument(
            title = "示例服务文档",
            description = "这是一个示例服务文档",
            parameters = listOf(
                ParameterDocument(
                    name = "param1",
                    type = "string",
                    required = true,
                    description = "参数 1"
                ),
                ParameterDocument(
                    name = "param2",
                    type = "number",
                    required = false,
                    defaultValue = "0",
                    description = "参数 2"
                )
            ),
            returnFields = listOf(
                FieldDocument(
                    name = "result",
                    type = "object",
                    description = "返回结果"
                ),
                FieldDocument(
                    name = "message",
                    type = "string",
                    description = "返回消息"
                )
            ),
            examples = listOf(
                DocumentExample(
                    id = "example-1",
                    description = "示例 1",
                    requestParams = "{\"param1\": \"value1\", \"param2\": 123}",
                    responseData = "{\"result\": {\"id\": 1, \"name\": \"test\"}, \"message\": \"success\"}"
                )
            ),
            notes = "这是一个示例服务文档，仅供参考。"
        )
    }

    override fun generateExampleGroup(): ServiceDocument {
        // 生成示例分组文档
        return ServiceDocument(
            title = "示例分组文档",
            description = "这是一个示例分组文档",
            notes = "这是一个示例分组文档，仅供参考。"
        )
    }

    private fun generateServiceDocument(serviceId: String, template: DocumentTemplate): ServiceDocument {
        try {
            // 获取服务
            val service = dataServiceRepository.getService(serviceId)
            if (service == null) {
                logger.warn("服务不存在: {}", serviceId)
                return ServiceDocument(
                    title = "服务不存在",
                    description = "服务ID: $serviceId"
                )
            }

            // 生成标题
            val titleTemplate = template.titleTemplate
            val title = if (titleTemplate != null) {
                titleTemplate.replace("{{service.name}}", service.name)
            } else {
                service.name
            }

            // 生成描述
            val description = template.descriptionTemplate?.replace("{{service.description}}", service.description ?: "")
                ?: service.description

            // 生成参数文档
            val parameters = mutableListOf<ParameterDocument>()

            // 根据服务类型生成不同的参数文档
            when (service.type) {
                "SQL" -> {
                    parameters.add(
                        ParameterDocument(
                            name = "sql",
                            type = "string",
                            required = true,
                            description = "SQL查询语句"
                        )
                    )
                    parameters.add(
                        ParameterDocument(
                            name = "dataSourceId",
                            type = "string",
                            required = true,
                            description = "数据源ID"
                        )
                    )
                }
                "HTTP" -> {
                    parameters.add(
                        ParameterDocument(
                            name = "url",
                            type = "string",
                            required = true,
                            description = "请求URL"
                        )
                    )
                    parameters.add(
                        ParameterDocument(
                            name = "method",
                            type = "string",
                            required = true,
                            defaultValue = "GET",
                            description = "请求方法，如GET、POST等"
                        )
                    )
                    parameters.add(
                        ParameterDocument(
                            name = "headers",
                            type = "object",
                            required = false,
                            description = "请求头"
                        )
                    )
                    parameters.add(
                        ParameterDocument(
                            name = "body",
                            type = "string",
                            required = false,
                            description = "请求体"
                        )
                    )
                }
                else -> {
                    // 默认参数
                    parameters.add(
                        ParameterDocument(
                            name = "param1",
                            type = "string",
                            required = false,
                            description = "参数1"
                        )
                    )
                    parameters.add(
                        ParameterDocument(
                            name = "param2",
                            type = "string",
                            required = false,
                            description = "参数2"
                        )
                    )
                }
            }

            // 生成返回字段文档
            val returnFields = mutableListOf<FieldDocument>()

            // 根据服务类型生成不同的返回字段文档
            when (service.type) {
                "SQL" -> {
                    returnFields.add(
                        FieldDocument(
                            name = "data",
                            type = "array",
                            description = "查询结果数据"
                        )
                    )
                    returnFields.add(
                        FieldDocument(
                            name = "total",
                            type = "number",
                            description = "总记录数"
                        )
                    )
                }
                "HTTP" -> {
                    returnFields.add(
                        FieldDocument(
                            name = "statusCode",
                            type = "number",
                            description = "HTTP状态码"
                        )
                    )
                    returnFields.add(
                        FieldDocument(
                            name = "headers",
                            type = "object",
                            description = "响应头"
                        )
                    )
                    returnFields.add(
                        FieldDocument(
                            name = "body",
                            type = "string",
                            description = "响应体"
                        )
                    )
                }
                else -> {
                    // 默认返回字段
                    returnFields.add(
                        FieldDocument(
                            name = "result",
                            type = "object",
                            description = "返回结果"
                        )
                    )
                }
            }

            // 生成示例
            val examples = exampleGenerator.generateExamples(service, 2)

            // 生成注意事项
            val notes = template.notesTemplate

            return ServiceDocument(
                title = title,
                description = description,
                parameters = parameters,
                returnFields = returnFields,
                examples = examples,
                notes = notes
            )
        } catch (e: Exception) {
            logger.error("生成文档失败: {}", serviceId, e)
            return ServiceDocument(
                title = "文档生成失败",
                description = "生成文档时发生错误: ${e.message}"
            )
        }
    }

    override fun saveTemplate(template: DocumentTemplate): String {
        val templateId = template.id.ifBlank { UUID.randomUUID().toString() }
        val newTemplate = template.copy(
            id = templateId,
            updateTime = System.currentTimeMillis()
        )

        templates[templateId] = newTemplate
        logger.info("保存文档模板: {}", templateId)

        return templateId
    }

    override fun getTemplate(templateId: String): DocumentTemplate? {
        return templates[templateId]
    }

    override fun getAllTemplates(): List<DocumentTemplate> {
        return templates.values.toList()
    }

    override fun deleteTemplate(templateId: String): Boolean {
        if (templateId == "default") {
            logger.warn("不能删除默认模板")
            return false
        }

        val removed = templates.remove(templateId)
        logger.info("删除文档模板: {}, 结果: {}", templateId, removed != null)

        return removed != null
    }
}
