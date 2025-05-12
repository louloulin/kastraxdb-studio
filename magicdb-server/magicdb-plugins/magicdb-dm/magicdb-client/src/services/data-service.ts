import { request } from '@/utils/request';

/**
 * 获取服务列表
 */
export async function getServiceList(params?: {
  groupId?: string;
  keyword?: string;
  page?: number;
  pageSize?: number;
}) {
  return request<any[]>('/api/data-service/list', {
    method: 'GET',
    params,
  });
}

/**
 * 获取服务详情
 */
export async function getServiceDetail(id: string) {
  return request<any>(`/api/data-service/${id}`);
}

/**
 * 创建服务
 */
export async function createService(data: any) {
  return request<string>('/api/data-service', {
    method: 'POST',
    data,
  });
}

/**
 * 更新服务
 */
export async function updateService(id: string, data: any) {
  return request<boolean>(`/api/data-service/${id}`, {
    method: 'PUT',
    data,
  });
}

/**
 * 删除服务
 */
export async function deleteService(id: string) {
  return request<boolean>(`/api/data-service/${id}`, {
    method: 'DELETE',
  });
}

/**
 * 执行服务
 */
export async function executeService(id: string, params: any) {
  return request<any>(`/api/data-service/${id}/execute`, {
    method: 'POST',
    data: params,
  });
}

/**
 * 导出服务
 */
export async function exportServices(ids: string[]) {
  return request<any[]>('/api/data-service/export', {
    method: 'POST',
    data: { ids },
  });
}

/**
 * 导入服务
 */
export async function importServices(services: any[]) {
  return request<number>('/api/data-service/import', {
    method: 'POST',
    data: { services },
  });
}

/**
 * 批量删除服务
 */
export async function batchDeleteServices(ids: string[]) {
  return request<number>('/api/data-service/batch-delete', {
    method: 'POST',
    data: { ids },
  });
}

/**
 * 批量更新服务状态
 */
export async function batchUpdateServiceStatus(ids: string[], enabled: boolean) {
  return request<number>('/api/data-service/batch-update-status', {
    method: 'POST',
    data: { ids, enabled },
  });
}
