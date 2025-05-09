package ai.magicdb.script.runtime.web

import ai.magicdb.script.api.ApiService
import ai.magicdb.script.api.model.ApiGroupInfo
import ai.magicdb.script.api.model.ApiInfo
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletRequest

/**
 * API控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api")
class ApiController(private val apiService: ApiService) {
    private val logger = LoggerFactory.getLogger(ApiController::class.java)

    /**
     * 获取所有API
     */
    @GetMapping
    fun getAllApis(): ResponseEntity<List<ApiInfo>> {
        return ResponseEntity.ok(apiService.getAllApis())
    }

    /**
     * 获取API
     */
    @GetMapping("/{id}")
    fun getApi(@PathVariable id: String): ResponseEntity<ApiInfo> {
        val apiInfo = apiService.getApi(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(apiInfo)
    }

    /**
     * 保存API
     */
    @PostMapping
    fun saveApi(@RequestBody apiInfo: ApiInfo): ResponseEntity<ApiInfo> {
        return ResponseEntity.ok(apiService.saveApi(apiInfo))
    }

    /**
     * 删除API
     */
    @DeleteMapping("/{id}")
    fun deleteApi(@PathVariable id: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(apiService.deleteApi(id))
    }

    /**
     * 获取分组下的API
     */
    @GetMapping("/group/{groupId}")
    fun getApisByGroupId(@PathVariable groupId: String): ResponseEntity<List<ApiInfo>> {
        return ResponseEntity.ok(apiService.getApisByGroupId(groupId))
    }

    /**
     * 获取所有分组
     */
    @GetMapping("/group")
    fun getAllGroups(): ResponseEntity<List<ApiGroupInfo>> {
        return ResponseEntity.ok(apiService.getAllGroups())
    }

    /**
     * 获取分组
     */
    @GetMapping("/group/{id}")
    fun getGroup(@PathVariable id: String): ResponseEntity<ApiGroupInfo> {
        val groupInfo = apiService.getGroup(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(groupInfo)
    }

    /**
     * 保存分组
     */
    @PostMapping("/group")
    fun saveGroup(@RequestBody groupInfo: ApiGroupInfo): ResponseEntity<ApiGroupInfo> {
        return ResponseEntity.ok(apiService.saveGroup(groupInfo))
    }

    /**
     * 删除分组
     */
    @DeleteMapping("/group/{id}")
    fun deleteGroup(@PathVariable id: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(apiService.deleteGroup(id))
    }

    /**
     * 获取子分组
     */
    @GetMapping("/group/children")
    fun getChildGroups(@RequestParam(required = false) parentId: String?): ResponseEntity<List<ApiGroupInfo>> {
        return ResponseEntity.ok(apiService.getChildGroups(parentId))
    }

    /**
     * 执行API
     */
    @PostMapping("/execute/{id}")
    fun executeApi(@PathVariable id: String, @RequestBody parameters: Map<String, Any?>): ResponseEntity<Any?> {
        return ResponseEntity.ok(apiService.executeApi(id, parameters))
    }
}
