/**
 * Tauri API 包装器 (Web 模式)
 *
 * 这个文件提供了 Tauri API 的 Web 模式封装，用于在非 Tauri 环境中使用。
 */

// 获取平台信息
const platformInfo = (() => {
  const userAgent = navigator.userAgent.toLowerCase();
  return {
    isLinux: userAgent.includes('linux'),
    isWin: userAgent.includes('win'),
    isMac: userAgent.includes('mac')
  };
})();

// Tauri API 包装器 (Web 模式)
const tauriApi = {
  // 打开开发者工具
  openDevTools: async () => {
    console.warn('openDevTools 仅在 Tauri 环境中可用');
  },

  // 通知主进程前端服务器已就绪
  serverReady: async (serverUrl: string) => {
    console.warn('serverReady 仅在 Tauri 环境中可用');
  },

  // 窗口控制
  minimizeWindow: async () => {
    console.warn('minimizeWindow 仅在 Tauri 环境中可用');
  },

  maximizeWindow: async () => {
    console.warn('maximizeWindow 仅在 Tauri 环境中可用');
  },

  closeWindow: async () => {
    console.warn('closeWindow 仅在 Tauri 环境中可用');
  },

  // 获取应用程序信息
  getAppInfo: async () => {
    return {
      version: '1.0.0',
      platform: 'web',
      arch: 'web'
    };
  },

  // 启动服务器
  startServerForSpawn: async () => {
    return { success: true, message: '服务器已启动' };
  },

  // 退出应用程序
  quitApp: async () => {
    console.warn('quitApp 仅在 Tauri 环境中可用');
  },

  // 设置基础 URL
  setBaseURL: async (baseUrl: string) => {
    console.warn('setBaseURL 仅在 Tauri 环境中可用');
  },

  // 设置强制退出代码
  setForceQuitCode: async (code: boolean) => {
    console.warn('setForceQuitCode 仅在 Tauri 环境中可用');
  },

  // 注册应用程序菜单
  registerAppMenu: async (menuProps: any) => {
    console.warn('registerAppMenu 仅在 Tauri 环境中可用');
  },

  // 设置最大化
  setMaximize: async () => {
    console.warn('setMaximize 仅在 Tauri 环境中可用');
  },

  // 获取当前窗口是否是最大化
  isMaximized: async () => {
    return false;
  },

  // 获取环境是mac还是windows还是linux
  getPlatform: () => {
    return platformInfo;
  },

  // 打开外部链接
  openExternal: async (url: string) => {
    window.open(url, '_blank');
  },

  // 打开文件对话框
  openFileDialog: async (options: any) => {
    console.warn('openFileDialog 仅在 Tauri 环境中可用');
    return null;
  },

  // 保存文件对话框
  saveFileDialog: async (options: any) => {
    console.warn('saveFileDialog 仅在 Tauri 环境中可用');
    return null;
  },

  // 确认对话框
  confirmDialog: async (options: any) => {
    return window.confirm(options.message);
  },

  // 消息对话框
  messageDialog: async (options: any) => {
    alert(options.message);
  },

  // 读取文件
  readFile: async (filePath: string) => {
    console.warn('readFile 仅在 Tauri 环境中可用');
    return '';
  },

  // 写入文件
  writeFile: async (filePath: string, content: string) => {
    console.warn('writeFile 仅在 Tauri 环境中可用');
  },

  // 检查文件是否存在
  fileExists: async (filePath: string) => {
    console.warn('fileExists 仅在 Tauri 环境中可用');
    return false;
  },

  // 获取用户主目录
  getHomeDir: async () => {
    console.warn('getHomeDir 仅在 Tauri 环境中可用');
    return '';
  },

  // 重启应用程序
  relaunchApp: async () => {
    console.warn('relaunchApp 仅在 Tauri 环境中可用');
  }
};

export default tauriApi;
