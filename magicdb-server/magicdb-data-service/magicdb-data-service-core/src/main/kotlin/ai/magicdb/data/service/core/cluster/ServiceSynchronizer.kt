package ai.magicdb.data.service.core.cluster

import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceGroup
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct

/**
 * 服务同步器
 * 负责在集群环境中同步数据服务的变更
 *
 * @author magicdb
 */
@Component
class ServiceSynchronizer(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val redisMessageListenerContainer: RedisMessageListenerContainer,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(ServiceSynchronizer::class.java)
    
    // 节点ID
    private val nodeId = UUID.randomUUID().toString()
    
    // 服务变更通道
    private val SERVICE_CHANGE_TOPIC = "data_service:change"
    
    // 服务变更监听器
    private val listeners = ConcurrentHashMap<String, ServiceChangeListener>()
    
    /**
     * 初始化
     */
    @PostConstruct
    fun init() {
        // 注册消息监听器
        val listenerAdapter = MessageListenerAdapter(this, "onMessage")
        redisMessageListenerContainer.addMessageListener(listenerAdapter, ChannelTopic(SERVICE_CHANGE_TOPIC))
        
        logger.info("服务同步器初始化完成，节点ID: {}", nodeId)
    }
    
    /**
     * 发布服务变更消息
     *
     * @param changeType 变更类型
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param entity 实体对象
     */
    fun publishServiceChange(changeType: ChangeType, entityType: EntityType, entityId: String, entity: Any? = null) {
        try {
            // 创建变更消息
            val message = ServiceChangeMessage(
                nodeId = nodeId,
                changeType = changeType,
                entityType = entityType,
                entityId = entityId,
                entityJson = entity?.let { objectMapper.writeValueAsString(it) }
            )
            
            // 发布消息
            redisTemplate.convertAndSend(SERVICE_CHANGE_TOPIC, message)
            
            logger.debug("发布服务变更消息: {}", message)
        } catch (e: Exception) {
            logger.error("发布服务变更消息失败", e)
        }
    }
    
    /**
     * 接收消息
     *
     * @param message 消息
     */
    fun onMessage(message: ServiceChangeMessage) {
        try {
            // 忽略自己发布的消息
            if (message.nodeId == nodeId) {
                return
            }
            
            logger.debug("接收到服务变更消息: {}", message)
            
            // 通知监听器
            listeners.values.forEach { listener ->
                try {
                    when (message.entityType) {
                        EntityType.SERVICE -> {
                            val service = message.entityJson?.let {
                                objectMapper.readValue(it, DataService::class.java)
                            }
                            
                            when (message.changeType) {
                                ChangeType.CREATE, ChangeType.UPDATE -> {
                                    if (service != null) {
                                        listener.onServiceChanged(message.changeType, service)
                                    }
                                }
                                ChangeType.DELETE -> {
                                    listener.onServiceDeleted(message.entityId)
                                }
                            }
                        }
                        EntityType.GROUP -> {
                            val group = message.entityJson?.let {
                                objectMapper.readValue(it, ServiceGroup::class.java)
                            }
                            
                            when (message.changeType) {
                                ChangeType.CREATE, ChangeType.UPDATE -> {
                                    if (group != null) {
                                        listener.onGroupChanged(message.changeType, group)
                                    }
                                }
                                ChangeType.DELETE -> {
                                    listener.onGroupDeleted(message.entityId)
                                }
                            }
                        }
                        EntityType.CACHE -> {
                            when (message.changeType) {
                                ChangeType.DELETE -> {
                                    if (message.entityId == "all") {
                                        listener.onCacheCleared()
                                    } else {
                                        listener.onServiceCacheCleared(message.entityId)
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                } catch (e: Exception) {
                    logger.error("处理服务变更消息失败: {}", message, e)
                }
            }
        } catch (e: Exception) {
            logger.error("处理服务变更消息失败", e)
        }
    }
    
    /**
     * 添加服务变更监听器
     *
     * @param listenerId 监听器ID
     * @param listener 监听器
     */
    fun addListener(listenerId: String, listener: ServiceChangeListener) {
        listeners[listenerId] = listener
        logger.debug("添加服务变更监听器: {}", listenerId)
    }
    
    /**
     * 移除服务变更监听器
     *
     * @param listenerId 监听器ID
     */
    fun removeListener(listenerId: String) {
        listeners.remove(listenerId)
        logger.debug("移除服务变更监听器: {}", listenerId)
    }
    
    /**
     * 变更类型
     */
    enum class ChangeType {
        CREATE, UPDATE, DELETE
    }
    
    /**
     * 实体类型
     */
    enum class EntityType {
        SERVICE, GROUP, CACHE
    }
    
    /**
     * 服务变更消息
     */
    data class ServiceChangeMessage(
        val nodeId: String,
        val changeType: ChangeType,
        val entityType: EntityType,
        val entityId: String,
        val entityJson: String? = null
    )
    
    /**
     * 服务变更监听器
     */
    interface ServiceChangeListener {
        /**
         * 服务变更
         *
         * @param changeType 变更类型
         * @param service 服务
         */
        fun onServiceChanged(changeType: ChangeType, service: DataService)
        
        /**
         * 服务删除
         *
         * @param serviceId 服务ID
         */
        fun onServiceDeleted(serviceId: String)
        
        /**
         * 分组变更
         *
         * @param changeType 变更类型
         * @param group 分组
         */
        fun onGroupChanged(changeType: ChangeType, group: ServiceGroup)
        
        /**
         * 分组删除
         *
         * @param groupId 分组ID
         */
        fun onGroupDeleted(groupId: String)
        
        /**
         * 服务缓存清除
         *
         * @param serviceId 服务ID
         */
        fun onServiceCacheCleared(serviceId: String)
        
        /**
         * 所有缓存清除
         */
        fun onCacheCleared()
    }
}
