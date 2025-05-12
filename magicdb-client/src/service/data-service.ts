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
export async function updateService(id: string, data: any) {
  return request(`/api/data-service/${id}`, {
    method: 'PUT',
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
export async function updateServiceGroup(id: string, data: any) {
  return request(`/api/data-service/group/${id}`, {
    method: 'PUT',
    data,
  });
}

// 删除数据服务分组
export async function deleteServiceGroup(id: string) {
  return request(`/api/data-service/group/${id}`, {
    method: 'DELETE',
  });
}

// 获取数据服务历史记录
export async function getServiceHistory(id: string) {
  return request(`/api/data-service/${id}/history`, {
    method: 'GET',
  });
}

// 恢复数据服务版本
export async function restoreServiceVersion(serviceId: string, versionId: string) {
  return request(`/api/data-service/${serviceId}/history/${versionId}/restore`, {
    method: 'POST',
  });
}

// 导出数据服务
export async function exportService(id: string, options: any = {}) {
  return request(`/api/data-service/${id}/export`, {
    method: 'POST',
    data: options,
  });
}

// 导入数据服务
export async function importService(data: any) {
  return request('/api/data-service/import', {
    method: 'POST',
    data,
  });
}

// 验证数据服务参数
export async function validateParameters(id: string, params: any) {
  return request(`/api/data-service/${id}/validate`, {
    method: 'POST',
    data: params,
  });
}

// 获取根分组
export async function getRootGroups() {
  return request('/api/data-service/group/root', {
    method: 'GET',
  });
}

// 获取分组下的服务
export async function getServicesByGroup(groupId: string) {
  return request(`/api/data-service/group/${groupId}/services`, {
    method: 'GET',
  });
}

// 直接执行脚本
export async function executeScript(language: string, script: string, parameters: any) {
  return request('/api/data-service/execute-script', {
    method: 'POST',
    params: {
      language,
    },
    data: {
      script,
      parameters,
    },
  });
}