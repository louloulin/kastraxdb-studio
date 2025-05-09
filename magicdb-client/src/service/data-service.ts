import { request } from 'umi';

// 获取数据服务列表
export async function getServiceList() {
  return request('/api/data-service', {
    method: 'GET',
  });
}

// 获取数据服务详情
export async function getServiceById(id: string) {
  return request(`/api/data-service/${id}`, {
    method: 'GET',
  });
}

// 创建数据服务
export async function createService(data: any) {
  return request('/api/data-service', {
    method: 'POST',
    data,
  });
}

// 更新数据服务
export async function updateService(data: any) {
  return request('/api/data-service', {
    method: 'POST',
    data,
  });
}

// 删除数据服务
export async function deleteService(id: string) {
  return request(`/api/data-service/${id}`, {
    method: 'DELETE',
  });
}

// 执行数据服务
export async function executeService(id: string, params: any) {
  return request(`/api/data-service/${id}/execute`, {
    method: 'POST',
    data: params,
  });
}

// 获取数据服务分组列表
export async function getServiceGroupList() {
  return request('/api/data-service/group', {
    method: 'GET',
  });
}

// 创建数据服务分组
export async function createServiceGroup(data: any) {
  return request('/api/data-service/group', {
    method: 'POST',
    data,
  });
}

// 更新数据服务分组
export async function updateServiceGroup(data: any) {
  return request('/api/data-service/group', {
    method: 'POST',
    data,
  });
}

// 删除数据服务分组
export async function deleteServiceGroup(id: string) {
  return request(`/api/data-service/group/${id}`, {
    method: 'DELETE',
  });
}

// 导出数据服务
export async function exportService(id: string) {
  return request(`/api/data-service/${id}/export`, {
    method: 'GET',
  });
}

// 导入数据服务
export async function importService(data: any) {
  return request('/api/data-service/import', {
    method: 'POST',
    data,
  });
}
