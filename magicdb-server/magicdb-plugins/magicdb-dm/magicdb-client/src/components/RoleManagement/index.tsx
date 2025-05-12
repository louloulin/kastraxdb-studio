import React, { useEffect, useState } from 'react';
import { Button, Card, Table, Modal, Form, Input, Space, Popconfirm, message, Tabs, Tag, Select } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import {
  getRoles,
  createRole,
  updateRole,
  deleteRole,
  getRole,
  addRolePermission,
  removeRolePermission,
  ResourceType,
  PermissionType,
  RoleInfo,
  PermissionInfo,
} from '@/services/permission';
import styles from './index.less';

const { TabPane } = Tabs;
const { Option } = Select;

interface RoleManagementProps {
  className?: string;
  style?: React.CSSProperties;
}

const RoleManagement: React.FC<RoleManagementProps> = ({ className, style }) => {
  const [roles, setRoles] = useState<RoleInfo[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>('创建角色');
  const [editingRole, setEditingRole] = useState<RoleInfo | null>(null);
  const [permissionModalVisible, setPermissionModalVisible] = useState<boolean>(false);
  const [selectedRole, setSelectedRole] = useState<RoleInfo | null>(null);
  const [form] = Form.useForm();
  const [permissionForm] = Form.useForm();

  // 加载角色列表
  useEffect(() => {
    fetchRoles();
  }, []);

  // 获取角色列表
  const fetchRoles = async () => {
    setLoading(true);
    try {
      const response = await getRoles();
      if (response && response.success) {
        setRoles(response.data || []);
      } else {
        message.error('获取角色列表失败');
      }
    } catch (error) {
      console.error('Failed to fetch roles:', error);
      message.error('获取角色列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 获取角色详情
  const fetchRoleDetail = async (id: string) => {
    try {
      const response = await getRole(id);
      if (response && response.success) {
        setSelectedRole(response.data);
      } else {
        message.error('获取角色详情失败');
      }
    } catch (error) {
      console.error('Failed to fetch role detail:', error);
      message.error('获取角色详情失败');
    }
  };

  // 处理创建角色
  const handleCreateRole = () => {
    setModalTitle('创建角色');
    setEditingRole(null);
    form.resetFields();
    setModalVisible(true);
  };

  // 处理编辑角色
  const handleEditRole = (role: RoleInfo) => {
    setModalTitle('编辑角色');
    setEditingRole(role);
    form.setFieldsValue({
      name: role.name,
      description: role.description,
    });
    setModalVisible(true);
  };

  // 处理删除角色
  const handleDeleteRole = async (id: string) => {
    try {
      const response = await deleteRole(id);
      if (response && response.success) {
        message.success('删除角色成功');
        fetchRoles();
      } else {
        message.error('删除角色失败');
      }
    } catch (error) {
      console.error('Failed to delete role:', error);
      message.error('删除角色失败');
    }
  };

  // 处理管理权限
  const handleManagePermissions = async (role: RoleInfo) => {
    await fetchRoleDetail(role.id);
    setPermissionModalVisible(true);
  };

  // 处理表单提交
  const handleFormSubmit = async () => {
    try {
      const values = await form.validateFields();
      
      if (editingRole) {
        // 更新角色
        const response = await updateRole(editingRole.id, values.name, values.description);
        if (response && response.success) {
          message.success('更新角色成功');
          setModalVisible(false);
          fetchRoles();
        } else {
          message.error('更新角色失败');
        }
      } else {
        // 创建角色
        const response = await createRole(values.name, values.description);
        if (response && response.success) {
          message.success('创建角色成功');
          setModalVisible(false);
          fetchRoles();
        } else {
          message.error('创建角色失败');
        }
      }
    } catch (error) {
      console.error('Form validation failed:', error);
    }
  };

  // 处理添加权限
  const handleAddPermission = async () => {
    try {
      const values = await permissionForm.validateFields();
      
      if (!selectedRole) {
        message.error('未选择角色');
        return;
      }
      
      const response = await addRolePermission(
        selectedRole.id,
        values.resourceType,
        values.resourceId,
        values.permissionType
      );
      
      if (response && response.success) {
        message.success('添加权限成功');
        permissionForm.resetFields();
        fetchRoleDetail(selectedRole.id);
      } else {
        message.error('添加权限失败');
      }
    } catch (error) {
      console.error('Form validation failed:', error);
    }
  };

  // 处理移除权限
  const handleRemovePermission = async (permission: PermissionInfo) => {
    if (!selectedRole) {
      message.error('未选择角色');
      return;
    }
    
    try {
      const response = await removeRolePermission(
        selectedRole.id,
        permission.resourceType,
        permission.resourceId,
        permission.permissionType
      );
      
      if (response && response.success) {
        message.success('移除权限成功');
        fetchRoleDetail(selectedRole.id);
      } else {
        message.error('移除权限失败');
      }
    } catch (error) {
      console.error('Failed to remove permission:', error);
      message.error('移除权限失败');
    }
  };

  // 角色表格列
  const columns: ColumnsType<RoleInfo> = [
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
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (text: string) => new Date(text).toLocaleString(),
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      render: (text: string) => new Date(text).toLocaleString(),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEditRole(record)}
          >
            编辑
          </Button>
          <Button
            type="link"
            onClick={() => handleManagePermissions(record)}
          >
            权限
          </Button>
          <Popconfirm
            title="确定要删除这个角色吗？"
            onConfirm={() => handleDeleteRole(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button
              type="link"
              danger
              icon={<DeleteOutlined />}
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

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
          title="确定要移除这个权限吗？"
          onConfirm={() => handleRemovePermission(record)}
          okText="确定"
          cancelText="取消"
        >
          <Button
            type="link"
            danger
            icon={<DeleteOutlined />}
          >
            移除
          </Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <div className={`${styles.roleManagement} ${className}`} style={style}>
      <Card
        title="角色管理"
        extra={
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleCreateRole}
          >
            创建角色
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={roles}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      {/* 角色表单 */}
      <Modal
        title={modalTitle}
        open={modalVisible}
        onOk={handleFormSubmit}
        onCancel={() => setModalVisible(false)}
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="name"
            label="角色名称"
            rules={[{ required: true, message: '请输入角色名称' }]}
          >
            <Input placeholder="请输入角色名称" />
          </Form.Item>
          <Form.Item
            name="description"
            label="角色描述"
          >
            <Input.TextArea placeholder="请输入角色描述" rows={4} />
          </Form.Item>
        </Form>
      </Modal>

      {/* 权限管理 */}
      <Modal
        title={`管理权限 - ${selectedRole?.name}`}
        open={permissionModalVisible}
        onCancel={() => setPermissionModalVisible(false)}
        footer={null}
        width={800}
      >
        <Tabs defaultActiveKey="permissions">
          <TabPane tab="权限列表" key="permissions">
            <Table
              columns={permissionColumns}
              dataSource={selectedRole?.permissions || []}
              rowKey="id"
              pagination={{ pageSize: 5 }}
            />
          </TabPane>
          <TabPane tab="添加权限" key="addPermission">
            <Form
              form={permissionForm}
              layout="vertical"
              onFinish={handleAddPermission}
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
              <Form.Item>
                <Button type="primary" htmlType="submit">
                  添加权限
                </Button>
              </Form.Item>
            </Form>
          </TabPane>
        </Tabs>
      </Modal>
    </div>
  );
};

export default RoleManagement;
