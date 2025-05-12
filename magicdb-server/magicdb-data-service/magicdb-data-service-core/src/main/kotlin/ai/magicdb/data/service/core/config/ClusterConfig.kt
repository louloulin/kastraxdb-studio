package ai.magicdb.data.service.core.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * 集群配置
 *
 * @author magicdb
 */
@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "magicdb.data-service.cluster")
class ClusterConfig {
    
    /**
     * 是否启用集群
     */
    var enabled: Boolean = false
    
    /**
     * 集群名称
     */
    var name: String = "magicdb-data-service-cluster"
    
    /**
     * 负载均衡策略
     */
    var loadBalancingStrategy: String = "ROUND_ROBIN"
    
    /**
     * 节点心跳间隔（毫秒）
     */
    var heartbeatInterval: Long = 10000
    
    /**
     * 节点过期时间（毫秒）
     */
    var nodeExpireTime: Long = 30000
    
    /**
     * Redis配置
     */
    val redis = RedisConfig()
    
    /**
     * Redis配置
     */
    class RedisConfig {
        /**
         * 是否启用Redis
         */
        var enabled: Boolean = false
        
        /**
         * Redis主机
         */
        var host: String = "localhost"
        
        /**
         * Redis端口
         */
        var port: Int = 6379
        
        /**
         * Redis密码
         */
        var password: String? = null
        
        /**
         * Redis数据库
         */
        var database: Int = 0
        
        /**
         * 连接超时时间（毫秒）
         */
        var timeout: Long = 5000
    }
}
