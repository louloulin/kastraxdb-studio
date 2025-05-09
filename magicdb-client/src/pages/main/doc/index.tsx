import React, { useState, useEffect } from 'react';
import { Layout, Tree, Spin, Empty, message, Button } from 'antd';
import { FileOutlined, FolderOutlined, ReloadOutlined } from '@ant-design/icons';
import ApiDoc from './components/ApiDoc';
import { getApiList, getApiDetail } from '@/service/doc';
import styles from './index.less';

const { Sider, Content } = Layout;
const { DirectoryTree } = Tree;

interface ApiDocProps {
  id: string;
  name: string;
  path: string;
  method: string;
  description: string;
  parameters: any[];
  responses: any[];
}

const DocPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [apiList, setApiList] = useState<any[]>([]);
  const [selectedApi, setSelectedApi] = useState<ApiDocProps | null>(null);
  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    fetchApiList();
  }, []);

  const fetchApiList = async () => {
    try {
      setLoading(true);
      const response = await getApiList();
      if (response && response.success) {
        setApiList(response.data || []);
      } else {
        messageApi.error('获取API列表失败');
      }
    } catch (error) {
      console.error('获取API列表出错:', error);
      messageApi.error('获取API列表出错');
    } finally {
      setLoading(false);
    }
  };

  const handleSelectApi = async (apiId: string) => {
    try {
      setLoading(true);
      const response = await getApiDetail(apiId);
      if (response && response.success) {
        setSelectedApi(response.data);
      } else {
        messageApi.error('获取API详情失败');
      }
    } catch (error) {
      console.error('获取API详情出错:', error);
      messageApi.error('获取API详情出错');
    } finally {
      setLoading(false);
    }
  };

  // 将API列表转换为树形结构
  const convertToTreeData = (apis: any[]) => {
    // 按路径分组
    const pathMap = {};
    
    apis.forEach(api => {
      const pathParts = api.path.split('/').filter(Boolean);
      let currentMap = pathMap;
      
      pathParts.forEach((part, index) => {
        if (!currentMap[part]) {
          currentMap[part] = {};
        }
        
        if (index === pathParts.length - 1) {
          if (!currentMap[part].apis) {
            currentMap[part].apis = [];
          }
          currentMap[part].apis.push(api);
        }
        
        currentMap = currentMap[part];
      });
    });
    
    // 递归构建树节点
    const buildTreeNodes = (map, path = '') => {
      return Object.keys(map).map(key => {
        const currentPath = path ? `${path}/${key}` : `/${key}`;
        const node = {
          title: key,
          key: currentPath,
          icon: <FolderOutlined />,
          children: []
        };
        
        // 添加API节点
        if (map[key].apis) {
          map[key].apis.forEach(api => {
            node.children.push({
              title: `${api.method} ${api.name}`,
              key: `api-${api.id}`,
              icon: <FileOutlined />,
              isLeaf: true,
              api
            });
          });
        }
        
        // 递归添加子节点
        const childNodes = buildTreeNodes(map[key], currentPath);
        node.children.push(...childNodes);
        
        return node;
      });
    };
    
    return buildTreeNodes(pathMap);
  };

  // 处理选择节点
  const handleSelect = (selectedKeys: React.Key[], info: any) => {
    const key = selectedKeys[0] as string;
    if (key && key.startsWith('api-')) {
      const apiId = key.replace('api-', '');
      handleSelectApi(apiId);
    }
  };

  const treeData = convertToTreeData(apiList);

  return (
    <Layout className={styles.docContainer}>
      {contextHolder}
      <Sider width={280} theme="light" className={styles.docSider}>
        <div className={styles.docSiderHeader}>
          <h3>API文档</h3>
          <Button
            icon={<ReloadOutlined />}
            size="small"
            onClick={fetchApiList}
          />
        </div>
        <Spin spinning={loading}>
          {treeData.length > 0 ? (
            <DirectoryTree
              className={styles.docTree}
              treeData={treeData}
              onSelect={handleSelect}
            />
          ) : (
            <Empty description="暂无API文档" />
          )}
        </Spin>
      </Sider>
      <Content className={styles.docContent}>
        <Spin spinning={loading}>
          {selectedApi ? (
            <ApiDoc api={selectedApi} />
          ) : (
            <div className={styles.docEmpty}>
              请选择一个API查看详情
            </div>
          )}
        </Spin>
      </Content>
    </Layout>
  );
};

export default DocPage;
