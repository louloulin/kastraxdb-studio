package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.core.cluster.ClusterManager
import ai.magicdb.data.service.core.cluster.LoadBalancer
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 集群控制器
 * 用于管理和监控集群状态
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/cluster")
class ClusterController(
    private val clusterManager: ClusterManager,
    private val loadBalancer: LoadBalancer
) {
    
    /**
     * 获取集群节点信息
     */
    @GetMapping("/nodes")
    fun getNodes(): DataResult<List<ClusterManager.NodeInfo>> {
        val nodes = clusterManager.getActiveNodes()
        return DataResult.of(nodes)
    }
    
    /**
     * 获取当前节点信息
     */
    @GetMapping("/current-node")
    fun getCurrentNode(): DataResult<ClusterManager.NodeInfo> {
        val nodeInfo = clusterManager.getCurrentNodeInfo()
        return DataResult.of(nodeInfo)
    }
    
    /**
     * 获取集群状态信息
     */
    @GetMapping("/status")
    fun getClusterStatus(): DataResult<Map<String, Any>> {
        val nodes = clusterManager.getActiveNodes()
        val currentNodeId = clusterManager.getCurrentNodeId()
        
        val result = mutableMapOf<String, Any>()
        result["nodeCount"] = nodes.size
        result["currentNodeId"] = currentNodeId
        result["nodes"] = nodes
        
        return DataResult.of(result)
    }
}
