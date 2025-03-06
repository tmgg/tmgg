/**
 *
 */

import React from "react";
React.lazy(() => import('./pages/settings/ChangePassword'));
React.lazy(() => import('./pages/system/onlineUser/index'));
React.lazy(() => import('./pages/system/log/oplog/index'));
React.lazy(() => import('./pages/settings/MsgSubscribe'));
React.lazy(() => import('./pages/system/machine/index'));
React.lazy(() => import('./pages/system/datasource/index'));
React.lazy(() => import('./pages/system/config/index'));
React.lazy(() => import('./pages/system/dict/index'));
React.lazy(() => import('./pages/system/role/index'));
React.lazy(() => import('./pages/system/menu/index'));
React.lazy(() => import("./pages/system/user"));
React.lazy(() => import('./pages/system/file/main'));
React.lazy(() => import('./pages/system/msg/index'));
React.lazy(() => import('./pages/system/org/index'));
React.lazy(() => import('./pages/settings/index'));
React.lazy(() => import('./pages/ssoLogin'));
React.lazy(() => import('./pages/login'));
React.lazy(() => import('./pages/about'));
React.lazy(() => import('./pages/index.jsx'));
React.lazy(() => import('./pages/code'));
React.lazy(() => import('./pages/openApi'));
React.lazy(() => import('./pages/openApi/doc'));
React.lazy(() => import('./pages/system/mysqlStatus'));
React.lazy(() => import('./pages/job'));
React.lazy(() => import('./pages/job/logList'));
React.lazy(() => import('./pages/job/status'));
React.lazy(() => import('./pages/report/chart'));
React.lazy(() => import('./pages/report/chart/design'));
React.lazy(() => import('./pages/report/chart/$code'));
React.lazy(() => import('./pages/report/table'));
React.lazy(() => import('./pages/report/table/design'));
React.lazy(() => import('./pages/report/table/$code'));
export * from "./layouts"


