package ai.magicdb.dataservice.api.model

import java.io.Serializable

/**
 * 流程参数模型
 *
 * @author magicdb
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
    var defaultValue: Any? = null,
    
    /**
     * 参数验证规则
     */
    var validation: Map<String, Any?> = emptyMap(),
    
    /**
     * 参数元数据
     */
    var metadata: Map<String, Any?> = emptyMap()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
