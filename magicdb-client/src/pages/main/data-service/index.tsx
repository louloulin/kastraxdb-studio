import React, { useState, useEffect } from 'react';
import { Layout, Spin, message, Button } from 'antd';
import ServiceTree from './components/ServiceTree';
import ServiceEditor from './components/ServiceEditor';
import ServiceParams from './components/ServiceParams';
import { getServiceList, getServiceById } from '@/service/data-service';
import { checkApiStatus } from '@/service/misc';
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
  const [retryCount, setRetryCount] = useState<number>(0);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchServiceList();
  }, [retryCount]);


  // 检查API服务状态
  const checkApiService = async () => {
    try {
      const response = await checkApiStatus();
      if (!response.success) {
        setError(response.message);
        messageApi.error(response.message);
      }
      return response.success;
    } catch (error) {
      console.error('API服务检查失败:', error);
      const errorMsg = '无法连接到API服务';
      setError(errorMsg);
      messageApi.error(errorMsg);
      return false;
    }
  };

  const fetchServiceList = async () => {
    try {
      setLoading(true);
      setError(null);
      console.log('Fetching service list, attempt:', retryCount + 1);
      
      // 先检查API服务状态
      const apiAvailable = await checkApiService();
      if (!apiAvailable) {
        return;
      }
      
      const response = await getServiceList();
      if (response && response.success) {
        setServiceList(response.data || []);
        setRetryCount(0); // 重置重试计数
      } else {
        const errorMsg = i18n('data-service.list.failed');
        setError(errorMsg);
        messageApi.error(errorMsg);
        
        // 如果失败，5秒后自动重试，最多重试3次
        if (retryCount < 3) {
          setTimeout(() => {
            setRetryCount(prev => prev + 1);
          }, 5000);
        }
      }
    } catch (error) {
      console.error('Failed to fetch service list:', error);
      const errorMsg = i18n('data-service.list.error');
      setError(errorMsg);
      messageApi.error(errorMsg);
      
      // 如果失败，5秒后自动重试，最多重试3次
      if (retryCount < 3) {
        setTimeout(() => {
          setRetryCount(prev => prev + 1);
        }, 5000);
      }
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
            <div className={styles.serviceEmpty}>
              <p>{i18n('data-service.empty')}</p>
              {error && (
                <Button 
                  type="primary" 
                  onClick={() => setRetryCount(prev => prev + 1)} 
                  style={{ marginTop: 16 }}
                >
                  {i18n('data-service.retry')}
                </Button>
              )}
            </div>
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
