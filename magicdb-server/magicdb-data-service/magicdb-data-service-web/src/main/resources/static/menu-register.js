// 数据服务菜单注册器
(function() {
  // 获取菜单配置
  fetch('/api/menu/data-service')
    .then(response => response.json())
    .then(menuConfig => {
      // 注册菜单
      if (window.registerMenu) {
        window.registerMenu(menuConfig);
      } else {
        // 如果菜单注册函数不存在，则将菜单配置保存到全局变量中
        window.pendingMenus = window.pendingMenus || [];
        window.pendingMenus.push(menuConfig);
      }
    })
    .catch(error => {
      console.error('Failed to register data service menu:', error);
    });
})();
