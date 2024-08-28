import { history } from 'umi';
import {PageTool} from "../system";

export default class RouterUtil {
  static getCurrenRoutePathFull() {
    const hash = window.location.hash; // #/stock/purchaseOrder/form?id=CG202209060002

    let url = hash.replace('#', '');

    return url;
  }

  static getCurrenRoutePath() {
    const hash = window.location.hash; // #/stock/purchaseOrder/form?id=CG202209060002

    let url = hash.replace('#', '');
    let idx = url.indexOf('?');
    if (idx > 0) {
      url = url.substring(0, idx);
    }
    return url;
  }

  static getSearch() {
    const hash = window.location.hash; // #/stock/purchaseOrder/form?id=CG202209060002

    let url = hash.replace('#', '');
    let idx = url.indexOf('?');
    if (idx > 0) {
      url = url.substring(idx + 1);
      return url; // id=CG202209060002
    }
  }

  // 返回键值对象
  static getQuery() {
    let search = this.getSearch();
    const us = new URLSearchParams(search);
    const rs = {};
    us.forEach((v, k) => {
      rs[k] = v;
    });
    return rs;
  }

  static push(url) {
    history.push(url);
  }

  static replace(url) {
    history.replace(url);
  }

  static goBack() {
    PageTool.close();
  }
}
