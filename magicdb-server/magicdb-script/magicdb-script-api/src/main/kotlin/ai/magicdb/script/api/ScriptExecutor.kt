package ai.magicdb.script.api

/**
 * 脚本执行器接口
 *
 * @author magicdb
 */
interface ScriptExecutor {
    /**
     * 执行脚本
     *
     * @param languageName 语言类型
     * @param script       脚本内容
     * @param context      当前环境中的变量信息
     * @return 执行结果
     * @throws Exception 执行过程中抛出的异常
     */
    @Throws(Exception::class)
    fun execute(languageName: String, script: String, context: Map<String, Any?>): Any?

    /**
     * 获取支持的语言列表
     *
     * @return 支持的语言列表
     */
    fun getSupportedLanguages(): Array<String>
}
