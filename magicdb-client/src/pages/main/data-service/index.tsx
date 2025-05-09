import React, { useState, useEffect } from 'react';
import { Layout, Spin, message } from 'antd';
import ServiceTree from './components/ServiceTree';
import ServiceEditor from './components/ServiceEditor';
import ServiceParams from './components/ServiceParams';
import { useModel } from 'umi';
import { getServiceList, getServiceById } from '@/service/data-service';
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
  const { initialState } = useModel('@@initialState');

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
        messageApi.error('获取数据服务列表失败');
      }
    } catch (error) {
      console.error('获取数据服务列表出错:', error);
      messageApi.error('获取数据服务列表出错');
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
        messageApi.error('获取数据服务详情失败');
      }
    } catch (error) {
      console.error('获取数据服务详情出错:', error);
      messageApi.error('获取数据服务详情出错');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveService = async (service: ServiceProps) => {
    // 保存数据服务的逻辑将在ServiceEditor组件中实现
    await fetchServiceList();
  };

  return (
    <Layout className={styles.serviceContainer}>
      {contextHolder}
      <Sider width={280} theme="light" className={styles.serviceSider}>
        <ServiceTree 
          serviceList={serviceList} 
          onSelectService={handleSelectService} 
          onRefresh={fetchServiceList}
        />
      </Sider>
      <Content className={styles.serviceContent}>
        <Spin spinning={loading}>
          {selectedService ? (
            <ServiceEditor 
              service={selectedService} 
              onSave={handleSaveService} 
            />
          ) : (
            <div className={styles.serviceEmpty}>
              请选择或创建一个数据服务
            </div>
          )}
        </Spin>
      </Content>
      <Sider width={320} theme="light" className={styles.serviceParams}>
        <ServiceParams 
          service={selectedService} 
          onSave={handleSaveService}
        />
      </Sider>
    </Layout>
  );
};

export default DataServicePage;
