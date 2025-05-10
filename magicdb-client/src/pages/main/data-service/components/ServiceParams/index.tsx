import React, { useState } from 'react';
import { Form, Input, Button, Switch, Select, Divider, Tag, Tooltip, message, InputNumber } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { updateService } from '@/service/data-service';
import i18n from '@/i18n';
import styles from './index.less';

const { Option } = Select;

interface ServiceParamsProps {
  service: any;
  onSave: (service: any) => void;
}

const ServiceParams: React.FC<ServiceParamsProps> = ({ service, onSave }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(false);
  const [inputVisible, setInputVisible] = useState<boolean>(false);
  const [inputValue, setInputValue] = useState<string>('');
  const [messageApi, contextHolder] = message.useMessage();

  // 如果服务不存在，显示空内容
  if (!service) {
    return <div className={styles.serviceParamsEmpty}>{i18n('data-service.empty')}</div>;
  }

  // 处理保存参数
  const handleSaveParams = async (values: any) => {
    try {
      setLoading(true);
      const updatedService = {
        ...service,
        ...values,
      };

      const response = await updateService(updatedService);

      if (response && response.success) {
        messageApi.success(i18n('data-service.save.success'));
        onSave(response.data);
      } else {
        messageApi.error(i18n('data-service.save.failed'));
      }
    } catch (error) {
      console.error('Error saving parameters:', error);
      messageApi.error(i18n('data-service.save.error'));
    } finally {
      setLoading(false);
    }
  };

  // 处理添加标签
  const handleAddTag = () => {
    if (inputValue && !service.tags.includes(inputValue)) {
      const tags = [...(service.tags || []), inputValue];
      updateService({ ...service, tags })
        .then((response) => {
          if (response && response.success) {
            messageApi.success(i18n('data-service.tag.add.success'));
            onSave(response.data);
          } else {
            messageApi.error(i18n('data-service.tag.add.failed'));
          }
        })
        .catch((error) => {
          console.error('Error adding tag:', error);
          messageApi.error(i18n('data-service.tag.add.error'));
        });
    }
    setInputVisible(false);
    setInputValue('');
  };

  // 处理删除标签
  const handleRemoveTag = (tag: string) => {
    const tags = (service.tags || []).filter((t: string) => t !== tag);
    updateService({ ...service, tags })
      .then((response) => {
        if (response && response.success) {
          messageApi.success('删除标签成功');
          onSave(response.data);
        } else {
          messageApi.error('删除标签失败');
        }
      })
      .catch((error) => {
        console.error('删除标签出错:', error);
        messageApi.error('删除标签出错');
      });
  };

  return (
    <div className={styles.serviceParams}>
      {contextHolder}
      <div className={styles.serviceParamsHeader}>
        <h3>服务参数</h3>
      </div>
      <div className={styles.serviceParamsContent}>
        <Form form={form} layout="vertical" initialValues={service} onFinish={handleSaveParams}>
          <Form.Item name="name" label="服务名称" rules={[{ required: true }]}>
            <Input placeholder="请输入服务名称" />
          </Form.Item>

          <Form.Item name="description" label="服务描述">
            <Input.TextArea placeholder="请输入服务描述" rows={4} />
          </Form.Item>

          <Form.Item name="type" label="服务类型" rules={[{ required: true }]}>
            <Select placeholder="请选择服务类型">
              <Option value="query">查询</Option>
              <Option value="update">更新</Option>
              <Option value="delete">删除</Option>
              <Option value="insert">插入</Option>
              <Option value="custom">自定义</Option>
            </Select>
          </Form.Item>

          <Form.Item name="groupId" label="所属分组">
            <Input placeholder="请输入分组ID" />
          </Form.Item>

          <Form.Item name="enabled" label="是否启用" valuePropName="checked">
            <Switch />
          </Form.Item>

          <Form.Item name="cacheTime" label="缓存时间(毫秒)">
            <InputNumber min={0} step={1000} style={{ width: '100%' }} />
          </Form.Item>

          <Divider>标签</Divider>

          <div className={styles.serviceTags}>
            {(service.tags || []).map((tag: string) => (
              <Tag key={tag} closable onClose={() => handleRemoveTag(tag)}>
                {tag}
              </Tag>
            ))}
            {inputVisible ? (
              <Input
                type="text"
                size="small"
                style={{ width: 78 }}
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                onBlur={handleAddTag}
                onPressEnter={handleAddTag}
                autoFocus
              />
            ) : (
              <Tag onClick={() => setInputVisible(true)} className={styles.serviceTagPlus}>
                <PlusOutlined /> 新标签
              </Tag>
            )}
          </div>

          <Divider>参数列表</Divider>

          <Form.List name="parameters">
            {(fields, { add, remove }) => (
              <>
                {fields.map((field) => (
                  <div key={field.key} className={styles.serviceParamItem}>
                    <Form.Item
                      {...field}
                      name={[field.name, 'name']}
                      fieldKey={[field.fieldKey, 'name']}
                      rules={[{ required: true, message: '请输入参数名称' }]}
                    >
                      <Input placeholder="参数名称" />
                    </Form.Item>
                    <Form.Item
                      {...field}
                      name={[field.name, 'type']}
                      fieldKey={[field.fieldKey, 'type']}
                      rules={[{ required: true, message: '请选择参数类型' }]}
                    >
                      <Select placeholder="参数类型">
                        <Option value="string">字符串</Option>
                        <Option value="number">数字</Option>
                        <Option value="boolean">布尔值</Option>
                        <Option value="object">对象</Option>
                        <Option value="array">数组</Option>
                      </Select>
                    </Form.Item>
                    <Form.Item
                      {...field}
                      name={[field.name, 'required']}
                      fieldKey={[field.fieldKey, 'required']}
                      valuePropName="checked"
                    >
                      <Switch checkedChildren="必填" unCheckedChildren="选填" />
                    </Form.Item>
                    <Tooltip title="删除参数">
                      <Button type="text" danger icon={<DeleteOutlined />} onClick={() => remove(field.name)} />
                    </Tooltip>
                  </div>
                ))}
                <Form.Item>
                  <Button
                    type="dashed"
                    onClick={() => add({ name: '', type: 'string', required: false })}
                    block
                    icon={<PlusOutlined />}
                  >
                    添加参数
                  </Button>
                </Form.Item>
              </>
            )}
          </Form.List>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>
              保存参数
            </Button>
          </Form.Item>
        </Form>
      </div>
    </div>
  );
};

export default ServiceParams;
