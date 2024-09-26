/**
 * umi 规定的入口
 */

import {initBase, patchClientRoutesRegistered} from "@tmgg/tmgg-base";
import {initSystem} from "@tmgg/tmgg-system";
import {initJob} from "@tmgg/tmgg-system-job";

initBase()
initSystem()
initJob()

export function patchClientRoutes({ routes }) {
    patchClientRoutesRegistered(routes)
}
