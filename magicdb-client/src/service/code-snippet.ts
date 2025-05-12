import { request } from 'umi';

/**
 * 获取所有代码片段
 */
export async function getAllSnippets(language?: string) {
  return request('/api/code-snippet', {
    params: language ? { language } : {}
  });
}

/**
 * 获取代码片段
 */
export async function getSnippet(id: string) {
  return request(`/api/code-snippet/${id}`);
}

/**
 * 创建代码片段
 */
export async function createSnippet(snippet: {
  id?: string;
  name: string;
  description?: string;
  content: string;
  language: string;
  tags?: string[];
}) {
  return request('/api/code-snippet', {
    method: 'POST',
    data: snippet
  });
}

/**
 * 更新代码片段
 */
export async function updateSnippet(id: string, snippet: {
  name: string;
  description?: string;
  content: string;
  language: string;
  tags?: string[];
}) {
  return request(`/api/code-snippet/${id}`, {
    method: 'PUT',
    data: snippet
  });
}

/**
 * 删除代码片段
 */
export async function deleteSnippet(id: string) {
  return request(`/api/code-snippet/${id}`, {
    method: 'DELETE'
  });
}

/**
 * 搜索代码片段
 */
export async function searchSnippets(keyword: string, language?: string) {
  return request('/api/code-snippet/search', {
    params: {
      keyword,
      ...(language ? { language } : {})
    }
  });
}
