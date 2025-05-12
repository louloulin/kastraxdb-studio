package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 服务文档模型
 */
data class ServiceDocument(
    /**
     * 文档标题
     */
    val title: String,

    /**
     * 文档描述
     */
    val description: String? = null,

    /**
     * 服务 ID
     */
    val serviceId: String,

    /**
     * 服务类型
     */
    val serviceType: String,

    /**
     * 请求路径
     */
    val path: String,

    /**
     * HTTP 方法
     */
    val method: String = "POST",

    /**
     * 参数列表
     */
    val parameters: List<ParameterDocument> = emptyList(),

    /**
     * 返回字段列表
     */
    val returnFields: List<FieldDocument> = emptyList(),

    /**
     * 示例列表
     */
    val examples: List<ServiceDocumentExample> = emptyList(),

    /**
     * 注意事项
     */
    val notes: String? = null,

    /**
     * 标签
     */
    val tags: List<String> = emptyList(),

    /**
     * 版本
     */
    val version: String = "1.0"
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 参数文档
 */
data class ParameterDocument(
    /**
     * 参数名
     */
    val name: String,

    /**
     * 参数类型
     */
    val type: String,

    /**
     * 是否必填
     */
    val required: Boolean = false,

    /**
     * 默认值
     */
    val defaultValue: String? = null,

    /**
     * 参数描述
     */
    val description: String? = null,

    /**
     * 示例值
     */
    val example: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 字段文档
 */
data class FieldDocument(
    /**
     * 字段名
     */
    val name: String,

    /**
     * 字段类型
     */
    val type: String,

    /**
     * 字段描述
     */
    val description: String? = null,

    /**
     * 示例值
     */
    val example: String? = null,

    /**
     * 子字段列表（用于对象类型）
     */
    val fields: List<FieldDocument> = emptyList()
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 服务文档示例
 */
data class ServiceDocumentExample(
    /**
     * 示例名称
     */
    val name: String,

    /**
     * 请求示例
     */
    val request: String,

    /**
     * 响应示例
     */
    val response: String,

    /**
     * 示例描述
     */
    val description: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
