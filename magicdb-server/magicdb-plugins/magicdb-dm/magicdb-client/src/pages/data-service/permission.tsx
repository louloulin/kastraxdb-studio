import React, { useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import { Card, Tabs } from 'antd';
import RoleManagement from '@/components/RoleManagement';
import UserPermission from '@/components/UserPermission';

const { TabPane } = Tabs;

const PermissionPage: React.FC = () => {
  // 当前登录用户ID，实际应用中应该从用户上下文获取
  const currentUserId = 'current-user';
  const currentUsername = '当前用户';

  return (
    <PageContainer title="权限管理">
      <Tabs defaultActiveKey="roles">
        <TabPane tab="角色管理" key="roles">
          <RoleManagement />
        </TabPane>
        <TabPane tab="我的权限" key="myPermissions">
          <UserPermission userId={currentUserId} username={currentUsername} />
        </TabPane>
      </Tabs>
    </PageContainer>
  );
};

export default PermissionPage;
