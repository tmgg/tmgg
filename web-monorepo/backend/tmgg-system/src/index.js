/**
 *
 */

import React from "react";

export const routeComponentsSystem = {
    'settings/ChangePassword': React.lazy(() => import('./pages/settings/ChangePassword')),
    'system/onlineUser': React.lazy(() => import('./pages/system/onlineUser/index')),
    'system/log/oplog': React.lazy(() => import('./pages/system/log/oplog/index')),
    'settings/MsgSubscribe': React.lazy(() => import('./pages/settings/MsgSubscribe')),
    'system/machine': React.lazy(() => import('./pages/system/machine/index')),
    'system/datasource': React.lazy(() => import('./pages/system/datasource/index')),
    'system/config': React.lazy(() => import('./pages/system/config/index')),
    'system/dict': React.lazy(() => import('./pages/system/dict/index')),
    'system/role': React.lazy(() => import('./pages/system/role/index')),
    'system/menu': React.lazy(() => import('./pages/system/menu/index')),
    'system/user': React.lazy(() => import("./pages/system/user")),
    'system/file/main': React.lazy(() => import('./pages/system/file/main')),
    'system/msg': React.lazy(() => import('./pages/system/msg/index')),
    'system/org': React.lazy(() => import('./pages/system/org/index')),
    'settings': React.lazy(() => import('./pages/settings/index')),
    'ssoLogin': React.lazy(() => import('./pages/ssoLogin')),
    'login': React.lazy(() => import('./pages/login')),
    'about': React.lazy(() => import('./pages/about')),
    'index': React.lazy(() => import('./pages/index.jsx')),

    'code': React.lazy(() => import('./pages/code')),
    'openApi': React.lazy(() => import('./pages/openApi')),
    'openApi/doc': React.lazy(() => import('./pages/openApi/doc')),

    'system/mysqlStatus': React.lazy(() => import('./pages/system/mysqlStatus')),

    'job': React.lazy(() => import('./pages/job')),
    'job/logList': React.lazy(() => import('./pages/job/logList')),
    'job/status': React.lazy(() => import('./pages/job/status')),

    'report/chart': React.lazy(() => import('./pages/report/chart')),
    'report/chart/design': React.lazy(() => import('./pages/report/chart/design')),
    'report/chart/:code': React.lazy(() => import('./pages/report/chart/$code')),

    'report/table': React.lazy(() => import('./pages/report/table')),
    'report/table/design': React.lazy(() => import('./pages/report/table/design')),
    'report/table/:code': React.lazy(() => import('./pages/report/table/$code')),
};

export * from "./layouts"


