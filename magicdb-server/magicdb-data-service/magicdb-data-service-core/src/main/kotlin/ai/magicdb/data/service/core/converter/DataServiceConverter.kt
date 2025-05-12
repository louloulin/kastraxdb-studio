package ai.magicdb.data.service.core.converter

import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceParameter
import ai.magicdb.data.service.core.entity.DataServiceDO
import ai.magicdb.data.service.core.entity.ServiceParameterDO
import ai.magicdb.data.service.core.entity.ServiceTagDO
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.util.*

/**
 * 数据服务转换器
 *
 * @author magicdb
 */
@Component
class DataServiceConverter(private val objectMapper: ObjectMapper) {

    /**
     * 将DO转换为模型
     *
     * @param entity DO对象
     * @param parameters 参数列表
     * @param tags 标签列表
     * @return 模型对象
     */
    fun toModel(entity: DataServiceDO, parameters: List<ServiceParameter>, tags: List<String>): DataService {
        // 解析元数据
        val metadata = try {
            objectMapper.readValue(entity.metadata, object : TypeReference<Map<String, Any?>>() {})
        } catch (e: Exception) {
            emptyMap<String, Any?>()
        }
        
        return DataService(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            type = entity.type,
            dataSourceId = entity.dataSourceId,
            databaseName = entity.databaseName,
            schemaName = entity.schemaName,
            tableName = entity.tableName,
            script = entity.script,
            language = entity.language,
            parameters = parameters,
            output = null, // TODO: 实现输出定义
            groupId = entity.groupId,
            tags = tags,
            createTime = entity.gmtCreate,
            updateTime = entity.gmtModified,
            enabled = entity.enabled,
            timeout = entity.timeout,
            cacheTime = entity.cacheTime,
            metadata = metadata
        )
    }

    /**
     * 将模型转换为DO
     *
     * @param model 模型对象
     * @return DO对象
     */
    fun toDO(model: DataService): DataServiceDO {
        // 序列化标签
        val tagsJson = objectMapper.writeValueAsString(model.tags)
        
        // 序列化元数据
        val metadataJson = objectMapper.writeValueAsString(model.metadata)
        
        return DataServiceDO(
            id = model.id,
            name = model.name,
            description = model.description,
            type = model.type,
            dataSourceId = model.dataSourceId,
            databaseName = model.databaseName,
            schemaName = model.schemaName,
            tableName = model.tableName,
            script = model.script,
            language = model.language,
            groupId = model.groupId,
            gmtCreate = model.createTime,
            gmtModified = model.updateTime,
            enabled = model.enabled,
            timeout = model.timeout,
            cacheTime = model.cacheTime,
            createUserId = null, // TODO: 从上下文获取当前用户ID
            modifiedUserId = null, // TODO: 从上下文获取当前用户ID
            tags = tagsJson,
            metadata = metadataJson
        )
    }

    /**
     * 将参数模型转换为DO
     *
     * @param serviceId 服务ID
     * @param parameters 参数列表
     * @return DO列表
     */
    fun toParameterDOs(serviceId: String, parameters: List<ServiceParameter>): List<ServiceParameterDO> {
        return parameters.mapIndexed { index, parameter ->
            ServiceParameterDO(
                serviceId = serviceId,
                name = parameter.name,
                type = parameter.type,
                description = parameter.description ?: "",
                defaultValue = parameter.defaultValue,
                required = parameter.required,
                orderNum = index,
                gmtCreate = Date(),
                gmtModified = Date()
            )
        }
    }

    /**
     * 将参数DO转换为模型
     *
     * @param entities DO列表
     * @return 模型列表
     */
    fun toParameterModels(entities: List<ServiceParameterDO>): List<ServiceParameter> {
        return entities.map { entity ->
            ServiceParameter(
                name = entity.name,
                type = entity.type,
                description = entity.description,
                defaultValue = entity.defaultValue,
                required = entity.required
            )
        }.sortedBy { it.name }
    }

    /**
     * 将标签转换为DO
     *
     * @param serviceId 服务ID
     * @param tags 标签列表
     * @return DO列表
     */
    fun toTagDOs(serviceId: String, tags: List<String>): List<ServiceTagDO> {
        return tags.map { tag ->
            ServiceTagDO(
                serviceId = serviceId,
                name = tag,
                gmtCreate = Date()
            )
        }
    }

    /**
     * 将标签DO转换为标签列表
     *
     * @param entities DO列表
     * @return 标签列表
     */
    fun toTagNames(entities: List<ServiceTagDO>): List<String> {
        return entities.map { it.name }
    }
}
