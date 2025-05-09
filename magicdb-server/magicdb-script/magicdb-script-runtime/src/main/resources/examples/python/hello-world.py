# 简单的Python Hello World脚本
# 参数: name - 名称

# 定义问候函数
def greet(name):
    return f"Hello, {name}!"

# 获取参数，如果没有提供则使用默认值
input_name = name if 'name' in globals() else "World"

# 执行并返回结果
greet(input_name)
