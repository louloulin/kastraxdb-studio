import React, { useState, useEffect } from 'react';
import { Select, Spin, Empty, Input, Space, Cascader, message } from 'antd';
import { SearchOutlined, DatabaseOutlined, TableOutlined, ColumnOutlined } from '@ant-design/icons';
import { getAllDataSources, getDatabases, getTables, getColumns } from '@/service/data-source';
import styles from './index.less';

const { Option } = Select;

export interface DataSourceSelectorProps {
  value?: {
    dataSourceId?: number;
    databaseName?: string;
    tableName?: string;
    columnName?: string;
  };
  onChange?: (value: {
    dataSourceId?: number;
    databaseName?: string;
    tableName?: string;
    columnName?: string;
  }) => void;
  showColumn?: boolean;
  showTable?: boolean;
  style?: React.CSSProperties;
  className?: string;
}

interface CascaderOption {
  value: string;
  label: string | React.ReactNode;
  children?: CascaderOption[];
  isLeaf?: boolean;
  loading?: boolean;
  disabled?: boolean;
}

const DataSourceSelector: React.FC<DataSourceSelectorProps> = ({
  value = {},
  onChange,
  showColumn = true,
  showTable = true,
  style,
  className
}) => {
  const [dataSources, setDataSources] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [options, setOptions] = useState<CascaderOption[]>([]);
  const [selectedOptions, setSelectedOptions] = useState<string[]>([]);
  const [messageApi, contextHolder] = message.useMessage();

  // 加载数据源列表
  useEffect(() => {
    fetchDataSources();
  }, []);

  // 初始化选中值
  useEffect(() => {
    if (value.dataSourceId) {
      const selected: string[] = [`ds-${value.dataSourceId}`];
      
      if (value.databaseName) {
        selected.push(`db-${value.databaseName}`);
        
        if (showTable && value.tableName) {
          selected.push(`tb-${value.tableName}`);
          
          if (showColumn && value.columnName) {
            selected.push(`col-${value.columnName}`);
          }
        }
      }
      
      setSelectedOptions(selected);
    }
  }, [value, showTable, showColumn]);

  // 获取数据源列表
  const fetchDataSources = async () => {
    try {
      setLoading(true);
      const response = await getAllDataSources();
      
      if (response.success && response.data) {
        setDataSources(response.data);
        
        // 构建级联选择器选项
        const cascaderOptions = response.data.map(ds => ({
          value: `ds-${ds.id}`,
          label: (
            <Space>
              <DatabaseOutlined />
              <span>{ds.name}</span>
              <span className={styles.typeTag}>{ds.type}</span>
            </Space>
          ),
          isLeaf: false,
          dataSourceId: ds.id
        }));
        
        setOptions(cascaderOptions);
      } else {
        messageApi.error('获取数据源列表失败');
      }
    } catch (error) {
      console.error('Error fetching data sources:', error);
      messageApi.error('获取数据源列表出错');
    } finally {
      setLoading(false);
    }
  };

  // 加载子选项
  const loadData = async (selectedOptions: CascaderOption[]) => {
    const targetOption = selectedOptions[selectedOptions.length - 1];
    targetOption.loading = true;

    try {
      // 解析选项值
      const optionValue = targetOption.value as string;
      
      if (optionValue.startsWith('ds-')) {
        // 加载数据库列表
        const dataSourceId = parseInt(optionValue.substring(3));
        const response = await getDatabases(dataSourceId);
        
        if (response.success && response.data) {
          targetOption.children = response.data.map(db => ({
            value: `db-${db.name}`,
            label: db.name,
            isLeaf: !showTable,
            dataSourceId,
            databaseName: db.name
          }));
        } else {
          messageApi.error('获取数据库列表失败');
        }
      } else if (optionValue.startsWith('db-') && showTable) {
        // 加载表列表
        const dataSourceId = selectedOptions[0].dataSourceId as number;
        const databaseName = targetOption.databaseName as string;
        const response = await getTables(dataSourceId, databaseName);
        
        if (response.success && response.data) {
          targetOption.children = response.data.map(table => ({
            value: `tb-${table.name}`,
            label: (
              <Space>
                <TableOutlined />
                <span>{table.name}</span>
                <span className={styles.typeTag}>{table.type}</span>
              </Space>
            ),
            isLeaf: !showColumn,
            dataSourceId,
            databaseName,
            tableName: table.name
          }));
        } else {
          messageApi.error('获取表列表失败');
        }
      } else if (optionValue.startsWith('tb-') && showColumn) {
        // 加载列列表
        const dataSourceId = selectedOptions[0].dataSourceId as number;
        const databaseName = selectedOptions[1].databaseName as string;
        const tableName = targetOption.tableName as string;
        const response = await getColumns(dataSourceId, databaseName, tableName);
        
        if (response.success && response.data) {
          targetOption.children = response.data.map(column => ({
            value: `col-${column.name}`,
            label: (
              <Space>
                <ColumnOutlined />
                <span>{column.name}</span>
                <span className={styles.typeTag}>{column.type}</span>
              </Space>
            ),
            isLeaf: true,
            dataSourceId,
            databaseName,
            tableName,
            columnName: column.name
          }));
        } else {
          messageApi.error('获取列列表失败');
        }
      }
    } catch (error) {
      console.error('Error loading data:', error);
      messageApi.error('加载数据出错');
    } finally {
      targetOption.loading = false;
      setOptions([...options]);
    }
  };

  // 处理选择变更
  const handleChange = (selectedOptions: string[], selectedNodes: any) => {
    setSelectedOptions(selectedOptions);
    
    if (onChange) {
      const result: {
        dataSourceId?: number;
        databaseName?: string;
        tableName?: string;
        columnName?: string;
      } = {};
      
      // 解析选中值
      if (selectedOptions.length > 0) {
        const dataSourceOption = selectedOptions[0];
        if (dataSourceOption.startsWith('ds-')) {
          result.dataSourceId = parseInt(dataSourceOption.substring(3));
        }
        
        if (selectedOptions.length > 1) {
          const databaseOption = selectedOptions[1];
          if (databaseOption.startsWith('db-')) {
            result.databaseName = databaseOption.substring(3);
          }
          
          if (selectedOptions.length > 2 && showTable) {
            const tableOption = selectedOptions[2];
            if (tableOption.startsWith('tb-')) {
              result.tableName = tableOption.substring(3);
            }
            
            if (selectedOptions.length > 3 && showColumn) {
              const columnOption = selectedOptions[3];
              if (columnOption.startsWith('col-')) {
                result.columnName = columnOption.substring(4);
              }
            }
          }
        }
      }
      
      onChange(result);
    }
  };

  return (
    <div className={`${styles.dataSourceSelector} ${className}`} style={style}>
      {contextHolder}
      <Spin spinning={loading}>
        <Cascader
          options={options}
          loadData={loadData as any}
          onChange={handleChange as any}
          value={selectedOptions}
          placeholder="请选择数据源"
          showSearch={{
            filter: (inputValue, path) => {
              return path.some(option => {
                const label = option.label as string;
                return label.toLowerCase().indexOf(inputValue.toLowerCase()) > -1;
              });
            }
          }}
          className={styles.cascader}
        />
      </Spin>
    </div>
  );
};

export default DataSourceSelector;
