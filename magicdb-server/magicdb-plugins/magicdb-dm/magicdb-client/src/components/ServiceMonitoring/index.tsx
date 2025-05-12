import React, { useEffect, useState } from 'react';
import { Card, Table, Tabs, DatePicker, Button, Space, Select, Tooltip, Badge, message, Statistic, Row, Col, Modal } from 'antd';
import { ReloadOutlined, DeleteOutlined, ExclamationCircleOutlined, LineChartOutlined } from '@ant-design/icons';
import { Column } from '@ant-design/charts';
import moment from 'moment';
import { 
  getServiceStatistics, 
  getExecutionHistory, 
  getErrorStatistics, 
  clearExecutionHistory,
  ServiceStatistics,
  ExecutionRecord,
  ErrorStatistics
} from '@/services/service-monitoring';
import { getServiceList } from '@/services/data-service';
import styles from './index.less';

const { TabPane } = Tabs;
const { RangePicker } = DatePicker;
const { Option } = Select;
const { confirm } = Modal;

interface ServiceMonitoringProps {
  className?: string;
  style?: React.CSSProperties;
}

const ServiceMonitoring: React.FC<ServiceMonitoringProps> = ({ className, style }) => {
  const [activeTab, setActiveTab] = useState<string>('statistics');
  const [loading, setLoading] = useState<boolean>(false);
  const [statistics, setStatistics] = useState<ServiceStatistics[]>([]);
  const [history, setHistory] = useState<ExecutionRecord[]>([]);
  const [errors, setErrors] = useState<ErrorStatistics[]>([]);
  const [services, setServices] = useState<any[]>([]);
  const [selectedService, setSelectedService] = useState<string | undefined>(undefined);
  const [dateRange, setDateRange] = useState<[moment.Moment, moment.Moment] | null>(null);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  
  // 加载服务列表
  useEffect(() => {
    fetchServices();
  }, []);
  
  // 加载数据
  useEffect(() => {
    if (activeTab === 'statistics') {
      fetchStatistics();
    } else if (activeTab === 'history') {
      fetchHistory();
    } else if (activeTab === 'errors') {
      fetchErrors();
    }
  }, [activeTab, selectedService, dateRange]);
  
  // 获取服务列表
  const fetchServices = async () => {
    try {
      const response = await getServiceList();
      if (response && response.success) {
        setServices(response.data || []);
      }
    } catch (error) {
      console.error('Failed to fetch services:', error);
      message.error('获取服务列表失败');
    }
  };
  
  // 获取服务统计
  const fetchStatistics = async () => {
    setLoading(true);
    try {
      const params: any = {};
      
      if (selectedService) {
        params.serviceId = selectedService;
      }
      
      if (dateRange) {
        params.startTime = dateRange[0].format('YYYY-MM-DD HH:mm:ss');
        params.endTime = dateRange[1].format('YYYY-MM-DD HH:mm:ss');
      }
      
      const response = await getServiceStatistics(params);
      
      if (response && response.success) {
        setStatistics(response.data || []);
      } else {
        message.error('获取服务统计失败');
      }
    } catch (error) {
      console.error('Failed to fetch statistics:', error);
      message.error('获取服务统计失败');
    } finally {
      setLoading(false);
    }
  };
  
  // 获取执行历史
  const fetchHistory = async () => {
    setLoading(true);
    try {
      const params: any = {
        limit: pagination.pageSize,
        offset: (pagination.current - 1) * pagination.pageSize
      };
      
      if (selectedService) {
        params.serviceId = selectedService;
      }
      
      if (dateRange) {
        params.startTime = dateRange[0].format('YYYY-MM-DD HH:mm:ss');
        params.endTime = dateRange[1].format('YYYY-MM-DD HH:mm:ss');
      }
      
      const response = await getExecutionHistory(params);
      
      if (response && response.success) {
        setHistory(response.data || []);
        setPagination({
          ...pagination,
          total: response.total || response.data?.length || 0
        });
      } else {
        message.error('获取执行历史失败');
      }
    } catch (error) {
      console.error('Failed to fetch history:', error);
      message.error('获取执行历史失败');
    } finally {
      setLoading(false);
    }
  };
  
  // 获取错误统计
  const fetchErrors = async () => {
    setLoading(true);
    try {
      const params: any = {};
      
      if (selectedService) {
        params.serviceId = selectedService;
      }
      
      if (dateRange) {
        params.startTime = dateRange[0].format('YYYY-MM-DD HH:mm:ss');
        params.endTime = dateRange[1].format('YYYY-MM-DD HH:mm:ss');
      }
      
      const response = await getErrorStatistics(params);
      
      if (response && response.success) {
        setErrors(response.data || []);
      } else {
        message.error('获取错误统计失败');
      }
    } catch (error) {
      console.error('Failed to fetch errors:', error);
      message.error('获取错误统计失败');
    } finally {
      setLoading(false);
    }
  };
  
  // 清除历史记录
  const handleClearHistory = () => {
    confirm({
      title: '确定要清除历史记录吗？',
      icon: <ExclamationCircleOutlined />,
      content: selectedService 
        ? `这将清除服务 "${services.find(s => s.id === selectedService)?.name || selectedService}" 的所有历史记录。`
        : '这将清除所有服务的历史记录。',
      onOk: async () => {
        try {
          const params: any = {};
          
          if (selectedService) {
            params.serviceId = selectedService;
          }
          
          if (dateRange) {
            params.before = dateRange[1].format('YYYY-MM-DD HH:mm:ss');
          }
          
          const response = await clearExecutionHistory(params);
          
          if (response && response.success) {
            message.success(`成功清除 ${response.data} 条历史记录`);
            
            // 重新加载数据
            fetchStatistics();
            fetchHistory();
            fetchErrors();
          } else {
            message.error('清除历史记录失败');
          }
        } catch (error) {
          console.error('Failed to clear history:', error);
          message.error('清除历史记录失败');
        }
      }
    });
  };
  
  // 处理表格分页变化
  const handleTableChange = (pagination: any) => {
    setPagination(pagination);
  };
  
  // 处理刷新
  const handleRefresh = () => {
    if (activeTab === 'statistics') {
      fetchStatistics();
    } else if (activeTab === 'history') {
      fetchHistory();
    } else if (activeTab === 'errors') {
      fetchErrors();
    }
  };
  
  // 渲染工具栏
  const renderToolbar = () => {
    return (
      <div className={styles.toolbar}>
        <Space>
          <Select
            placeholder="选择服务"
            style={{ width: 200 }}
            allowClear
            value={selectedService}
            onChange={setSelectedService}
          >
            {services.map(service => (
              <Option key={service.id} value={service.id}>{service.name}</Option>
            ))}
          </Select>
          
          <RangePicker
            showTime
            format="YYYY-MM-DD HH:mm:ss"
            value={dateRange}
            onChange={value => setDateRange(value as [moment.Moment, moment.Moment])}
          />
          
          <Button 
            type="primary" 
            icon={<ReloadOutlined />} 
            onClick={handleRefresh}
            loading={loading}
          >
            刷新
          </Button>
          
          <Button 
            danger 
            icon={<DeleteOutlined />} 
            onClick={handleClearHistory}
            disabled={loading}
          >
            清除历史
          </Button>
        </Space>
      </div>
    );
  };
  
  // 渲染统计概览
  const renderStatisticsOverview = () => {
    // 计算总计
    const totalExecutions = statistics.reduce((sum, stat) => sum + stat.totalExecutions, 0);
    const successfulExecutions = statistics.reduce((sum, stat) => sum + stat.successfulExecutions, 0);
    const failedExecutions = statistics.reduce((sum, stat) => sum + stat.failedExecutions, 0);
    const averageTime = statistics.length > 0
      ? statistics.reduce((sum, stat) => sum + stat.averageExecutionTime * stat.totalExecutions, 0) / totalExecutions
      : 0;
    
    return (
      <Row gutter={16} className={styles.statisticsOverview}>
        <Col span={6}>
          <Card>
            <Statistic
              title="总执行次数"
              value={totalExecutions}
              prefix={<LineChartOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="成功次数"
              value={successfulExecutions}
              valueStyle={{ color: '#3f8600' }}
              prefix={<Badge status="success" />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="失败次数"
              value={failedExecutions}
              valueStyle={{ color: '#cf1322' }}
              prefix={<Badge status="error" />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="平均执行时间"
              value={averageTime.toFixed(2)}
              suffix="ms"
            />
          </Card>
        </Col>
      </Row>
    );
  };
  
  // 渲染统计图表
  const renderStatisticsChart = () => {
    // 准备图表数据
    const chartData = statistics.map(stat => ({
      service: stat.serviceName,
      type: '总次数',
      value: stat.totalExecutions
    })).concat(
      statistics.map(stat => ({
        service: stat.serviceName,
        type: '成功',
        value: stat.successfulExecutions
      }))
    ).concat(
      statistics.map(stat => ({
        service: stat.serviceName,
        type: '失败',
        value: stat.failedExecutions
      }))
    );
    
    return (
      <div className={styles.statisticsChart}>
        <Column
          data={chartData}
          isGroup={true}
          xField="service"
          yField="value"
          seriesField="type"
          label={{
            position: 'middle',
            layout: [
              { type: 'interval-adjust-position' },
              { type: 'interval-hide-overlap' },
              { type: 'adjust-color' }
            ]
          }}
          color={['#5B8FF9', '#5AD8A6', '#F6BD16']}
        />
      </div>
    );
  };
  
  // 渲染统计表格
  const renderStatisticsTable = () => {
    const columns = [
      {
        title: '服务名称',
        dataIndex: 'serviceName',
        key: 'serviceName',
        sorter: (a: ServiceStatistics, b: ServiceStatistics) => a.serviceName.localeCompare(b.serviceName)
      },
      {
        title: '总执行次数',
        dataIndex: 'totalExecutions',
        key: 'totalExecutions',
        sorter: (a: ServiceStatistics, b: ServiceStatistics) => a.totalExecutions - b.totalExecutions
      },
      {
        title: '成功次数',
        dataIndex: 'successfulExecutions',
        key: 'successfulExecutions',
        sorter: (a: ServiceStatistics, b: ServiceStatistics) => a.successfulExecutions - b.successfulExecutions,
        render: (text: number, record: ServiceStatistics) => (
          <span>
            {text} ({(record.totalExecutions > 0 ? (text / record.totalExecutions * 100) : 0).toFixed(2)}%)
          </span>
        )
      },
      {
        title: '失败次数',
        dataIndex: 'failedExecutions',
        key: 'failedExecutions',
        sorter: (a: ServiceStatistics, b: ServiceStatistics) => a.failedExecutions - b.failedExecutions,
        render: (text: number, record: ServiceStatistics) => (
          <span>
            {text} ({(record.totalExecutions > 0 ? (text / record.totalExecutions * 100) : 0).toFixed(2)}%)
          </span>
        )
      },
      {
        title: '平均执行时间',
        dataIndex: 'averageExecutionTime',
        key: 'averageExecutionTime',
        sorter: (a: ServiceStatistics, b: ServiceStatistics) => a.averageExecutionTime - b.averageExecutionTime,
        render: (text: number) => `${text.toFixed(2)} ms`
      },
      {
        title: '最长执行时间',
        dataIndex: 'maxExecutionTime',
        key: 'maxExecutionTime',
        sorter: (a: ServiceStatistics, b: ServiceStatistics) => a.maxExecutionTime - b.maxExecutionTime,
        render: (text: number) => `${text} ms`
      },
      {
        title: '最短执行时间',
        dataIndex: 'minExecutionTime',
        key: 'minExecutionTime',
        sorter: (a: ServiceStatistics, b: ServiceStatistics) => a.minExecutionTime - b.minExecutionTime,
        render: (text: number) => `${text} ms`
      },
      {
        title: '最后执行时间',
        dataIndex: 'lastExecutionTime',
        key: 'lastExecutionTime',
        sorter: (a: ServiceStatistics, b: ServiceStatistics) => 
          new Date(a.lastExecutionTime).getTime() - new Date(b.lastExecutionTime).getTime(),
        render: (text: string) => moment(text).format('YYYY-MM-DD HH:mm:ss')
      }
    ];
    
    return (
      <Table
        columns={columns}
        dataSource={statistics}
        rowKey="serviceId"
        loading={loading}
        pagination={false}
      />
    );
  };
  
  // 渲染历史表格
  const renderHistoryTable = () => {
    const columns = [
      {
        title: '服务名称',
        dataIndex: 'serviceName',
        key: 'serviceName',
        sorter: (a: ExecutionRecord, b: ExecutionRecord) => a.serviceName.localeCompare(b.serviceName)
      },
      {
        title: '执行时间',
        dataIndex: 'timestamp',
        key: 'timestamp',
        sorter: (a: ExecutionRecord, b: ExecutionRecord) => 
          new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime(),
        render: (text: string) => moment(text).format('YYYY-MM-DD HH:mm:ss')
      },
      {
        title: '状态',
        dataIndex: 'success',
        key: 'success',
        sorter: (a: ExecutionRecord, b: ExecutionRecord) => (a.success ? 1 : 0) - (b.success ? 1 : 0),
        render: (success: boolean) => (
          <Badge 
            status={success ? 'success' : 'error'} 
            text={success ? '成功' : '失败'} 
          />
        )
      },
      {
        title: '执行时长',
        dataIndex: 'executionTime',
        key: 'executionTime',
        sorter: (a: ExecutionRecord, b: ExecutionRecord) => a.executionTime - b.executionTime,
        render: (text: number) => `${text} ms`
      },
      {
        title: '错误信息',
        dataIndex: 'errorMessage',
        key: 'errorMessage',
        render: (text: string) => text ? (
          <Tooltip title={text}>
            <span className={styles.errorMessage}>{text.length > 50 ? `${text.substring(0, 50)}...` : text}</span>
          </Tooltip>
        ) : '-'
      }
    ];
    
    return (
      <Table
        columns={columns}
        dataSource={history}
        rowKey="id"
        loading={loading}
        pagination={pagination}
        onChange={handleTableChange}
      />
    );
  };
  
  // 渲染错误表格
  const renderErrorsTable = () => {
    const columns = [
      {
        title: '服务名称',
        dataIndex: 'serviceName',
        key: 'serviceName',
        sorter: (a: ErrorStatistics, b: ErrorStatistics) => a.serviceName.localeCompare(b.serviceName)
      },
      {
        title: '错误信息',
        dataIndex: 'errorMessage',
        key: 'errorMessage',
        render: (text: string) => (
          <Tooltip title={text}>
            <span className={styles.errorMessage}>{text.length > 50 ? `${text.substring(0, 50)}...` : text}</span>
          </Tooltip>
        )
      },
      {
        title: '出现次数',
        dataIndex: 'occurrences',
        key: 'occurrences',
        sorter: (a: ErrorStatistics, b: ErrorStatistics) => a.occurrences - b.occurrences
      },
      {
        title: '首次出现',
        dataIndex: 'firstOccurrence',
        key: 'firstOccurrence',
        sorter: (a: ErrorStatistics, b: ErrorStatistics) => 
          new Date(a.firstOccurrence).getTime() - new Date(b.firstOccurrence).getTime(),
        render: (text: string) => moment(text).format('YYYY-MM-DD HH:mm:ss')
      },
      {
        title: '最后出现',
        dataIndex: 'lastOccurrence',
        key: 'lastOccurrence',
        sorter: (a: ErrorStatistics, b: ErrorStatistics) => 
          new Date(a.lastOccurrence).getTime() - new Date(b.lastOccurrence).getTime(),
        render: (text: string) => moment(text).format('YYYY-MM-DD HH:mm:ss')
      }
    ];
    
    return (
      <Table
        columns={columns}
        dataSource={errors}
        rowKey={(record) => `${record.serviceId}-${record.errorMessage}`}
        loading={loading}
        pagination={false}
      />
    );
  };
  
  return (
    <div className={`${styles.serviceMonitoring} ${className}`} style={style}>
      <Card
        title="服务监控"
        extra={renderToolbar()}
      >
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane tab="服务统计" key="statistics">
            {renderStatisticsOverview()}
            {statistics.length > 0 && renderStatisticsChart()}
            {renderStatisticsTable()}
          </TabPane>
          
          <TabPane tab="执行历史" key="history">
            {renderHistoryTable()}
          </TabPane>
          
          <TabPane tab="错误统计" key="errors">
            {renderErrorsTable()}
          </TabPane>
        </Tabs>
      </Card>
    </div>
  );
};

export default ServiceMonitoring;
