package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.core.version.VersionManager
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 版本控制器
 * 用于管理和查询版本信息
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/version")
class VersionController(
    private val versionManager: VersionManager
) {
    
    /**
     * 获取当前版本
     */
    @GetMapping("/current")
    fun getCurrentVersion(): DataResult<Map<String, String>> {
        val result = mapOf(
            "appVersion" to versionManager.getAppVersion(),
            "dataServiceVersion" to versionManager.getDataServiceVersion(),
            "currentVersion" to versionManager.getCurrentVersion()
        )
        return DataResult.of(result)
    }
    
    /**
     * 获取版本历史
     */
    @GetMapping("/history")
    fun getVersionHistory(): DataResult<List<VersionManager.VersionHistory>> {
        val history = versionManager.getVersionHistory()
        return DataResult.of(history)
    }
}
