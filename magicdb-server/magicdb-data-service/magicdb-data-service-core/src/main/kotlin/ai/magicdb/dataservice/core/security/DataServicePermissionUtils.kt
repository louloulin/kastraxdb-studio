package ai.magicdb.dataservice.core.security

import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.server.tools.common.exception.PermissionDeniedBusinessException
import ai.magicdb.server.tools.common.util.ContextUtils
import ai.magicdb.server.domain.core.util.PermissionUtils
import org.slf4j.LoggerFactory

/**
 * 数据服务权限工具类
 *
 * @author magicdb
 */
object DataServicePermissionUtils {
    private val logger = LoggerFactory.getLogger(DataServicePermissionUtils::class.java)

    /**
     * 检查数据服务操作权限
     *
     * @param service 数据服务
     * @throws PermissionDeniedBusinessException 如果没有权限
     */
    @JvmStatic
    fun checkServiceOperationPermission(service: DataService) {
        try {
            // 获取创建者ID
            val createUserId = service.metadata?.get("createUserId") as? Long
            if (createUserId != null) {
                PermissionUtils.checkOperationPermission(createUserId)
            } else {
                // 如果没有创建者ID，只有管理员可以操作
                PermissionUtils.checkDeskTopOrAdmin()
            }
        } catch (e: Exception) {
            logger.warn("用户没有操作数据服务的权限: {}", e.message)
            throw PermissionDeniedBusinessException()
        }
    }

    /**
     * 检查分组操作权限
     *
     * @param group 分组
     * @throws PermissionDeniedBusinessException 如果没有权限
     */
    @JvmStatic
    fun checkGroupOperationPermission(group: ServiceGroup) {
        try {
            // 获取创建者ID
            val createUserId = group.metadata?.get("createUserId") as? Long
            if (createUserId != null) {
                PermissionUtils.checkOperationPermission(createUserId)
            } else {
                // 如果没有创建者ID，只有管理员可以操作
                PermissionUtils.checkDeskTopOrAdmin()
            }
        } catch (e: Exception) {
            logger.warn("用户没有操作分组的权限: {}", e.message)
            throw PermissionDeniedBusinessException()
        }
    }

    /**
     * 检查数据服务访问权限
     *
     * @param service 数据服务
     * @return 是否有权限
     */
    @JvmStatic
    fun checkServiceAccessPermission(service: DataService): Boolean {
        try {
            // 获取创建者ID
            val createUserId = service.metadata?.get("createUserId") as? Long
            if (createUserId != null) {
                // 检查是否是公开服务
                val isPublic = service.metadata?.get("isPublic") as? Boolean ?: false
                if (isPublic) {
                    return true
                }

                // 检查是否是创建者或管理员
                val loginUser = ContextUtils.getLoginUser()
                return loginUser.admin || loginUser.id == createUserId
            } else {
                // 如果没有创建者ID，默认所有人都可以访问
                return true
            }
        } catch (e: Exception) {
            logger.warn("检查数据服务访问权限失败: {}", e.message)
            return false
        }
    }

    /**
     * 检查脚本执行权限
     *
     * @param script 脚本内容
     * @param language 脚本语言
     * @return 是否有权限
     */
    @JvmStatic
    fun checkScriptExecutionPermission(script: String, language: String): Boolean {
        try {
            // 检查是否是管理员
            val loginUser = ContextUtils.getLoginUser()
            if (loginUser.admin) {
                return true
            }

            // 检查脚本是否包含危险操作
            if (containsDangerousOperation(script, language)) {
                logger.warn("脚本包含危险操作，需要管理员权限")
                return false
            }

            return true
        } catch (e: Exception) {
            logger.warn("检查脚本执行权限失败: {}", e.message)
            return false
        }
    }

    /**
     * 检查脚本是否包含危险操作
     *
     * @param script 脚本内容
     * @param language 脚本语言
     * @return 是否包含危险操作
     */
    private fun containsDangerousOperation(script: String, language: String): Boolean {
        // 危险操作关键字
        val dangerousKeywords = listOf(
            "System", "Runtime", "Process", "exec", "eval",
            "File", "FileSystem", "delete", "rm", "remove",
            "chmod", "chown", "sudo", "su", "shutdown", "reboot",
            "format", "mkfs", "fdisk", "dd", "mount", "umount"
        )

        // 检查脚本是否包含危险关键字
        return dangerousKeywords.any { script.contains(it) }
    }

    /**
     * 设置当前用户为创建者
     *
     * @param service 数据服务
     */
    @JvmStatic
    fun setCurrentUserAsCreator(service: DataService) {
        try {
            val loginUser = ContextUtils.getLoginUser()
            val metadata = service.metadata?.toMutableMap() ?: mutableMapOf()
            metadata["createUserId"] = loginUser.id
            service.metadata = metadata
        } catch (e: Exception) {
            logger.warn("设置当前用户为创建者失败: {}", e.message)
        }
    }

    /**
     * 设置当前用户为创建者
     *
     * @param group 分组
     */
    @JvmStatic
    fun setCurrentUserAsCreator(group: ServiceGroup) {
        try {
            val loginUser = ContextUtils.getLoginUser()
            val metadata = group.metadata?.toMutableMap() ?: mutableMapOf()
            metadata["createUserId"] = loginUser.id
            group.metadata = metadata
        } catch (e: Exception) {
            logger.warn("设置当前用户为创建者失败: {}", e.message)
        }
    }
}
