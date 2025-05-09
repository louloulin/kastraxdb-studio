/**
 * 环境检测工具
 *
 * 用于检测当前应用运行的环境：Tauri 或 Web
 */

/**
 * 检测是否在 Tauri 环境中运行
 */
export const isTauri = (): boolean => {
  return typeof window !== 'undefined' && window.__TAURI__ !== undefined;
};

/**
 * 检测是否在 Web 环境中运行（非 Tauri）
 */
export const isWeb = (): boolean => {
  return !isTauri();
};

/**
 * 获取当前环境名称
 */
export const getEnvironment = (): 'tauri' | 'web' => {
  if (isTauri()) return 'tauri';
  return 'web';
};

/**
 * 获取平台信息
 */
export const getPlatform = (): { isLinux: boolean; isWin: boolean; isMac: boolean } => {
  // 如果在 Tauri 环境中，使用 Tauri API
  if (isTauri() && window.__TAURI__) {
    // 这里会在运行时通过 Tauri 命令获取平台信息
    // 暂时返回一个默认值，实际使用时应该通过 invoke 调用后端
    return {
      isLinux: false,
      isWin: false,
      isMac: true // 默认假设是 Mac
    };
  }

  // 否则，使用 userAgent 进行检测
  const userAgent = navigator.userAgent.toLowerCase();
  return {
    isLinux: userAgent.includes('linux'),
    isWin: userAgent.includes('win'),
    isMac: userAgent.includes('mac')
  };
};

/**
 * 检测是否在开发环境中运行
 */
export const isDevelopment = (): boolean => {
  return process.env.NODE_ENV === 'development';
};

/**
 * 检测是否在生产环境中运行
 */
export const isProduction = (): boolean => {
  return process.env.NODE_ENV === 'production';
};

export default {
  isTauri,
  isWeb,
  getEnvironment,
  getPlatform,
  isDevelopment,
  isProduction
};
