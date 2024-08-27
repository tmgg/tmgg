// 调整umi 默认配置


import {http} from "@tmgg/tmgg-base";
import {sys} from "./common";

http.axiosInstance.defaults.baseURL = sys.getServerUrl()

export function patchRoutes({routes}) {
//  patchRoutesPlugin(routes,false)
}





