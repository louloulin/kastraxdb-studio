// 修复渲染问题的脚本
const { spawn } = require('child_process');
const fs = require('fs');
const path = require('path');

// 修改 main.js 文件，添加更多调试信息和修复
function patchMainFile() {
  const mainPath = path.join(__dirname, 'src', 'main', 'main.js');
  let mainContent = fs.readFileSync(mainPath, 'utf8');
  
  // 备份原始文件
  fs.writeFileSync(mainPath + '.bak', mainContent);
  console.log('已备份原始 main.js 文件');
  
  // 查找 BrowserWindow 配置
  const browserWindowRegex = /new\s+BrowserWindow\s*\(\s*{([^}]*?)}\s*\)/s;
  const match = mainContent.match(browserWindowRegex);
  
  if (match) {
    const config = match[1];
    let newConfig = config;
    
    // 添加或修改 webPreferences
    if (config.includes('webPreferences')) {
      newConfig = config.replace(
        /webPreferences\s*:\s*{([^}]*?)}/s,
        'webPreferences: {$1, nodeIntegration: true, contextIsolation: false, webSecurity: false}'
      );
    } else {
      newConfig = config + `,
  webPreferences: {
    nodeIntegration: true,
    contextIsolation: false,
    webSecurity: false
  }`;
    }
    
    // 替换配置
    mainContent = mainContent.replace(match[0], `new BrowserWindow({${newConfig}})`);
    
    // 写回文件
    fs.writeFileSync(mainPath, mainContent);
    console.log('已修改 main.js 文件，添加了更多调试信息和修复');
  } else {
    console.error('无法找到 BrowserWindow 配置');
  }
}

// 修改 preload.js 文件，添加更多调试信息
function patchPreloadFile() {
  const preloadPath = path.join(__dirname, 'src', 'main', 'preload.js');
  let preloadContent = fs.readFileSync(preloadPath, 'utf8');
  
  // 备份原始文件
  fs.writeFileSync(preloadPath + '.bak', preloadContent);
  console.log('已备份原始 preload.js 文件');
  
  // 添加更多调试日志
  preloadContent = preloadContent.replace(
    'contextBridge.exposeInMainWorld(\'electronApi\', {',
    `// 添加调试信息
console.log('Preload script is running');

// 添加全局错误处理
window.addEventListener('error', (event) => {
  console.error('Uncaught error:', event.error);
});

// 添加未处理的 Promise 拒绝处理
window.addEventListener('unhandledrejection', (event) => {
  console.error('Unhandled Promise rejection:', event.reason);
});

contextBridge.exposeInMainWorld('electronApi', {`
  );
  
  // 写回文件
  fs.writeFileSync(preloadPath, preloadContent);
  console.log('已修改 preload.js 文件，添加了更多调试信息');
}

// 创建一个简单的 HTML 文件作为备用
function createBackupHtml() {
  const backupHtmlPath = path.join(__dirname, 'backup.html');
  const backupHtmlContent = `
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>MagicDB</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100vh;
      margin: 0;
      background-color: #f5f5f5;
    }
    h1 {
      color: #333;
    }
    p {
      color: #666;
    }
    button {
      margin-top: 20px;
      padding: 10px 20px;
      background-color: #4CAF50;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
    button:hover {
      background-color: #45a049;
    }
  </style>
</head>
<body>
  <h1>MagicDB</h1>
  <p>正在尝试连接到前端服务...</p>
  <button id="reload">重新加载</button>
  <script>
    document.getElementById('reload').addEventListener('click', () => {
      window.location.reload();
    });
    
    // 尝试连接到前端服务
    function tryConnect() {
      fetch('http://localhost:8000')
        .then(response => {
          if (response.ok) {
            window.location.href = 'http://localhost:8000';
          } else {
            console.error('前端服务返回错误状态码:', response.status);
            setTimeout(tryConnect, 2000);
          }
        })
        .catch(error => {
          console.error('无法连接到前端服务:', error);
          setTimeout(tryConnect, 2000);
        });
    }
    
    // 开始尝试连接
    tryConnect();
  </script>
</body>
</html>
  `;
  
  fs.writeFileSync(backupHtmlPath, backupHtmlContent);
  console.log('已创建备用 HTML 文件:', backupHtmlPath);
}

