const fs = require('fs');
const path = require('path');

// 读取 package.json
const packageJsonPath = path.join(__dirname, 'package.json');
const packageJson = require(packageJsonPath);

// 添加调试脚本
packageJson.scripts.debug = 'concurrently "npm run start:web" "node debug.js"';

// 写回 package.json
fs.writeFileSync(packageJsonPath, JSON.stringify(packageJson, null, 2));

console.log('已成功添加调试脚本到 package.json');
