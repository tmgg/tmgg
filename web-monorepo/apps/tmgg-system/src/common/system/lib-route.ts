import {StrTool} from "../utils";

let finalRoutes = [];

export function setFinalRoutes(arr) {
  finalRoutes = arr;
}

export function getRouteByPath(path) {
  // 兼容性处理， 移除path最后的 /
  if (path.length > 1 && path.endsWith('/')) {
    console.warn('传入path 以斜杠结尾, 开始兼容性处理', path);
    path = path.substring(0, path.length - 1);
    console.warn('处理结果', path);
  }

  for (let route of finalRoutes) {
    if (StrTool.equalsIgnoreCase(route.path, path)) {
      return route;
    }
  }
}
/**
 * 追加框架默认的路由
 * @param routes
 */
export function appendDefaultRoutes(routes, appends) {
  routes = getRoutes(routes);

  appends.forEach(r => {
    routes.push(r)
  })
}

export function cacheRoutes(routes) {
  let finalRoutes = [];
  routes = getRoutes(routes);
  routes.forEach(r => finalRoutes.push(r))

  setFinalRoutes(finalRoutes)
}
function getRoutes(routes) {
  if (routes.length > 0 && routes[0].routes) {
    routes = routes[0].routes
  }
  return routes;
}


export function patchRoutesDependency(routes, routesToAppend){
    appendDefaultRoutes(routes, routesToAppend)
    cacheRoutes(routes)
}
