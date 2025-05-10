package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceParameter
import ai.magicdb.dataservice.core.converter.DataServiceConverter
import ai.magicdb.dataservice.core.converter.ServiceGroupConverter
import ai.magicdb.dataservice.core.entity.DataServiceDO
import ai.magicdb.dataservice.core.entity.ServiceGroupDO
import ai.magicdb.dataservice.core.entity.ServiceParameterDO
import ai.magicdb.dataservice.core.entity.ServiceTagDO
import ai.magicdb.dataservice.core.mapper.*
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

/**
 * MyBatis数据服务存储测试
 *
 * @author magicdb
 */
@ExtendWith(MockitoExtension::class)
class MybatisDataServiceRepositoryTest {

    @Mock
    private lateinit var dataServiceMapper: DataServiceMapper

    @Mock
    private lateinit var serviceGroupMapper: ServiceGroupMapper

    @Mock
    private lateinit var serviceParameterMapper: ServiceParameterMapper

    @Mock
    private lateinit var serviceHistoryMapper: ServiceHistoryMapper

    @Mock
    private lateinit var serviceTagMapper: ServiceTagMapper

    @Mock
    private lateinit var dataServiceConverter: DataServiceConverter

    @Mock
    private lateinit var serviceGroupConverter: ServiceGroupConverter

    @InjectMocks
    private lateinit var repository: MybatisDataServiceRepository

    private val testServiceId = "test-service"
    private val testGroupId = "test-group"

    @BeforeEach
    fun setUp() {
        // 设置模拟对象的行为
    }

    @Test
    fun testSaveService() {
        // 准备测试数据
        val service = DataService(
            id = testServiceId,
            name = "测试服务",
            description = "测试服务描述",
            type = "query",
            script = "// js\nreturn { message: 'Hello, World!' };",
            language = "js",
            groupId = testGroupId,
            parameters = listOf(
                ServiceParameter(name = "name", type = "string", description = "姓名", required = false, defaultValue = "World")
            ),
            tags = listOf("test", "demo")
        )

        val serviceDO = DataServiceDO(
            id = testServiceId,
            name = "测试服务",
            description = "测试服务描述",
            type = "query",
            script = "// js\nreturn { message: 'Hello, World!' };",
            language = "js",
            groupId = testGroupId
        )

        // 设置模拟对象的行为
        `when`(dataServiceConverter.toDO(service)).thenReturn(serviceDO)
        `when`(dataServiceMapper.selectById(testServiceId)).thenReturn(null)
        `when`(dataServiceMapper.insert(serviceDO)).thenReturn(1)

        val parameterDOs = listOf(
            ServiceParameterDO(
                serviceId = testServiceId,
                name = "name",
                type = "string",
                description = "姓名",
                defaultValue = "World",
                required = false
            )
        )

        `when`(dataServiceConverter.toParameterDOs(testServiceId, service.parameters)).thenReturn(parameterDOs)

        val tagDOs = listOf(
            ServiceTagDO(
                serviceId = testServiceId,
                name = "test"
            ),
            ServiceTagDO(
                serviceId = testServiceId,
                name = "demo"
            )
        )

        `when`(dataServiceConverter.toTagDOs(testServiceId, service.tags)).thenReturn(tagDOs)

        // 设置getService的模拟行为
        `when`(dataServiceMapper.selectById(testServiceId)).thenReturn(serviceDO)
        `when`(dataServiceConverter.toModel(eq(serviceDO), any(), any())).thenReturn(service)

        // 执行测试
        val result = repository.saveService(service)

        // 验证结果
        assertNotNull(result)
        assertEquals(testServiceId, result.id)

        // 验证方法调用
        verify(dataServiceMapper).insert(serviceDO)
        verify(serviceParameterMapper).deleteByServiceId(testServiceId)
        verify(serviceTagMapper).deleteByServiceId(testServiceId)
        verify(serviceHistoryMapper).selectMaxVersionByServiceId(testServiceId)
        verify(serviceHistoryMapper).insert(any())
    }

