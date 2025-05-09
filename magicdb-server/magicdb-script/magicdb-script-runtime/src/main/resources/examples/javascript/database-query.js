// 数据库查询示例脚本
// 参数: 
//   - tableName: 表名
//   - condition: 查询条件
//   - limit: 限制返回记录数

// 模拟数据库连接
function getConnection() {
    // 在实际应用中，这里会使用JDBC或其他数据库连接
    // 这里仅作为示例，返回一个模拟的连接对象
    return {
        query: function(sql, params) {
            console.log("执行SQL: " + sql);
            console.log("参数: " + JSON.stringify(params));
            
            // 模拟查询结果
            return [
                { id: 1, name: "张三", age: 30 },
                { id: 2, name: "李四", age: 25 },
                { id: 3, name: "王五", age: 35 }
            ];
        },
        close: function() {
            console.log("关闭连接");
        }
    };
}

// 构建SQL查询
function buildQuery(tableName, condition, limit) {
    var sql = "SELECT * FROM " + tableName;
    
    if (condition) {
        sql += " WHERE " + condition;
    }
    
    if (limit) {
        sql += " LIMIT " + limit;
    }
    
    return sql;
}

// 执行查询
function executeQuery(tableName, condition, limit) {
    var connection = null;
    try {
        connection = getConnection();
        var sql = buildQuery(tableName, condition, limit);
        var params = {};
        
        return connection.query(sql, params);
    } finally {
        if (connection) {
            connection.close();
        }
    }
}

// 处理输入参数
var table = tableName || "users";
var whereClause = condition;
var rowLimit = limit || 10;

// 执行查询并返回结果
var result = executeQuery(table, whereClause, rowLimit);
result;
