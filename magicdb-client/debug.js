// 调试启动脚本
const { spawn } = require('child_process');
const path = require('path');
const fs = require('fs');

// 启动 Electron 应用程序，并启用远程调试
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

console.log('Electron 已启动，远程调试端口：9222');
console.log('您可以在 Chrome 浏览器中访问 chrome://inspect 进行调试');

electronProcess.on('close', (code) => {
  console.log(`Electron 进程已退出，退出码：${code}`);
  process.exit(code);
});
