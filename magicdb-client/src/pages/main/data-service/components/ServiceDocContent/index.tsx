import React, { useState } from 'react';
import { Tabs, Table, Card, Tag, Divider, Typography, Button, Space } from 'antd';
import { CopyOutlined, CheckOutlined } from '@ant-design/icons';
import { executeService } from '@/service/data-service';
import ServiceTest from '../ServiceTest';
import SwaggerUI from 'swagger-ui-react';
import 'swagger-ui-react/swagger-ui.css';
import ReactMarkdown from 'react-markdown';
import i18n from '@/i18n';
import styles from './index.less';

const { TabPane } = Tabs;
const { Title, Paragraph, Text } = Typography;

interface ServiceDocContentProps {
  service: any;
  serviceDoc: any;
  swaggerSpec: string;
}

const ServiceDocContent: React.FC<ServiceDocContentProps> = ({ 
  service, 
  serviceDoc, 
  swaggerSpec 
}) => {
  const [activeTab, setActiveTab] = useState<string>('info');
  const [copied, setCopied] = useState<boolean>(false);

  if (!service) {
    return null;
  }

  const handleCopyCode = (code: string) => {
    navigator.clipboard.writeText(code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const renderBasicInfo = () => {
    if (!serviceDoc) {
      return (
        <Card title={i18n('data-service.doc.basic-info')}>
          <Paragraph>{service.description}</Paragraph>
          <Divider />
          <div className={styles.infoItem}>
            <Text strong>{i18n('data-service.doc.service-id')}:</Text>
            <Text>{service.id}</Text>
          </div>
          <div className={styles.infoItem}>
            <Text strong>{i18n('data-service.doc.service-type')}:</Text>
            <Text>{service.type}</Text>
          </div>
          <div className={styles.infoItem}>
            <Text strong>{i18n('data-service.doc.path')}:</Text>
            <Text>{`/api/data-service/${service.id}/execute`}</Text>
          </div>
          <div className={styles.infoItem}>
            <Text strong>{i18n('data-service.doc.method')}:</Text>
            <Text>POST</Text>
          </div>
          {service.tags && service.tags.length > 0 && (
            <div className={styles.infoItem}>
              <Text strong>{i18n('data-service.doc.tags')}:</Text>
              <div>
                {service.tags.map((tag: string) => (
                  <Tag key={tag}>{tag}</Tag>
                ))}
              </div>
            </div>
          )}
        </Card>
      );
    }

    return (
      <Card title={i18n('data-service.doc.basic-info')}>
        <Paragraph>{serviceDoc.description || service.description}</Paragraph>
        <Divider />
        <div className={styles.infoItem}>
          <Text strong>{i18n('data-service.doc.service-id')}:</Text>
          <Text>{serviceDoc.serviceId}</Text>
        </div>
        <div className={styles.infoItem}>
          <Text strong>{i18n('data-service.doc.service-type')}:</Text>
          <Text>{serviceDoc.serviceType}</Text>
        </div>
        <div className={styles.infoItem}>
          <Text strong>{i18n('data-service.doc.path')}:</Text>
          <Text>{serviceDoc.path}</Text>
        </div>
        <div className={styles.infoItem}>
          <Text strong>{i18n('data-service.doc.method')}:</Text>
          <Text>{serviceDoc.method}</Text>
        </div>
        {serviceDoc.tags && serviceDoc.tags.length > 0 && (
          <div className={styles.infoItem}>
            <Text strong>{i18n('data-service.doc.tags')}:</Text>
            <div>
              {serviceDoc.tags.map((tag: string) => (
                <Tag key={tag}>{tag}</Tag>
              ))}
            </div>
          </div>
        )}
      </Card>
    );
  };

  const renderParameters = () => {
    const parameters = serviceDoc?.parameters || service.parameters || [];
    
    const columns = [
      {
        title: i18n('data-service.doc.param-name'),
        dataIndex: 'name',
        key: 'name',
      },
      {
        title: i18n('data-service.doc.param-type'),
        dataIndex: 'type',
        key: 'type',
        render: (text: string) => <Tag>{text}</Tag>,
      },
      {
        title: i18n('data-service.doc.param-required'),
        dataIndex: 'required',
        key: 'required',
        render: (required: boolean) => (
          required ? <Tag color="red">必填</Tag> : <Tag color="green">可选</Tag>
        ),
      },
      {
        title: i18n('data-service.doc.param-default'),
        dataIndex: 'defaultValue',
        key: 'defaultValue',
      },
      {
        title: i18n('data-service.doc.param-desc'),
        dataIndex: 'description',
        key: 'description',
      },
    ];
    
    return (
      <Card title={i18n('data-service.doc.parameters')}>
        {parameters.length > 0 ? (
          <Table 
            dataSource={parameters} 
            columns={columns} 
            rowKey="name" 
            pagination={false} 
            size="small"
          />
        ) : (
          <Paragraph>{i18n('data-service.doc.no-parameters')}</Paragraph>
        )}
      </Card>
    );
  };

  const renderReturnFields = () => {
    if (!serviceDoc || !serviceDoc.returnFields || serviceDoc.returnFields.length === 0) {
      return (
        <Card title={i18n('data-service.doc.return-fields')}>
          <Paragraph>{i18n('data-service.doc.no-return-fields')}</Paragraph>
        </Card>
      );
    }
    
    const columns = [
      {
        title: i18n('data-service.doc.field-name'),
        dataIndex: 'name',
        key: 'name',
      },
      {
        title: i18n('data-service.doc.field-type'),
        dataIndex: 'type',
        key: 'type',
        render: (text: string) => <Tag>{text}</Tag>,
      },
      {
        title: i18n('data-service.doc.field-desc'),
        dataIndex: 'description',
        key: 'description',
      },
      {
        title: i18n('data-service.doc.field-example'),
        dataIndex: 'example',
        key: 'example',
      },
    ];
    
    return (
      <Card title={i18n('data-service.doc.return-fields')}>
        <Table 
          dataSource={serviceDoc.returnFields} 
          columns={columns} 
          rowKey="name" 
          pagination={false} 
          size="small"
        />
      </Card>
    );
  };

  const renderExamples = () => {
    if (!serviceDoc || !serviceDoc.examples || serviceDoc.examples.length === 0) {
      return (
        <Card title={i18n('data-service.doc.examples')}>
          <Paragraph>{i18n('data-service.doc.no-examples')}</Paragraph>
        </Card>
      );
    }
    
    return (
      <div className={styles.examples}>
        {serviceDoc.examples.map((example: any, index: number) => (
          <Card 
            key={index} 
            title={example.name} 
            className={styles.exampleCard}
          >
            {example.description && (
              <Paragraph>{example.description}</Paragraph>
            )}
            
            <Title level={5}>{i18n('data-service.doc.request')}</Title>
            <div className={styles.codeBlock}>
              <pre>{example.request}</pre>
              <Button 
                type="text" 
                icon={copied ? <CheckOutlined /> : <CopyOutlined />} 
                className={styles.copyButton}
                onClick={() => handleCopyCode(example.request)}
              />
            </div>
            
            <Title level={5}>{i18n('data-service.doc.response')}</Title>
            <div className={styles.codeBlock}>
              <pre>{example.response}</pre>
              <Button 
                type="text" 
                icon={copied ? <CheckOutlined /> : <CopyOutlined />} 
                className={styles.copyButton}
                onClick={() => handleCopyCode(example.response)}
              />
            </div>
          </Card>
        ))}
      </div>
    );
  };

  const renderNotes = () => {
    if (!serviceDoc || !serviceDoc.notes) {
      return null;
    }
    
    return (
      <Card title={i18n('data-service.doc.notes')}>
        <ReactMarkdown>{serviceDoc.notes}</ReactMarkdown>
      </Card>
    );
  };

  const renderCodeSamples = () => {
    const curlSample = `curl -X POST "${window.location.origin}/api/data-service/${service.id}/execute" \\
  -H "Content-Type: application/json" \\
  -d '${JSON.stringify(generateSampleParams(), null, 2)}'`;
    
    const jsSample = `// 使用 fetch API
fetch("${window.location.origin}/api/data-service/${service.id}/execute", {
  method: "POST",
  headers: {
    "Content-Type": "application/json",
  },
  body: JSON.stringify(${JSON.stringify(generateSampleParams(), null, 2)}),
})
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error("Error:", error));`;
    
    const javaSample = `// 使用 OkHttp
OkHttpClient client = new OkHttpClient();
MediaType mediaType = MediaType.parse("application/json");
RequestBody body = RequestBody.create(mediaType, "${JSON.stringify(generateSampleParams()).replace(/"/g, '\\"')}");
Request request = new Request.Builder()
  .url("${window.location.origin}/api/data-service/${service.id}/execute")
  .post(body)
  .addHeader("Content-Type", "application/json")
  .build();
Response response = client.newCall(request).execute();`;
    
    const pythonSample = `# 使用 requests
import requests
import json

url = "${window.location.origin}/api/data-service/${service.id}/execute"
payload = ${JSON.stringify(generateSampleParams(), null, 2)}
headers = {"Content-Type": "application/json"}

response = requests.post(url, json=payload, headers=headers)
data = response.json()
print(data)`;
    
    return (
      <Tabs defaultActiveKey="curl">
        <TabPane tab="cURL" key="curl">
          <div className={styles.codeBlock}>
            <pre>{curlSample}</pre>
            <Button 
              type="text" 
              icon={copied ? <CheckOutlined /> : <CopyOutlined />} 
              className={styles.copyButton}
              onClick={() => handleCopyCode(curlSample)}
            />
          </div>
        </TabPane>
        <TabPane tab="JavaScript" key="js">
          <div className={styles.codeBlock}>
            <pre>{jsSample}</pre>
            <Button 
              type="text" 
              icon={copied ? <CheckOutlined /> : <CopyOutlined />} 
              className={styles.copyButton}
              onClick={() => handleCopyCode(jsSample)}
            />
          </div>
        </TabPane>
        <TabPane tab="Java" key="java">
          <div className={styles.codeBlock}>
            <pre>{javaSample}</pre>
            <Button 
              type="text" 
              icon={copied ? <CheckOutlined /> : <CopyOutlined />} 
              className={styles.copyButton}
              onClick={() => handleCopyCode(javaSample)}
            />
          </div>
        </TabPane>
        <TabPane tab="Python" key="python">
          <div className={styles.codeBlock}>
            <pre>{pythonSample}</pre>
            <Button 
              type="text" 
              icon={copied ? <CheckOutlined /> : <CopyOutlined />} 
              className={styles.copyButton}
              onClick={() => handleCopyCode(pythonSample)}
            />
          </div>
        </TabPane>
      </Tabs>
    );
  };

  const generateSampleParams = () => {
    const params: Record<string, any> = {};
    const parameters = serviceDoc?.parameters || service.parameters || [];
    
    parameters.forEach((param: any) => {
      if (param.example) {
        params[param.name] = param.example;
      } else if (param.defaultValue) {
        params[param.name] = param.defaultValue;
      } else {
        // 根据类型生成示例值
        switch (param.type.toLowerCase()) {
          case 'string':
            params[param.name] = 'example';
            break;
          case 'number':
          case 'integer':
            params[param.name] = 123;
            break;
          case 'boolean':
            params[param.name] = true;
            break;
          case 'array':
            params[param.name] = [];
            break;
          case 'object':
            params[param.name] = {};
            break;
          default:
            params[param.name] = 'example';
        }
      }
    });
    
    return params;
  };

  const renderSwaggerUI = () => {
    if (!swaggerSpec) {
      return (
        <div className={styles.swaggerEmpty}>
          <Paragraph>{i18n('data-service.doc.no-swagger')}</Paragraph>
        </div>
      );
    }
    
    try {
      const spec = typeof swaggerSpec === 'string' ? JSON.parse(swaggerSpec) : swaggerSpec;
      return <SwaggerUI spec={spec} />;
    } catch (error) {
      console.error('Failed to parse Swagger spec:', error);
      return (
        <div className={styles.swaggerEmpty}>
          <Paragraph>{i18n('data-service.doc.invalid-swagger')}</Paragraph>
        </div>
      );
    }
  };

  return (
    <div className={styles.docContent}>
      <Tabs activeKey={activeTab} onChange={setActiveTab} className={styles.tabs}>
        <TabPane tab={i18n('data-service.doc.tab-info')} key="info">
          <div className={styles.tabContent}>
            <Space direction="vertical" size="large" style={{ width: '100%' }}>
              {renderBasicInfo()}
              {renderParameters()}
              {renderReturnFields()}
              {renderExamples()}
              {renderNotes()}
            </Space>
          </div>
        </TabPane>
        <TabPane tab={i18n('data-service.doc.tab-code')} key="code">
          <div className={styles.tabContent}>
            <Card title={i18n('data-service.doc.code-samples')}>
              {renderCodeSamples()}
            </Card>
          </div>
        </TabPane>
        <TabPane tab={i18n('data-service.doc.tab-swagger')} key="swagger">
          <div className={styles.tabContent}>
            {renderSwaggerUI()}
          </div>
        </TabPane>
        <TabPane tab={i18n('data-service.doc.tab-test')} key="test">
          <div className={styles.tabContent}>
            <ServiceTest serviceId={service.id} />
          </div>
        </TabPane>
      </Tabs>
    </div>
  );
};

export default ServiceDocContent;
