import createRequest from './base';
const testService = createRequest<null, boolean>('/api/system', { errorLevel: false });
const systemStop = createRequest<void, void>('/api/system/stop', { errorLevel: false, method: 'post' });
const testApiSmooth = createRequest<void, void>('/api/system/get-version-a', { errorLevel: false, method: 'get' });

export default {
  testService,
  systemStop,
  testApiSmooth,
};

// 检查API服务状态
export async function checkApiStatus() {
  try {
    const response = await fetch('/api/health', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (response.ok) {
      return { success: true, data: await response.json() };
    } else {
      return { success: false, message: 'API服务不可用' };
    }
  } catch (error) {
    console.error('API服务检查失败:', error);
    return { success: false, message: '无法连接到API服务' };
  }
}
