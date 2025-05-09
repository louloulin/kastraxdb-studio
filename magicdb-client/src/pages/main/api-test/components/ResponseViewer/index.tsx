import React, { useState } from 'react';
import { Tabs, Descriptions, Badge, Button, message } from 'antd';
import { CopyOutlined } from '@ant-design/icons';
import MonacoEditor from '@/components/MonacoEditor';
import styles from './index.less';

const { TabPane } = Tabs;

interface ResponseViewerProps {
  response: any;
}

const ResponseViewer: React.FC<ResponseViewerProps> = ({ response }) => {
  const [messageApi, contextHolder] = message.useMessage();

  if (!response) {
    return (
      <div className={styles.responseEmpty}>
        暂无响应数据
      </div>
    );
  }

  // 处理复制响应
  const handleCopyResponse = () => {
    try {
      const responseText = JSON.stringify(response.data, null, 2);
      navigator.clipboard.writeText(responseText);
      messageApi.success('已复制到剪贴板');
    } catch (error) {
      console.error('复制响应出错:', error);
      messageApi.error('复制响应出错');
    }
  };

  // 格式化响应头
  const formatHeaders = (headers: any) => {
    if (!headers) return {};
    
    // 如果headers已经是对象，直接返回
    if (typeof headers === 'object' && !Array.isArray(headers)) {
      return headers;
    }
    
    // 尝试解析JSON字符串
    if (typeof headers === 'string') {
      try {
        return JSON.parse(headers);
      } catch (error) {
        // 如果解析失败，返回空对象
        return {};
      }
    }
    
    return {};
  };

  // 获取状态标签
  const getStatusBadge = (status: number) => {
    if (status >= 200 && status < 300) {
      return <Badge status="success" text={`${status} ${response.statusText || ''}`} />;
    } else if (status >= 400) {
      return <Badge status="error" text={`${status} ${response.statusText || ''}`} />;
    } else {
      return <Badge status="warning" text={`${status} ${response.statusText || ''}`} />;
    }
  };

  const headers = formatHeaders(response.headers);

  return (
    <div className={styles.responseViewer}>
      {contextHolder}
      <div className={styles.responseHeader}>
        <div className={styles.responseStatus}>
          {getStatusBadge(response.status)}
          {response.time && <span className={styles.responseTime}>{response.time}ms</span>}
        </div>
        <Button
          icon={<CopyOutlined />}
          onClick={handleCopyResponse}
          size="small"
        >
          复制
        </Button>
      </div>
      <Tabs defaultActiveKey="body">
        <TabPane tab="响应体" key="body">
          <MonacoEditor
            language="json"
            value={JSON.stringify(response.data, null, 2)}
            options={{ readOnly: true }}
            height="300px"
          />
        </TabPane>
        <TabPane tab="响应头" key="headers">
          <Descriptions bordered size="small" column={1}>
            {Object.entries(headers).map(([key, value]) => (
              <Descriptions.Item key={key} label={key}>
                {String(value)}
              </Descriptions.Item>
            ))}
          </Descriptions>
        </TabPane>
      </Tabs>
    </div>
  );
};

export default ResponseViewer;
