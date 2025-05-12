
import { extend } from 'umi-request';

// 创建一个 umi-request 实例
const request = extend({
  prefix: '',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
request.interceptors.request.use((url, options) => {
  const token = localStorage.getItem('token');
  if (token) {
    options.headers = {
      ...options.headers,
      Authorization: `Bearer ${token}`,
    };
  }
  return { url, options };
});

// 响应拦截器
request.interceptors.response.use(async (response) => {
  const data = await response.clone().json();
  
  // 处理错误响应
  if (!data.success) {
    console.error('API request failed:', data);
  }
  
  return response;
});

export default request;
