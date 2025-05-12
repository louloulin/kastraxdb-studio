import { request } from '@/utils/request';

/**
 * 服务统计信息
 */
export interface ServiceStatistics {
  serviceId: string;
  serviceName: string;
  totalExecutions: number;
  successfulExecutions: number;
  failedExecutions: number;
  averageExecutionTime: number;
  maxExecutionTime: number;
  minExecutionTime: number;
  lastExecutionTime: string;
}

/**
 * 执行记录
 */
export interface ExecutionRecord {
  id: string;
  serviceId: string;
  serviceName: string;
  executionTime: number;
  success: boolean;
  errorMessage?: string;
  timestamp: string;
}

/**
 * 错误统计
 */
export interface ErrorStatistics {
  serviceId: string;
  serviceName: string;
  errorMessage: string;
  occurrences: number;
  firstOccurrence: string;
  lastOccurrence: string;
}

/**
 * 获取服务统计
 */
export async function getServiceStatistics(params?: {
  serviceId?: string;
  startTime?: string;
  endTime?: string;
}) {
  return request<ServiceStatistics[]>('/api/data-service/monitoring/statistics', {
    method: 'GET',
    params,
  });
}

/**
 * 获取执行历史
 */
export async function getExecutionHistory(params?: {
  serviceId?: string;
  startTime?: string;
  endTime?: string;
  limit?: number;
  offset?: number;
}) {
  return request<ExecutionRecord[]>('/api/data-service/monitoring/history', {
    method: 'GET',
    params,
  });
}

/**
 * 获取错误统计
 */
export async function getErrorStatistics(params?: {
  serviceId?: string;
  startTime?: string;
  endTime?: string;
}) {
  return request<ErrorStatistics[]>('/api/data-service/monitoring/errors', {
    method: 'GET',
    params,
  });
}

/**
 * 清除执行历史
 */
export async function clearExecutionHistory(params?: {
  serviceId?: string;
  before?: string;
}) {
  return request<number>('/api/data-service/monitoring/history', {
    method: 'DELETE',
    params,
  });
}
