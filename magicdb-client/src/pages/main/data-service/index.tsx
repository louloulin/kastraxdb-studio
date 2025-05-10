import React, { useState, useEffect } from 'react';
import { Layout, Spin, message } from 'antd';
import ServiceTree from './components/ServiceTree';
import ServiceEditor from './components/ServiceEditor';
import ServiceParams from './components/ServiceParams';
import { getServiceList, getServiceById } from '@/service/data-service';
import i18n from '@/i18n';
import styles from './index.less';

const { Sider, Content } = Layout;

interface ServiceProps {
  id: string;
  name: string;
  type: string;
  script: string;
  language: string;
  description: string;
  groupId: string;
  tags: string[];
  parameters: any[];
}

const DataServicePage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [serviceList, setServiceList] = useState<any[]>([]);
  const [selectedService, setSelectedService] = useState<ServiceProps | null>(null);
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

  const handleSelectService = async (serviceId: string) => {
    try {
      setLoading(true);
      const response = await getServiceById(serviceId);
      if (response && response.success) {
        setSelectedService(response.data);
      } else {
        messageApi.error(i18n('data-service.detail.failed'));
      }
    } catch (error) {
      console.error('Failed to fetch service detail:', error);
      messageApi.error(i18n('data-service.detail.error'));
    } finally {
      setLoading(false);
    }
  };

  const handleSaveService = async (_service: ServiceProps) => {
    // 保存数据服务的逻辑将在ServiceEditor组件中实现
    await fetchServiceList();
  };

  return (
    <Layout className={styles.serviceContainer}>
      {contextHolder}
      <Sider width={280} theme="light" className={styles.serviceSider}>
        <ServiceTree serviceList={serviceList} onSelectService={handleSelectService} onRefresh={fetchServiceList} />
      </Sider>
      <Content className={styles.serviceContent}>
        <Spin spinning={loading}>
          {selectedService ? (
            <ServiceEditor service={selectedService} onSave={handleSaveService} />
          ) : (
            <div className={styles.serviceEmpty}>{i18n('data-service.empty')}</div>
          )}
        </Spin>
      </Content>
      <Sider width={320} theme="light" className={styles.serviceParams}>
        <ServiceParams service={selectedService} onSave={handleSaveService} />
      </Sider>
    </Layout>
  );
};

export default DataServicePage;
