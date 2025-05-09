// 启用 DevTools 的脚本
const fs = require('fs');
const path = require('path');

// 读取主进程文件
const mainJsPath = path.join(__dirname, 'src', 'main', 'main.js');
let mainJsContent = fs.readFileSync(mainJsPath, 'utf8');

// 备份原始文件
fs.writeFileSync(mainJsPath + '.bak', mainJsContent);
console.log('已备份原始 main.js 文件');

// 查找 createWindow 函数
const createWindowMatch = mainJsContent.match(/function\s+createWindow\s*\(\s*\)\s*{([^}]*?)}/s);
if (createWindowMatch) {
  const createWindowBody = createWindowMatch[1];
  
  // 添加打开 DevTools 的代码
  const newCreateWindowBody = createWindowBody + '\n\n  // 打开开发者工具\n  mainWindow.webContents.openDevTools();';
  
  // 替换函数体
  mainJsContent = mainJsContent.replace(createWindowMatch[0], `function createWindow() {${newCreateWindowBody}}`);
  
  // 修改 BrowserWindow 配置
  mainJsContent = mainJsContent.replace(
    /new\s+BrowserWindow\s*\(\s*{([^}]*?)}\s*\)/s,
    (match, config) => {
      // 检查是否已经有 webPreferences
      if (config.includes('webPreferences')) {
        return match.replace(
          /webPreferences\s*:\s*{([^}]*?)}/s,
          'webPreferences: {$1,\n    devTools: true\n  }'
        );
      } else {
        // 添加 webPreferences
        return match.replace(
          /{([^}]*?)}/s,
          '{$1,\n  webPreferences: {\n    devTools: true\n  }\n}'
        );
      }
    }
  );
  
  // 写回文件
  fs.writeFileSync(mainJsPath, mainJsContent);
  console.log('已成功修改 main.js，添加了 DevTools 支持');
} else {
  console.error('无法找到 createWindow 函数');
}
