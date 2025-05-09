package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.DataServiceManager
import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceResult
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 数据服务控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/data-service")
class DataServiceController(private val dataServiceManager: DataServiceManager) {
    private val logger = LoggerFactory.getLogger(DataServiceController::class.java)

    /**
     * 获取所有数据服务
     */
    @GetMapping
    fun getAllServices(): ResponseEntity<List<DataService>> {
        return ResponseEntity.ok(dataServiceManager.getAllServices())
    }

    /**
     * 获取数据服务
     */
    @GetMapping("/{id}")
    fun getService(@PathVariable id: String): ResponseEntity<DataService> {
        val service = dataServiceManager.getService(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(service)
    }

    /**
     * 保存数据服务
     */
    @PostMapping
    fun saveService(@RequestBody service: DataService): ResponseEntity<DataService> {
        return ResponseEntity.ok(dataServiceManager.saveService(service))
    }

    /**
     * 删除数据服务
     */
    @DeleteMapping("/{id}")
    fun deleteService(@PathVariable id: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(dataServiceManager.deleteService(id))
    }

    /**
     * 根据分组获取数据服务
     */
    @GetMapping("/group/{groupId}")
    fun getServicesByGroup(@PathVariable groupId: String): ResponseEntity<List<DataService>> {
        return ResponseEntity.ok(dataServiceManager.getServicesByGroup(groupId))
    }

    /**
     * 根据标签获取数据服务
     */
    @GetMapping("/tag/{tag}")
    fun getServicesByTag(@PathVariable tag: String): ResponseEntity<List<DataService>> {
        return ResponseEntity.ok(dataServiceManager.getServicesByTag(tag))
    }

    /**
     * 获取所有服务分组
     */
    @GetMapping("/group")
    fun getAllGroups(): ResponseEntity<List<ServiceGroup>> {
        return ResponseEntity.ok(dataServiceManager.getAllGroups())
    }

    /**
     * 获取服务分组
     */
    @GetMapping("/group/{id}")
    fun getGroup(@PathVariable id: String): ResponseEntity<ServiceGroup> {
        val group = dataServiceManager.getGroup(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(group)
    }

    /**
     * 保存服务分组
     */
    @PostMapping("/group")
    fun saveGroup(@RequestBody group: ServiceGroup): ResponseEntity<ServiceGroup> {
        return ResponseEntity.ok(dataServiceManager.saveGroup(group))
    }

    /**
     * 删除服务分组
     */
    @DeleteMapping("/group/{id}")
    fun deleteGroup(@PathVariable id: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(dataServiceManager.deleteGroup(id))
    }

    /**
     * 获取子分组
     */
    @GetMapping("/group/children")
    fun getChildGroups(@RequestParam(required = false) parentId: String?): ResponseEntity<List<ServiceGroup>> {
        return ResponseEntity.ok(dataServiceManager.getChildGroups(parentId))
    }

    /**
     * 执行数据服务
     */
    @PostMapping("/execute/{id}")
    fun executeService(@PathVariable id: String, @RequestBody parameters: Map<String, Any?>): ResponseEntity<ServiceResult> {
        return ResponseEntity.ok(dataServiceManager.executeService(id, parameters))
    }

    /**
     * 执行脚本
     */
    @PostMapping("/execute-script")
    fun executeScript(
        @RequestParam language: String,
        @RequestBody script: String,
        @RequestParam(required = false) parameters: Map<String, Any?>?
    ): ResponseEntity<ServiceResult> {
        return ResponseEntity.ok(dataServiceManager.executeScript(script, language, parameters ?: emptyMap()))
    }

    /**
     * 验证脚本
     */
    @PostMapping("/validate-script")
    fun validateScript(
        @RequestParam language: String,
        @RequestBody script: String
    ): ResponseEntity<ServiceResult> {
        return ResponseEntity.ok(dataServiceManager.validateScript(script, language))
    }

    /**
     * 导出数据服务
     */
    @GetMapping("/export/{id}")
    fun exportService(@PathVariable id: String): ResponseEntity<Map<String, Any?>> {
        return ResponseEntity.ok(dataServiceManager.exportService(id))
    }

    /**
     * 导入数据服务
     */
    @PostMapping("/import")
    fun importService(@RequestBody data: Map<String, Any?>): ResponseEntity<DataService> {
        return ResponseEntity.ok(dataServiceManager.importService(data))
    }
}
