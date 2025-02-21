/**
 * 导出组件
 */

import {registerRoutes} from "@tmgg/tmgg-base";


export * from "./layouts"


export function initSystem() {
    /**
     * 其实这里的路由不是框架用的，是项目调用的，
     *
     * 由于使用了umi，使用了基于文件的自动路由，实际项目集成是，需要手动调用一下
     */
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
        'system/user': require("./pages/system/user").default,
        'system/file/main': require('./pages/system/file/main').default,
        'system/msg': require('./pages/system/msg/index').default,
        'system/org': require('./pages/system/org/index').default,
        'settings': require('./pages/settings/index').default,
        'ssoLogin': require('./pages/ssoLogin').default,
        'login': require('./pages/login').default,
        'about': require('./pages/about').default,

        'code': require('./pages/code').default,
        'api/apiAccount': require('./pages/api/apiAccount').default,

        'system/mysqlStatus': require('./pages/system/mysqlStatus').default,

        'job': require('./pages/job').default,
        'job/logList': require('./pages/job/logList').default,
        'job/status': require('./pages/job/status').default,

        'report/chart': require('./pages/report/chart').default,
        'report/chart/design': require('./pages/report/chart/design').default,
        'report/chart/:code': require('./pages/report/chart/$code').default,

        'report/table': require('./pages/report/table').default,
        'report/table/design': require('./pages/report/table/design').default,
        'report/table/:code': require('./pages/report/table/$code').default,
    })
}
