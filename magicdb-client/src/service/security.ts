import { request } from 'umi';

// 安全审计相关API

// 获取安全审计记录列表
export async function getAuditRecords(params: any) {
  return request('/api/data-service/security/audit/list', {
    method: 'GET',
    params,
  });
}

// 获取安全审计记录详情
export async function getAuditRecord(id: string) {
  return request(`/api/data-service/security/audit/${id}`, {
    method: 'GET',
  });
}

// 审核安全审计记录
export async function reviewAuditRecord(id: string, reviewerId: string, reviewNotes?: string) {
  return request(`/api/data-service/security/audit/${id}/review`, {
    method: 'POST',
    params: {
      reviewerId,
      reviewNotes,
    },
  });
}

// 批量审核安全审计记录
export async function batchReviewAuditRecords(ids: string[], reviewerId: string, reviewNotes?: string) {
  return request('/api/data-service/security/audit/batch-review', {
    method: 'POST',
    data: ids,
    params: {
      reviewerId,
      reviewNotes,
    },
  });
}

// 删除安全审计记录
export async function deleteAuditRecord(id: string) {
  return request(`/api/data-service/security/audit/${id}`, {
    method: 'DELETE',
  });
}

// 批量删除安全审计记录
export async function batchDeleteAuditRecords(ids: string[]) {
  return request('/api/data-service/security/audit/batch', {
    method: 'DELETE',
    data: ids,
  });
}

// 清理过期的安全审计记录
export async function cleanupAuditRecords(beforeTime: string) {
  return request('/api/data-service/security/audit/cleanup', {
    method: 'DELETE',
    params: {
      beforeTime,
    },
  });
}

// 导出安全审计记录
export async function exportAuditRecords(ids?: string[], format: string = 'CSV') {
  return request('/api/data-service/security/audit/export', {
    method: 'GET',
    params: {
      ids,
      format,
    },
    responseType: 'blob',
  });
}

// 获取安全审计统计信息
export async function getAuditStatistics(startTime?: string, endTime?: string) {
  return request('/api/data-service/security/audit/statistics', {
    method: 'GET',
    params: {
      startTime,
      endTime,
    },
  });
}

// 漏洞扫描相关API

// 启动漏洞扫描
export async function startVulnerabilityScan(params: any) {
  return request('/api/data-service/security/vulnerability/scan', {
    method: 'POST',
    params,
  });
}

// 获取扫描任务状态
export async function getVulnerabilityScanStatus(scanId: string) {
  return request(`/api/data-service/security/vulnerability/scan/${scanId}/status`, {
    method: 'GET',
  });
}

// 获取漏洞记录详情
export async function getVulnerabilityRecord(id: string) {
  return request(`/api/data-service/security/vulnerability/${id}`, {
    method: 'GET',
  });
}

// 获取漏洞记录列表
export async function getVulnerabilityRecords(params: any) {
  return request('/api/data-service/security/vulnerability/list', {
    method: 'GET',
    params,
  });
}

// 修复漏洞
export async function fixVulnerability(id: string, fixedBy: string, fixMethod?: string, fixNotes?: string, needRescan: boolean = false) {
  return request(`/api/data-service/security/vulnerability/${id}/fix`, {
    method: 'POST',
    params: {
      fixedBy,
      fixMethod,
      fixNotes,
      needRescan,
    },
  });
}

// 批量修复漏洞
export async function batchFixVulnerabilities(ids: string[], fixedBy: string, fixMethod?: string, fixNotes?: string, needRescan: boolean = false) {
  return request('/api/data-service/security/vulnerability/batch-fix', {
    method: 'POST',
    data: ids,
    params: {
      fixedBy,
      fixMethod,
      fixNotes,
      needRescan,
    },
  });
}

// 删除漏洞记录
export async function deleteVulnerabilityRecord(id: string) {
  return request(`/api/data-service/security/vulnerability/${id}`, {
    method: 'DELETE',
  });
}

// 批量删除漏洞记录
export async function batchDeleteVulnerabilityRecords(ids: string[]) {
  return request('/api/data-service/security/vulnerability/batch', {
    method: 'DELETE',
    data: ids,
  });
}

// 导出漏洞记录
export async function exportVulnerabilityRecords(ids?: string[], format: string = 'CSV') {
  return request('/api/data-service/security/vulnerability/export', {
    method: 'GET',
    params: {
      ids,
      format,
    },
    responseType: 'blob',
  });
}

// 获取漏洞统计信息
export async function getVulnerabilityStatistics(startTime?: string, endTime?: string) {
  return request('/api/data-service/security/vulnerability/statistics', {
    method: 'GET',
    params: {
      startTime,
      endTime,
    },
  });
}

// 获取漏洞修复建议
export async function getVulnerabilityFixSuggestions(id: string) {
  return request(`/api/data-service/security/vulnerability/${id}/fix-suggestions`, {
    method: 'GET',
  });
}

// 自动修复漏洞
export async function autoFixVulnerability(id: string, fixedBy: string) {
  return request(`/api/data-service/security/vulnerability/${id}/auto-fix`, {
    method: 'POST',
    params: {
      fixedBy,
    },
  });
}
