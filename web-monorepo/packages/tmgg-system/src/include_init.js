import {registerRoutes} from "@tmgg/tmgg-base";

registerRoutes({
    'settings/ChangePassword': require('./pages/settings/ChangePassword').default,
    'system/log/vislog': require('./pages/system/log/vislog/index').default,
    'system/onlineUser': require('./pages/system/onlineUser/index').default,
    'system/user/UserOrgForm': require('./pages/system/user/UserOrgForm').default,
    'system/log/oplog': require('./pages/system/log/oplog/index').default,
    'system/watermark': require('./pages/system/watermark/index').default,
    'settings/MsgSubscribe': require('./pages/settings/MsgSubscribe').default,
    'system/machine': require('./pages/system/machine/index').default,
    'system/config': require('./pages/system/config/index').default,
    'system/dict': require('./pages/system/dict/index').default,
    'system/role': require('./pages/system/role/index').default,
    'system/user': require( "./pages/system/user").default,
    'system/dict/Data': require('./pages/system/dict/Data').default,
    'system/file/main': require('./pages/system/file/main').default,
    'system/msg': require('./pages/system/msg/index').default,
    'system/org': require('./pages/system/org/index').default,
    'settings': require('./pages/settings/index').default,
    'report': require('./pages/report/index').default,
    'ssoLogin': require('./pages/ssoLogin').default,
    'login': require('./pages/login').default,
})



