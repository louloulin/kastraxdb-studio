import React, { useState, useRef } from 'react';
import { Button, Tabs, Dropdown, message, Spin, Modal, Input } from 'antd';
import {
  SaveOutlined,
  PlayCircleOutlined,
  HistoryOutlined,
  DownOutlined,
  ExportOutlined,
  ImportOutlined,
} from '@ant-design/icons';
import MonacoEditor from '@/components/MonacoEditor';
import { updateService, executeService } from '@/service/data-service';
import ServiceHistory from '../ServiceHistory';
import ServiceExport from '../ServiceExport';
import ServiceImport from '../ServiceImport';
import i18n from '@/i18n';
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
  const [historyVisible, setHistoryVisible] = useState<boolean>(false);
  const [saveModalVisible, setSaveModalVisible] = useState<boolean>(false);
  const [saveComment, setSaveComment] = useState<string>('');
  const [exportVisible, setExportVisible] = useState<boolean>(false);
  const [importVisible, setImportVisible] = useState<boolean>(false);

  // 处理编辑器内容变化
  const handleEditorChange = (value: string) => {
    setScript(value);
  };

  // 打开保存对话框
  const openSaveModal = () => {
    setSaveModalVisible(true);
  };

  // 处理保存服务
  const handleSaveService = async (comment = '') => {
    try {
      setLoading(true);
      const response = await updateService({
        ...service,
        script,
        language,
        comment, // 添加注释，用于版本历史
      });

      if (response && response.success) {
        messageApi.success(i18n('data-service.save.success'));
        onSave(response.data);
        setSaveModalVisible(false);
        setSaveComment('');
      } else {
        messageApi.error(i18n('data-service.save.failed'));
      }
    } catch (error) {
      console.error('Error saving service:', error);
      messageApi.error(i18n('data-service.save.error'));
    } finally {
      setLoading(false);
    }
  };

  // 打开历史记录
  const openHistory = () => {
    setHistoryVisible(true);
  };

  // 处理版本恢复
  const handleRestoreVersion = (restoredService: any) => {
    setScript(restoredService.script || '');
    setLanguage(restoredService.language || 'javascript');
    onSave(restoredService);
  };

  // 打开导出对话框
  const openExport = () => {
    setExportVisible(true);
  };

  // 打开导入对话框
  const openImport = () => {
    setImportVisible(true);
  };

  // 处理导入服务
  const handleImportService = (importedService: any) => {
    setScript(importedService.script || '');
    setLanguage(importedService.language || 'javascript');
    onSave(importedService);
  };

  // 处理执行服务
  const handleExecuteService = async () => {
    try {
      setExecuting(true);
      setActiveTab('result');

      const response = await executeService(service.id, {});

      if (response && response.success) {
        setResult(response.data);
        messageApi.success(i18n('data-service.execute.success'));
      } else {
        setResult({
          error: response.message || i18n('data-service.execute.failed'),
        });
        messageApi.error(i18n('data-service.execute.failed'));
      }
    } catch (error) {
      console.error('Error executing service:', error);
      setResult({
        error: error instanceof Error ? error.message : i18n('data-service.execute.error'),
      });
      messageApi.error(i18n('data-service.execute.error'));
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
    { label: 'SQL', value: 'sql' },
  ];

  return (
    <div className={styles.serviceEditor}>
      {contextHolder}
      <div className={styles.serviceEditorHeader}>
        <div className={styles.serviceEditorTitle}>{service.name}</div>
        <div className={styles.serviceEditorActions}>
          <Dropdown
            menu={{
              items: languageOptions.map((option) => ({
                key: option.value,
                label: option.label,
                onClick: () => handleLanguageChange(option.value),
              })),
            }}
            trigger={['click']}
          >
            <Button>
              {languageOptions.find((option) => option.value === language)?.label || language}
              <DownOutlined />
            </Button>
          </Dropdown>
          <Button type="primary" icon={<SaveOutlined />} onClick={() => openSaveModal()} loading={loading}>
            {i18n('data-service.save')}
          </Button>
          <Button type="primary" icon={<PlayCircleOutlined />} onClick={handleExecuteService} loading={executing}>
            {i18n('data-service.execute')}
          </Button>
          <Button type="default" icon={<HistoryOutlined />} onClick={() => openHistory()}>
            {i18n('data-service.history')}
          </Button>
          <Button type="default" icon={<ExportOutlined />} onClick={() => openExport()}>
            {i18n('data-service.export')}
          </Button>
          <Button type="default" icon={<ImportOutlined />} onClick={() => openImport()}>
            {i18n('data-service.import')}
          </Button>
        </div>
      </div>
      <div className={styles.serviceEditorContent}>
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane tab={i18n('data-service.editor')} key="editor">
            <Spin spinning={loading}>
              <MonacoEditor
                ref={editorRef}
                id="service-editor"
                language={language}
                defaultValue={script}
                didMount={(editor) => {
                  editor.onDidChangeModelContent(() => {
                    handleEditorChange(editor.getValue());
                  });
                }}
              />
            </Spin>
          </TabPane>
          <TabPane tab={i18n('data-service.result')} key="result">
            <Spin spinning={executing}>
              {result ? (
                <div className={styles.serviceEditorResult}>
                  {result.error ? (
                    <div className={styles.serviceEditorError}>{result.error}</div>
                  ) : (
                    <pre className={styles.serviceEditorSuccess}>{JSON.stringify(result, null, 2)}</pre>
                  )}
                </div>
              ) : (
                <div className={styles.serviceEditorEmpty}>{i18n('data-service.result.empty')}</div>
              )}
            </Spin>
          </TabPane>
        </Tabs>
      </div>

      {/* 保存对话框 */}
      <Modal
        title="保存服务"
        open={saveModalVisible}
        onCancel={() => setSaveModalVisible(false)}
        onOk={() => handleSaveService(saveComment)}
        confirmLoading={loading}
      >
        <Input.TextArea
          placeholder="输入版本说明（可选）"
          value={saveComment}
          onChange={(e) => setSaveComment(e.target.value)}
          rows={4}
        />
      </Modal>

      {/* 历史记录对话框 */}
      <ServiceHistory
        serviceId={service.id}
        visible={historyVisible}
        onClose={() => setHistoryVisible(false)}
        onRestore={handleRestoreVersion}
      />

      {/* 导出对话框 */}
      <ServiceExport serviceId={service.id} visible={exportVisible} onClose={() => setExportVisible(false)} />

      {/* 导入对话框 */}
      <ServiceImport visible={importVisible} onClose={() => setImportVisible(false)} onImport={handleImportService} />
    </div>
  );
};

export default ServiceEditor;
