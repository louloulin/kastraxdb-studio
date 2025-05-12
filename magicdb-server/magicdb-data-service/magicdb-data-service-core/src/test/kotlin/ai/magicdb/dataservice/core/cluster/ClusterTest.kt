package ai.magicdb.dataservice.core.cluster

import ai.magicdb.dataservice.api.model.DataService
import ai.magicdb.dataservice.api.model.ServiceGroup
import ai.magicdb.dataservice.api.model.ServiceResult
import ai.magicdb.dataservice.core.cache.RedisDataServiceCacheManager
import ai.magicdb.dataservice.core.config.RedisConfig
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

/**
 * 集群测试
 *
 * @author magicdb
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [RedisConfig::class, ServiceSynchronizer::class, ClusterManager::class, LoadBalancer::class, RedisDataServiceCacheManager::class])
class ClusterTest {

    @MockBean
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    @MockBean
    private lateinit var redisMessageListenerContainer: RedisMessageListenerContainer

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var serviceSynchronizer: ServiceSynchronizer

    @Autowired
    private lateinit var clusterManager: ClusterManager

    @Autowired
    private lateinit var loadBalancer: LoadBalancer

    @Autowired
    private lateinit var redisCacheManager: RedisDataServiceCacheManager

    @Test
    fun testServiceSynchronizer() {
        // 创建测试数据
        val service = DataService(
            id = "test-service",
            name = "测试服务",
            description = "测试服务描述",
            script = "return 'Hello World';",
            language = "js",
            createTime = Date(),
            updateTime = Date()
        )

        // 创建测试监听器
        val listener = object : ServiceSynchronizer.ServiceChangeListener {
            var serviceChanged = false
            var serviceDeleted = false
            var groupChanged = false
            var groupDeleted = false
            var cacheCleared = false
            var serviceCacheCleared = false

            override fun onServiceChanged(changeType: ServiceSynchronizer.ChangeType, service: DataService) {
                serviceChanged = true
            }

            override fun onServiceDeleted(serviceId: String) {
                serviceDeleted = true
            }

            override fun onGroupChanged(changeType: ServiceSynchronizer.ChangeType, group: ServiceGroup) {
                groupChanged = true
            }

            override fun onGroupDeleted(groupId: String) {
                groupDeleted = true
            }

            override fun onServiceCacheCleared(serviceId: String) {
                serviceCacheCleared = true
            }

            override fun onCacheCleared() {
                cacheCleared = true
            }
        }

        // 注册监听器
        serviceSynchronizer.addListener("test-listener", listener)

        // 模拟接收消息
        val message = ServiceSynchronizer.ServiceChangeMessage(
            nodeId = "other-node",
            changeType = ServiceSynchronizer.ChangeType.CREATE,
            entityType = ServiceSynchronizer.EntityType.SERVICE,
            entityId = service.id,
            entityJson = objectMapper.writeValueAsString(service)
        )

        // 处理消息
        serviceSynchronizer.onMessage(message)

        // 验证结果
        assertTrue(listener.serviceChanged)
        assertFalse(listener.serviceDeleted)
        assertFalse(listener.groupChanged)
        assertFalse(listener.groupDeleted)
        assertFalse(listener.cacheCleared)
        assertFalse(listener.serviceCacheCleared)

        // 移除监听器
        serviceSynchronizer.removeListener("test-listener")
    }

    @Test
    fun testClusterManager() {
        // 获取当前节点ID
        val nodeId = clusterManager.getCurrentNodeId()

        // 验证结果
        assertNotNull(nodeId)

        // 获取当前节点信息
        val nodeInfo = clusterManager.getCurrentNodeInfo()

        // 验证结果
        assertNotNull(nodeInfo)
        assertEquals(nodeId, nodeInfo.id)

        // 模拟活跃节点
        val nodes = listOf(nodeInfo)
        `when`(redisTemplate.keys(anyString())).thenReturn(setOf("data_service:cluster:node:$nodeId"))
        `when`(redisTemplate.opsForValue().get(anyString())).thenReturn(nodeInfo)

        // 获取活跃节点
        val activeNodes = clusterManager.getActiveNodes()

        // 验证结果
        assertEquals(1, activeNodes.size)
        assertEquals(nodeId, activeNodes[0].id)
    }

    @Test
    fun testLoadBalancer() {
        // 模拟活跃节点
        val nodeId = clusterManager.getCurrentNodeId()
        val nodeInfo = clusterManager.getCurrentNodeInfo()
        val nodes = listOf(nodeInfo)
        `when`(redisTemplate.keys(anyString())).thenReturn(setOf("data_service:cluster:node:$nodeId"))
        `when`(redisTemplate.opsForValue().get(anyString())).thenReturn(nodeInfo)

        // 选择节点
        val selectedNode = loadBalancer.selectNode("test-service")

        // 验证结果
        assertEquals(nodeId, selectedNode)
    }

    @Test
    fun testRedisCacheManager() {
        // 模拟缓存结果
        val cacheKey = "test-cache-key"
        val cacheValue = ServiceResult(true, "test-cache-value")

        // 测试缓存操作
        redisCacheManager.put(cacheKey, cacheValue, 1000)

        // 验证方法调用
        verify(redisTemplate.opsForValue()).set(eq("data_service:cache:$cacheKey"), any(), eq(1000L), any())
    }
}
