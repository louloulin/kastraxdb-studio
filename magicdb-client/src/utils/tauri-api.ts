/**
 * Tauri API 包装器
 *
 * 这个文件提供了 Tauri API 的封装，使其更易于在应用程序中使用。
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
  } else {
    const userAgent = navigator.userAgent.toLowerCase();
    platformInfo = {
      isLinux: userAgent.includes('linux'),
      isWin: userAgent.includes('win'),
      isMac: userAgent.includes('mac')
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
    } else {
      console.warn('openDevTools 仅在 Tauri 环境中可用');
    }
  },

  // 通知主进程前端服务器已就绪
  serverReady: async (serverUrl: string) => {
    if (isTauri) {
      // Tauri 不需要这个功能，因为它会自动处理
      console.log('Tauri 环境中不需要 serverReady');
    } else {
      console.warn('serverReady 仅在 Tauri 环境中可用');
    }
  },

  // 窗口控制
  minimizeWindow: async () => {
    if (isTauri) {
      await appWindow.minimize();
    } else {
      console.warn('minimizeWindow 仅在 Tauri 环境中可用');
    }
  },

  maximizeWindow: async () => {
    if (isTauri) {
      if (await appWindow.isMaximized()) {
        await appWindow.unmaximize();
      } else {
        await appWindow.maximize();
      }
    } else {
      console.warn('maximizeWindow 仅在 Tauri 环境中可用');
    }
  },

  closeWindow: async () => {
    if (isTauri) {
      await appWindow.close();
    } else {
      console.warn('closeWindow 仅在 Tauri 环境中可用');
    }
  },

  // 获取应用程序信息
  getAppInfo: async () => {
    if (isTauri) {
      return await invoke('get_app_info');
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
    }
    return { success: true, message: '服务器已启动' };
  },

  // 退出应用程序
  quitApp: async () => {
    if (isTauri) {
      await exit(0);
    } else {
      console.warn('quitApp 仅在 Tauri 环境中可用');
    }
  },

  // 设置基础 URL
  setBaseURL: async (baseUrl: string) => {
    if (isTauri) {
      await invoke('set_base_url', { url: baseUrl });
    } else {
      console.warn('setBaseURL 仅在 Tauri 环境中可用');
    }
  },

  // 设置强制退出代码
  setForceQuitCode: async (code: boolean) => {
    if (isTauri) {
      await invoke('set_force_quit_code', { code });
    } else {
      console.warn('setForceQuitCode 仅在 Tauri 环境中可用');
    }
  },

  // 注册应用程序菜单
  registerAppMenu: async (menuProps: any) => {
    if (isTauri) {
      await invoke('register_app_menu', { menuProps });
    } else {
      console.warn('registerAppMenu 仅在 Tauri 环境中可用');
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
    } else {
      console.warn('setMaximize 仅在 Tauri 环境中可用');
    }
  },

  // 获取当前窗口是否是最大化
  isMaximized: async () => {
    if (isTauri) {
      return await appWindow.isMaximized();
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
    } else {
      window.open(url, '_blank');
    }
  },

  // 打开文件对话框
  openFileDialog: async (options: any) => {
    if (isTauri) {
      return await openDialog(options);
    }
    return null;
  },

  // 保存文件对话框
  saveFileDialog: async (options: any) => {
    if (isTauri) {
      return await save(options);
    }
    return null;
  },

  // 确认对话框
  confirmDialog: async (options: any) => {
    if (isTauri) {
      return await confirm(options.message, options.title || '确认');
    }
    return window.confirm(options.message);
  },

  // 消息对话框
  messageDialog: async (options: any) => {
    if (isTauri) {
      await message(options.message, options.title || '消息');
    } else {
      alert(options.message);
    }
  },

  // 读取文件
  readFile: async (filePath: string) => {
    if (isTauri) {
      return await readTextFile(filePath);
    }
    return '';
  },

  // 写入文件
  writeFile: async (filePath: string, content: string) => {
    if (isTauri) {
      await writeTextFile(filePath, content);
    } else {
      console.warn('writeFile 仅在 Tauri 环境中可用');
    }
  },

  // 检查文件是否存在
  fileExists: async (filePath: string) => {
    if (isTauri) {
      return await exists(filePath);
    }
    return false;
  },

  // 获取用户主目录
  getHomeDir: async () => {
    if (isTauri) {
      return await homeDir();
    }
    return '';
  },

  // 重启应用程序
  relaunchApp: async () => {
    if (isTauri) {
      await relaunch();
    } else {
      console.warn('relaunchApp 仅在 Tauri 环境中可用');
    }
  }
};

export default tauriApi;
