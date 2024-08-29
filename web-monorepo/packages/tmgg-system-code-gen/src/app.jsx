// 调整umi 默认配置

import {patchClientRoutesRegistered} from "@tmgg/tmgg-base";
import  "@tmgg/tmgg-system/src/include_init";


export function patchClientRoutes({ routes }) {
    patchClientRoutesRegistered(routes)
}


