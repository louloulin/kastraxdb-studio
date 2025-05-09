// 数据处理示例脚本
// 参数: data - 输入数据数组

// 计算平均值
function calculateAverage(numbers) {
    if (!numbers || numbers.length === 0) {
        return 0;
    }
    
    var sum = 0;
    for (var i = 0; i < numbers.length; i++) {
        sum += numbers[i];
    }
    
    return sum / numbers.length;
}

// 查找最大值
function findMax(numbers) {
    if (!numbers || numbers.length === 0) {
        return null;
    }
    
    var max = numbers[0];
    for (var i = 1; i < numbers.length; i++) {
        if (numbers[i] > max) {
            max = numbers[i];
        }
    }
    
    return max;
}

// 查找最小值
function findMin(numbers) {
    if (!numbers || numbers.length === 0) {
        return null;
    }
    
    var min = numbers[0];
    for (var i = 1; i < numbers.length; i++) {
        if (numbers[i] < min) {
            min = numbers[i];
        }
    }
    
    return min;
}

// 处理输入数据
var inputData = data || [1, 2, 3, 4, 5];
var result = {
    data: inputData,
    count: inputData.length,
    average: calculateAverage(inputData),
    max: findMax(inputData),
    min: findMin(inputData)
};

// 返回处理结果
result;
