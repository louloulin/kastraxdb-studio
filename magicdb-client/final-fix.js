// 最终修复 Electron 渲染问题的脚本
const { spawn } = require('child_process');
const fs = require('fs');
const path = require('path');
const http = require('http');

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
      margin-bottom: 20px;
    }
    button {
      margin: 10px;
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
    #status {
      margin-top: 20px;
      padding: 10px;
      border-radius: 4px;
      background-color: #f0f0f0;
      max-width: 80%;
    }
  </style>
</head>
<body>
  <h1>MagicDB</h1>
  <p>正在尝试连接到前端服务...</p>
  <div>
    <button id="reload">重新加载</button>
    <button id="direct">直接访问前端</button>
    <button id="debug">打开调试工具</button>
  </div>
  <div id="status"></div>
  
  <script>
    const statusDiv = document.getElementById('status');
    
    // 显示状态信息
    function showStatus(message) {
      statusDiv.innerHTML += message + '<br>';
      console.log(message);
    }
    
    // 重新加载按钮
    document.getElementById('reload').addEventListener('click', () => {
      showStatus('正在重新加载...');
      window.location.reload();
    });
    
    // 直接访问前端按钮
    document.getElementById('direct').addEventListener('click', () => {
      showStatus('正在直接访问前端...');
      window.location.href = 'http://localhost:8000';
    });
    
    // 打开调试工具按钮
    document.getElementById('debug').addEventListener('click', () => {
      showStatus('正在打开调试工具...');
      if (window.electronApi) {
        window.electronApi.openDevTools();
      } else {
        showStatus('错误: electronApi 不可用');
      }
    });
    
    // 尝试连接到前端服务
    function tryConnect() {
      showStatus('尝试连接到前端服务...');
      
      fetch('http://localhost:8000')
        .then(response => {
          if (response.ok) {
            showStatus('连接成功，正在跳转...');
            window.location.href = 'http://localhost:8000';
          } else {
            showStatus('前端服务返回错误状态码: ' + response.status);
            setTimeout(tryConnect, 2000);
          }
        })
        .catch(error => {
          showStatus('无法连接到前端服务: ' + error.message);
          setTimeout(tryConnect, 2000);
        });
    }
    
    // 开始尝试连接
    setTimeout(tryConnect, 1000);
  </script>
</body>
</html>
  `;
  
  fs.writeFileSync(backupHtmlPath, backupHtmlContent);
  console.log('已创建备用 HTML 文件:', backupHtmlPath);
  return backupHtmlPath;
}

// 修改 preload.js 文件，添加 openDevTools 方法
function patchPreloadFile() {
  const preloadPath = path.join(__dirname, 'src', 'main', 'preload.js');
  let preloadContent = fs.readFileSync(preloadPath, 'utf8');
  
  // 备份原始文件
  fs.writeFileSync(preloadPath + '.bak', preloadContent);
  console.log('已备份原始 preload.js 文件');
  
  // 添加 openDevTools 方法
  preloadContent = preloadContent.replace(
    'contextBridge.exposeInMainWorld(\'electronApi\', {',
    `// 添加调试信息
console.log('Preload script is running');

