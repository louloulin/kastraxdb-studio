package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.CompletionItem
import ai.magicdb.data.service.api.model.DiagnosticItem
import ai.magicdb.data.service.api.model.HoverInfo

/**
 * 脚本语言服务接口
 * 提供脚本语法分析、代码补全、错误提示等功能
 */
interface ScriptLanguageService {
    /**
     * 获取代码补全项
     *
     * @param language 脚本语言
     * @param script 脚本内容
     * @param position 光标位置
     * @return 补全项列表
     */
    fun getCompletionItems(language: String, script: String, position: Int): List<CompletionItem>
    
    /**
     * 获取诊断信息
     *
     * @param language 脚本语言
     * @param script 脚本内容
     * @return 诊断信息列表
     */
    fun getDiagnostics(language: String, script: String): List<DiagnosticItem>
    
    /**
     * 获取悬停信息
     *
     * @param language 脚本语言
     * @param script 脚本内容
     * @param position 光标位置
     * @return 悬停信息
     */
    fun getHoverInfo(language: String, script: String, position: Int): HoverInfo?
    
    /**
     * 格式化代码
     *
     * @param language 脚本语言
     * @param script 脚本内容
     * @return 格式化后的代码
     */
    fun formatCode(language: String, script: String): String
    
    /**
     * 获取语言支持的功能
     *
     * @param language 脚本语言
     * @return 支持的功能列表
     */
    fun getLanguageCapabilities(language: String): Map<String, Boolean>
}
