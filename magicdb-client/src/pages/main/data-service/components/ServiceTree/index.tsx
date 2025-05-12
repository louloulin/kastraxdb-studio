import React, { useState } from 'react';
import { Tree, Input, Button, Dropdown, Modal, Form, message } from 'antd';
import type { MenuProps } from 'antd';
import {
  FolderOutlined,
  ApiOutlined,
  PlusOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  MoreOutlined,
} from '@ant-design/icons';
import { createService, createServiceGroup, deleteService, deleteServiceGroup } from '@/service/data-service';
import i18n from '@/i18n';
import styles from './index.less';

const { Search } = Input;
const { DirectoryTree } = Tree;

interface ServiceTreeProps {
  serviceList: any[];
  onSelectService: (serviceId: string) => void;
  onRefresh: () => void;
}

const ServiceTree: React.FC<ServiceTreeProps> = ({ serviceList, onSelectService, onRefresh }) => {
  const [searchValue, setSearchValue] = useState<string>('');
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  const [createGroupModalVisible, setCreateGroupModalVisible] = useState<boolean>(false);
  const [form] = Form.useForm();
  const [groupForm] = Form.useForm();
  const [messageApi, contextHolder] = message.useMessage();

  // 将服务列表转换为树形结构
  const convertToTreeData = (services: any[], groups: any[]) => {
    // 创建分组节点
    const groupNodes = groups.map((group) => ({
      title: group.name,
      key: `group-${group.id}`,
      icon: <FolderOutlined />,
      isLeaf: false,
      children: [],
    }));

    // 创建分组映射，方便查找
    const groupMap = {};
    groupNodes.forEach((node) => {
      groupMap[node.key] = node;
    });

    // 将服务添加到对应的分组中
    services.forEach((service) => {
      const node = {
        title: service.name,
        key: `service-${service.id}`,
        icon: <ApiOutlined />,
        isLeaf: true,
        service,
      };

      if (service.groupId) {
        const groupKey = `group-${service.groupId}`;
        if (groupMap[groupKey]) {
          groupMap[groupKey].children.push(node);
        } else {
          // 如果分组不存在，添加到根节点
          groupNodes.push(node);
        }
      } else {
        // 没有分组的服务直接添加到根节点
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
    if (key && key.startsWith('service-')) {
      const serviceId = key.replace('service-', '');
      onSelectService(serviceId);
    }
  };

  // 处理右键菜单
  const handleRightClick = ({ event, node }: any) => {
    event.preventDefault();
    // 根据节点类型显示不同的菜单
    if (node.key.startsWith('group-')) {
      // 分组菜单
    } else if (node.key.startsWith('service-')) {
      // 服务菜单
    }
  };

  // 创建数据服务
  const handleCreateService = async (values: any) => {
    try {
      const response = await createService({
        name: values.name,
        type: values.type || 'query',
        script: '',
        language: values.language || 'js',
        description: values.description || '',
        groupId: values.groupId || null,
      });

      if (response && response.success) {
        messageApi.success('创建数据服务成功');
        setCreateModalVisible(false);
        form.resetFields();
        onRefresh();
        // 选择新创建的服务
        onSelectService(response.data.id);
      } else {
        messageApi.error('创建数据服务失败');
      }
    } catch (error) {
      console.error('创建数据服务出错:', error);
      messageApi.error('创建数据服务出错');
    }
  };

  // 创建分组
  const handleCreateGroup = async (values: any) => {
    try {
      const response = await createServiceGroup({
        name: values.name,
        description: values.description || '',
        parentId: values.parentId || null,
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

  // 提取服务和分组数据
  const services = serviceList.filter((item) => item.type !== 'group');
  const groups = serviceList.filter((item) => item.type === 'group');
  const treeData = convertToTreeData(services, groups);

  return (
    <div className={styles.serviceTree}>
      {contextHolder}
      <div className={styles.serviceTreeHeader}>
        <Search
          placeholder={i18n('data-service.search')}
          allowClear
          onSearch={handleSearch}
          className={styles.serviceTreeSearch}
        />
        <div className={styles.serviceTreeActions}>
          <Dropdown
            menu={{
              items: [
                {
                  key: 'service',
                  icon: <ApiOutlined />,
                  label: i18n('data-service.create'),
                  onClick: () => setCreateModalVisible(true)
                },
                {
                  key: 'group',
                  icon: <FolderOutlined />,
                  label: i18n('data-service.group.create'),
                  onClick: () => setCreateGroupModalVisible(true)
                }
              ]
            }}
            trigger={['click']}
          >
            <Button type="primary" icon={<PlusOutlined />} size="small" />
          </Dropdown>
          <Button icon={<ReloadOutlined />} size="small" onClick={onRefresh} />
        </div>
      </div>
      <DirectoryTree
        className={styles.serviceTreeContent}
        treeData={treeData}
        onSelect={handleSelect}
        onRightClick={handleRightClick}
        filterTreeNode={filterTreeNode}
      />

      {/* 创建数据服务弹窗 */}
      <Modal
        title={i18n('data-service.create')}
        open={createModalVisible}
        onCancel={() => setCreateModalVisible(false)}
        onOk={() => form.submit()}
      >
        <Form form={form} layout="vertical" onFinish={handleCreateService}>
          <Form.Item
            name="name"
            label={i18n('data-service.name')}
            rules={[{ required: true, message: i18n('data-service.name.required') }]}
          >
            <Input placeholder={i18n('data-service.name.placeholder')} />
          </Form.Item>
          <Form.Item name="description" label={i18n('data-service.description')}>
            <Input.TextArea placeholder={i18n('data-service.description.placeholder')} />
          </Form.Item>
          <Form.Item name="type" label={i18n('data-service.type')} initialValue="query">
            <Input placeholder={i18n('data-service.type.placeholder')} />
          </Form.Item>
          <Form.Item name="language" label={i18n('data-service.language')} initialValue="js">
            <Input placeholder={i18n('data-service.language.placeholder')} />
          </Form.Item>
          <Form.Item name="groupId" label={i18n('data-service.group')}>
            <Input placeholder={i18n('data-service.group.placeholder')} />
          </Form.Item>
        </Form>
      </Modal>

      {/* 创建分组弹窗 */}
      <Modal
        title={i18n('data-service.group.create')}
        open={createGroupModalVisible}
        onCancel={() => setCreateGroupModalVisible(false)}
        onOk={() => groupForm.submit()}
      >
        <Form form={groupForm} layout="vertical" onFinish={handleCreateGroup}>
          <Form.Item
            name="name"
            label={i18n('data-service.group.name')}
            rules={[{ required: true, message: i18n('data-service.group.name.required') }]}
          >
            <Input placeholder={i18n('data-service.group.name.placeholder')} />
          </Form.Item>
          <Form.Item name="description" label={i18n('data-service.group.description')}>
            <Input.TextArea placeholder={i18n('data-service.group.description.placeholder')} />
          </Form.Item>
          <Form.Item name="parentId" label={i18n('data-service.group.parent')}>
            <Input placeholder={i18n('data-service.group.parent.placeholder')} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ServiceTree;
