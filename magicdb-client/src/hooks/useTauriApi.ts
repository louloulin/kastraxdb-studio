/**
 * Tauri API Hook
 *
 * 提供一个 React Hook 来使用 Tauri API
 */

import { useState, useEffect, useCallback } from 'react';
import tauriApi from '../utils/tauri-api';
import { isTauri } from '../utils/environment';

/**
 * 使用 Tauri API 的 Hook
 */
export const useTauriApi = () => {
  const [platformInfo, setPlatformInfo] = useState<PlatformInfo | null>(null);
  const [appInfo, setAppInfo] = useState<AppInfo | null>(null);
  const [isMaximized, setIsMaximized] = useState<boolean>(false);
  const [isDesktopApp, setIsDesktopApp] = useState<boolean>(false);

  // 初始化
  useEffect(() => {
    const init = async () => {
      try {
        // 检测是否是桌面应用
        setIsDesktopApp(isTauri());

        // 获取平台信息
        const platform = tauriApi.getPlatform();
        setPlatformInfo(platform);

        // 获取应用信息
        if (isDesktopApp) {
          const info = await tauriApi.getAppInfo();
          setAppInfo(info);

          // 获取窗口状态
          const maximized = await tauriApi.isMaximized();
          setIsMaximized(maximized);
        }
      } catch (error) {
        console.error('初始化 Tauri API 失败:', error);
      }
    };

    init();
  }, [isDesktopApp]);

  // 窗口控制
  const minimizeWindow = useCallback(async () => {
    try {
      await tauriApi.minimizeWindow();
    } catch (error) {
      console.error('最小化窗口失败:', error);
    }
  }, []);

  const maximizeWindow = useCallback(async () => {
    try {
      await tauriApi.setMaximize();
      const maximized = await tauriApi.isMaximized();
      setIsMaximized(maximized);
    } catch (error) {
      console.error('最大化窗口失败:', error);
    }
  }, []);

  const closeWindow = useCallback(async () => {
    try {
      await tauriApi.closeWindow();
    } catch (error) {
      console.error('关闭窗口失败:', error);
    }
  }, []);

  // 对话框
  const showMessageDialog = useCallback(async (options: DialogOptions) => {
    try {
      await tauriApi.messageDialog(options);
    } catch (error) {
      console.error('显示消息对话框失败:', error);
    }
  }, []);

  const showConfirmDialog = useCallback(async (options: DialogOptions) => {
    try {
      return await tauriApi.confirmDialog(options);
    } catch (error) {
      console.error('显示确认对话框失败:', error);
      return false;
    }
  }, []);

  const openFileDialog = useCallback(async (options: FileDialogOptions) => {
    try {
      return await tauriApi.openFileDialog(options);
    } catch (error) {
      console.error('打开文件对话框失败:', error);
      return null;
    }
  }, []);

  const saveFileDialog = useCallback(async (options: FileDialogOptions) => {
    try {
      return await tauriApi.saveFileDialog(options);
    } catch (error) {
      console.error('保存文件对话框失败:', error);
      return null;
    }
  }, []);

  // 文件操作
  const readFile = useCallback(async (filePath: string) => {
    try {
      return await tauriApi.readFile(filePath);
    } catch (error) {
      console.error('读取文件失败:', error);
      return '';
    }
  }, []);

  const writeFile = useCallback(async (filePath: string, content: string) => {
    try {
      await tauriApi.writeFile(filePath, content);
      return true;
    } catch (error) {
      console.error('写入文件失败:', error);
      return false;
    }
  }, []);

  const fileExists = useCallback(async (filePath: string) => {
    try {
      return await tauriApi.fileExists(filePath);
    } catch (error) {
      console.error('检查文件是否存在失败:', error);
      return false;
    }
  }, []);

  // 应用控制
  const quitApp = useCallback(async () => {
    try {
      await tauriApi.quitApp();
    } catch (error) {
      console.error('退出应用失败:', error);
    }
  }, []);

  const relaunchApp = useCallback(async () => {
    try {
      await tauriApi.relaunchApp();
    } catch (error) {
      console.error('重启应用失败:', error);
    }
  }, []);

  // 其他功能
  const openExternal = useCallback(async (url: string) => {
    try {
      await tauriApi.openExternal(url);
    } catch (error) {
      console.error('打开外部链接失败:', error);
    }
  }, []);

  return {
    // 状态
    platformInfo,
    appInfo,
    isMaximized,
    isDesktopApp,

    // 窗口控制
    minimizeWindow,
    maximizeWindow,
    closeWindow,

    // 对话框
    showMessageDialog,
    showConfirmDialog,
    openFileDialog,
    saveFileDialog,

    // 文件操作
    readFile,
    writeFile,
    fileExists,

    // 应用控制
    quitApp,
    relaunchApp,

    // 其他功能
    openExternal,

    // 原始 API
    tauriApi
  };
};

export default useTauriApi;
