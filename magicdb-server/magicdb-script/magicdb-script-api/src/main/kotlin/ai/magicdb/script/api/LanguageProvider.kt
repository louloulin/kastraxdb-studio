package ai.magicdb.script.api

/**
 * 自定义语言接口
 *
 * @author magicdb
 */
interface LanguageProvider {
    /**
     * 是否支持该语言
     *
     * @param languageName 语言名
     * @return 是否支持
     */
    fun support(languageName: String): Boolean

    /**
     * 执行具体脚本
     *
     * @param languageName 语言类型
     * @param script       脚本内容
     * @param context      当前环境中的变量信息
     * @return 执行结果
     * @throws Exception 执行过程中抛出的异常
     */
    @Throws(Exception::class)
    fun execute(languageName: String, script: String, context: Map<String, Any?>): Any?
}
