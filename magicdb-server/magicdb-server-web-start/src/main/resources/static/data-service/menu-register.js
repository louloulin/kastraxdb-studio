/**
 * 数据服务菜单注册脚本
 */
(function() {
  // 加载菜单配置
  fetch('/static/data-service/menu-config.json')
    .then(response => response.json())
    .then(menuConfig => {
      // 注册菜单
      if (window.registerMenu && typeof window.registerMenu === 'function') {
        window.registerMenu(menuConfig);
      } else {
        console.warn('Menu registration function not found');
      }
    })
    .catch(error => {
      console.error('Failed to load data service menu config:', error);
    });
})();
