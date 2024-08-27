// 调整umi 默认配置
import {patchRoutesDependency} from "@tmgg/tmgg-system/src/common/system/lib-route";
import {getRoutes} from "@tmgg/tmgg-system";

export function patchRoutes({ routes }) {
  patchRoutesDependency(routes, getRoutes())
}



