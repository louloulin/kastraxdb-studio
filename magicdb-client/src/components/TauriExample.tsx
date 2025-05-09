import React, { useState, useEffect } from 'react';
import tauriApi from '../utils/tauri-api';

interface AppInfo {
  version: string;
  platform: string;
  arch: string;
}

interface PlatformInfo {
  isLinux: boolean;
  isWin: boolean;
  isMac: boolean;
}

const TauriExample: React.FC = () => {
  const [appInfo, setAppInfo] = useState<AppInfo | null>(null);
  const [platformInfo, setPlatformInfo] = useState<PlatformInfo | null>(null);
  const [isMaximized, setIsMaximized] = useState<boolean>(false);

  useEffect(() => {
    // 获取应用程序信息
    const fetchAppInfo = async () => {
      try {
        const info = await tauriApi.getAppInfo();
        setAppInfo(info);
      } catch (error) {
        console.error('获取应用程序信息失败:', error);
      }
    };

    // 获取平台信息
    const fetchPlatformInfo = async () => {
      try {
        const info = tauriApi.getPlatform();
        setPlatformInfo(info);
      } catch (error) {
        console.error('获取平台信息失败:', error);
      }
    };

    // 获取窗口状态
    const fetchWindowState = async () => {
      try {
        const maximized = await tauriApi.isMaximized();
        setIsMaximized(maximized);
      } catch (error) {
        console.error('获取窗口状态失败:', error);
      }
    };

    fetchAppInfo();
    fetchPlatformInfo();
    fetchWindowState();
  }, []);

  // 最小化窗口
  const handleMinimize = async () => {
    try {
      await tauriApi.minimizeWindow();
    } catch (error) {
      console.error('最小化窗口失败:', error);
    }
  };

  // 最大化/还原窗口
  const handleMaximize = async () => {
    try {
      await tauriApi.setMaximize();
      setIsMaximized(await tauriApi.isMaximized());
    } catch (error) {
      console.error('最大化/还原窗口失败:', error);
    }
  };

  // 关闭窗口
  const handleClose = async () => {
    try {
      await tauriApi.closeWindow();
    } catch (error) {
      console.error('关闭窗口失败:', error);
    }
  };

  // 打开外部链接
  const handleOpenExternal = async () => {
    try {
      await tauriApi.openExternal('https://www.sqlgpt.cn/zh');
    } catch (error) {
      console.error('打开外部链接失败:', error);
    }
  };

  // 显示消息对话框
  const handleShowMessage = async () => {
    try {
      await tauriApi.messageDialog({
        title: '消息',
        message: '这是一个消息对话框'
      });
    } catch (error) {
      console.error('显示消息对话框失败:', error);
    }
  };

  // 显示确认对话框
  const handleShowConfirm = async () => {
    try {
      const result = await tauriApi.confirmDialog({
        title: '确认',
        message: '这是一个确认对话框'
      });
      console.log('确认结果:', result);
    } catch (error) {
      console.error('显示确认对话框失败:', error);
    }
  };

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Tauri 示例</h1>
      
      <div className="mb-4">
        <h2 className="text-xl font-semibold mb-2">应用程序信息</h2>
        {appInfo ? (
          <div className="bg-gray-100 p-2 rounded">
            <p>版本: {appInfo.version}</p>
            <p>平台: {appInfo.platform}</p>
            <p>架构: {appInfo.arch}</p>
          </div>
        ) : (
          <p>加载中...</p>
        )}
      </div>
      
      <div className="mb-4">
        <h2 className="text-xl font-semibold mb-2">平台信息</h2>
        {platformInfo ? (
          <div className="bg-gray-100 p-2 rounded">
            <p>Linux: {platformInfo.isLinux ? '是' : '否'}</p>
            <p>Windows: {platformInfo.isWin ? '是' : '否'}</p>
            <p>macOS: {platformInfo.isMac ? '是' : '否'}</p>
          </div>
        ) : (
          <p>加载中...</p>
        )}
      </div>
      
      <div className="mb-4">
        <h2 className="text-xl font-semibold mb-2">窗口控制</h2>
        <div className="flex space-x-2">
          <button
            className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded"
            onClick={handleMinimize}
          >
            最小化
          </button>
          <button
            className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded"
            onClick={handleMaximize}
          >
            {isMaximized ? '还原' : '最大化'}
          </button>
          <button
            className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded"
            onClick={handleClose}
          >
            关闭
          </button>
        </div>
      </div>
      
      <div className="mb-4">
        <h2 className="text-xl font-semibold mb-2">其他功能</h2>
        <div className="flex space-x-2">
          <button
            className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded"
            onClick={handleOpenExternal}
          >
            打开官网
          </button>
          <button
            className="bg-purple-500 hover:bg-purple-600 text-white px-4 py-2 rounded"
            onClick={handleShowMessage}
          >
            显示消息
          </button>
          <button
            className="bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded"
            onClick={handleShowConfirm}
          >
            显示确认
          </button>
        </div>
      </div>
    </div>
  );
};

export default TauriExample;
