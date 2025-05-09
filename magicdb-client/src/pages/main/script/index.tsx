import React, { useEffect, useState } from 'react';
import { Layout, Spin, message } from 'antd';
import ScriptEditor from './components/ScriptEditor';
import ScriptTree from './components/ScriptTree';
import ScriptParams from './components/ScriptParams';
import { useModel } from 'umi';
import { getScriptList, getScriptById } from '@/service/script';
import styles from './index.less';

const { Sider, Content } = Layout;

interface ScriptProps {
  id: string;
  name: string;
  content: string;
  language: string;
  description: string;
  groupId: string;
  tags: string[];
  parameters: any[];
}

const ScriptPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [scriptList, setScriptList] = useState<any[]>([]);
  const [selectedScript, setSelectedScript] = useState<ScriptProps | null>(null);
  const [messageApi, contextHolder] = message.useMessage();
  const { initialState } = useModel('@@initialState');

  useEffect(() => {
    fetchScriptList();
  }, []);

  const fetchScriptList = async () => {
    try {
      setLoading(true);
      const response = await getScriptList();
      if (response && response.success) {
        setScriptList(response.data || []);
      } else {
        messageApi.error('获取脚本列表失败');
      }
    } catch (error) {
      console.error('获取脚本列表出错:', error);
      messageApi.error('获取脚本列表出错');
    } finally {
      setLoading(false);
    }
  };

  const handleSelectScript = async (scriptId: string) => {
    try {
      setLoading(true);
      const response = await getScriptById(scriptId);
      if (response && response.success) {
        setSelectedScript(response.data);
      } else {
        messageApi.error('获取脚本详情失败');
      }
    } catch (error) {
      console.error('获取脚本详情出错:', error);
      messageApi.error('获取脚本详情出错');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveScript = async (script: ScriptProps) => {
    // 保存脚本的逻辑将在ScriptEditor组件中实现
    await fetchScriptList();
  };

  return (
    <Layout className={styles.scriptContainer}>
      {contextHolder}
      <Sider width={280} theme="light" className={styles.scriptSider}>
        <ScriptTree 
          scriptList={scriptList} 
          onSelectScript={handleSelectScript} 
          onRefresh={fetchScriptList}
        />
      </Sider>
      <Content className={styles.scriptContent}>
        <Spin spinning={loading}>
          {selectedScript ? (
            <ScriptEditor 
              script={selectedScript} 
              onSave={handleSaveScript} 
            />
          ) : (
            <div className={styles.scriptEmpty}>
              请选择或创建一个脚本
            </div>
          )}
        </Spin>
      </Content>
      <Sider width={320} theme="light" className={styles.scriptParams}>
        <ScriptParams 
          script={selectedScript} 
          onSave={handleSaveScript}
        />
      </Sider>
    </Layout>
  );
};

export default ScriptPage;
