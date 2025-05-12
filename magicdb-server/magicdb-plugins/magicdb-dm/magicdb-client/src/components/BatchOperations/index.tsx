import React, { useState } from 'react';
import { Button, Upload, message, Modal, Space, Tooltip, Checkbox, Table } from 'antd';
import { UploadOutlined, DownloadOutlined, DeleteOutlined, ExclamationCircleOutlined, PoweroffOutlined } from '@ant-design/icons';
import type { UploadProps } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { exportServices, importServices, batchDeleteServices, batchUpdateServiceStatus } from '@/services/data-service';
import styles from './index.less';

const { confirm } = Modal;

interface BatchOperationsProps {
  selectedRowKeys: React.Key[];
  onSelectionChange: (selectedRowKeys: React.Key[]) => void;
  onBatchOperationComplete: () => void;
  services: any[];
}

const BatchOperations: React.FC<BatchOperationsProps> = ({
  selectedRowKeys,
  onSelectionChange,
  onBatchOperationComplete,
  services,
}) => {
  const [importModalVisible, setImportModalVisible] = useState<boolean>(false);
  const [importedServices, setImportedServices] = useState<any[]>([]);
  const [importing, setImporting] = useState<boolean>(false);
  const [exporting, setExporting] = useState<boolean>(false);
  const [deleting, setDeleting] = useState<boolean>(false);
  const [updating, setUpdating] = useState<boolean>(false);

  // 处理导入
  const handleImport = () => {
    setImportModalVisible(true);
  };

  // 处理导出
  const handleExport = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要导出的服务');
      return;
    }

    setExporting(true);
    try {
      const response = await exportServices(selectedRowKeys as string[]);
      
      if (response && response.success) {
        // 创建下载链接
        const blob = new Blob([JSON.stringify(response.data, null, 2)], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'services.json';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
        
        message.success(`成功导出 ${selectedRowKeys.length} 个服务`);
      } else {
        message.error('导出服务失败');
      }
    } catch (error) {
      console.error('Failed to export services:', error);
      message.error('导出服务失败');
    } finally {
      setExporting(false);
    }
  };

  // 处理删除
  const handleDelete = () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的服务');
      return;
    }

    confirm({
      title: '确定要删除选中的服务吗？',
      icon: <ExclamationCircleOutlined />,
      content: `此操作将删除 ${selectedRowKeys.length} 个服务，删除后无法恢复。`,
      onOk: async () => {
        setDeleting(true);
        try {
          const response = await batchDeleteServices(selectedRowKeys as string[]);
          
          if (response && response.success) {
            message.success(`成功删除 ${response.data} 个服务`);
            onSelectionChange([]);
            onBatchOperationComplete();
          } else {
            message.error('删除服务失败');
          }
        } catch (error) {
          console.error('Failed to delete services:', error);
          message.error('删除服务失败');
        } finally {
          setDeleting(false);
        }
      },
    });
  };

  // 处理启用/禁用
  const handleToggleStatus = (enabled: boolean) => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要操作的服务');
      return;
    }

    confirm({
      title: `确定要${enabled ? '启用' : '禁用'}选中的服务吗？`,
      icon: <ExclamationCircleOutlined />,
      content: `此操作将${enabled ? '启用' : '禁用'} ${selectedRowKeys.length} 个服务。`,
      onOk: async () => {
        setUpdating(true);
        try {
          const response = await batchUpdateServiceStatus(selectedRowKeys as string[], enabled);
          
          if (response && response.success) {
            message.success(`成功${enabled ? '启用' : '禁用'} ${response.data} 个服务`);
            onBatchOperationComplete();
          } else {
            message.error(`${enabled ? '启用' : '禁用'}服务失败`);
          }
        } catch (error) {
          console.error(`Failed to ${enabled ? 'enable' : 'disable'} services:`, error);
          message.error(`${enabled ? '启用' : '禁用'}服务失败`);
        } finally {
          setUpdating(false);
        }
      },
    });
  };

  // 上传配置
  const uploadProps: UploadProps = {
    name: 'file',
    accept: '.json',
    showUploadList: false,
    beforeUpload: (file) => {
      const reader = new FileReader();
      
      reader.onload = (e) => {
        try {
          const content = e.target?.result as string;
          const services = JSON.parse(content);
          
          if (Array.isArray(services)) {
            setImportedServices(services);
          } else {
            message.error('导入文件格式不正确');
          }
        } catch (error) {
          console.error('Failed to parse import file:', error);
          message.error('导入文件解析失败');
        }
      };
      
      reader.readAsText(file);
      return false;
    },
  };

  // 确认导入
  const handleConfirmImport = async () => {
    if (importedServices.length === 0) {
      message.warning('没有可导入的服务');
      return;
    }

    setImporting(true);
    try {
      const response = await importServices(importedServices);
      
      if (response && response.success) {
        message.success(`成功导入 ${response.data} 个服务`);
        setImportModalVisible(false);
        setImportedServices([]);
        onBatchOperationComplete();
      } else {
        message.error('导入服务失败');
      }
    } catch (error) {
      console.error('Failed to import services:', error);
      message.error('导入服务失败');
    } finally {
      setImporting(false);
    }
  };

  // 导入表格列
  const importColumns: ColumnsType<any> = [
    {
      title: '服务名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '语言',
      dataIndex: 'language',
      key: 'language',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
  ];

  return (
    <div className={styles.batchOperations}>
      <Space>
        <Upload {...uploadProps}>
          <Button icon={<UploadOutlined />} onClick={handleImport}>
            导入
          </Button>
        </Upload>
        
        <Button
          icon={<DownloadOutlined />}
          onClick={handleExport}
          disabled={selectedRowKeys.length === 0}
          loading={exporting}
        >
          导出 ({selectedRowKeys.length})
        </Button>
        
        <Tooltip title="启用">
          <Button
            icon={<PoweroffOutlined />}
            onClick={() => handleToggleStatus(true)}
            disabled={selectedRowKeys.length === 0}
            loading={updating}
          >
            启用 ({selectedRowKeys.length})
          </Button>
        </Tooltip>
        
        <Tooltip title="禁用">
          <Button
            icon={<PoweroffOutlined />}
            onClick={() => handleToggleStatus(false)}
            disabled={selectedRowKeys.length === 0}
            loading={updating}
            danger
          >
            禁用 ({selectedRowKeys.length})
          </Button>
        </Tooltip>
        
        <Button
          icon={<DeleteOutlined />}
          onClick={handleDelete}
          disabled={selectedRowKeys.length === 0}
          loading={deleting}
          danger
        >
          删除 ({selectedRowKeys.length})
        </Button>
      </Space>
      
      <Modal
        title="导入服务"
        open={importModalVisible}
        onCancel={() => {
          setImportModalVisible(false);
          setImportedServices([]);
        }}
        onOk={handleConfirmImport}
        confirmLoading={importing}
        width={800}
      >
        <div className={styles.importModalContent}>
          {importedServices.length === 0 ? (
            <div className={styles.uploadTip}>
              <Upload {...uploadProps}>
                <Button icon={<UploadOutlined />}>选择文件</Button>
              </Upload>
              <p>请选择服务配置文件（JSON格式）</p>
            </div>
          ) : (
            <div>
              <div className={styles.importSummary}>
                <p>共 {importedServices.length} 个服务待导入</p>
              </div>
              <Table
                columns={importColumns}
                dataSource={importedServices}
                rowKey="name"
                pagination={{ pageSize: 5 }}
                size="small"
              />
            </div>
          )}
        </div>
      </Modal>
    </div>
  );
};

export default BatchOperations;
