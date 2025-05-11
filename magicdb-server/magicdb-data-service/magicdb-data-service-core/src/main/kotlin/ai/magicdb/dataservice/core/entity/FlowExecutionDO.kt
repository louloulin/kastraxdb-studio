package ai.magicdb.dataservice.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * 流程执行记录实体类
 *
 * @author magicdb
 */
@TableName("data_service_flow_execution")
class FlowExecutionDO {
    
    /**
     * 执行ID
     */
    @TableId(type = IdType.INPUT)
    var id: String = ""
    
    /**
     * 流程ID
     */
    var flowId: String = ""
    
    /**
     * 流程名称
     */
    var flowName: String? = null
    
    /**
     * 执行参数（JSON格式）
     */
    var parameters: String? = null
    
    /**
     * 执行变量（JSON格式）
     */
    var variables: String? = null
    
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
     * 触发类型
     */
    var triggerType: String? = null
    
    /**
     * 触发者ID
     */
    var triggerId: String? = null
}
