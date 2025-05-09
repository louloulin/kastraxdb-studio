package ai.magicdb.script.engine.compiler

import ai.magicdb.script.engine.cache.CompiledScript
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.Value
import org.slf4j.LoggerFactory

/**
 * GraalVM脚本编译器
 *
 * @author magicdb
 */
class GraalVMScriptCompiler : ScriptCompiler() {
    private val logger = LoggerFactory.getLogger(GraalVMScriptCompiler::class.java)
    
    override fun doCompile(script: String, language: String): Any {
        try {
            // 创建GraalVM上下文
            Context.newBuilder()
                .allowAllAccess(true)
                .build().use { context ->
                
                // 创建源代码
                val source = Source.newBuilder(language, script, "script.$language").build()
                
                // 解析源代码（预编译）
                val parsedSource = context.parse(source)
                
                // 返回解析后的源代码
                return parsedSource
            }
        } catch (e: Exception) {
            logger.error("编译脚本出错: {}", e.message, e)
            throw RuntimeException("编译脚本出错: ${e.message}", e)
        }
    }
    
    override fun doExecute(compiledScript: CompiledScript, context: Map<String, Any?>): Any? {
        try {
            // 创建GraalVM上下文
            Context.newBuilder()
                .allowAllAccess(true)
                .build().use { polyglotContext ->
                
                // 绑定上下文变量
                val bindings = polyglotContext.getBindings(compiledScript.language)
                context.forEach { (key, value) ->
                    bindings.putMember(key, value)
                }
                
                // 执行已编译的脚本
                val parsedSource = compiledScript.compiledObject as org.graalvm.polyglot.Source
                val result = polyglotContext.eval(parsedSource)
                
                // 转换结果为Java对象
                return convertValue(result)
            }
        } catch (e: Exception) {
            logger.error("执行脚本出错: {}", e.message, e)
            throw RuntimeException("执行脚本出错: ${e.message}", e)
        }
    }
    
    /**
     * 转换GraalVM值为Java对象
     *
     * @param value GraalVM值
     * @return Java对象
     */
    private fun convertValue(value: Value): Any? {
        if (value.isNull) {
            return null
        }
        
        if (value.isBoolean) {
            return value.asBoolean()
        }
        
        if (value.isNumber) {
            if (value.fitsInInt()) {
                return value.asInt()
            }
            if (value.fitsInLong()) {
                return value.asLong()
            }
            if (value.fitsInDouble()) {
                return value.asDouble()
            }
            return value.asFloat()
        }
        
        if (value.isString) {
            return value.asString()
        }
        
        if (value.hasArrayElements()) {
            val size = value.arraySize
            val list = ArrayList<Any?>(size.toInt())
            for (i in 0 until size) {
                list.add(convertValue(value.getArrayElement(i)))
            }
            return list
        }
        
        if (value.hasMembers()) {
            val map = HashMap<String, Any?>()
            for (key in value.memberKeys) {
                map[key] = convertValue(value.getMember(key))
            }
            return map
        }
        
        // 其他类型，尝试转换为Java对象
        return value.`as`(Any::class.java)
    }
}
