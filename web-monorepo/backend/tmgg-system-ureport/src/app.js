/**
 * umi 规定的入口
 */

import {initBase, patchClientRoutesRegistered} from "@tmgg/tmgg-base";
import {initSystem} from "@tmgg/tmgg-system";

initBase()
initSystem()

export function patchClientRoutes({ routes }) {
    patchClientRoutesRegistered(routes)
}