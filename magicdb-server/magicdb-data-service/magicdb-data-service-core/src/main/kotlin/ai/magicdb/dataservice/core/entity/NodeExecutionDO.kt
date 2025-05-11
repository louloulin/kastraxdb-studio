package ai.magicdb.dataservice.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * 节点执行记录实体类
 *
 * @author magicdb
 */
@TableName("data_service_node_execution")
class NodeExecutionDO {
    
    /**
     * 执行ID
     */
    @TableId(type = IdType.INPUT)
    var id: String = ""
    
    /**
     * 流程执行ID
     */
    var flowExecutionId: String = ""
    
    /**
     * 节点ID
     */
    var nodeId: String = ""
    
    /**
     * 节点名称
     */
    var nodeName: String? = null
    
    /**
     * 节点类型
     */
    var nodeType: String? = null
    
    /**
     * 开始时间
     */
    var startTime: Long? = null
    
    /**
     * 结束时间
     */
    var endTime: Long? = null
    
    /**
     * 执行耗时（毫秒）
     */
    var duration: Long? = null
    
    /**
     * 执行状态
     */
    var status: String? = null
    
    /**
     * 执行结果（JSON格式）
     */
    var result: String? = null
    
    /**
     * 错误消息
     */
    var errorMessage: String? = null
    
    /**
     * 输入数据（JSON格式）
     */
    var input: String? = null
    
    /**
     * 输出数据（JSON格式）
     */
    var output: String? = null
}
