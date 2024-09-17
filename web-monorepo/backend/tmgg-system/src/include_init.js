import {registerRoutes} from "@tmgg/tmgg-base";

export function initSystem(){
    registerRoutes({
        'settings/ChangePassword': require('./pages/settings/ChangePassword').default,
        'system/log/vislog': require('./pages/system/log/vislog/index').default,
        'system/onlineUser': require('./pages/system/onlineUser/index').default,
        'system/log/oplog': require('./pages/system/log/oplog/index').default,
        'settings/MsgSubscribe': require('./pages/settings/MsgSubscribe').default,
        'system/machine': require('./pages/system/machine/index').default,
        'system/config': require('./pages/system/config/index').default,
        'system/dict': require('./pages/system/dict/index').default,
        'system/role': require('./pages/system/role/index').default,
        'system/user': require( "./pages/system/user").default,
        'system/file/main': require('./pages/system/file/main').default,
        'system/msg': require('./pages/system/msg/index').default,
        'system/org': require('./pages/system/org/index').default,
        'settings': require('./pages/settings/index').default,
        'report': require('./pages/report/index').default,
        'ssoLogin': require('./pages/ssoLogin').default,
        'login': require('./pages/login').default,
        'about': require('./pages/about').default,
    })
}




