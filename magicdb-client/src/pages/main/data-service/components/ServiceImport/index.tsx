import React, { useState } from 'react';
import { Modal, Button, Upload, message, Spin, Alert } from 'antd';
import { ImportOutlined, InboxOutlined } from '@ant-design/icons';
import { importService } from '@/service/data-service';
import i18n from '@/i18n';
import styles from './index.less';

const { Dragger } = Upload;

interface ServiceImportProps {
  visible: boolean;
  onClose: () => void;
  onImport: (service: any) => void;
}

const ServiceImport: React.FC<ServiceImportProps> = ({ visible, onClose, onImport }) => {
  const [loading, setLoading] = useState<boolean>(false);
  const [fileList, setFileList] = useState<any[]>([]);
  const [importData, setImportData] = useState<any>(null);
  const [messageApi, contextHolder] = message.useMessage();

  const handleImport = async () => {
    if (!importData) {
      messageApi.warning(i18n('data-service.import.no-file'));
      return;
    }
    
    setLoading(true);
    try {
      const response = await importService(importData);
      
      if (response && response.success) {
        messageApi.success(i18n('data-service.import.success'));
        onImport(response.data);
        onClose();
      } else {
        messageApi.error(i18n('data-service.import.failed'));
      }
    } catch (error) {
      console.error('Error importing service:', error);
      messageApi.error(i18n('data-service.import.error'));
    } finally {
      setLoading(false);
    }
  };

  const handleFileChange = (info: any) => {
    const { fileList } = info;
    setFileList(fileList);
    
    const file = fileList[0]?.originFileObj;
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          const content = e.target?.result as string;
          const data = JSON.parse(content);
          setImportData(data);
        } catch (error) {
          console.error('Error parsing JSON:', error);
          messageApi.error(i18n('data-service.import.invalid-json'));
          setImportData(null);
        }
      };
      reader.readAsText(file);
    } else {
      setImportData(null);
    }
  };

  const uploadProps = {
    name: 'file',
    multiple: false,
    fileList,
    beforeUpload: (file: File) => {
      const isJSON = file.type === 'application/json' || file.name.endsWith('.json');
      if (!isJSON) {
        messageApi.error(i18n('data-service.import.not-json'));
      }
      return isJSON || Upload.LIST_IGNORE;
    },
    customRequest: ({ onSuccess }: any) => {
      setTimeout(() => {
        onSuccess('ok');
      }, 0);
    },
    onChange: handleFileChange,
  };

  return (
    <>
      {contextHolder}
      <Modal
        title={
          <div className={styles.serviceImportTitle}>
            <ImportOutlined /> {i18n('data-service.import')}
          </div>
        }
        open={visible}
        onCancel={onClose}
        footer={[
          <Button key="cancel" onClick={onClose}>
            {i18n('data-service.cancel')}
          </Button>,
          <Button key="import" type="primary" onClick={handleImport} loading={loading} disabled={!importData}>
            {i18n('data-service.import.confirm')}
          </Button>,
        ]}
      >
        <Spin spinning={loading}>
          <div className={styles.serviceImportContent}>
            <Alert
              message={i18n('data-service.import.warning')}
              description={i18n('data-service.import.warning.description')}
              type="warning"
              showIcon
              className={styles.serviceImportWarning}
            />
            
            <Dragger {...uploadProps} className={styles.serviceImportUpload}>
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">{i18n('data-service.import.upload')}</p>
              <p className="ant-upload-hint">{i18n('data-service.import.upload.hint')}</p>
            </Dragger>
            
            {importData && (
              <div className={styles.serviceImportPreview}>
                <h4>{i18n('data-service.import.preview')}:</h4>
                <div className={styles.serviceImportPreviewItem}>
                  <span className={styles.serviceImportPreviewLabel}>{i18n('data-service.name')}:</span>
                  <span>{importData.name}</span>
                </div>
                {importData.description && (
                  <div className={styles.serviceImportPreviewItem}>
                    <span className={styles.serviceImportPreviewLabel}>{i18n('data-service.description')}:</span>
                    <span>{importData.description}</span>
                  </div>
                )}
                <div className={styles.serviceImportPreviewItem}>
                  <span className={styles.serviceImportPreviewLabel}>{i18n('data-service.type')}:</span>
                  <span>{importData.type}</span>
                </div>
              </div>
            )}
          </div>
        </Spin>
      </Modal>
    </>
  );
};

export default ServiceImport;
