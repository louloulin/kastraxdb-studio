import React, { useState, useEffect } from 'react';
import { Button, Card, Space, Typography, Divider, Input, message } from 'antd';
import { FolderOpenOutlined, SaveOutlined, GlobalOutlined, InfoCircleOutlined } from '@ant-design/icons';
import tauriApi from '../utils/tauri-api';
import { isTauri } from '../utils/environment';

const { Title, Text, Paragraph } = Typography;

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

const TauriDemo: React.FC = () => {
  const [appInfo, setAppInfo] = useState<AppInfo | null>(null);
  const [platformInfo, setPlatformInfo] = useState<PlatformInfo | null>(null);
  const [isMaximized, setIsMaximized] = useState<boolean>(false);
  const [filePath, setFilePath] = useState<string>('');
  const [fileContent, setFileContent] = useState<string>('');
  const [saveContent, setSaveContent] = useState<string>('');
  const [savePath, setSavePath] = useState<string>('');
  const [homeDir, setHomeDir] = useState<string>('');
  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    const fetchData = async () => {
      try {
        // 获取应用信息
        const info = await tauriApi.getAppInfo();
        setAppInfo(info);

        // 获取平台信息
        const platform = tauriApi.getPlatform();
        setPlatformInfo(platform);

        // 获取窗口状态
        if (isTauri()) {
          const maximized = await tauriApi.isMaximized();
          setIsMaximized(maximized);
          
          // 获取用户主目录
          const home = await tauriApi.getHomeDir();
          setHomeDir(home);
        }
      } catch (error) {
        console.error('获取数据失败:', error);
        messageApi.error('获取数据失败');
      }
    };

    fetchData();
  }, [messageApi]);

  // 窗口控制
  const handleMinimize = async () => {
    try {
      await tauriApi.minimizeWindow();
    } catch (error) {
      console.error('最小化窗口失败:', error);
      messageApi.error('最小化窗口失败');
    }
  };

  const handleMaximize = async () => {
    try {
      await tauriApi.setMaximize();
      const maximized = await tauriApi.isMaximized();
      setIsMaximized(maximized);
    } catch (error) {
      console.error('最大化窗口失败:', error);
      messageApi.error('最大化窗口失败');
    }
  };

  const handleClose = async () => {
    try {
      const confirmed = await tauriApi.confirmDialog({
        title: '确认',
        message: '确定要关闭窗口吗？'
      });
      
      if (confirmed) {
        await tauriApi.closeWindow();
      }
    } catch (error) {
      console.error('关闭窗口失败:', error);
      messageApi.error('关闭窗口失败');
    }
  };

  // 文件操作
  const handleOpenFile = async () => {
    try {
      const selected = await tauriApi.openFileDialog({
        title: '选择文件',
        filters: [
          { name: '文本文件', extensions: ['txt', 'md', 'json'] },
          { name: '所有文件', extensions: ['*'] }
        ]
      });
      
      if (selected && typeof selected === 'string') {
        setFilePath(selected);
        const content = await tauriApi.readFile(selected);
        setFileContent(content);
        messageApi.success('文件读取成功');
      }
    } catch (error) {
      console.error('打开文件失败:', error);
      messageApi.error('打开文件失败');
    }
  };

  const handleSaveFile = async () => {
    try {
      if (!saveContent) {
        messageApi.warning('请输入要保存的内容');
        return;
      }
      
      const selected = await tauriApi.saveFileDialog({
        title: '保存文件',
        filters: [
          { name: '文本文件', extensions: ['txt'] },
          { name: '所有文件', extensions: ['*'] }
        ]
      });
      
      if (selected) {
        await tauriApi.writeFile(selected, saveContent);
        setSavePath(selected);
        messageApi.success('文件保存成功');
      }
    } catch (error) {
      console.error('保存文件失败:', error);
      messageApi.error('保存文件失败');
    }
  };

  // 打开外部链接
  const handleOpenExternal = async () => {
    try {
      await tauriApi.openExternal('https://tauri.app/');
      messageApi.success('已打开 Tauri 官网');
    } catch (error) {
      console.error('打开外部链接失败:', error);
      messageApi.error('打开外部链接失败');
    }
  };

  // 显示应用信息
  const handleShowAppInfo = async () => {
    try {
      await tauriApi.messageDialog({
        title: '应用信息',
        message: `版本: ${appInfo?.version || '未知'}\n平台: ${appInfo?.platform || '未知'}\n架构: ${appInfo?.arch || '未知'}`
      });
    } catch (error) {
      console.error('显示应用信息失败:', error);
      messageApi.error('显示应用信息失败');
    }
  };

  return (
    <div className="p-6">
      {contextHolder}
      <Title level={2}>Tauri 功能演示</Title>
      <Paragraph>
        这个组件演示了 Tauri 的基本功能，包括窗口控制、文件操作、对话框等。
        {!isTauri() && (
          <Text type="warning"> 注意：当前不在 Tauri 环境中运行，某些功能可能不可用。</Text>
        )}
      </Paragraph>

      <Divider orientation="left">应用信息</Divider>
      <Card title="应用和平台信息" style={{ marginBottom: 16 }}>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <Title level={4}>应用信息</Title>
            {appInfo ? (
              <ul className="list-disc pl-5">
                <li>版本: {appInfo.version}</li>
                <li>平台: {appInfo.platform}</li>
                <li>架构: {appInfo.arch}</li>
              </ul>
            ) : (
              <Text type="secondary">加载中...</Text>
            )}
          </div>
          <div>
            <Title level={4}>平台信息</Title>
            {platformInfo ? (
              <ul className="list-disc pl-5">
                <li>Linux: {platformInfo.isLinux ? '是' : '否'}</li>
                <li>Windows: {platformInfo.isWin ? '是' : '否'}</li>
                <li>macOS: {platformInfo.isMac ? '是' : '否'}</li>
              </ul>
            ) : (
              <Text type="secondary">加载中...</Text>
            )}
          </div>
        </div>
        {homeDir && (
          <div className="mt-4">
            <Title level={4}>用户主目录</Title>
            <Text>{homeDir}</Text>
          </div>
        )}
      </Card>

      <Divider orientation="left">窗口控制</Divider>
      <Card title="窗口操作" style={{ marginBottom: 16 }}>
        <Space>
          <Button onClick={handleMinimize}>最小化</Button>
          <Button onClick={handleMaximize}>
            {isMaximized ? '还原' : '最大化'}
          </Button>
          <Button danger onClick={handleClose}>关闭</Button>
        </Space>
      </Card>

      <Divider orientation="left">文件操作</Divider>
      <Card title="文件读取" style={{ marginBottom: 16 }}>
        <Space direction="vertical" style={{ width: '100%' }}>
          <Button icon={<FolderOpenOutlined />} onClick={handleOpenFile}>
            打开文件
          </Button>
          {filePath && (
            <div>
              <Text strong>文件路径:</Text>
              <Paragraph copyable>{filePath}</Paragraph>
            </div>
          )}
          {fileContent && (
            <div>
              <Text strong>文件内容:</Text>
              <Input.TextArea
                value={fileContent}
                rows={4}
                readOnly
                style={{ marginTop: 8 }}
              />
            </div>
          )}
        </Space>
      </Card>

      <Card title="文件保存" style={{ marginBottom: 16 }}>
        <Space direction="vertical" style={{ width: '100%' }}>
          <Input.TextArea
            value={saveContent}
            onChange={(e) => setSaveContent(e.target.value)}
            placeholder="输入要保存的内容"
            rows={4}
          />
          <Button icon={<SaveOutlined />} onClick={handleSaveFile}>
            保存文件
          </Button>
          {savePath && (
            <div>
              <Text strong>保存路径:</Text>
              <Paragraph copyable>{savePath}</Paragraph>
            </div>
          )}
        </Space>
      </Card>

      <Divider orientation="left">其他功能</Divider>
      <Card title="其他操作">
        <Space>
          <Button 
            icon={<GlobalOutlined />} 
            onClick={handleOpenExternal}
          >
            打开 Tauri 官网
          </Button>
          <Button 
            icon={<InfoCircleOutlined />} 
            onClick={handleShowAppInfo}
          >
            显示应用信息
          </Button>
        </Space>
      </Card>
    </div>
  );
};

export default TauriDemo;
