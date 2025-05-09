/**
 * Tauri API 包装器
 * 
 * 这个文件提供了一个与 Electron API 兼容的接口，使用 Tauri API 实现。
 * 这样可以最小化迁移到 Tauri 所需的代码更改。
 */

import { invoke } from '@tauri-apps/api/tauri';
import { open } from '@tauri-apps/api/shell';
import { appWindow, WebviewWindow } from '@tauri-apps/api/window';
import { confirm, message, open as openDialog, save } from '@tauri-apps/api/dialog';
import { exit, relaunch } from '@tauri-apps/api/process';
import { platform } from '@tauri-apps/api/os';
import { readTextFile, writeTextFile, exists } from '@tauri-apps/api/fs';
import { homeDir, join } from '@tauri-apps/api/path';

// 检测是否在 Tauri 环境中运行
const isTauri = typeof window !== 'undefined' && window.__TAURI__ !== undefined;

// 检测是否在 Electron 环境中运行
const isElectron = typeof window !== 'undefined' && 
  (window.electronApi !== undefined || 
   window.process?.versions?.electron !== undefined);

// 获取平台信息
let platformInfo = {
  isLinux: false,
  isWin: false,
  isMac: false
};

// 初始化平台信息
async function initPlatformInfo() {
  if (isTauri) {
    platformInfo = await invoke('get_platform');
  } else if (isElectron && window.electronApi) {
    platformInfo = window.electronApi.getPlatform();
  } else {
    const plat = navigator.platform.toLowerCase();
    platformInfo = {
      isLinux: plat.includes('linux'),
      isWin: plat.includes('win'),
      isMac: plat.includes('mac')
    };
  }
}

// 初始化平台信息
initPlatformInfo();

