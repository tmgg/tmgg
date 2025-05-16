import {useAppData} from "umi";
import React from "react";
import {Result} from "antd";
import {matchRoutes} from "umi";
import {UrlUtil} from "@tmgg/tmgg-commons-lang";

/**
 * 通过指定 pathname 渲染页面
 * @param props
 * 为了规范，接收参数何router保持一致，
 * pathname： 路径 如 /flowable/task/form
 * search：搜索参数 如 /?id=1
 * ma
 *
 * @returns {React.JSX.Element|*}
 * @constructor
 */
export default function PageRender(props) {
    let {pathname, search,params} = props
    const appData = useAppData()

    if(search && (params == null || Object.keys(params).length === 0)){
        params = UrlUtil.getParams(search)
    }

    if(pathname !== '/'){
        const map =appData.routeComponents
        const key =pathname.substring(1); // 移除第一个斜杠
        let cmp = map[key]
        if(!cmp){
            cmp = map[key +'/index']
        }
        if(cmp){
            return React.createElement(cmp, {pathname, search,params})
        }
    }




    const mathArr = matchRoutes(appData.clientRoutes, pathname)
    if (mathArr != null) {

        if (pathname === '/') {
            // 匹配结果为1，表示未定义index.jsx ，导致死循环
            if (mathArr.length === 1) { // 如果项目中没有定义index.jsx
                return <Result icon={null} title='未定义首页'></Result>
            }
        }


        // 取最匹配的那个
        const mathResult = mathArr[mathArr.length - 1].route
        let element = mathResult.element;

            return element;
    }

    // 如果实在找不到页面组件，则适用自带
    return <Result
        status={404}
        title='页面不存在！'
        subTitle={<div>路由地址：{pathname}</div>}
    />
}
