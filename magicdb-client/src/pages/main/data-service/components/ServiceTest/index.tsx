import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Select, Spin, Card, Tabs, message, Divider } from 'antd';
import { PlayCircleOutlined, SaveOutlined, DeleteOutlined } from '@ant-design/icons';
import { getServiceById, executeService } from '@/service/data-service';
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

  const loadSavedTests = () => {
    // Load saved tests from localStorage
    try {
      const savedTestsJson = localStorage.getItem(`service-tests-${serviceId}`);
      if (savedTestsJson) {
        const tests = JSON.parse(savedTestsJson);
        setSavedTests(tests);
      }
    } catch (error) {
      console.error('Failed to load saved tests:', error);
    }
  };

  const handleExecute = async () => {
    try {
      const values = await form.validateFields();
      setExecuting(true);
      setActiveTab('result');

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

  const handleSaveTest = async () => {
    try {
      const values = await form.validateFields();
      const testName = window.prompt(i18n('data-service.test.save-prompt'));
      
      if (!testName) return;
      
      const newTest = {
        id: Date.now().toString(),
        name: testName,
        parameters: values,
        createdAt: new Date().toISOString()
      };
      
      const updatedTests = [...savedTests, newTest];
      setSavedTests(updatedTests);
      
      // Save to localStorage
      localStorage.setItem(`service-tests-${serviceId}`, JSON.stringify(updatedTests));
      
      messageApi.success(i18n('data-service.test.save-success'));
    } catch (error) {
      console.error('Failed to save test:', error);
      messageApi.error(i18n('data-service.test.save-error'));
    }
  };

  const handleLoadTest = (test: any) => {
    form.setFieldsValue(test.parameters);
    messageApi.success(i18n('data-service.test.load-success'));
  };

  const handleDeleteTest = (testId: string) => {
    const updatedTests = savedTests.filter(test => test.id !== testId);
    setSavedTests(updatedTests);
    
    // Save to localStorage
    localStorage.setItem(`service-tests-${serviceId}`, JSON.stringify(updatedTests));
    
    messageApi.success(i18n('data-service.test.delete-success'));
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
      
      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        <TabPane tab={i18n('data-service.test.params')} key="params">
          <Form form={form} layout="vertical">
            {service.parameters?.map((param: any) => (
              <Form.Item
                key={param.name}
                name={param.name}
                label={`${param.name}${param.required ? ' *' : ''}`}
                tooltip={param.description}
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
            
            <Form.Item>
              <Button
                type="primary"
                icon={<PlayCircleOutlined />}
                onClick={handleExecute}
                loading={executing}
              >
                {i18n('data-service.test.execute')}
              </Button>
              <Button
                style={{ marginLeft: 8 }}
                icon={<SaveOutlined />}
                onClick={handleSaveTest}
              >
                {i18n('data-service.test.save')}
              </Button>
            </Form.Item>
          </Form>
          
          {savedTests.length > 0 && (
            <>
              <Divider>{i18n('data-service.test.saved')}</Divider>
              <div className={styles.savedTests}>
                {savedTests.map(test => (
                  <Card
                    key={test.id}
                    size="small"
                    title={test.name}
                    extra={
                      <>
                        <Button
                          type="link"
                          size="small"
                          onClick={() => handleLoadTest(test)}
                        >
                          {i18n('data-service.test.load')}
                        </Button>
                        <Button
                          type="link"
                          danger
                          size="small"
                          icon={<DeleteOutlined />}
                          onClick={() => handleDeleteTest(test.id)}
                        />
                      </>
                    }
                    className={styles.savedTestCard}
                  >
                    <div className={styles.savedTestDate}>
                      {new Date(test.createdAt).toLocaleString()}
                    </div>
                  </Card>
                ))}
              </div>
            </>
          )}
        </TabPane>
        
        <TabPane tab={i18n('data-service.test.result')} key="result">
          <Spin spinning={executing}>
            {result ? (
              <div className={styles.resultContainer}>
                <JSONEditor
                  value={JSON.stringify(result, null, 2)}
                  height={400}
                  readOnly
                />
              </div>
            ) : (
              <div className={styles.noResult}>
                {i18n('data-service.test.no-result')}
              </div>
            )}
          </Spin>
        </TabPane>
      </Tabs>
    </div>
  );
};

export default ServiceTest;
