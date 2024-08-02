import hutool from "@moon-cn/hutool";
import {message} from "antd";

import {appendDefaultRoutes, cacheRoutes, getRoutes, patchRoutesDependency} from "@tmgg/tmgg-system";

export function patchRoutes({ routes }) {
  patchRoutesDependency(routes, getRoutes())
}


function errorMessageHandler(msg) {
  message.error(msg)
}


hutool.http.init({
  errorMessageHandler: errorMessageHandler,
})


