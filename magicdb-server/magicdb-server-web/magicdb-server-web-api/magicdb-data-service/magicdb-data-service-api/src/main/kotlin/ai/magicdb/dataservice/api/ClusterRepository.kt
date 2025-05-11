package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.ClusterNode
import ai.magicdb.dataservice.api.model.ClusterStatus

/**
 * 集群存储库接口
 *
 * @author magicdb
 */
interface ClusterRepository {
    /**
     * 保存节点信息
     *
     * @param node 节点信息
     * @return 是否成功
     */
    fun saveNode(node: ClusterNode): Boolean
    
    /**
     * 更新节点信息
     *
     * @param node 节点信息
     * @return 是否成功
     */
    fun updateNode(node: ClusterNode): Boolean
    
    /**
     * 删除节点
     *
     * @param nodeId 节点ID
     * @return 是否成功
     */
    fun deleteNode(nodeId: String): Boolean
    
    /**
     * 获取节点信息
     *
     * @param nodeId 节点ID
     * @return 节点信息
     */
    fun getNode(nodeId: String): ClusterNode?
    
    /**
     * 获取所有节点
     *
     * @param includeOffline 是否包含离线节点
     * @return 节点列表
     */
    fun getAllNodes(includeOffline: Boolean = false): List<ClusterNode>
    
    /**
     * 更新节点状态
     *
     * @param nodeId 节点ID
     * @param online 是否在线
     * @return 是否成功
     */
    fun updateNodeStatus(nodeId: String, online: Boolean): Boolean
    
    /**
     * 更新节点心跳时间
     *
     * @param nodeId 节点ID
     * @return 是否成功
     */
    fun updateNodeHeartbeat(nodeId: String): Boolean
    
    /**
     * 获取主节点
     *
     * @return 主节点信息
     */
    fun getMasterNode(): ClusterNode?
    
    /**
     * 设置主节点
     *
     * @param nodeId 节点ID
     * @return 是否成功
     */
    fun setMasterNode(nodeId: String): Boolean
    
    /**
     * 保存集群状态
     *
     * @param status 集群状态
     * @return 是否成功
     */
    fun saveClusterStatus(status: ClusterStatus): Boolean
    
    /**
     * 获取集群状态
     *
     * @return 集群状态
     */
    fun getClusterStatus(): ClusterStatus?
    
    /**
     * 保存服务节点映射
     *
     * @param serviceId 服务ID
     * @param nodeId 节点ID
     * @return 是否成功
     */
    fun saveServiceNodeMapping(serviceId: String, nodeId: String): Boolean
    
    /**
     * 删除服务节点映射
     *
     * @param serviceId 服务ID
     * @param nodeId 节点ID
     * @return 是否成功
     */
    fun deleteServiceNodeMapping(serviceId: String, nodeId: String): Boolean
    
    /**
     * 获取服务所在节点
     *
     * @param serviceId 服务ID
     * @return 节点ID列表
     */
    fun getServiceNodes(serviceId: String): List<String>
    
    /**
     * 获取节点上的服务
     *
     * @param nodeId 节点ID
     * @return 服务ID列表
     */
    fun getNodeServices(nodeId: String): List<String>
    
    /**
     * 保存集群配置
     *
     * @param key 配置键
     * @param value 配置值
     * @return 是否成功
     */
    fun saveClusterConfig(key: String, value: String): Boolean
    
    /**
     * 获取集群配置
     *
     * @param key 配置键
     * @return 配置值
     */
    fun getClusterConfig(key: String): String?
    
    /**
     * 获取所有集群配置
     *
     * @return 配置映射
     */
    fun getAllClusterConfig(): Map<String, String>
    
    /**
     * 删除集群配置
     *
     * @param key 配置键
     * @return 是否成功
     */
    fun deleteClusterConfig(key: String): Boolean
}
