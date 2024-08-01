import hutool from "@moon-cn/hutool";
import {message} from "antd";
import {appendDefaultRoutes, cacheRoutes} from "@tmgg/tmgg-system";

export function patchRoutes({ routes }) {
  appendDefaultRoutes(routes)
  cacheRoutes(routes)
}


function errorMessageHandler(msg) {
  message.error(msg)
}


hutool.http.init({
  errorMessageHandler: errorMessageHandler,
})


