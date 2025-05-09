// Kotlin数据处理示例脚本
// 参数: data - 输入数据列表

// 计算平均值
fun calculateAverage(numbers: List<Number>): Double {
    if (numbers.isEmpty()) {
        return 0.0
    }
    
    return numbers.sumOf { it.toDouble() } / numbers.size
}

// 获取输入数据，如果没有提供则使用默认值
@Suppress("UNCHECKED_CAST")
val inputData = bindings["data"] as? List<Number> ?: listOf(1, 2, 3, 4, 5)

// 处理数据
val result = mapOf(
    "data" to inputData,
    "count" to inputData.size,
    "average" to calculateAverage(inputData),
    "max" to inputData.maxOrNull(),
    "min" to inputData.minOrNull(),
    "sum" to inputData.sumOf { it.toDouble() }
)

// 返回处理结果
result
