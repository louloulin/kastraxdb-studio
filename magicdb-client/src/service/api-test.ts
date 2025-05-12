import { request } from 'umi';

// 测试API
export async function testApi(data: any) {
  const { method, url, params, headers, body } = data;

  // 解析参数
  let parsedParams = {};
  if (params) {
    try {
      parsedParams = typeof params === 'string' ? JSON.parse(params) : params;
    } catch (error) {
      console.error('解析参数出错:', error);
    }
  }

  // 解析请求头
  let parsedHeaders = {};
  if (headers) {
    try {
      parsedHeaders = typeof headers === 'string' ? JSON.parse(headers) : headers;
    } catch (error) {
      console.error('解析请求头出错:', error);
    }
  }

  // 解析请求体
  let parsedBody = undefined;
  if (body) {
    try {
      parsedBody = typeof body === 'string' ? JSON.parse(body) : body;
    } catch (error) {
      // 如果不是JSON格式，直接使用原始字符串
      parsedBody = body;
    }
  }

  return request(url, {
    method,
    params: parsedParams,
    headers: parsedHeaders,
    data: parsedBody,
    getResponse: true, // 获取完整响应
    errorHandler: (error) => {
      // 自定义错误处理，返回错误响应
      if (error.response) {
        return {
          status: error.response.status,
          statusText: error.response.statusText,
          headers: error.response.headers,
          data: error.data,
        };
      }
      return {
        status: 0,
        statusText: error.message || '请求错误',
        headers: {},
        data: { error: error.message || '请求错误' },
      };
    },
  });
}

/**
 * 执行 API 测试
 */
export async function executeTest(serviceId: string, parameters: Record<string, any>) {
  return request(`/api/data-service/${serviceId}/test`, {
    method: 'POST',
    data: parameters
  });
}

/**
 * 验证参数
 */
export async function validateParameters(serviceId: string, parameters: Record<string, any>) {
  return request(`/api/data-service/${serviceId}/validate`, {
    method: 'POST',
    data: parameters
  });
}

/**
 * 保存测试用例
 */
export async function saveTestCase(testCase: {
  id?: string;
  name: string;
  serviceId: string;
  parameters: Record<string, any>;
  description?: string;
  tags?: string[];
}) {
  return request('/api/data-service/test-case', {
    method: 'POST',
    data: testCase
  });
}

/**
 * 获取测试用例
 */
export async function getTestCase(id: string) {
  return request(`/api/data-service/test-case/${id}`);
}

/**
 * 获取服务的所有测试用例
 */
export async function getTestCasesByService(serviceId: string) {
  return request(`/api/data-service/${serviceId}/test-cases`);
}

/**
 * 删除测试用例
 */
export async function deleteTestCase(id: string) {
  return request(`/api/data-service/test-case/${id}`, {
    method: 'DELETE'
  });
}
