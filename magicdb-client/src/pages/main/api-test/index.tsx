import React, { useState, useEffect } from 'react';
import { Layout, Tabs, Button, Select, Input, Form, Spin, message } from 'antd';
import { SendOutlined, SaveOutlined, CopyOutlined, DeleteOutlined } from '@ant-design/icons';
import MonacoEditor from '@/components/MonacoEditor';
import RequestHistory from './components/RequestHistory';
import ResponseViewer from './components/ResponseViewer';
import { testApi } from '@/service/api-test';
import styles from './index.less';

const { Header, Content, Sider } = Layout;
const { TabPane } = Tabs;
const { Option } = Select;

const ApiTestPage: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(false);
  const [requestBody, setRequestBody] = useState<string>('');
  const [response, setResponse] = useState<any>(null);
  const [activeTab, setActiveTab] = useState<string>('params');
  const [history, setHistory] = useState<any[]>([]);
  const [messageApi, contextHolder] = message.useMessage();

  // 加载历史记录
  useEffect(() => {
    const savedHistory = localStorage.getItem('apiTestHistory');
    if (savedHistory) {
      try {
        setHistory(JSON.parse(savedHistory));
      } catch (error) {
        console.error('解析历史记录出错:', error);
      }
    }
  }, []);

  // 保存历史记录
  const saveHistory = (newHistory: any[]) => {
    setHistory(newHistory);
    localStorage.setItem('apiTestHistory', JSON.stringify(newHistory));
  };

  // 处理发送请求
  const handleSendRequest = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);
      
      const requestData = {
        ...values,
        body: activeTab === 'body' ? requestBody : undefined,
      };
      
      const startTime = Date.now();
      const response = await testApi(requestData);
      const endTime = Date.now();
      
      const responseData = {
        status: response.status || 200,
        statusText: response.statusText || 'OK',
        headers: response.headers || {},
        data: response.data,
        time: endTime - startTime,
      };
      
      setResponse(responseData);
      
      // 添加到历史记录
      const historyItem = {
        id: Date.now().toString(),
        method: values.method,
        url: values.url,
        params: values.params,
        headers: values.headers,
        body: requestBody,
        response: responseData,
        time: new Date().toISOString(),
      };
      
      const newHistory = [historyItem, ...history].slice(0, 50); // 只保留最近50条记录
      saveHistory(newHistory);
      
      messageApi.success('请求发送成功');
    } catch (error) {
      console.error('发送请求出错:', error);
      messageApi.error('发送请求出错');
    } finally {
      setLoading(false);
    }
  };

  // 处理加载历史记录
  const handleLoadHistory = (item: any) => {
    form.setFieldsValue({
      method: item.method,
      url: item.url,
      params: item.params,
      headers: item.headers,
    });
    setRequestBody(item.body || '');
    setResponse(item.response);
  };

  // 处理清除历史记录
  const handleClearHistory = () => {
    setHistory([]);
    localStorage.removeItem('apiTestHistory');
    messageApi.success('历史记录已清除');
  };

  return (
    <Layout className={styles.apiTestContainer}>
      {contextHolder}
      <Layout>
        <Header className={styles.apiTestHeader}>
          <Form
            form={form}
            layout="inline"
            initialValues={{ method: 'GET', url: '' }}
            className={styles.apiTestForm}
          >
            <Form.Item name="method" rules={[{ required: true }]}>
              <Select style={{ width: 100 }}>
                <Option value="GET">GET</Option>
                <Option value="POST">POST</Option>
                <Option value="PUT">PUT</Option>
                <Option value="DELETE">DELETE</Option>
                <Option value="PATCH">PATCH</Option>
                <Option value="HEAD">HEAD</Option>
                <Option value="OPTIONS">OPTIONS</Option>
              </Select>
            </Form.Item>
            <Form.Item name="url" rules={[{ required: true }]} className={styles.apiTestUrl}>
              <Input placeholder="请输入请求URL" />
            </Form.Item>
            <Form.Item>
              <Button
                type="primary"
                icon={<SendOutlined />}
                onClick={handleSendRequest}
                loading={loading}
              >
                发送
              </Button>
            </Form.Item>
          </Form>
        </Header>
        <Content className={styles.apiTestContent}>
          <Spin spinning={loading}>
            <Tabs defaultActiveKey="params" onChange={setActiveTab}>
              <TabPane tab="参数" key="params">
                <Form
                  form={form}
                  layout="vertical"
                  className={styles.apiTestParamsForm}
                >
                  <Form.Item name="params" label="查询参数">
                    <Input.TextArea rows={4} placeholder="请输入查询参数，格式为 JSON 对象" />
                  </Form.Item>
                  <Form.Item name="headers" label="请求头">
                    <Input.TextArea rows={4} placeholder="请输入请求头，格式为 JSON 对象" />
                  </Form.Item>
                </Form>
              </TabPane>
              <TabPane tab="请求体" key="body">
                <MonacoEditor
                  language="json"
                  value={requestBody}
                  onChange={setRequestBody}
                  height="200px"
                />
              </TabPane>
            </Tabs>
            <div className={styles.apiTestResponse}>
              <div className={styles.apiTestResponseHeader}>
                <h3>响应</h3>
              </div>
              <ResponseViewer response={response} />
            </div>
          </Spin>
        </Content>
      </Layout>
      <Sider width={300} theme="light" className={styles.apiTestSider}>
        <div className={styles.apiTestHistoryHeader}>
          <h3>历史记录</h3>
          <Button
            type="text"
            icon={<DeleteOutlined />}
            onClick={handleClearHistory}
            disabled={history.length === 0}
          >
            清除
          </Button>
        </div>
        <RequestHistory history={history} onSelect={handleLoadHistory} />
      </Sider>
    </Layout>
  );
};

export default ApiTestPage;
