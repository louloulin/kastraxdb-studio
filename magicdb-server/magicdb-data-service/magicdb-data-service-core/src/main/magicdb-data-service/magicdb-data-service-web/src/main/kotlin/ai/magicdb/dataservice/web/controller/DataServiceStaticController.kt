package ai.magicdb.dataservice.web.controller

import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.nio.charset.StandardCharsets

/**
 * 数据服务静态资源控制器
 */
@Controller
@RequestMapping("/static/data-service")
class DataServiceStaticController {

    /**
     * 获取菜单配置文件
     */
    @GetMapping("/menu-config.json")
    @ResponseBody
    fun getMenuConfig(): ResponseEntity<String> {
        val resource = ClassPathResource("static/menu-config.json")
        val content = resource.inputStream.readAllBytes().toString(StandardCharsets.UTF_8)
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(content)
    }

    /**
     * 获取菜单注册脚本
     */
    @GetMapping("/menu-register.js")
    @ResponseBody
    fun getMenuRegister(): ResponseEntity<String> {
        val resource = ClassPathResource("static/menu-register.js")
        val content = resource.inputStream.readAllBytes().toString(StandardCharsets.UTF_8)
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JAVASCRIPT)
            .body(content)
    }
}
