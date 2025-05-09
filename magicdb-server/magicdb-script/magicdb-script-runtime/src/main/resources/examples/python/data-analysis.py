# Python数据分析示例脚本
# 参数: data - 输入数据列表

# 导入模块
import statistics
import json

# 获取输入数据，如果没有提供则使用默认值
input_data = data if 'data' in globals() else [1, 2, 3, 4, 5]

# 数据分析
result = {
    "data": input_data,
    "count": len(input_data),
    "mean": statistics.mean(input_data) if input_data else 0,
    "median": statistics.median(input_data) if input_data else 0,
    "max": max(input_data) if input_data else None,
    "min": min(input_data) if input_data else None,
    "sum": sum(input_data),
    "variance": statistics.variance(input_data) if len(input_data) > 1 else 0
}

# 返回处理结果
result
