import React, { useState, useEffect } from 'react';
import { Layout, Tree, Spin, Empty, Button, Tabs, message, Input, Space, Dropdown, Menu } from 'antd';
import { 
  FolderOutlined, 
  ApiOutlined, 
  ReloadOutlined, 
  DownloadOutlined,
  SearchOutlined,
  ExportOutlined
} from '@ant-design/icons';
import { getServiceList, getServiceById } from '@/service/data-service';
import { getServiceDoc, getServiceSwagger, exportServiceDoc } from '@/service/doc';
import ServiceDocContent from './components/ServiceDocContent';
import i18n from '@/i18n';
import styles from './doc.less';

const { Sider, Content } = Layout;
const { DirectoryTree } = Tree;
const { Search } = Input;
const { TabPane } = Tabs;

const DataServiceDocPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [serviceList, setServiceList] = useState<any[]>([]);
  const [selectedService, setSelectedService] = useState<any>(null);
  const [serviceDoc, setServiceDoc] = useState<any>(null);
  const [swaggerSpec, setSwaggerSpec] = useState<string>('');
  const [searchValue, setSearchValue] = useState<string>('');
  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    fetchServiceList();
  }, []);

  useEffect(() => {
    if (selectedService) {
      fetchServiceDoc(selectedService.id);
    }
  }, [selectedService]);

  const fetchServiceList = async () => {
    try {
      setLoading(true);
      const response = await getServiceList();
      if (response && response.success) {
        setServiceList(response.data || []);
      } else {
        messageApi.error(i18n('data-service.doc.list-failed'));
      }
    } catch (error) {
      console.error('Failed to fetch service list:', error);
      messageApi.error(i18n('data-service.doc.list-error'));
    } finally {
      setLoading(false);
    }
  };

  const fetchServiceDoc = async (serviceId: string) => {
    try {
      setLoading(true);
      
      // 获取服务文档
      const docResponse = await getServiceDoc(serviceId);
      if (docResponse && docResponse.success) {
        setServiceDoc(docResponse.data);
      } else {
        messageApi.error(i18n('data-service.doc.load-failed'));
      }
      
      // 获取 Swagger 规范
      const swaggerResponse = await getServiceSwagger(serviceId);
      if (swaggerResponse && swaggerResponse.success) {
        setSwaggerSpec(swaggerResponse.data);
      }
    } catch (error) {
      console.error('Failed to fetch service doc:', error);
      messageApi.error(i18n('data-service.doc.load-error'));
    } finally {
      setLoading(false);
    }
  };

  const handleSelectService = (selectedKeys: React.Key[], info: any) => {
    const key = selectedKeys[0] as string;
    if (key && key.startsWith('service-')) {
      const serviceId = key.replace('service-', '');
      const service = serviceList.find(item => item.id === serviceId);
      if (service) {
        setSelectedService(service);
      }
    }
  };

  const handleExportDoc = async (format: string) => {
    if (!selectedService) return;
    
    try {
      setLoading(true);
      
      // 调用导出 API
      const response = await exportServiceDoc(selectedService.id, format);
      
      // 创建下载链接
      const blob = new Blob([response], { 
        type: format === 'pdf' 
          ? 'application/pdf' 
          : format === 'html' 
            ? 'text/html' 
            : 'text/markdown' 
      });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${selectedService.name}_doc.${format}`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
      
      messageApi.success(i18n('data-service.doc.export-success'));
    } catch (error) {
      console.error('Failed to export doc:', error);
      messageApi.error(i18n('data-service.doc.export-error'));
    } finally {
      setLoading(false);
    }
  };

  const renderTreeData = () => {
    // 过滤服务列表
    const filteredServices = serviceList.filter(service => 
      service.name.toLowerCase().includes(searchValue.toLowerCase()) ||
      (service.description && service.description.toLowerCase().includes(searchValue.toLowerCase()))
    );
    
    // 按分组组织服务
    const groupMap: { [key: string]: any } = {};
    const rootServices: any[] = [];
    
    filteredServices.forEach(service => {
      if (service.groupId) {
        if (!groupMap[service.groupId]) {
          const group = serviceList.find(item => item.id === service.groupId);
          groupMap[service.groupId] = {
            title: group ? group.name : 'Unknown Group',
            key: `group-${service.groupId}`,
            icon: <FolderOutlined />,
            children: []
          };
        }
        
        groupMap[service.groupId].children.push({
          title: service.name,
          key: `service-${service.id}`,
          icon: <ApiOutlined />,
          isLeaf: true
        });
      } else {
        rootServices.push({
          title: service.name,
          key: `service-${service.id}`,
          icon: <ApiOutlined />,
          isLeaf: true
        });
      }
    });
    
    return [...Object.values(groupMap), ...rootServices];
  };

  const exportMenu = (
    <Menu>
      <Menu.Item key="markdown" onClick={() => handleExportDoc('markdown')}>
        Markdown (.md)
      </Menu.Item>
      <Menu.Item key="html" onClick={() => handleExportDoc('html')}>
        HTML (.html)
      </Menu.Item>
      <Menu.Item key="pdf" onClick={() => handleExportDoc('pdf')}>
        PDF (.pdf)
      </Menu.Item>
    </Menu>
  );

  return (
    <Layout className={styles.docContainer}>
      {contextHolder}
      <Sider width={280} theme="light" className={styles.docSider}>
        <div className={styles.docSiderHeader}>
          <h3>{i18n('data-service.doc.title')}</h3>
          <Space>
            <Button
              icon={<ReloadOutlined />}
              size="small"
              onClick={fetchServiceList}
            />
          </Space>
        </div>
        <div className={styles.docSiderSearch}>
          <Search
            placeholder={i18n('data-service.doc.search')}
            value={searchValue}
            onChange={e => setSearchValue(e.target.value)}
            allowClear
          />
        </div>
        <Spin spinning={loading}>
          {serviceList.length > 0 ? (
            <DirectoryTree
              defaultExpandAll
              onSelect={handleSelectService}
              treeData={renderTreeData()}
              className={styles.docTree}
            />
          ) : (
            <Empty description={i18n('data-service.doc.no-services')} />
          )}
        </Spin>
      </Sider>
      <Content className={styles.docContent}>
        {selectedService ? (
          <div className={styles.docContentWrapper}>
            <div className={styles.docHeader}>
              <h2>{selectedService.name}</h2>
              <Space>
                <Dropdown overlay={exportMenu} placement="bottomRight">
                  <Button icon={<ExportOutlined />}>
                    {i18n('data-service.doc.export')}
                  </Button>
                </Dropdown>
              </Space>
            </div>
            <Spin spinning={loading}>
              <ServiceDocContent 
                service={selectedService} 
                serviceDoc={serviceDoc} 
                swaggerSpec={swaggerSpec} 
              />
            </Spin>
          </div>
        ) : (
          <div className={styles.docEmpty}>
            <Empty description={i18n('data-service.doc.select-service')} />
          </div>
        )}
      </Content>
    </Layout>
  );
};

export default DataServiceDocPage;
