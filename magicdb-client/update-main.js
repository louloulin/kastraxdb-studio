const fs = require('fs');
const path = require('path');

// 读取主进程文件
const mainJsPath = path.join(__dirname, 'src', 'main', 'main.js');
let mainJsContent = fs.readFileSync(mainJsPath, 'utf8');

// 查找 BrowserWindow 配置
const browserWindowRegex = /new BrowserWindow\(\{([^}]*)\}\)/s;
const match = mainJsContent.match(browserWindowRegex);

if (match) {
  // 添加 webPreferences 配置
  const existingConfig = match[1];
  const newConfig = existingConfig + `,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false,
      devTools: true
    }`;
  
  // 替换配置
  mainJsContent = mainJsContent.replace(browserWindowRegex, `new BrowserWindow({${newConfig}})`);
  
  // 添加打开 DevTools 的代码
  const loadURLRegex = /mainWindow\.loadURL\([^)]*\);/;
  if (mainJsContent.match(loadURLRegex)) {
    mainJsContent = mainJsContent.replace(
      loadURLRegex, 
      `$&\n  // 打开开发者工具\n  mainWindow.webContents.openDevTools();`
    );
  }
  
  // 写回文件
  fs.writeFileSync(mainJsPath, mainJsContent);
  console.log('已成功修改 main.js，添加了调试功能');
} else {
  console.error('无法找到 BrowserWindow 配置');
}
