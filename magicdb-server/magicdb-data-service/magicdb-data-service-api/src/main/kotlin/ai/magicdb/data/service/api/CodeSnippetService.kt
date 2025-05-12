package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.CodeSnippet

/**
 * 代码片段服务接口
 * 提供代码片段相关功能
 */
interface CodeSnippetService {
    /**
     * 获取所有代码片段
     *
     * @param language 语言
     * @return 代码片段列表
     */
    fun getAllSnippets(language: String? = null): List<CodeSnippet>
    
    /**
     * 获取代码片段
     *
     * @param id 代码片段 ID
     * @return 代码片段
     */
    fun getSnippet(id: String): CodeSnippet?
    
    /**
     * 创建代码片段
     *
     * @param snippet 代码片段
     * @return 创建后的代码片段
     */
    fun createSnippet(snippet: CodeSnippet): CodeSnippet
    
    /**
     * 更新代码片段
     *
     * @param id 代码片段 ID
     * @param snippet 代码片段
     * @return 更新后的代码片段
     */
    fun updateSnippet(id: String, snippet: CodeSnippet): CodeSnippet?
    
    /**
     * 删除代码片段
     *
     * @param id 代码片段 ID
     * @return 是否删除成功
     */
    fun deleteSnippet(id: String): Boolean
    
    /**
     * 搜索代码片段
     *
     * @param keyword 关键字
     * @param language 语言
     * @return 代码片段列表
     */
    fun searchSnippets(keyword: String, language: String? = null): List<CodeSnippet>
}
