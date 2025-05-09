import React, { useEffect, useState } from 'react';
import { Drawer, List, Button, Tag, Spin, Empty, message } from 'antd';
import { SyncOutlined, ClockCircleOutlined } from '@ant-design/icons';
import { getScriptVersions, switchScriptVersion } from '@/service/script';
import styles from './index.less';

interface ScriptVersionHistoryProps {
  scriptId: string;
  onClose: () => void;
  onSwitchVersion: (version: any) => void;
}

const ScriptVersionHistory: React.FC<ScriptVersionHistoryProps> = ({
  scriptId,
  onClose,
  onSwitchVersion,
}) => {
  const [versions, setVersions] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [switching, setSwitching] = useState<string | null>(null);
  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    fetchVersions();
  }, [scriptId]);

  const fetchVersions = async () => {
    try {
      setLoading(true);
      const response = await getScriptVersions(scriptId);
      if (response && response.success) {
        setVersions(response.data || []);
      } else {
        messageApi.error('获取版本历史失败');
      }
    } catch (error) {
      console.error('获取版本历史出错:', error);
      messageApi.error('获取版本历史出错');
    } finally {
      setLoading(false);
    }
  };

  const handleSwitchVersion = async (version: any) => {
    try {
      setSwitching(version.id);
      const response = await switchScriptVersion(scriptId, version.version);
      if (response && response.success) {
        messageApi.success('切换版本成功');
        onSwitchVersion(version);
      } else {
        messageApi.error('切换版本失败');
      }
    } catch (error) {
      console.error('切换版本出错:', error);
      messageApi.error('切换版本出错');
    } finally {
      setSwitching(null);
    }
  };

  const formatDate = (dateString: string) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  return (
    <Drawer
      title="版本历史"
      placement="right"
      onClose={onClose}
      open={true}
      width={400}
    >
      {contextHolder}
      <div className={styles.versionHistoryHeader}>
        <Button icon={<SyncOutlined />} onClick={fetchVersions} loading={loading}>
          刷新
        </Button>
      </div>
      <Spin spinning={loading}>
        {versions.length > 0 ? (
          <List
            className={styles.versionList}
            dataSource={versions}
            renderItem={(version) => (
              <List.Item
                key={version.id}
                actions={[
                  <Button
                    key="switch"
                    type={version.current ? 'primary' : 'default'}
                    size="small"
                    loading={switching === version.id}
                    onClick={() => handleSwitchVersion(version)}
                    disabled={version.current}
                  >
                    {version.current ? '当前版本' : '切换'}
                  </Button>,
                ]}
              >
                <List.Item.Meta
                  title={
                    <div className={styles.versionTitle}>
                      <span>版本 {version.version}</span>
                      {version.current && <Tag color="green">当前</Tag>}
                    </div>
                  }
                  description={
                    <div className={styles.versionDescription}>
                      <div>{version.description || '无描述'}</div>
                      <div className={styles.versionTime}>
                        <ClockCircleOutlined /> {formatDate(version.createTime)}
                      </div>
                      {version.creator && (
                        <div className={styles.versionCreator}>
                          创建者: {version.creator}
                        </div>
                      )}
                    </div>
                  }
                />
              </List.Item>
            )}
          />
        ) : (
          <Empty description="暂无版本历史" />
        )}
      </Spin>
    </Drawer>
  );
};

export default ScriptVersionHistory;
