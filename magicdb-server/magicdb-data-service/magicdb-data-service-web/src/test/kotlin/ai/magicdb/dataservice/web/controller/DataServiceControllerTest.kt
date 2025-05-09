package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.DataServiceManager
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceParameter
import ai.magicdb.dataservice.api.model.ServiceResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(DataServiceController::class)
class DataServiceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var dataServiceManager: DataServiceManager

    private lateinit var testService: DataService
    private lateinit var testGroup: ServiceGroup

    @BeforeEach
    fun setup() {
        testService = DataService(
            id = "test-service-1",
            name = "Test Service",
            description = "A test service",
            script = "function execute(params) { return params; }",
            language = "javascript",
            groupId = "test-group-1",
            tags = listOf("test", "example"),
            parameters = listOf(
                ServiceParameter("param1", "string"),
                ServiceParameter("param2", "number")
            ),
            createTime = Date(),
            updateTime = Date(),
            enabled = true
        )

        testGroup = ServiceGroup(
            id = "test-group-1",
            name = "Test Group",
            description = "A test group",
            parentId = null,
            createTime = Date(),
            updateTime = Date()
        )
    }

    @Test
    fun `should get all services`() {
        `when`(dataServiceManager.getAllServices()).thenReturn(listOf(testService))

        mockMvc.perform(get("/data-service"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(testService.id))
            .andExpect(jsonPath("$[0].name").value(testService.name))
    }

    @Test
    fun `should get service by id`() {
        `when`(dataServiceManager.getService(testService.id)).thenReturn(testService)

        mockMvc.perform(get("/data-service/${testService.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(testService.id))
            .andExpect(jsonPath("$.name").value(testService.name))
    }

    @Test
    fun `should return 404 when service not found`() {
        `when`(dataServiceManager.getService("non-existent")).thenReturn(null)

        mockMvc.perform(get("/data-service/non-existent"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should save service`() {
        `when`(dataServiceManager.saveService(any(DataService::class.java))).thenReturn(testService)

        mockMvc.perform(post("/data-service")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "id": "test-service-1",
                    "name": "Test Service",
                    "description": "A test service",
                    "script": "function execute(params) { return params; }",
                    "language": "javascript",
                    "groupId": "test-group-1",
                    "tags": ["test", "example"],
                    "parameters": {
                        "param1": "string",
                        "param2": "number"
                    },
                    "enabled": true
                }
            """.trimIndent()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(testService.id))
            .andExpect(jsonPath("$.name").value(testService.name))
    }

    @Test
    fun `should delete service`() {
        `when`(dataServiceManager.deleteService(testService.id)).thenReturn(true)

        mockMvc.perform(delete("/data-service/${testService.id}"))
            .andExpect(status().isOk)
            .andExpect(content().string("true"))
    }

    @Test
    fun `should get services by group`() {
        `when`(dataServiceManager.getServicesByGroup(testGroup.id)).thenReturn(listOf(testService))

        mockMvc.perform(get("/data-service/group/${testGroup.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(testService.id))
            .andExpect(jsonPath("$[0].name").value(testService.name))
    }

    @Test
    fun `should get services by tag`() {
        `when`(dataServiceManager.getServicesByTag("test")).thenReturn(listOf(testService))

        mockMvc.perform(get("/data-service/tag/test"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(testService.id))
            .andExpect(jsonPath("$[0].name").value(testService.name))
    }

    @Test
    fun `should get all groups`() {
        `when`(dataServiceManager.getAllGroups()).thenReturn(listOf(testGroup))

        mockMvc.perform(get("/data-service/group"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(testGroup.id))
            .andExpect(jsonPath("$[0].name").value(testGroup.name))
    }

    @Test
    fun `should get group by id`() {
        `when`(dataServiceManager.getGroup(testGroup.id)).thenReturn(testGroup)

        mockMvc.perform(get("/data-service/group/${testGroup.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(testGroup.id))
            .andExpect(jsonPath("$.name").value(testGroup.name))
    }

    @Test
    fun `should return 404 when group not found`() {
        `when`(dataServiceManager.getGroup("non-existent")).thenReturn(null)

        mockMvc.perform(get("/data-service/group/non-existent"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should save group`() {
        `when`(dataServiceManager.saveGroup(any(ServiceGroup::class.java))).thenReturn(testGroup)

        mockMvc.perform(post("/data-service/group")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "id": "test-group-1",
                    "name": "Test Group",
                    "description": "A test group",
                    "parentId": null
                }
            """.trimIndent()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(testGroup.id))
            .andExpect(jsonPath("$.name").value(testGroup.name))
    }

    @Test
    fun `should delete group`() {
        `when`(dataServiceManager.deleteGroup(testGroup.id)).thenReturn(true)

        mockMvc.perform(delete("/data-service/group/${testGroup.id}"))
            .andExpect(status().isOk)
            .andExpect(content().string("true"))
    }

    @Test
    fun `should get child groups`() {
        val childGroup = ServiceGroup(
            id = "child-group-1",
            name = "Child Group",
            description = "A child group",
            parentId = testGroup.id,
            createTime = Date(),
            updateTime = Date()
        )

        `when`(dataServiceManager.getChildGroups(testGroup.id)).thenReturn(listOf(childGroup))

        mockMvc.perform(get("/data-service/group/children?parentId=${testGroup.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(childGroup.id))
            .andExpect(jsonPath("$[0].name").value(childGroup.name))
            .andExpect(jsonPath("$[0].parentId").value(testGroup.id))
    }

    @Test
    fun `should execute service`() {
        val result = ServiceResult(
            success = true,
            data = mapOf("result" to "test result"),
            message = "Success"
        )

        `when`(dataServiceManager.executeService(eq(testService.id), any())).thenReturn(result)

        mockMvc.perform(post("/data-service/execute/${testService.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "param1": "test",
                    "param2": 123
                }
            """.trimIndent()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.result").value("test result"))
            .andExpect(jsonPath("$.message").value("Success"))
    }

    @Test
    fun `should execute script`() {
        val result = ServiceResult(
            success = true,
            data = mapOf("result" to "test result"),
            message = "Success"
        )

        `when`(dataServiceManager.executeScript(
            eq("function execute(params) { return params; }"),
            eq("javascript"),
            any()
        )).thenReturn(result)

        mockMvc.perform(post("/data-service/execute-script?language=javascript")
            .contentType(MediaType.APPLICATION_JSON)
            .content("function execute(params) { return params; }"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.result").value("test result"))
            .andExpect(jsonPath("$.message").value("Success"))
    }

    @Test
    fun `should validate script`() {
        val result = ServiceResult(
            success = true,
            data = mapOf("valid" to true),
            message = "Script is valid"
        )

        `when`(dataServiceManager.validateScript(
            eq("function execute(params) { return params; }"),
            eq("javascript")
        )).thenReturn(result)

        mockMvc.perform(post("/data-service/validate-script?language=javascript")
            .contentType(MediaType.APPLICATION_JSON)
            .content("function execute(params) { return params; }"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.valid").value(true))
            .andExpect(jsonPath("$.message").value("Script is valid"))
    }
}
