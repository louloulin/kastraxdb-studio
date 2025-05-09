// 调试启动脚本
const { spawn } = require('child_process');

// 启动 Electron 应用程序，并启用远程调试
const electronProcess = spawn('electron', [
  '.',
  '--remote-debugging-port=9222',
  '--enable-logging',
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

console.log('Electron 已启动，远程调试端口：9222');
console.log('您可以在 Chrome 浏览器中访问 chrome://inspect 进行调试');
console.log('Node.js 调试端口：5858');

electronProcess.on('close', (code) => {
  console.log(`Electron 进程已退出，退出码：${code}`);
  process.exit(code);
});
