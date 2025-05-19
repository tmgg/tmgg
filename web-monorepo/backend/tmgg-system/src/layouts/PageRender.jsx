import {matchRoutes, useAppData} from "umi";
import React from "react";
import {Result} from "antd";
import {UrlUtil} from "@tmgg/tmgg-commons-lang";

/**
 * 通过指定 pathname 渲染页面
 * @param props
 * 为了规范，接收参数何router保持一致，
 * pathname： 路径 如 /flowable/task/form
 * search：搜索参数 如 /?id=1
 *
 * passLocation: 是否把location信息透传到真正页面
 *
 * @returns {React.JSX.Element|*}
 * @constructor
 */




export default function PageRender(props) {
    let {pathname, search, passLocation} = props
    const appData = useAppData()
    const matchArr = matchRoutes(appData.clientRoutes, pathname)

    return <_PageRender appData={appData} matchArr={matchArr} pathname={pathname} search={search}
                        passLocation={passLocation}/>
}

class _PageRender extends React.Component {

    render() {
        if (this.props.passLocation) {
            return this.passLocationRender()
        }
        return this.defaultRender()
    }

    passLocationRender = () => {
        let {pathname, search, params, appData} = this.props
        if (search && (params == null || Object.keys(params).length === 0)) {
            params = UrlUtil.getParams(search)
        }
        const map = appData.routeComponents
        const key = pathname.substring(1); // 移除第一个斜杠
        let componentType = map[key] || map[key + '/index']
        if (componentType) {
            const location = {pathname, search, params}
            return React.createElement(componentType, {location});
        }
    };


    defaultRender = () => {
        let {pathname, matchArr} = this.props
        if (matchArr != null) {
            if (pathname === '/') {
                // 匹配结果为1，表示未定义index.jsx ，导致死循环
                if (matchArr.length === 1) { // 如果项目中没有定义index.jsx
                    return <Result icon={null} title='未定义首页'></Result>
                }
            }
            // 取最匹配的那个
            const mathResult = matchArr[matchArr.length - 1].route
            let element = mathResult.element;
            return element;
        }

        // 如果实在找不到页面组件，则适用自带
        return <Result
            status={404}
            title='页面不存在！'
            subTitle={<div>路由地址：{pathname}</div>}
        />
    };
}
