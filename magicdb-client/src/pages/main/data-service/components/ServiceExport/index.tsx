import React, { useState } from 'react';
import { Modal, Button, Checkbox, message, Spin } from 'antd';
import { ExportOutlined } from '@ant-design/icons';
import { exportService } from '@/service/data-service';
import i18n from '@/i18n';
import styles from './index.less';

interface ServiceExportProps {
  serviceId: string;
  visible: boolean;
  onClose: () => void;
}

const ServiceExport: React.FC<ServiceExportProps> = ({ serviceId, visible, onClose }) => {
  const [loading, setLoading] = useState<boolean>(false);
  const [options, setOptions] = useState<string[]>(['script', 'parameters', 'tags']);
  const [messageApi, contextHolder] = message.useMessage();

  const handleExport = async () => {
    if (!serviceId) return;
    
    setLoading(true);
    try {
      const response = await exportService(serviceId, { options });
      
      if (response && response.success) {
        // 创建一个 Blob 对象
        const blob = new Blob([JSON.stringify(response.data, null, 2)], { type: 'application/json' });
        
        // 创建一个下载链接
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `service-${serviceId}.json`;
        
        // 触发下载
        document.body.appendChild(link);
        link.click();
        
        // 清理
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
        
        messageApi.success(i18n('data-service.export.success'));
        onClose();
      } else {
        messageApi.error(i18n('data-service.export.failed'));
      }
    } catch (error) {
      console.error('Error exporting service:', error);
      messageApi.error(i18n('data-service.export.error'));
    } finally {
      setLoading(false);
    }
  };

  const handleOptionChange = (checkedValues: string[]) => {
    setOptions(checkedValues);
  };

  return (
    <>
      {contextHolder}
      <Modal
        title={
          <div className={styles.serviceExportTitle}>
            <ExportOutlined /> {i18n('data-service.export')}
          </div>
        }
        open={visible}
        onCancel={onClose}
        footer={[
          <Button key="cancel" onClick={onClose}>
            {i18n('data-service.cancel')}
          </Button>,
          <Button key="export" type="primary" onClick={handleExport} loading={loading}>
            {i18n('data-service.export.confirm')}
          </Button>,
        ]}
      >
        <Spin spinning={loading}>
          <div className={styles.serviceExportContent}>
            <p className={styles.serviceExportDescription}>
              {i18n('data-service.export.description')}
            </p>
            
            <div className={styles.serviceExportOptions}>
              <p>{i18n('data-service.export.options')}:</p>
              <Checkbox.Group value={options} onChange={handleOptionChange}>
                <div className={styles.serviceExportOption}>
                  <Checkbox value="script">{i18n('data-service.script')}</Checkbox>
                  <span className={styles.serviceExportOptionDesc}>
                    {i18n('data-service.export.script.description')}
                  </span>
                </div>
                <div className={styles.serviceExportOption}>
                  <Checkbox value="parameters">{i18n('data-service.parameters')}</Checkbox>
                  <span className={styles.serviceExportOptionDesc}>
                    {i18n('data-service.export.parameters.description')}
                  </span>
                </div>
                <div className={styles.serviceExportOption}>
                  <Checkbox value="tags">{i18n('data-service.tags')}</Checkbox>
                  <span className={styles.serviceExportOptionDesc}>
                    {i18n('data-service.export.tags.description')}
                  </span>
                </div>
              </Checkbox.Group>
            </div>
          </div>
        </Spin>
      </Modal>
    </>
  );
};

export default ServiceExport;
