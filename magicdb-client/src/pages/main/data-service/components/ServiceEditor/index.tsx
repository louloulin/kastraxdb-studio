import React, { useState, useRef } from 'react';
import { Button, Tabs, Dropdown, Menu, message, Spin } from 'antd';
import { 
  SaveOutlined, 
  PlayCircleOutlined, 
  HistoryOutlined,
  DownOutlined
} from '@ant-design/icons';
import MonacoEditor from '@/components/MonacoEditor';
import { updateService, executeService } from '@/service/data-service';
import styles from './index.less';

const { TabPane } = Tabs;

interface ServiceEditorProps {
  service: any;
  onSave: (service: any) => void;
}

const ServiceEditor: React.FC<ServiceEditorProps> = ({ service, onSave }) => {
  const [script, setScript] = useState<string>(service.script || '');
  const [language, setLanguage] = useState<string>(service.language || 'javascript');
  const [loading, setLoading] = useState<boolean>(false);
  const [executing, setExecuting] = useState<boolean>(false);
  const [result, setResult] = useState<any>(null);
  const [activeTab, setActiveTab] = useState<string>('editor');
  const editorRef = useRef<any>(null);
  const [messageApi, contextHolder] = message.useMessage();

  // 处理编辑器内容变化
  const handleEditorChange = (value: string) => {
    setScript(value);
  };

  // 处理保存服务
  const handleSaveService = async () => {
    try {
      setLoading(true);
      const response = await updateService({
        ...service,
        script,
        language
      });

      if (response && response.success) {
        messageApi.success('保存成功');
        onSave(response.data);
      } else {
        messageApi.error('保存失败');
      }
    } catch (error) {
      console.error('保存数据服务出错:', error);
      messageApi.error('保存数据服务出错');
    } finally {
      setLoading(false);
    }
  };

  // 处理执行服务
  const handleExecuteService = async () => {
    try {
      setExecuting(true);
      setActiveTab('result');
      
      const response = await executeService(service.id, {});

      if (response && response.success) {
        setResult(response.data);
        messageApi.success('执行成功');
      } else {
        setResult({
          error: response.message || '执行失败'
        });
        messageApi.error('执行失败');
      }
    } catch (error) {
      console.error('执行数据服务出错:', error);
      setResult({
        error: error.message || '执行出错'
      });
      messageApi.error('执行数据服务出错');
    } finally {
      setExecuting(false);
    }
  };

  // 处理切换语言
  const handleLanguageChange = (lang: string) => {
    setLanguage(lang);
  };

  // 语言选项
  const languageOptions = [
    { label: 'JavaScript', value: 'javascript' },
    { label: 'Kotlin', value: 'kotlin' },
    { label: 'Python', value: 'python' },
    { label: 'SQL', value: 'sql' }
  ];

  return (
    <div className={styles.serviceEditor}>
      {contextHolder}
      <div className={styles.serviceEditorHeader}>
        <div className={styles.serviceEditorTitle}>
          {service.name}
        </div>
        <div className={styles.serviceEditorActions}>
          <Dropdown
            overlay={
              <Menu>
                {languageOptions.map(option => (
                  <Menu.Item
                    key={option.value}
                    onClick={() => handleLanguageChange(option.value)}
                  >
                    {option.label}
                  </Menu.Item>
                ))}
              </Menu>
            }
            trigger={['click']}
          >
            <Button>
              {languageOptions.find(option => option.value === language)?.label || language}
              <DownOutlined />
            </Button>
          </Dropdown>
          <Button
            type="primary"
            icon={<SaveOutlined />}
            onClick={handleSaveService}
            loading={loading}
          >
            保存
          </Button>
          <Button
            type="primary"
            icon={<PlayCircleOutlined />}
            onClick={handleExecuteService}
            loading={executing}
          >
            执行
          </Button>
        </div>
      </div>
      <div className={styles.serviceEditorContent}>
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane tab="编辑器" key="editor">
            <Spin spinning={loading}>
              <MonacoEditor
                ref={editorRef}
                language={language}
                value={script}
                onChange={handleEditorChange}
                height="calc(100vh - 200px)"
              />
            </Spin>
          </TabPane>
          <TabPane tab="执行结果" key="result">
            <Spin spinning={executing}>
              {result ? (
                <div className={styles.serviceEditorResult}>
                  {result.error ? (
                    <div className={styles.serviceEditorError}>
                      {result.error}
                    </div>
                  ) : (
                    <pre className={styles.serviceEditorSuccess}>
                      {JSON.stringify(result, null, 2)}
                    </pre>
                  )}
                </div>
              ) : (
                <div className={styles.serviceEditorEmpty}>
                  暂无执行结果
                </div>
              )}
            </Spin>
          </TabPane>
        </Tabs>
      </div>
    </div>
  );
};

export default ServiceEditor;
