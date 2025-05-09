import React from 'react';
import TauriDemo from '../../components/TauriDemo';

const TauriDemoPage: React.FC = () => {
  return (
    <div>
      <div style={{ padding: '16px', background: '#f0f2f5' }}>
        <h1>Tauri 功能演示</h1>
        <p>展示 Tauri 的各种功能和 API</p>
      </div>
      <TauriDemo />
    </div>
  );
};

export default TauriDemoPage;
