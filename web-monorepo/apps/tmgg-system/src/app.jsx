// 调整umi 默认配置
import {cacheRoutes} from "./routes";


export function patchRoutes({routes}) {
  // 文档dumi模块也会调用这里。 使用 root.routes区分下
    cacheRoutes(routes)
}

/**
 * 在渲染前显示
 * https://v3.umijs.org/zh-CN/docs/runtime-config#renderoldrender-function
 * @param oldRender
 */
export function render(oldRender, b) {
  oldRender()
}




