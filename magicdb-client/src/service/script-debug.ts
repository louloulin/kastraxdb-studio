import { request } from 'umi';

/**
 * 创建调试会话
 */
export async function createDebugSession(serviceId: string, parameters: Record<string, any>) {
  return request('/api/script-debug/session', {
    method: 'POST',
    data: { serviceId, parameters }
  });
}

/**
 * 获取调试会话
 */
export async function getDebugSession(sessionId: string) {
  return request(`/api/script-debug/session/${sessionId}`);
}

/**
 * 结束调试会话
 */
export async function terminateDebugSession(sessionId: string) {
  return request(`/api/script-debug/session/${sessionId}`, {
    method: 'DELETE'
  });
}

/**
 * 添加断点
 */
export async function addBreakpoint(sessionId: string, breakpoint: {
  id: string;
  line: number;
  column?: number;
  condition?: string;
  enabled?: boolean;
}) {
  return request(`/api/script-debug/session/${sessionId}/breakpoint`, {
    method: 'POST',
    data: breakpoint
  });
}

/**
 * 删除断点
 */
export async function removeBreakpoint(sessionId: string, breakpointId: string) {
  return request(`/api/script-debug/session/${sessionId}/breakpoint/${breakpointId}`, {
    method: 'DELETE'
  });
}

/**
 * 获取所有断点
 */
export async function getBreakpoints(sessionId: string) {
  return request(`/api/script-debug/session/${sessionId}/breakpoints`);
}

/**
 * 单步执行
 */
export async function stepOver(sessionId: string) {
  return request(`/api/script-debug/session/${sessionId}/step-over`, {
    method: 'POST'
  });
}

/**
 * 单步进入
 */
export async function stepInto(sessionId: string) {
  return request(`/api/script-debug/session/${sessionId}/step-into`, {
    method: 'POST'
  });
}

/**
 * 单步跳出
 */
export async function stepOut(sessionId: string) {
  return request(`/api/script-debug/session/${sessionId}/step-out`, {
    method: 'POST'
  });
}

/**
 * 继续执行
 */
export async function continueExecution(sessionId: string) {
  return request(`/api/script-debug/session/${sessionId}/continue`, {
    method: 'POST'
  });
}

/**
 * 获取变量
 */
export async function getVariables(sessionId: string) {
  return request(`/api/script-debug/session/${sessionId}/variables`);
}

/**
 * 获取调用栈
 */
export async function getCallStack(sessionId: string) {
  return request(`/api/script-debug/session/${sessionId}/call-stack`);
}

/**
 * 执行表达式
 */
export async function evaluateExpression(sessionId: string, expression: string) {
  return request(`/api/script-debug/session/${sessionId}/evaluate`, {
    method: 'POST',
    data: { expression }
  });
}
