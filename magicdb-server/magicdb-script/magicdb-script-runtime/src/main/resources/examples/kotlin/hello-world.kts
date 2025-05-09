// 简单的Kotlin Hello World脚本
// 参数: name - 名称

// 定义问候函数
fun greet(name: String): String {
    return "Hello, $name!"
}

// 获取参数，如果没有提供则使用默认值
val inputName = bindings["name"] as? String ?: "World"

// 执行并返回结果
greet(inputName)
