/**
 * MagicDB Electron 启动器
 * 
 * 这个文件是 Electron 应用程序的入口点，它负责：
 * 1. 启动前端开发服务器
 * 2. 创建 Electron 窗口
 * 3. 加载加载页面，等待前端服务器就绪
 * 4. 加载前端应用程序
 */

const { app, BrowserWindow, ipcMain, dialog, Menu } = require('electron');
const path = require('path');
const { spawn } = require('child_process');
const http = require('http');
const fs = require('fs');
const url = require('url');

// 保持对窗口对象的全局引用，避免 JavaScript 对象被垃圾回收时窗口关闭
let mainWindow;
let frontendProcess;
let isDevMode = process.env.NODE_ENV === 'development';
let serverReady = false;
let forceQuitCode = false;
let baseUrl = '';

// 前端服务器 URL
const DEV_SERVER_URL = 'http://localhost:8000';

/**
 * 创建主窗口
 */
function createWindow() {
  console.log('创建主窗口...');
  
  // 创建浏览器窗口
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 800,
    show: false, // 先不显示窗口，等待内容加载完成
    frame: false, // 无边框窗口
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: path.join(__dirname, 'electron-preload.js')
    }
  });
  
  // 当窗口准备好显示时再显示窗口，避免白屏
  mainWindow.once('ready-to-show', () => {
    console.log('窗口准备好显示');
    mainWindow.show();
  });
  
  // 加载加载页面
  const loadingPath = path.join(__dirname, 'src', 'main', 'loading.html');
  console.log('加载加载页面:', loadingPath);
  
  mainWindow.loadFile(loadingPath)
    .then(() => {
      console.log('加载页面加载成功');
    })
    .catch(err => {
      console.error('加载页面加载失败:', err);
      // 如果加载页面加载失败，直接尝试加载前端应用程序
      tryLoadApp();
    });
  
  // 打开开发者工具
  if (isDevMode) {
    mainWindow.webContents.openDevTools();
  }
  
  // 当窗口关闭时触发
  mainWindow.on('closed', function () {
    console.log('窗口已关闭');
    mainWindow = null;
    
    // 关闭前端服务器
    if (frontendProcess) {
      console.log('关闭前端服务器');
      frontendProcess.kill();
      frontendProcess = null;
    }
  });
}

/**
 * 尝试加载前端应用程序
 */
function tryLoadApp() {
  if (!mainWindow) return;
  
  console.log('尝试加载前端应用程序:', DEV_SERVER_URL);
  
  mainWindow.loadURL(DEV_SERVER_URL)
    .then(() => {
      console.log('前端应用程序加载成功');
      serverReady = true;
    })
    .catch(err => {
      console.error('前端应用程序加载失败:', err);
      
      // 如果前端应用程序加载失败，显示错误对话框
      if (mainWindow) {
        dialog.showErrorBox(
          '连接错误',
          `无法连接到前端服务器 (${DEV_SERVER_URL})。\n\n错误信息: ${err.message}\n\n请确保前端服务器已启动。`
        );
      }
    });
}

/**
 * 检查前端服务器是否已启动
 */
function checkServerReady() {
  return new Promise((resolve, reject) => {
    http.get(DEV_SERVER_URL, (res) => {
      if (res.statusCode === 200) {
        resolve(true);
      } else {
        reject(new Error(`服务器返回状态码: ${res.statusCode}`));
      }
    }).on('error', (err) => {
      reject(err);
    });
  });
}

/**
 * 启动前端服务器
 */
function startFrontendServer() {
  console.log('启动前端服务器...');
  
  frontendProcess = spawn('npm', ['run', 'start:web'], {
    stdio: 'inherit',
    shell: true
  });
  
  frontendProcess.on('error', (err) => {
    console.error('前端服务器启动失败:', err);
  });
  
  frontendProcess.on('close', (code) => {
    console.log(`前端服务器已关闭，退出码: ${code}`);
    frontendProcess = null;
  });
}

/**
 * 等待前端服务器就绪
 */
async function waitForServer() {
  console.log('等待前端服务器就绪...');
  
  let attempts = 0;
  const maxAttempts = 30;
  const interval = 2000;
  
  while (attempts < maxAttempts) {
    try {
      await checkServerReady();
      console.log('前端服务器已就绪');
      return true;
    } catch (err) {
      attempts++;
      console.log(`前端服务器尚未就绪 (${attempts}/${maxAttempts}): ${err.message}`);
      await new Promise(resolve => setTimeout(resolve, interval));
    }
  }
  
  console.error('等待前端服务器超时');
  return false;
}

/**
 * 创建应用程序菜单
 */