contextBridge.exposeInMainWorld('electronApi', {
  openDevTools: () => {
    ipcRenderer.send('open-devtools');
  },`
  );
  
  // 写回文件
  fs.writeFileSync(preloadPath, preloadContent);
  console.log('已修改 preload.js 文件，添加了 openDevTools 方法');
}

// 修改 main.js 文件，添加 IPC 处理程序
function patchMainFile() {
  const mainPath = path.join(__dirname, 'src', 'main', 'main.js');
  let mainContent = fs.readFileSync(mainPath, 'utf8');
  
  // 备份原始文件
  fs.writeFileSync(mainPath + '.bak', mainContent);
  console.log('已备份原始 main.js 文件');
  
  // 添加 IPC 处理程序
  if (mainContent.includes('app.on(\'ready\', () => {')) {
    mainContent = mainContent.replace(
      'app.on(\'ready\', () => {',
      `// 添加 IPC 处理程序
ipcMain.on('open-devtools', () => {
  if (mainWindow) {
    mainWindow.webContents.openDevTools();
  }
});

app.on('ready', () => {`
    );
  }
  
  // 添加调试日志
  mainContent = mainContent.replace(
    'const createWindow = () => {',
    `const createWindow = () => {
  console.log('Creating window...');`
  );
  
  // 添加 openDevTools 调用
  mainContent = mainContent.replace(
    'loadMainResource(mainWindow);',
    `// 添加页面加载事件监听
  mainWindow.webContents.on('did-start-loading', () => {
    console.log('Page started loading');
  });
  
  mainWindow.webContents.on('did-finish-load', () => {
    console.log('Page finished loading');
  });
  
  mainWindow.webContents.on('did-fail-load', (event, errorCode, errorDescription) => {
    console.error('Page failed to load:', errorCode, errorDescription);
    
    // 加载备用 HTML 文件
    const backupHtmlPath = path.join(__dirname, '../../backup.html');
    if (fs.existsSync(backupHtmlPath)) {
      console.log('Loading backup HTML file:', backupHtmlPath);
      mainWindow.loadFile(backupHtmlPath);
    }
  });
  
  // 打开开发者工具
  mainWindow.webContents.openDevTools();
  
  loadMainResource(mainWindow);`
  );
  
  // 写回文件
  fs.writeFileSync(mainPath, mainContent);
  console.log('已修改 main.js 文件，添加了 IPC 处理程序和调试日志');
}

// 修改 utils.js 文件，使用备用 HTML 文件
function patchUtilsFile(backupHtmlPath) {
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
  
  // 修改加载 URL 的代码
  utilsContent = utilsContent.replace(
    'mainWindow.loadURL(DEV_WEB_URL);',
    `// 先尝试加载备用 HTML 文件
    const backupHtmlPath = '${backupHtmlPath.replace(/\\/g, '\\\\')}';
    console.log('Loading backup HTML file first:', backupHtmlPath);
    
    mainWindow.loadFile(backupHtmlPath)
      .catch(err => {
        console.error('Failed to load backup HTML file:', err);
        
        // 如果备用 HTML 文件加载失败，再尝试加载开发 URL
        console.log('Trying to load development URL:', DEV_WEB_URL);
        mainWindow.loadURL(DEV_WEB_URL)
          .catch(err => {
            console.error('Failed to load development URL:', err);
          });
      });`
  );
  
  // 写回文件
  fs.writeFileSync(utilsPath, utilsContent);
  console.log('已修改 utils.js 文件，使用备用 HTML 文件');
}

// 检查前端服务是否已启动
function checkFrontendReady() {
  return new Promise((resolve, reject) => {
    let attempts = 0;
    const maxAttempts = 30;
    
    const checkInterval = setInterval(() => {
      attempts++;
      http.get('http://localhost:8000', (res) => {
        clearInterval(checkInterval);
        console.log('前端服务已就绪，状态码:', res.statusCode);
        resolve(true);
      }).on('error', (err) => {
        console.log(`前端服务尚未就绪，等待中... (${attempts}/${maxAttempts})`);
        if (attempts >= maxAttempts) {
          clearInterval(checkInterval);
          reject(new Error('前端服务启动超时'));
        }
      });
    }, 1000);
  });
}

// 应用所有修复
async function applyFixes() {
  try {
    // 创建备用 HTML 文件
    const backupHtmlPath = createBackupHtml();
    
    // 修改 preload.js 文件
    patchPreloadFile();
    
    // 修改 main.js 文件
    patchMainFile();
    
    // 修改 utils.js 文件
    patchUtilsFile(backupHtmlPath);
    
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

// 主函数
async function main() {
  try {
    // 启动前端服务
    console.log('启动前端服务...');
    const frontendProcess = spawn('npm', ['run', 'start:web'], {
      stdio: 'inherit'
    });
    
    // 应用所有修复
    await applyFixes();
    
    // 等待前端服务就绪
    console.log('等待前端服务就绪...');
    await checkFrontendReady();
    
    // 启动 Electron 应用程序
    console.log('启动 Electron 应用程序...');
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
    
    // 处理进程终止
    process.on('SIGINT', () => {
      console.log('接收到终止信号，正在关闭所有进程...');
      restoreFiles();
      frontendProcess.kill();
      process.exit(0);
    });
  } catch (error) {
    console.error('运行过程中出错:', error);
    restoreFiles();
    process.exit(1);
  }
}

// 运行主函数
main();
