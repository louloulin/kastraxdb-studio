package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.web.dto.MenuConfigDTO
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

/**
 * Controller for data service menu configuration
 */
@RestController
@RequestMapping("/api/menu")
class DataServiceMenuController(
    private val objectMapper: ObjectMapper
) {
    
    /**
     * Get data service menu configuration
     */
    @GetMapping("/data-service")
    fun getMenuConfig(): DataResult<MenuConfigDTO> {
        try {
            val resource = ClassPathResource("static/menu-config.json")
            val menuConfig = objectMapper.readValue(resource.inputStream, MenuConfigDTO::class.java)
            return DataResult.of(menuConfig)
        } catch (e: IOException) {
            return DataResult.failed("Failed to load menu configuration: ${e.message}")
        }
    }
}