// 修改 utils.js 文件，添加备用 HTML 文件
function patchUtilsFile() {
  const utilsPath = path.join(__dirname, 'src', 'main', 'utils.js');
  let utilsContent = fs.readFileSync(utilsPath, 'utf8');
  
  // 备份原始文件
  fs.writeFileSync(utilsPath + '.bak', utilsContent);
  console.log('已备份原始 utils.js 文件');
  
  // 修改 loadMainResource 函数
  utilsContent = utilsContent.replace(
    'function loadMainResource(mainWindow) {',
    `function loadMainResource(mainWindow) {
  console.log('loadMainResource called, NODE_ENV:', process.env.NODE_ENV);`
  );
  
  utilsContent = utilsContent.replace(
    'mainWindow.loadURL(DEV_WEB_URL);',
    `console.log('Loading development URL:', DEV_WEB_URL);
    // 先尝试加载备用 HTML 文件
    const backupHtmlPath = path.join(__dirname, '../../backup.html');
    if (fs.existsSync(backupHtmlPath)) {
      console.log('Loading backup HTML file:', backupHtmlPath);
      mainWindow.loadFile(backupHtmlPath).catch(err => {
        console.error('Failed to load backup HTML file:', err);
        // 如果备用 HTML 文件加载失败，再尝试加载开发 URL
        mainWindow.loadURL(DEV_WEB_URL).catch(err => {
          console.error('Failed to load development URL:', err);
        });
      });
    } else {
      // 如果备用 HTML 文件不存在，直接尝试加载开发 URL
      mainWindow.loadURL(DEV_WEB_URL).catch(err => {
        console.error('Failed to load development URL:', err);
      });
    }`
  );
  
  // 写回文件
  fs.writeFileSync(utilsPath, utilsContent);
  console.log('已修改 utils.js 文件，添加了备用 HTML 文件');
}

// 应用所有修复
function applyFixes() {
  try {
    // 创建备用 HTML 文件
    createBackupHtml();
    
    // 修改 main.js 文件
    patchMainFile();
    
    // 修改 preload.js 文件
    patchPreloadFile();
    
    // 修改 utils.js 文件
    patchUtilsFile();
    
    console.log('所有修复已应用');
  } catch (error) {
    console.error('应用修复时出错:', error);
  }
}

// 恢复所有修改
function restoreFiles() {
  const files = ['main.js', 'preload.js', 'utils.js'];
  
  for (const file of files) {
    const filePath = path.join(__dirname, 'src', 'main', file);
    const backupPath = filePath + '.bak';
    
    if (fs.existsSync(backupPath)) {
      fs.copyFileSync(backupPath, filePath);
      fs.unlinkSync(backupPath);
      console.log(`已恢复原始 ${file} 文件`);
    }
  }
  
  // 删除备用 HTML 文件
  const backupHtmlPath = path.join(__dirname, 'backup.html');
  if (fs.existsSync(backupHtmlPath)) {
    fs.unlinkSync(backupHtmlPath);
    console.log('已删除备用 HTML 文件');
  }
}

// 启动前端服务
console.log('启动前端服务...');
const frontendProcess = spawn('npm', ['run', 'start:web'], {
  stdio: 'inherit'
});

// 应用所有修复
applyFixes();

// 等待 5 秒，确保前端服务已启动
setTimeout(() => {
  console.log('启动 Electron 应用程序...');
  
  // 启动 Electron 应用程序
  const electronProcess = spawn('electron', [
    '.',
    '--remote-debugging-port=9222',
    '--enable-logging',
    '--trace-warnings',
    '--inspect=5858'
  ], {
    stdio: 'inherit',
    env: {
      ...process.env,
      ELECTRON_ENABLE_LOGGING: 'true',
      ELECTRON_ENABLE_STACK_DUMPING: 'true',
      NODE_ENV: 'development'
    }
  });

  console.log('Chrome 调试已启用:');
  console.log('1. 在 Chrome 浏览器中访问 chrome://inspect');
  console.log('2. 点击 "Open dedicated DevTools for Node"');
  console.log('3. 在 Connection 标签页中添加 localhost:9222');
  console.log('4. 返回到 Devices 标签页，您应该能看到您的 Electron 应用程序');

  // 处理进程终止
  electronProcess.on('close', (code) => {
    console.log(`Electron 进程已退出，退出码：${code}`);
    restoreFiles();
    frontendProcess.kill();
    process.exit(code);
  });
}, 5000);

// 处理进程终止
process.on('SIGINT', () => {
  console.log('接收到终止信号，正在关闭所有进程...');
  restoreFiles();
  frontendProcess.kill();
  process.exit(0);
});
