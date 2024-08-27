import {str} from "../str";

export  class PageTool {

    static currentLocationQuery(){
        let url = window.location.href


        let hasQuery = url.indexOf('?') > 0
        if(!hasQuery){
            return {}
        }

        const search = url.substring(url.indexOf('?') +1);
        const kvs = search.split('&')

        const rs = {}
        for (let kv of kvs) {
            const kvArr = kv.split('=')
            const k = kvArr[0];
            const v = kvArr[1];
            rs[k] = v;
        }

        return kvs
    }

    //"http://localhost:8000/#/login?id=1"
    static currentPathname(){
        let url = window.location.href;

        if(str.contains(url,'#')){
          url =  str.subAfter(url,'#')
        }
        if(str.contains(url, '?')){
            url = str.subBefore('?')
        }
        return url;
    }


}
