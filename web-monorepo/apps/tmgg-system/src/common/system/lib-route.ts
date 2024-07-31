import {StrTool} from "../utils";

let finalRoutes: any = [];

export function setFinalRoutes(arr: any) {
  finalRoutes = arr;
}

export function getRouteByPath(path: string) {
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
