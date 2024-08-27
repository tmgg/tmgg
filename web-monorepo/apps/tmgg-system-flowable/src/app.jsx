// 调整umi 默认配置
import {patchRoutesPlugin} from "@tmgg/tmgg-system";

export function patchRoutes({ routes }) {
  patchRoutesPlugin(routes)
}