    @Test
    fun testGetService() {
        // 准备测试数据
        val serviceDO = DataServiceDO(
            id = testServiceId,
            name = "测试服务",
            description = "测试服务描述",
            type = "query",
            script = "// js\nreturn { message: 'Hello, World!' };",
            language = "js",
            groupId = testGroupId
        )

        val service = DataService(
            id = testServiceId,
            name = "测试服务",
            description = "测试服务描述",
            type = "query",
            script = "// js\nreturn { message: 'Hello, World!' };",
            language = "js",
            groupId = testGroupId,
            parameters = listOf(
                ServiceParameter(name = "name", type = "string", description = "姓名", required = false, defaultValue = "World")
            ),
            tags = listOf("test", "demo")
        )

        // 设置模拟对象的行为
        `when`(dataServiceMapper.selectById(testServiceId)).thenReturn(serviceDO)

        val parameterDOs = listOf(
            ServiceParameterDO(
                serviceId = testServiceId,
                name = "name",
                type = "string",
                description = "姓名",
                defaultValue = "World",
                required = false
            )
        )

        `when`(serviceParameterMapper.selectList(any())).thenReturn(parameterDOs)
        `when`(dataServiceConverter.toParameterModels(parameterDOs)).thenReturn(service.parameters)

        val tagDOs = listOf(
            ServiceTagDO(
                serviceId = testServiceId,
                name = "test"
            ),
            ServiceTagDO(
                serviceId = testServiceId,
                name = "demo"
            )
        )

        `when`(serviceTagMapper.selectList(any())).thenReturn(tagDOs)
        `when`(dataServiceConverter.toTagNames(tagDOs)).thenReturn(service.tags)

        `when`(dataServiceConverter.toModel(serviceDO, service.parameters, service.tags)).thenReturn(service)

        // 执行测试
        val result = repository.getService(testServiceId)

        // 验证结果
        assertNotNull(result)
        assertEquals(testServiceId, result?.id)
        assertEquals("测试服务", result?.name)

        // 验证方法调用
        verify(dataServiceMapper).selectById(testServiceId)
        verify(serviceParameterMapper).selectList(any())
        verify(serviceTagMapper).selectList(any())
    }

    @Test
    fun testSaveGroup() {
        // 准备测试数据
        val group = ServiceGroup(
            id = testGroupId,
            name = "测试分组",
            description = "测试分组描述"
        )

        val groupDO = ServiceGroupDO(
            id = testGroupId,
            name = "测试分组",
            description = "测试分组描述"
        )

        // 设置模拟对象的行为
        `when`(serviceGroupConverter.toDO(group)).thenReturn(groupDO)
        `when`(serviceGroupMapper.selectById(testGroupId)).thenReturn(null)
        `when`(serviceGroupMapper.insert(groupDO)).thenReturn(1)
        `when`(serviceGroupConverter.toModel(groupDO)).thenReturn(group)

        // 执行测试
        val result = repository.saveGroup(group)

        // 验证结果
        assertNotNull(result)
        assertEquals(testGroupId, result.id)
        assertEquals("测试分组", result.name)

        // 验证方法调用
        verify(serviceGroupMapper).insert(groupDO)
    }

    @Test
    fun testGetChildGroups() {
        // 准备测试数据
        val parentId = "parent-group"

        val groupDOs = listOf(
            ServiceGroupDO(
                id = testGroupId,
                name = "测试分组",
                description = "测试分组描述",
                parentId = parentId
            )
        )

        val groups = listOf(
            ServiceGroup(
                id = testGroupId,
                name = "测试分组",
                description = "测试分组描述",
                parentId = parentId
            )
        )

        // 设置模拟对象的行为
        `when`(serviceGroupMapper.selectList(any())).thenReturn(groupDOs)
        `when`(serviceGroupConverter.toModels(groupDOs)).thenReturn(groups)

        // 执行测试
        val result = repository.getChildGroups(parentId)

        // 验证结果
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(testGroupId, result[0].id)

        // 验证方法调用
        verify(serviceGroupMapper).selectList(any())
    }
}
