import React, { useState, useEffect } from 'react';
import { Layout, Tree, Spin, Empty, message } from 'antd';
import { FolderOutlined, ApiOutlined } from '@ant-design/icons';
import ServiceTest from './components/ServiceTest';
import { getServiceList } from '@/service/data-service';
import i18n from '@/i18n';
import styles from './test.less';

const { Sider, Content } = Layout;
const { DirectoryTree } = Tree;

const DataServiceTestPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [serviceList, setServiceList] = useState<any[]>([]);
  const [selectedServiceId, setSelectedServiceId] = useState<string | null>(null);
  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    fetchServiceList();
  }, []);

  const fetchServiceList = async () => {
    try {
      setLoading(true);
      const response = await getServiceList();
      if (response && response.success) {
        setServiceList(response.data || []);
      } else {
        messageApi.error(i18n('data-service.list.failed'));
      }
    } catch (error) {
      console.error('Failed to fetch service list:', error);
      messageApi.error(i18n('data-service.list.error'));
    } finally {
      setLoading(false);
    }
  };

  const handleSelectService = (selectedKeys: React.Key[], info: any) => {
    const key = selectedKeys[0] as string;
    if (key && key.startsWith('service-')) {
      setSelectedServiceId(key.replace('service-', ''));
    }
  };

  const renderTreeData = () => {
    // Group services by group
    const groupMap: { [key: string]: any } = {};
    const rootServices: any[] = [];
    
    serviceList.forEach(service => {
      if (service.groupId) {
        if (!groupMap[service.groupId]) {
          groupMap[service.groupId] = {
            title: service.groupName || 'Unknown Group',
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

  return (
    <Layout className={styles.testContainer}>
      {contextHolder}
      <Sider width={280} theme="light" className={styles.testSider}>
        <div className={styles.testSiderHeader}>
          <h3>{i18n('data-service.test.services')}</h3>
        </div>
        <Spin spinning={loading}>
          {serviceList.length > 0 ? (
            <DirectoryTree
              defaultExpandAll
              onSelect={handleSelectService}
              treeData={renderTreeData()}
            />
          ) : (
            <Empty description={i18n('data-service.test.no-services')} />
          )}
        </Spin>
      </Sider>
      <Content className={styles.testContent}>
        {selectedServiceId ? (
          <ServiceTest serviceId={selectedServiceId} />
        ) : (
          <div className={styles.testEmpty}>
            <Empty description={i18n('data-service.test.select-service')} />
          </div>
        )}
      </Content>
    </Layout>
  );
};

export default DataServiceTestPage;
