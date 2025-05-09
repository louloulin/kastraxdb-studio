package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup

/**
 * 数据服务存储接口
 *
 * @author magicdb
 */
interface DataServiceRepository {
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
}
