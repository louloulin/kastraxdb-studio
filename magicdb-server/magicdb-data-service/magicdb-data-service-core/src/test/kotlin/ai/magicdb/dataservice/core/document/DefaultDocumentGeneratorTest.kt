package ai.magicdb.dataservice.core.document

import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.DocumentTemplate
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceParameter
import ai.magicdb.dataservice.core.entity.DocumentTemplateDO
import ai.magicdb.dataservice.core.entity.ServiceDocumentDO
import ai.magicdb.dataservice.core.mapper.DocumentTemplateMapper
import ai.magicdb.dataservice.core.mapper.ServiceDocumentMapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.UUID
import java.util.Date

/**
 * 默认文档生成器测试
 *
 * @author magicdb
 */
class DefaultDocumentGeneratorTest {

    private lateinit var documentGenerator: DefaultDocumentGenerator
    private lateinit var serviceRepository: DataServiceRepository
    private lateinit var serviceDocumentMapper: ServiceDocumentMapper
    private lateinit var documentTemplateMapper: DocumentTemplateMapper
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        serviceRepository = mock(DataServiceRepository::class.java)
        serviceDocumentMapper = mock(ServiceDocumentMapper::class.java)
        documentTemplateMapper = mock(DocumentTemplateMapper::class.java)
        objectMapper = ObjectMapper()

