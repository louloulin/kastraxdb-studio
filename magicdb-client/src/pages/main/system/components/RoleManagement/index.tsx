import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Tree, message, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons';
import { getRoleList, getPermissionList, createRole, updateRole, deleteRole } from '@/service/system';
import styles from './index.less';

const RoleManagement: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [roleList, setRoleList] = useState<any[]>([]);
  const [permissionList, setPermissionList] = useState<any[]>([]);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>('创建角色');
  const [form] = Form.useForm();
  const [currentRole, setCurrentRole] = useState<any>(null);
  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    fetchRoleList();
    fetchPermissionList();
  }, []);

  const fetchRoleList = async () => {
    try {
      setLoading(true);
      const response = await getRoleList();
      if (response && response.success) {
        setRoleList(response.data || []);
      } else {
        messageApi.error('获取角色列表失败');
      }
    } catch (error) {
      console.error('获取角色列表出错:', error);
      messageApi.error('获取角色列表出错');
    } finally {
      setLoading(false);
    }
  };

  const fetchPermissionList = async () => {
    try {
      const response = await getPermissionList();
      if (response && response.success) {
        setPermissionList(response.data || []);
      } else {
        messageApi.error('获取权限列表失败');
      }
    } catch (error) {
      console.error('获取权限列表出错:', error);
      messageApi.error('获取权限列表出错');
    }
  };

  const handleCreateRole = () => {
    setModalTitle('创建角色');
    setCurrentRole(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEditRole = (role: any) => {
    setModalTitle('编辑角色');
    setCurrentRole(role);
    form.setFieldsValue({
      ...role,
      permissions: role.permissions.map((p: any) => p.id),
    });
    setModalVisible(true);
  };

  const handleDeleteRole = async (roleId: string) => {
    try {
      setLoading(true);
      const response = await deleteRole(roleId);
      if (response && response.success) {
        messageApi.success('删除角色成功');
        fetchRoleList();
      } else {
        messageApi.error('删除角色失败');
      }
    } catch (error) {
      console.error('删除角色出错:', error);
      messageApi.error('删除角色出错');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveRole = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);
      
      const saveFunc = currentRole ? updateRole : createRole;
      const response = await saveFunc({
        ...values,
        id: currentRole?.id,
      });
      
      if (response && response.success) {
        messageApi.success(`${currentRole ? '更新' : '创建'}角色成功`);
        setModalVisible(false);
        fetchRoleList();
      } else {
        messageApi.error(`${currentRole ? '更新' : '创建'}角色失败`);
      }
    } catch (error) {
      console.error(`${currentRole ? '更新' : '创建'}角色出错:`, error);
      messageApi.error(`${currentRole ? '更新' : '创建'}角色出错`);
    } finally {
      setLoading(false);
    }
  };

  // 将权限列表转换为树形结构
  const convertToTreeData = (permissions: any[]) => {
    return permissions.map(permission => ({
      title: permission.name,
      key: permission.id,
      children: permission.children ? convertToTreeData(permission.children) : undefined,
    }));
  };

  const columns = [
    {
      title: '角色名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '角色编码',
      dataIndex: 'code',
      key: 'code',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record: any) => (
        <div className={styles.tableActions}>
          <Button
            type="text"
            icon={<EditOutlined />}
            onClick={() => handleEditRole(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除该角色吗？"
            onConfirm={() => handleDeleteRole(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button
              type="text"
              danger
              icon={<DeleteOutlined />}
            >
              删除
            </Button>
          </Popconfirm>
        </div>
      ),
    },
  ];

  const treeData = convertToTreeData(permissionList);

  return (
    <div className={styles.roleManagement}>
      {contextHolder}
      <div className={styles.roleManagementHeader}>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={handleCreateRole}
        >
          创建角色
        </Button>
        <Button
          icon={<ReloadOutlined />}
          onClick={fetchRoleList}
        >
          刷新
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={roleList}
        rowKey="id"
        loading={loading}
      />
      <Modal
        title={modalTitle}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={handleSaveRole}
        confirmLoading={loading}
        width={600}
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
            name="code"
            label="角色编码"
            rules={[{ required: true, message: '请输入角色编码' }]}
          >
            <Input placeholder="请输入角色编码" />
          </Form.Item>
          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea placeholder="请输入描述" rows={4} />
          </Form.Item>
          <Form.Item
            name="permissions"
            label="权限"
            rules={[{ required: true, message: '请选择权限' }]}
          >
            <Tree
              checkable
              treeData={treeData}
              defaultExpandAll
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default RoleManagement;
