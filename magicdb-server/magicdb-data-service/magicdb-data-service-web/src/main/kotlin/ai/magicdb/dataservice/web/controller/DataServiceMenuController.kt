package ai.magicdb.dataservice.web.controller

import ai.magicdb.server.tools.base.wrapper.result.DataResult
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets

/**
 * 数据服务菜单控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/menu")
class DataServiceMenuController(private val objectMapper: ObjectMapper) {

    /**
     * 获取数据服务菜单
     */
    @GetMapping("/data-service")
    fun getDataServiceMenu(): DataResult<Any> {
        try {
            val resource = ClassPathResource("static/data-service/menu-config.json")
            val menuConfig = resource.inputStream.readAllBytes().toString(StandardCharsets.UTF_8)
            val menuData = objectMapper.readValue(menuConfig, Map::class.java)
            return DataResult.of(menuData)
        } catch (e: Exception) {
            return DataResult.error("500", "获取数据服务菜单失败: ${e.message}")
        }
    }
}
