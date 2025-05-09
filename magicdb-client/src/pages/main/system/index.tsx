import React, { useState } from 'react';
import { Tabs, Card } from 'antd';
import UserManagement from './components/UserManagement';
import RoleManagement from './components/RoleManagement';
import SystemConfig from './components/SystemConfig';
import LogViewer from './components/LogViewer';
import styles from './index.less';

const { TabPane } = Tabs;

const SystemPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<string>('user');

  return (
    <div className={styles.systemContainer}>
      <Card className={styles.systemCard}>
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane tab="用户管理" key="user">
            <UserManagement />
          </TabPane>
          <TabPane tab="角色管理" key="role">
            <RoleManagement />
          </TabPane>
          <TabPane tab="系统配置" key="config">
            <SystemConfig />
          </TabPane>
          <TabPane tab="日志查看" key="log">
            <LogViewer />
          </TabPane>
        </Tabs>
      </Card>
    </div>
  );
};

export default SystemPage;
