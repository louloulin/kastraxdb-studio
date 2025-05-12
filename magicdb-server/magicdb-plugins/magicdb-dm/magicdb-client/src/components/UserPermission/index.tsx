import React, { useEffect, useState } from 'react';
import { Button, Card, Table, Modal, Form, Input, Space, Popconfirm, message, Tabs, Tag, Select, Transfer } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { TransferItem } from 'antd/es/transfer';
import {
  getUserRoleInfo,
  getUserPermissions,
  getUserRoles,
  grantPermission,
  revokePermission,
  assignRoleToUser,
  removeRoleFromUser,
  getRoles,
  ResourceType,
  PermissionType,
  PermissionInfo,
  RoleInfo,
  UserRoleInfo,
} from '@/services/permission';
import styles from './index.less';

const { TabPane } = Tabs;
const { Option } = Select;

interface UserPermissionProps {
  userId: string;
  username: string;
  className?: string;
  style?: React.CSSProperties;
}

const UserPermission: React.FC<UserPermissionProps> = ({ userId, username, className, style }) => {
  const [userRoleInfo, setUserRoleInfo] = useState<UserRoleInfo | null>(null);
  const [allRoles, setAllRoles] = useState<RoleInfo[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [permissionModalVisible, setPermissionModalVisible] = useState<boolean>(false);
  const [roleModalVisible, setRoleModalVisible] = useState<boolean>(false);
  const [permissionForm] = Form.useForm();

  // 加载用户角色信息
  useEffect(() => {
    if (userId) {
      fetchUserRoleInfo();
      fetchAllRoles();
    }
  }, [userId]);

  // 获取用户角色信息
  const fetchUserRoleInfo = async () => {
    setLoading(true);
    try {
      const response = await getUserRoleInfo(userId);
      if (response && response.success) {
        setUserRoleInfo(response.data);
      } else {
        message.error('获取用户角色信息失败');
      }
    } catch (error) {
      console.error('Failed to fetch user role info:', error);
      message.error('获取用户角色信息失败');
    } finally {
      setLoading(false);
    }
  };

  // 获取所有角色
  const fetchAllRoles = async () => {
    try {
      const response = await getRoles();
      if (response && response.success) {
        setAllRoles(response.data || []);
      } else {
        message.error('获取角色列表失败');
      }
    } catch (error) {
      console.error('Failed to fetch roles:', error);
      message.error('获取角色列表失败');
    }
  };

  // 处理添加权限
  const handleAddPermission = async () => {
    try {
      const values = await permissionForm.validateFields();

      const response = await grantPermission(
        userId,
        values.resourceType,
        values.resourceId,
        values.permissionType
      );

      if (response && response.success) {
        message.success('授予权限成功');
        permissionForm.resetFields();
        fetchUserRoleInfo();
      } else {
        message.error('授予权限失败');
      }
    } catch (error) {
      console.error('Form validation failed:', error);
    }
  };

  // 处理移除权限
  const handleRemovePermission = async (permission: PermissionInfo) => {
    try {
      const response = await revokePermission(
        userId,
        permission.resourceType,
        permission.resourceId,
        permission.permissionType
      );

      if (response && response.success) {
        message.success('撤销权限成功');
        fetchUserRoleInfo();
      } else {
        message.error('撤销权限失败');
      }
    } catch (error) {
      console.error('Failed to revoke permission:', error);
      message.error('撤销权限失败');
    }
  };

  // 处理角色分配
  const [transferTargetKeys, setTransferTargetKeys] = useState<string[]>([]);

  // 初始化选中的角色
  useEffect(() => {
    if (userRoleInfo) {
      setTransferTargetKeys(userRoleInfo.roles.map(role => role.id));
    }
  }, [userRoleInfo]);

  const handleRoleAssignment = async (targetKeys: string[]) => {
    try {
      // 获取当前用户角色
      const currentRoleIds = userRoleInfo?.roles.map(role => role.id) || [];

      // 计算需要添加的角色
      const rolesToAdd = targetKeys.filter(id => !currentRoleIds.includes(id));

      // 计算需要移除的角色
      const rolesToRemove = currentRoleIds.filter(id => !targetKeys.includes(id));

      // 添加角色
      for (const roleId of rolesToAdd) {
        await assignRoleToUser(userId, roleId);
      }

      // 移除角色
      for (const roleId of rolesToRemove) {
        await removeRoleFromUser(userId, roleId);
      }

      message.success('角色分配成功');
      fetchUserRoleInfo();
      setRoleModalVisible(false);
    } catch (error) {
      console.error('Failed to assign roles:', error);
      message.error('角色分配失败');
    }
  };

  // 权限表格列
  const permissionColumns: ColumnsType<PermissionInfo> = [
    {
      title: '资源类型',
      dataIndex: 'resourceType',
      key: 'resourceType',
      render: (text: ResourceType) => (
        <Tag color={
          text === ResourceType.GLOBAL ? 'blue' :
          text === ResourceType.GROUP ? 'green' : 'orange'
        }>
          {text}
        </Tag>
      ),
    },
    {
      title: '资源ID',
      dataIndex: 'resourceId',
      key: 'resourceId',
    },
    {
      title: '权限类型',
      dataIndex: 'permissionType',
      key: 'permissionType',
      render: (text: PermissionType) => (
        <Tag color={
          text === PermissionType.READ ? 'blue' :
          text === PermissionType.EXECUTE ? 'green' :
          text === PermissionType.EDIT ? 'orange' :
          text === PermissionType.DELETE ? 'red' : 'purple'
        }>
          {text}
        </Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Popconfirm
          title="确定要撤销这个权限吗？"
          onConfirm={() => handleRemovePermission(record)}
          okText="确定"
          cancelText="取消"
        >
          <Button
            type="link"
            danger
            icon={<DeleteOutlined />}
          >
            撤销
          </Button>
        </Popconfirm>
      ),
    },
  ];

  // 角色表格列
  const roleColumns: ColumnsType<RoleInfo> = [
    {
      title: '角色名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
  ];

  // 转换角色数据为Transfer数据
  const getRoleTransferData = (): TransferItem[] => {
    return allRoles.map(role => ({
      key: role.id,
      title: role.name,
      description: role.description,
    }));
  };

  // 获取已选择的角色ID
  const getSelectedRoleKeys = (): string[] => {
    return userRoleInfo?.roles.map(role => role.id) || [];
  };

  return (
    <div className={`${styles.userPermission} ${className}`} style={style}>
      <Card
        title={`用户权限管理 - ${username}`}
        extra={
          <Space>
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => setPermissionModalVisible(true)}
            >
              添加权限
            </Button>
            <Button
              onClick={() => setRoleModalVisible(true)}
            >
              分配角色
            </Button>
          </Space>
        }
      >
        <Tabs defaultActiveKey="permissions">
          <TabPane tab="直接权限" key="permissions">
            <Table
              columns={permissionColumns}
              dataSource={userRoleInfo?.permissions.filter(p => p.userId === userId) || []}
              rowKey="id"
              loading={loading}
              pagination={{ pageSize: 10 }}
            />
          </TabPane>
          <TabPane tab="角色权限" key="rolePermissions">
            <Table
              columns={permissionColumns}
              dataSource={userRoleInfo?.permissions.filter(p => p.userId !== userId) || []}
              rowKey="id"
              loading={loading}
              pagination={{ pageSize: 10 }}
            />
          </TabPane>
          <TabPane tab="已分配角色" key="roles">
            <Table
              columns={roleColumns}
              dataSource={userRoleInfo?.roles || []}
              rowKey="id"
              loading={loading}
              pagination={{ pageSize: 10 }}
            />
          </TabPane>
        </Tabs>
      </Card>

      {/* 添加权限 */}
      <Modal
        title="添加权限"
        open={permissionModalVisible}
        onOk={handleAddPermission}
        onCancel={() => setPermissionModalVisible(false)}
      >
        <Form
          form={permissionForm}
          layout="vertical"
        >
          <Form.Item
            name="resourceType"
            label="资源类型"
            rules={[{ required: true, message: '请选择资源类型' }]}
          >
            <Select placeholder="请选择资源类型">
              <Option value={ResourceType.SERVICE}>服务</Option>
              <Option value={ResourceType.GROUP}>服务组</Option>
              <Option value={ResourceType.GLOBAL}>全局</Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="resourceId"
            label="资源ID"
            rules={[{ required: true, message: '请输入资源ID' }]}
          >
            <Input placeholder="请输入资源ID，使用 * 表示所有资源" />
          </Form.Item>
          <Form.Item
            name="permissionType"
            label="权限类型"
            rules={[{ required: true, message: '请选择权限类型' }]}
          >
            <Select placeholder="请选择权限类型">
              <Option value={PermissionType.READ}>读取</Option>
              <Option value={PermissionType.EXECUTE}>执行</Option>
              <Option value={PermissionType.EDIT}>编辑</Option>
              <Option value={PermissionType.DELETE}>删除</Option>
              <Option value={PermissionType.MANAGE}>管理</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      {/* 分配角色 */}
      <Modal
        title="分配角色"
        open={roleModalVisible}
        onOk={() => {
          const targetKeys = transferTargetKeys;
          handleRoleAssignment(targetKeys);
        }}
        onCancel={() => setRoleModalVisible(false)}
        width={800}
      >
        <Transfer
          dataSource={getRoleTransferData()}
          titles={['可用角色', '已分配角色']}
          targetKeys={getSelectedRoleKeys()}
          onChange={setTransferTargetKeys}
          render={item => item.title as string}
          listStyle={{ width: 300, height: 300 }}
        />
      </Modal>
    </div>
  );
};

export default UserPermission;
