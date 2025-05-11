package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.ClusterNode
import ai.magicdb.dataservice.api.model.ClusterStatus
import ai.magicdb.dataservice.api.model.SyncResult

/**
 * 集群服务接口
 *
 * @author magicdb
 */
interface ClusterService {
    /**
     * 获取集群状态
     *
     * @return 集群状态
     */
    fun getClusterStatus(): ClusterStatus
    
    /**
     * 获取集群节点列表
     *
     * @param includeOffline 是否包含离线节点
     * @return 节点列表
     */
    fun getClusterNodes(includeOffline: Boolean = false): List<ClusterNode>
    
    /**
     * 获取当前节点信息
     *
     * @return 当前节点信息
     */
    fun getCurrentNode(): ClusterNode
    
    /**
     * 注册节点
     *
     * @param node 节点信息
     * @return 是否成功
     */
    fun registerNode(node: ClusterNode): Boolean
    
    /**
     * 更新节点状态
     *
     * @param nodeId 节点ID
     * @param online 是否在线
     * @return 是否成功
     */
    fun updateNodeStatus(nodeId: String, online: Boolean): Boolean
    
    /**
     * 移除节点
     *
     * @param nodeId 节点ID
     * @return 是否成功
     */
    fun removeNode(nodeId: String): Boolean
    
    /**
     * 同步服务
     *
     * @param serviceId 服务ID，如果为null则同步所有服务
     * @param targetNodeId 目标节点ID，如果为null则同步到所有节点
     * @return 同步结果
     */
    fun syncService(serviceId: String? = null, targetNodeId: String? = null): SyncResult
    
    /**
     * 同步服务组
     *
     * @param groupId 服务组ID
     * @param targetNodeId 目标节点ID，如果为null则同步到所有节点
     * @return 同步结果
     */
    fun syncServiceGroup(groupId: String, targetNodeId: String? = null): SyncResult
    
    /**
     * 同步所有服务
     *
     * @param targetNodeId 目标节点ID，如果为null则同步到所有节点
     * @return 同步结果
     */
    fun syncAllServices(targetNodeId: String? = null): SyncResult
    
    /**
     * 检查节点健康状态
     *
     * @param nodeId 节点ID，如果为null则检查所有节点
     * @return 健康状态（节点ID -> 是否健康）
     */
    fun checkNodeHealth(nodeId: String? = null): Map<String, Boolean>
    
    /**
     * 获取节点负载
     *
     * @param nodeId 节点ID，如果为null则获取所有节点
     * @return 负载信息（节点ID -> 负载百分比）
     */
    fun getNodeLoad(nodeId: String? = null): Map<String, Double>
    
    /**
     * 获取节点服务数量
     *
     * @param nodeId 节点ID，如果为null则获取所有节点
     * @return 服务数量（节点ID -> 服务数量）
     */
    fun getNodeServiceCount(nodeId: String? = null): Map<String, Int>
    
    /**
     * 获取节点调用次数
     *
     * @param nodeId 节点ID，如果为null则获取所有节点
     * @return 调用次数（节点ID -> 调用次数）
     */
    fun getNodeCallCount(nodeId: String? = null): Map<String, Long>
    
    /**
     * 分配服务到节点
     *
     * @param serviceId 服务ID
     * @param nodeId 节点ID
     * @return 是否成功
     */
    fun assignServiceToNode(serviceId: String, nodeId: String): Boolean
    
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
     * 启用集群模式
     *
     * @return 是否成功
     */
    fun enableClusterMode(): Boolean
    
    /**
     * 禁用集群模式
     *
     * @return 是否成功
     */
    fun disableClusterMode(): Boolean
    
    /**
     * 是否启用集群模式
     *
     * @return 是否启用
     */
    fun isClusterModeEnabled(): Boolean
}
