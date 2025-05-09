// 调试渲染进程的脚本
const { spawn } = require('child_process');
const http = require('http');
const fs = require('fs');
const path = require('path');

// 修改 utils.js 文件，添加更多调试信息
function patchUtilsFile() {
  const utilsPath = path.join(__dirname, 'src', 'main', 'utils.js');
  let utilsContent = fs.readFileSync(utilsPath, 'utf8');
  
  // 备份原始文件
  fs.writeFileSync(utilsPath + '.bak', utilsContent);
  console.log('已备份原始 utils.js 文件');
  
  // 添加更多调试日志
  utilsContent = utilsContent.replace(
    'function loadMainResource(mainWindow) {',
    `function loadMainResource(mainWindow) {
  console.log('loadMainResource called, NODE_ENV:', process.env.NODE_ENV);`
  );
  
  utilsContent = utilsContent.replace(
    'mainWindow.loadURL(DEV_WEB_URL);',
    `console.log('Loading development URL:', DEV_WEB_URL);
    mainWindow.loadURL(DEV_WEB_URL).catch(err => {
      console.error('Failed to load URL:', err);
      // 尝试加载本地文件作为备用
      const localPath = path.join(__dirname, '..', 'renderer', 'index.html');
      console.log('Trying to load local file:', localPath);
      if (fs.existsSync(localPath)) {
        mainWindow.loadFile(localPath).catch(err => {
          console.error('Failed to load local file:', err);
        });
      } else {
        console.error('Local file not found:', localPath);
      }
    });`
  );
  
  // 写回文件
  fs.writeFileSync(utilsPath, utilsContent);
  console.log('已修改 utils.js 文件，添加了更多调试信息');
}

// 启动前端服务
console.log('启动前端服务...');
const frontendProcess = spawn('npm', ['run', 'start:web'], {
  stdio: 'inherit'
});

// 修改 utils.js 文件
patchUtilsFile();

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

// 等待前端服务就绪后启动 Electron
async function startElectron() {
  try {
    console.log('等待前端服务就绪...');
    await checkFrontendReady();
    
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
        NODE_ENV: 'development',
        ELECTRON_ENABLE_LOGGING: 'true',
        ELECTRON_ENABLE_STACK_DUMPING: 'true',
        ELECTRON_FORCE_WINDOW_MENU_BAR: 'true'
      }
    });

    console.log('Chrome 调试已启用:');
    console.log('1. 在 Chrome 浏览器中访问 chrome://inspect');
    console.log('2. 点击 "Open dedicated DevTools for Node"');
    console.log('3. 在 Connection 标签页中添加 localhost:9222');
    console.log('4. 返回到 Devices 标签页，您应该能看到您的 Electron 应用程序');

    electronProcess.on('close', (code) => {
      console.log(`Electron 进程已退出，退出码：${code}`);
      // 恢复原始文件
      const utilsPath = path.join(__dirname, 'src', 'main', 'utils.js');
      const backupPath = utilsPath + '.bak';
      if (fs.existsSync(backupPath)) {
        fs.copyFileSync(backupPath, utilsPath);
        fs.unlinkSync(backupPath);
        console.log('已恢复原始 utils.js 文件');
      }
      frontendProcess.kill();
      process.exit(code);
    });
  } catch (error) {
    console.error('启动过程中出错:', error);
    // 恢复原始文件
    const utilsPath = path.join(__dirname, 'src', 'main', 'utils.js');
    const backupPath = utilsPath + '.bak';
    if (fs.existsSync(backupPath)) {
      fs.copyFileSync(backupPath, utilsPath);
      fs.unlinkSync(backupPath);
      console.log('已恢复原始 utils.js 文件');
    }
    frontendProcess.kill();
    process.exit(1);
  }
}

// 处理进程终止
process.on('SIGINT', () => {
  console.log('接收到终止信号，正在关闭所有进程...');
  // 恢复原始文件
  const utilsPath = path.join(__dirname, 'src', 'main', 'utils.js');
  const backupPath = utilsPath + '.bak';
  if (fs.existsSync(backupPath)) {
    fs.copyFileSync(backupPath, utilsPath);
    fs.unlinkSync(backupPath);
    console.log('已恢复原始 utils.js 文件');
  }
  frontendProcess.kill();
  process.exit(0);
});

// 启动应用
startElectron();
