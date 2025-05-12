import React, { useEffect, useState } from 'react';
import { Switch, Dropdown, Menu, Button } from 'antd';
import { BulbOutlined, DownOutlined } from '@ant-design/icons';
import { useIntl } from 'umi';
import styles from './index.less';

export type ThemeType = 'light' | 'dark' | 'system';

interface ThemeSwitcherProps {
  className?: string;
  style?: React.CSSProperties;
  onChange?: (theme: ThemeType) => void;
}

const ThemeSwitcher: React.FC<ThemeSwitcherProps> = ({ className, style, onChange }) => {
  const intl = useIntl();
  const [theme, setTheme] = useState<ThemeType>(() => {
    // 从本地存储获取主题设置
    const savedTheme = localStorage.getItem('theme') as ThemeType;
    return savedTheme || 'system';
  });

  // 应用主题
  const applyTheme = (newTheme: ThemeType) => {
    const isDark = 
      newTheme === 'dark' || 
      (newTheme === 'system' && window.matchMedia('(prefers-color-scheme: dark)').matches);
    
    document.documentElement.setAttribute('data-theme', isDark ? 'dark' : 'light');
    
    // 更新 Ant Design 主题
    document.documentElement.classList.toggle('dark', isDark);
    
    // 存储主题设置
    localStorage.setItem('theme', newTheme);
  };

  // 处理主题变化
  const handleThemeChange = (newTheme: ThemeType) => {
    setTheme(newTheme);
    applyTheme(newTheme);
    onChange?.(newTheme);
  };

  // 监听系统主题变化
  useEffect(() => {
    if (theme === 'system') {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
      
      const handleChange = () => {
        applyTheme('system');
      };
      
      mediaQuery.addEventListener('change', handleChange);
      
      return () => {
        mediaQuery.removeEventListener('change', handleChange);
      };
    }
  }, [theme]);

  // 初始应用主题
  useEffect(() => {
    applyTheme(theme);
  }, []);

  // 主题菜单
  const menu = (
    <Menu 
      selectedKeys={[theme]}
      onClick={({ key }) => handleThemeChange(key as ThemeType)}
    >
      <Menu.Item key="light">
        {intl.formatMessage({ id: 'data-service.ui.theme.light' })}
      </Menu.Item>
      <Menu.Item key="dark">
        {intl.formatMessage({ id: 'data-service.ui.theme.dark' })}
      </Menu.Item>
      <Menu.Item key="system">
        {intl.formatMessage({ id: 'data-service.ui.theme.system' })}
      </Menu.Item>
    </Menu>
  );

  return (
    <div className={`${styles.themeSwitcher} ${className}`} style={style}>
      <Dropdown overlay={menu} trigger={['click']}>
        <Button type="text" className={styles.themeButton}>
          <BulbOutlined />
          <span className={styles.themeText}>
            {intl.formatMessage({ id: `data-service.ui.theme.${theme}` })}
          </span>
          <DownOutlined />
        </Button>
      </Dropdown>
    </div>
  );
};

export default ThemeSwitcher;
