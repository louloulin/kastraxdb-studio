package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 服务流程（编排）
 *
 * @author magicdb
 */
data class ServiceFlow(
    /**
     * 流程ID
     */
    var id: String = "",
    
    /**
     * 流程名称
     */
    var name: String = "",
    
    /**
     * 流程描述
     */
    var description: String = "",
    
    /**
     * 流程节点列表
     */
    var nodes: List<FlowNode> = emptyList(),
    
    /**
     * 流程连接列表
     */
    var connections: List<FlowConnection> = emptyList(),
    
    /**
     * 流程变量
     */
    var variables: Map<String, Any?> = emptyMap(),
    
    /**
     * 流程参数
     */
    var parameters: List<FlowParameter> = emptyList(),
    
    /**
     * 创建时间
     */
    var createTime: Long = 0,
    
    /**
     * 更新时间
     */
    var updateTime: Long = 0,
    
    /**
     * 创建用户ID
     */
    var createUserId: Long = 0,
    
    /**
     * 是否启用
     */
    var enabled: Boolean = true,
    
    /**
     * 标签
     */
    var tags: List<String> = emptyList(),
    
    /**
     * 分组
     */
    var group: String = "default",
    
    /**
     * 超时时间（毫秒）
     */
    var timeout: Long = 60000,
    
    /**
     * 执行次数
     */
    var executeCount: Long = 0,
    
    /**
     * 成功次数
     */
    var successCount: Long = 0,
    
    /**
     * 失败次数
     */
    var failCount: Long = 0,
    
    /**
     * 最后执行时间
     */
    var lastExecuteTime: Long = 0,
    
    /**
     * 最后执行结果
     */
    var lastExecuteResult: Boolean = false,
    
    /**
     * 最后执行消息
     */
    var lastExecuteMessage: String = "",
    
    /**
     * 最后执行耗时（毫秒）
     */
    var lastExecuteDuration: Long = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 流程节点
 */
data class FlowNode(
    /**
     * 节点ID
     */
    var id: String = "",
    
    /**
     * 节点名称
     */
    var name: String = "",
    
    /**
     * 节点类型
     */
    var type: FlowNodeType = FlowNodeType.SERVICE,
    
    /**
     * 节点配置
     */
    var config: Map<String, Any?> = emptyMap(),
    
    /**
     * 节点位置X
     */
    var x: Int = 0,
    
    /**
     * 节点位置Y
     */
    var y: Int = 0,
    
    /**
     * 节点宽度
     */
    var width: Int = 200,
    
    /**
     * 节点高度
     */
    var height: Int = 100
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 流程连接
 */
data class FlowConnection(
    /**
     * 连接ID
     */
    var id: String = "",
    
    /**
     * 源节点ID
     */
    var sourceId: String = "",
    
    /**
     * 源节点输出端口
     */
    var sourcePort: String = "output",
    
    /**
     * 目标节点ID
     */
    var targetId: String = "",
    
    /**
     * 目标节点输入端口
     */
    var targetPort: String = "input",
    
    /**
     * 连接条件
     */
    var condition: String = "",
    
    /**
     * 连接标签
     */
    var label: String = ""
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 流程参数
 */
data class FlowParameter(
    /**
     * 参数名称
     */
    var name: String = "",
    
    /**
     * 参数类型
     */
    var type: String = "string",
    
    /**
     * 参数描述
     */
    var description: String = "",
    
    /**
     * 是否必填
     */
    var required: Boolean = false,
    
    /**
     * 默认值
     */
    var defaultValue: Any? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 流程节点类型
 */
enum class FlowNodeType {
    /**
     * 开始节点
     */
    START,
    
    /**
     * 结束节点
     */
    END,
    
    /**
     * 服务节点
     */
    SERVICE,
    
    /**
     * 条件节点
     */
    CONDITION,
    
    /**
     * 并行节点
     */
    PARALLEL,
    
    /**
     * 合并节点
     */
    MERGE,
    
    /**
     * 循环节点
     */
    LOOP,
    
    /**
     * 子流程节点
     */
    SUB_FLOW,
    
    /**
     * 脚本节点
     */
    SCRIPT,
    
    /**
     * 数据转换节点
     */
    TRANSFORM,
    
    /**
     * 延时节点
     */
    DELAY,
    
    /**
     * 错误处理节点
     */
    ERROR_HANDLER
}
