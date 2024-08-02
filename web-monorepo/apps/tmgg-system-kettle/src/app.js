import hutool from "@moon-cn/hutool";
import {message} from "antd";

import {appendDefaultRoutes, cacheRoutes, getRoutes, patchRoutesDependency} from "@tmgg/tmgg-system";

import {getSystemJobRoutes} from "@tmgg/tmgg-system-job";



export function patchRoutes({ routes }) {
  patchRoutesDependency(routes, getRoutes())
  patchRoutesDependency(routes, getSystemJobRoutes)
}


function errorMessageHandler(msg) {
  message.error(msg)
}


hutool.http.init({
  errorMessageHandler: errorMessageHandler,
})


