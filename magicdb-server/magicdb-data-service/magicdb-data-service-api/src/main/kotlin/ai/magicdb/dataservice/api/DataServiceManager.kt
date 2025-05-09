package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceResult

/**
 * 数据服务管理接口
 *
 * @author magicdb
 */
interface DataServiceManager {
    /**
     * 保存数据服务
     *
     * @param service 数据服务
     * @return 保存后的数据服务
     */
    fun saveService(service: DataService): DataService

    /**
     * 删除数据服务
     *
     * @param id 服务ID
     * @return 是否删除成功
     */
    fun deleteService(id: String): Boolean

    /**
     * 获取数据服务
     *
     * @param id 服务ID
     * @return 数据服务
     */
    fun getService(id: String): DataService?

    /**
     * 获取所有数据服务
     *
     * @return 数据服务列表
     */
    fun getAllServices(): List<DataService>

    /**
     * 根据分组获取数据服务
     *
     * @param groupId 分组ID
     * @return 数据服务列表
     */
    fun getServicesByGroup(groupId: String): List<DataService>

    /**
     * 根据标签获取数据服务
     *
     * @param tag 标签
     * @return 数据服务列表
     */
    fun getServicesByTag(tag: String): List<DataService>

    /**
     * 保存服务分组
     *
     * @param group 服务分组
     * @return 保存后的服务分组
     */
    fun saveGroup(group: ServiceGroup): ServiceGroup

    /**
     * 删除服务分组
     *
     * @param id 分组ID
     * @return 是否删除成功
     */
    fun deleteGroup(id: String): Boolean

    /**
     * 获取服务分组
     *
     * @param id 分组ID
     * @return 服务分组
     */
    fun getGroup(id: String): ServiceGroup?

    /**
     * 获取所有服务分组
     *
     * @return 服务分组列表
     */
    fun getAllGroups(): List<ServiceGroup>

    /**
     * 获取子分组
     *
     * @param parentId 父分组ID
     * @return 服务分组列表
     */
    fun getChildGroups(parentId: String?): List<ServiceGroup>

    /**
     * 执行数据服务
     *
     * @param serviceId 服务ID
     * @param parameters 参数
     * @return 执行结果
     */
    fun executeService(serviceId: String, parameters: Map<String, Any?>): ServiceResult

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

    /**
     * 导出数据服务
     *
     * @param serviceId 服务ID
     * @return 导出的数据
     */
    fun exportService(serviceId: String): Map<String, Any?>

    /**
     * 导入数据服务
     *
     * @param data 导入的数据
     * @return 导入的服务
     */
    fun importService(data: Map<String, Any?>): DataService
}
