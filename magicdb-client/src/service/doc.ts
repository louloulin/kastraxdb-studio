import { request } from 'umi';

// 获取API列表
export async function getApiList() {
  return request('/api/doc/api', {
    method: 'GET',
  });
}

// 获取API详情
export async function getApiDetail(id: string) {
  return request(`/api/doc/api/${id}`, {
    method: 'GET',
  });
}

// 获取Swagger文档
export async function getSwaggerDoc() {
  return request('/api/doc/swagger', {
    method: 'GET',
  });
}

// 导出API文档
export async function exportApiDoc(format: string) {
  return request('/api/doc/export', {
    method: 'GET',
    params: { format },
  });
}
