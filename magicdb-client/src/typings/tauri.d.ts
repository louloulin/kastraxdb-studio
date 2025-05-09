/**
 * Tauri 类型定义
 */

// 全局 Tauri 对象
interface Window {
  __TAURI__?: {
    invoke: (command: string, args?: any) => Promise<any>;
    tauri: {
      invoke: (command: string, args?: any) => Promise<any>;
    };
  };
}

// 应用信息接口
interface AppInfo {
  version: string;
  platform: string;
  arch: string;
}

// 平台信息接口
interface PlatformInfo {
  isLinux: boolean;
  isWin: boolean;
  isMac: boolean;
}

// 菜单项接口
interface MenuProps {
  version?: string;
  menus?: MenuInfo[];
}

interface MenuInfo {
  label: string;
  key?: string;
  children?: MenuChild[];
}

interface MenuChild {
  label: string;
  key: string;
}

// 对话框选项接口
interface DialogOptions {
  title?: string;
  message: string;
  detail?: string;
  type?: 'info' | 'warning' | 'error' | 'question';
  buttons?: string[];
  defaultId?: number;
  cancelId?: number;
}

// 文件对话框选项接口
interface FileDialogOptions {
  title?: string;
  defaultPath?: string;
  buttonLabel?: string;
  filters?: FileFilter[];
  properties?: string[];
  message?: string;
}

interface FileFilter {
  name: string;
  extensions: string[];
}

// Tauri API 接口
interface TauriApi {
  openDevTools: () => Promise<void>;
  serverReady: (serverUrl: string) => Promise<void>;
  minimizeWindow: () => Promise<void>;
  maximizeWindow: () => Promise<void>;
  closeWindow: () => Promise<void>;
  getAppInfo: () => Promise<AppInfo>;
  startServerForSpawn: () => Promise<any>;
  quitApp: () => Promise<void>;
  setBaseURL: (baseUrl: string) => Promise<void>;
  setForceQuitCode: (code: boolean) => Promise<void>;
  registerAppMenu: (menuProps: any) => Promise<void>;
  setMaximize: () => Promise<void>;
  isMaximized: () => Promise<boolean>;
  getPlatform: () => PlatformInfo;
  openExternal: (url: string) => Promise<void>;
  openFileDialog: (options: FileDialogOptions) => Promise<string | string[] | null>;
  saveFileDialog: (options: FileDialogOptions) => Promise<string | null>;
  confirmDialog: (options: DialogOptions) => Promise<boolean>;
  messageDialog: (options: DialogOptions) => Promise<void>;
  readFile: (filePath: string) => Promise<string>;
  writeFile: (filePath: string, content: string) => Promise<void>;
  fileExists: (filePath: string) => Promise<boolean>;
  getHomeDir: () => Promise<string>;
  relaunchApp: () => Promise<void>;
}
