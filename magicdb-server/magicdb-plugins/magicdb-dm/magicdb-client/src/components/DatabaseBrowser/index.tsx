import React, { useState, useEffect } from 'react';
import { Tree, Input, Spin, Empty, Tooltip, Button, message } from 'antd';
import { 
  DatabaseOutlined, 
  TableOutlined, 
  ColumnOutlined, 
  KeyOutlined,
  SearchOutlined,
  ReloadOutlined,
  CopyOutlined
} from '@ant-design/icons';
import { getAllDataSources, getDatabases, getTables, getColumns } from '@/service/data-source';
import styles from './index.less';

const { Search } = Input;

export interface DatabaseBrowserProps {
  onSelect?: (node: any) => void;
  onDragStart?: (node: any, event: React.DragEvent) => void;
  style?: React.CSSProperties;
  className?: string;
}

interface TreeNode {
  key: string;
  title: string | React.ReactNode;
  icon?: React.ReactNode;
  children?: TreeNode[];
  isLeaf?: boolean;
  selectable?: boolean;
  dataSourceId?: number;
  databaseName?: string;
  tableName?: string;
  columnName?: string;
  columnType?: string;
  isPrimaryKey?: boolean;
  draggable?: boolean;
}

const DatabaseBrowser: React.FC<DatabaseBrowserProps> = ({
  onSelect,
  onDragStart,
  style,
  className
}) => {
  const [treeData, setTreeData] = useState<TreeNode[]>([]);
  const [expandedKeys, setExpandedKeys] = useState<string[]>([]);
  const [searchValue, setSearchValue] = useState<string>('');
  const [autoExpandParent, setAutoExpandParent] = useState<boolean>(true);
  const [loading, setLoading] = useState<boolean>(false);
  const [messageApi, contextHolder] = message.useMessage();

  // 加载数据源列表
  useEffect(() => {
    fetchDataSources();
  }, []);

  // 获取数据源列表
  const fetchDataSources = async () => {
    try {
      setLoading(true);
      const response = await getAllDataSources();
      
      if (response.success && response.data) {
        const dataSourceNodes = response.data.map(ds => ({
          key: `ds-${ds.id}`,
          title: (
            <span className={styles.nodeTitle}>
              <span className={styles.nodeName}>{ds.name}</span>
              <span className={styles.nodeType}>{ds.type}</span>
            </span>
          ),
          icon: <DatabaseOutlined />,
          children: [],
          isLeaf: false,
          selectable: true,
          dataSourceId: ds.id,
          draggable: false
        }));
        
        setTreeData(dataSourceNodes);
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

  // 加载子节点
  const onLoadData = async (treeNode: any): Promise<void> => {
    if (treeNode.children && treeNode.children.length > 0) {
      return;
    }

    try {
      const { key, dataSourceId, databaseName, tableName } = treeNode;
      
      if (key.startsWith('ds-')) {
        // 加载数据库列表
        const response = await getDatabases(dataSourceId);
        
        if (response.success && response.data) {
          const databaseNodes = response.data.map(db => ({
            key: `db-${dataSourceId}-${db.name}`,
            title: db.name,
            icon: <DatabaseOutlined />,
            children: [],
            isLeaf: false,
            selectable: true,
            dataSourceId,
            databaseName: db.name,
            draggable: true
          }));
          
          updateTreeData(treeData, key, databaseNodes);
        } else {
          messageApi.error('获取数据库列表失败');
        }
      } else if (key.startsWith('db-')) {
        // 加载表列表
        const response = await getTables(dataSourceId, databaseName);
        
        if (response.success && response.data) {
          const tableNodes = response.data.map(table => ({
            key: `tb-${dataSourceId}-${databaseName}-${table.name}`,
            title: (
              <span className={styles.nodeTitle}>
                <span className={styles.nodeName}>{table.name}</span>
                <span className={styles.nodeType}>{table.type}</span>
              </span>
            ),
            icon: <TableOutlined />,
            children: [],
            isLeaf: false,
            selectable: true,
            dataSourceId,
            databaseName,
            tableName: table.name,
            draggable: true
          }));
          
          updateTreeData(treeData, key, tableNodes);
        } else {
          messageApi.error('获取表列表失败');
        }
      } else if (key.startsWith('tb-')) {
        // 加载列列表
        const response = await getColumns(dataSourceId, databaseName, tableName);
        
        if (response.success && response.data) {
          const columnNodes = response.data.map(column => ({
            key: `col-${dataSourceId}-${databaseName}-${tableName}-${column.name}`,
            title: (
              <span className={styles.nodeTitle}>
                <span className={styles.nodeName}>{column.name}</span>
                <span className={styles.nodeType}>{column.type}</span>
              </span>
            ),
            icon: column.isPrimaryKey ? <KeyOutlined /> : <ColumnOutlined />,
            isLeaf: true,
            selectable: true,
            dataSourceId,
            databaseName,
            tableName,
            columnName: column.name,
            columnType: column.type,
            isPrimaryKey: column.isPrimaryKey,
            draggable: true
          }));
          
          updateTreeData(treeData, key, columnNodes);
        } else {
          messageApi.error('获取列列表失败');
        }
      }
    } catch (error) {
      console.error('Error loading data:', error);
      messageApi.error('加载数据出错');
    }
  };

  // 更新树数据
  const updateTreeData = (list: TreeNode[], key: string, children: TreeNode[]): void => {
    const newTreeData = list.map(node => {
      if (node.key === key) {
        return {
          ...node,
          children
        };
      }
      
      if (node.children) {
        return {
          ...node,
          children: updateTreeData(node.children, key, children)
        };
      }
      
      return node;
    });
    
    setTreeData(newTreeData);
  };

  // 处理搜索
  const handleSearch = (value: string) => {
    setSearchValue(value);
    
    if (value) {
      // 搜索并展开匹配的节点
      const expandedKeys = findMatchingKeys(treeData, value);
      setExpandedKeys(expandedKeys);
      setAutoExpandParent(true);
    } else {
      setExpandedKeys([]);
      setAutoExpandParent(false);
    }
  };

  // 查找匹配的键
  const findMatchingKeys = (nodes: TreeNode[], searchValue: string): string[] => {
    const keys: string[] = [];
    
    const traverse = (nodes: TreeNode[], parentMatched: boolean = false) => {
      nodes.forEach(node => {
        const nodeTitle = typeof node.title === 'string' ? node.title : '';
        const matched = nodeTitle.toLowerCase().includes(searchValue.toLowerCase()) || parentMatched;
        
        if (matched) {
          keys.push(node.key);
        }
        
        if (node.children) {
          traverse(node.children, matched);
        }
      });
    };
    
    traverse(nodes);
    return keys;
  };

  // 处理展开/折叠
  const handleExpand = (expandedKeys: string[]) => {
    setExpandedKeys(expandedKeys);
    setAutoExpandParent(false);
  };

  // 处理选择
  const handleSelect = (selectedKeys: React.Key[], info: any) => {
    if (selectedKeys.length > 0 && onSelect) {
      onSelect(info.node);
    }
  };

  // 处理拖拽开始
  const handleDragStart = (info: any) => {
    if (onDragStart && info.node.draggable) {
      onDragStart(info.node, info.event);
    }
  };

  // 处理复制
  const handleCopy = (node: any) => {
    let textToCopy = '';
    
    if (node.columnName) {
      textToCopy = node.columnName;
    } else if (node.tableName) {
      textToCopy = node.tableName;
    } else if (node.databaseName) {
      textToCopy = node.databaseName;
    }
    
    if (textToCopy) {
      navigator.clipboard.writeText(textToCopy);
      messageApi.success(`已复制: ${textToCopy}`);
    }
  };

  // 渲染树节点标题
  const renderTitle = (node: any) => {
    const { title, draggable } = node;
    
    return (
      <div className={styles.titleContainer}>
        <span className={styles.title}>{title}</span>
        {draggable && (
          <span className={styles.actions}>
            <Tooltip title="复制">
              <Button
                type="text"
                size="small"
                icon={<CopyOutlined />}
                onClick={(e) => {
                  e.stopPropagation();
                  handleCopy(node);
                }}
              />
            </Tooltip>
          </span>
        )}
      </div>
    );
  };

  return (
    <div className={`${styles.databaseBrowser} ${className}`} style={style}>
      {contextHolder}
      <div className={styles.header}>
        <Search
          placeholder="搜索数据库对象"
          allowClear
          value={searchValue}
          onChange={(e) => setSearchValue(e.target.value)}
          onSearch={handleSearch}
          className={styles.search}
        />
        <Button
          icon={<ReloadOutlined />}
          onClick={fetchDataSources}
          size="small"
        />
      </div>
      
      <div className={styles.content}>
        <Spin spinning={loading}>
          {treeData.length > 0 ? (
            <Tree
              showIcon
              loadData={onLoadData}
              treeData={treeData}
              expandedKeys={expandedKeys}
              autoExpandParent={autoExpandParent}
              onExpand={handleExpand}
              onSelect={handleSelect}
              onDragStart={handleDragStart}
              draggable
              titleRender={renderTitle}
            />
          ) : (
            <Empty description="暂无数据源" />
          )}
        </Spin>
      </div>
    </div>
  );
};

export default DatabaseBrowser;
