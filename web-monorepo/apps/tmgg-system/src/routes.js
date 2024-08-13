import React from 'react';


/**
 * 参考 src/.umi/core.routes.ts 文件， 可直接拷贝部分
 *
 * 1. 去掉 plugin.applyPlugins 代码块
 * 2. 修改 @/pages 为 ./pages
 * 3. 去掉包装的布局父节点
 */

export function getRoutes() {
  const routes = [
    {
      "path": "/",
      "exact": true,
      "component": require('./pages/index.jsx').default
    },
    {
      "path": "/report",
      "exact": true,
      "component": require('./pages/report/index.jsx').default
    },
    {
      "path": "/system/config",
      "exact": true,
      "component": require('./pages/system/config/index.jsx').default
    },
    {
      "path": "/system/dict/Data",
      "exact": true,
      "component": require('./pages/system/dict/Data.jsx').default
    },
    {
      "path": "/system/dict",
      "exact": true,
      "component": require('./pages/system/dict/index.jsx').default
    },
    {
      "path": "/system/file/main",
      "exact": true,
      "component": require('./pages/system/file/main.jsx').default
    },
    {
      "path": "/system/log/oplog",
      "exact": true,
      "component": require('./pages/system/log/oplog/index.jsx').default
    },
    {
      "path": "/system/log/vislog",
      "exact": true,
      "component": require('./pages/system/log/vislog/index.jsx').default
    },
    {
      "path": "/system/login",
      "exact": true,
      "component": require('./pages/system/login.jsx').default
    },
    {
      "path": "/system/machine",
      "exact": true,
      "component": require('./pages/system/machine/index.jsx').default
    },
    {
      "path": "/system/msg",
      "exact": true,
      "component": require('./pages/system/msg/index.jsx').default
    },
    {
      "path": "/system/onlineUser",
      "exact": true,
      "component": require('./pages/system/onlineUser/index.jsx').default
    },
    {
      "path": "/system/org",
      "exact": true,
      "component": require('./pages/system/org/index.jsx').default
    },
    {
      "path": "/system/role",
      "exact": true,
      "component": require('./pages/system/role/index.jsx').default
    },
    {
      "path": "/system/role/RoleMenuTree",
      "exact": true,
      "component": require('./pages/system/role/RoleMenuTree.jsx').default
    },
    {
      "path": "/system/role/RoleUserTree",
      "exact": true,
      "component": require('./pages/system/role/RoleUserTree.jsx').default
    },
    {
      "path": "/system/ssoLogin",
      "exact": true,
      "component": require('./pages/system/ssoLogin.jsx').default
    },
    {
      "path": "/system/user",
      "exact": true,
      "component": require('./pages/system/user/index.jsx').default
    },
    {
      "path": "/system/user/UserOrgForm",
      "exact": true,
      "component": require('./pages/system/user/UserOrgForm.jsx').default
    },
    {
      "path": "/settings",
      "exact": true,
      "component": require('./pages/settings/index.jsx').default
    },
    {
      "path": "/system/watermark",
      "exact": true,
      "component": require('./pages/system/watermark/index.jsx').default
    }
  ]



  return routes;
}








