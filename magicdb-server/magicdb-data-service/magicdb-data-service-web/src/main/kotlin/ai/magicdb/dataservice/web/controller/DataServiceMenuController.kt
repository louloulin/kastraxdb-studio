package ai.magicdb.dataservice.web.controller

import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
class DataServiceMenuController {

    /**
     * 获取数据服务菜单配置
     */
    @GetMapping("/data-service")
    fun getDataServiceMenu(): ResponseEntity<String> {
        val resource = ClassPathResource("static/menu-config.json")
        val content = resource.inputStream.readAllBytes().toString(StandardCharsets.UTF_8)
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(content)
    }
}
