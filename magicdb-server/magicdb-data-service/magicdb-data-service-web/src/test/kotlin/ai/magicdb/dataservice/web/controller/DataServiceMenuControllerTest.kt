package ai.magicdb.dataservice.web.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(DataServiceMenuController::class)
class DataServiceMenuControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should get data service menu configuration`() {
        mockMvc.perform(get("/api/menu/data-service"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("data-service"))
            .andExpect(jsonPath("$.name").value("数据服务"))
            .andExpect(jsonPath("$.icon").value("database"))
            .andExpect(jsonPath("$.path").value("/main/data-service"))
            .andExpect(jsonPath("$.order").value(3))
            .andExpect(jsonPath("$.children").isArray)
            .andExpect(jsonPath("$.children[0].name").value("服务管理"))
            .andExpect(jsonPath("$.children[1].name").value("服务测试"))
            .andExpect(jsonPath("$.children[2].name").value("服务文档"))
    }
}
