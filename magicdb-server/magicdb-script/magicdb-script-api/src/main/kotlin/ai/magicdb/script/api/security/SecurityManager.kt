package ai.magicdb.script.api.security

/**
 * 安全管理器接口
 *
 * @author magicdb
 */
interface SecurityManager {
    /**
     * 检查API访问权限
     *
     * @param apiId API ID
     * @param user 用户
     * @return 是否有权限
     */
    fun checkApiAccess(apiId: String, user: String?): Boolean

    /**
     * 检查脚本执行权限
     *
     * @param script 脚本内容
     * @param language 脚本语言
     * @param user 用户
     * @return 是否有权限
     */
    fun checkScriptExecution(script: String, language: String, user: String?): Boolean

    /**
     * 验证输入
     *
     * @param input 输入内容
     * @param type 输入类型
     * @return 是否有效
     */
    fun validateInput(input: String, type: String): Boolean

    /**
     * 获取安全上下文
     *
     * @param user 用户
     * @return 安全上下文
     */
    fun getSecurityContext(user: String?): Map<String, Any?>
}
