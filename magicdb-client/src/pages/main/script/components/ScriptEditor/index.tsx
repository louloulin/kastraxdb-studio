import React, { useState, useRef } from 'react';
import { Button, Tabs, Dropdown, Menu, message, Spin } from 'antd';
import { 
  SaveOutlined, 
  PlayCircleOutlined, 
  HistoryOutlined,
  DownOutlined
} from '@ant-design/icons';
import MonacoEditor from '@/components/MonacoEditor';
import { updateScript, executeScript } from '@/service/script';
import ScriptVersionHistory from '../ScriptVersionHistory';
import styles from './index.less';

const { TabPane } = Tabs;

interface ScriptEditorProps {
  script: any;
  onSave: (script: any) => void;
}

const ScriptEditor: React.FC<ScriptEditorProps> = ({ script, onSave }) => {
  const [content, setContent] = useState<string>(script.content || '');
  const [language, setLanguage] = useState<string>(script.language || 'javascript');
  const [loading, setLoading] = useState<boolean>(false);
  const [executing, setExecuting] = useState<boolean>(false);
  const [result, setResult] = useState<any>(null);
  const [activeTab, setActiveTab] = useState<string>('editor');
  const [showVersionHistory, setShowVersionHistory] = useState<boolean>(false);
  const editorRef = useRef<any>(null);
  const [messageApi, contextHolder] = message.useMessage();

  // 处理编辑器内容变化
  const handleEditorChange = (value: string) => {
    setContent(value);
  };

  // 处理保存脚本
  const handleSaveScript = async () => {
    try {
      setLoading(true);
      const response = await updateScript({
        ...script,
        content,
        language
      });

      if (response && response.success) {
        messageApi.success('保存成功');
        onSave(response.data);
      } else {
        messageApi.error('保存失败');
      }
    } catch (error) {
      console.error('保存脚本出错:', error);
      messageApi.error('保存脚本出错');
    } finally {
      setLoading(false);
    }
  };

  // 处理执行脚本
  const handleExecuteScript = async () => {
    try {
      setExecuting(true);
      setActiveTab('result');
      
      const response = await executeScript(script.id, {});

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
      console.error('执行脚本出错:', error);
      setResult({
        error: error.message || '执行出错'
      });
      messageApi.error('执行脚本出错');
    } finally {
      setExecuting(false);
    }
  };

  // 处理切换语言
  const handleLanguageChange = (lang: string) => {
    setLanguage(lang);
  };

  // 处理切换版本
  const handleSwitchVersion = (version: any) => {
    setContent(version.content);
    setLanguage(version.language);
    setShowVersionHistory(false);
    messageApi.success(`已切换到版本 ${version.version}`);
  };

  // 语言选项
  const languageOptions = [
    { label: 'JavaScript', value: 'javascript' },
    { label: 'Kotlin', value: 'kotlin' },
    { label: 'Python', value: 'python' },
    { label: 'SQL', value: 'sql' }
  ];

  return (
    <div className={styles.scriptEditor}>
      {contextHolder}
      <div className={styles.scriptEditorHeader}>
        <div className={styles.scriptEditorTitle}>
          {script.name}
        </div>
        <div className={styles.scriptEditorActions}>
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
            icon={<HistoryOutlined />}
            onClick={() => setShowVersionHistory(!showVersionHistory)}
          >
            版本历史
          </Button>
          <Button
            type="primary"
            icon={<SaveOutlined />}
            onClick={handleSaveScript}
            loading={loading}
          >
            保存
          </Button>
          <Button
            type="primary"
            icon={<PlayCircleOutlined />}
            onClick={handleExecuteScript}
            loading={executing}
          >
            执行
          </Button>
        </div>
      </div>
      <div className={styles.scriptEditorContent}>
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane tab="编辑器" key="editor">
            <Spin spinning={loading}>
              <MonacoEditor
                ref={editorRef}
                language={language}
                value={content}
                onChange={handleEditorChange}
                height="calc(100vh - 200px)"
              />
            </Spin>
          </TabPane>
          <TabPane tab="执行结果" key="result">
            <Spin spinning={executing}>
              {result ? (
                <div className={styles.scriptEditorResult}>
                  {result.error ? (
                    <div className={styles.scriptEditorError}>
                      {result.error}
                    </div>
                  ) : (
                    <pre className={styles.scriptEditorSuccess}>
                      {JSON.stringify(result, null, 2)}
                    </pre>
                  )}
                </div>
              ) : (
                <div className={styles.scriptEditorEmpty}>
                  暂无执行结果
                </div>
              )}
            </Spin>
          </TabPane>
        </Tabs>
      </div>
      {showVersionHistory && (
        <ScriptVersionHistory
          scriptId={script.id}
          onClose={() => setShowVersionHistory(false)}
          onSwitchVersion={handleSwitchVersion}
        />
      )}
    </div>
  );
};

export default ScriptEditor;
