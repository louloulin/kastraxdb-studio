import { request } from '@/utils/request';

/**
 * 断点信息
 */
export interface BreakpointInfo {
  id?: string;
  line: number;
  column: number;
  enabled?: boolean;
  condition?: string;
  hitCount?: number;
}

/**
 * 变量信息
 */
export interface VariableInfo {
  name: string;
  type: string;
  value: any;
}

/**
 * 调试会话状态
 */
export enum DebugSessionStatus {
  CREATED = 'CREATED',
  RUNNING = 'RUNNING',
  PAUSED = 'PAUSED',
  COMPLETED = 'COMPLETED',
  STOPPED = 'STOPPED',
  ERROR = 'ERROR',
}

/**
 * 调试会话
 */
export interface DebugSession {
  id: string;
  serviceId: string;
  status: DebugSessionStatus;
  currentLine: number;
  currentColumn: number;
  breakpoints: BreakpointInfo[];
  callStack: any[];
  result?: any;
  errorMessage?: string;
  createTime: string;
}

/**
 * 调试步骤结果
 */
export interface DebugStepResult {
  sessionId: string;
  status: DebugSessionStatus;
  currentLine: number;
  currentColumn: number;
  variables: VariableInfo[];
  callStack: any[];
  result?: any;
  errorMessage?: string;
}

/**
 * 创建调试会话
 */
export async function createDebugSession(serviceId: string, parameters: Record<string, any> = {}) {
  return request<DebugSession>('/api/script-debug/session', {
    method: 'POST',
    data: {
      serviceId,
      parameters,
    },
  });
}

/**
 * 获取调试会话
 */
export async function getDebugSession(sessionId: string) {
  return request<DebugSession>(`/api/script-debug/session/${sessionId}`);
}

/**
 * 结束调试会话
 */
export async function terminateDebugSession(sessionId: string) {
  return request<boolean>(`/api/script-debug/session/${sessionId}`, {
    method: 'DELETE',
  });
}

/**
 * 添加断点
 */
export async function addBreakpoint(sessionId: string, breakpoint: BreakpointInfo) {
  return request<boolean>(`/api/script-debug/session/${sessionId}/breakpoint`, {
    method: 'POST',
    data: breakpoint,
  });
}

/**
 * 删除断点
 */
export async function removeBreakpoint(sessionId: string, breakpointId: string) {
  return request<boolean>(`/api/script-debug/session/${sessionId}/breakpoint/${breakpointId}`, {
    method: 'DELETE',
  });
}

/**
 * 获取所有断点
 */
export async function getBreakpoints(sessionId: string) {
  return request<BreakpointInfo[]>(`/api/script-debug/session/${sessionId}/breakpoints`);
}

/**
 * 单步执行
 */
export async function stepOver(sessionId: string) {
  return request<DebugStepResult>(`/api/script-debug/session/${sessionId}/step-over`, {
    method: 'POST',
  });
}

/**
 * 单步进入
 */
export async function stepInto(sessionId: string) {
  return request<DebugStepResult>(`/api/script-debug/session/${sessionId}/step-into`, {
    method: 'POST',
  });
}

/**
 * 单步跳出
 */
export async function stepOut(sessionId: string) {
  return request<DebugStepResult>(`/api/script-debug/session/${sessionId}/step-out`, {
    method: 'POST',
  });
}

/**
 * 继续执行
 */
export async function continueExecution(sessionId: string) {
  return request<DebugStepResult>(`/api/script-debug/session/${sessionId}/continue`, {
    method: 'POST',
  });
}

/**
 * 获取变量
 */
export async function getVariables(sessionId: string) {
  return request<VariableInfo[]>(`/api/script-debug/session/${sessionId}/variables`);
}

/**
 * 获取调用栈
 */
export async function getCallStack(sessionId: string) {
  return request<any[]>(`/api/script-debug/session/${sessionId}/call-stack`);
}

/**
 * 执行表达式
 */
export async function evaluateExpression(sessionId: string, expression: string) {
  return request<any>(`/api/script-debug/session/${sessionId}/evaluate`, {
    method: 'POST',
    data: {
      expression,
    },
  });
}
