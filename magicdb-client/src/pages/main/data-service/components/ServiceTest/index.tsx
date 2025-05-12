import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Select, Spin, Card, Tabs, message, Divider, Modal, Space, Tooltip, Popconfirm } from 'antd';
import { PlayCircleOutlined, SaveOutlined, DeleteOutlined, CopyOutlined, CheckOutlined } from '@ant-design/icons';
import { getServiceById, executeService } from '@/service/data-service';
import { executeTest, saveTestCase, getTestCasesByService, deleteTestCase } from '@/service/api-test';
import JSONEditor from '@/components/JSONEditor';
import i18n from '@/i18n';
import styles from './index.less';

const { TabPane } = Tabs;
const { Option } = Select;

interface ServiceTestProps {
  serviceId: string;
}

const ServiceTest: React.FC<ServiceTestProps> = ({ serviceId }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(false);
  const [executing, setExecuting] = useState<boolean>(false);
  const [service, setService] = useState<any>(null);
  const [result, setResult] = useState<any>(null);
  const [savedTests, setSavedTests] = useState<any[]>([]);
  const [activeTab, setActiveTab] = useState<string>('params');
  const [saveModalVisible, setSaveModalVisible] = useState<boolean>(false);
  const [testCaseName, setTestCaseName] = useState<string>('');
  const [testCaseDescription, setTestCaseDescription] = useState<string>('');
  const [copied, setCopied] = useState<boolean>(false);
  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    if (serviceId) {
      fetchServiceDetails();
      loadSavedTests();
    }
  }, [serviceId]);

  const fetchServiceDetails = async () => {
    try {
      setLoading(true);
      const response = await getServiceById(serviceId);
      if (response && response.success) {
        setService(response.data);
        // Initialize form with default values
        const initialValues: any = {};
        response.data.parameters?.forEach((param: any) => {
          if (param.defaultValue !== undefined && param.defaultValue !== null) {
            initialValues[param.name] = param.defaultValue;
          }
        });
        form.setFieldsValue(initialValues);
      } else {
        messageApi.error(i18n('data-service.test.load-failed'));
      }
    } catch (error) {
      console.error('Failed to fetch service details:', error);
      messageApi.error(i18n('data-service.test.load-error'));
    } finally {
      setLoading(false);
    }
  };

  const loadSavedTests = async () => {
    try {
      // Try to load from API first
      const response = await getTestCasesByService(serviceId);

      if (response && response.success && response.data) {
        setSavedTests(response.data);
        return;
      }

      // Fallback to localStorage
      const savedTestsJson = localStorage.getItem(`service-tests-${serviceId}`);
      if (savedTestsJson) {
        const tests = JSON.parse(savedTestsJson);
        setSavedTests(tests);
      }
    } catch (error) {
      console.error('Failed to load saved tests:', error);

      // Fallback to localStorage
      try {
        const savedTestsJson = localStorage.getItem(`service-tests-${serviceId}`);
        if (savedTestsJson) {
          const tests = JSON.parse(savedTestsJson);
          setSavedTests(tests);
        }
      } catch (e) {
        console.error('Failed to load saved tests from localStorage:', e);
      }
    }
  };

  const handleExecute = async () => {
    try {
      const values = await form.validateFields();
      setExecuting(true);
      setActiveTab('result');

      // Try to use the new API first
      try {
        const response = await executeTest(serviceId, values);

        if (response && response.success) {
          setResult(response.data);
          messageApi.success(i18n('data-service.test.execute-success'));
          return;
        }
      } catch (e) {
        console.error('Failed to execute test with new API, falling back to old API:', e);
      }

      // Fallback to old API
      const response = await executeService(serviceId, values);

      if (response && response.success) {
        setResult(response.data);
        messageApi.success(i18n('data-service.test.execute-success'));
      } else {
        setResult({
          error: response.message || i18n('data-service.test.execute-failed')
        });
        messageApi.error(i18n('data-service.test.execute-failed'));
      }
    } catch (error) {
      console.error('Failed to execute service:', error);
      messageApi.error(i18n('data-service.test.execute-error'));
    } finally {
      setExecuting(false);
    }
  };

  const handleSaveTest = () => {
    setSaveModalVisible(true);
  };

  const handleSaveTestConfirm = async () => {
    try {
      const values = await form.validateFields();

      if (!testCaseName) {
        messageApi.error(i18n('data-service.test.name-required'));
        return;
      }

      // Try to save using the new API first
      try {
        const testCase = {
          id: '',
          name: testCaseName,
          serviceId,
          parameters: values,
          description: testCaseDescription
        };

        const response = await saveTestCase(testCase);

        if (response && response.success) {
          messageApi.success(i18n('data-service.test.save-success'));
          setSaveModalVisible(false);
          setTestCaseName('');
          setTestCaseDescription('');
          loadSavedTests();
          return;
        }
      } catch (e) {
        console.error('Failed to save test case with new API, falling back to localStorage:', e);
      }

      // Fallback to localStorage
      const newTest = {
        id: Date.now().toString(),
        name: testCaseName,
        parameters: values,
        description: testCaseDescription,
        createdAt: new Date().toISOString()
      };

      const updatedTests = [...savedTests, newTest];
      setSavedTests(updatedTests);

      // Save to localStorage
      localStorage.setItem(`service-tests-${serviceId}`, JSON.stringify(updatedTests));

      messageApi.success(i18n('data-service.test.save-success'));
      setSaveModalVisible(false);
      setTestCaseName('');
      setTestCaseDescription('');
    } catch (error) {
      console.error('Failed to save test:', error);
      messageApi.error(i18n('data-service.test.save-error'));
    }
  };

  const handleLoadTest = (test: any) => {
    form.setFieldsValue(test.parameters);
    messageApi.success(i18n('data-service.test.load-success'));
  };

  const handleDeleteTest = async (testId: string) => {
    // Try to delete using the new API first
    try {
      const response = await deleteTestCase(testId);

      if (response && response.success) {
        messageApi.success(i18n('data-service.test.delete-success'));
        loadSavedTests();
        return;
      }
    } catch (e) {
      console.error('Failed to delete test case with new API, falling back to localStorage:', e);
    }

    // Fallback to localStorage
    const updatedTests = savedTests.filter(test => test.id !== testId);
    setSavedTests(updatedTests);

    // Save to localStorage
    localStorage.setItem(`service-tests-${serviceId}`, JSON.stringify(updatedTests));

    messageApi.success(i18n('data-service.test.delete-success'));
  };

  // Copy result to clipboard
  const handleCopyResult = () => {
    if (result) {
      const resultText = JSON.stringify(result, null, 2);
      navigator.clipboard.writeText(resultText);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
      messageApi.success(i18n('data-service.test.copied'));
    }
  };

  const renderParameterInput = (param: any) => {
    switch (param.type.toLowerCase()) {
      case 'string':
        return <Input placeholder={param.description} />;
      case 'number':
        return <Input type="number" placeholder={param.description} />;
      case 'boolean':
        return (
          <Select placeholder={param.description}>
            <Option value="true">True</Option>
            <Option value="false">False</Option>
          </Select>
        );
      case 'object':
      case 'array':
        return <JSONEditor height={100} />;
      default:
        return <Input placeholder={param.description} />;
    }
  };

  if (loading) {
    return <Spin spinning={true} />;
  }

  if (!service) {
    return <div>{i18n('data-service.test.no-service')}</div>;
  }

  return (
    <div className={styles.serviceTest}>
      {contextHolder}
      <h2>{service.name}</h2>
      <p>{service.description}</p>

      <div className={styles.actions}>
        <Space>
          <Button
            type="primary"
            icon={<PlayCircleOutlined />}
            onClick={handleExecute}
            loading={executing}
          >
            {i18n('data-service.test.execute')}
          </Button>
          <Button
            icon={<SaveOutlined />}
            onClick={handleSaveTest}
          >
            {i18n('data-service.test.save')}
          </Button>
        </Space>
      </div>

      <Tabs activeKey={activeTab} onChange={setActiveTab} className={styles.tabs}>
        <TabPane tab={i18n('data-service.test.params')} key="params">
          <div className={styles.tabContent}>
            <Form form={form} layout="vertical" className={styles.form}>
              {service.parameters?.map((param: any) => (
                <Form.Item
                  key={param.name}
                  name={param.name}
                  label={
                    <span>
                      {param.name}
                      {param.required && <span className={styles.required}>*</span>}
                      {param.description && (
                        <Tooltip title={param.description}>
                          <span className={styles.info}>i</span>
                        </Tooltip>
                      )}
                    </span>
                  }
                  rules={[
                    {
                      required: param.required,
                      message: i18n('data-service.test.param-required')
                    }
                  ]}
                >
                  {renderParameterInput(param)}
                </Form.Item>
              ))}
            </Form>
          </div>
        </TabPane>

        <TabPane tab={i18n('data-service.test.result')} key="result">
          <div className={styles.tabContent}>
            <Spin spinning={executing}>
              {result ? (
                <div className={styles.resultContainer}>
                  <div className={styles.resultHeader}>
                    <div className={styles.resultInfo}>
                      <div className={styles.resultStatus}>
                        {result.success !== false ? (
                          <span className={styles.success}>Success</span>
                        ) : (
                          <span className={styles.error}>Error</span>
                        )}
                      </div>
                      {result.executionTime !== undefined && (
                        <div className={styles.resultTime}>
                          {result.executionTime} ms
                        </div>
                      )}
                    </div>
                    <Button
                      icon={copied ? <CheckOutlined /> : <CopyOutlined />}
                      onClick={handleCopyResult}
                    >
                      {i18n('data-service.test.copy')}
                    </Button>
                  </div>
                  <JSONEditor
                    value={JSON.stringify(result, null, 2)}
                    height={400}
                    readOnly
                  />
                  {result.error && (
                    <div className={styles.resultError}>
                      {result.error}
                    </div>
                  )}
                </div>
              ) : (
                <div className={styles.noResult}>
                  {i18n('data-service.test.no-result')}
                </div>
              )}
            </Spin>
          </div>
        </TabPane>

        <TabPane tab={i18n('data-service.test.saved')} key="saved">
          <div className={styles.tabContent}>
            {savedTests.length > 0 ? (
              <div className={styles.savedTests}>
                {savedTests.map(test => (
                  <Card
                    key={test.id}
                    size="small"
                    title={test.name}
                    extra={
                      <Space>
                        <Button
                          size="small"
                          onClick={() => handleLoadTest(test)}
                        >
                          {i18n('data-service.test.load')}
                        </Button>
                        <Popconfirm
                          title={i18n('data-service.test.delete-confirm')}
                          onConfirm={() => handleDeleteTest(test.id)}
                          okText={i18n('common.yes')}
                          cancelText={i18n('common.no')}
                        >
                          <Button
                            size="small"
                            danger
                            icon={<DeleteOutlined />}
                          />
                        </Popconfirm>
                      </Space>
                    }
                    className={styles.savedTestCard}
                  >
                    {test.description && (
                      <div className={styles.testDescription}>
                        {test.description}
                      </div>
                    )}
                    <div className={styles.savedTestDate}>
                      {test.createdAt && new Date(test.createdAt).toLocaleString()}
                    </div>
                  </Card>
                ))}
              </div>
            ) : (
              <div className={styles.noSavedTests}>
                {i18n('data-service.test.no-saved-tests')}
              </div>
            )}
          </div>
        </TabPane>
      </Tabs>

      <Modal
        title={i18n('data-service.test.save-test')}
        open={saveModalVisible}
        onOk={handleSaveTestConfirm}
        onCancel={() => setSaveModalVisible(false)}
      >
        <Form layout="vertical">
          <Form.Item
            label={i18n('data-service.test.test-name')}
            required
          >
            <Input
              value={testCaseName}
              onChange={(e) => setTestCaseName(e.target.value)}
              placeholder={i18n('data-service.test.test-name-placeholder')}
            />
          </Form.Item>
          <Form.Item
            label={i18n('data-service.test.test-description')}
          >
            <Input.TextArea
              value={testCaseDescription}
              onChange={(e) => setTestCaseDescription(e.target.value)}
              placeholder={i18n('data-service.test.test-description-placeholder')}
              rows={4}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ServiceTest;
