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

/**
 * 获取服务文档
 */
export async function getServiceDoc(serviceId: string) {
  return request(`/api/data-service/${serviceId}/doc`);
}

/**
 * 获取服务 Swagger 规范
 */
export async function getServiceSwagger(serviceId: string) {
  return request(`/api/data-service/${serviceId}/swagger`);
}

/**
 * 导出服务文档
 */
export async function exportServiceDoc(serviceId: string, format: string) {
  return request(`/api/data-service/${serviceId}/doc/export`, {
    method: 'GET',
    params: { format },
    responseType: 'blob',
  });
}

/**
 * 获取所有服务的 Swagger 规范
 */
export async function getAllServicesSwagger() {
  return request('/api/data-service/swagger');
}
