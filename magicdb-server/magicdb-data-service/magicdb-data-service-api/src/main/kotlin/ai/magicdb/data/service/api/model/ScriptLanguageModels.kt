package ai.magicdb.data.service.api.model

import java.io.Serializable

/**
 * 代码补全项
 */
data class CompletionItem(
    /**
     * 显示标签
     */
    val label: String,
    
    /**
     * 插入文本
     */
    val insertText: String,
    
    /**
     * 类型
     */
    val kind: CompletionItemKind,
    
    /**
     * 详细信息
     */
    val detail: String? = null,
    
    /**
     * 文档信息
     */
    val documentation: String? = null,
    
    /**
     * 排序文本
     */
    val sortText: String? = null,
    
    /**
     * 过滤文本
     */
    val filterText: String? = null,
    
    /**
     * 是否预选
     */
    val preselect: Boolean = false,
    
    /**
     * 插入文本格式
     */
    val insertTextFormat: InsertTextFormat = InsertTextFormat.PLAIN_TEXT
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 补全项类型
 */
enum class CompletionItemKind {
    TEXT,
    METHOD,
    FUNCTION,
    CONSTRUCTOR,
    FIELD,
    VARIABLE,
    CLASS,
    INTERFACE,
    MODULE,
    PROPERTY,
    UNIT,
    VALUE,
    ENUM,
    KEYWORD,
    SNIPPET,
    COLOR,
    FILE,
    REFERENCE,
    FOLDER,
    ENUM_MEMBER,
    CONSTANT,
    STRUCT,
    EVENT,
    OPERATOR,
    TYPE_PARAMETER
}

/**
 * 插入文本格式
 */
enum class InsertTextFormat {
    PLAIN_TEXT,
    SNIPPET
}

/**
 * 诊断项
 */
data class DiagnosticItem(
    /**
     * 消息
     */
    val message: String,
    
    /**
     * 开始位置
     */
    val startPosition: Int,
    
    /**
     * 结束位置
     */
    val endPosition: Int,
    
    /**
     * 严重程度
     */
    val severity: DiagnosticSeverity,
    
    /**
     * 代码
     */
    val code: String? = null,
    
    /**
     * 源
     */
    val source: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 诊断严重程度
 */
enum class DiagnosticSeverity {
    ERROR,
    WARNING,
    INFORMATION,
    HINT
}

/**
 * 悬停信息
 */
data class HoverInfo(
    /**
     * 内容
     */
    val contents: List<String>,
    
    /**
     * 范围开始位置
     */
    val rangeStart: Int,
    
    /**
     * 范围结束位置
     */
    val rangeEnd: Int
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
