import React, { useState } from 'react';
import { Tabs, Card, Table, Tag, Button, Descriptions, Divider, message } from 'antd';
import { CopyOutlined, ApiOutlined } from '@ant-design/icons';
import MonacoEditor from '@/components/MonacoEditor';
import { testApi } from '@/service/api-test';
import styles from './index.less';

const { TabPane } = Tabs;

interface ApiDocProps {
  api: any;
}

const ApiDoc: React.FC<ApiDocProps> = ({ api }) => {
  const [activeTab, setActiveTab] = useState<string>('info');
  const [testing, setTesting] = useState<boolean>(false);
  const [testResult, setTestResult] = useState<any>(null);
  const [messageApi, contextHolder] = message.useMessage();

  // 处理复制API路径
  const handleCopyPath = () => {
    try {
      navigator.clipboard.writeText(api.path);
      messageApi.success('已复制API路径');
    } catch (error) {
      console.error('复制API路径出错:', error);
      messageApi.error('复制API路径出错');
    }
  };

  // 处理测试API
  const handleTestApi = async () => {
    try {
      setTesting(true);
      setActiveTab('test');
      
      const response = await testApi({
        method: api.method,
        url: api.path,
        params: {},
        headers: {}
      });
      
      setTestResult(response);
      messageApi.success('测试成功');
    } catch (error) {
      console.error('测试API出错:', error);
      setTestResult({
        error: error.message || '测试出错'
      });
      messageApi.error('测试API出错');
    } finally {
      setTesting(false);
    }
  };

  // 获取方法标签
  const getMethodTag = (method: string) => {
    switch (method) {
      case 'GET':
        return <Tag color="blue">{method}</Tag>;
      case 'POST':
        return <Tag color="green">{method}</Tag>;
      case 'PUT':
        return <Tag color="orange">{method}</Tag>;
      case 'DELETE':
        return <Tag color="red">{method}</Tag>;
      default:
        return <Tag>{method}</Tag>;
    }
  };

  // 参数列
  const paramColumns = [
    {
      title: '参数名',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (text: string) => <Tag>{text}</Tag>,
    },
    {
      title: '是否必须',
      dataIndex: 'required',
      key: 'required',
      render: (required: boolean) => (
        required ? <Tag color="red">必须</Tag> : <Tag color="green">可选</Tag>
      ),
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: '示例值',
      dataIndex: 'example',
      key: 'example',
    },
  ];

  // 响应列
  const responseColumns = [
    {
      title: '状态码',
      dataIndex: 'status',
      key: 'status',
      render: (text: number) => {
        if (text >= 200 && text < 300) {
          return <Tag color="success">{text}</Tag>;
        } else if (text >= 400) {
          return <Tag color="error">{text}</Tag>;
        } else {
          return <Tag color="warning">{text}</Tag>;
        }
      },
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: '示例',
      dataIndex: 'example',
      key: 'example',
      render: (text: string) => (
        text ? (
          <Button size="small" onClick={() => {}}>查看示例</Button>
        ) : null
      ),
    },
  ];

  return (
    <div className={styles.apiDoc}>
      {contextHolder}
      <div className={styles.apiDocHeader}>
        <div className={styles.apiDocTitle}>
          <ApiOutlined /> {api.name}
        </div>
        <div className={styles.apiDocPath}>
          {getMethodTag(api.method)} {api.path}
          <Button
            icon={<CopyOutlined />}
            size="small"
            onClick={handleCopyPath}
            className={styles.apiDocCopy}
          />
        </div>
        <div className={styles.apiDocActions}>
          <Button
            type="primary"
            onClick={handleTestApi}
            loading={testing}
          >
            测试API
          </Button>
        </div>
      </div>
      <div className={styles.apiDocDescription}>
        {api.description}
      </div>
      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        <TabPane tab="基本信息" key="info">
          <Card title="请求参数" className={styles.apiDocCard}>
            {api.parameters && api.parameters.length > 0 ? (
              <Table
                columns={paramColumns}
                dataSource={api.parameters}
                rowKey="name"
                pagination={false}
                size="small"
              />
            ) : (
              <div className={styles.apiDocEmpty}>无请求参数</div>
            )}
          </Card>
          <Card title="响应信息" className={styles.apiDocCard}>
            {api.responses && api.responses.length > 0 ? (
              <Table
                columns={responseColumns}
                dataSource={api.responses}
                rowKey="status"
                pagination={false}
                size="small"
              />
            ) : (
              <div className={styles.apiDocEmpty}>无响应信息</div>
            )}
          </Card>
        </TabPane>
        <TabPane tab="示例代码" key="example">
          <Card title="JavaScript" className={styles.apiDocCard}>
            <MonacoEditor
              language="javascript"
              value={`// 使用fetch请求
fetch('${api.path}', {
  method: '${api.method}',
  headers: {
    'Content-Type': 'application/json'
  },
  body: ${api.method !== 'GET' ? "JSON.stringify({\n  // 请求参数\n})" : 'undefined'}
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));`}
              options={{ readOnly: true }}
              height="200px"
            />
          </Card>
          <Card title="Python" className={styles.apiDocCard}>
            <MonacoEditor
              language="python"
              value={`# 使用requests库
import requests
import json

url = '${api.path}'
${api.method !== 'GET' ? `payload = {
    # 请求参数
}

response = requests.${api.method.toLowerCase()}(url, json=payload)` : `response = requests.get(url)`}
print(response.json())`}
              options={{ readOnly: true }}
              height="200px"
            />
          </Card>
        </TabPane>
        <TabPane tab="测试" key="test">
          {testResult ? (
            <div className={styles.apiDocTestResult}>
              <Descriptions title="测试结果" bordered>
                <Descriptions.Item label="状态码" span={3}>
                  {testResult.status}
                </Descriptions.Item>
                <Descriptions.Item label="响应时间" span={3}>
                  {testResult.time ? `${testResult.time}ms` : '未知'}
                </Descriptions.Item>
              </Descriptions>
              <Divider>响应数据</Divider>
              <MonacoEditor
                language="json"
                value={JSON.stringify(testResult.data, null, 2)}
                options={{ readOnly: true }}
                height="300px"
              />
            </div>
          ) : (
            <div className={styles.apiDocEmpty}>
              点击"测试API"按钮开始测试
            </div>
          )}
        </TabPane>
      </Tabs>
    </div>
  );
};

export default ApiDoc;
