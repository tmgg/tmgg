// 调整umi 默认配置






import { patchRoutesPlugin} from "./lib-route";

export function patchRoutes({routes}) {
  patchRoutesPlugin(routes)
}