// Tauri API 包装器
const tauriApi = {
  // 打开开发者工具
  openDevTools: async () => {
    if (isTauri) {
      await appWindow.webviewWindow.openDevTools();
    } else if (isElectron && window.electronApi) {
      window.electronApi.openDevTools();
    }
  },

  // 通知主进程前端服务器已就绪
  serverReady: async (serverUrl: string) => {
    if (isTauri) {
      // Tauri 不需要这个功能，因为它会自动处理
    } else if (isElectron && window.electronApi) {
      window.electronApi.serverReady(serverUrl);
    }
  },

  // 窗口控制
  minimizeWindow: async () => {
    if (isTauri) {
      await appWindow.minimize();
    } else if (isElectron && window.electronApi) {
      window.electronApi.minimizeWindow();
    }
  },

  maximizeWindow: async () => {
    if (isTauri) {
      if (await appWindow.isMaximized()) {
        await appWindow.unmaximize();
      } else {
        await appWindow.maximize();
      }
    } else if (isElectron && window.electronApi) {
      window.electronApi.maximizeWindow();
    }
  },

  closeWindow: async () => {
    if (isTauri) {
      await appWindow.close();
    } else if (isElectron && window.electronApi) {
      window.electronApi.closeWindow();
    }
  },

  // 获取应用程序信息
  getAppInfo: async () => {
    if (isTauri) {
      return await invoke('get_app_info');
    } else if (isElectron && window.electronApi) {
      return window.electronApi.getAppInfo();
    }
    return {
      version: '1.0.0',
      platform: 'web',
      arch: 'web'
    };
  },

  // 启动服务器
  startServerForSpawn: async () => {
    if (isTauri) {
      return await invoke('start_server_for_spawn');
    } else if (isElectron && window.electronApi) {
      return await window.electronApi.startServerForSpawn();
    }
    return { success: true, message: '服务器已启动' };
  },

  // 退出应用程序
  quitApp: async () => {
    if (isTauri) {
      await exit(0);
    } else if (isElectron && window.electronApi) {
      window.electronApi.quitApp();
    }
  },

  // 设置基础 URL
  setBaseURL: async (baseUrl: string) => {
    if (isTauri) {
      await invoke('set_base_url', { url: baseUrl });
    } else if (isElectron && window.electronApi) {
      window.electronApi.setBaseURL(baseUrl);
    }
  },

  // 设置强制退出代码
  setForceQuitCode: async (code: boolean) => {
    if (isTauri) {
      await invoke('set_force_quit_code', { code });
    } else if (isElectron && window.electronApi) {
      window.electronApi.setForceQuitCode(code);
    }
  },

  // 注册应用程序菜单
  registerAppMenu: async (menuProps: any) => {
    if (isTauri) {
      await invoke('register_app_menu', { menuProps });
    } else if (isElectron && window.electronApi) {
      window.electronApi.registerAppMenu(menuProps);
    }
  },

  // 设置最大化
  setMaximize: async () => {
    if (isTauri) {
      if (await appWindow.isMaximized()) {
        await appWindow.unmaximize();
      } else {
        await appWindow.maximize();
      }
    } else if (isElectron && window.electronApi) {
      window.electronApi.setMaximize();
    }
  },

  // 获取当前窗口是否是最大化
  isMaximized: async () => {
    if (isTauri) {
      return await appWindow.isMaximized();
    } else if (isElectron && window.electronApi) {
      return window.electronApi.isMaximized();
    }
    return false;
  },

  // 获取环境是mac还是windows还是linux
  getPlatform: () => {
    return platformInfo;
  },

  // 打开外部链接
  openExternal: async (url: string) => {
    if (isTauri) {
      await open(url);
    } else if (isElectron && window.electronApi) {
      // 假设 Electron 有一个 openExternal 方法
      // window.electronApi.openExternal(url);
      window.open(url, '_blank');
    } else {
      window.open(url, '_blank');
    }
  },

  // 打开文件对话框
  openFileDialog: async (options: any) => {
    if (isTauri) {
      return await openDialog(options);
    } else if (isElectron && window.electronApi) {
      // 假设 Electron 有一个 openFileDialog 方法
      // return await window.electronApi.openFileDialog(options);
      return null;
    }
    return null;
  },

  // 保存文件对话框
  saveFileDialog: async (options: any) => {
    if (isTauri) {
      return await save(options);
    } else if (isElectron && window.electronApi) {
      // 假设 Electron 有一个 saveFileDialog 方法
      // return await window.electronApi.saveFileDialog(options);
      return null;
    }
    return null;
  },

  // 确认对话框
  confirmDialog: async (options: any) => {
    if (isTauri) {
      return await confirm(options.message, options.title || '确认');
    } else if (isElectron && window.electronApi) {
      // 假设 Electron 有一个 confirmDialog 方法
      // return await window.electronApi.confirmDialog(options);
      return window.confirm(options.message);
    }
    return window.confirm(options.message);
  },

  // 消息对话框
  messageDialog: async (options: any) => {
    if (isTauri) {
      await message(options.message, options.title || '消息');
    } else if (isElectron && window.electronApi) {
      // 假设 Electron 有一个 messageDialog 方法
      // await window.electronApi.messageDialog(options);
      alert(options.message);
    } else {
      alert(options.message);
    }
  },

  // 读取文件
  readFile: async (filePath: string) => {
    if (isTauri) {
      return await readTextFile(filePath);
    } else if (isElectron && window.electronApi) {
      // 假设 Electron 有一个 readFile 方法
      // return await window.electronApi.readFile(filePath);
      return '';
    }
    return '';
  },

  // 写入文件
  writeFile: async (filePath: string, content: string) => {
    if (isTauri) {
      await writeTextFile(filePath, content);
    } else if (isElectron && window.electronApi) {
      // 假设 Electron 有一个 writeFile 方法
      // await window.electronApi.writeFile(filePath, content);
    }
  },

  // 检查文件是否存在
  fileExists: async (filePath: string) => {
    if (isTauri) {
      return await exists(filePath);
    } else if (isElectron && window.electronApi) {
      // 假设 Electron 有一个 fileExists 方法
      // return await window.electronApi.fileExists(filePath);
      return false;
    }
    return false;
  },

  // 获取用户主目录
  getHomeDir: async () => {
    if (isTauri) {
      return await homeDir();
    } else if (isElectron && window.electronApi) {
      // 假设 Electron 有一个 getHomeDir 方法
      // return await window.electronApi.getHomeDir();
      return '';
    }
    return '';
  },

  // 重启应用程序
  relaunchApp: async () => {
    if (isTauri) {
      await relaunch();
    } else if (isElectron && window.electronApi) {
      // 假设 Electron 有一个 relaunchApp 方法
      // await window.electronApi.relaunchApp();
    }
  }
};

export default tauriApi;
