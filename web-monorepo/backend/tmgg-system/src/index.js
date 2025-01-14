/**
 * 导出组件
 */

import {registerRoutes} from "@tmgg/tmgg-base";


export * from "./layouts"


export function initSystem(){
    registerRoutes({
        'settings/ChangePassword': require('./pages/settings/ChangePassword').default,
        'system/onlineUser': require('./pages/system/onlineUser/index').default,
        'system/log/oplog': require('./pages/system/log/oplog/index').default,
        'settings/MsgSubscribe': require('./pages/settings/MsgSubscribe').default,
        'system/machine': require('./pages/system/machine/index').default,
        'system/datasource': require('./pages/system/datasource/index').default,
        'system/config': require('./pages/system/config/index').default,
        'system/dict': require('./pages/system/dict/index').default,
        'system/role': require('./pages/system/role/index').default,
        'system/menu': require('./pages/system/menu/index').default,
        'system/user': require( "./pages/system/user").default,
        'system/file/main': require('./pages/system/file/main').default,
        'system/msg': require('./pages/system/msg/index').default,
        'system/org': require('./pages/system/org/index').default,
        'settings': require('./pages/settings/index').default,
        'ssoLogin': require('./pages/ssoLogin').default,
        'login': require('./pages/login').default,
        'about': require('./pages/about').default,

        'code': require('./pages/code').default,

        'mysqlStatus': require('./pages/system/mysqlStatus').default,

        'job': require('./pages/job').default,
        'job/logList': require('./pages/job/logList').default,


        'chart/sysChart': require('./pages/chart/sysChart').default,
    })
}
