import {str} from "@tmgg/tmgg-base"
import {getRoutes} from "./routes";

let FINAL_ROUTES = [];

function setFinalRoutes(arr) {
  FINAL_ROUTES = arr;
}

export function getRouteByPath(path) {
  // 兼容性处理， 移除path最后的 /
  if (path.length > 1 && path.endsWith('/')) {
    console.warn('传入path 以斜杠结尾, 开始兼容性处理', path);
    path = path.substring(0, path.length - 1);
    console.warn('处理结果', path);
  }


  for (let route of FINAL_ROUTES) {
    if (str.equalsIgnoreCase(route.path, path)) {
      return route;
    }
  }
}


function cacheRoutes(routes) {
  let finalRoutes = [];
  routes = getRootRoutes(routes);
  routes.forEach(r => finalRoutes.push(r))

  setFinalRoutes(finalRoutes)
}

function getRootRoutes(routes) {
  if (routes.length > 0 && routes[0].routes) {
    routes = routes[0].routes
  }
  return routes;
}


export function patchRoutesPlugin(routes, appendSystem=true) {
  if(appendSystem){
    const appends = getRoutes();
    routes = getRootRoutes(routes);
    appends.forEach(r => {
      routes.push(r)
    })
  }

  cacheRoutes(routes)
}
