// 简单的Hello World脚本
// 参数: name - 名称

// 返回问候消息
function greet(name) {
    return "Hello, " + name + "!";
}

// 执行并返回结果
var message = greet(name || "World");
message;
