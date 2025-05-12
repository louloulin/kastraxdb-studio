package ai.magicdb.data.service.core.repository

import ai.magicdb.data.service.api.DataServiceRepository
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceGroup
import ai.magicdb.data.service.core.converter.DataServiceConverter
import ai.magicdb.data.service.core.converter.ServiceGroupConverter
import ai.magicdb.data.service.core.entity.ServiceHistoryDO
import ai.magicdb.data.service.core.mapper.*
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * 基于MyBatis-Plus的数据服务存储实现
 *
 * @author magicdb
 */
@Repository
class MybatisDataServiceRepository(
    private val dataServiceMapper: DataServiceMapper,
    private val serviceGroupMapper: ServiceGroupMapper,
    private val serviceParameterMapper: ServiceParameterMapper,
    private val serviceHistoryMapper: ServiceHistoryMapper,
    private val serviceTagMapper: ServiceTagMapper,
    private val dataServiceConverter: DataServiceConverter,
    private val serviceGroupConverter: ServiceGroupConverter
) : DataServiceRepository {
    private val logger = LoggerFactory.getLogger(MybatisDataServiceRepository::class.java)

    @Transactional
    override fun saveService(service: DataService): DataService {
        try {
            // 更新时间
            service.updateTime = Date()
            
            // 如果是新服务，设置创建时间
            if (service.id.isBlank()) {
                service.id = UUID.randomUUID().toString()
                service.createTime = Date()
            }
            
            // 转换为DO对象
            val serviceDO = dataServiceConverter.toDO(service)
            
            // 保存服务
            if (dataServiceMapper.selectById(service.id) == null) {
                dataServiceMapper.insert(serviceDO)
            } else {
                dataServiceMapper.updateById(serviceDO)
            }
            
            // 保存参数
            saveParameters(service.id, service.parameters)
            
            // 保存标签
            saveTags(service.id, service.tags)
            
            // 保存历史记录
            saveHistory(service)
            
            logger.info("保存数据服务: {}", service.id)
            
            // 返回保存后的服务
            return getService(service.id)!!
        } catch (e: Exception) {
            logger.error("保存数据服务失败: {}", e.message, e)
            throw e
        }
    }

    @Transactional
    override fun deleteService(id: String): Boolean {
        try {
            // 删除参数
            serviceParameterMapper.deleteByServiceId(id)
            
            // 删除标签
            serviceTagMapper.deleteByServiceId(id)
            
            // 删除服务
            val result = dataServiceMapper.deleteById(id) > 0
            
            if (result) {
                logger.info("删除数据服务: {}", id)
            } else {
                logger.warn("删除数据服务失败，未找到服务: {}", id)
            }
            
            return result
        } catch (e: Exception) {
            logger.error("删除数据服务失败: {}", e.message, e)
            return false
        }
    }

    override fun getService(id: String): DataService? {
        try {
            val serviceDO = dataServiceMapper.selectById(id) ?: return null
            
            // 获取参数
            val parameters = getParameters(id)
            
            // 获取标签
            val tags = getTags(id)
            
            return dataServiceConverter.toModel(serviceDO, parameters, tags)
        } catch (e: Exception) {
            logger.error("获取数据服务失败: {}", e.message, e)
            return null
        }
    }

    override fun getAllServices(): List<DataService> {
        try {
            val serviceDOs = dataServiceMapper.selectList(null)
            
            return serviceDOs.map { serviceDO ->
                val parameters = getParameters(serviceDO.id)
                val tags = getTags(serviceDO.id)
                dataServiceConverter.toModel(serviceDO, parameters, tags)
            }
        } catch (e: Exception) {
            logger.error("获取所有数据服务失败: {}", e.message, e)
            return emptyList()
        }
    }

    override fun getServicesByGroup(groupId: String): List<DataService> {
        try {
            val wrapper = LambdaQueryWrapper<ai.magicdb.dataservice.core.entity.DataServiceDO>()
                .eq(ai.magicdb.dataservice.core.entity.DataServiceDO::groupId, groupId)
            
            val serviceDOs = dataServiceMapper.selectList(wrapper)
            
            return serviceDOs.map { serviceDO ->
                val parameters = getParameters(serviceDO.id)
                val tags = getTags(serviceDO.id)
                dataServiceConverter.toModel(serviceDO, parameters, tags)
            }
        } catch (e: Exception) {
            logger.error("根据分组获取数据服务失败: {}", e.message, e)
            return emptyList()
        }
    }

    override fun getServicesByTag(tag: String): List<DataService> {
        try {
            val serviceDOs = dataServiceMapper.selectByTag(tag)
            
            return serviceDOs.map { serviceDO ->
                val parameters = getParameters(serviceDO.id)
                val tags = getTags(serviceDO.id)
                dataServiceConverter.toModel(serviceDO, parameters, tags)
            }
        } catch (e: Exception) {
            logger.error("根据标签获取数据服务失败: {}", e.message, e)
            return emptyList()
        }
    }

    @Transactional
    override fun saveGroup(group: ServiceGroup): ServiceGroup {
        try {
            // 更新时间
            group.updateTime = Date()
            
            // 如果是新分组，设置创建时间
            if (group.id.isBlank()) {
                group.id = UUID.randomUUID().toString()
                group.createTime = Date()
            }
            
            // 转换为DO对象
            val groupDO = serviceGroupConverter.toDO(group)
            
            // 保存分组
            if (serviceGroupMapper.selectById(group.id) == null) {
                serviceGroupMapper.insert(groupDO)
            } else {
                serviceGroupMapper.updateById(groupDO)
            }
            
            logger.info("保存服务分组: {}", group.id)
            
            // 返回保存后的分组
            return serviceGroupConverter.toModel(groupDO)
        } catch (e: Exception) {
            logger.error("保存服务分组失败: {}", e.message, e)
            throw e
        }
    }

    @Transactional
    override fun deleteGroup(id: String): Boolean {
        try {
            // 检查是否有子分组
            val childWrapper = LambdaQueryWrapper<ai.magicdb.dataservice.core.entity.ServiceGroupDO>()
                .eq(ai.magicdb.dataservice.core.entity.ServiceGroupDO::parentId, id)
            val childCount = serviceGroupMapper.selectCount(childWrapper)
            if (childCount > 0) {
                logger.warn("删除服务分组失败，存在子分组: {}", id)
                return false
            }
            
            // 检查是否有关联的服务
            val serviceWrapper = LambdaQueryWrapper<ai.magicdb.dataservice.core.entity.DataServiceDO>()
                .eq(ai.magicdb.dataservice.core.entity.DataServiceDO::groupId, id)
            val serviceCount = dataServiceMapper.selectCount(serviceWrapper)
            if (serviceCount > 0) {
                logger.warn("删除服务分组失败，存在关联的服务: {}", id)
                return false
            }
            
            // 删除分组
            val result = serviceGroupMapper.deleteById(id) > 0
            
            if (result) {
                logger.info("删除服务分组: {}", id)
            } else {
                logger.warn("删除服务分组失败，未找到分组: {}", id)
            }
            
            return result
        } catch (e: Exception) {
            logger.error("删除服务分组失败: {}", e.message, e)
            return false
        }
    }

    override fun getGroup(id: String): ServiceGroup? {
        try {
            val groupDO = serviceGroupMapper.selectById(id) ?: return null
            return serviceGroupConverter.toModel(groupDO)
        } catch (e: Exception) {
            logger.error("获取服务分组失败: {}", e.message, e)
            return null
        }
    }

    override fun getAllGroups(): List<ServiceGroup> {
        try {
            val groupDOs = serviceGroupMapper.selectList(null)
            return serviceGroupConverter.toModels(groupDOs)
        } catch (e: Exception) {
            logger.error("获取所有服务分组失败: {}", e.message, e)
            return emptyList()
        }
    }

    override fun getChildGroups(parentId: String?): List<ServiceGroup> {
        try {
            val wrapper = LambdaQueryWrapper<ai.magicdb.dataservice.core.entity.ServiceGroupDO>()
            if (parentId == null) {
                wrapper.isNull(ai.magicdb.dataservice.core.entity.ServiceGroupDO::parentId)
            } else {
                wrapper.eq(ai.magicdb.dataservice.core.entity.ServiceGroupDO::parentId, parentId)
            }
            
            val groupDOs = serviceGroupMapper.selectList(wrapper)
            return serviceGroupConverter.toModels(groupDOs)
        } catch (e: Exception) {
            logger.error("获取子分组失败: {}", e.message, e)
            return emptyList()
        }
    }

    /**
     * 保存服务参数
     */
    @Transactional
    private fun saveParameters(serviceId: String, parameters: List<ai.magicdb.dataservice.api.model.ServiceParameter>) {
        // 删除旧参数
        serviceParameterMapper.deleteByServiceId(serviceId)
        
        // 保存新参数
        val parameterDOs = dataServiceConverter.toParameterDOs(serviceId, parameters)
        parameterDOs.forEach { serviceParameterMapper.insert(it) }
    }

    /**
     * 保存服务标签
     */
    @Transactional
    private fun saveTags(serviceId: String, tags: List<String>) {
        // 删除旧标签
        serviceTagMapper.deleteByServiceId(serviceId)
        
        // 保存新标签
        val tagDOs = dataServiceConverter.toTagDOs(serviceId, tags)
        tagDOs.forEach { serviceTagMapper.insert(it) }
    }

    /**
     * 保存服务历史记录
     */
    @Transactional
    private fun saveHistory(service: DataService) {
        // 获取最新版本号
        val maxVersion = serviceHistoryMapper.selectMaxVersionByServiceId(service.id) ?: 0
        
        // 创建历史记录
        val historyDO = ServiceHistoryDO(
            serviceId = service.id,
            version = maxVersion + 1,
            name = service.name,
            description = service.description,
            type = service.type,
            script = service.script,
            language = service.language,
            groupId = service.groupId,
            gmtCreate = Date(),
            createUserId = null, // TODO: 从上下文获取当前用户ID
            comment = null // TODO: 从请求中获取修改说明
        )
        
        serviceHistoryMapper.insert(historyDO)
    }

    /**
     * 获取服务参数
     */
    private fun getParameters(serviceId: String): List<ai.magicdb.dataservice.api.model.ServiceParameter> {
        val wrapper = LambdaQueryWrapper<ai.magicdb.dataservice.core.entity.ServiceParameterDO>()
            .eq(ai.magicdb.dataservice.core.entity.ServiceParameterDO::serviceId, serviceId)
            .orderByAsc(ai.magicdb.dataservice.core.entity.ServiceParameterDO::orderNum)
        
        val parameterDOs = serviceParameterMapper.selectList(wrapper)
        return dataServiceConverter.toParameterModels(parameterDOs)
    }

    /**
     * 获取服务标签
     */
    private fun getTags(serviceId: String): List<String> {
        val wrapper = LambdaQueryWrapper<ai.magicdb.dataservice.core.entity.ServiceTagDO>()
            .eq(ai.magicdb.dataservice.core.entity.ServiceTagDO::serviceId, serviceId)
        
        val tagDOs = serviceTagMapper.selectList(wrapper)
        return dataServiceConverter.toTagNames(tagDOs)
    }
}
