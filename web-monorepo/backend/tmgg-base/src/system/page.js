import {history} from "umi";
import {StrUtil} from "@tmgg/tmgg-commons-lang";

export class PageUtil {

    static currentLocationQuery() {
        let url = window.location.href


        let hasQuery = url.indexOf('?') > 0
        if (!hasQuery) {
            return {}
        }

        const search = url.substring(url.indexOf('?') + 1);
        const kvs = search.split('&')

        const rs = {}
        for (let kv of kvs) {
            const kvArr = kv.split('=')
            const k = kvArr[0];
            const v = kvArr[1];
            rs[k] = v;
        }

        return rs
    }

    //"http://localhost:8000/#/login?id=1"
    static currentPathname() {
        let path = window.location.hash.substring(1);
        return StrUtil.subBefore(path, '?')
    }

    /**
     *  当前路由的最后一个斜杠后面的单词， 通常用于基于路径的路由
     * @returns {string|null}
     */
    static currentPathnameLastPart() {
        const path = this.currentPathname()
        return StrUtil.subAfterLast(path, '/')
    }


    static open(path) {
        history.push(path)
    }

}
