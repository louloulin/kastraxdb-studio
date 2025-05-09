// 高级调试启动脚本
const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('path');
const { loadMainResource } = require('./src/main/utils');

// 启用详细日志
process.env.ELECTRON_ENABLE_LOGGING = 'true';
process.env.ELECTRON_ENABLE_STACK_DUMPING = 'true';
process.env.NODE_ENV = 'development';

// 创建主窗口
let mainWindow;

function createWindow() {
  // 创建浏览器窗口
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 800,
    frame: false,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: true,
      preload: path.join(__dirname, 'src/main/preload.js')
    }
  });

  // 加载应用
  loadMainResource(mainWindow);

  // 监听页面加载状态
  mainWindow.webContents.on('did-start-loading', () => {
    console.log('Page started loading');
  });

  mainWindow.webContents.on('did-finish-load', () => {
    console.log('Page finished loading');
  });

  mainWindow.webContents.on('did-fail-load', (event, errorCode, errorDescription) => {
    console.error('Page failed to load:', errorCode, errorDescription);
  });

  // 捕获控制台消息
  mainWindow.webContents.on('console-message', (event, level, message, line, sourceId) => {
    console.log(`[Renderer Console][${level}]: ${message}`);
  });

  // 当窗口关闭时触发
  mainWindow.on('closed', function () {
    mainWindow = null;
  });
}

// 注册 IPC 事件处理程序
function registerIpcHandlers() {
  ipcMain.handle('get-product-name', () => {
    return app.getName();
  });

  ipcMain.on('quit-app', () => {
    app.quit();
  });

  ipcMain.on('set-base-url', (event, baseUrl) => {
    console.log('Setting base URL:', baseUrl);
  });

  ipcMain.on('set-force-quit-code', (event, code) => {
    console.log('Setting force quit code:', code);
  });

  ipcMain.on('register-app-menu', (event, menuProps) => {
    console.log('Registering app menu:', menuProps);
  });

  ipcMain.on('set-maximize', () => {
    if (mainWindow) {
      if (mainWindow.isMaximized()) {
        mainWindow.unmaximize();
      } else {
        mainWindow.maximize();
      }
    }
  });

  ipcMain.on('is-maximized', (event) => {
    if (mainWindow) {
      event.returnValue = mainWindow.isMaximized();
    }
  });

  ipcMain.on('close-window', () => {
    if (mainWindow) {
      mainWindow.close();
    }
  });

  ipcMain.on('minimize-window', () => {
    if (mainWindow) {
      mainWindow.minimize();
    }
  });
}

// 当 Electron 完成初始化并准备创建浏览器窗口时调用此方法
app.on('ready', () => {
  console.log('Electron app is ready');
  registerIpcHandlers();
  createWindow();
});

// 当所有窗口关闭时退出应用
app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('activate', function () {
  if (mainWindow === null) {
    createWindow();
  }
});

// 捕获未处理的异常
process.on('uncaughtException', (error) => {
  console.error('Uncaught Exception:', error);
});

// 捕获未处理的 Promise 拒绝
process.on('unhandledRejection', (reason, promise) => {
  console.error('Unhandled Rejection at:', promise, 'reason:', reason);
});
