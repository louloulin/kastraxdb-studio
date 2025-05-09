package ai.magicdb.script.runtime.web

import ai.magicdb.script.api.ScriptManager
import ai.magicdb.script.api.VersionDiff
import ai.magicdb.script.api.model.Script
import ai.magicdb.script.api.model.ScriptGroup
import ai.magicdb.script.api.model.ScriptMetadata
import ai.magicdb.script.api.model.ScriptVersion
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 脚本控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/script")
class ScriptController(private val scriptManager: ScriptManager) {
    private val logger = LoggerFactory.getLogger(ScriptController::class.java)

    /**
     * 获取所有脚本
     */
    @GetMapping
    fun getAllScripts(): ResponseEntity<List<Script>> {
        return ResponseEntity.ok(scriptManager.getAllScripts())
    }

    /**
     * 获取脚本
     */
    @GetMapping("/{id}")
    fun getScript(@PathVariable id: String): ResponseEntity<Script> {
        val script = scriptManager.getScript(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(script)
    }

    /**
     * 保存脚本
     */
    @PostMapping
    fun saveScript(@RequestBody script: Script): ResponseEntity<Script> {
        return ResponseEntity.ok(scriptManager.saveScript(script))
    }

    /**
     * 删除脚本
     */
    @DeleteMapping("/{id}")
    fun deleteScript(@PathVariable id: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(scriptManager.deleteScript(id))
    }

    /**
     * 根据分组获取脚本
     */
    @GetMapping("/group/{groupId}")
    fun getScriptsByGroup(@PathVariable groupId: String): ResponseEntity<List<Script>> {
        return ResponseEntity.ok(scriptManager.getScriptsByGroup(groupId))
    }

    /**
     * 根据标签获取脚本
     */
    @GetMapping("/tag/{tag}")
    fun getScriptsByTag(@PathVariable tag: String): ResponseEntity<List<Script>> {
        return ResponseEntity.ok(scriptManager.getScriptsByTag(tag))
    }

    /**
     * 获取所有分组
     */
    @GetMapping("/group")
    fun getAllGroups(): ResponseEntity<List<ScriptGroup>> {
        return ResponseEntity.ok(scriptManager.getAllGroups())
    }

    /**
     * 获取分组
     */
    @GetMapping("/group/{id}")
    fun getGroup(@PathVariable id: String): ResponseEntity<ScriptGroup> {
        val group = scriptManager.getGroup(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(group)
    }

    /**
     * 保存分组
     */
    @PostMapping("/group")
    fun saveGroup(@RequestBody group: ScriptGroup): ResponseEntity<ScriptGroup> {
        return ResponseEntity.ok(scriptManager.saveGroup(group))
    }

    /**
     * 删除分组
     */
    @DeleteMapping("/group/{id}")
    fun deleteGroup(@PathVariable id: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(scriptManager.deleteGroup(id))
    }

    /**
     * 获取子分组
     */
    @GetMapping("/group/children")
    fun getChildGroups(@RequestParam(required = false) parentId: String?): ResponseEntity<List<ScriptGroup>> {
        return ResponseEntity.ok(scriptManager.getChildGroups(parentId))
    }

    /**
     * 创建脚本版本
     */
    @PostMapping("/{id}/version")
    fun createVersion(
        @PathVariable id: String,
        @RequestParam description: String,
        @RequestParam(required = false) creator: String?
    ): ResponseEntity<ScriptVersion> {
        val version = scriptManager.createVersion(id, description, creator) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(version)
    }

    /**
     * 获取脚本版本
     */
    @GetMapping("/{id}/version/{version}")
    fun getVersion(@PathVariable id: String, @PathVariable version: Int): ResponseEntity<ScriptVersion> {
        val scriptVersion = scriptManager.getVersion(id, version) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(scriptVersion)
    }

    /**
     * 获取脚本所有版本
     */
    @GetMapping("/{id}/version")
    fun getVersions(@PathVariable id: String): ResponseEntity<List<ScriptVersion>> {
        return ResponseEntity.ok(scriptManager.getVersions(id))
    }

    /**
     * 切换脚本版本
     */
    @PutMapping("/{id}/version/{version}")
    fun switchVersion(@PathVariable id: String, @PathVariable version: Int): ResponseEntity<Script> {
        val script = scriptManager.switchVersion(id, version) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(script)
    }

    /**
     * 比较脚本版本
     */
    @GetMapping("/{id}/version/{fromVersion}/compare/{toVersion}")
    fun compareVersions(
        @PathVariable id: String,
        @PathVariable fromVersion: Int,
        @PathVariable toVersion: Int
    ): ResponseEntity<VersionDiff> {
        val diff = scriptManager.compareVersions(id, fromVersion, toVersion) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(diff)
    }

    /**
     * 获取脚本元数据
     */
    @GetMapping("/{id}/metadata")
    fun getMetadata(@PathVariable id: String): ResponseEntity<ScriptMetadata> {
        val metadata = scriptManager.getMetadata(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(metadata)
    }

    /**
     * 更新脚本元数据
     */
    @PutMapping("/{id}/metadata")
    fun updateMetadata(@PathVariable id: String, @RequestBody metadata: ScriptMetadata): ResponseEntity<ScriptMetadata> {
        return ResponseEntity.ok(scriptManager.updateMetadata(id, metadata))
    }

    /**
     * 添加标签
     */
    @PostMapping("/{id}/tag/{tag}")
    fun addTag(@PathVariable id: String, @PathVariable tag: String): ResponseEntity<Script> {
        val script = scriptManager.addTag(id, tag) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(script)
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}/tag/{tag}")
    fun removeTag(@PathVariable id: String, @PathVariable tag: String): ResponseEntity<Script> {
        val script = scriptManager.removeTag(id, tag) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(script)
    }

    /**
     * 获取脚本依赖
     */
    @GetMapping("/{id}/dependency")
    fun getDependencies(@PathVariable id: String): ResponseEntity<List<String>> {
        return ResponseEntity.ok(scriptManager.getDependencies(id))
    }

    /**
     * 添加依赖
     */
    @PostMapping("/{id}/dependency/{dependencyId}")
    fun addDependency(@PathVariable id: String, @PathVariable dependencyId: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(scriptManager.addDependency(id, dependencyId))
    }

    /**
     * 删除依赖
     */
    @DeleteMapping("/{id}/dependency/{dependencyId}")
    fun removeDependency(@PathVariable id: String, @PathVariable dependencyId: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(scriptManager.removeDependency(id, dependencyId))
    }

    /**
     * 获取脚本使用情况
     */
    @GetMapping("/{id}/usage")
    fun getUsage(@PathVariable id: String): ResponseEntity<Any> {
        return ResponseEntity.ok(scriptManager.getUsage(id))
    }

    /**
     * 执行脚本
     */
    @PostMapping("/{id}/execute")
    fun executeScript(@PathVariable id: String, @RequestBody parameters: Map<String, Any?>): ResponseEntity<Any?> {
        return ResponseEntity.ok(scriptManager.executeScript(id, parameters))
    }

    /**
     * 验证脚本
     */
    @PostMapping("/validate")
    fun validateScript(
        @RequestParam language: String,
        @RequestBody script: String
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(scriptManager.validateScript(script, language))
    }

    /**
     * 导出脚本
     */
    @GetMapping("/{id}/export")
    fun exportScript(@PathVariable id: String): ResponseEntity<Map<String, Any?>> {
        return ResponseEntity.ok(scriptManager.exportScript(id))
    }

    /**
     * 导入脚本
     */
    @PostMapping("/import")
    fun importScript(@RequestBody data: Map<String, Any?>): ResponseEntity<Script> {
        return ResponseEntity.ok(scriptManager.importScript(data))
    }
}
