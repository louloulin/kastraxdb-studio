// 创建一个启用 Chrome 调试的启动脚本
const { spawn } = require('child_process');
const fs = require('fs');
const path = require('path');

// 启动前端服务
console.log('启动前端服务...');
const frontendProcess = spawn('npm', ['run', 'start:web'], {
  stdio: 'inherit'
});

// 等待 5 秒，确保前端服务已启动
setTimeout(() => {
  console.log('启动 Electron 应用程序，并启用 Chrome 调试...');
  
  // 启动 Electron 应用程序，并启用 Chrome 调试
  const electronProcess = spawn('electron', [
    '.',
    '--remote-debugging-port=9222',
    '--enable-logging',
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
    frontendProcess.kill();
    process.exit(code);
  });
}, 5000);

// 处理进程终止
process.on('SIGINT', () => {
  console.log('接收到终止信号，正在关闭所有进程...');
  frontendProcess.kill();
  process.exit(0);
});
