package ai.magicdb.dataservice.core.script

import java.util.concurrent.TimeUnit

/**
 * 脚本执行器接口
 *
 * @author magicdb
 */
interface ScriptExecutor {
    /**
     * 执行脚本
     *
     * @param script 脚本内容
     * @param language 脚本语言
     * @param context 上下文
     * @param timeout 超时时间
     * @param timeUnit 时间单位
     * @return 执行结果
     */
    fun execute(script: String, language: String, context: Map<String, Any?> = emptyMap(), timeout: Long = 30000, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): Any?

    /**
     * 获取支持的语言列表
     *
     * @return 语言列表
     */
    fun getSupportedLanguages(): List<String>
}
