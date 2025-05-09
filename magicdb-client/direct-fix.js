// 直接修复 Electron 渲染问题的脚本
const { spawn } = require('child_process');
const fs = require('fs');
const path = require('path');

// 直接修改 main.js 文件
function fixMainFile() {
  const mainPath = path.join(__dirname, 'src', 'main', 'main.js');
  let mainContent = fs.readFileSync(mainPath, 'utf8');
  
  // 备份原始文件
  fs.writeFileSync(mainPath + '.bak', mainContent);
  console.log('已备份原始 main.js 文件');
  
  // 添加调试日志
  mainContent = mainContent.replace(
    'const createWindow = () => {',
    `const createWindow = () => {
  console.log('Creating window...');`
  );
  
  // 添加 openDevTools 调用
  mainContent = mainContent.replace(
    'loadMainResource(mainWindow);',
    `loadMainResource(mainWindow);
  
  // 打开开发者工具
  mainWindow.webContents.openDevTools();
  
  // 添加页面加载事件监听
  mainWindow.webContents.on('did-start-loading', () => {
    console.log('Page started loading');
  });
  
  mainWindow.webContents.on('did-finish-load', () => {
    console.log('Page finished loading');
  });
  
  mainWindow.webContents.on('did-fail-load', (event, errorCode, errorDescription) => {
    console.error('Page failed to load:', errorCode, errorDescription);
  });`
  );
  
  // 写回文件
  fs.writeFileSync(mainPath, mainContent);
  console.log('已修改 main.js 文件，添加了调试日志和 DevTools');
}

// 直接修改 utils.js 文件
function fixUtilsFile() {
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
    `console.log('Trying to load URL:', DEV_WEB_URL);
    
    // 设置更长的超时时间
    setTimeout(() => {
      mainWindow.loadURL(DEV_WEB_URL)
        .then(() => {
          console.log('Successfully loaded URL:', DEV_WEB_URL);
        })
        .catch(err => {
          console.error('Failed to load URL:', err);
          
          // 尝试直接加载 localhost:8000
          const directUrl = 'http://localhost:8000';
          console.log('Trying to load direct URL:', directUrl);
          
          mainWindow.loadURL(directUrl)
            .then(() => {
              console.log('Successfully loaded direct URL:', directUrl);
            })
            .catch(err => {
              console.error('Failed to load direct URL:', err);
            });
        });
    }, 3000); // 等待 3 秒再加载 URL`
  );
  
  // 写回文件
  fs.writeFileSync(utilsPath, utilsContent);
  console.log('已修改 utils.js 文件，添加了延迟加载和错误处理');
}

// 应用所有修复
function applyFixes() {
  try {
    // 修改 main.js 文件
    fixMainFile();
    
    // 修改 utils.js 文件
    fixUtilsFile();
    
    console.log('所有修复已应用');
  } catch (error) {
    console.error('应用修复时出错:', error);
  }
}

// 恢复所有修改
function restoreFiles() {
  const files = ['main.js', 'utils.js'];
  
  for (const file of files) {
    const filePath = path.join(__dirname, 'src', 'main', file);
    const backupPath = filePath + '.bak';
    
    if (fs.existsSync(backupPath)) {
      fs.copyFileSync(backupPath, filePath);
      fs.unlinkSync(backupPath);
      console.log(`已恢复原始 ${file} 文件`);
    }
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
