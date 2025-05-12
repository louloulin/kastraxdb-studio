package ai.magicdb.data.service.api.model

import java.io.Serializable
import java.util.Date

/**
 * 调试会话
 */
data class DebugSession(
    /**
     * 会话 ID
     */
    val id: String,
    
    /**
     * 服务 ID
     */
    val serviceId: String,
    
    /**
     * 脚本内容
     */
    val script: String,
    
    /**
     * 脚本语言
     */
    val language: String,
    
    /**
     * 参数
     */
    val parameters: Map<String, Any?>,
    
    /**
     * 断点列表
     */
    val breakpoints: List<BreakpointInfo> = emptyList(),
    
    /**
     * 当前行号
     */
    val currentLine: Int? = null,
    
    /**
     * 当前列号
     */
    val currentColumn: Int? = null,
    
    /**
     * 状态
     */
    val status: DebugSessionStatus = DebugSessionStatus.CREATED,
    
    /**
     * 创建时间
     */
    val createTime: Date = Date(),
    
    /**
     * 最后更新时间
     */
    val updateTime: Date = Date()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 调试会话状态
 */
enum class DebugSessionStatus {
    /**
     * 已创建
     */
    CREATED,
    
    /**
     * 运行中
     */
    RUNNING,
    
    /**
     * 已暂停
     */
    PAUSED,
    
    /**
     * 已完成
     */
    COMPLETED,
    
    /**
     * 出错
     */
    ERROR,
    
    /**
     * 已终止
     */
    TERMINATED
}

/**
 * 断点信息
 */
data class BreakpointInfo(
    /**
     * 断点 ID
     */
    val id: String,
    
    /**
     * 行号
     */
    val line: Int,
    
    /**
     * 列号
     */
    val column: Int? = null,
    
    /**
     * 条件
     */
    val condition: String? = null,
    
    /**
     * 命中次数
     */
    val hitCount: Int = 0,
    
    /**
     * 是否启用
     */
    val enabled: Boolean = true
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 调试步骤结果
 */
data class DebugStepResult(
    /**
     * 是否成功
     */
    val success: Boolean,
    
    /**
     * 当前行号
     */
    val line: Int? = null,
    
    /**
     * 当前列号
     */
    val column: Int? = null,
    
    /**
     * 状态
     */
    val status: DebugSessionStatus,
    
    /**
     * 变量列表
     */
    val variables: List<VariableInfo> = emptyList(),
    
    /**
     * 调用栈
     */
    val callStack: List<Map<String, Any?>> = emptyList(),
    
    /**
     * 错误消息
     */
    val errorMessage: String? = null,
    
    /**
     * 结果
     */
    val result: Any? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 变量信息
 */
data class VariableInfo(
    /**
     * 变量名
     */
    val name: String,
    
    /**
     * 变量值
     */
    val value: Any?,
    
    /**
     * 变量类型
     */
    val type: String,
    
    /**
     * 是否可展开
     */
    val expandable: Boolean = false,
    
    /**
     * 子变量
     */
    val children: List<VariableInfo> = emptyList(),
    
    /**
     * 作用域
     */
    val scope: VariableScope = VariableScope.LOCAL
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 变量作用域
 */
enum class VariableScope {
    /**
     * 局部变量
     */
    LOCAL,
    
    /**
     * 全局变量
     */
    GLOBAL,
    
    /**
     * 闭包变量
     */
    CLOSURE,
    
    /**
     * 参数
     */
    PARAMETER
}
