package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.ServiceResult

/**
 * 数据服务执行器接口
 *
 * @author magicdb
 */
interface DataServiceExecutor {
    /**
     * 执行数据服务
     *
     * @param serviceId 服务ID
     * @param parameters 参数
     * @return 执行结果
     */
    fun execute(serviceId: String, parameters: Map<String, Any?>): ServiceResult

    /**
     * 执行脚本
     *
     * @param script 脚本内容
     * @param language 脚本语言
     * @param parameters 参数
     * @return 执行结果
     */
    fun executeScript(script: String, language: String, parameters: Map<String, Any?>): ServiceResult

    /**
     * 验证脚本
     *
     * @param script 脚本内容
     * @param language 脚本语言
     * @return 验证结果
     */
    fun validateScript(script: String, language: String): ServiceResult
}
