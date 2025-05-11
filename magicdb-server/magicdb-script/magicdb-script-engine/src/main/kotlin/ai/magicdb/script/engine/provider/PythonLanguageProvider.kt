package ai.magicdb.script.engine.provider

import ai.magicdb.script.api.LanguageProvider
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.Value
import org.slf4j.LoggerFactory

/**
 * Python语言提供者
 *
 * @author magicdb
 */
class PythonLanguageProvider : LanguageProvider {
    private val logger = LoggerFactory.getLogger(PythonLanguageProvider::class.java)

    companion object {
        private val SUPPORTED_LANGUAGES = arrayOf("python", "py")
    }

    override fun support(languageName: String): Boolean {
        return SUPPORTED_LANGUAGES.any { it.equals(languageName, ignoreCase = true) }
    }

    @Throws(Exception::class)
    override fun execute(languageName: String, script: String, context: Map<String, Any?>): Any? {
        try {
            // 创建GraalVM上下文，允许所有访问权限
            Context.newBuilder()
                .allowAllAccess(true)
                .build().use { polyglotContext ->

                // 绑定上下文变量
                val bindings = polyglotContext.getBindings("python")
                context.forEach { (key, value) ->
                    bindings.putMember(key, value)
                }

                // 添加Python特定的辅助函数
                addPythonHelpers(polyglotContext)

                // 创建源代码
                val source = Source.newBuilder("python", script, "script.py").build()

                // 执行脚本
                val result = polyglotContext.eval(source)

                // 转换结果为Java对象
                return convertValue(result)
            }
        } catch (e: Exception) {
            logger.error("执行Python脚本出错: {}", e.message, e)
            throw RuntimeException("执行Python脚本出错: ${e.message}", e)
        }
    }

    /**
     * 添加Python特定的辅助函数
     */
    private fun addPythonHelpers(context: Context) {
        // 添加JSON转换辅助函数
        val jsonHelpers = """
            import json

            def to_json(obj):
                # Convert Python object to JSON string
                return json.dumps(obj)

            def from_json(json_str):
                # Convert JSON string to Python object
                return json.loads(json_str)
        """.trimIndent()

        // 执行辅助函数定义
        context.eval("python", jsonHelpers)

        // 添加日期时间辅助函数
        val datetimeHelpers = """
            import datetime

            def now():
                # Get current datetime
                return datetime.datetime.now()

            def today():
                # Get current date
                return datetime.date.today()

            def parse_date(date_str, format="%Y-%m-%d"):
                # Parse date string to date object
                return datetime.datetime.strptime(date_str, format).date()

            def parse_datetime(datetime_str, format="%Y-%m-%d %H:%M:%S"):
                # Parse datetime string to datetime object
                return datetime.datetime.strptime(datetime_str, format)
        """.trimIndent()

        // 执行辅助函数定义
        context.eval("python", datetimeHelpers)
    }

    /**
     * 将GraalVM值转换为Java对象
     */
    private fun convertValue(value: Value): Any? {
        if (value.isNull) {
            return null
        }

        return when {
            value.isString -> value.asString()
            value.isNumber -> {
                if (value.fitsInInt()) {
                    value.asInt()
                } else if (value.fitsInLong()) {
                    value.asLong()
                } else if (value.fitsInFloat()) {
                    value.asFloat()
                } else if (value.fitsInDouble()) {
                    value.asDouble()
                } else {
                    value.asDouble()
                }
            }
            value.isBoolean -> value.asBoolean()
            value.hasArrayElements() -> {
                val size = value.arraySize
                val list = ArrayList<Any?>(size.toInt())
                for (i in 0 until size) {
                    list.add(convertValue(value.getArrayElement(i)))
                }
                list
            }
            value.hasMembers() -> {
                val map = HashMap<String, Any?>()
                for (key in value.memberKeys) {
                    map[key] = convertValue(value.getMember(key))
                }
                map
            }
            value.isHostObject -> value.asHostObject()
            value.canExecute() -> value.toString()
            else -> value.toString()
        }
    }
}