function createAppMenu(menuProps) {
  if (!menuProps) return;
  
  try {
    console.log('创建应用程序菜单:', menuProps);
    
    const template = [];
    
    // 添加应用程序菜单（仅在 macOS 上）
    if (process.platform === 'darwin') {
      template.push({
        label: app.name,
        submenu: [
          { role: 'about' },
          { type: 'separator' },
          { role: 'services' },
          { type: 'separator' },
          { role: 'hide' },
          { role: 'hideOthers' },
          { role: 'unhide' },
          { type: 'separator' },
          { role: 'quit' }
        ]
      });
    }
    
    // 添加自定义菜单
    if (menuProps.menus && Array.isArray(menuProps.menus)) {
      menuProps.menus.forEach(menu => {
        const submenu = [];
        
        if (menu.children && Array.isArray(menu.children)) {
          menu.children.forEach(child => {
            submenu.push({
              label: child.label,
              click: () => {
                if (mainWindow) {
                  mainWindow.webContents.send('menu-click', child.key);
                }
              }
            });
          });
        }
        
        template.push({
          label: menu.label,
          submenu
        });
      });
    }
    
    // 添加编辑菜单
    template.push({
      label: '编辑',
      submenu: [
        { role: 'undo' },
        { role: 'redo' },
        { type: 'separator' },
        { role: 'cut' },
        { role: 'copy' },
        { role: 'paste' },
        { role: 'delete' },
        { role: 'selectAll' }
      ]
    });
    
    // 添加视图菜单
    template.push({
      label: '视图',
      submenu: [
        { role: 'reload' },
        { role: 'forceReload' },
        { role: 'toggleDevTools' },
        { type: 'separator' },
        { role: 'resetZoom' },
        { role: 'zoomIn' },
        { role: 'zoomOut' },
        { type: 'separator' },
        { role: 'togglefullscreen' }
      ]
    });
    
    // 添加窗口菜单
    template.push({
      label: '窗口',
      submenu: [
        { role: 'minimize' },
        { role: 'zoom' },
        ...(process.platform === 'darwin' ? [
          { type: 'separator' },
          { role: 'front' },
          { type: 'separator' },
          { role: 'window' }
        ] : [
          { role: 'close' }
        ])
      ]
    });
    
    // 添加帮助菜单
    template.push({
      role: 'help',
      submenu: [
        {
          label: '关于 MagicDB',
          click: async () => {
            dialog.showMessageBox({
              title: '关于 MagicDB',
              message: 'MagicDB',
              detail: `版本: ${app.getVersion()}\n\n一个强大的数据库管理工具。`
            });
          }
        }
      ]
    });
    
    const menu = Menu.buildFromTemplate(template);
    Menu.setApplicationMenu(menu);
  } catch (error) {
    console.error('创建应用程序菜单失败:', error);
  }
}

/**
 * 注册 IPC 处理程序
 */
function registerIpcHandlers() {
  // 打开开发者工具
  ipcMain.on('open-devtools', () => {
    if (mainWindow) {
      mainWindow.webContents.openDevTools();
    }
  });
  
  // 前端服务器已就绪
  ipcMain.on('server-ready', (event, serverUrl) => {
    console.log('收到前端服务器就绪消息:', serverUrl);
    serverReady = true;
    
    if (mainWindow) {
      mainWindow.loadURL(serverUrl)
        .catch(err => {
          console.error('加载前端应用程序失败:', err);
        });
    }
  });
  
  // 最小化窗口
  ipcMain.on('minimize-window', () => {
    if (mainWindow) {
      mainWindow.minimize();
    }
  });
  
  // 最大化/还原窗口
  ipcMain.on('set-maximize', () => {
    if (mainWindow) {
      if (mainWindow.isMaximized()) {
        mainWindow.unmaximize();
      } else {
        mainWindow.maximize();
      }
    }
  });
  
  // 获取窗口是否最大化
  ipcMain.on('is-maximized', (event) => {
    if (mainWindow) {
      event.returnValue = mainWindow.isMaximized();
    } else {
      event.returnValue = false;
    }
  });
  
  // 关闭窗口
  ipcMain.on('close-window', () => {
    if (mainWindow) {
      mainWindow.close();
    }
  });
  
  // 退出应用程序
  ipcMain.on('quit-app', () => {
    app.quit();
  });
  
  // 设置基础 URL
  ipcMain.on('set-base-url', (event, url) => {
    console.log('设置基础 URL:', url);
    baseUrl = url;
  });
  
  // 设置强制退出代码
  ipcMain.on('set-force-quit-code', (event, code) => {
    console.log('设置强制退出代码:', code);
    forceQuitCode = code;
  });
  
  // 注册应用程序菜单
  ipcMain.on('register-app-menu', (event, menuProps) => {
    createAppMenu(menuProps);
  });
  
  // 获取产品名称
  ipcMain.handle('get-product-name', () => {
    return app.getName();
  });
  
  // 启动服务器
  ipcMain.handle('start-server-for-spawn', async () => {
    console.log('收到启动服务器请求');
    // 在开发模式下，我们不需要启动 Java 服务器，因为它已经由 npm run start:web 启动了
    return { success: true, message: '服务器已启动' };
  });
}

/**
 * 应用程序入口点
 */
async function main() {
  try {
    // 注册 IPC 处理程序
    registerIpcHandlers();
    
    // 等待 Electron 应用程序就绪
    await app.whenReady();
    
    // 启动前端服务器
    startFrontendServer();
    
    // 创建主窗口
    createWindow();
    
    // 在 macOS 上，当点击 dock 图标并且没有其他窗口打开时，
    // 通常在应用程序中重新创建一个窗口。
    app.on('activate', function () {
      if (BrowserWindow.getAllWindows().length === 0) {
        createWindow();
      }
    });
    
    // 当所有窗口关闭时退出应用程序
    app.on('window-all-closed', function () {
      if (process.platform !== 'darwin') {
        app.quit();
      }
    });
  } catch (err) {
    console.error('应用程序启动失败:', err);
    app.quit();
  }
}

// 捕获未处理的异常
process.on('uncaughtException', (err) => {
  console.error('未捕获的异常:', err);
  
  if (app.isReady()) {
    dialog.showErrorBox(
      '应用程序错误',
      `发生了一个未捕获的异常:\n\n${err.message}\n\n${err.stack}`
    );
  }
});

// 启动应用程序
main();
