package ai.magicdb.dataservice.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * 数据服务静态资源控制器
 *
 * @author magicdb
 */
@Controller
@RequestMapping("/data-service")
class DataServiceStaticController {

    /**
     * 数据服务菜单页面
     */
    @GetMapping("/menu")
    fun menu(): String {
        return "forward:/static/data-service/data-service-menu.html"
    }
}
