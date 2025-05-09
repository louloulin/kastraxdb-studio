import React from 'react';
import { List, Tag, Tooltip, Empty } from 'antd';
import { 
  CheckCircleOutlined, 
  CloseCircleOutlined, 
  ClockCircleOutlined 
} from '@ant-design/icons';
import styles from './index.less';

interface RequestHistoryProps {
  history: any[];
  onSelect: (item: any) => void;
}

const RequestHistory: React.FC<RequestHistoryProps> = ({ history, onSelect }) => {
  // 格式化时间
  const formatTime = (timeString: string) => {
    if (!timeString) return '';
    const date = new Date(timeString);
    return date.toLocaleString();
  };

  // 获取状态标签
  const getStatusTag = (status: number) => {
    if (status >= 200 && status < 300) {
      return <Tag color="success" icon={<CheckCircleOutlined />}>{status}</Tag>;
    } else if (status >= 400) {
      return <Tag color="error" icon={<CloseCircleOutlined />}>{status}</Tag>;
    } else {
      return <Tag color="warning">{status}</Tag>;
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

  return (
    <div className={styles.requestHistory}>
      {history.length > 0 ? (
        <List
          dataSource={history}
          renderItem={(item) => (
            <List.Item
              key={item.id}
              className={styles.requestHistoryItem}
              onClick={() => onSelect(item)}
            >
              <div className={styles.requestHistoryItemContent}>
                <div className={styles.requestHistoryItemHeader}>
                  {getMethodTag(item.method)}
                  {item.response && getStatusTag(item.response.status)}
                </div>
                <div className={styles.requestHistoryItemUrl}>
                  <Tooltip title={item.url}>
                    {item.url}
                  </Tooltip>
                </div>
                <div className={styles.requestHistoryItemTime}>
                  <ClockCircleOutlined /> {formatTime(item.time)}
                </div>
              </div>
            </List.Item>
          )}
        />
      ) : (
        <Empty description="暂无历史记录" />
      )}
    </div>
  );
};

export default RequestHistory;
