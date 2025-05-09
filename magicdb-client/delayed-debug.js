// 延迟启动的调试脚本
const { spawn } = require('child_process');
const http = require('http');

// 启动前端服务
const frontendProcess = spawn('npm', ['run', 'start:web'], {
  stdio: 'inherit',
  env: {
    ...process.env,
    NODE_ENV: 'development'
  }
});

console.log('前端服务启动中...');

// 检查前端服务是否已启动
function checkFrontendReady() {
  return new Promise((resolve) => {
    const checkInterval = setInterval(() => {
      http.get('http://localhost:8000', (res) => {
        clearInterval(checkInterval);
        console.log('前端服务已就绪，状态码:', res.statusCode);
        resolve(true);
      }).on('error', (err) => {
        console.log('前端服务尚未就绪，等待中...');
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
      '--enable-logging'
    ], {
      stdio: 'inherit',
      env: {
        ...process.env,
        NODE_ENV: 'development',
        ELECTRON_ENABLE_LOGGING: 'true',
        ELECTRON_ENABLE_STACK_DUMPING: 'true'
      }
    });

    electronProcess.on('close', (code) => {
      console.log(`Electron 进程已退出，退出码：${code}`);
      frontendProcess.kill();
      process.exit(code);
    });
  } catch (error) {
    console.error('启动过程中出错:', error);
    frontendProcess.kill();
    process.exit(1);
  }
}

// 处理进程终止
process.on('SIGINT', () => {
  console.log('接收到终止信号，正在关闭所有进程...');
  frontendProcess.kill();
  process.exit(0);
});

// 启动应用
startElectron();
