import { request } from 'umi';

/**
 * 获取代码补全项
 */
export async function getCompletionItems(language: string, script: string, position: number) {
  return request('/api/script-language/completion', {
    method: 'POST',
    params: { language },
    data: { script, position }
  });
}

/**
 * 获取诊断信息
 */
export async function getDiagnostics(language: string, script: string) {
  return request('/api/script-language/diagnostics', {
    method: 'POST',
    params: { language },
    data: { script }
  });
}

/**
 * 获取悬停信息
 */
export async function getHoverInfo(language: string, script: string, position: number) {
  return request('/api/script-language/hover', {
    method: 'POST',
    params: { language },
    data: { script, position }
  });
}

/**
 * 格式化代码
 */
export async function formatCode(language: string, script: string) {
  return request('/api/script-language/format', {
    method: 'POST',
    params: { language },
    data: { script }
  });
}

/**
 * 获取语言支持的功能
 */
export async function getLanguageCapabilities(language: string) {
  return request('/api/script-language/capabilities', {
    method: 'GET',
    params: { language }
  });
}
