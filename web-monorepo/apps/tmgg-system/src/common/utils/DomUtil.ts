export class DomUtil {
  static offset(dom:any) {
    // 定义信号量
    var isIE8 = false;
    // 定义浏览器信息
    var str = window.navigator.userAgent;
    // 检测浏览器
    if (str.indexOf('MSIE 8.0') === -1) {
      isIE8 = false;
    } else {
      isIE8 = true;
    }

    // 定义对象
    var result = {
      left: dom.offsetLeft,
      top: dom.offsetTop,
    };
    // 使用while循环
    while (dom != document.body) {
      // 获取定位父元素
      dom = dom.offsetParent;
      // 判定
      if (isIE8) {
        // 说明是IE8 不需要额外加上 外边框
        result.left += dom.offsetLeft;
        result.top += dom.offsetTop;
      } else {
        // 不是IE8 所以要加上边框的值
        result.left += dom.offsetLeft + dom.clientLeft;
        result.top += dom.offsetTop + dom.clientTop;
      }
    }
    // 返回对象
    return result;
  }
}
