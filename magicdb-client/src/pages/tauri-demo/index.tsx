import React from 'react';
import { PageContainer } from '@ant-design/pro-components';
import TauriDemo from '../../components/TauriDemo';

const TauriDemoPage: React.FC = () => {
  return (
    <PageContainer
      header={{
        title: 'Tauri 功能演示',
        subTitle: '展示 Tauri 的各种功能和 API',
      }}
    >
      <TauriDemo />
    </PageContainer>
  );
};

export default TauriDemoPage;
