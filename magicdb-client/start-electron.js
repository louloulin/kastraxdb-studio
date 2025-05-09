/**
 * MagicDB Electron 启动脚本
 * 
 * 这个脚本用于启动 Electron 应用程序，它会：
 * 1. 设置必要的环境变量
 * 2. 启动 Electron 应用程序
 */

const { spawn } = require('child_process');
const path = require('path');

// 设置环境变量
process.env.NODE_ENV = 'development';
process.env.ELECTRON_ENABLE_LOGGING = 'true';
process.env.ELECTRON_ENABLE_STACK_DUMPING = 'true';

// 启动 Electron 应用程序
console.log('启动 Electron 应用程序...');

const electronProcess = spawn('electron', [
  path.join(__dirname, 'electron-starter.js'),
  '--remote-debugging-port=9222',
  '--enable-logging',
  '--trace-warnings',
  '--inspect=5858'
], {
  stdio: 'inherit',
  env: process.env
});

// 处理进程终止
electronProcess.on('close', (code) => {
  console.log(`Electron 进程已退出，退出码：${code}`);
  process.exit(code);
});

// 处理进程错误
electronProcess.on('error', (err) => {
  console.error('Electron 进程启动失败:', err);
  process.exit(1);
});

// 处理进程终止信号
process.on('SIGINT', () => {
  console.log('接收到终止信号，正在关闭 Electron 进程...');
  electronProcess.kill();
  process.exit(0);
});
