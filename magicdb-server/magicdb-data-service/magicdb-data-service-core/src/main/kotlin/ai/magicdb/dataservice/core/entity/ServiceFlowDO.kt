package ai.magicdb.dataservice.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * 服务流程实体类
 *
 * @author magicdb
 */
@TableName("data_service_flow")
class ServiceFlowDO {
    
    /**
     * 流程ID
     */
    @TableId(type = IdType.INPUT)
    var id: String = ""
    
    /**
     * 流程名称
     */
    var name: String = ""
    
    /**
     * 流程描述
     */
    var description: String? = null
    
    /**
     * 流程节点列表（JSON格式）
     */
    var nodes: String? = null
    
    /**
     * 流程连接列表（JSON格式）
     */
    var connections: String? = null
    
    /**
     * 流程变量（JSON格式）
     */
    var variables: String? = null
    
    /**
     * 流程参数（JSON格式）
     */
    var parameters: String? = null
    
    /**
     * 创建时间
     */
    var createTime: Long? = null
    
    /**
     * 更新时间
     */
    var updateTime: Long? = null
    
    /**
     * 创建用户ID
     */
    var createUserId: Long? = null
    
    /**
     * 是否启用
     */
    var enabled: Boolean? = true
    
    /**
     * 标签（JSON格式）
     */
    var tags: String? = null
    
    /**
     * 分组
     */
    var group: String? = "default"
    
    /**
     * 超时时间（毫秒）
     */
    var timeout: Long? = 60000
    
    /**
     * 执行次数
     */
    var executeCount: Long? = 0
    
    /**
     * 成功次数
     */
    var successCount: Long? = 0
    
    /**
     * 失败次数
     */
    var failCount: Long? = 0
    
    /**
     * 最后执行时间
     */
    var lastExecuteTime: Long? = null
    
    /**
     * 最后执行结果
     */
    var lastExecuteResult: Boolean? = null
    
    /**
     * 最后执行消息
     */
    var lastExecuteMessage: String? = null
    
    /**
     * 最后执行耗时（毫秒）
     */
    var lastExecuteDuration: Long? = null
}
