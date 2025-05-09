import React from 'react';
import { Menu, Button } from 'antd';
import { DesktopOutlined } from '@ant-design/icons';
import { history } from 'umi';

const TauriDemoMenu: React.FC = () => {
  const handleClick = () => {
    history.push('/tauri-demo');
  };

  return (
    <Button 
      type="primary" 
      icon={<DesktopOutlined />} 
      onClick={handleClick}
      style={{ margin: '16px' }}
    >
      Tauri 演示
    </Button>
  );
};

export default TauriDemoMenu;
