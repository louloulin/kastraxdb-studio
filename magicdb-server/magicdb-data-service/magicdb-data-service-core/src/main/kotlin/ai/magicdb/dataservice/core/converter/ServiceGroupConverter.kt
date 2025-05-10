package ai.magicdb.dataservice.core.converter

import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.core.entity.ServiceGroupDO
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

/**
 * 服务分组转换器
 *
 * @author magicdb
 */
@Component
class ServiceGroupConverter(private val objectMapper: ObjectMapper) {

    /**
     * 将DO转换为模型
     *
     * @param entity DO对象
     * @return 模型对象
     */
    fun toModel(entity: ServiceGroupDO): ServiceGroup {
        // 解析元数据
        val metadata = try {
            objectMapper.readValue(entity.metadata, object : TypeReference<Map<String, Any?>>() {})
        } catch (e: Exception) {
            emptyMap<String, Any?>()
        }
        
        return ServiceGroup(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            parentId = entity.parentId,
            order = entity.orderNum,
            createTime = entity.gmtCreate,
            updateTime = entity.gmtModified,
            metadata = metadata
        )
    }

    /**
     * 将模型转换为DO
     *
     * @param model 模型对象
     * @return DO对象
     */
    fun toDO(model: ServiceGroup): ServiceGroupDO {
        // 序列化元数据
        val metadataJson = objectMapper.writeValueAsString(model.metadata)
        
        return ServiceGroupDO(
            id = model.id,
            name = model.name,
            description = model.description,
            parentId = model.parentId,
            orderNum = model.order,
            gmtCreate = model.createTime,
            gmtModified = model.updateTime,
            createUserId = null, // TODO: 从上下文获取当前用户ID
            modifiedUserId = null, // TODO: 从上下文获取当前用户ID
            metadata = metadataJson
        )
    }

    /**
     * 将DO列表转换为模型列表
     *
     * @param entities DO列表
     * @return 模型列表
     */
    fun toModels(entities: List<ServiceGroupDO>): List<ServiceGroup> {
        return entities.map { toModel(it) }
    }
}
