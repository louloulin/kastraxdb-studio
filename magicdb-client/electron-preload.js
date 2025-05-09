/**
 * MagicDB Electron Preload 脚本
 * 
 * 这个文件在渲染进程中运行，负责：
 * 1. 在渲染进程中安全地暴露 API
 * 2. 提供与主进程通信的接口
 */

const { contextBridge, ipcRenderer } = require('electron');
const path = require('path');

// 在控制台中显示 preload 脚本已加载的消息
console.log('Preload 脚本已加载');

// 检测平台
const isLinux = process.platform === 'linux';
const isWin = process.platform === 'win32';
const isMac = process.platform === 'darwin';

// 在渲染进程中安全地暴露 API
contextBridge.exposeInMainWorld('electronApi', {
  // 打开开发者工具
  openDevTools: () => {
    ipcRenderer.send('open-devtools');
  },
  
  // 通知主进程前端服务器已就绪
  serverReady: (serverUrl) => {
    ipcRenderer.send('server-ready', serverUrl);
  },
  
  // 窗口控制
  minimizeWindow: () => {
    ipcRenderer.send('minimize-window');
  },
  
  maximizeWindow: () => {
    ipcRenderer.send('maximize-window');
  },
  
  closeWindow: () => {
    ipcRenderer.send('close-window');
  },
  
  // 获取应用程序信息
  getAppInfo: () => {
    return {
      version: '1.0.0',
      platform: process.platform,
      arch: process.arch,
      nodeVersion: process.versions.node,
      electronVersion: process.versions.electron,
      chromeVersion: process.versions.chrome
    };
  },
  
  // 以下是从原始 preload.js 复制的函数
  
  // 启动服务器
  startServerForSpawn: async () => {
    console.log('startServerForSpawn called, but not implemented in this version');
    // 在开发模式下，我们不需要启动 Java 服务器，因为它已经由 npm run start:web 启动了
    return ipcRenderer.invoke('start-server-for-spawn');
  },
  
  // 退出应用程序
  quitApp: () => {
    ipcRenderer.send('quit-app');
  },
  
  // 设置基础 URL
  setBaseURL: (baseUrl) => {
    ipcRenderer.send('set-base-url', baseUrl);
  },
  
  // 设置强制退出代码
  setForceQuitCode: (code) => {
    ipcRenderer.send('set-force-quit-code', !code);
  },
  
  // 注册应用程序菜单
  registerAppMenu: (menuProps) => {
    ipcRenderer.send('register-app-menu', menuProps);
  },
  
  // 设置最大化
  setMaximize: () => {
    ipcRenderer.send('set-maximize');
  },
  
  // 获取当前窗口是否是最大化
  isMaximized: () => {
    return ipcRenderer.sendSync('is-maximized');
  },
  
  // 获取环境是mac还是windows还是linux
  getPlatform: () => {
    return {
      isLinux,
      isWin,
      isMac,
    };
  },
});

// 添加全局错误处理
window.addEventListener('error', (event) => {
  console.error('渲染进程错误:', event.error);
});

// 添加未处理的 Promise 拒绝处理
window.addEventListener('unhandledrejection', (event) => {
  console.error('未处理的 Promise 拒绝:', event.reason);
});
