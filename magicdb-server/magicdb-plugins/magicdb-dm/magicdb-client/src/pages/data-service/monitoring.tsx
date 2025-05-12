import React from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import ServiceMonitoring from '@/components/ServiceMonitoring';

const ServiceMonitoringPage: React.FC = () => {
  return (
    <PageContainer title="服务监控">
      <ServiceMonitoring />
    </PageContainer>
  );
};

export default ServiceMonitoringPage;
