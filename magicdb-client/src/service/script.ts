import { request } from 'umi';

// 获取脚本列表
export async function getScriptList() {
  return request('/api/script', {
    method: 'GET',
  });
}

// 获取脚本详情
export async function getScriptById(id: string) {
  return request(`/api/script/${id}`, {
    method: 'GET',
  });
}

// 创建脚本
export async function createScript(data: any) {
  return request('/api/script', {
    method: 'POST',
    data,
  });
}

// 更新脚本
export async function updateScript(data: any) {
  return request('/api/script', {
    method: 'POST',
    data,
  });
}

// 删除脚本
export async function deleteScript(id: string) {
  return request(`/api/script/${id}`, {
    method: 'DELETE',
  });
}

// 执行脚本
export async function executeScript(id: string, params: any) {
  return request(`/api/script/${id}/execute`, {
    method: 'POST',
    data: params,
  });
}

// 验证脚本
export async function validateScript(script: string, language: string) {
  return request('/api/script/validate', {
    method: 'POST',
    params: { language },
    data: script,
  });
}

// 获取脚本版本列表
export async function getScriptVersions(scriptId: string) {
  return request(`/api/script/${scriptId}/version`, {
    method: 'GET',
  });
}

// 获取脚本版本详情
export async function getScriptVersion(scriptId: string, version: number) {
  return request(`/api/script/${scriptId}/version/${version}`, {
    method: 'GET',
  });
}

// 创建脚本版本
export async function createScriptVersion(scriptId: string, description: string, creator?: string) {
  return request(`/api/script/${scriptId}/version`, {
    method: 'POST',
    params: { description, creator },
  });
}

// 切换脚本版本
export async function switchScriptVersion(scriptId: string, version: number) {
  return request(`/api/script/${scriptId}/version/${version}`, {
    method: 'PUT',
  });
}

// 比较脚本版本
export async function compareScriptVersions(scriptId: string, fromVersion: number, toVersion: number) {
  return request(`/api/script/${scriptId}/version/${fromVersion}/compare/${toVersion}`, {
    method: 'GET',
  });
}

// 获取脚本分组列表
export async function getScriptGroupList() {
  return request('/api/script/group', {
    method: 'GET',
  });
}

// 创建脚本分组
export async function createScriptGroup(data: any) {
  return request('/api/script/group', {
    method: 'POST',
    data,
  });
}

// 更新脚本分组
export async function updateScriptGroup(data: any) {
  return request('/api/script/group', {
    method: 'POST',
    data,
  });
}

// 删除脚本分组
export async function deleteScriptGroup(id: string) {
  return request(`/api/script/group/${id}`, {
    method: 'DELETE',
  });
}

// 获取脚本依赖
export async function getScriptDependencies(scriptId: string) {
  return request(`/api/script/${scriptId}/dependency`, {
    method: 'GET',
  });
}

// 添加脚本依赖
export async function addScriptDependency(scriptId: string, dependencyId: string) {
  return request(`/api/script/${scriptId}/dependency/${dependencyId}`, {
    method: 'POST',
  });
}

// 删除脚本依赖
export async function removeScriptDependency(scriptId: string, dependencyId: string) {
  return request(`/api/script/${scriptId}/dependency/${dependencyId}`, {
    method: 'DELETE',
  });
}

// 导出脚本
export async function exportScript(scriptId: string) {
  return request(`/api/script/${scriptId}/export`, {
    method: 'GET',
  });
}

// 导入脚本
export async function importScript(data: any) {
  return request('/api/script/import', {
    method: 'POST',
    data,
  });
}
