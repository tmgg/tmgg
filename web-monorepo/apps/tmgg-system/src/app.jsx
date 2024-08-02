// 调整umi 默认配置




import {cacheRoutes} from "./common/system/lib-route";

export function patchRoutes({routes}) {
    cacheRoutes(routes)
}





