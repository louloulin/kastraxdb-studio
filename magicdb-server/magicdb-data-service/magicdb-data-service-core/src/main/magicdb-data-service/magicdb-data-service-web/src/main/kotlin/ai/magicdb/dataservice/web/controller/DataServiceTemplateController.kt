package ai.magicdb.dataservice.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * 数据服务模板控制器
 */
@Controller
@RequestMapping("/templates/data-service")
class DataServiceTemplateController {

    /**
     * 获取数据服务菜单HTML片段
     */
    @GetMapping("/menu")
    fun getMenuTemplate(): String {
        return "data-service-menu"
    }
}
