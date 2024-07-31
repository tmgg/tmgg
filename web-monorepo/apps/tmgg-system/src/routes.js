import React from 'react';
import {setFinalRoutes} from "./common";


/**
 * 参考 src/.umi/core.routes.ts 文件， 可直接拷贝部分
 *
 * 1. 去掉 plugin.applyPlugins 代码块
 * 2. 修改 @/pages 为 ./pages
 */
const baseRoutes = [
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
    "path": "/system/ssoLogin",
    "exact": true,
    "component": require('./pages/system/ssoLogin.jsx').default
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
    "path": "/system/user",
    "exact": true,
    "component": require('./pages/system/user/index.jsx').default
  },
  {
    "path": "/system/userCenter/changePassword",
    "exact": true,
    "component": require('./pages/system/userCenter/changePassword.jsx').default
  },
  {
    "path": "/system/file/main",
    "exact": true,
    "component": require('./pages/system/file/main.jsx').default
  }
  ,
  {
    "path": "/system/watermark",
    "exact": true,
    "component": require('./pages/system/watermark/index.jsx').default
  }
];


function getRoutes(routes) {
  if (routes.length > 0 && routes[0].routes) {
    routes = routes[0].routes
  }
  return routes;
}

/**
 * 追加框架默认的路由
 * @param routes
 */
export function appendDefaultRoutes(routes) {
  routes = getRoutes(routes);

  baseRoutes.forEach(r => {
    routes.push(r)
  })
}

export function cacheRoutes(routes) {
  let finalRoutes = [];
  // 缓存
  routes = getRoutes(routes);
  routes.forEach(r => finalRoutes.push(r))

  setFinalRoutes(finalRoutes)
}
