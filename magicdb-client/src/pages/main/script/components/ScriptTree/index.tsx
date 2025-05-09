import React, { useState } from 'react';
import { Tree, Input, Button, Dropdown, Menu, Modal, Form, message } from 'antd';
import { 
  FolderOutlined, 
  FileOutlined, 
  PlusOutlined, 
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  MoreOutlined
} from '@ant-design/icons';
import { createScript, createScriptGroup, deleteScript, deleteScriptGroup } from '@/service/script';
import styles from './index.less';

const { Search } = Input;
const { DirectoryTree } = Tree;

interface ScriptTreeProps {
  scriptList: any[];
  onSelectScript: (scriptId: string) => void;
  onRefresh: () => void;
}

const ScriptTree: React.FC<ScriptTreeProps> = ({ scriptList, onSelectScript, onRefresh }) => {
  const [searchValue, setSearchValue] = useState<string>('');
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  const [createGroupModalVisible, setCreateGroupModalVisible] = useState<boolean>(false);
  const [form] = Form.useForm();
  const [groupForm] = Form.useForm();
  const [messageApi, contextHolder] = message.useMessage();

  // 将脚本列表转换为树形结构
  const convertToTreeData = (scripts: any[], groups: any[]) => {
    // 创建分组节点
    const groupNodes = groups.map(group => ({
      title: group.name,
      key: `group-${group.id}`,
      icon: <FolderOutlined />,
      isLeaf: false,
      children: []
    }));

    // 创建分组映射，方便查找
    const groupMap = {};
    groupNodes.forEach(node => {
      groupMap[node.key] = node;
    });

    // 将脚本添加到对应的分组中
    scripts.forEach(script => {
      const node = {
        title: script.name,
        key: `script-${script.id}`,
        icon: <FileOutlined />,
        isLeaf: true,
        script
      };

      if (script.groupId) {
        const groupKey = `group-${script.groupId}`;
        if (groupMap[groupKey]) {
          groupMap[groupKey].children.push(node);
        } else {
          // 如果分组不存在，添加到根节点
          groupNodes.push(node);
        }
      } else {
        // 没有分组的脚本直接添加到根节点
        groupNodes.push(node);
      }
    });

    return groupNodes;
  };

  // 处理搜索
  const handleSearch = (value: string) => {
    setSearchValue(value);
  };

  // 过滤树节点
  const filterTreeNode = (node: any) => {
    if (!searchValue) return true;
    if (node.title.toLowerCase().indexOf(searchValue.toLowerCase()) > -1) {
      return true;
    }
    return false;
  };

  // 处理选择节点
  const handleSelect = (selectedKeys: React.Key[], info: any) => {
    const key = selectedKeys[0] as string;
    if (key && key.startsWith('script-')) {
      const scriptId = key.replace('script-', '');
      onSelectScript(scriptId);
    }
  };

  // 处理右键菜单
  const handleRightClick = ({ event, node }: any) => {
    event.preventDefault();
    // 根据节点类型显示不同的菜单
    if (node.key.startsWith('group-')) {
      // 分组菜单
    } else if (node.key.startsWith('script-')) {
      // 脚本菜单
    }
  };

  // 创建脚本
  const handleCreateScript = async (values: any) => {
    try {
      const response = await createScript({
        name: values.name,
        content: '',
        language: values.language || 'js',
        description: values.description || '',
        groupId: values.groupId || null
      });

      if (response && response.success) {
        messageApi.success('创建脚本成功');
        setCreateModalVisible(false);
        form.resetFields();
        onRefresh();
        // 选择新创建的脚本
        onSelectScript(response.data.id);
      } else {
        messageApi.error('创建脚本失败');
      }
    } catch (error) {
      console.error('创建脚本出错:', error);
      messageApi.error('创建脚本出错');
    }
  };

  // 创建分组
  const handleCreateGroup = async (values: any) => {
    try {
      const response = await createScriptGroup({
        name: values.name,
        description: values.description || '',
        parentId: values.parentId || null
      });

      if (response && response.success) {
        messageApi.success('创建分组成功');
        setCreateGroupModalVisible(false);
        groupForm.resetFields();
        onRefresh();
      } else {
        messageApi.error('创建分组失败');
      }
    } catch (error) {
      console.error('创建分组出错:', error);
      messageApi.error('创建分组出错');
    }
  };

  // 提取脚本和分组数据
  const scripts = scriptList.filter(item => item.type === 'script');
  const groups = scriptList.filter(item => item.type === 'group');
  const treeData = convertToTreeData(scripts, groups);

  return (
    <div className={styles.scriptTree}>
      {contextHolder}
      <div className={styles.scriptTreeHeader}>
        <Search
          placeholder="搜索脚本"
          allowClear
          onSearch={handleSearch}
          className={styles.scriptTreeSearch}
        />
        <div className={styles.scriptTreeActions}>
          <Dropdown
            overlay={
              <Menu>
                <Menu.Item key="script" onClick={() => setCreateModalVisible(true)}>
                  <FileOutlined /> 创建脚本
                </Menu.Item>
                <Menu.Item key="group" onClick={() => setCreateGroupModalVisible(true)}>
                  <FolderOutlined /> 创建分组
                </Menu.Item>
              </Menu>
            }
            trigger={['click']}
          >
            <Button type="primary" icon={<PlusOutlined />} size="small" />
          </Dropdown>
          <Button icon={<ReloadOutlined />} size="small" onClick={onRefresh} />
        </div>
      </div>
      <DirectoryTree
        className={styles.scriptTreeContent}
        treeData={treeData}
        onSelect={handleSelect}
        onRightClick={handleRightClick}
        filterTreeNode={filterTreeNode}
      />

      {/* 创建脚本弹窗 */}
      <Modal
        title="创建脚本"
        open={createModalVisible}
        onCancel={() => setCreateModalVisible(false)}
        onOk={() => form.submit()}
      >
        <Form form={form} layout="vertical" onFinish={handleCreateScript}>
          <Form.Item
            name="name"
            label="脚本名称"
            rules={[{ required: true, message: '请输入脚本名称' }]}
          >
            <Input placeholder="请输入脚本名称" />
          </Form.Item>
          <Form.Item name="description" label="脚本描述">
            <Input.TextArea placeholder="请输入脚本描述" />
          </Form.Item>
          <Form.Item name="language" label="脚本语言" initialValue="js">
            <Input placeholder="请输入脚本语言" />
          </Form.Item>
          <Form.Item name="groupId" label="所属分组">
            <Input placeholder="请输入分组ID" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 创建分组弹窗 */}
      <Modal
        title="创建分组"
        open={createGroupModalVisible}
        onCancel={() => setCreateGroupModalVisible(false)}
        onOk={() => groupForm.submit()}
      >
        <Form form={groupForm} layout="vertical" onFinish={handleCreateGroup}>
          <Form.Item
            name="name"
            label="分组名称"
            rules={[{ required: true, message: '请输入分组名称' }]}
          >
            <Input placeholder="请输入分组名称" />
          </Form.Item>
          <Form.Item name="description" label="分组描述">
            <Input.TextArea placeholder="请输入分组描述" />
          </Form.Item>
          <Form.Item name="parentId" label="父分组">
            <Input placeholder="请输入父分组ID" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ScriptTree;
