import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Select, Switch, message, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons';
import { getUserList, createUser, updateUser, deleteUser } from '@/service/system';
import styles from './index.less';

const { Option } = Select;

const UserManagement: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [userList, setUserList] = useState<any[]>([]);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>('创建用户');
  const [form] = Form.useForm();
  const [currentUser, setCurrentUser] = useState<any>(null);
  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    fetchUserList();
  }, []);

  const fetchUserList = async () => {
    try {
      setLoading(true);
      const response = await getUserList();
      if (response && response.success) {
        setUserList(response.data || []);
      } else {
        messageApi.error('获取用户列表失败');
      }
    } catch (error) {
      console.error('获取用户列表出错:', error);
      messageApi.error('获取用户列表出错');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateUser = () => {
    setModalTitle('创建用户');
    setCurrentUser(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEditUser = (user: any) => {
    setModalTitle('编辑用户');
    setCurrentUser(user);
    form.setFieldsValue(user);
    setModalVisible(true);
  };

  const handleDeleteUser = async (userId: string) => {
    try {
      setLoading(true);
      const response = await deleteUser(userId);
      if (response && response.success) {
        messageApi.success('删除用户成功');
        fetchUserList();
      } else {
        messageApi.error('删除用户失败');
      }
    } catch (error) {
      console.error('删除用户出错:', error);
      messageApi.error('删除用户出错');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveUser = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);
      
      const saveFunc = currentUser ? updateUser : createUser;
      const response = await saveFunc({
        ...values,
        id: currentUser?.id,
      });
      
      if (response && response.success) {
        messageApi.success(`${currentUser ? '更新' : '创建'}用户成功`);
        setModalVisible(false);
        fetchUserList();
      } else {
        messageApi.error(`${currentUser ? '更新' : '创建'}用户失败`);
      }
    } catch (error) {
      console.error(`${currentUser ? '更新' : '创建'}用户出错:`, error);
      messageApi.error(`${currentUser ? '更新' : '创建'}用户出错`);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: '角色',
      dataIndex: 'role',
      key: 'role',
    },
    {
      title: '状态',
      dataIndex: 'enabled',
      key: 'enabled',
      render: (enabled: boolean) => (
        enabled ? <span className={styles.statusEnabled}>启用</span> : <span className={styles.statusDisabled}>禁用</span>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record: any) => (
        <div className={styles.tableActions}>
          <Button
            type="text"
            icon={<EditOutlined />}
            onClick={() => handleEditUser(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除该用户吗？"
            onConfirm={() => handleDeleteUser(record.id)}
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

  return (
    <div className={styles.userManagement}>
      {contextHolder}
      <div className={styles.userManagementHeader}>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={handleCreateUser}
        >
          创建用户
        </Button>
        <Button
          icon={<ReloadOutlined />}
          onClick={fetchUserList}
        >
          刷新
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={userList}
        rowKey="id"
        loading={loading}
      />
      <Modal
        title={modalTitle}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={handleSaveUser}
        confirmLoading={loading}
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input placeholder="请输入用户名" />
          </Form.Item>
          {!currentUser && (
            <Form.Item
              name="password"
              label="密码"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <Input.Password placeholder="请输入密码" />
            </Form.Item>
          )}
          <Form.Item
            name="name"
            label="姓名"
            rules={[{ required: true, message: '请输入姓名' }]}
          >
            <Input placeholder="请输入姓名" />
          </Form.Item>
          <Form.Item
            name="email"
            label="邮箱"
            rules={[
              { required: true, message: '请输入邮箱' },
              { type: 'email', message: '请输入有效的邮箱地址' }
            ]}
          >
            <Input placeholder="请输入邮箱" />
          </Form.Item>
          <Form.Item
            name="role"
            label="角色"
            rules={[{ required: true, message: '请选择角色' }]}
          >
            <Select placeholder="请选择角色">
              <Option value="admin">管理员</Option>
              <Option value="user">普通用户</Option>
              <Option value="guest">访客</Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="enabled"
            label="状态"
            valuePropName="checked"
            initialValue={true}
          >
            <Switch checkedChildren="启用" unCheckedChildren="禁用" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserManagement;