        documentGenerator = DefaultDocumentGenerator(
            serviceRepository,
            serviceDocumentMapper,
            documentTemplateMapper,
            objectMapper
        )
    }

    @Test
    fun testGenerateServiceDocument() {
        // 准备测试数据
        val serviceId = "service-123"
        val service = createTestService(serviceId, "测试服务")
        val template = createTestServiceTemplate()
        val templateDO = createTestServiceTemplateDO()

        // 设置模拟对象的行为
        `when`(serviceRepository.getService(serviceId)).thenReturn(service)
        `when`(documentTemplateMapper.selectDefaultByType("service")).thenReturn(templateDO)
        `when`(serviceDocumentMapper.insert(any())).thenReturn(1)

        // 执行测试
        val document = documentGenerator.generateServiceDocument(serviceId, null)

        // 验证结果
        assertNotNull(document)
        assertEquals("测试服务 - 接口文档", document.title)
        assertTrue(document.content.contains("测试服务"))
        assertEquals("markdown", document.format)
        assertEquals(serviceId, document.serviceId)

        // 验证方法调用
        verify(serviceRepository).getService(serviceId)
        verify(documentTemplateMapper).selectDefaultByType("service")
        verify(serviceDocumentMapper).insert(any())
    }

    @Test
    fun testGenerateGroupDocument() {
        // 准备测试数据
        val groupId = "group-123"
        val group = createTestGroup(groupId, "测试分组")
        val service1 = createTestService("service-123", "测试服务1", groupId)
        val service2 = createTestService("service-456", "测试服务2", groupId)
        val services = listOf(service1, service2)
        val template = createTestGroupTemplate()
        val templateDO = createTestGroupTemplateDO()

        // 设置模拟对象的行为
        `when`(serviceRepository.getGroup(groupId)).thenReturn(group)
        `when`(serviceRepository.getServicesByGroup(groupId)).thenReturn(services)
        `when`(documentTemplateMapper.selectDefaultByType("group")).thenReturn(templateDO)
        `when`(serviceDocumentMapper.insert(any())).thenReturn(1)

        // 执行测试
        val document = documentGenerator.generateGroupDocument(groupId, null)

        // 验证结果
        assertNotNull(document)
        assertEquals("测试分组 - 接口文档", document.title)
        assertTrue(document.content.contains("测试分组"))
        assertEquals("markdown", document.format)
        assertEquals(groupId, document.groupId)

        // 验证方法调用
        verify(serviceRepository).getGroup(groupId)
        verify(serviceRepository).getServicesByGroup(groupId)
        verify(documentTemplateMapper).selectDefaultByType("group")
        verify(serviceDocumentMapper).insert(any())
    }

    @Test
    fun testGenerateApiDocument() {
        // 准备测试数据
        val group1 = createTestGroup("group-123", "测试分组1")
        val group2 = createTestGroup("group-456", "测试分组2")
        val groups = listOf(group1, group2)
        val service1 = createTestService("service-123", "测试服务1", "group-123")
        val service2 = createTestService("service-456", "测试服务2", "group-123")
        val service3 = createTestService("service-789", "测试服务3", "group-456")
        val services = listOf(service1, service2, service3)
        val template = createTestApiTemplate()
        val templateDO = createTestApiTemplateDO()

        // 设置模拟对象的行为
        `when`(serviceRepository.getAllGroups()).thenReturn(groups)
        `when`(serviceRepository.getAllServices()).thenReturn(services)
        `when`(documentTemplateMapper.selectDefaultByType("api")).thenReturn(templateDO)
        `when`(serviceDocumentMapper.insert(any())).thenReturn(1)

        // 执行测试
        val document = documentGenerator.generateApiDocument(null)

        // 验证结果
        assertNotNull(document)
        assertEquals("API接口文档", document.title)
        assertTrue(document.content.contains("API接口文档"))
        assertEquals("markdown", document.format)
        assertNull(document.serviceId)
        assertNull(document.groupId)

        // 验证方法调用
        verify(serviceRepository).getAllGroups()
        verify(serviceRepository).getAllServices()
        verify(documentTemplateMapper).selectDefaultByType("api")
        verify(serviceDocumentMapper).insert(any())
    }

    @Test
    fun testGetDefaultTemplate() {
        // 准备测试数据
        val type = "service"
        val templateDO = createTestServiceTemplateDO()

        // 设置模拟对象的行为
        `when`(documentTemplateMapper.selectDefaultByType(type)).thenReturn(templateDO)

        // 执行测试
        val template = documentGenerator.getDefaultTemplate(type)

        // 验证结果
        assertNotNull(template)
        assertEquals("默认服务文档模板", template.name)
        assertEquals("service", template.type)
        assertTrue(template.isSystem)

        // 验证方法调用
        verify(documentTemplateMapper).selectDefaultByType(type)
    }

    @Test
    fun testGetDefaultTemplateNotFound() {
        // 准备测试数据
        val type = "service"

        // 设置模拟对象的行为
        `when`(documentTemplateMapper.selectDefaultByType(type)).thenReturn(null)
        `when`(documentTemplateMapper.insert(any())).thenReturn(1)

        // 执行测试
        val template = documentGenerator.getDefaultTemplate(type)

        // 验证结果
        assertNotNull(template)
        assertEquals("默认服务文档模板", template.name)
        assertEquals("service", template.type)
        assertTrue(template.isSystem)

        // 验证方法调用
        verify(documentTemplateMapper).selectDefaultByType(type)
        verify(documentTemplateMapper).insert(any())
    }

    /**
     * 创建测试服务
     */
    private fun createTestService(id: String, name: String, groupId: String? = null): DataService {
        return DataService(
            id = id,
            name = name,
            groupId = groupId,
            description = "测试服务描述",
            script = "function execute(parameters) { return { message: 'Hello, ' + parameters.name }; }",
            parameters = listOf(
                ServiceParameter(
                    name = "name",
                    type = "string",
                    required = true,
                    defaultValue = "World",
                    description = "名称参数"
                )
            ),
            createTime = Date(),
            updateTime = Date()
        )
    }

    /**
     * 创建测试分组
     */
    private fun createTestGroup(id: String, name: String): ServiceGroup {
        return ServiceGroup(
            id = id,
            name = name,
            description = "测试分组描述",
            createTime = Date(),
            updateTime = Date()
        )
    }

    /**
     * 创建测试服务模板
     */
    private fun createTestServiceTemplate(): DocumentTemplate {
        return DocumentTemplate(
            id = "template-123",
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
            createUserId = 1,
            isSystem = true,
            enabled = true,
            sort = 0,
            description = "默认服务文档模板"
        )
    }

    /**
     * 创建测试服务模板DO
     */
    private fun createTestServiceTemplateDO(): DocumentTemplateDO {
        val templateDO = DocumentTemplateDO()
        templateDO.id = "template-123"
        templateDO.name = "默认服务文档模板"
        templateDO.content = """
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
        """.trimIndent()
        templateDO.format = "markdown"
        templateDO.type = "service"
        templateDO.createTime = System.currentTimeMillis()
        templateDO.updateTime = System.currentTimeMillis()
        templateDO.createUserId = 1
        templateDO.isSystem = true
        templateDO.enabled = true
        templateDO.sort = 0
        templateDO.description = "默认服务文档模板"
        return templateDO
    }

    /**
     * 创建测试分组模板
     */
    private fun createTestGroupTemplate(): DocumentTemplate {
        return DocumentTemplate(
            id = "template-456",
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
            createUserId = 1,
            isSystem = true,
            enabled = true,
            sort = 0,
            description = "默认分组文档模板"
        )
    }

    /**
     * 创建测试分组模板DO
     */
    private fun createTestGroupTemplateDO(): DocumentTemplateDO {
        val templateDO = DocumentTemplateDO()
        templateDO.id = "template-456"
        templateDO.name = "默认分组文档模板"
        templateDO.content = """
        # {{group.name}}

        **ID**: {{group.id}}

        **描述**: {{group.description}}

        **创建时间**: {{group.createTime}}

        **更新时间**: {{group.updateTime}}

        ## 服务列表

        {{group.services}}
        """.trimIndent()
        templateDO.format = "markdown"
        templateDO.type = "group"
        templateDO.createTime = System.currentTimeMillis()
        templateDO.updateTime = System.currentTimeMillis()
        templateDO.createUserId = 1
        templateDO.isSystem = true
        templateDO.enabled = true
        templateDO.sort = 0
        templateDO.description = "默认分组文档模板"
        return templateDO
    }

    /**
     * 创建测试API模板
     */
    private fun createTestApiTemplate(): DocumentTemplate {
        return DocumentTemplate(
            id = "template-789",
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
            createUserId = 1,
            isSystem = true,
            enabled = true,
            sort = 0,
            description = "默认API文档模板"
        )
    }

    /**
     * 创建测试API模板DO
     */
    private fun createTestApiTemplateDO(): DocumentTemplateDO {
        val templateDO = DocumentTemplateDO()
        templateDO.id = "template-789"
        templateDO.name = "默认API文档模板"
        templateDO.content = """
        # API接口文档

        ## 目录

        {{api.groups}}

        ## 接口详情

        {{api.services}}
        """.trimIndent()
        templateDO.format = "markdown"
        templateDO.type = "api"
        templateDO.createTime = System.currentTimeMillis()
        templateDO.updateTime = System.currentTimeMillis()
        templateDO.createUserId = 1
        templateDO.isSystem = true
        templateDO.enabled = true
        templateDO.sort = 0
        templateDO.description = "默认API文档模板"
        return templateDO
    }
}
