import React, { useState, useEffect } from 'react';
import { Modal, Table, Button, Tag, Tooltip, message } from 'antd';
import { HistoryOutlined, RollbackOutlined, EyeOutlined } from '@ant-design/icons';
import { getServiceHistory, restoreServiceVersion } from '@/service/data-service';
import i18n from '@/i18n';
import styles from './index.less';

interface ServiceHistoryProps {
  serviceId: string;
  visible: boolean;
  onClose: () => void;
  onRestore: (service: any) => void;
}

const ServiceHistory: React.FC<ServiceHistoryProps> = ({ serviceId, visible, onClose, onRestore }) => {
  const [loading, setLoading] = useState<boolean>(false);
  const [history, setHistory] = useState<any[]>([]);
  const [messageApi, contextHolder] = message.useMessage();
  const [previewVisible, setPreviewVisible] = useState<boolean>(false);
  const [previewData, setPreviewData] = useState<any>(null);

  useEffect(() => {
    if (visible && serviceId) {
      fetchHistory();
    }
  }, [visible, serviceId]);

  const fetchHistory = async () => {
    if (!serviceId) return;
    
    setLoading(true);
    try {
      const response = await getServiceHistory(serviceId);
      if (response && response.success) {
        setHistory(response.data || []);
      } else {
        messageApi.error(i18n('data-service.history.failed'));
      }
    } catch (error) {
      console.error('Error fetching service history:', error);
      messageApi.error(i18n('data-service.history.error'));
    } finally {
      setLoading(false);
    }
  };

  const handleRestore = async (versionId: string) => {
    setLoading(true);
    try {
      const response = await restoreServiceVersion(serviceId, versionId);
      if (response && response.success) {
        messageApi.success(i18n('data-service.history.restore.success'));
        onRestore(response.data);
        onClose();
      } else {
        messageApi.error(i18n('data-service.history.restore.failed'));
      }
    } catch (error) {
      console.error('Error restoring service version:', error);
      messageApi.error(i18n('data-service.history.restore.error'));
    } finally {
      setLoading(false);
    }
  };

  const handlePreview = (record: any) => {
    setPreviewData(record);
    setPreviewVisible(true);
  };

  const columns = [
    {
      title: i18n('data-service.history.version'),
      dataIndex: 'version',
      key: 'version',
      render: (text: string) => <Tag color="blue">{text}</Tag>,
    },
    {
      title: i18n('data-service.history.date'),
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (text: string) => new Date(text).toLocaleString(),
    },
    {
      title: i18n('data-service.history.user'),
      dataIndex: 'createdBy',
      key: 'createdBy',
    },
    {
      title: i18n('data-service.history.comment'),
      dataIndex: 'comment',
      key: 'comment',
      ellipsis: true,
    },
    {
      title: i18n('data-service.history.actions'),
      key: 'actions',
      render: (_: any, record: any) => (
        <div className={styles.serviceHistoryActions}>
          <Tooltip title={i18n('data-service.history.preview')}>
            <Button 
              type="text" 
              icon={<EyeOutlined />} 
              onClick={() => handlePreview(record)} 
            />
          </Tooltip>
          <Tooltip title={i18n('data-service.history.restore')}>
            <Button 
              type="text" 
              icon={<RollbackOutlined />} 
              onClick={() => handleRestore(record.id)} 
            />
          </Tooltip>
        </div>
      ),
    },
  ];

  return (
    <>
      {contextHolder}
      <Modal
        title={
          <div className={styles.serviceHistoryTitle}>
            <HistoryOutlined /> {i18n('data-service.history.title')}
          </div>
        }
        open={visible}
        onCancel={onClose}
        width={800}
        footer={[
          <Button key="close" onClick={onClose}>
            {i18n('data-service.history.close')}
          </Button>,
        ]}
      >
        <Table
          columns={columns}
          dataSource={history}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Modal>

      <Modal
        title={i18n('data-service.history.preview.title')}
        open={previewVisible}
        onCancel={() => setPreviewVisible(false)}
        width={700}
        footer={[
          <Button key="close" onClick={() => setPreviewVisible(false)}>
            {i18n('data-service.history.close')}
          </Button>,
          <Button
            key="restore"
            type="primary"
            onClick={() => {
              setPreviewVisible(false);
              handleRestore(previewData?.id);
            }}
          >
            {i18n('data-service.history.restore')}
          </Button>,
        ]}
      >
        {previewData && (
          <div className={styles.serviceHistoryPreview}>
            <div className={styles.serviceHistoryPreviewItem}>
              <div className={styles.serviceHistoryPreviewLabel}>{i18n('data-service.name')}:</div>
              <div className={styles.serviceHistoryPreviewValue}>{previewData.name}</div>
            </div>
            <div className={styles.serviceHistoryPreviewItem}>
              <div className={styles.serviceHistoryPreviewLabel}>{i18n('data-service.description')}:</div>
              <div className={styles.serviceHistoryPreviewValue}>{previewData.description}</div>
            </div>
            <div className={styles.serviceHistoryPreviewItem}>
              <div className={styles.serviceHistoryPreviewLabel}>{i18n('data-service.type')}:</div>
              <div className={styles.serviceHistoryPreviewValue}>{previewData.type}</div>
            </div>
            <div className={styles.serviceHistoryPreviewItem}>
              <div className={styles.serviceHistoryPreviewLabel}>{i18n('data-service.script')}:</div>
              <pre className={styles.serviceHistoryPreviewCode}>{previewData.script}</pre>
            </div>
          </div>
        )}
      </Modal>
    </>
  );
};

export default ServiceHistory;
