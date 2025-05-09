import React, { useState } from 'react';
import { Form, Input, Button, Switch, Select, Divider, Tag, Tooltip, message } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { updateScript } from '@/service/script';
import styles from './index.less';

const { Option } = Select;

interface ScriptParamsProps {
  script: any;
  onSave: (script: any) => void;
}

const ScriptParams: React.FC<ScriptParamsProps> = ({ script, onSave }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(false);
  const [inputVisible, setInputVisible] = useState<boolean>(false);
  const [inputValue, setInputValue] = useState<string>('');
  const [messageApi, contextHolder] = message.useMessage();

  // 如果脚本不存在，显示空内容
  if (!script) {
    return (
      <div className={styles.scriptParamsEmpty}>
        请选择一个脚本
      </div>
    );
  }

  // 处理保存参数
  const handleSaveParams = async (values: any) => {
    try {
      setLoading(true);
      const updatedScript = {
        ...script,
        ...values,
      };
      
      const response = await updateScript(updatedScript);

      if (response && response.success) {
        messageApi.success('保存成功');
        onSave(response.data);
      } else {
        messageApi.error('保存失败');
      }
    } catch (error) {
      console.error('保存参数出错:', error);
      messageApi.error('保存参数出错');
    } finally {
      setLoading(false);
    }
  };

  // 处理添加标签
  const handleAddTag = () => {
    if (inputValue && !script.tags.includes(inputValue)) {
      const tags = [...(script.tags || []), inputValue];
      updateScript({ ...script, tags })
        .then(response => {
          if (response && response.success) {
            messageApi.success('添加标签成功');
            onSave(response.data);
          } else {
            messageApi.error('添加标签失败');
          }
        })
        .catch(error => {
          console.error('添加标签出错:', error);
          messageApi.error('添加标签出错');
        });
    }
    setInputVisible(false);
    setInputValue('');
  };

  // 处理删除标签
  const handleRemoveTag = (tag: string) => {
    const tags = (script.tags || []).filter((t: string) => t !== tag);
    updateScript({ ...script, tags })
      .then(response => {
        if (response && response.success) {
          messageApi.success('删除标签成功');
          onSave(response.data);
        } else {
          messageApi.error('删除标签失败');
        }
      })
      .catch(error => {
        console.error('删除标签出错:', error);
        messageApi.error('删除标签出错');
      });
  };

  return (
    <div className={styles.scriptParams}>
      {contextHolder}
      <div className={styles.scriptParamsHeader}>
        <h3>脚本参数</h3>
      </div>
      <div className={styles.scriptParamsContent}>
        <Form
          form={form}
          layout="vertical"
          initialValues={script}
          onFinish={handleSaveParams}
        >
          <Form.Item name="name" label="脚本名称" rules={[{ required: true }]}>
            <Input placeholder="请输入脚本名称" />
          </Form.Item>
          
          <Form.Item name="description" label="脚本描述">
            <Input.TextArea placeholder="请输入脚本描述" rows={4} />
          </Form.Item>
          
          <Form.Item name="groupId" label="所属分组">
            <Input placeholder="请输入分组ID" />
          </Form.Item>
          
          <Form.Item name="enabled" label="是否启用" valuePropName="checked">
            <Switch />
          </Form.Item>
          
          <Divider>标签</Divider>
          
          <div className={styles.scriptTags}>
            {(script.tags || []).map((tag: string) => (
              <Tag
                key={tag}
                closable
                onClose={() => handleRemoveTag(tag)}
              >
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
              <Tag onClick={() => setInputVisible(true)} className={styles.scriptTagPlus}>
                <PlusOutlined /> 新标签
              </Tag>
            )}
          </div>
          
          <Divider>参数列表</Divider>
          
          <Form.List name="parameters">
            {(fields, { add, remove }) => (
              <>
                {fields.map(field => (
                  <div key={field.key} className={styles.scriptParamItem}>
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
                      <Button
                        type="text"
                        danger
                        icon={<DeleteOutlined />}
                        onClick={() => remove(field.name)}
                      />
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

export default ScriptParams;
